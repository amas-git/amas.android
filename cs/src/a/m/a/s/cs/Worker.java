package a.m.a.s.cs;

public class Worker extends CP {
	@Override
	public void consume(Object o) {
		if (o instanceof Runnable) {
			((Runnable) o).run();
		}
	}
}
