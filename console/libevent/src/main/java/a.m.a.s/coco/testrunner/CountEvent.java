package a.m.a.s.coco.testrunner;

import a.m.a.s.coco.CocoEvent;

public class CountEvent extends CocoEvent {
	int i, max;

	public CountEvent(int i, int max) {
		this.i = i;
		this.max = max;
	}

	@Override
	public String toString() {
		return String.format("(CountEvent : %4d/%4d %s)", i, max, super.toString());
	}
	
	public boolean isFirst() {
		return i == 0;
	}
	
	public boolean isLast() {
		return i == max -1;
	}

}
