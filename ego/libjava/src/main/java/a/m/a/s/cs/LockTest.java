package a.m.a.s.cs;

public class LockTest {

	public synchronized void forever() {
		try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stuck() {
		System.out.println("hello......");
		synchronized (this) {
			System.out.println("dosomething......");
		}
	}
}
