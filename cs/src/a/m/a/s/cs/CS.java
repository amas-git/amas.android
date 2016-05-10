package a.m.a.s.cs;

import java.util.Random;

public class CS {
	
	public static void randomSleep(int min, int max) {
		Random random = new Random(System.currentTimeMillis());
		int next = random.nextInt(max - min);
		try {
			Thread.sleep(next);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void safeSleep(long msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
