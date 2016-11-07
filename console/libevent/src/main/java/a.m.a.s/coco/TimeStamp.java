package a.m.a.s.coco;

import java.util.ArrayList;
import java.util.Locale;

/**
 * For tracking task life
 * 
 * @author amas
 */
public class TimeStamp {
	public static boolean ENABLED = true;

	public enum Tag {
		START_TIME, END_TIME
	}

	private long mStartTime = 0;
	private long mEndTime = 0;
	private String mId = "TRACER";
	ArrayList<Item> timeline = new ArrayList<Item>();
	boolean enabled = true;

	public class Item {
		public Item(String id) {
			this.id = id;
			time = System.currentTimeMillis();
		}

		public String id = "";
		public long time = 0;

		public String duration(long anchor) {
			return String.format(Locale.US, "[ %5d ] : %-10s", (time - anchor),
					id);
		}

		public long getTime() {
			return time;
		}
	}

	public TimeStamp put(String what) {
		if (ENABLED) {
			timeline.add(new Item(what));
		}
		return this;
	}

	public TimeStamp(String id) {
		this();
		this.mId = id;
	}

	public TimeStamp() {
		reset();
		put(".");
	}

	public void touch(Tag tag) {
		switch (tag) {
		case START_TIME:
			mStartTime = System.currentTimeMillis();
			break;
		case END_TIME:
			mEndTime = System.currentTimeMillis();
			break;
		default:
			/* NOP */
		}
	}

	public long getLifeTime() {
		touch(Tag.END_TIME);
		return mEndTime - mStartTime;
	}

	public double getLifeTimeSec() {
		touch(Tag.END_TIME);
		return (mEndTime - mStartTime) / 1000.0;
	}

	public int getLifeTimeSecInt() {
		touch(Tag.END_TIME);
		return (int) (mEndTime - mStartTime);
	}

	/*
	 * public long getStartTimestamp() { return mStartTime; }
	 */

	/*
	 * public long getEndTimestamp() { return mEndTime; }
	 */

	public TimeStamp reset() {
		timeline = new ArrayList<Item>();
		touch(Tag.START_TIME);
		return this;
	}

	public String getDumpString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ " + mId + " ] : " + getLifeTimeSecInt() + "(ms)").append(
				"\n");

		long last = mStartTime;
		for (int i = 0; i < timeline.size(); ++i) {
			Item ii = timeline.get(i);
			sb.append("  + " + ii.duration(last)).append("\n");
			last = ii.getTime();
		}
		return sb.toString();
	}

	public void dump() {
		if (ENABLED) {
			System.out.println(getDumpString());
		}
	}
}
