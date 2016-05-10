package a.m.a.s.coco.demo;

import a.m.a.s.coco.Coco;
import a.m.a.s.coco.CocoTask;
import a.m.a.s.coco.testrunner.CountTask;

public class HelloCoco extends CocoTask {
	static PrintListener l = new PrintListener();
	
	public class Task2 extends CocoTask {
		
	}
	
	public static void runTest(int max) {
		//Coco.getInstance().removeEventListener(0, this);
		Coco.getInstance().addEventListener(Coco.DID_MAIN, l);
		for(int i=0; i<max; ++i) {
			Coco.getInstance().exec(new CountTask(i, max).did(0));
		}
	}
}
