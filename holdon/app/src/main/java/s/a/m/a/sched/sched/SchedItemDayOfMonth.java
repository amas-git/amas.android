package s.a.m.a.sched.sched;

import java.util.Calendar;

public class SchedItemDayOfMonth extends SchedItem {
	public SchedItemDayOfMonth() {
		super(1, 31, Sched.CRON_ITEM_DAY_OF_MONTH);
		mDesc = "日期";
	}
	
	/**
	 * TODO(amas): 需要优化/使用二分法
	 * 
	 * @param element
	 * @return
	 */
	public int nearest(Calendar c, int element) {
		int m = Sched.getCalendarField(c, Sched.CRON_ITEM_MONTH);
		
		if(m == 2) {
			int y = Sched.getCalendarField(c, Sched.CRON_ITEM_YEAR);
			mRangeMax = Sched.isLeapYear(y) ? 29 : 28;
		} else if(m==1 || m==3 || m==5 || m==7 || m==8 || m==10 || m==12) {
			mRangeMax = 31;
		} else {
			mRangeMax = 30;
		}
		
		return super.nearest(null, element);
	}
}
