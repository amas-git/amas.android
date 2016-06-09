package com.cmcm.onews.infoc;

import android.content.Context;
import android.content.SharedPreferences;

import com.cmcm.onews.C;
import com.cmcm.onews.NewsL;
import com.cmcm.onews.util.ConflictCommons;
import com.cmcm.onews.util.PackageUtils;

/**
 * CREATE FROM: newsindia_actbg:126 uptime2:int
 */
public class newsindia_actbg extends act {

    private static String sSuperName = ConflictCommons.isCNVersion() ? "newscn_actbg" : "newsindia_actbg";

    public newsindia_actbg() {
        super(sSuperName);
    }

    @Override
    public void reset() {
        installtype(PackageUtils.getInstallSource(C.getAppContext(), "default"));
    }


    @Override
    public boolean isValidate() {
        long now  = System.currentTimeMillis();
        long last = getLastReport();

        boolean isvalidate = false;
        if(now > last) {
            setLastReport(now+BG_ACT_INTERVAL);
            isvalidate = true;
        }
        NewsL.alarm("newsindia_actbg : " + true);
        return isvalidate;
    }

    private void setLastReport(long now) {
        SharedPreferences sp = C.getAppContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putLong(":bgact_last", now).apply();
    }

    private newsindia_actbg installtype(String installtype) {
        set("installtype", installtype);
        return this;
    }

    private static String SP_NAME = ".etc";
    public static long BG_ACT_INTERVAL = 4 * 60 * 60 * 1000L;

    private long getLastReport() {
        SharedPreferences sp = C.getAppContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getLong(":bgact_last", 0);
    }
}
