package org.whitetree.sched;

/**
 * 0: sunday
 *  ..
 * 6: saturday
 * @author amas
 *
 */
public class SchedItemDayOfWeek extends SchedItem {
	public SchedItemDayOfWeek() {
		super(0, 6, Sched.CRON_ITEM_DAY_OF_WEEK);
		mDesc = "星期";
	}
}
