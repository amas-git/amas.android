/**
 * @brief     Package com.ijinshan.browser.model.impl.manager
 * @author    zuoyi
 * @since     1.0.0.0
 * @version   1.0.0.0
 * @date      2013-01-23
 */

package com.cmcm.onews.util.push.http;

/** 
 * @file      KNetUtils.java
 * @brief     utils about net function
 *
 * @author    zuoyi
 * @since     1.0.0.0
 * @version   1.0.0.0
 * @date      2013-01-23
 *
 * \if TOSPLATFORM_CONFIDENTIAL_PROPRIETARY
 * ============================================================================\n
 *\n
 *           Copyright (c) 2012 zuoyi.  All Rights Reserved.\n
 *\n
 * ============================================================================\n
 *\n
 *                              Update History\n
 *\n
 * Author (Name[WorkID]) | Modification | Tracked Id | Description\n
 * --------------------- | ------------ | ---------- | ------------------------\n
 * zuoyi[]   |  2013-01-23  | 1.0.0.0 | Initial Created.\n
 *\n
 * \endif
 *
 * <tt>
 *\n
 * Release History:\n
 *\n
 * Author (Name[WorkID]) | ModifyDate | Version | Description \n
 * --------------------- | ---------- | ------- | -----------------------------\n
 * zuoyi[]   | 2013-01-23 | 1.0.0.0 | Initial created. \n
 *\n
 * </tt>
 */
//=============================================================================
//IMPORT PACKAGES
//=============================================================================

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;


import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//=============================================================================
//CLASS DEFINITIONS
//=============================================================================

/**
 * @class KNetUtils
 * @brief utils about net function\n
 * @author zuoyi
 * @since 1.0.0.0
 * @version 1.0.0.0
 * @date 2013-01-23
 * @par Applied: External
 */
public class KNetUtils {

    private static final String TAG = "KNetUtils";
    public static final String NETWORK_NONE = "none";
    public static final String NETWORK_WIFI = "wifi";
    public static final String NETWORK_MOBILE = "mobile";
    public static final String NETWORK_SSID = "ssid";
    /**
     * @brief upload file to the server
     * @param name
     * @param filename
     * @param post_url
     * @param post_file
     * @return true if success
     */
    public static boolean postFile(String name, String filename,
            String post_url, File post_file) {
        boolean result = false;

        FileInputStream fi = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            final int buff_len = 1024;
            byte buff[] = new byte[buff_len];

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "***kbrowser**";

            URL url = new URL(post_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322;");

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + name
                    + "\";" + " filename=\"" + filename + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.flush();

            fi = new FileInputStream(post_file);
            while (true) {
                int count = fi.read(buff, 0, buff_len);
                if (count <= 0) {
                    break;
                }

                dos.write(buff, 0, count);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            dos.flush();

            dis = new DataInputStream(conn.getInputStream());
            String s = dis.readLine();
            if (s != null) {
                s = s.trim();
                if (s.equals("0")) {
                    result = true;
                }
            }
        } catch (Exception e) {
        } finally {
            closeQuietly(dis);
            closeQuietly(fi);
            closeQuietly(dos);
        }

        return result;
    }

    // 是否是wifi网络
    public static boolean isWifiNetworkAvailable(Context context) {
        if (context == null)
            return false;

        ConnectivityManager conmgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conmgr == null) {
            return false;
        }

        NetworkInfo activeNetInfo = null;
        try {
            activeNetInfo = conmgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        } catch (SecurityException e) {
            // maybe have no permission :
            // android.permission.ACCESS_NETWORK_STATE
            activeNetInfo = null;
        } catch (Exception e) {
            // maybe NullPointerException :
            //at android.os.Parcel.readException(Parcel.java:1431)
            //at android.os.Parcel.readException(Parcel.java:1379)
            //at android.net.IConnectivityManager$Stub$Proxy.getNetworkInfo(IConnectivityManager.java:734)
            //at android.net.ConnectivityManager.getNetworkInfo(ConnectivityManager.java:478)
            activeNetInfo = null;
        }

        if (activeNetInfo == null) {
            return false;
        }
        State wifi = activeNetInfo.getState();

        if (wifi == State.CONNECTED)
            return true;
        return false;
    }

    // 判断网络是否存在
    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                try {
                    if (cm.getActiveNetworkInfo() != null) {
                        if (cm.getActiveNetworkInfo().isAvailable()) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    return false;
                }

            }
            return false;
        }
        return false;
    }

    // 获取当前网络连接类型
    public static int getNetworkState(Context context)
    {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        // 平板可能没有移动通讯模块
        // infoMobile == null device:
        // LENOVO IdeaTab S6000-F
        // asus Nexus 7
        // samsung GT-N8010
        if (null == manager)
            return -1;

        NetworkInfo infoMobile = null;
        try {
            infoMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        } catch (Exception e) {
            // maybe have no permission :
            // android.permission.ACCESS_NETWORK_STATE
            infoMobile = null;
        }
        if (infoMobile != null) {
            State mobile = infoMobile.getState();
            if (mobile == State.CONNECTED || mobile == State.CONNECTING)
                return ConnectivityManager.TYPE_MOBILE;
        }
        NetworkInfo infoWifi = null;
        try {
            infoWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        } catch (SecurityException e) {
            // maybe have no permission :
            // android.permission.ACCESS_NETWORK_STATE
            infoWifi = null;
        }
        if (infoWifi != null) {
            State wifi = infoWifi.getState();
            if (wifi == State.CONNECTED || wifi == State.CONNECTING)
                return ConnectivityManager.TYPE_WIFI;
        }
        return -1;
    }
    public static String getNetworkName(Context context){
        String netWorkName = null;
        int networkState = getNetworkState(context);
        switch (networkState) {
            case ConnectivityManager.TYPE_MOBILE:
                netWorkName = NETWORK_MOBILE;
                break;
            case ConnectivityManager.TYPE_WIFI:
                netWorkName = NETWORK_WIFI;
                break;
            default:
                netWorkName = NETWORK_NONE;
                break;
        }
        return netWorkName;
    }

    // 获取当前连接wifi的SSID，如果没有连接返回为链接字符串，获取失败则返回null
    public static String getCurrentSSID(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null && networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }
    
    public static boolean isMobileNetwork(Context context) {
        return getNetworkState(context) == ConnectivityManager.TYPE_MOBILE;
    }
    public static void closeQuietly(Closeable c) {
        if (c == null)
            return;

        try {
            c.close();
        } catch (Exception e) {
        }
    }
}
