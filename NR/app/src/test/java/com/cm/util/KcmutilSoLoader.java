package com.cm.util;

import android.content.Context;

import com.cm.kinfoc.api.MMM;

public class KcmutilSoLoader {
	public static final String LIB_NAME = "kcmutil";

	private static Object mMutex = new Object();
	/* DCL不安全，必须加上volatile保证可见性 */
	private volatile static boolean mLoaded = false;

	public static boolean doLoad(Context context, boolean u) {

		if (mLoaded) {
			return true;
		}

		boolean loaded = false;
		synchronized (mMutex) {
			if (mLoaded) {
				return true;
			}

			try {
				LibLoader.getInstance().loadLibrary(context, LIB_NAME);
				loaded = true;
			} catch (Exception e) {
                MMM.log("LOAD SO EXCEPTION :" + e.toString());
                e.printStackTrace();
                loaded = false;
			} catch (Error e) {
                MMM.log("LOAD SO Error      :" + e.toString());
				loaded = false;
			}
			mLoaded = loaded;
            MMM.log("mLoaded   :" + mLoaded);
		}

		return loaded;
	}
}
