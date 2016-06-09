package com.google.analytics.tracking.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.cm.kinfoc.KInfocClient;
import com.cm.kinfoc.api.CnParser;
import com.cmcm.onews.C;
import com.cmcm.onews.infoc.newsindia_actcn;

import java.net.URLDecoder;

public class CampaignTrackingReceiver_nr extends BroadcastReceiver {
    static final String INSTALL_ACTION = "com.android.vending.INSTALL_REFERRER";
    static final String CAMPAIGN_KEY = "referrer";
    static final String SP_NAME=".ch";
    static final String TAG = "gp";
    public static String ORION_URL = "http://ssdk.adkmob.com/postback/ds/?tid=";

    public void onReceive(Context ctx, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action:" + action);
        if (TextUtils.isEmpty(action))
            return;

        Log.i(TAG, "CampaignTrackingReceiver comed.");
        String campaign = intent.getStringExtra("referrer");

        if ((!INSTALL_ACTION.equals(intent.getAction()))
                || (campaign == null) || ctx == null) {
            Log.i(TAG, "param error");
            return;
        }

        Log.i(TAG, campaign);

        new CampainThread(ctx, campaign).start();
    }

    public static void setChannel(Context context, int chid) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(":chid", chid).commit();
        new newsindia_actcn().refer(chid).report();
        KInfocClient.getInstance().setPublicHeaderInvalidate();
    }

    public static int getChannel(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(":chid", CnParser.getInstance(C.getAppContext()).getCN());
    }

    public static String getMeta(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(":utm_source", "N/A");
    }

    public static void setMeta(Context context, String meta) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(":utm_source", meta).commit();
    }

    public static String getCN2(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(":gp_cn2", CnParser.getInstance(C.getAppContext()).getCN2());
    }

    public static void setCN2(Context context, String gp_cn2) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(":gp_cn2", gp_cn2).commit();
    }

    private class CampainThread extends Thread {
        private Context mCtx = null;
        private String mCampaign = null;

        public CampainThread(Context context, String campaign){
            mCtx = context;
            mCampaign = campaign;
        }

        @Override
        public void run(){
            try {
                final String campaignDecode = URLDecoder.decode(mCampaign, "GBK");
                Log.i(TAG, "DECODE: " + campaignDecode);
                setMeta(mCtx, campaignDecode);
                if (!TextUtils.isEmpty(campaignDecode)) {
                    utm_source(mCtx,campaignDecode);
                }

                //pid#af_sub1#af_sub2
                String pid = getParam("pid", "N/A");
                String af_sub1 = getParam("af_sub1", "N/A");
                String af_sub2 = getParam("af_sub2", "N/A");
                setCN2(mCtx, pid+"#"+af_sub1+"#"+af_sub2);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String getParam(String paramName, String def){
            try {
                final String campaignDecode = URLDecoder.decode(mCampaign, "GBK");
                Log.i(TAG, "DECODE: " + campaignDecode);
                setMeta(mCtx, campaignDecode);
                if (!TextUtils.isEmpty(campaignDecode)) {
                    String[] utm_sourceSplit = campaignDecode.split(paramName+"=");
                    if (utm_sourceSplit.length > 1) {
                        String[] source = utm_sourceSplit[1].split("&");
                        if (source.length > 0) {
                            return TextUtils.isEmpty(source[0])? def: source[0];
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return def;
        }
    }

    private void utm_source(Context mCtx,String campaignDecode) {
        String[] utm_sourceSplit = campaignDecode.split("utm_source=");
        if (utm_sourceSplit.length > 1) {
            String[] source = utm_sourceSplit[1].split("&");
            if (source.length > 0) {
                try {
                    int channelId = Integer.valueOf(source[0]);
                    setChannel(mCtx, channelId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}