package a.m.a.s.sched;

public class SchedItemMonth extends SchedItem {
	public SchedItemMonth(){
		super(1, 12,Sched.CRON_ITEM_MONTH);
		mDesc = "月份";
	}
}
