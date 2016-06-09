package com.cmcm.onews.util.push.comm;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.cm.CH;
import com.cmcm.onews.C;
import com.cmcm.onews.util.PackageUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Locale;
import java.util.regex.Pattern;

public class AppEnvUtils {
    private static final String LOGTAG = "AppEnvUtils";


    private static String sAndroidID;
    private static String sIMEI;
    private static String DEFAULT_UA;

    /**
     * 获取原始 MAC 地址，以 ":" 分隔
     * @param context
     * @return
     */
    public static String getRawMacAddress(Context context) {
        String macAddress = null;
        try {
            WifiManager wifiMgr = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiMgr ? null : wifiMgr
                    .getConnectionInfo());
            if (null != info) {
                macAddress = info.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (macAddress == null) {
            macAddress = "";
        }
        return macAddress;
    }

    public static boolean isMemoryOk(final Context cxt) {
        long a = getAvailabeMemoryM(cxt);
        return getAvailabeMemoryM(cxt) > 256;
    }

    private static long getAvailabeMemoryM(final Context cxt) {
        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) cxt
                .getSystemService(cxt.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        return mi.availMem / 1024 / 1024;
    }
    /**
     * 获取手机mac地址<br/>
     * 错误返回12个0
     */
    public static String getMacAddress(Context context) {
        // 获取mac地址：
        String macAddress = getRawMacAddress(context);
        return macAddress.replace(":", "");
    }

    public static String getLanguage() {
        return Locale.getDefault().getLanguage();// 语言
    }

    public static String getSerial() {
        String serial = "";
        try {
            serial = android.os.Build.SERIAL;
        } catch (Exception e) {
        }
        return serial;
    }

    public static boolean getRootAhth() {
        try {
            File su = new File("/system/bin/su");
            if (su.exists()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }



    /**
     * Gets total RAM return -1 if error
     */
    private static final String MEM_TOTAL = "MemTotal:";
    private static final String MEM_UNIT = " kB";


    private static long getTotalRAM() {
        RandomAccessFile reader = null;
        String load = null;
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
        }

        if (TextUtils.isEmpty(load))
            return -1;

        int off = load.indexOf(MEM_TOTAL);
        if (off == -1) {
            return -1;
        }

        off = off + MEM_TOTAL.length();

        int end = load.indexOf(MEM_UNIT);
        if (end == -1) {
            return -1;
        }

        String num = load.substring(off, end).trim();
        long size = Long.valueOf(num);
        return size;
    }

    /**
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     *
     * @return The number of cores, or 1 if failed to get result
     */
    private static int getNumCores() {
        // Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                // Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            // Default to return 1 core
            return 1;
        }
    }

    public static String GetAndroidID(Context cx) {
        if (sAndroidID != null)
            return sAndroidID;
        sAndroidID = Settings.System.getString(cx
                        .getContentResolver(),
                Settings.System.ANDROID_ID);
        return sAndroidID;
    }

    public static String getChannel() {
        return CH.getChannelId()+"";
    }


    public static String getVersionName() {
        return  PackageUtils.getAppVersionName(C.getAppContext());
    }
}
