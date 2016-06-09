package com.cmcm.onews.util.push.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.cm.CH;
import com.cmcm.onews.C;
import com.cmcm.onews.NewsL;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.transport.HTTPC;
import com.cmcm.onews.util.DevUtil;
import com.cmcm.onews.util.DeviceUtils;
import com.cmcm.onews.util.LanguageCountry;
import com.cmcm.onews.util.PackageUtils;
import com.cmcm.onews.util.UIConfigManager;
import com.cmcm.onews.util.gcm.GCM_CMTrackServer;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 负责上报注册行为
 */
public class GCM_CMRegServer {

    protected String host = "http://cm.gcm.ksmobile.com/rpc/gcm/report/?";

    protected Map<String, String> params = new HashMap<String, String>();
    private  Context mContext;
    private  SharedPreferences mSharedPreferences;
    private  String mRegId;

    private static Map<String, String> STATIC_PARAMS = new HashMap<String, String>();
    /**
     * 靜態請求參數,通常不會發生改變
     */
    static {
        addParams(STATIC_PARAMS, "appflag", "india_news");
        addParams(STATIC_PARAMS, "aid", DevUtil.getAndroidId(C.getAppContext()));       // 设备唯一 ID，Android 是 Android Device Id

        addParams(STATIC_PARAMS, "apkversion", PackageUtils.getAppVersionName(C.getAppContext()));      // (靜態) 对应客户端 app 的版本。
        addParams(STATIC_PARAMS, "sdkversion", DevUtil.getOSVersion());      // (靜態) 操作系统版本。

        addParams(STATIC_PARAMS, "manufacture", Build.MANUFACTURER.toLowerCase());
      /*  addParams(STATIC_PARAMS, "mcc", DevUtil.getMCC(C.getAppContext()));
        addParams(STATIC_PARAMS, "mnc", DevUtil.getMNC(C.getAppContext()));

        addParams(STATIC_PARAMS, "cl", DevUtil.getCL());

        addParams(STATIC_PARAMS, "country", Locale.getDefault().getCountry());
        addParams(STATIC_PARAMS, "phonelanguage", Locale.getDefault().getLanguage());*/

        addParams(STATIC_PARAMS, "timezone", DevUtil.getTimeZone());
//        addParams(STATIC_PARAMS, "channel", String.valueOf(CH.getChannelId()));

        addParams(STATIC_PARAMS, "buildnum", Build.SERIAL );
    }


    public GCM_CMRegServer(Context context,String regId) {
        mContext = context;
        params.putAll(STATIC_PARAMS);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mRegId = regId;
        setOldRegId();
        regid(mRegId);
        initParams();
    }

    public void initParams(){
        LanguageCountry lang = UIConfigManager.getInstanse(C.getAppContext()).getLanguageSelected(C.getAppContext());
        addParams("mcc", DevUtil.getMCC(C.getAppContext()));
        addParams("mnc", DevUtil.getMNC(C.getAppContext()));

        addParams("cl", DevUtil.getCL());

        addParams("country", lang.getCountry());

        addParams("phonelanguage", lang.getLanguage());
        addParams("channel", String.valueOf(CH.getChannelId()));

    }

    public void setOldRegId() {
        String regID = mSharedPreferences.getString(GCM_CMTrackServer.GCM_TOKEN, "");
        if (!("".equals(regID))){
            oldRegid(regID);
        }
    }

    /**
     * 必填: 是
     * 接入點REG ID, Google GCM 分配給客戶端的token
     */
    public void regid(String regid) {
        addParams("regid", regid);
    }

    /**
     * 必填: 否
     * 舊的接入點REG ID, Google GCM 分配給客戶端的舊的token
     */
    public void oldRegid(String regid) {
        addParams("oregid", regid);
    }

    /**
     * KEY   VALUE
     * $key  $key=encode($value)
     */
    protected GCM_CMRegServer addParams(String key, String value) {
        if(value == null) {
            value = "";
        }
        addParams(params, key, value);
        return this;
    }

    protected static void addParams(Map<String,String> params, String key, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(key)
                .append("=");

        try {
            sb.append(URLEncoder.encode(value, "UTF_8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        params.put(key, sb.toString());
    }

    public String toUrl() {
        StringBuilder sb = new StringBuilder();

        String _host = host.trim();
        sb.append(_host);

        if (!_host.endsWith("/?")) {
            sb.append("/?");
        }

        if (!params.isEmpty()) {
            sb.append(TextUtils.join("&", params.values()));
        }

        return sb.toString();
    }


    public void regToCMServer() {
        new RegAsyncTask().execute(toUrl());
    }


    class RegAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... parameters) {
            try {
                String result = HTTPC.httpGet(parameters[0]);
                JSONObject jobj = new JSONObject(result);
                if (null != jobj)
                {
                    if (jobj.optString("code").equals("0")) {
                        NewsL.push("[cm server result] success" + result);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                        // You should store a boolean that indicates whether the generated token has been
                        // sent to your server. If the boolean is false, send the token to your server,
                        // otherwise your server should have already received the token.
                        int versionCode =  DeviceUtils.getVersionCode(mContext);
                        L.gcm("[app versionCode] " + versionCode);
                        sharedPreferences.edit().putBoolean(GCMRegister.SENT_TOKEN_TO_SERVER, true).apply();
                        sharedPreferences.edit().putString(GCM_CMTrackServer.GCM_TOKEN, mRegId).apply();
                        sharedPreferences.edit().putInt(GCMRegister.GET_VERSION_CODE,versionCode);
                    } else {
                        NewsL.push("[cm server result] fail" + result);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {

        }
    }
}
