package com.cm.kinfoc;

import android.content.Context;
import android.util.Log;

import com.cm.kinfoc.api.MMM;
import com.cm.kinfoc.base.InfocCommonBase;

import java.io.File;
import java.io.IOException;

/**
 * 读取kctrl.dat文件类
 * @author singun 
 */

public class KInfoControl {
	
	/******* 这里的数值会存在一个遍历，要确认 emKInfoPriority_End 为最大值 **************/
	public final static int emKInfoPriority_Unknow = -1;
	public final static int emKInfoPriority_High = 0;
	public final static int emKInfoPriority_Normal = 0;
	public final static int emKInfoPriority_Basic = 1;
	public final static int emKInfoPriority_End = 2; ///<最大值
	
	//调试结果到这个页面查看 http://119.147.146.243/debug62.html 
	private final String defServerUrl = "https://helpnewsindia1.ksmobile.com/c/";
	private final String fileName = "kctrl.dat";
	
	private KIniReader reader;
	boolean bInited = false;
	
	public KInfoControl(Context context) {
		try {
			reader = new KIniReader(InfocCommonBase.getInstance().getFilesDir().getAbsolutePath()
					+ File.separatorChar + fileName);
			bInited = true;
		} catch (IOException e) {
			bInited = false;
			e.printStackTrace();
		}
	}
	
	public int getProductID() {
		if (bInited == true) {
			return reader.getPrivateProfileInt("common", "product", 90);
		} else {
			return 0;
		}
	}
	
	public int getValidityDays(){
		if (bInited == true) {
			return reader.getPrivateProfileInt("common", "validity", 7);
		} else {
			return 0;
		}
	}
	
	public int getProbability(String szTableName) {
	    int nProbability = 10000;	    
		if (bInited == true) {
		    nProbability = reader.getPrivateProfileInt(szTableName, "probability", 10000);

		    return nProbability;
		} else {
			return nProbability;
		}
	}
	
	public int getUserProbability(String szTableName) {
	    int nProbability = 10000;
		if (bInited == true) {
		    nProbability = reader.getPrivateProfileInt(szTableName, "userprobability", 10000);

		    return nProbability;
		} else {
			return nProbability;
		}
	}
	
	public int getPriority(String szTableName) {
		if (bInited == true) {
			return reader.getPrivateProfileInt(szTableName, "priority", emKInfoPriority_Basic);
		} else {
			return emKInfoPriority_Basic;
		}
	}
	
	public int getUniqueFlag(String szTableName) {
		if (bInited == true) {
			return reader.getPrivateProfileInt(szTableName, "unique", 0);
		} else {
			return 0;
		}
	}
	
	public boolean isV5(int nPriority){
		return true;
	}
	
	public String getHttpsServerUrl(int nPriority) {
		
		if(nPriority != KInfoControl.emKInfoPriority_Normal){
			return getServerUrl(nPriority);
		}
		String sHttpUrl = null;
		if (bInited == true) {
			String strKeyName;
			strKeyName = "serverhttps";
			sHttpUrl = reader.getPrivateProfileString("common", strKeyName, defServerUrl);
            MMM.log("getHttpsServerUrl/HTTPS=" + sHttpUrl + " @" + strKeyName);
		} else {
			if(KInfocUtil.debugLog) Log.d(KInfocUtil.LOG_TAG, "bInited == true url=" + sHttpUrl);
			sHttpUrl = defServerUrl;
		}
		if(android.os.Build.VERSION.SDK_INT < 10){
			if(sHttpUrl != null){
				sHttpUrl = sHttpUrl.replaceFirst("https", "http");
			}
		}
		if(KInfocUtil.debugLog) Log.d(KInfocUtil.LOG_TAG, "url=" + sHttpUrl);

		if(KInfocUtil.serverbugLog){
			Log.w(KInfocUtil.LOG_TAG, "serverbugLog!! " + sHttpUrl + "==>" + defServerUrl);
			return defServerUrl;
		}
		return sHttpUrl;
	}
	
	public String getServerUrl(int nPriority) {
		if (bInited == true) {
			String strKeyName;
			strKeyName = "server" + nPriority;

			if(KInfocUtil.serverbugLog){
				Log.w(KInfocUtil.LOG_TAG, "serverbugLog!! " + strKeyName + "==>" + defServerUrl);
				return defServerUrl;
			}
			return reader.getPrivateProfileString("common", strKeyName, defServerUrl);
		} else {
			return defServerUrl;
		}
	}
	
	/*public String getServerUrl(String szTableName) {
		int nPriority = getPriority(szTableName);
		return getServerUrl(nPriority);
	}*/
}
