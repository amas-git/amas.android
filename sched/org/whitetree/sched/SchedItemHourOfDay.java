package org.whitetree.sched;

public class SchedItemHourOfDay extends SchedItem {

	public SchedItemHourOfDay() {
		super(0, 23, Sched.CRON_ITEM_HOUR_OF_DAY);
		mDesc = "小时";
	}

}
