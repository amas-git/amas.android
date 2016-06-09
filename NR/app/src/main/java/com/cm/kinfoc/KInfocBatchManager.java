package com.cm.kinfoc;

import android.content.Context;
import android.util.Log;

import com.cm.kinfoc.api.MMM;
import com.cm.kinfoc.base.InfocCommonBase;
import com.cm.kinfoc.base.InfocServerControllerBase;
import com.cm.kinfoc.base.InfocServerControllerBase.CONTROLLERTYPE;
import com.cm.kinfoc.base.InfocServerControllerBase.IResultCallback;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class KInfocBatchManager {
	
	private static KInfocBatchManager mInstance = new KInfocBatchManager();
	private KInfocBatchReporter mBatchRep = new KInfocBatchReporter();
	private int mProduceId = -1;
	private int mExpireDay = 0;
	private String mKfmtPath = null;
	private String mDataPublic = null;
	private KInfoControl mControl = null;
	private volatile boolean mIsReporting = false;
	private final Object mSyncObj = new Object();
	
	private Timer mReportTimer = null;
	private TimerTask mReportTask = null;
	private final Object mTimerSync = new Object();
	
	public static int REPORT_INTERVAL = 3 * 60 * 1000;
	public static int ADD_INTERVAL = 5000;
	public static int MIN_BATCH_REPORT_FILE_COUNT = 30;
	
	public static KInfocBatchManager getInstance() {
		return mInstance;
	}
	
	public void setReportData(KInfoControl control, String dataPublic, int produceId, int expireDay, String kfmtPath) {
		MMM.log("===================== BATCH REPORT ==========================");
//		MMM.log("   server1  : " + control.getHttpsServerUrl(1));
//		MMM.log("   server2  : " + control.getHttpsServerUrl(2));
//		MMM.log("   produceId: " + produceId);
//		MMM.log("   expireDay: " + expireDay);
		mControl = control;
		mProduceId = produceId;
		mExpireDay = expireDay;
		mKfmtPath = kfmtPath;
		mDataPublic = dataPublic;
	}
	
	public void requestReport(){
		MMM.log("-> KInfocBatchManager.requestReport");
		if(!KInfocClient.isInited()){
			return;
		}
		MMM.log("-> KInfocBatchManager.INITED");
		//是否正在上报
		synchronized (mSyncObj) {
			if(mIsReporting) {
				MMM.log("-> KInfocBatchManager.ONGOING EXIT");
				return;
			}
		}
		
		//是否为WIFI状态
		final Context context = InfocCommonBase.getInstance().getApplication().getApplicationContext();
		if(!KInfocCommon.isWiFiActive(context)){
			MMM.log("-> KInfocBatchManager.NETWORK DOWN");
			return;
		}
		
		
		//是否关闭了隐私数据上报
		InfocServerControllerBase.getInstance().getInfocRepPrivateDataAval(new IResultCallback() {
			@Override
			public void onResult(CONTROLLERTYPE type, boolean isSuccess, String result) {
				if(!isSuccess){
					return;
				}
				onStartReportX(context);
			}
		});
		
	}
	
	protected void onStartReportX(Context context) {
		if(needBatchReport(context)) {
			synchronized (mSyncObj) {
				if (!mIsReporting) {
					InfocCommonBase.getInstance().setLastBatchReportTime(System.currentTimeMillis());
					reportBatchDataX();
				}
			}
			clearTimer();
		}
		
		setNextReportTimer();
	}
	
	private boolean needBatchReport(Context context) {
		long now = System.currentTimeMillis();
		if (now - InfocCommonBase.getInstance().getLastBatchReportTime() >= KInfocBatchManager.REPORT_INTERVAL) {
			return true;
		}
		for (int nPriority = KInfoControl.emKInfoPriority_Unknow + 1; nPriority < KInfoControl.emKInfoPriority_End; ++nPriority) {
			if (KInfocUtil.getIchCount_CACHE_DIR(context, nPriority) >= KInfocBatchManager.MIN_BATCH_REPORT_FILE_COUNT) {
				return true;
			}
		}
		return false;
	}
	
	public static void createDir() {
		Context c = InfocCommonBase.getInstance().getApplication();
		
		for (int nPriority = KInfoControl.emKInfoPriority_Unknow + 1; nPriority < KInfoControl.emKInfoPriority_End; ++nPriority) {
			KInfocUtil.getExistOrCreate_CACHE_DIR_FORCE(c, nPriority);
			KInfocUtil.getExistOrCreate_CACHE_DIR(c, nPriority);
		}
	}

	private void setNextReportTimer() {
		if(mReportTimer != null) {
			return;
		}
		
		if(KInfocUtil.debugLog){
			Log.d(KInfocUtil.LOG_TAG, "set batch timer");
		}
		
		Context context = InfocCommonBase.getInstance().getApplication();
		synchronized(mTimerSync) {
			
			mReportTimer = new Timer();
			mReportTask = new TimerTask() {
				@Override
				public void run() {
					requestReport();
				}
			};
			
			long interval = REPORT_INTERVAL - (System.currentTimeMillis() - InfocCommonBase.getInstance().getLastBatchReportTime()) + ADD_INTERVAL;
			if(interval <= 0) {
				interval = REPORT_INTERVAL;
			}
			mReportTimer.schedule(mReportTask, interval);
		}
	}
	
	private void clearTimer() {
		if(KInfocUtil.debugLog){
			Log.d(KInfocUtil.LOG_TAG, "clear batch timer");
		}
		
		synchronized(mTimerSync) {
			if(null != mReportTask) {
				mReportTask.cancel();
				mReportTask = null;
			}
			
			if(null != mReportTimer) {
				mReportTimer.purge();
				mReportTimer.cancel();
				mReportTimer = null;
			}
		}
	}
	
	private String getServerUrl(int p) {
		if(mControl == null) {
			return null;
		}
		return mControl.getHttpsServerUrl(p);
//		if(p == KInfoControl.emKInfoPriority_Normal){
//			return  mControl.getHttpsServerUrl(p);
//		}else{
//			return  mControl.getServerUrl(p);
//		}
	}
	
	private void reportBatchDataX() {
		synchronized (mSyncObj) {
			mIsReporting = true;
		}

		new Thread() {
			@Override
			public void run() {

				try {
					KInfocBatchReporter.d(" BATCH REPORTER STARTED ........");
					Context context = InfocCommonBase.getInstance().getApplication().getApplicationContext();

					for (int nPriority = KInfoControl.emKInfoPriority_Unknow + 1; nPriority < KInfoControl.emKInfoPriority_End; ++nPriority) {

						File ichd = KInfocUtil.getExisted_CACHE_DIR(context, nPriority);
						if (ichd == null) {
							continue;
						}

						File[] ichs = ichd.listFiles();
						if (ichs == null || ichs.length == 0) {
							continue;
						}

						KInfocBatchReporter.d(" -> ICH DIR : " + ichd.getAbsolutePath());
						int rc = 0;
						if(mControl.isV5(nPriority)){
							rc = mBatchRep.startReportX(getServerUrl(nPriority), ichs, mDataPublic, mProduceId, mKfmtPath, mExpireDay);
						}else{
							rc = mBatchRep.startReportX(getServerUrl(nPriority), ichs, mDataPublic, mProduceId, mKfmtPath, mExpireDay);
						}
						
						
						if (rc == KInfocBatchReporter.REPORT_RESULT_FAIL_NETWORK) {
							break;
						}
					}
				} finally {
					synchronized (mSyncObj) {
						mIsReporting = false;
					}
				}
			}
		}.start();
	}
}
