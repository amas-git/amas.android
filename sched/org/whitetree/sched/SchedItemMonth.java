package org.whitetree.sched;

public class SchedItemMonth extends SchedItem {
	public SchedItemMonth(){
		super(1, 12,Sched.CRON_ITEM_MONTH);
		mDesc = "月份";
	}
}
