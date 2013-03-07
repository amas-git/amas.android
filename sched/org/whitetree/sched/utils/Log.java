package org.whitetree.sched.utils;

public final class Log {
    private static final String APP_TAG = "Alarm_LOG";
    public static final boolean DBG = true;

    private Log() {}

    private static String formatMsg(String tag, String msg) {
        return tag + " - " + msg;
    }

    public static void e(String tag, String msg) {
        android.util.Log.e(APP_TAG, formatMsg(tag, msg));
    }

    public static void e(String tag, String msg, Throwable tr) {
        android.util.Log.e(APP_TAG, formatMsg(tag, msg), tr);
    }

    public static void w(String tag, String msg) {
        android.util.Log.w(APP_TAG, formatMsg(tag, msg));
    }

    public static void w(String tag, String msg, Throwable tr) {
        android.util.Log.w(APP_TAG, formatMsg(tag, msg), tr);
    }

    public static void i(String tag, String msg) {
        android.util.Log.i(APP_TAG, formatMsg(tag, msg));
    }

    public static void i(String tag, String msg, Throwable tr) {
        android.util.Log.i(APP_TAG, formatMsg(tag, msg), tr);
    }

    public static void d(String tag, String msg) {
    	if(DBG)
        android.util.Log.d(APP_TAG, formatMsg(tag, msg));
    }

    public static void d(String tag, String msg, Throwable tr) {
        android.util.Log.d(APP_TAG, formatMsg(tag, msg), tr);
    }

    public static void v(String tag, String msg) {
        android.util.Log.v(APP_TAG, formatMsg(tag, msg));
    }

    public static void v(String tag, String msg, Throwable tr) {
        android.util.Log.v(APP_TAG, formatMsg(tag, msg), tr);
    }
}
