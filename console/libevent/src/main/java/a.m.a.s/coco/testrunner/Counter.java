package a.m.a.s.coco.testrunner;

public class Counter implements Runnable {
	int mN = 0;
	int mMax = 0;
	
	public Counter(int i, int max) {
		mN = i;
		mMax = max;
	}
	
	@Override
	public void run() {
		
	}
	
	boolean isAllDone() {
		return mN == mMax;
	}
	
	boolean isFirst() {
		return mN == 0;
	}
}