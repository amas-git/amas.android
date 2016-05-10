package a.m.a.s.coco.testrunner;

import a.m.a.s.coco.old.EventDispatcher;

public class PEventDispatcherTest {
	public void runTest() {
		EventDispatcher ed = EventDispatcher.FIFO();
		
		int en = 10000;
		
		for(int i=0; i< en; ++i) {
			ed.push(new CountEvent(i, en));
		}
		
		ed.start();
		
	}
}
