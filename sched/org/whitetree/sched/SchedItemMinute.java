package org.whitetree.sched;

public class SchedItemMinute extends SchedItem {

	public SchedItemMinute() {
		super(0, 59, Sched.CRON_ITEM_MINUTE_OF_HOUR);
		mDesc = "分钟";
	}

}
