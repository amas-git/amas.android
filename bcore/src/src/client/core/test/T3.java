package client.core.test;

import client.core.model.Event;
import client.core.model.Task;

public class T3 extends Task {
	@Override
	protected Event process() {
		System.out.println("T3: running .....");
		for(int i=0; i< 100000; ++i) {
			System.err.println("T3");
		}
		return new E3();
	}
}