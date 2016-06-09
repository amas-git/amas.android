package com.cm.kinfoc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.cm.kinfoc.IHttpSender.OnResultListener;
import com.cm.kinfoc.api.MMM;
import com.cm.kinfoc.base.InfocCommonBase;
import com.cm.kinfoc.base.InfocServerControllerBase;
import com.cm.kinfoc.base.InfocServerControllerBase.CONTROLLERTYPE;
import com.cm.kinfoc.base.InfocServerControllerBase.IResultCallback;
import com.cm.kinfoc.base.KHttpsPoster;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;


/**
 * Infoc上报类
 * @author singun
 *
 */
public class KInfocReporter {

	// 用于活跃 计时
	private static final String TIMER_ACTION = "com.ijinshan.common.kinfoc.ActivityTimer";
	private static boolean DEBUG_AUTO_POSTER = false;

	/*
	 * 普通上报相关定义
	 */
	private Context mContext 		= null;
	private boolean 			mCacheEnable 	= true;	//默认开启缓存
	private long 				mExpireDay 		= 0;
	private boolean				mToggle 		= true;	//上报开关
	
	private volatile boolean    mbIsReportingCache = false;
	private Object mSyncReportingObject = new Object();

	public KInfoControl mControl = null;
	private KHttpPoster mPoster  = null;
//	private KHttpGeter 	mGeter   = null;
	private KHttpsPoster mHttpsPoster = null;
	

	
	
	//上报结果回调
	OnResultListener mOnResult = new OnResultListener() {
		@Override
		public void onSuccess(long lasttime, KHttpData data) {
			if (KInfocUtil.debugLog)
				Log.i(KInfocUtil.LOG_TAG, "Post successed, " + "server Priority: " + data.getServerPriority() + ", table: " + data.getTableName() + ", last time: " + lasttime);
			
			if(data == null){
				return;
			}
			
			//删除批量上报的数据文件
			if(!data.isForce() && data.getBatchFiles() != null){
				for(String filePath : data.getBatchFiles()){
					File file = new File(filePath);
					if(file.exists() && file.isFile()){
						file.delete();
					}
				}
			}
			
			long cacheTime = data.getCacheTime();
			if ((cacheTime > 0) && mCacheEnable && data.getData() != null && data.getServerPriority() != KInfoControl.emKInfoPriority_Unknow){
				cleanCacheFile(data.getTableName(), data.isForce(), cacheTime, false, data.getServerPriority());
			}
			
			if((cacheTime > 0) && mCacheEnable && !TextUtils.isEmpty(data.getStringData())){
				cleanCacheFile(data.getTableName(), data.isForce(), cacheTime, true, KInfoControl.emKInfoPriority_Unknow);
			}
		}
		
		@Override
		public void onFail(KHttpData data) {
			if (KInfocUtil.debugLog)
				Log.i(KInfocUtil.LOG_TAG, "Post failed, " + "server Priority: " + data.getServerPriority() + ", table: " + data.getTableName());
			
			if(data == null){
				return;
			}
			
			//非强制数据属于批量上报数据，已经在本地
			if ( (data.getCacheTime() == 0) && mCacheEnable && data.isForce()) {
				if(data.getData() != null && data.getServerPriority() != KInfoControl.emKInfoPriority_Unknow){
					saveCache(data.getData(), data.getTableName(), data.isForce(), false, data.getServerPriority());
				}
				
				if(!TextUtils.isEmpty(data.getStringData())){
					saveCache(data.getStringData().getBytes(), data.getTableName(), data.isForce(), true, KInfoControl.emKInfoPriority_Unknow);
				}
			}
		}
	};
	
	/*
	 * 自动上报相关定义
	 */
	private int mDelayTime = 20 * 1000;	// 延迟20秒上报缓存

	private IntentFilter mFilter 			= null;
	private IntentFilter mActivityFilter 	= null;
	private Intent mActivityIntent 	= null;
	private PendingIntent mActivitySender 	= null;
	private AlarmManager mAlarm 				= null;
	private Handler mHandler 			= null;

	//接收网络连接已改变的广播
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				if (mHandler != null) {
					mHandler.postDelayed(mPostRunnable, getDelayTime());
				}
			}
		}
	};

	//接收定时器广播
	private BroadcastReceiver mActivityReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(TIMER_ACTION)) {
				if (mHandler != null) {
					if (DEBUG_AUTO_POSTER) {
						act("REPORT=" + System.currentTimeMillis());
					}
					mHandler.post(mPostRunnable);				}
			}
		}
	};

	//自动上报线程
	private Runnable mPostRunnable = new Runnable() {
		private Boolean mIsRunning = false;
		private final Object mRunningSyncObj = new Object();
		
		@Override
		public void run() {
			synchronized (mRunningSyncObj) {
				if (!mIsRunning) {
					if (KInfocUtil.debugLog)
						Log.i(KInfocUtil.LOG_TAG, "Auto Post");
					
					mIsRunning = true;
					
					new Thread() {
						@Override
						public void run() {
							//强制上报
							reportCache();
							//批量上报
							KInfocClient.getInstance().requestBatchReport();
							mIsRunning = false;
						}
					}.start(); 
				}
			}
		}
	};
	
	public void reportCache(){		
		if (mbIsReportingCache) { // 保证整个逻辑不要同时多个线程进入，造成同样的数据大量上传
			return;
		}
		
		if (isCacheEnable() && KInfocCommon.isNetworkActive(mContext)) {	
			
			InfocServerControllerBase.getInstance().getInfocRepPrivateDataAval(new IResultCallback() {				
				@Override
				public void onResult(CONTROLLERTYPE type, boolean isSuccess, String result) {
					if(!isSuccess){
						return;
					}
					
					try {
						synchronized(mSyncReportingObject) {
							if (mbIsReportingCache) {
								return;
							} else {
								mbIsReportingCache = true;
							}
						}
						
						
						File dir = KInfocUtil.getExisted_CACHE(mContext);
						if(dir == null) {
							return;
						}
			
						for(int nPriority = KInfoControl.emKInfoPriority_Unknow + 1; nPriority < KInfoControl.emKInfoPriority_End; ++nPriority) {
							postCache(true, false, nPriority);
						}
					} finally {
						mbIsReportingCache = false;
					}
					
					//改成批量上报了
					/*if (KInfocCommon.isWiFiActive(mContext)) {
						for(int nPriority = KInfoControl.emKInfoPriority_Unknow + 1; nPriority < KInfoControl.emKInfoPriority_End; ++nPriority) {
							postCache(false, false, nPriority);
							//postCache(false, true);
						}
					}*/
				}
			});
		}
	}
	
	//定时上报线程
	/*private class ReportThread extends Thread {
		@Override
		public void run() {
			if (KInfocUtil.debugLog)
				Log.i(KInfocUtil.LOG_TAG, "Report OnTimer");
			
			// 每4小时也进行一次数据上报
			reportCache();
			KInfocClient.getInstance().requestBatchReport();
			
//			DubaInfocUtil.reportActive();
			// 这是手机毒霸使用的逻辑，我们不需要。
		}
	};*/

	/*
	 * 以下为方法实现
	 */
	
	/**
	 * 初始化
	 */
	public KInfocReporter(Context context, KInfoControl control){

		if (control != null) {
			mControl = control;
		}
		if (context != null) {
			mContext = context;
			mHandler = new Handler(context.getMainLooper());
		}
		
		mPoster = new KHttpPoster();
		mHttpsPoster = new KHttpsPoster();
//		mGeter  = new KHttpGeter();
	}
	
	/**
	 * 设置Infoc开关
	 * @param 开关	true 上报 false 不上报
	 */
	public void setInfocToggle(boolean toggle) {
		mToggle = toggle;
	}
	
	/**
	 * 发送数据
	 * @param myData 数据内容
	 * @param tableName 表名/缓存文件名
	 * @param isForce 强制上传
	 */
	public void postData(byte[] myData, String tableName, boolean isForce, KHttpResultListener listener) {
		postData(myData, tableName, isForce, 0, listener, null);
	}
	
	/**
	 * 发送数据
	 * @param myData 数据内容
	 * @param tableName 表名/缓存文件名
	 * @param isForce 强制上传
	 */
	public void postData(byte[] myData, String tableName, boolean isForce) {
		postData(myData, tableName, isForce, 0, null, null);
	}
	
	/**
	 * 发送数据
	 * @param myData 数据内容
	 * @param tableName 表名/缓存文件名
	 * @param isForce 强制上传
	 * @param cacheTime 缓存时间，用于上报缓存，普通上报请设为0
	 * @param specialServerUrl 指定的域名上报，如果为null，则会调用 getServerUrl 获取
	 */
	public void postData(byte[] myData, String tableName, boolean isForce, long cacheTime) {
		postData(myData, tableName, isForce, cacheTime, null, null);
	}
	
	public void postBatchData(byte[] myData, String tableName, ArrayList<String> files, KHttpResultListener listener){
		postData(myData, tableName, false, 0, listener, files);
	}
	
	/**
	 * 发送数据
	 * @param myData 数据内容
	 * @param tableName 表名/缓存文件名
	 * @param isForce 强制上传
	 * @param cacheTime 缓存时间，用于上报缓存，普通上报请设为0
	 * @param specialServerUrl 指定的域名上报，如果为null，则会调用 getServerUrl 获取
	 */
	public void postData(byte[] myData, String tableName, boolean isForce, long cacheTime, KHttpResultListener listener, ArrayList<String> files) {
		if (mContext == null || tableName == null || myData == null)
			return;
		
		if (KInfocCommon.isNetworkActive(mContext)) {			
			KHttpData data = new KHttpData();
			data.setData(myData);
			data.setTableName(tableName);
			data.setForce(isForce);
			data.setCacheTime(cacheTime);
			data.setHttpListener(listener);
			data.setBatchFiles(files);
			
			if (isForce) {
				if (KInfocUtil.debugLog)
					Log.d(KInfocUtil.LOG_TAG, "Post data via network.");
			
				//data.setServerPriority(KInfoControl.emKInfoPriority_Basic);
				//mPoster.send(data, mControl.getServerUrl(KInfoControl.emKInfoPriority_Basic), mOnResult);
				
				//再报一个V5服务器 deep clone super.clone()
				KHttpData data_V5 = (KHttpData)data.clone();
				data_V5.setServerPriority(KInfoControl.emKInfoPriority_Normal);
				mHttpsPoster.send(data_V5, mControl.getHttpsServerUrl(KInfoControl.emKInfoPriority_Normal), mOnResult);

			} else {
				if (KInfocCommon.isWiFiActive(mContext)) {
					if (KInfocUtil.debugLog)
						Log.d(KInfocUtil.LOG_TAG, "Post data via Wifi.");
					
					//data.setServerPriority(KInfoControl.emKInfoPriority_Basic);
					//mPoster.send(data, mControl.getServerUrl(KInfoControl.emKInfoPriority_Basic), mOnResult);


					
					//再报一个V5服务器
					KHttpData data_V5 = (KHttpData)data.clone();
					data_V5.setServerPriority(KInfoControl.emKInfoPriority_Normal);
					mHttpsPoster.send(data_V5, mControl.getHttpsServerUrl(KInfoControl.emKInfoPriority_Normal), mOnResult);
					
					
				} else {
					//非强制上报数据已经在本地保存了
					/*if ((cacheTime == 0) && mCacheEnable){
						saveCache(myData, tableName, isForce, false, KInfoControl.emKInfoPriority_Basic);
						saveCache(myData, tableName, isForce, false, KInfoControl.emKInfoPriority_Normal);
					}*/
				}
			}
		} else {
			if ((cacheTime == 0) && mCacheEnable && isForce) {
				saveCache(myData, tableName, isForce, false, KInfoControl.emKInfoPriority_Basic);
				saveCache(myData, tableName, isForce, false, KInfoControl.emKInfoPriority_Normal);
			}
		}
	}
	
	/*
	 * @purpose 只用来报POST缓存
	 */
	private void postCacheData(byte[] myData, String tableName, boolean isForce, long cacheTime, KHttpResultListener listener, int nPriority) {
		if (mContext == null || tableName == null || myData == null)
			return;
		
		if (KInfocCommon.isNetworkActive(mContext)) {			
			KHttpData data = new KHttpData();
			data.setData(myData);
			data.setTableName(tableName);
			data.setForce(isForce);
			data.setCacheTime(cacheTime);
			data.setHttpListener(listener);
			
			
			if (isForce) {
				if (KInfocUtil.debugLog)
					Log.d(KInfocUtil.LOG_TAG, "Post data via network.");
				
				KInfocUtil.log2local("post cache on " + mControl.getServerUrl(nPriority) + " table name: " + data.getTableName() + " cache time: " + Long.toString(data.getCacheTime()));
				
				data.setServerPriority(nPriority);
				
				if(mControl.isV5(nPriority)){
					mHttpsPoster.httpsPost(data, mControl.getHttpsServerUrl(nPriority), mOnResult);
				}else{
					 // 转换成同步的上报，防止同一数据被多次上报
					mPoster.httpPost(data, mControl.getServerUrl(nPriority), mOnResult);
				}
				
			} else { // 已经批量上传
				if (KInfocCommon.isWiFiActive(mContext)) {
					if (KInfocUtil.debugLog)
						Log.d(KInfocUtil.LOG_TAG, "Post data via Wifi.");
					
					KInfocUtil.log2local("post cache on " + mControl.getServerUrl(nPriority) + " table name: " + data.getTableName() + " cache time: " + Long.toString(data.getCacheTime()));
					
					data.setServerPriority(nPriority);
					if(mControl.isV5(nPriority)){
						mHttpsPoster.httpsPost(data, mControl.getHttpsServerUrl(nPriority), mOnResult);
					}else{
						 // 转换成同步的上报，防止同一数据被多次上报
						mPoster.httpPost(data, mControl.getServerUrl(nPriority), mOnResult);
					}

				}
			}
		}
	}
	


	/**
	 * XXX(zhoujiabo): 忽略公共头，只存储数据部分，为了避免修改C层，采用了传入Header,然后计算Header长度，再去掉Header的方式
	 * @param header     公共字段
	 * @param produceId
	 * @param kfmtPath
	 * @param data
	 * @param tableName
	 * @param isForce
	 * @param serverPriority
	 * @return
	 */
	public boolean saveCacheNoHeader(String header, int produceId, String kfmtPath, byte[] data, String tableName, boolean isForce, int serverPriority) {
		int headerLen = KInfocUtil.calcPublicHeaderLength(mContext, header, produceId, kfmtPath);
		return saveCache(data, headerLen, tableName, isForce, serverPriority);
	}

	public boolean saveCache(byte[] data, int startOffset, String tableName, boolean isForce, int serverPriority) {
		boolean bReturn = false;
		File dIch = isForce
				? KInfocUtil.getExistOrCreate_CACHE_DIR_FORCE(mContext, serverPriority) 
				: KInfocUtil.getExistOrCreate_CACHE_DIR(mContext,serverPriority);
		if(MMM.DEBUG) MMM.log(" + SAVE CACHE : " + "ISFORCE="+isForce + " > " + dIch.getAbsolutePath()); 
		if (dIch == null) {
			return false;
		}

		if (startOffset < 0 || startOffset > data.length) {
			return false;
		}

		int size = data.length - startOffset;
		if (size <= 0) {
			return false;
		}

		
		ByteBuffer b = ByteBuffer.allocate(size);

		for(int i=startOffset; i<data.length; ++i) {
			b.put(data[i]);
		}
		
		if (MMM.DEBUG) {
			MMM.log("SAVE ICH : " + "ALL="+data.length + " startOffset="+startOffset+ " HL="+startOffset + " DL="+b.array().length);
		}
		try {
			KFile file = new KFile(mContext);
			bReturn = file.saveCacheFile(dIch.getAbsolutePath(), tableName + KInfocUtil.SEP_CHAR + System.currentTimeMillis() + KInfocUtil.FILE_EXT,
			        b.array());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bReturn;
	}

	/**
	 * 保存缓存文件
 	 * @param myData
	 * @param isForce
	 * @param serverPriority 会有对应的server url, 要保存在不同的文件夹
	 */
	public boolean saveCache(byte[] myData, String tableName, boolean isForce, boolean isGet, int serverPriority) {
		boolean bReturn = false;
		
		File dIch = isForce
				? KInfocUtil.getExistOrCreate_CACHE_DIR_FORCE(mContext, serverPriority) 
				: KInfocUtil.getExistOrCreate_CACHE_DIR(mContext,serverPriority);
		
		if (dIch == null) {
			return false;
		}
				
		try {
			KFile file = new KFile(mContext);
			bReturn = file.saveCacheFile(dIch.getAbsolutePath(), tableName + KInfocUtil.SEP_CHAR + System.currentTimeMillis() + KInfocUtil.FILE_EXT,myData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bReturn;
	}

	/**
	 * 发送缓存文件
	 * @param isForce
	 */
	private void postCache(boolean isForce, boolean isGet, int nServerPriority) {
		if (mContext == null)
			return;
		
		
		byte[] myData = null;
		String fileName, tableName, dateStr;
		int index = 0;
		
		try{
			File dIch = isForce
					? KInfocUtil.getExisted_CACHE_DIR_FORCE(mContext, nServerPriority)
					: KInfocUtil.getExisted_CACHE_DIR(mContext, nServerPriority);
			
			if(dIch == null) {
				return;
			}
			
			File[] files = dIch.listFiles();
			if (null == files) {
				return;
			}
			
			for (int i = 0; i < files.length; i++) {
				if (KInfocUtil.debugLog)
					Log.d(KInfocUtil.LOG_TAG, "Post cache " + (i + 1));
				
				fileName = files[i].getName();
				if (!files[i].isFile() || !fileName.endsWith(KInfocUtil.FILE_EXT))
					continue;
				
				index = fileName.lastIndexOf(KInfocUtil.SEP_CHAR);
				if (index == -1) 
					continue;
				
				tableName = fileName.substring(0, index);
				dateStr = fileName.substring(index + 1, fileName.length()-4);
				long cacheDate = 0;
				try {
					cacheDate = Long.parseLong(dateStr);
				} catch(NumberFormatException e) {
					e.printStackTrace();
				}
				
				if (!mToggle) {
					files[i].delete();
					continue;
				}
				
				if (mExpireDay > 0){	//过期
					if (KInfocUtil.getDayDiff(cacheDate) >= mExpireDay){
						files[i].delete();
						continue;
					}
				}
				
				myData = KFile.readBuffer(files[i]);
				if (myData != null) {
					if(isGet){
						//get(new String(myData), tableName, isForce, cacheDate, mControl.getOverseaServerUrl());
					}else{
						postCacheData(myData, tableName, isForce, cacheDate, null, nServerPriority);
					}
				}
				
				Thread.sleep(1000);	//大量文件时内存回收可能不及时，暂停一秒
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除所有缓存文件
 	 * @param isForce
	 */
	public void cleanCache(boolean isForce) {
		if (mContext == null)
			return;
		
		for(int nPriority = KInfoControl.emKInfoPriority_Unknow + 1; nPriority < KInfoControl.emKInfoPriority_End; ++nPriority){
			File dir = isForce
					? KInfocUtil.getExisted_CACHE_DIR_FORCE(mContext, nPriority)
					: KInfocUtil.getExisted_CACHE_DIR(mContext, nPriority);
			if(dir != null) {
				KFileUtil.delAllFile(dir.getAbsolutePath());
			}
		}
	}
	
	/*
	 * 清理不同域名的缓存
	 */
	public void cleanCache(int kinfoPriority){
		if (mContext == null) {
			return;
		}
		
		File dir = KInfocUtil.getExisted_CACHE_DIR_FORCE(mContext, kinfoPriority);
		if(dir != null) {
			KFileUtil.delAllFile(dir.getAbsolutePath());
		}
		
		dir = KInfocUtil.getExisted_CACHE_DIR(mContext, kinfoPriority);
		if(dir != null) {
			KFileUtil.delAllFile(dir.getAbsolutePath());
		}
	}
	
	/**
	 * 删除指定缓存文件
	 * @param tableName
 	 * @param isForce
	 */
	public void cleanCacheFile(String tableName, boolean isForce, long cacheTime, boolean isGet, int nServerPriotiry) {
		if (mContext == null)
			return;
		File dir = isForce
				? KInfocUtil.getExisted_CACHE_DIR_FORCE(mContext, nServerPriotiry)
				: KInfocUtil.getExisted_CACHE_DIR(mContext, nServerPriotiry);
		if(dir == null) {
			return;
		}
		
		KFileUtil.deleteFile(dir.getAbsolutePath() + File.separatorChar + tableName + KInfocUtil.SEP_CHAR + cacheTime + KInfocUtil.FILE_EXT);
	}
	
	/**
	 * 删除过期缓存文件
 	 * @param isForce
	 */
	public void cleanExpireCache(boolean isForce, boolean isGet, int nServerPriotiry) {
		if (mContext == null || mExpireDay == 0)	//过期
			return;
		
		File dir = isForce
				? KInfocUtil.getExisted_CACHE_DIR_FORCE(mContext, nServerPriotiry)
				: KInfocUtil.getExisted_CACHE_DIR(mContext, nServerPriotiry);
				
		if(dir == null) {
			return;
		}
		
		File files[] = dir.listFiles();
		if (null == files) {
			return;
		}
		
		String fileName, dateStr;
		int index;
		for (int i = 0; i < files.length; i++) {
	        if(files[i].isFile() && files[i].exists()) {
	        	fileName = files[i].getName();
				if (!files[i].isFile())
					continue;
				index = fileName.lastIndexOf(KInfocUtil.SEP_CHAR);
				if (index == -1) 
					continue;
				dateStr = fileName.substring(index + 1, fileName.length()-4);
				long cacheDate = 0;
				try {
					cacheDate = Long.parseLong(dateStr);
				} catch(NumberFormatException e) {
					e.printStackTrace();
				}
				if (KInfocUtil.getDayDiff(cacheDate) >= mExpireDay) {
					files[i].delete();
				}
	        }
		}
	}
	
	/**
	 * 线程删除过期缓存文件
	 */
	public void cleanExpireCacheThread() {
		if (mContext == null || mHandler == null || mExpireDay == 0)	//过期
			return;
		
		new Thread() {
			@Override
			public void run() {
				
				for(int nPriority = KInfoControl.emKInfoPriority_Unknow + 1; nPriority < KInfoControl.emKInfoPriority_End; ++nPriority){
					cleanExpireCache(true, false, nPriority);
					cleanExpireCache(false, false, nPriority);
				}
				
				//get 
				//cleanExpireCache(true, true, KInfoControl.emKInfoPriority_Unknow);
				//cleanExpireCache(false, true, KInfoControl.emKInfoPriority_Unknow);
			}
		}.start();
	}
	
	/**
	 * 判断缓存是否可用
 	 * @return true:可用 false:不可用
	 */
	public boolean isCacheEnable() {
		return mCacheEnable;
	}
	
	/**
	 * 设置是否开启缓存
	 * @param force true:开启  false:不开启
	 */
	public void setCacheEnable(boolean isEnable) {
		mCacheEnable = isEnable;
	}
	
	/**
	 * 获取缓存过期天数
 	 * @return 天数
	 */
	public long getExpireDay() {
		return mExpireDay;
	}
	
	/**
	 * 设置缓存过期天数
	 * @param day
	 */
	public void setExpireDay(long day) {
		mExpireDay = day;
	}
	
	/**
	 * 反初始化自动上报器
	 */
	public void uninitAutoPoster() {
		if (mContext != null) {
			try {
				mContext.unregisterReceiver(mReceiver);
				mHandler.removeCallbacks(mPostRunnable);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化自动上报器
	 */
	public void initAutoPoster() {
		if (mContext != null) {
			try {
				mFilter = new IntentFilter();
				mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
				
				mContext.registerReceiver(mReceiver, mFilter);
				
				mActivityFilter = new IntentFilter();
				mActivityFilter.addAction(TIMER_ACTION);  
								
				mContext.registerReceiver(mActivityReceiver, mActivityFilter);
				
				mActivityIntent = new Intent();
				mActivityIntent.setAction(TIMER_ACTION);
				mActivitySender = PendingIntent.getBroadcast(mContext, 0, mActivityIntent, 0);
				
				mAlarm = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
				long delay  = getAutoPosterTriggerTime();
				long repeat = getAutoPosterRepeatTime();
				if(DEBUG_AUTO_POSTER) {
					act("INIT  ="+ System.currentTimeMillis() + " DELAY ="+ delay + " REPEAT : " + repeat);
				}
				mAlarm.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + delay, repeat, mActivitySender);

			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void act(String tag) {
		InfocCommonBase.getInstance().dump(tag+"\n", new File("/sdcard/act.log"),true);
	}
	
	/**
	 * @since 5.3 周期报活，添加一个随机偏移，避免流量峰值
	 * @return
	 */
	public long getAutoPosterTriggerTime() {
		//return Commons.random(0, 20) * 1000L;
		return 20 * 1000L;
	}
	
	public long getAutoPosterRepeatTime() {
		return InfocCommonBase.getInstance().random(3*60*60, 5*60*60) * 1000L;
	}
	
	public int getDelayTime() {
		return mDelayTime + InfocCommonBase.getInstance().random(100) * 100;
	}
	
	/**
	 * 设置自动上报网络状态变化时执行延迟时间
 	 * @param delayTimedelayTime
	 */
	public void setAutoPostDelayTime(int delayTime) {
		mDelayTime = delayTime;
	}
}
