package com.cm.kinfoc;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.cm.kinfoc.api.MMM;
import com.cm.kinfoc.base.InfocCommonBase;
import com.cm.kinfoc.base.InfocLog;
import com.cm.newsrepublic_public;
import com.cm.util.ArrayMap;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


/**
 * Infoc客户端
 * @author singun 
 * 
 * 使用前请确保Infoc配置文件完整，可用KInfocUtil释放apk中的配置文件
 * init函数必须手动执行，不会自动调用
 * 为了在最大程度上减少模块初始化时对主程序的影响，JNI库通过独立线程加载
 * 调用reportData之前请确保init中的线程已执行结束，可以用isInited判断
 * 
 * 需要权限：
 * INTERNET
 * ACCESS_NETWORK_STATE
 * ACCESS_WIFI_STATE
 * READ_PHONE_STATE
 * 部分系统可能需要权限：
 * BROADCAST_STICKY
 */
public class KInfocClient {
	public static final int		MAX_PROBABILITY = 10000;	// 上报率最大控制值
	public static final int 	ROOT_FALSE	= 0;	//手机无root
	public static final int 	ROOT_TRUE	= 1;	//手机已root
	public static final int 	ROOT_DUBA	= 2;	//手机毒霸已获取root
    public static final int 	USER_APP 	= 0;  	//用户应用
    public static final int 	SYSTEM_APP 	= 1;	//系统应用
    public static final int 	UPDATE_APP 	= 2;	//更新过的系统应用
    
    public static final long    REPORT_ACTIVE_TIME_PERIOD = 24L * 60L * 60L * 1000L;  // 报活跃时间间隔期(单位ms)
	
	private static final int 	INIT_MSG 	= 256;	//msg id
	
	private Context mContext 	= null;
	private String mFilePath 	= null;		//infoc配置文件路径
	private String mDataPublic = null;		//公共数据字符串
	private int 	mProductId 	= 0;		//产品ID
	private int 	mRootState 	= 0;		//手机ROOT状态
	private int 	mAppType 	= 0;		//当前应用类型
	private boolean	mToggle 	= true;		//上报开关(总开关，包括强制上报的数据也受控)
	
	private KInfocReporter 	mDataRepoter 	= null;
	private KInfoControl 	mControl 		= null;
	
	private static KInfocClient mClient = null;
	private static boolean 		mInited = false;
	private static boolean mAllowedReportInfo = true;	// 上报开关(强制上报的数据不受此开关控制)
	
	private int    mnSaveCacheFileCount = 0;  // 已经写入Cache的文件数量
	private Object mSyncCacheFileCountObj = new Object();
	
	private Map<String, Boolean> mUserReportableMap = new ArrayMap<String, Boolean>();
	private Object mUserReportableMutex = new Object();
    private static String msModel = null;
    private static long			mNextReadImeiTime = 0L;

	/**
	 * @param context 上下文
	 */
	private KInfocClient(Context context) {
		mContext = context;
		if (mContext != null) {
			ResetControler();
		}
	}
	
	
	private synchronized void ResetControler(){
		try {
			mFilePath		= InfocCommonBase.getInstance().getFilesDir().getAbsolutePath();
			MMM.log(" * FILE PATH : " + mFilePath);
			mDataPublic		= getPublicData(mContext);
			MMM.log(" * HEADER    : " + mDataPublic);
			mControl		= new KInfoControl(mContext);
			MMM.log(" * CTRL      : " + mControl);
			mDataRepoter	= new KInfocReporter(mContext, mControl);
			MMM.log(" * REPORTER  : " + mDataRepoter);
			mProductId		= mControl.getProductID();
			MMM.log(" * PID       : " + mProductId);
			int expireData  = mControl.getValidityDays();
			mDataRepoter.setExpireDay(expireData);
			MMM.log(" * EXPIRED   : " + expireData);
			KInfocBatchManager.getInstance().setReportData(mControl, mDataPublic, mProductId, expireData, mFilePath);
			MMM.log(" * BATCH     : " + "OK");
			if (null == mDataPublic) {
				// 取不到公共数据，就自动关闭上报。(通常发生在飞行模式下首次运行)
				setInfocToggle(false);
			}
		} catch (Exception e) {
			MMM.log("============== INFOC CLIENT INIT ERRO ==================");
			MMM.log(e.getMessage());
			
			mFilePath = null;
			KInfocBatchManager.getInstance().setReportData(null, null, -1, 0, null);
			mDataPublic = null;
			mControl = null;
			mDataRepoter = null;
			mProductId = 0;
			setInfocToggle(false);
		}
	}
	
	/**
	 * 获取单实例
	 * @return KInfocClient实例
	 */
	
	private static Object s_Lock_new_instance = new Object();
	public static KInfocClient getInstance() {
		synchronized (s_Lock_new_instance) {
			if (mClient == null) {
				mClient = new KInfocClient(InfocCommonBase.getInstance().getApplication());
			}

			if(mClient.mDataPublic != null && (msModel == null)){
				mClient.mDataPublic		= KInfocClient.getPublicData(mClient.mContext);
				InfocLog.getLogInstance().log(" read imei in getinstance, again ");
			}

            if(isPublicHeaderInvalidate) {
                mClient.mDataPublic		= KInfocClient.getPublicData(mClient.mContext);
                MMM.log(" HEADER UPDATED: " + mClient.mDataPublic);
                isPublicHeaderInvalidate = true;
            }
			return mClient;
		}
	}


    static volatile boolean isPublicHeaderInvalidate = false;

    /**
     * Header有更新调用此函数
     */
    public void setPublicHeaderInvalidate() {
        isPublicHeaderInvalidate = true;
    }
	/**
	 * 异步初始化流程
	 */
	public static void init(boolean rptFailInfo){
		if (mInited)
			return;
		
		new LoadModuleThread(rptFailInfo).start();
	}
	
	/**
	 * 同步初始化流程
	 * return 成功返回true，失败返回false。
	 */
	public static synchronized boolean init_Sync(){
		boolean ok = KInfocClient.isInited();
		MMM.log("init_Sync: " + ok);
		if (ok) {
			return ok;
		}
		
		new LoadModuleThread(false).run();
		
		int loopCount = 5;
		do {
			ok = KInfocClient.isInited();
			if (ok) {
				KInfocClient.getInstance();
				break;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				loopCount = 0;
			}
		} while ((loopCount--) > 0);
		
		return ok;
	}
	
	
	private static class LoadModuleThread extends Thread {
		private boolean mRptFailInfo = false;
		private static Object s_lock_load_thread = new Object();
		public LoadModuleThread(boolean rptFailInfo) {
			mRptFailInfo = rptFailInfo;
		}
		
		@Override
		public void run() {
			synchronized (s_lock_load_thread) {
				if (KInfocClient.isInited()) {
					return;
				}
                if(MMM.DEBUG) MMM.log("LoadModuleThread START..." );
				// 判断是否允许上报
				mAllowedReportInfo = true;

				if ((!InfocCommonBase.getInstance().isAllowedReportInfo())) {
					mAllowedReportInfo = false;
				}

				// 确保infoc配置文件正确可用
				boolean rec = KInfocUtil.checkInfocFile(InfocCommonBase.getInstance().getApplication());
				if(MMM.DEBUG) MMM.log("LOAD 1: " + rec + " mRptFailInfo="+mRptFailInfo );
				
				if (!rec) {
					KInfocClient.setInited(false);
					return;
				}

				rec = InfocCommonBase.getInstance().doLoad(mRptFailInfo);
				if(MMM.DEBUG) MMM.log("LOAD 2: " + rec);
				if (rec) {
					KInfocClient.setInited(true);
				} else {
					KInfocClient.setInited(false);
				}
			}
			
			// 初始化成功，就建立AutoPoster
			if (KInfocClient.isInited() && InfocCommonBase.getInstance().isServiceProcess()) {  // 在服务里面，需要进行自动的上传相关处理
				KInfocClient.getInstance().initAutoPoster();
				InfocCommonBase.getInstance().hello();
			}
			
		}
	}
	
	/**
	 * 反初始化
	 */
	public static boolean unInit() {
		if (InfocCommonBase.getInstance().isServiceProcess()) {
			KInfocClient.getInstance().uninitAutoPoster();
		}
		return true;
	}
	
	/**
	 * 判断加载库是否已经初始化
	 */
	public static boolean isInited() {
		return mInited;
	}
	
	/**
	 * 设置加载库已初始化状态
	 */
	private static void setInited(boolean inited) {
		mInited = inited;
	}
	
	/**
	 * 设置Infoc开关
	 * @param 开关	true 上报 false 不上报
	 */
	public void setInfocToggle(boolean toggle) {
		mToggle = toggle;
		if (mDataRepoter != null) {
			mDataRepoter.setInfocToggle(toggle);
		}
	}
	
	private static String getValidData( String data, String sectionName) {
		
		if((sectionName != null && "uuid".equalsIgnoreCase(sectionName)) && (data == null || "00000000000000000000000000000000".equals(data))){
			data = InfocCommonBase.getInstance().getInfocPublicData(sectionName);
			if(data == null || data.length() == 0){
				data = "00000000000000000000000000000000";
			}
			return data;
		}

		if (null == data || data.equals("")) {
			if ( null == sectionName) {
				return null;
			}
			data = InfocCommonBase.getInstance().getInfocPublicData(sectionName);
			if (null == data || data.equals("")) {
				return null;
			}
			
			return data;
		}
		
		if ( null != sectionName && !sectionName.equals("")) {
			InfocCommonBase.getInstance().setInfocPublicData(sectionName, data);
		}
		
		return data;
	}
	
	/**
	 * 获取公共数据   
	 * @param context
	 * @return 公共数据字符串
	 */
	public static String getPublicData(Context context) {
		String header = newsrepublic_public.getInstance(context).toInfocString();
		return header;
		/*
//		ServiceConfigManager cm = ServiceConfigManager.getInstanse(context);
		String tempData = null;
		StringBuilder dataPublic = new StringBuilder("uuid=");
		tempData = getValidData(KInfocCommon.getUUID(context), "uuid");
		if (null == tempData) {
			tempData = "11111111111111111111111111111111";
		}else if("00000000000000000000000000000000".equals(tempData)){
			msImei = null;
		}else {
			msImei = tempData;
		}
		mNextReadImeiTime = System.currentTimeMillis();
		
		dataPublic.append(tempData);
		
		dataPublic.append("&ver=");
		tempData = Integer.toString(InfocCommonBase.getInstance().getVersionCode());
		if (null == tempData) {
			tempData = "0";
		}
		dataPublic.append(tempData);
		
		dataPublic.append("&mcc=");
		tempData = getValidData(KInfocCommon.getMCC(context), "mcc");
		if (null == tempData) {
			tempData = "-1";
		}
		dataPublic.append(tempData);
		
		dataPublic.append("&mnc=");
		tempData = getValidData(KInfocCommon.getMNC(context), "mnc");
		if (null == tempData) {
			tempData = "-1";
		}
		dataPublic.append(tempData);
		
		dataPublic.append("&cl=");
		tempData = Locale.getDefault().getCountry();
		String tempData2 = Locale.getDefault().getLanguage();
		if (null == tempData || tempData.equals("") || 
				null == tempData2 || tempData2.equals("")) {
			tempData = getValidData(null, "cl");
		} else {
			tempData = getValidData(tempData + "_" + tempData2, "cl");
		}
		if (null == tempData) {
			tempData = "NONE";
		}
		dataPublic.append(tempData);
		
		dataPublic.append("&cn=");
		tempData = getValidData(InfocCommonBase.getInstance().getChannelIdString(), "cn");
		if (null == tempData) {
			tempData = "-1";
		}
		dataPublic.append(tempData);

		dataPublic.append("&prodid=");
		dataPublic.append(InfocCommonBase.getInstance().getProductId());
		
		dataPublic.append("&xaid=");
		tempData = getValidData(InfocCommonBase.getInstance().getAndroidID(), "xaid");
		if (null == tempData) {
			tempData = "";
		}
		dataPublic.append(tempData);
		
		dataPublic.append("&root2="  +(InfocCommonBase.getInstance().isMobileRoot() ? 1 : 0));
		
		byte apilevel = (byte)InfocCommonBase.getInstance().getSDKLevel();
		dataPublic.append("&capi="   + apilevel);
		dataPublic.append("&brand2=" + InfocCommonBase.getInstance().brand());
		
		msModel = getValidData(InfocCommonBase.getInstance().model(), "model_x");
		dataPublic.append("&model2=" + msModel);
		dataPublic.append("&serial2="+ InfocCommonBase.getInstance().SERIAL());
		
		tempData = getValidData( InfocCommonBase.getInstance().getChannelId2String(), "cn2");
		if(tempData ==  null) {
			tempData = "";
		}
		dataPublic.append("&cn2="    + tempData);
		return dataPublic.toString(); */
	}
	
	/**
	 * 按照用户上报概，计算本用户是否需要上报。(最好不要直接调用本函数，从KInfocClientAssist.needReportData()间接调用会更安全。)
	 * @param tableName	表名
	 * @param reset		是否要重算概率
	 * @return 是否需要上报
	 */
	public boolean needReportData(String tableName, boolean reset) {
		if (TextUtils.isEmpty(tableName)) {
			return false;
		}
		
		if (null == mControl) {
			MMM.log("needReportData : control null");
			return false;
		}
		
		synchronized(mUserReportableMutex) {
			if (!reset) {
				Boolean report = mUserReportableMap.get(tableName);
				if (null != report) {
					return report.booleanValue();
				}
			}

			int usrProbability = mControl.getUserProbability(tableName);
			if (0 == usrProbability) {
				// 上报概率控制值为0，不上报。
				mUserReportableMap.put(tableName, false);
				MMM.log("P TEST 1 :" + usrProbability);
				return false;
			} else if (usrProbability < MAX_PROBABILITY && ((int)(InfocCommonBase.getInstance().random() * MAX_PROBABILITY) > usrProbability)) {
				// 按随机概率，此条不报。
				mUserReportableMap.put(tableName, false);
				MMM.log("P TEST 2 :" + usrProbability);
				return false;
			}

			mUserReportableMap.put(tableName, true);
		}
		
		return true;
	}
	
	/**
	 * 上报数据
	 * @param tableName 要上报的表名
 	 * @param dataString 字符串
	 * @return 是否完成上报
	 */
	public boolean reportData(String tableName, String dataString) {
		MMM.log(" 1 -> " + tableName);
		if (mContext == null || dataString == null)
			return false;
		
    	return reportData(tableName, dataString, false);
	}
	
	/**
	 *  强制上报数据
	 * @param tableName 要上报的表名
 	 * @param dataString 字符串
	 * @return 是否完成上报
	 */
	public boolean forceReportData(String tableName, String dataString) {
		if (mContext == null || dataString == null) {
			return false;
		}
		
		return reportData(tableName, dataString, true);
	}
	
	/**
	 *  强制上报数据
	 * @param tableName 要上报的表名
 	 * @param dataString 字符串
	 * @return 是否完成上报
	 */
	public boolean forceReportData(String tableName, String dataString, KHttpResultListener listener) {
		if (mContext == null || dataString == null) {
			return false;
		}
		return reportData(tableName, dataString, true, listener);
	}
	
	/**
	 * 格式化上报数据，数据字符串采用类似C/C++中的字符串格式化方法
	 * @param tableName 要上报的表名
 	 * @param format 格式化字符串
 	 * @param args 多个格式化字符串参数
	 * @return 是否完成上报
	 */
	public boolean formatReport(String tableName, String format, Object... args) {
		if (mContext == null || format == null)
			return false;
		
		return reportData(tableName, String.format(format, args), false);
	}
	
	/**
	 * 上报Service活跃信息，每个Service，每24小时只允许上报一次。不论是否上报，都会通过INRDCallback回调返回下次上报的时间。
	 */
	public boolean reportServiceActive(ServiceActiveData data) {
		if (null == data || null == data.mSrv) {
			return false;
		}
		
		boolean rst = true;
		long nextReportTime = 0L;
		try {
			nextReportTime = calcNextReportServiceActiveTime(data.mSrv);
		} catch (NullPointerException e) {
			e.printStackTrace();
			rst = false;
		}
		
		if (!rst) {
			return rst;
		}
		
		if (nextReportTime > 0L) {
			// 时间还没到
			if (null != data.mNRD) {
				data.mNRD.notifyNextReportTime(nextReportTime);
			}
			return true;
		}
		
		String tableName = getActiveTableName(data.mSrv);
		if (null == tableName) {
			return false;
		}
		
		boolean enableReport = false;
		try {
			enableReport = InfocCommonBase.getInstance().isAllowedReportInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean isSupportLauncher = false;
		{
			String currentLauncher = InfocCommonBase.getInstance().getCurrentLauncherName(false);
			if ("com.qihoo360.launcher".equals(currentLauncher)) {
				isSupportLauncher = false;
			}
			
			if (InfocCommonBase.getInstance().isSupportedLauncher(currentLauncher))
				isSupportLauncher = true;
		}
		
		boolean hadGooglePlay = InfocCommonBase.getInstance().isGPAvailable();
		boolean hasShortcut = false;
		if (isSupportLauncher)
			hasShortcut = InfocCommonBase.getInstance().hasShortcut(
					InfocCommonBase.getInstance().getApplicationName(), InfocCommonBase.getInstance().acquireMainActivityClassPath());
		
		int installFlag = getInstallFlag();
		
		long firstInstallTime = InfocCommonBase.getInstance().getFirstInstallTime();
		int usedHour = (int) ((System.currentTimeMillis() - firstInstallTime) / 1000 / 3600);
		
		int hassc = 2;
		if (isSupportLauncher) {
			if (hasShortcut)
				hassc = 1;
			else
				hassc = 0;
		}
		
		boolean isFirstReport = InfocCommonBase.getInstance().isTodayFirstReport();
		msModel = getValidData(InfocCommonBase.getInstance().model(), "model_x");
		rst = reportData(tableName, "s=" + (hadGooglePlay ? "1" : "0") + 
				"&i=" + String.valueOf(installFlag) +
				"&aid=" + InfocCommonBase.getInstance().getAndroidID() + 
				"&brand=" + InfocCommonBase.getInstance().brand() + 
				"&model=" + msModel + 
				"&api=" + InfocCommonBase.getInstance().getSDKLevel() + 
				"&er=" + (enableReport ? "1" : "0") + 
				"&ctt=" + InfocCommonBase.getInstance().getCampaignTrackingTimeSeconds() + 
				"&insys=" + (InfocCommonBase.getInstance().beInstalledInSystem(mContext) ? "1" : "0") + 
				"&cpm=" + Integer.toString(InfocCommonBase.getInstance().getCompeteProductMask(mContext)) +
				"&msver=" + Integer.toString(InfocCommonBase.getInstance().getDataVersionInt()) +
				"&cmid=" + InfocCommonBase.getInstance().getCMIDString() + 
				"&hassc=" + hassc +
				"&inhours=" + usedHour + 
				"&isupload=" + (isFirstReport ? 1:0),
				true);
		
		if (rst) {
			if (null != data.mNRD) {
				data.mNRD.notifyNextReportTime(REPORT_ACTIVE_TIME_PERIOD);
			}
		}
		
		return rst;
	}
	
	/**
	 * 在安装包安装后，首次调用时判断新装用户和覆盖安装用户。(注意，覆盖安装用户只有在首次调用时返回2，再次调用会返回0。)
	 * @return 新装用户返回1，覆盖安装用户返回2，其他情况返回0。
	 */
	private int getInstallFlag() {
		Application app = InfocCommonBase.getInstance().getApplication();
		if (null == app) {
			return 0;
		}

		PackageManager pm = app.getPackageManager();
		if (null == pm) {
			return 0;
		}
		
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(app.getPackageName(), 0);
		} catch (/*NameNotFoundException*/Exception e) {
			e.printStackTrace();
		}
		if (null == pi) {
			return 0;
		}

		
		int recordedVersionCode = InfocCommonBase.getInstance().getVersionCodeOld();
		if (0 == recordedVersionCode) {
			// 新装用户
			InfocCommonBase.getInstance().setVersionCode(pi.versionCode);
			return 1;
		}
		
		if (pi.versionCode == recordedVersionCode) {
			// 重复查询
			return 0;
		}
		
		// 覆盖安装用户
		InfocCommonBase.getInstance().setVersionCode(pi.versionCode);
		return recordedVersionCode;
	}
	
	/**
	 * 上报Activity活跃信息，每个Activity，每24小时上报一次。(此数据强制上报)
	 * @param actInfo 要报活跃的Activity信息
	 * @return 成功返回true
	 */
	public boolean reportActivityStart(ActivityReportStartInfo actInfo) {
		if (null == actInfo) {
			return false;
		}
		
		if (!needReportActive(actInfo.act, actInfo.tableName)) {
			return true;
		}
		
		String tableName = getActiveTableName(actInfo.act);
		if (null == tableName) {
			tableName = actInfo.tableName;
			if (null == tableName) {	
				return false;
			}
		}
		
		String dataExt = toInfocString(actInfo.b);
		
		String dataString;
		StringBuffer sb = new StringBuffer("");
		
		if (0 == tableName.compareTo("cm_act_1")) {
			
			sb .append("s=" + (InfocCommonBase.getInstance().isMobileRoot() ? "1" : "0"));
			if (!TextUtils.isEmpty(dataExt)) {
				sb.append("&" + dataExt);
			}
			//上报屏幕分辨率和英寸数
			String inchs = String.valueOf(InfocCommonBase.getInstance().getDiagonalInch());
			if(inchs.length()>20)
			{
				inchs = inchs.substring(0, 19);
			}
			
			int net_type = KInfocCommon.getNetworkType(this.mContext);
			if(net_type == 0)//为避免与原默认值混淆，无网络类型值改为64
			{
				net_type = 64;
			}
			sb.append("&resolution="+InfocCommonBase.getInstance().getWindowWidth()+"x"+InfocCommonBase.getInstance().getWindowHeight())
			.append("&network="+net_type)
			.append("&screensize="+inchs);
			
			return reportData(tableName, sb.toString(), true);
		}
		
		dataString = "s=" + (actInfo.s ? "1":"0");
		if (!TextUtils.isEmpty(dataExt)) {
			dataString += "&" + dataExt;
		}
		
		//Log.d("reportalive", tableName + ", " + dataString);
		
		return reportData(tableName, dataString, true);
		
	}
	
	protected String toInfocString(Bundle b) {
		if (b == null || b.isEmpty()) {
			return null;
		}
		Set<String> kies = b.keySet();
		if (kies == null || kies.isEmpty()) {
			return null;
		}
		ArrayList<String> chunk = new ArrayList<String>(kies.size());

		for (String key : kies) {
			chunk.add(key + "=" + String.valueOf(b.get(key)));
		}
		return TextUtils.join("&", chunk);
	}
	
	
	
	/**
	 * 设置root状态
	 * @param root状态
	 */
	public void setRootState(int rootState) {
		mRootState = rootState;
	}
	
	/**
	 * 设置app类型
	 * @param app类型
	 */
	public void setAppType(int appType) {
		mAppType = appType;
	}
	
	/**
	 * 上报数据
	 * @param tableName 要上报的表名
 	 * @param dataString 字符串
 	 * @param isForce 强制上传 true:在所有网络下都上传 false:只在wifi下上传
	 * @return 是否完成上报
	 */
	private boolean reportData(String tableName, String dataString, boolean isForce) {
		return reportData(tableName, dataString, isForce, null);
	}
	
	/**
	 * 上报数据
	 * @param tableName 要上报的表名
 	 * @param dataString 字符串
 	 * @param isForce 强制上传 true:在所有网络下都上传 false:只在wifi下上传
 	 * @param listener 通知http执行结果
	 * @return 是否完成上报
	 */
	private boolean reportData(String tableName, String dataString, boolean isForce, KHttpResultListener listener) {
		if (!mInited || !mToggle) {
			MMM.log(" 2 -> " + tableName + " mInited="+mInited+" ; mToggle="+mToggle);
			return false;
		}
		if (null == mFilePath || null == mDataPublic || null == mControl || null == mDataRepoter) {
			MMM.log(" 3 -> " + tableName);
			return false;
		}
		if ((!mAllowedReportInfo) && (!isForce)) {
			MMM.log(" 4 -> " + tableName);
			// 用户设定不允许上报开关时，只能上报强制上报的数据。
			return true;
		}
		if (!needReportData(tableName, false)) {
			MMM.log(" 5 -> " + tableName);
			return true;
		}
		int probability = mControl.getProbability(tableName);
		if (0 == probability) {
			MMM.log(" 6 -> " + tableName);
			// 上报概率控制值为0，不上报。
			return true;
		}
		else if (probability < MAX_PROBABILITY && 
				((int)(InfocCommonBase.getInstance().random() * MAX_PROBABILITY) > probability)) {
			// 按随机概率，此条不报。
			MMM.log(" 7 -> " + tableName);
			return true;
		}
		
		String uptime = Long.toString((System.currentTimeMillis() / 1000L));
		if (!dataString.contains("&uptime2=")) {
			dataString += "&uptime2=" + uptime;			
			
		}
		if (KInfocUtil.debugLog)
			Log.d(KInfocUtil.LOG_TAG, "tableName: " + tableName + " dataString: " + dataString);
		
		boolean result = true;
		byte[] byteArray = null;
//		MMM.log(" * mDataPublic = " + mDataPublic);
//		MMM.log(" * mProductId  = " + mProductId);
//		MMM.log(" * mFilePath   = " + mFilePath);


		byteArray = getData(tableName, dataString /*+ "&uptime=" + uptime*/, mDataPublic, mProductId, mFilePath);
		if (byteArray == null) {
			if (KInfocUtil.debugLog) {
				Log.d(KInfocUtil.LOG_TAG, "getData return null " + dataString);
			}
			//if (BuildConfig.DEBUG || new File("/sdcard/__test_infoc__").exists()) {
				//throw new NullPointerException("infoc data format error, see logcat for more details. table name: " + tableName + ": " + dataString);
            //}
			MMM.log(" 8 -> " + tableName);
			return false;
		}
		
		//强制上报的就直接获取要上报的二进制了，非强制的就先写二进制到本地,走批量上报逻辑. 2013.10.26
		if(isForce) {
			mDataRepoter.postData(byteArray, tableName, isForce, listener);
		}else{
			String kfmtPath = InfocCommonBase.getInstance().getFilesDir().getAbsolutePath();
			mDataRepoter.saveCacheNoHeader(mDataPublic, mProductId, kfmtPath, byteArray, tableName, isForce, KInfoControl.emKInfoPriority_Basic);
			mDataRepoter.saveCacheNoHeader(mDataPublic, mProductId, kfmtPath, byteArray, tableName, isForce, KInfoControl.emKInfoPriority_Normal);
			// 存储达到KInfocBatchManager.MIN_BATCH_REPORT_FILE_COUNT的个数，才进行一次通知
			boolean isNeedRequest = false;
			//目前理解为存在同时上报的情况，故对mnSaveCacheFileCount和isNeedRequest操作时加锁
			synchronized(mSyncCacheFileCountObj) {
				if (++mnSaveCacheFileCount >= KInfocBatchManager.MIN_BATCH_REPORT_FILE_COUNT) {
					mnSaveCacheFileCount = 0;
					isNeedRequest = true;
				}
			}
			
			if(isNeedRequest) {
				requestBatchReport();
			}
		}
 		
		return result;
	}
	
	/**
	 * 检测上报数据格式是否合法
	 * @param tableName 表名
	 * @param dataString 数据
	 * @return true合法 false不合法
	 */
	public boolean isValidateData(String tableName, String dataString) {
		if (!mInited || !mToggle)
			return false;
		
		if (null == mFilePath || null == mDataPublic || null == mControl || null == mDataRepoter) {
			return false;
		}
		
		//int probability = mControl.getProbability(tableName);
		
		String uptime = Long.toString((System.currentTimeMillis() / 1000L));
		dataString += "&uptime2=" + uptime;
		
		byte[] byteArray = getData(tableName, dataString /*+ "&uptime=" + uptime*/ , mDataPublic, mProductId, mFilePath);
		return (byteArray != null);
	}
	
	/**
	 * 设置是否开启缓存
	 * @param force true:开启  false:不开启
	 */
	public void setCacheEnable(boolean enable) {
		if (mDataRepoter != null) {
			mDataRepoter.setCacheEnable(enable);
		}
	}
	
	/**
	 * 判断缓存是否可用
 	 * @return true:可用 false:不可用
	 */
	public boolean isCacheEnable() {
		if (mDataRepoter != null) {
			return mDataRepoter.isCacheEnable();
		}
		return false;
	}
	
	/**
	 * 获取缓存过期天数
 	 * @return 天数
	 */
	public long getExpireDay() {
		if (mDataRepoter != null) {
			return mDataRepoter.getExpireDay();
		}
		return 0;
	}
	
	/**
	 * 设置缓存过期天数
	 * @param day
	 */
	public void setExpireDay(long day) {
		if (mDataRepoter != null) {
			mDataRepoter.setExpireDay(day);
		}
	}
	
	/**
	 * 删除所有缓存文件
 	 * @param isForce  指定清除强制上报缓存还是清除普通缓存
	 */
	public void cleanCache(boolean isForce) {
		if (mDataRepoter != null) {
			mDataRepoter.cleanCache(isForce);
		}
	}
	
	public void cleanCache(int kinfoPriority){
		if (mDataRepoter != null) {
			mDataRepoter.cleanCache(kinfoPriority);
		}
	}
	
	public void cleanV5Cache(){
		cleanCache(KInfoControl.emKInfoPriority_Normal);
	}
	
	/**
	 * 删除过期缓存文件
 	 * @param isForce  指定清除强制上报缓存还是清除普通缓存
	 */
	public void cleanExpireCache(boolean isForce) {
		if (mDataRepoter != null) {
			mDataRepoter.cleanExpireCache(isForce, false, KInfoControl.emKInfoPriority_Unknow);
			
			for(int nPriority = KInfoControl.emKInfoPriority_Unknow + 1; nPriority < KInfoControl.emKInfoPriority_End; ++nPriority) {
				mDataRepoter.cleanExpireCache(isForce, true, nPriority);
			}
		}
	}
	
	/**
	 * 反初始化自动上报器
	 */
	public void uninitAutoPoster() {
		if (mDataRepoter != null) {
			mDataRepoter.uninitAutoPoster();
		}
	}
	
	/**
	 * 初始化自动上报器
	 */
	public void initAutoPoster() {
		if (mDataRepoter != null) 
			if(MMM.DEBUG) MMM.log("INIT AUTOPOSTER");{
			mDataRepoter.initAutoPoster();
		}
	}
	
	/**
	 * 设置自动上报网络状态变化时执行延迟时间
 	 * @param delayTime
	 */
	public void setAutoPostDelayTime(int delayTime) {
		if (mDataRepoter != null) {
			mDataRepoter.setAutoPostDelayTime(delayTime);
		}
	}
	
	/**
	 * 上报缓存数据
	 * @param isForce
	 */
	public void reportCache() {
		if (mDataRepoter != null) {
			mDataRepoter.reportCache();
		}
	}
	
	public void requestBatchReport() {
		if (!mInited || !mToggle)
			return ;
		
		if (!mAllowedReportInfo) {
			// 用户设定不允许上报开关时，只能上报强制上报的数据。
			return ;
		}
		
		// 分进程内外，进行逻辑处理
		if (InfocCommonBase.getInstance().isServiceProcess()) { // 在服务内部，直接调用批量处理
			KInfocBatchManager.getInstance().requestReport();
		} else { // 在服务外部，通过IPC调用处理
			try {
//				SyncIpcCtrl.getIns().getIPCClient().requestBatchReport();
				InfocCommonBase.getInstance().ipcRequestBatchReport();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 设置是否允许上报开关
	 * @param allowedReportInfo
	 */
	public static void setAllowedReportFlag(boolean allowedReportInfo) {
		mAllowedReportInfo = allowedReportInfo;
	}
	
	/**
	 * 获取数据 - 调用C++层
	 * @param tableName 要上报的表名
	 * @param dataString 字符串
	 * @param dataPublic 公共字符串
	 * @param produceId 产品ID值
	 * @param filePath 配置文件（kfmt.dat/kctrl.dat）路径
	 * @return 上报字节流
	 */
	private byte[] getData(String tableName, String dataString,
			String dataPublic, int produceId, String filePath) {
		byte[] rst = null;
        MMM.log(" tableName  -> : " + tableName);
        MMM.log(" dataString -> : " + dataString);
        MMM.log(" dataPublic -> : " + dataPublic);
        MMM.log(" produceId  -> : " + produceId);
        MMM.log(" filePath   -> : " + filePath);
		try {
			rst = a.a(tableName, dataString, dataPublic, produceId, filePath);
		} catch (Exception e) {
			rst = null;
            MMM.log("EXCEPTION: " + e.getMessage());
		}
		
		return rst;
	}

	private boolean needReportActive(Activity act, String tableName) {
		if (null == act) {
			return (!TextUtils.isEmpty(tableName));
		}
		
		String canonicalName = act.getClass().getCanonicalName();
		if (null == canonicalName) {
			return false;
		}
//根据当前项目mainActivity判断
//com.keniu.security.main.MainActivity
		if (!canonicalName.equals("com.cm.infocdemos.MainActivity")) {
			// 只有主界面才限制24小时上报，其它都是有一次报一次。
			return true;
		}
		


		
		long lastTime = InfocCommonBase.getInstance().getLastReportActiveTime(canonicalName);
		long nowTime = System.currentTimeMillis();
		if (0 == lastTime || (nowTime - lastTime) >= REPORT_ACTIVE_TIME_PERIOD) {
			InfocCommonBase.getInstance().setLastReportActiveTime(canonicalName, nowTime);
			return true;
		}
		
		return false;
	}
	
	private String getActiveType(Object obj) {
		String canonicalName = obj.getClass().getCanonicalName();
		if (null == canonicalName) {
			return null;
		}
		
		Context ctx = InfocCommonBase.getInstance().getApplication();
		if (null == ctx) {
			return null;
		}
		
		Resources rsc = ctx.getResources();
		if (null == rsc) {
			return null;
		}
		
		XmlResourceParser xrp = null;
		try {
			xrp = rsc.getXml(InfocCommonBase.getInstance().getActType());
		} catch (Exception e) {
			
		}
		


		if (null == xrp) {
			return null;
		}
		
		String actType = null;
		try {
			int eventType = xrp.getEventType();
			boolean stop = false;
			while (!stop) {
				switch (eventType) {
				case XmlResourceParser.END_DOCUMENT:
					stop = true;
					break;
					
				case XmlResourceParser.START_TAG:
					String name = xrp.getName();
					if (null != name) {
						if (name.equals("activity") || name.equals("service")) {
							String actName = xrp.getAttributeValue(0);
							if (null != actName && actName.equals(canonicalName)) {
								eventType = xrp.next();
								actType = xrp.getText();
								stop = true;
								break;
							}
						}
					}
					eventType = xrp.next();
					break;
					
				default:
					eventType = xrp.next();
					break;
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			xrp.close();
			xrp = null;
		}
		
		if (null == actType) {
			actType = "0";
		}
		
		return actType;
	}
	
	private String getActiveTableName(Activity act) {
		if (null == act) {
			return null;
		}
		
		String actType = getActiveType(act);
		if (null == actType) {
			return null;
		}
		
		return "cm_act_" + actType;
	}
	
	private String getActiveTableName(Service srv) {
		String actType = getActiveType(srv);
		if (null == actType || actType.equals("0")) {
			return null;
		}
		
		return "cm_act_srv_" + actType;
	}
	
	private long calcNextReportServiceActiveTime(Service srv) throws NullPointerException {
		String canonicalName = srv.getClass().getCanonicalName();
		if (null == canonicalName) {
			throw new NullPointerException("null name.");
		}

		
		long lastTime = InfocCommonBase.getInstance().getLastReportActiveTime(canonicalName);
		long nextTime = lastTime + REPORT_ACTIVE_TIME_PERIOD;
		long nowTime = System.currentTimeMillis();
		nextTime -= nowTime;
		
		if (nextTime <= 0) {
			InfocCommonBase.getInstance().setLastReportActiveTime(canonicalName, nowTime);
		}
		
		return nextTime;
	}
}
