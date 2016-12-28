package a.m.a.s.sched;

public class SchedItemYear extends SchedItem {
	public SchedItemYear(){
		super(1970, 2100,Sched.CRON_ITEM_YEAR);
		mDesc = "年份";
	}
}
