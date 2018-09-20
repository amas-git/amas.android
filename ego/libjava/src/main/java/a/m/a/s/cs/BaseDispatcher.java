package a.m.a.s.cs;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by amas on 9/5/17.
 */
public abstract class BaseDispatcher<T> implements Runnable {
	protected BlockingQueue<T> mQueue = new LinkedBlockingQueue<>();
	private volatile boolean mStop = false;
	private Thread mThread = null;
	private volatile int mMaxFailedTimes = 10;
	private Stat mStat = new Stat();
	private String mName = null;

	class Stat {
		volatile long dispatcher_n = 0;
		volatile long start_time = 0;
		volatile long failed_times = -1;
		long max_dispatch_time = 0;
		long min_dispatch_time = Long.MAX_VALUE;
		long all_dispatch_time = 0;
		long all_idle_iime = 0;
		public void incIdleTime(long time) {
			all_idle_iime += time;
		}

		public void onDispatchFinished(long time) {
			if(time > max_dispatch_time) {
				max_dispatch_time = time;
			} else if (time < min_dispatch_time) {
				min_dispatch_time = time;
			}

			all_dispatch_time += time;
			dispatcher_n+=1;
		}

		@Override
		public String toString() {
			StringBuilder _s = new StringBuilder();
			_s.append("[ST] " + mName).append("\n")
				.append("[ST] " + " + max   : " + max_dispatch_time).append("\n")
				.append("[ST] " + " + min   : " + min_dispatch_time).append("\n")
				.append("[ST] " + " + all   : " + all_dispatch_time).append("\n")
				.append("[ST] " + " + times : " + dispatcher_n).append("\n")
				.append("[ST] " + " + avg   : " + ((1.0f * all_dispatch_time) / dispatcher_n) + "(ms)").append("\n")
				.append("[ST] " + " + idle  : " + all_idle_iime + "(ms)").append("\n");
			return _s.toString();
		}
	}


	public void setName(String name) {
		mName = name;
	}

	public BaseDispatcher() {
	}

	protected BlockingQueue<T> getQueue() {
		if(mQueue == null) {
			mQueue = new LinkedBlockingQueue<T>();
		}
		return mQueue;
	}

	volatile int promote = Thread.NORM_PRIORITY;

	public void degrade() {
		promote = (promote < Thread.MIN_PRIORITY) ? Thread.MIN_PRIORITY : promote - 2;
	}

	public void promote() {
		promote = (promote > Thread.MAX_PRIORITY) ? Thread.MAX_PRIORITY : promote + 2;
	}


	// start a dispatch loop;
	public void run() {
		while (!mStop && !Thread.interrupted()) {

			if(promote !=  Thread.currentThread().getPriority()) {
				Thread.currentThread().setPriority(promote);
				System.out.println("PROMOTED : " + promote);
			}

			try {
				dispatchLoop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("我操，發生Exception了");
				e.printStackTrace();
			}
		}

	}

	private void checkThread() {
		synchronized (this) {
			if(mThread == null || !mThread.isAlive()) {
				mThread = newThread(this, mName);
				mStat.failed_times+=1;
				if(mStat.failed_times < mMaxFailedTimes) {
					mThread.start();
					onStartUp(mStat.failed_times);
				}
			}
		}
	}

    /**
     * 线程启动时调用
     * @param times
     */
	abstract protected void onStartUp(long times);

    /**
     * 分发者
     * @param elem
     */
    abstract public void onDispatch(T elem);

    /**
     * 分发后调用
     */
    abstract protected void onPostDispatch();

    /**
     * 分发前调用
     */
    abstract protected void onPreDispatch();

    /**
     * 入队时候调用
     * @param elem
     */
    protected void onPush(T elem) {

    }

	public Thread newThread(Runnable r, String name) {
		Thread t = new Thread(r);
		t.setName("DISPATCHER : " + (name == null ? t.getId() : name));
		//t.setUncaughtExceptionHandler(eh);
		return t;
	}

	static final int IDLE_DETECT = 0;

	protected void dispatchLoop() throws InterruptedException {
		long start = System.currentTimeMillis() ;
		long diff = 0;
		boolean idle = false;

		int etimes = 0;
		while(getQueue().isEmpty()) {
			if(etimes++ > IDLE_DETECT) {
				idle = true;
				break;
			}
		}

		T elem = getQueue().take();
		diff = System.currentTimeMillis() - start;
		if(idle) {
			mStat.incIdleTime(diff);
		} else {

		}
		start = System.currentTimeMillis();
		onPreDispatch();
		onDispatch(elem);
		onPostDispatch();
		diff = System.currentTimeMillis() - start;
		mStat.onDispatchFinished(diff);
	}


	/** may not start at once */
	public void push(T elem) {
		getQueue().add(elem);
	}

	public void start() {
		checkThread();
	}

	public void shutdown() {
		synchronized (this) {
			mStop = true;
			if (mThread != null) {
				mThread.interrupt();
			}
		}
	}

	public void dump() {
		System.out.println(mStat);
	}
}