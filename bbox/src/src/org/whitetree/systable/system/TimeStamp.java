package org.whitetree.systable.system;

/**
 * For tracking task life 
 * @author amas
 */
public class TimeStamp {
	public enum Tag {
		START_TIME,
		END_TIME
	}
	private long mStartTime = 0;
	private long mEndTime   = 0;	
	
	public TimeStamp() {}
	
	public void touch(Tag tag) {
		switch (tag) {
		case START_TIME:
			mStartTime = System.currentTimeMillis();
			break;
		case END_TIME:
			mEndTime   = System.currentTimeMillis();
			break;
		default:
			/* NOP */
		}
	}
	
	public void reset() {
		touch(Tag.START_TIME);
	}

	public long getLifeTime() {
		touch(Tag.END_TIME);
		return mEndTime - mStartTime;
	}
	
	public float getLifeTimeSec() {
		touch(Tag.END_TIME);
		return (mEndTime - mStartTime) / 1000.0f;
	}
	
	public long getStartTimestamp() {
		return mStartTime;
	}
	
	public long getEndTimestamp() {
		return mEndTime;
	}
}
