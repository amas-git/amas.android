
package com.cmcm.onews.util.push.comm;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

/**
 * 泡泡指令系统相关通用工具
 * 
 * @author Administrator
 */
public class MsgUtil {
//    public static final String DATEFORMAT5 = "yyyyMMddHHmmss";

    /**
     * @param address
     * @param params
     * @param proxy
     * @param timeOut 单位ms
     * @return
     */
    public static String httpGet(String address, String params, Proxy proxy,
            int timeOut) {

        try {
            URL url;
            if (TextUtils.isEmpty(params)) {
                url = new URL(address);
            } else {
                url = new URL(address + "?" + params);
            }
            HttpURLConnection httpConn;
            if (proxy == null) {
                httpConn = (HttpURLConnection) url.openConnection();
            } else {
                httpConn = (HttpURLConnection) url.openConnection(proxy);
            }

            httpConn.setDoOutput(false);// 使用 URL 连接进行输出
            httpConn.setDoInput(true);// 使用 URL 连接进行输入
            httpConn.setUseCaches(false);// 忽略缓存

            httpConn.setReadTimeout(timeOut);
            httpConn.setConnectTimeout(timeOut);
            httpConn.setRequestMethod("GET");// 设置URL请求方法

            httpConn.connect();
            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuffer sb = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                responseReader = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream(), "UTF-8"), 10 * 1024);
                while (!TextUtils.isEmpty(readLine = responseReader.readLine())) {
                    sb.append(readLine).append("\n");
                }
                responseReader.close();
                httpConn.disconnect();
                return sb.toString();
            } else {
            }

        } catch (Exception e) {
        }

        return null;

    }

    public static String byte2String(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%1$02x", 0xff & b));
        }
        return sb.toString();
    }

}
