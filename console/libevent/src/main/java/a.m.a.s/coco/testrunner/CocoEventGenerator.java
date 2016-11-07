package a.m.a.s.coco.testrunner;

import a.m.a.s.coco.BaseDispatcher;
import a.m.a.s.coco.CocoEvent;
import a.m.a.s.coco.old.EventDispatcher;

public class CocoEventGenerator extends BaseDispatcher<CocoEvent> implements Runnable {
	EventDispatcher target = null;

	public void bind(EventDispatcher target) {
		this.target = target;
	}

	@Override
	public void run() {
	}

}
