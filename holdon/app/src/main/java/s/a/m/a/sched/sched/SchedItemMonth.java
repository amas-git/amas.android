package s.a.m.a.sched.sched;

public class SchedItemMonth extends SchedItem {
	public SchedItemMonth(){
		super(1, 12,Sched.CRON_ITEM_MONTH);
		mDesc = "月份";
	}
}
