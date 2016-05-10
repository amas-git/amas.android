package a.m.a.s.cs;

import java.io.File;

import a.m.a.s.os.linux.proc;
import a.m.a.s.os.linux.proc_pid_stat;
import a.m.a.s.os.linux.vnode;
import a.m.a.s.os.linux.vnode.rules;


class Job implements Runnable {
	int i = 0;

	public Job(int i) {
		this.i = i;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("JOB -> " + i + " @ " + Thread.currentThread());
	}
}



public class Main {
	public static void main(String[] args) {
		//Disspatcher.runTest();
		//CountTask.runTest();
		//EventDispatcher.runTest();
		//LookupTest.runTest();
		//HelloCoco.runTest(100);
		//TaskXs.runTest(1000, 1);
		long tid = Thread.currentThread().getId();
		for(int i=0; i<100010; ++i) {
			//int x = i*i;
			//MurmurHash.hash64("i"+i);
			//System.out.println("PID="+proc.getPid());
		}
		//System.out.println("TID="+tid);
		
		//SmartEventDispatcher.runTest(1, 100);
		proc_pid_stat ps = new proc_pid_stat();
		
		System.out.println(ps.update());
//		SimpleDateFormat time_ctrl = new SimpleDateFormat("yyyyMMdd:HH");
//		
//		long now = System.currentTimeMillis();
//		
//		
//		Date then;
//		try {
//			then = time_ctrl.parse("20141216:21");
//			System.out.println("" +(now > then.getTime()));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		CocoShell shell = new CocoShell();
//		
//		System.out.println("cmdline="+proc_pid_stat.cmdline());
//		String expr = "key > 1";
//		Conditions.parse(expr);
////		SmartEventDispatcher.runTest(0,1000);
//		TaskXs.runTest(100, 4);
//		TaskXs.runTest(100, 4);
//		CS.safeSleep(2000);
//		TaskXs.runTest(0, 2);
//		CS.safeSleep(2000);
//		TaskXs.runTest(0, 4);
//		CS.safeSleep(2000);
//		TaskXs.runTest(0, 1);
//		CS.safeSleep(2000);
//		TaskXs.runTest(0, 2);
//		CS.safeSleep(2000);
//		TaskXs.runTest(0, 1);
		//TaskXs.runTest(1000, 2);
//		TaskXs.runTest(1000, 1);
//		TaskXs.runTest(1000, 2);
		
		//ASM.runTest();
		//System.out.println(TimeRange.timeIn("20101014"));
		//System.out.println(TimeRange.timeIn("20150909,20101014"));
	}

	public static long ONE_DAY = 24 * 60 * 60 * 1000L;

	/**
	 * @param start
	 * @param end
	 * @return
	 */
	public static long[] range_day(long start, long end) {
		int duration = (int) ((end - start) / ONE_DAY);
		long[] range = new long[duration];
		if (duration == 0) {
			return range;
		}

		for (int i = 0; i < duration; ++i) {
			range[i] = start + i * ONE_DAY;
		}
		return range;
	}
    
	static void locktest() {
		final LockTest l = new LockTest();
		new Thread() {
			public void run() {
				l.forever();
			};
		}.start();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		l.stuck();
	}

	public static void test() {
		TP tp = new TP(4);

		tp.start();

		for (int i = 0; i < 100; ++i) {
			tp.execute(new Job(i));
		}

		try {
			Thread.sleep(1000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("2");
	}
}
