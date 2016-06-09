package com.cmcm.onews.util.push.gcm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cmcm.onews.NewsL;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.util.DeviceUtils;
import com.cmcm.onews.util.gcm.GCM_CMTrackServer;

/**
 * Created by cm on 2015/11/16.
 */
public class GCMRegister {

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    //public static final String GCM_TOKEN = "GCM_Token";
    public static final String GET_VERSION_CODE = "get_version_code";
    // for app register , before register check token exist or not
    public static void register(Context context,String senderId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean sentToken = sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false);
        String regID = sharedPreferences.getString(GCM_CMTrackServer.GCM_TOKEN, "");
        int versionCode = sharedPreferences.getInt(GET_VERSION_CODE, 0);//old versionCode
        int currentBVersionCode = 0;
        try {
             currentBVersionCode = DeviceUtils.getVersionCode(context);//new versionCode
        } catch (Exception e) {
            L.gcm("get current versionCode fail");
            e.printStackTrace();
        }
        if (false == sentToken || "".equals(regID) || versionCode !=currentBVersionCode) {
            registerToGoogle(context, senderId);
        } else {
            NewsL.push("[have registered] : " + regID);
        }
    }

    // for app register & re-register when system notify to refresh token
    public static void registerToGoogle(Context context,String senderId) {
        try {
            Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
            registrationIntent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
            registrationIntent.putExtra("sender", senderId);
            registrationIntent.setPackage("com.google.android.gms");
            context.startService(registrationIntent);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
