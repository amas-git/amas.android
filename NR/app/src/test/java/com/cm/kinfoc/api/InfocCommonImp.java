package com.cm.kinfoc.api;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.cm.kinfoc.KInfocBatchManager;
import com.cm.kinfoc.KInfocUtil;
import com.cm.kinfoc.base.InfocCommonBase;
import com.cm.util.KcmutilSoLoader;
import com.cm.util.LibLoadUtils;
import com.cm.util.Md5Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class InfocCommonImp extends InfocCommonBase {
	Application context = null;
	SharedPreferences sp = null;
	
	public InfocCommonImp(Context context) {
		this.context = (Application)context.getApplicationContext();
		this.sp = context.getSharedPreferences(".cmtrace", Context.MODE_PRIVATE);
		MMM.log("-> LiteGameInfocCommon INIT");
	}

	
	/**
	 * 封装对Context.getFilesDir()的调用
	 */
	private static File getFilesDir(Context ctx) {
		if (null == ctx) {
			return null;
		}
		
		File result = null;
		for (int i = 0; i < 3; ++i) {
			// 因为有时候getFilesDir()在无法创建目录时会返回失败，所以我们在此等待并于半秒内尝试三次。
			result = ctx.getFilesDir();
			if (null != result) {
				break;
			} else {
				try {
					Thread.sleep(166);
				} catch (InterruptedException e) {
				}
			}
		}
		MMM.log(" * getFilesDir="+result);
		return result;
	}
	
	@Override
	public boolean isServiceProcess() {
		return true;
	}

	@Override
	public Application getApplication() {
		return context;
	}

	@Override
	public long getLastBatchReportTime() {
		return sp.getLong(":LT", System.currentTimeMillis());
	}

	@Override
	public void setLastBatchReportTime(long time) {
		sp.edit().putLong(":LT", time).commit();
	}

	@Override
	public File getFilesDir() {
		return getFilesDir(context);
	}

	@Override
	public String getFileMD5(File file) {
		return Md5Util.getFileMD5(file);
	}

	@Override
	public String getStreamMD5(InputStream in) {
		return Md5Util.getStreamMD5(in);
	}

	@Override
	public boolean getSSLException() {
		return false;
	}

	@Override
	public void setSSLException(boolean bException) {

	}

	@Override
	public boolean doLoad(boolean u) {
		boolean success = KcmutilSoLoader.doLoad(context, u);
		MMM.log("DO LOAD : " + success);
		return success;
	}
	
	@Override
	public String getLibName() {
		return KcmutilSoLoader.LIB_NAME;
	}

	@Override
	public String getOwnCopySoPath() {
		LibLoadUtils utils = new LibLoadUtils(context, KcmutilSoLoader.LIB_NAME);
		String ownCopy = utils.GetOwnCopySoPath();
		return ownCopy;
	}

	@Override
	public String getSystemCopySoPath() {
		LibLoadUtils utils = new LibLoadUtils(context, KcmutilSoLoader.LIB_NAME);
		String sysCopy = utils.GetSystemCopySoPath();
		return sysCopy;
	}

	@Override
	public boolean isAllowedReportInfo() {
		return true;
	}

	@Override
	public long getFirstInstallTime() {
		return 0;
	}

	@Override
	public void setInfocPublicData(String secName, String infocPublicData) {
		MMM.log("-> setInfocPublicData = " + infocPublicData);
		sp.edit().putString(":public."+secName, infocPublicData).commit();
	}

	@Override
	public String getInfocPublicData(String secName) {
		String s = sp.getString(":public."+secName, "");
		MMM.log("-> getInfocPublicData = " + s);
		return s;
	}

	@Override
	public int getVersionCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getVersionCodeOld() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setVersionCode(int versionCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getLastReportActiveTime(String actName) {
		return sp.getLong(actName, System.currentTimeMillis());
	}

	@Override
	public void setLastReportActiveTime(String actName, long time) {
		sp.edit().putLong(actName, time);
	}

	@Override
	public void ipcRequestBatchReport() {

	}

	@Override
	public boolean checkInfocFile() {
		return KInfocUtil.checkInfocFile(context);
	}

	@Override
	public void setSoFailedCrashReported(boolean bReported) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getSoFailedCrashReported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDebug() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double random() {
		return _random();
	}

	@Override
	public int getCampaignTrackingTimeSeconds() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean beInstalledInSystem(Context context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getCompeteProductMask(Context ctx) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDataVersionInt() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private static Random mRandObj = new Random(System.currentTimeMillis());

	public static double _random() {
		synchronized (mRandObj) {
			return mRandObj.nextDouble();
		}
	}
	@Override
	public String getCMIDString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isGPAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasShortcut(String shortcutLabel, String shortcutIntent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getApplicationName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getChannelId2String() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getChannelIdString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean dump(String text, File file, boolean bFlag) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int random(int max) {
		return 0;
	}

	@Override
	public int random(int min, int max) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isMobileRoot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getAndroidID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String brand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String model() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSDKLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getProductId() {
		return 90;
	}

	@Override
	public String SERIAL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentLauncherName(boolean bFlag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSupportedLauncher(String launcherName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String acquireMainActivityClassPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getActType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isTodayFirstReport() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getDiagonalInch() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWindowWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWindowHeight() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public String getKFMTContent() {
		if(Meta.KFMT == null) {
            Meta.KFMT = getAssetsContent(context, "kfmt.dat");
            MMM.log(Meta.KFMT);
        }

        return Meta.KFMT;
	}
	
	public String getKCTRLContent() {
        if(Meta.KCTRL == null) {
            Meta.KCTRL = getAssetsContent(context, "kctrl.dat");
            MMM.log(Meta.KCTRL);
        }
		return Meta.KCTRL;
	}


	@Override
	public void hello() {
		KInfocBatchManager.createDir();
	}

	public static String getAssetsContent(Context context, String fname) {
		StringBuilder sb = new StringBuilder();
		InputStream is = null;
		try {
			is = context.getAssets().open(fname);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			String line = null;
			do {
				line = reader.readLine();
				if (line != null) {
					sb.append(line).append("\r\n");
				}
			} while (line != null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
}
