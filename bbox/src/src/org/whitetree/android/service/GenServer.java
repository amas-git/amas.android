package org.whitetree.android.service;


import org.whitetree.systable.LOG;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import client.core.model.TimeStamp;

public class GenServer extends Service {
	public static long MAX_UPDATE_INTERVAL = 600000;
	public static long DEFAULT_UPDATE_INTERVAL = 3000;

	private volatile Looper mMonitorLooper;
	private volatile static ServiceHandler mServiceHandler;
	private String mName="UNKNOWN";
	private long mUpdateInterval = DEFAULT_UPDATE_INTERVAL;
	private long mCounter        = 0;
	private TimeStamp mTs = new TimeStamp();
	
	private Looper  mLooper  = null;
	private static Handler mHandler = null;
	
	PowerManager pm = null;
	
	private boolean mIsScreenOn = true;
	
	/**
	 * Handle commans/intent in handler thread for avoiding block main thread.
	 * @author amas
	 */
	class CoreServiceHandler extends Handler{

		public CoreServiceHandler(Looper mLooper) {
			super(mLooper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			Intent intent = (Intent) msg.obj;
			int startId   = msg.arg1;
			if (intent != null) {
				onHandleIntent(intent, startId);
			}
		}
	}
	
	/**
	 * 设置轮循时间
	 * @param interval 轮循时间(毫秒)
	 * @param immediate
	 */
	protected synchronized void setUpdateInterval(long interval, boolean immediate) {
		LOG._("变更为:=========" + interval);
		if(interval > 0) {
			mUpdateInterval = interval;
		}
		if (immediate) {
			nextRound(true);
		}
	}
	
	private Runnable mRuner = new Runnable() {
		
		@Override
		public void run() {
			try {
				LOG._(mName, String.format("(:COUNTER %d)", mCounter));
				loop();
			} catch (Exception e) {
				e.printStackTrace();
			}
			nextRound(false);
		}
	};
	
	/**
	 * 开始下一次论寻，
	 * @param immediate true: 立即执行一次论寻 
	 */
	synchronized private void nextRound(boolean immediate) {
		mCounter++;
		mServiceHandler.sendEmptyMessage(immediate ? 1 : 0);
	}
	
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1) {
				mServiceHandler.removeCallbacks(mRuner);
				mServiceHandler.post(mRuner);
			} else {
				mServiceHandler.removeCallbacks(mRuner);
				mServiceHandler.postDelayed(mRuner, getUpdateInterval());
			}
		}
	}
	
	public synchronized long getUpdateInterval() {
		return mUpdateInterval;
	}
	
	protected void loop() {

	}

	@Override
	public void onCreate() {
		super.onCreate();
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mIsScreenOn = pm.isScreenOn();
		
		startLoop();
		
		HandlerThread handlerThread = new HandlerThread("GenService[" + mName + ".C]", android.os.Process.THREAD_PRIORITY_BACKGROUND);
		handlerThread.start();

		mLooper  = handlerThread.getLooper();
		mHandler = new CoreServiceHandler(mLooper);
	}

	synchronized private void startLoop() {
		if (mIsScreenOn) {
			if(mMonitorLooper != null) {
				mMonitorLooper.quit();
				mMonitorLooper = null;
			}
			
			HandlerThread thread = new HandlerThread("GenService[" + mName + ".L]", android.os.Process.THREAD_PRIORITY_BACKGROUND);
			thread.start();
			mMonitorLooper  = thread.getLooper();
			mServiceHandler = new ServiceHandler(mMonitorLooper);
			mServiceHandler.sendEmptyMessage(0);
			mTs.reset(); /// start time trace
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Log.i(TAG, "SERVICE: onStartCommand() -- SID=" + startId + " I=" + intent);
		Message msg = mHandler.obtainMessage();
		msg.obj = intent;
		msg.arg1 = startId;
		mHandler.sendMessage(msg);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		mMonitorLooper.quit();
		mLooper.quit();
		double time = mTs.getLifeTimeSec();
		LOG._(mName, String.format("(:COUNTER %d :LIFETIME %.2fsec)", mCounter, time));
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void setName(String name) {
		mName = name;
	}
	
	protected void onHandleIntent(Intent intent, int startId) {
		LOG._(">>>>>>>>>>>>>>>> " + intent);
	}
	
	protected void onScreenStatusChanged() {
		mIsScreenOn = pm.isScreenOn();
		if (mIsScreenOn) {
			LOG._("--->>>>>>>>>>>>>>>> SCREEN ON start the LOOP...");
			startLoop();
		} else {
			LOG._("--->>>>>>>>>>>>>>>> SCREEN OFF shutdown the LOOP... ");
			if(mMonitorLooper != null) {
				mMonitorLooper.quit();
				mMonitorLooper = null;
			}
		}
	}
}
