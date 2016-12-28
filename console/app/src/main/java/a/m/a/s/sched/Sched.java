package a.m.a.s.sched;

import android.text.TextUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 *  周 年  月 日 时 分
 *  * 2010 10 1 18 0
 *
 *  == 周
 *  0: 周日
 *  1: 周一
 *  2: 周二
 *  3: 周三
 *  4: 周四
 *  5: 周五
 *  6: 周六
 */
public class Sched implements Serializable {
	public static final SimpleDateFormat DATEFORMATER   = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 E ");
	public static final SimpleDateFormat DATEFORMATER_2 = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
	public static final SimpleDateFormat DATEFORMATER_TIME = new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat DATEFORMATER_DATE = new SimpleDateFormat("MM-dd");

    public static Map<String, Sched> shches = new HashMap<>();

    /**
     * 每日
     * @return
     */
    public static Sched SCHED_EVERY_DAY() {
        return obtainSched("* * * * 0 0");
    }

    /**
     * 每周一
     * @return
     */
    public static Sched SCHED_EVERY_WEEK() {
        return obtainSched("1 * * * 0 0");
    }


    public static Sched obtainSched(String schedExpr) {
        if(TextUtils.isEmpty(schedExpr)) {
            return null;
        }

        Sched sched  = shches.get(schedExpr);
        if(sched == null) {
            sched = SchedParser.parse(schedExpr);
            shches.put(schedExpr, sched);
        }
        return sched;
    }


	
	public static final int    CRON_ITEM_NUMS           = 6;
	
	public static final int    CRON_ITEM_DAY_OF_WEEK    = 0;
	public static final int    CRON_ITEM_YEAR           = 1;
	public static final int    CRON_ITEM_MONTH          = 2;
	public static final int    CRON_ITEM_DAY_OF_MONTH   = 3;
	public static final int    CRON_ITEM_HOUR_OF_DAY    = 4;
	public static final int    CRON_ITEM_MINUTE_OF_HOUR = 5;
	
	public static final int    EXPIRED_PERM             =-1; // 永久失效
	
	ArrayList<SchedItem> mCronItems = new ArrayList<SchedItem>(CRON_ITEM_NUMS);
    private String expr = "";
	
	//BaseCronItem[] items = new BaseCronItem[CRON_ITEM_NUMS]; 
	
	private static int[] sMapToCalendar = { 
	    Calendar.DAY_OF_WEEK,
        Calendar.YEAR,
        Calendar.MONTH,
        Calendar.DAY_OF_MONTH,
        Calendar.HOUR_OF_DAY,
        Calendar.MINUTE};
	
	String mAction = ""; // XXX(amas): reverse
	
	public boolean isYearlyRepeat() {
	    return mCronItems.get(CRON_ITEM_YEAR).isWhenerver();
	}
	
	public boolean isDaliyRepeat() {
	    return mCronItems.get(CRON_ITEM_DAY_OF_MONTH).isWhenerver();
	}
	
	public boolean isMonthlyRepeat() {
	    return mCronItems.get(CRON_ITEM_MONTH).isWhenerver();
	}
	
	public Sched() {
		
	}

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public String expr() {
        return this.expr;
    }
	
	public void addItem(SchedItem item) {
		mCronItems.add(item);
	}
	
	
	private SchedItem getItem(int i) {
		return mCronItems.get(i);
	}

    public static long distance(Sched milestone_sched_c) {
        long now = TimeUtils.normalizeNow();
        Calendar c0 = Calendar.getInstance();
        c0.setTime(new Date(now+TimeUtils.MINUTE));
        //android.util.Log.i("htask", " c0: " + TimeUtils.yyyy_MM_dd_HH_mm_ss(c0.getTimeInMillis()));
        Calendar c1 = milestone_sched_c.evalLatestTriggerTime(c0);
        //android.util.Log.i("htask", " c1: " + TimeUtils.yyyy_MM_dd_HH_mm_ss(c1.getTimeInMillis()));
        if(c1 == null) {
            return -1L;
        }
        return c1.getTimeInMillis() - now ;
    }

    class CompareStatus {
		public SchedItem cronItem = null;
		public int          value    = -1;
		public CompareStatus(SchedItem i, int v) {
			cronItem = i;
			value    = v;
		}
	}
	
	
	public Calendar evalLatestTriggerTime(Calendar anchor) {
		Stack<CompareStatus> stack = new Stack<CompareStatus>();
		Calendar latest = (Calendar) anchor.clone();
		latest.set(Calendar.SECOND, 0);// 我们不支持秒

		SchedItem item = null;
		int current = -1;
		int nearest = 0;

		boolean expire = false;
		int last = 0;

		for (int i = CRON_ITEM_YEAR; i <= CRON_ITEM_MINUTE_OF_HOUR; ++i) {
			item = mCronItems.get(i);
			current = getCalendarField(anchor, i);
			nearest = item.nearest(latest, current);

			if (nearest < current) {
					expire = true;
					// 回溯
					while (!stack.isEmpty()) {
						CompareStatus top = stack.pop();
						nearest = top.cronItem.nearest(latest, top.value + 1);
						if (nearest > top.value) {
							// 调整
							rollCalendarField(latest,top.cronItem.cronFiledIndex, nearest-top.value);
							expire = false;
							last = top.cronItem.cronFiledIndex;
							break;
						}
					}
					break; // 结束调整
			} else if (nearest == current) {
				stack.push(new CompareStatus(item, nearest)); // TODO(amas):
			} else {
				expire = false;
				last = item.cronFiledIndex;
				setCalendarField(latest, last, nearest);
				break;
			}
		}
		
		// 按星期调整时间
		SchedItemDayOfWeek weekItem = (SchedItemDayOfWeek)mCronItems.get(CRON_ITEM_DAY_OF_WEEK);
		
		if (!expire) {

			if(last > CRON_ITEM_DAY_OF_WEEK) {
				for (int i = last+1; i < CRON_ITEM_NUMS; ++i) {
					item = mCronItems.get(i);
					setCalendarField(latest, i, item.getMin());
				}
			}
			
			if(!weekItem.isWhenerver()) {
				int currentDayOfweek = CRON_WDAY(latest.get(Calendar.DAY_OF_WEEK));
				int day = latest.get(Calendar.DAY_OF_MONTH);
				while(!weekItem.contains(currentDayOfweek)) {
					latest.set(Calendar.DAY_OF_MONTH, ++day);
					currentDayOfweek = CRON_WDAY(latest.get(Calendar.DAY_OF_WEEK));
				}
			}
			
//			System.out.println(String.format("['%20s']", toCron()));
//			System.out.println("\t当前时间 = " + __(anchor));
//			System.out.println("\t触发时间 = " + __(latest));

			return latest;
		} else {
			return null;
		}
	}

	/**
	 * 我们用1,2,3,4,5,6表示周一至周六, 0表示周日, 这里用来与Java {@link Calendar}中的定义做映射
	 * @return
	 */
	public static int CRON_WDAY(int x) {
		return x - 1;
	}
	
	public static boolean isLeapYear(int year) {
		return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
	}
	
	/**
	 * 计算距离给定时间最近的一次执行时间
	 * @param currentTime
	 * a@return
	 */
	public Calendar evalLatestTriggerTime() {
		return evalLatestTriggerTime(Calendar.getInstance());
	}
	
	
	/**
	 * 计算距离给定时间最近的一次执行时间
	 * @param currentTime
	 * @return
	 */
	public long evalLatestTriggerTimeAtTimeMillis(){
		Calendar c = evalLatestTriggerTime();
		return c == null ? -1 : c.getTimeInMillis();
	}

	/**
	 * 计算下一个触发时间, offset用于忽略当前时间节点
	 * @param offset
	 * @return
     */
	public long evalLatestTriggerTimeAtTimeMillis(int offset) {
		Calendar anchor = Calendar.getInstance();
		anchor.setTimeInMillis(System.currentTimeMillis() + offset);

		Calendar c = evalLatestTriggerTime(anchor);
		return c == null ? -1 : c.getTimeInMillis();
	}

	public static void setCalendarField(Calendar c, int cronIndex, int v) {
		if(cronIndex == CRON_ITEM_MONTH) {
			if(v > 0) {
				v -= 1;
			}
		}
		c.set(sMapToCalendar[cronIndex], v);
	}
	
	public static void rollCalendarField(Calendar c, int cronIndex, int delta) {
		if(cronIndex == CRON_ITEM_MONTH) {
			if(delta > 1) {
				delta -= 1;
			}
		}
		c.roll(sMapToCalendar[cronIndex], delta);
	}
	
	public static int getCalendarField(Calendar c, int cronIndex) {
		int v = c.get(sMapToCalendar[cronIndex]);
		if(cronIndex == CRON_ITEM_MONTH) {
			++v;
		}
		return v;
	}
	
	
	public static String __(Calendar c) {
		return DATEFORMATER.format(new Date(c.getTimeInMillis()));
	}
	
	public static String ___(Calendar c) {
        return DATEFORMATER_2.format(new Date(c.getTimeInMillis()));
    }
	
	public static String __TIME(Calendar c) {
        return DATEFORMATER_TIME.format(new Date(c.getTimeInMillis()));
    }
	
	public static String __DATE(Calendar c) {
        return DATEFORMATER_DATE.format(new Date(c.getTimeInMillis()));
    }
	
	public static void print(Calendar c) {
		System.out.println(__(c));
	}
	
	private String toCron() {
		StringBuilder cron   = new StringBuilder();
		for(int i=0; i<CRON_ITEM_NUMS; ++i) {
			cron.append(getItem(i).text).append(SchedParser.REGEX_SEP);
		}
		return cron.toString();
	}
	
	private String toLine() {
		StringBuilder line   = new StringBuilder();
		for(int i=0; i<CRON_ITEM_NUMS; ++i) {
			line.append(getItem(i));
		}
		return line.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder cron = new StringBuilder();
		cron.append("============================================================\n");
		cron.append(String.format("'%s'\n", toCron()));
		cron.append("------------------------------------------------------------\n");
		cron.append(toLine());
		cron.append("------------------------------------------------------------\n\n");
		return cron.toString();
	}

    public boolean isExpired() {
        return evalLatestTriggerTime() == null;
    }
}
