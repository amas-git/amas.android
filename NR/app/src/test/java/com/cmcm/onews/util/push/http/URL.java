/**
 * URL.java
 *
 * Description: 
 * 
 *  Created on: Nov 1, 2012 11:11:57 AM
 *      Author: zengwei
 */
package com.cmcm.onews.util.push.http;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * @author zengwei
 * 
 */
public class URL {

    // private final static String TAG = "URI";

    private String rawUrl;
    private String scheme;
    private String host;
    private int port;
    private String path;
    private String query;
    private HashMap<String, String> params;
    private String anchor;

    public static final URL parse(String url) {
        return new URL(url);
    }

    private URL(String rawUrl) {
        this.rawUrl = rawUrl;
        try {
            parse();
        } catch (Exception e) {
        }
    }

    public String getRawUrl() {
        return rawUrl;
    }

    public String getScheme() {
        return scheme;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public HashMap<String, String> getParams() {
        return params == null ? (params = _parseQuery(query)) : params;
    }

    public String getParam(String key, String defaultValue) {
        if (key == null || key.trim().length() == 0)
            return "";

        if (params == null)
            params = _parseQuery(query);

        if (params != null && params.containsKey(key)) {
            return params.get(key);
        }

        return defaultValue;
    }

    public String getAnchor() {
        return anchor;
    }

//    /**
//     * 将传入的 query 字符串，分解成key-value对, value 会使用 URLDecoder.decode() 解码
//     * 
//     * @param query
//     *            需要分析的字符串
//     * @return key-value 对，如果分析失败则返回 null 或者空的HashMap对象
//     */
//    public static final HashMap<String, String> splitParams(String query) {
//        return _parseQuery(query);
//    }

    /**
     * 分析持有的URL，并将各段信息保存到相应的变量中
     */
    private void parse() {
        if (TextUtils.isEmpty(this.rawUrl)) return;
        
        java.net.URL uri = null;
        try {
            uri = new java.net.URL(this.rawUrl);
            scheme = uri.getProtocol();
            host = uri.getHost();
        } catch (Exception e) {
            int pos = this.rawUrl.indexOf("://");
            if (pos > 0) {
                scheme = this.rawUrl.substring(0, pos);
            }
            host = null;
        }

        if (TextUtils.isEmpty(scheme)) return;
        
        if (host == null || host.length() == 0) {
            String _url = rawUrl.replace(scheme, "").replace("://", "");
            if (_url.contains("/")) {
                String[] host_port = _url.substring(0, _url.indexOf('/')).split(":");
                if (host_port == null || host_port.length == 0) return;
                
                host = host_port[0];
                if (host_port.length > 1) {
                    port = Integer.valueOf(host_port[1]);
                }

                _url = _url.substring(_url.indexOf('/'));
            }

            if (_url.contains("?")) {
                path = _url.substring(0, _url.indexOf('?'));

                _url = _url.substring(_url.indexOf('?') + 1);
            }

            if (_url.contains("#")) {
                query = _url.substring(0, _url.indexOf('#'));
                anchor = _url = _url.substring(_url.indexOf('#') + 1);
            } else {
                query = _url;
            }
        } else if (uri != null) {
            port = uri.getPort();
            path = uri.getPath();
            query = uri.getQuery();
            if (rawUrl.contains("#")) {
                anchor = rawUrl.substring(rawUrl.lastIndexOf("#") + 1);
            }
        }
    }

    private static HashMap<String, String> _parseQuery(String query) {
        if (query == null || query.length() == 0)
            return null;

        HashMap<String, String> _params = new HashMap<String, String>();
        String[] key_value = query.split("&");
        for (int i = 0; i < key_value.length; i++) {
            String[] param = key_value[i].split("=");
            if (param.length == 1)
                _params.put(param[0], "");
            else {
                try {
                    _params.put(param[0], URLDecoder.decode(param[1], "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    _params.put(param[0], "");
                }
            }
        }
        return _params;
    }
}
