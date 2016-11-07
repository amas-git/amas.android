package a.m.a.s.coco;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class CocoTask extends CocoObject implements Runnable {
	protected static Ctrl EMPTY_CTRL = new Ctrl();
	protected int did = 0;
	protected int color = 0;
	protected Ctrl ctrl = EMPTY_CTRL;
	
	public CocoTask() {
		this.color = hashCode();
	}
	
	public CocoTask(int color) {
		this.color = color;
	}
	
	public CocoTask to(int to) {
		this.did = to;
		return this;
	}
	
	public static class CocoTaskCancelException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public CocoTaskCancelException() {
			super("normal canceled");
		}
	}

	public void pause()             { if(ctrl != null) ctrl.pause();         }
	public void pause(long timeout) { if(ctrl != null) ctrl.pause(timeout);  }
	public void cancel()            { if(ctrl != null) ctrl.canceled = true; }
	public void resume()            { if(ctrl != null) ctrl.resume();        }
	
	public static class Ctrl {
		volatile boolean canceled = false;
		volatile boolean paused = false;
		volatile long timeout = 0;
		
		ReentrantLock lock = new ReentrantLock();
		Condition c = lock.newCondition();
		
		public void pause() {
			if(canceled) {
				return;
			}
			paused = true;
		}
		
		public void pause(long timeout) {
			if(canceled || paused) {
				return;
			}
			paused = true;
			this.timeout = Math.abs(timeout);
		}
		
		public void resume() {
			if(canceled) {
				return;
			}
			if(paused) {
				synchronized(lock) {
					lock.notify();
				}
				paused  = false;
				timeout = 0;
			}
		}
		
		/**
		 * block with ctrl lock or throw CocoTaskCancelException when task be canceled
		 */
		public void barrier() {
			if(canceled) {
				throw new CocoTaskCancelException();
			}
			
			if(paused) {
				try {
					synchronized(lock) {
						lock.wait(timeout);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				paused = false;
				timeout = 0;
			}
		}
		
		public void cancel() {
			canceled = true;
		}
	}

	public CocoTask setCtrl(final Ctrl ctrl) {
		if(ctrl != null)
			this.ctrl = ctrl;
		return this;
	}
	


	protected void onPreCall() {}
	
	protected void onPostCall() {}
	
	protected void onCanceled() { System.out.println("[CANCELED] : " + this); }
	
	
	@Override
	final public void run() {
		CocoEvent event = null;
		try {
			ctrl.barrier();
			onPreCall();
			event = call();
			onPostCall();
		} catch (CocoTaskCancelException e) {
			onCanceled();
		} finally {
			fire(event);
		}
	}
	


	protected void barrier() { if(ctrl != null) ctrl.barrier(); }

	public CocoEvent call() {
		return null;
	}
	
	final protected void fire(CocoEvent event) {
		if (event != null) {
			event.from(this.color);
			event.send(this.did);
		}
	}
}
