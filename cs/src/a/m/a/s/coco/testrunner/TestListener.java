package a.m.a.s.coco.testrunner;

import a.m.a.s.coco.CocoEvent;
import a.m.a.s.coco.old.ICocoEventListener;

public class TestListener implements ICocoEventListener {
	public String name = "";

	public TestListener(String name) {
		this.name = name;
	}

	@Override
	public void onEvent(CocoEvent event) {
		System.out.println(name+": " + event);
	}
}
