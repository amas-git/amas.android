package com.cm.kinfoc;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.cm.kinfoc.base.InfocLog;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Infoc公共数据获取类 static
 * @author singun 
 * 
 */
public class KInfocCommon {
	public static final int 	UUID_LENGTH 	= 32;
	public static final int 	IMSI_LENGTH 	= 20;
	public static final String CHANNEL_FILE 	= "channel";
	public static final String CHANNEL_EXT		= ".txt";
	public static final String CHANNEL_BASE 	= "channelbase";
	public static final String CHANNEL_NORMAL 	= "0";
	public static final String CHANNEL_MYAPP	= "90000038";
	
    public static final int   NET_OFF       = 0;    //无网络
    public static final int   NET_UNKNOWN   = 1;    //未知网络
    public static final int   NET_WIFI      = 2;    //WIFI网络
    public static final int   NET_2G        = 4;    //2G网络
    public static final int   NET_3G        = 8;    //3G网络
    public static final int   NET_4G        = 16;   //4G网络
    public static final int   NET_EXCEPTION = 32;   //获取网络状态失败
    public static final int   NET_DEFAULT   = NET_WIFI + NET_3G + NET_4G;
    
	//网络类型
    public static final int NETWORK_TYPE_IDEN	= 11; // Level 8
    public static final int NETWORK_TYPE_EVDO_B	= 12; // Level 9
    public static final int NETWORK_TYPE_LTE	= 13; // Level 11
    public static final int NETWORK_TYPE_EHRPD	= 14; // Level 11
    public static final int NETWORK_TYPE_HSPAP	= 15; // Level 13
    
    public static final int CONNECTION_TIMEOUT = 60 * 1000;
	
	/**
	 * 获取版本代码
	 * 
	 * @param context
	 * @param cls
	 * @return 版本代码
	 */
	public static String getVersionCode(Context context, Class<?> cls) {
		if (context == null || cls == null)
			return null;
		ComponentName cn = new ComponentName(context, cls);
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					cn.getPackageName(), 0);
			return Integer.toString(info.versionCode);
		} catch (/*NameNotFoundException*/Exception e) {
			return null;
		}
	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @param cls
	 * @return 版本号
	 */
	public static String getVersionName(Context context, Class<?> cls) {
		if (context == null || cls == null)
			return null;
		ComponentName cn = new ComponentName(context, cls);
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					cn.getPackageName(), 0);
			return info.versionName + "(" + info.versionCode + ")";
		} catch (/*NameNotFoundException*/Exception e) {
			return null;
		}
	}
	
	/**
	 * 取IMEI
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		if (context == null)
			return null;
		final TelephonyManager tm = (TelephonyManager)context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}
	
	/**
	 * 根据IMEI获取UUID
	 * 
	 * @param context
	 * @return UUID
	 */
	public static String getUUID(Context context) {
		if (context == null){
			InfocLog.getLogInstance().log(" imei, the context is null ");
			return null;
		}
			
		String phoneIMEI = null;
		try {
			phoneIMEI = getIMEI(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		InfocLog.getLogInstance().log("the imei=" + phoneIMEI);
		int imeiLength = 0;
		if (phoneIMEI != null)
			imeiLength = phoneIMEI.length();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < UUID_LENGTH - imeiLength; i++) {
			builder.append('0');
		}
		if (phoneIMEI != null)
			builder.append(phoneIMEI);
        return builder.toString();
	}
	
	/**
	 * 获取IMSI
	 * 
	 * @param context
	 * @return IMSI
	 */
	public static String getIMSI(Context context) {
		if (context == null)
			return null;		
		final TelephonyManager tm = (TelephonyManager)context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String phoneIMSI = tm.getSubscriberId();
		int imsiLength = 0;
		if (phoneIMSI != null)
			imsiLength = phoneIMSI.length();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < IMSI_LENGTH - imsiLength; i++) {
			builder.append('0');
		}
		if (phoneIMSI != null)
			builder.append(phoneIMSI);
        return builder.toString();
	}
	
	public static int getMCC(Context context) {
		int _mcc = 0;
		if (context == null)
			return 0;
		final TelephonyManager tm = (TelephonyManager)context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mcc_mnc = tm.getSimOperator();
		StringBuilder mcc = null;
		if (null != mcc_mnc && mcc_mnc.length() >= 3) {
			mcc = new StringBuilder();
			mcc.append(mcc_mnc, 0, 3);
			try {
				_mcc = Short.parseShort(mcc.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return _mcc;
			
		}
		return _mcc;
	}
	
	public static int getMNC(Context context) {
		int _mnc = 0;
		if (context == null)
			return 0;
		final TelephonyManager tm = (TelephonyManager)context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mcc_mnc = tm.getSimOperator();
		StringBuilder mnc = null;
		if (null != mcc_mnc && mcc_mnc.length() >= 5) {
			mnc = new StringBuilder();
			mnc.append(mcc_mnc, 3, 5);
			try {
				_mnc = Short.parseShort(mnc.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return _mnc;
		}
		return 0;
	}
	
	/**
	 * 获取渠道号
	 * 
	 * @param context
	 * @return ChannelKey
	 */
	public static String getChannel(Context context) {
		if (context == null)
			return CHANNEL_NORMAL;
		
		String channelKey = null;
		
		String channelFileName = CHANNEL_FILE + CHANNEL_EXT;
		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		byte[] buff = new byte[0xff];;
		int rd = 0;

		try {
			fis = context.openFileInput(channelFileName);
			baos = new ByteArrayOutputStream();
			while ((rd = fis.read(buff)) != -1) {
				baos.write(buff, 0, rd);
			}
			fis.close();
			channelKey = new String(baos.toByteArray()).trim();
			baos.close();
			if (channelKey.length() == 0)
				channelKey = CHANNEL_NORMAL;
			else
				return channelKey;
		} catch (Exception e) {

		} finally {
			try {
				if (fis != null)
					fis.close();
				if (baos != null)
					baos.close();
			} catch (IOException ex) {
				
			}
		}

		Resources res = context.getResources();
		String packageName = context.getPackageName();
		int id = 0;
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			id = res.getIdentifier(CHANNEL_FILE, "raw",
					packageName);
			if (id > 0) {
				is = res.openRawResource(id);
				baos = new ByteArrayOutputStream();
				fos = context.openFileOutput(channelFileName, Context.MODE_PRIVATE);
				while ((rd = is.read(buff)) != -1) {
					baos.write(buff, 0, rd);
					fos.write(buff, 0, rd);
				}
				is.close();
				baos.close();
				fos.close();
				channelKey = new String(baos.toByteArray()).trim();
				if (channelKey.length() == 0)
					channelKey = CHANNEL_NORMAL;
				else
					return channelKey;
			}
		} catch (Exception e){

		} finally {
			try {
				if (is != null)
					is.close();
				if (baos != null)
					baos.close();
				if (fos != null)
					fos.close();
			}
			catch (IOException ex) {

			}
		}
		
		try {
			id = res.getIdentifier(CHANNEL_BASE, "string",
					packageName);

			if (id > 0) {
				channelKey = context.getString(id);
			} else {
				channelKey = CHANNEL_NORMAL;
			}
		} catch (NotFoundException e){
			
		}
		
		return channelKey;
	}
	
	/**
	 * 获取安装包渠道号
	 * 
	 * @param context
	 * @return ChannelKey
	 */
	public static String getPackageChannel(Context context) {
		if (context == null)
			return CHANNEL_NORMAL;
		
		String channelKey = null;
		String channelFileName = CHANNEL_FILE + CHANNEL_EXT;
		ByteArrayOutputStream baos = null;
		byte[] buff = new byte[0xff];;
		int rd = 0;

		Resources res = context.getResources();
		String packageName = context.getPackageName();
		int id = 0;
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			id = res.getIdentifier(CHANNEL_FILE, "raw",
					packageName);
			if (id > 0) {
				is = res.openRawResource(id);
				baos = new ByteArrayOutputStream();
				fos = context.openFileOutput(channelFileName, Context.MODE_PRIVATE);
				while ((rd = is.read(buff)) != -1) {
					baos.write(buff, 0, rd);
					fos.write(buff, 0, rd);
				}
				is.close();
				baos.close();
				fos.close();
				channelKey = new String(baos.toByteArray()).trim();
				if (channelKey.length() == 0)
					channelKey = CHANNEL_NORMAL;
				else
					return channelKey;
			}
		} catch (Exception e){

		} finally {
			try {
				if (is != null)
					is.close();
				if (baos != null)
					baos.close();
				if (fos != null)
					fos.close();
			}
			catch (IOException ex) {

			}
		}
		
		return CHANNEL_NORMAL;
	}
	
	/**
	 * 获取网络类型
	 * 
	 * @param context
	 * @return 网络类型
	 */
	public static int getNetworkType(Context context) {
		if (context == null)
			return NET_UNKNOWN;

		int nReturn = NET_OFF;

		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				int type = info.getType();
				int subType = info.getSubtype();
				if (type == ConnectivityManager.TYPE_WIFI) {
					if (isWiFiActive(context)) {
						nReturn = NET_WIFI;
					}
				} else if (type == ConnectivityManager.TYPE_MOBILE) {
					switch (subType) {
					case TelephonyManager.NETWORK_TYPE_CDMA: // = 4 ~ 14-64 kbps
					case TelephonyManager.NETWORK_TYPE_IDEN: // = 11 ~ 25 kbps
					case TelephonyManager.NETWORK_TYPE_1xRTT: // = 7 2.5G或者
																// 2.75G ~
																// 50-100 kbps
					case TelephonyManager.NETWORK_TYPE_GPRS: // = 1 2.5G ~ 171.2
																// kbps
					case TelephonyManager.NETWORK_TYPE_EDGE: // = 2 2.75G ~
																// 384-473.6
																// kbps
						nReturn = NET_2G;
						break;
					case TelephonyManager.NETWORK_TYPE_EVDO_0: // = 5 ~ 400-1000
																// kbps
					case TelephonyManager.NETWORK_TYPE_UMTS: // = 3 ~ 400-7000
																// kbps
					case TelephonyManager.NETWORK_TYPE_EVDO_A: // = 6 ~ 600-1400
																// kbps
					case TelephonyManager.NETWORK_TYPE_HSPA: // = 10 3G ~
																// 700-1700 kbps
					case TelephonyManager.NETWORK_TYPE_EHRPD: // = 14 3.75g ~
																// 1-2 Mbps
					case TelephonyManager.NETWORK_TYPE_HSUPA: // = 9 ~ 1-23 Mbps
					case TelephonyManager.NETWORK_TYPE_HSDPA: // = 8 ~ 2-14 Mbps
					case TelephonyManager.NETWORK_TYPE_EVDO_B: // = 12 ~ 9 Mbps
					case TelephonyManager.NETWORK_TYPE_HSPAP: // = 15 ~ 10-20
																// Mbps
						nReturn = NET_3G;
						break;
					case TelephonyManager.NETWORK_TYPE_LTE: // = 13 4G ~ 10+
															// Mbps
						nReturn = NET_4G;
						break;
					case TelephonyManager.NETWORK_TYPE_UNKNOWN:// = 0
					default:
						nReturn = NET_UNKNOWN;
						break;
					}
				} else {
					nReturn = NET_UNKNOWN;
				}
			}
		} catch (NullPointerException e) {
			/**
			 * java.lang.NullPointerException
				at android.os.Parcel.readException(Parcel.java:1431)
				at android.os.Parcel.readException(Parcel.java:1379)
				at android.net.IConnectivityManager$Stub$Proxy.getActiveNetworkInfo(IConnectivityManager.java:688)
				at android.net.ConnectivityManager.getActiveNetworkInfo(ConnectivityManager.java:460)
				at com.cleanmaster.kinfoc.ai.j(KInfocCommon.java:430)
				at com.cleanmaster.util.bt.b(DumpUploader.java:48)
			 */
			nReturn = NET_EXCEPTION;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nReturn;
	}
	
    public static int netType2Int(int type) {
        switch (type) {
            case NET_OFF:
                return 0;
            case NET_UNKNOWN:
                return 1;
            case NET_WIFI:
                return 2;
            case NET_2G:
                return 3;
            case NET_3G:
                return 4;
            case NET_4G:
                return 5;
            case NET_EXCEPTION:
                return 6;
        }
        return -1;
    }
	
	/**
	 * 判断Wifi是否可用
	 * @return true:可用 false:不可用
	 */
	public static boolean isWiFiActive(Context context) {
		if (context == null)
			return false;
		boolean bReturn = false;
		WifiManager mWifiManager = (WifiManager)context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = null;
		try{
			wifiInfo = mWifiManager.getConnectionInfo();
		}catch(Exception e){
		}
		int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
		if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
			bReturn = true;
		}
		return bReturn;
	}

	/**
	 * 判断网络是否可用
	 * @return true:可用 false:不可用
	 */
	public static boolean isNetworkActive(Context context) {
		if (context == null)
			return false;
		boolean bReturn = false;
		ConnectivityManager conManager = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conManager.getActiveNetworkInfo();
		if(netInfo != null && netInfo.isConnected()) {
            bReturn = true;
        }
		return bReturn;
	}
}
