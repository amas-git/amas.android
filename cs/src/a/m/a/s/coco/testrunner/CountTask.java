package a.m.a.s.coco.testrunner;

import a.m.a.s.coco.CocoEvent;
import a.m.a.s.coco.CocoTask;
import a.m.a.s.cs.CS;

public class CountTask extends CocoTask {
	int i = 0;
	int max = 0;

	public CountTask(int i, int max) {
		this.i = i;
		this.max = max;
	}
	
	public CocoEvent call() {
		CS.safeSleep(100);
		//System.out.println(" *** CountTask : " + i + " did="+ did);
		return new CountEvent(i, max);
	}

	public CocoTask did(int j) {
		did = j;
		return this;
	}
}
