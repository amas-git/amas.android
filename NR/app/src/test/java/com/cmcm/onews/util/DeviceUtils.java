package com.cmcm.onews.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class DeviceUtils {
	private static final String MODEL = Build.MODEL.toLowerCase();
	private static final String MANUFACTURER = Build.MANUFACTURER.toLowerCase();

	public static boolean isGTP1000() {
		return MODEL.equalsIgnoreCase("gt-p1000");
	}

	public static boolean isZTEU985() {
		return MANUFACTURER.equals("zte") && MODEL.contains("zte u985");
	}

    public static boolean isGtS5830i() {
        return MODEL.equalsIgnoreCase("gt-s5830i");
    }
    
    /**
     * 魅族手机smartbar相关判断
     */
    private static boolean m_bDetected = false;
    private static boolean m_bHasSmartBar = false;
    public static boolean hasSmartBar(){
        if(m_bDetected)//已检测过了，直接返回保存的检测结果
        {
            return m_bHasSmartBar;
        }
        else //未检测过，先判断手机厂商是否为魅族，如果是,判断是否有smartbar，否则直接返回false。
        {
            if (Build.MANUFACTURER.equals("Meizu") && Build.VERSION.SDK_INT >= 14)
            {
                try {
                    // 新型号可用反射调用Build.hasSmartBar()
                    Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
                    m_bHasSmartBar =  ((Boolean) method.invoke(null)).booleanValue();
                    m_bDetected = true;
                    return m_bHasSmartBar;
                } catch (Exception e) {
                }

                // 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
                if (!Build.DEVICE.equals("mx") && Build.DEVICE.contains("mx")) {
                    m_bHasSmartBar = true;
                    m_bDetected = true;
                    return m_bHasSmartBar;
                } else //if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9"))
                {
                    m_bHasSmartBar = false;
                    m_bDetected = true;
                    return m_bHasSmartBar;
                }
            }
            else
            {
                m_bHasSmartBar = false;
                m_bDetected = true;
                return m_bHasSmartBar;
            }
        }
    }

	private static int mCpuNum = 0;

	public static int getCpuNum() {

		if (mCpuNum > 0) {
			return mCpuNum;
		}

		File cpuDev = new File("/sys/devices/system/cpu");
		if (!cpuDev.exists() || !cpuDev.isDirectory()) {
			mCpuNum = 1;
			return mCpuNum;
		}

		String[] cpuInfo = cpuDev.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {

				if (TextUtils.isEmpty(filename)) {
					return false;
				}

				return (Pattern.matches("cpu\\d+", filename));
			}
		});

		if (null == cpuInfo || 0 == cpuInfo.length) {
			mCpuNum = 1;
			return mCpuNum;
		}

		mCpuNum = cpuInfo.length;
		return mCpuNum;
	}


    /**
     * 获取手机制式，如gsm, cdma
     * @param context
     * @param defaultType
     * @return
     */
    public static String getPhoneType(Context context, String defaultType) {
        String netWorkType = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                switch (tm.getPhoneType()) {
                    case TelephonyManager.PHONE_TYPE_GSM:
                        netWorkType = "gsm";
                        break;
                    case TelephonyManager.PHONE_TYPE_CDMA:
                        netWorkType = "cdma";
                        break;
                    case TelephonyManager.PHONE_TYPE_SIP:
                        netWorkType = "sip";
                        break;
                    case TelephonyManager.PHONE_TYPE_NONE:
                        netWorkType = "none";
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e){
        }
        return TextUtils.isEmpty(netWorkType) ? defaultType : netWorkType;
    }

    public static boolean isUseForNewAvatarChange(){
        return  Build.VERSION.SDK_INT >= 20;
    }


    public static boolean isMiui() {

        String s = Build.DISPLAY;
        if (s != null)
        {
            if (s.toUpperCase().contains("MIUI"))
            {
                return true;
            }
        }

        s = Build.MODEL; // 小米
        if (s != null)
        {
            if (s.contains("MI-ONE"))
            {
                return true;
            }
        }

        s = Build.DEVICE;
        if (s != null)
        {
            if (s.contains("mione"))
            {
                return true;
            }
        }

        s = Build.MANUFACTURER;
        if (s != null)
        {
            if (s.equalsIgnoreCase("Xiaomi"))
            {
                return true;
            }
        }

        s = Build.PRODUCT;
        if (s != null)
        {
            if (s.contains("mione"))
            {
                return true;
            }
        }

        return false;
    }

	public static boolean isScreenOn(Context context) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		return pm.isScreenOn();
	}

	@SuppressLint("SdCardPath")
	public static final String DATA_DIR = "/data/data/";

	public static final String DEFAULT_MATCH_START_TIME = "00:00";
	public static final String MATCH_START_TIME_FORMAT = "HH:mm";

	public static final String DEFAULT_MATCH_START_DATA = "00/00";
	public static final String MATCH_START_DATA_FORMAT = "MM/dd";

	public static File getDataDir(Context context) {

		File filesDir = context.getFilesDir();

		File dataDir = null;

		if (filesDir != null) {
			dataDir = filesDir.getParentFile();
		} else {
			dataDir = new File(DATA_DIR + context.getPackageName() + File.separator);
		}

		return dataDir;
	}

    private static String cacheResolution = null;
	/**
	 * 获取手机分辨率  (height x width)
	 * @param context
	 * @return
	 */
	public static String getResolution(Context context) {
            if(TextUtils.isEmpty(cacheResolution)){
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                cacheResolution = String.format(Locale.US, "%d*%d", dm.heightPixels, dm.widthPixels);
            }
        return cacheResolution ;
    }

    /**
     * mi 2判断
     */
    private static boolean m_bDetectedMi2 = false;
    private static boolean m_bMi2 = false;
    public static boolean isMI2() {
        if(m_bDetectedMi2){
            return m_bMi2;
        }else {
            m_bMi2 = MANUFACTURER.equals("xiaomi") && MODEL.equalsIgnoreCase("mi 2");
            m_bDetectedMi2 = true;
            return m_bMi2;
        }
    }

    /**
     * 获取 AndroidId
     * @param pContext Context
     * @return
     */
    public static String getAndroidId(Context pContext) {
        String androidID = "";
        try {
            try {
                androidID = Settings.Secure.getString(pContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidID;
    }

    public static String getMCC(Context pContext) {
        String mcc = "";
        String networkOperator = ((TelephonyManager) pContext.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            if (networkOperator.length() >= 5) {
                mcc =networkOperator.substring(0, 3);
            }
        }
        return mcc;
    }

    public static String getSimMNC(Context pContext) {
        String _MNC = "";
        String _networkOperator = ((TelephonyManager) pContext.getSystemService(Context.TELEPHONY_SERVICE)).getSimOperator();
        if (!TextUtils.isEmpty(_networkOperator)) {
            if (_networkOperator.length() >= 5) {
                _MNC = _networkOperator.substring(3);
            }
        }
        return _MNC;
    }

    /**
     * 获取系统版本号 E.g., "4.2.2"
     * @return
     */
    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 手机品牌(Brand)
     * @return
     */
    public static String getBrand() {
        return  Build.BRAND;
    }

    /**
     * 手机型号(MobileModel)
     * @return
     */
    public static String getMobileModel() {
        return Build.MODEL;
    }

    /**
     * 获得设备当前的语言_国家或地区代码。
     * @param pContext Context对象
     * @return 语言_国家或地区代码
     */
    public static String getLocale(Context pContext) {
        try {
            return pContext.getResources().getConfiguration().locale.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获得设备的時區
     */
    public static String getTimeZone() {
        return TimeZone.getDefault().getID();
    }

    /**
     * 获得设备的國家-語言 組合
     */

    public static String getCL() {
        // cl.
        String tempData = Locale.getDefault().getCountry();
        String tempData2 = Locale.getDefault().getLanguage();
        String cl_ = "";
        if (!TextUtils.isEmpty(tempData) && !TextUtils.isEmpty(tempData2)) {
            cl_ = tempData + "_" + tempData2;
        }
        return cl_;
    }


    /**
     * 获取当前应用的版本号
     */
    public String getVersionName(Context pContext) throws Exception
    {
        String version = "";
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = pContext.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(pContext.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (Exception e) {e.printStackTrace();}
        return version;
    }

    /**
     * 获取当前应用的versionCode
     */
    public static int getVersionCode(Context pContext) throws Exception
    {
        int version = 0;
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = pContext.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(pContext.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (Exception e) {e.printStackTrace();}
        return version;
    }

    public static String getVersionCodeS(Context context){
        try {
            return translateDecimal(getVersionCode(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //将形如30110001(3-01-1-0001)版本号格式化为3.1.1.1
    public static String translateDecimal(int version)
    {
        return String.format(Locale.US, "%d.%d.%d.%d",
                version / 10000000,
                version / 100000 % 100,
                version / 10000 % 10,
                version % 10000
        );
    }

    /**
     * 获取MNC
     * @param pContext Context
     * @return MNC
     */
    public static String getMNC(Context pContext) {
        String _MNC = "";
        String _networkOperator = ((TelephonyManager) pContext.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperator();
        if (!TextUtils.isEmpty(_networkOperator)) {
            if (_networkOperator.length() >= 5) {
                _MNC = _networkOperator.substring(3);
            }
        }
        return _MNC;
    }

    /**
     * 获取SimMCC
     * @param pContext Context
     * @return SimMCC
     */
    public static String getSimMCC(Context pContext) {
        String _MCC = "";
        String _networkOperator = ((TelephonyManager) pContext.getSystemService(Context.TELEPHONY_SERVICE)).getSimOperator();
        if (!TextUtils.isEmpty(_networkOperator)) {
            if (_networkOperator.length() >= 5) {
                _MCC =_networkOperator.substring(0, 3);
            }
        }
        return _MCC;
    }
    /**
     * 获取状态栏高度
     * @return
     */
    public static int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }


}
