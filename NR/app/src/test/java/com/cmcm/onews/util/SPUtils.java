package com.cmcm.onews.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.cmcm.onews.C;

public class SPUtils {

    static SharedPreferences sp = C.getAppContext().getSharedPreferences("crash_demo_sp",
            Context.MODE_PRIVATE);
    
    public static long GetLastUpdateDbTime() {
        return sp.getLong("LastUpdateDbTime", 0);
    }

    public static long GetAppStartTime() {
        return C.START_TIME;
    }

    public static void saveRecentCrashTime(long l) {
        sp.edit().putLong("RecentCrashTime", l).apply();
    }

    public static long getLastCrashFeedbackTime() {
        return sp.getLong("LastCrashFeedbackTime", 0);
    }

    public static void setLastCrashFeedbackTime(long lastCrashFeedbackTime) {
        sp.edit().putLong("LastCrashFeedbackTime", lastCrashFeedbackTime).apply();
        
    }

    public static int getLastBugFeedCount() {
        return sp.getInt("LastBugFeedCount", 0);
    }

    public static long getLastBugFeedTime() {
        return sp.getLong("LastBugFeedTime", 0);
    }

    public static void setLastBugFeedTime(long lastBugFeedTime) {
        sp.edit().putLong("LastBugFeedTime", lastBugFeedTime).apply();
    }

    public static void setLastBugFeedCount(int lastBugFeedCount) {
        sp.edit().putInt("LastBugFeedCount", lastBugFeedCount).apply();
    }
}
