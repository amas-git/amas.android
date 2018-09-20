package a.m.a.s.cs;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class CP extends Thread {
	List<Object> queue = new LinkedList<Object>();
	volatile boolean stop = false;

	public void product(Object o) {
		synchronized (queue) {
			queue.add(o);
			queue.notify();
		}
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				looper();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void looper() throws InterruptedException {
		Object o = null;
		synchronized (queue) {
			if (queue.isEmpty()) {
				queue.wait();
			}
			o = queue.remove(0);
		}
		consume(o);
	}

	public void consume(Object o) {

	}
}
