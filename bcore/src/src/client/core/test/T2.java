package client.core.test;

import client.core.model.Event;
import client.core.model.Task;

public class T2 extends Task {
	@Override
	protected Event process() {
		System.out.println("T2: running .....");
		for(int i=0; i< 100000; ++i) {
			System.err.println("T2");
		}
		return new E2();
	}
}