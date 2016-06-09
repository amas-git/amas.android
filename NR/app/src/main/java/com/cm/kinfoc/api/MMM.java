package com.cm.kinfoc.api;

import com.cm.kinfoc.KInfocUtil;

public class MMM {
	public static boolean DEBUG = KInfocUtil.serverbugLog;//BuildConfig.DEBUG;
	public static void log(String message) {
		if(KInfocUtil.debugLog) android.util.Log.i("mmm", "" + message);
	}
}
