package a.m.a.s.os.linux;

import java.io.File;

public class task {
	public static task create(String tid) {
		task t = new task(tid);
		return t;
	}
	
	
	String tid  = "self";      // Thread Id
	String comm = ""; // THread Name
	
	public task(String tid) {
		this.tid = tid;
		stat.createFromFile(new File("/proc/" + tid + "/stat"));
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
