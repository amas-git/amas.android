package com.cmcm.onews.util.push.mi;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.cmcm.onews.util.push.comm.AppEnvUtils;

import java.util.Locale;
import java.util.TimeZone;

public class MiPushDeviceParams {

    public static String getApkVersion(Context appCtx){
    	if(appCtx == null){
    		return null;
    	}

    	String ver = null;
		PackageManager pkgMgr = appCtx.getPackageManager();
		if(pkgMgr == null){
			return null;
		}
    	String pkgName = appCtx.getPackageName();
    	if(TextUtils.isEmpty(pkgName)){
    		return null;
    	}
    	PackageInfo pkgInfo = null;
    	try{
    		pkgInfo = pkgMgr.getPackageInfo(pkgName, 0);
    		if(pkgInfo != null){
    			ver = String.valueOf(pkgInfo.versionCode);
    		}
    	}catch(Exception e){
    		ver = null;
    	}

    	return ver;
    }

    public static void setApkVersion(Context appCtx, int versionCode){
    	if(appCtx == null || versionCode < 0){
    		return;
    	}
    	String ver = null;
    	ver = String.valueOf(versionCode);
		MiPushConfigManager cfgMgr = MiPushConfigManager.getInstanse(appCtx);
    	if(ver != null && cfgMgr != null){
    		cfgMgr.setStringValue(MiPushConfigManager.PUSH_TOPIC_APP_APKVERSION_NAME_, ver);
    	}
    }

    public static int getApkVersionNum(Context appCtx){
    	if(appCtx == null){
    		return 0;
    	}

    	int ver = 0;
		PackageManager pkgMgr = appCtx.getPackageManager();
		if(pkgMgr == null){
			return 0;
		}
    	String pkgName = appCtx.getPackageName();
    	if(TextUtils.isEmpty(pkgName)){
    		return 0;
    	}
    	PackageInfo pkgInfo = null;
    	try{
    		pkgInfo = pkgMgr.getPackageInfo(pkgName, 0);
    		if(pkgInfo != null){
    			ver = pkgInfo.versionCode;
    		}
    	}catch(Exception e){
    		ver = 0;
    	}

    	return ver;
    }

    public static void setApkVersionNum(Context appCtx, int versionCode){
    	if(appCtx == null || versionCode < 0){
    		return;
    	}
    	MiPushConfigManager cfgMgr = MiPushConfigManager.getInstanse(appCtx);
    	if(cfgMgr != null){
    		cfgMgr.setIntValue(MiPushConfigManager.PUSH_TOPIC_APP_APKVERSION_CODE_, versionCode);
    	}
    }

    public static String getChannelID(Context appCtx){
    	String channel = null;
    	if(appCtx == null){
    		return null;
    	}

        try {
            ApplicationInfo ai = appCtx.getPackageManager().getApplicationInfo(
            		appCtx.getPackageName(), PackageManager.GET_META_DATA);
            Object value = ai.metaData.get("CHANNEL");
            if (value != null) {
               return value.toString();
            }
        } catch (Exception e) {
        }

    	return channel;
    }

    public static void setChannelID(Context appCtx, String channel){
    	if(appCtx == null || TextUtils.isEmpty(channel)){
    		return;
    	}

    	MiPushConfigManager cfgMgr = MiPushConfigManager.getInstanse(appCtx);
    	if(cfgMgr != null){
    		cfgMgr.setStringValue(MiPushConfigManager.PUSH_TOPIC_CHANNEL_, channel);
    	}
    }

    public static String getCountryLanguage(Context appCtx){
    	String cl = null;
    	if(appCtx == null){
    		return null;
    	}
    	try{
            String country = appCtx.getResources().getConfiguration().locale.getCountry();
            String language = appCtx.getResources().getConfiguration().locale.getLanguage();
            if(!TextUtils.isEmpty(country) && !TextUtils.isEmpty(language)){
                cl = country+"_"+language;
                cl.replace(" ", "");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    	return cl;
    }

    public static void setCountryLanguage(Context appCtx){
    	if(appCtx == null){
    		return;
    	}
    	MiPushConfigManager cfgMgr = MiPushConfigManager.getInstanse(appCtx);
    	if(cfgMgr == null){
    		return;
    	}
    	String cl = null;
    	String country = appCtx.getResources().getConfiguration().locale.getCountry();
    	String language = appCtx.getResources().getConfiguration().locale.getLanguage();
    	if(!TextUtils.isEmpty(country) && !TextUtils.isEmpty(language)){
    		cl = country+"_"+language;
    		cl.replace(" ", "");
    		cfgMgr.setStringValue(MiPushConfigManager.PUSH_TOPIC_COUNTRY_LANGUAGE_, cl);
    	}
    }

    public static String getCountry(Context appCtx){
    	if(appCtx == null){
    		return null;
    	}
    	MiPushConfigManager cfgMgr = MiPushConfigManager.getInstanse(appCtx);
    	if(cfgMgr == null){
    		return null;
    	}
    	String country = appCtx.getResources().getConfiguration().locale.getCountry();
    	return country;
    }

    public static void setCountry(Context appCtx){
    	if(appCtx == null){
    		return;
    	}
    	MiPushConfigManager cfgMgr = MiPushConfigManager.getInstanse(appCtx);
    	if(cfgMgr == null){
    		return;
    	}

    	String country = appCtx.getResources().getConfiguration().locale.getCountry();
    	if(!TextUtils.isEmpty(country)){
        	cfgMgr.setStringValue(MiPushConfigManager.PUSH_TOPIC_COUNTRY_, country);
    	}
    }

    public static String getLanguage(Context appCtx){
    	if(appCtx == null){
    		return null;
    	}
    	MiPushConfigManager cfgMgr = MiPushConfigManager.getInstanse(appCtx);
    	if(cfgMgr == null){
    		return null;
    	}
        if(TextUtils.isEmpty(appCtx.getResources().getConfiguration().locale.getLanguage())){
            return null;
        }
    	String language = appCtx.getResources().getConfiguration().locale.getLanguage();
    	return language;
    }

    public static void setLanguage(Context appCtx){
    	if(appCtx == null){
    		return;
    	}
    	MiPushConfigManager cfgMgr = MiPushConfigManager.getInstanse(appCtx);
    	if(cfgMgr == null){
    		return;
    	}

    	String language = appCtx.getResources().getConfiguration().locale.getLanguage();
    	if(!TextUtils.isEmpty(language)){
        	cfgMgr.setStringValue(MiPushConfigManager.PUSH_TOPIC_LANGUAGE_, language);
    	}
    }

    public static String getMCC(Context appCtx){
		if (appCtx == null)
			return null;
		final TelephonyManager tm = (TelephonyManager)appCtx
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mcc_mnc = tm.getSimOperator();
		StringBuilder mcc = null;
		if (null != mcc_mnc && mcc_mnc.length() >= 3) {
			mcc = new StringBuilder();
			mcc.append(mcc_mnc, 0, 3);
			return mcc.toString();
		}
		return null;
    }

    public static void setMCC(Context appCtx){
    	if(appCtx == null){
    		return;
    	}
    	String mcc = getMCC(appCtx);
    	if(TextUtils.isEmpty(mcc)){
    		return;
    	}

    	MiPushConfigManager cfgMgr = MiPushConfigManager.getInstanse(appCtx);
    	if(cfgMgr == null){
    		return;
    	}

    	cfgMgr.setStringValue(MiPushConfigManager.PUSH_TOPIC_MCC_, mcc);
    }

    public static String getMNC(Context appCtx){
		if (appCtx == null)
			return null;
		final TelephonyManager tm = (TelephonyManager)appCtx
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mcc_mnc = tm.getSimOperator();
		StringBuilder mnc = null;
		if (null != mcc_mnc && mcc_mnc.length() >= 5) {
			mnc = new StringBuilder();
			mnc.append(mcc_mnc, 3, 5);
			return mnc.toString();
		}
		return null;
    }

    public static void setMNC(Context appCtx){
    	if(appCtx == null){
    		return;
    	}
    	String mnc = getMNC(appCtx);
    	if(TextUtils.isEmpty(mnc)){
    		return;
    	}

    	MiPushConfigManager cfgMgr = MiPushConfigManager.getInstanse(appCtx);
    	if(cfgMgr == null){
    		return;
    	}

    	cfgMgr.setStringValue(MiPushConfigManager.PUSH_TOPIC_MNC_, mnc);
    }

    public static String getManufacture(){
    	String manufacture_ = android.os.Build.MANUFACTURER;
    	return manufacture_;
    }

    public static void setManufacture(Context appCtx){
    	MiPushConfigManager cfgMgr = MiPushConfigManager.getInstanse(appCtx);
    	if(cfgMgr == null){
    		return;
    	}

    	String manufacture_ = android.os.Build.MANUFACTURER;
    	if(!TextUtils.isEmpty(manufacture_)){
    		cfgMgr.setStringValue(MiPushConfigManager.PUSH_TOPIC_MANUFACTURE_, manufacture_);
    	}
    }

	public static String getAndroidID(Context appCtx) {
		try {
			if(appCtx == null){
				return "";
			}
			ContentResolver cr = appCtx.getContentResolver();
			return Settings.System.getString(cr, Settings.System.ANDROID_ID);
		} catch (Exception e) {
			return "";
		}
	}
    
	//推送的，注册成功上报
	public static String getReportPushRegIDParam(Context appCtx){
		if(appCtx == null){
			return null;
		}
		StringBuilder paramBuild = new StringBuilder("appflag=instanews_cn");

		String param = "" ;

		String mcc = getMCC(appCtx);
		if(!TextUtils.isEmpty(mcc)){
			paramBuild.append("&mcc=");
			paramBuild.append(mcc);
		}
		
		String mnc = getMNC(appCtx);
		if(!TextUtils.isEmpty(mnc)){
			paramBuild.append("&mnc=");
			paramBuild.append(mnc);
		}
		
		param =  AppEnvUtils.getVersionName();
		if(!TextUtils.isEmpty(param)){
			paramBuild.append("&apkversion=");
			paramBuild.append(param.replace(" ", ""));
		}
		
		param = android.os.Build.VERSION.RELEASE;
		if(!TextUtils.isEmpty(param)){
			paramBuild.append("&sdkversion=");
			paramBuild.append(param.replace(" ", ""));
		}
		
		param = android.os.Build.MANUFACTURER;
		if(!TextUtils.isEmpty(param)){
			paramBuild.append("&manufacture=");
			paramBuild.append(param.replace(" ", ""));
		}
		
		param = AppEnvUtils.getChannel();
		if(!TextUtils.isEmpty(param)){
			paramBuild.append("&channel=");
			paramBuild.append(param.replace(" ", ""));
		}

		//是否允许第三方程序安装
		long nNonMarketInstFlag = GetNonMarketAppsAllowedMark(appCtx);
		if(nNonMarketInstFlag >= 0){
			paramBuild.append("&trdmarket=");
			paramBuild.append(Long.toString(nNonMarketInstFlag));
		}
		
		paramBuild.append("&cl=");
		String tempData = Locale.getDefault().getCountry();
		String tempData2 = Locale.getDefault().getLanguage();
		if (!TextUtils.isEmpty(tempData) && !TextUtils.isEmpty(tempData2)) {
			tempData = tempData + "_" + tempData2;
			paramBuild.append(tempData);
		}
		if(TextUtils.isEmpty(tempData)){
			tempData = getCountryLanguage(appCtx);
			paramBuild.append(tempData);
		}
		
		
		paramBuild.append("&aid=" + getAndroidID(appCtx));
//		paramBuild.append("&did=" + UserHelper.readUserId(appCtx));
		paramBuild.append("&timezone=" + TimeZone.getDefault().getID());


		String country = getCountry(appCtx);
		if(!TextUtils.isEmpty(country)){
			paramBuild.append("&country=" + country);
		}
		
		int enabled = 1;
		paramBuild.append("&enabled=");
		paramBuild.append(Integer.toString(enabled));
		
		return paramBuild.toString();
	}
	
	//推送的，消息下达后的action回报
	public static String getReportPushActionParam(Context appCtx){
		if(appCtx == null){
			return null;
		}
		StringBuilder paramBuild = new StringBuilder();
		paramBuild.append("aid=" + getAndroidID(appCtx));
		String version = getApkVersion(appCtx);
		if(!TextUtils.isEmpty(version)){
			paramBuild.append("&apkversion=");
			paramBuild.append(version.replace(" ", ""));
		}
		return paramBuild.toString();
	}
    
    /**
     * 转换十进制的版本号为字符串型
     * @author houlin
     * @date 2014.07.01
     * */
	private static String translateDecimal(int version)
	{
		return String.format(Locale.US, "%d.%d.%d.%d",
				version / 10000000,
				version / 100000 % 100,
				version / 10000 % 10,
				version % 10000
		);
	}
	
	//allow installation of apps from unknow sources will return 1, otherwise return 0.
	public static int GetNonMarketAppsAllowedMark(Context appCtx) {
		int nFlags = -1;
        if(appCtx == null){
        	return nFlags;
        }
        
		try{
			if ( Settings.Secure.getInt(appCtx.getContentResolver(),
					Settings.Secure.INSTALL_NON_MARKET_APPS, 0) > 0 ){
				nFlags = 1;
			}
			else{
				nFlags = 0;
			}
		}
		catch (Exception e) {
		
		}
		return nFlags;
	}
}
