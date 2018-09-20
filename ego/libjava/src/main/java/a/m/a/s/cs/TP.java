package a.m.a.s.cs;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
/**
 * A VERY SIMPLE THREAD POOL
 */
public class TP extends Thread {
	List<Runnable> queue = new LinkedList<Runnable>();
	int workers = 0;

	public TP(int workers) {
		this.workers = workers;
		setName(this.getClass().getName());
	}

	@Override
	public void run() {
		for (int i = 0; i < workers; ++i) {
			new Worker("worker:" + i).start();
		}
	}

	class Worker extends Thread {
		public Worker(String name) {
			setName(name);
		}

		@Override
		public void run() {
			System.out.println("START WORKER : " + getName());
			while (true) {
				try {
					loop();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void loop() throws InterruptedException {
			Runnable r = null;
			synchronized (queue) {
				if (queue.isEmpty()) {
					queue.wait();
				}

				r = queue.remove(0);
			}
			r.run();
		}
	}

	public void execute(Runnable job) {
		synchronized (queue) {
			queue.add(job);
			queue.notifyAll();
		}
	}
}
