package com.cmcm.onews;

import android.util.Log;


import java.util.HashMap;

/**
 * 1. 盡量避免再此類外部定義新的日志TAG
 * 2. 如果需要追加一個日志TAG, 比如需要一個xxx TAG的日志, 可以在此類中添加對應的函數, 函數名需要和TAG名一致
 */
public class NewsL {
    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static void refresh(String str){
        if(DEBUG){
            Log.d("refresh",str);
        }
    }

    public static void newslist(String str){
        if(DEBUG){
            Log.d("newslist",str);
        }
    }

    public static void push(String str) {
        if(DEBUG){
            Log.d("push", str);
        }
    }

    public static void report(String str){
        if(DEBUG){
            Log.d("report", str);
        }
    }

    public static void localservice(String str){
        if(DEBUG){
            Log.d("localservice", str);
        }
    }

    public static final int L1 = 0;
    public static final int L2 = 2;
    public static final int L3 = 4;
    public static final int L4 = 8;
    public static final int TAB_WIDTH = 4;

    public static HashMap<String,String> PADDING_CACHE = new HashMap<>();

    public static String getPaddingString(String s, int n) {
        String key = s+"."+n;
        String paddings = PADDING_CACHE.get(key);
        if(paddings == null) {
            paddings = repeat(s, n);
            PADDING_CACHE.put(key, paddings);
        }
        return paddings;
    }

    public static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<n*TAB_WIDTH; ++i) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Add padding padding space to log message
     * @param prefix prefix string of message
     * @param message the message
     * @param level padding width
     * @return message with whitespace padding
     */
    public static String padding(final String prefix, String message, int level) {
        return padding(prefix, message, "\n", level);
    }

    public static String padding(final String prefix, String message, String split, int level) {
        StringBuilder sb = new StringBuilder();
        String[] xs = message.split(split);

        String paddings = getPaddingString(" ", level);
        for(String x : xs) {
            sb.append(paddings);
            if(prefix != null) {
                sb.append("[ " + prefix + " ] ");
            }
            sb.append(x).append("\n");
        }
        return sb.toString();
    }

    public static void event(String message) {
        if(DEBUG) Log.d("event", padding(null, message, L2));
    }

    public static void alarm(String message) {
        if(DEBUG) Log.d("alarm",padding(null, message, L2) );
    }

    public static void shorcut(String message) {
        if(DEBUG) Log.d("shorcut",padding(null, message, L2) );
    }

    public static void timeline(String message) {
        if(DEBUG) Log.d("timeline",padding(null, message, L2) );
    }

    public static void newsListLoader(String str) {
        if(DEBUG){
            Log.d("newsListLoader", str);
        }
    }

    public static void item(String str) {
        if(DEBUG){
            Log.d("newsitem", str);
        }
    }

    public static void newspageadapter(String str) {
        if(DEBUG){
            Log.d("newspageadapter", str);
        }
    }
}
