package com.cm.kinfoc.base;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.io.InputStream;

/**
 * @author amas
 *
 */
public abstract class InfocCommonBase {
	
	private static  InfocCommonBase mInstance = null;
	
	public static InfocCommonBase getInstance(){
		if(mInstance == null){
			throw new RuntimeException("Virtual Commmon Base is null");
		}
		return mInstance;
	}
	
	public static void setInstance(InfocCommonBase instance){
		mInstance = instance;
	}
	
	/**
	 * 使用返回内容填充kfmt.dat
	 * @return
	 */
	public abstract String getKFMTContent();
	
	
	/**
	 * 使用返回内容填充kctrl.dat
	 * @return
	 */
	public abstract String getKCTRLContent();
	public abstract boolean isServiceProcess();
	
	public abstract Application getApplication();
	
//	public abstract boolean isAllowBatchReport();
	
	public abstract long getLastBatchReportTime();
	
	public abstract void setLastBatchReportTime(long time);
	
	public abstract File getFilesDir();
	
	public abstract String getFileMD5(File file);
	public abstract String getStreamMD5(InputStream in);
	
	public abstract boolean getSSLException();
	public abstract void setSSLException(boolean bException);
	

	//KCMlib
	public abstract boolean doLoad(boolean u);
	public abstract String getLibName();
	public abstract String getOwnCopySoPath();
	public abstract String getSystemCopySoPath();
	
	
	//sevice code
	public abstract boolean isAllowedReportInfo();
	public abstract long    getFirstInstallTime();
	public abstract void 	setInfocPublicData(String secName, String infocPublicData);
	public abstract String getInfocPublicData(String secName);
	public abstract int  	getVersionCode();
	public abstract int		getVersionCodeOld();
	public abstract void 	setVersionCode(int versionCode);
	public abstract long 	getLastReportActiveTime(String actName);
	public abstract void 	setLastReportActiveTime(String actName, long time);
	public abstract void    ipcRequestBatchReport();
	public abstract boolean checkInfocFile();
	public abstract void 	setSoFailedCrashReported(boolean bReported);
	public abstract boolean getSoFailedCrashReported();
	
	

	//commons
	public abstract boolean isDebug();
	public abstract double 	random();
	public abstract int     getCampaignTrackingTimeSeconds();
	public abstract boolean beInstalledInSystem(Context context);
	public abstract int 	getCompeteProductMask(Context ctx);
	public abstract int 	getDataVersionInt();
	public abstract String getCMIDString();
	public abstract boolean isGPAvailable();
	public abstract boolean hasShortcut(String shortcutLabel, String shortcutIntent);
	public abstract String getApplicationName();
	public abstract String getChannelId2String();
	public abstract String getChannelIdString();
	public abstract boolean dump(final String text, File file, boolean bFlag);
	public abstract int random(int max);
	public abstract int random(int min, int max);
	
	
	
	//mobile phone related properties
	public abstract boolean isMobileRoot();
	public abstract String getAndroidID();
	public abstract String brand();
	public abstract String model();
	public abstract int     getSDKLevel();
	public abstract int  getProductId();
	public abstract String SERIAL();
	
	
	//luanch util
	public abstract String getCurrentLauncherName(boolean bFlag);
	public abstract boolean isSupportedLauncher(String launcherName);
	public abstract String acquireMainActivityClassPath();
	public abstract int     getActType();
	
	//判断是不是今天第一次上报活跃
	public abstract boolean isTodayFirstReport();
	
	//界面适配
	public abstract double getDiagonalInch();
	public abstract int getWindowWidth();
	public abstract int getWindowHeight();

	// infoc ready to work !!!
	public abstract void hello();
}


