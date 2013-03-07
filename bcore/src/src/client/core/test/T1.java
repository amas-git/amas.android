package client.core.test;

import client.core.model.Event;
import client.core.model.Task;

public class T1 extends Task {
	@Override
	protected Event process() {
		System.out.println("T1: running .....");
		for(int i=0; i< 100000; ++i) {
			System.err.println("T1");
		}
		return new E1();
	}
}
