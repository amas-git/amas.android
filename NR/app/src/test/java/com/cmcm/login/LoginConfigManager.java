package com.cmcm.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.cmcm.onews.ONewsScenarios;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.util.LanguageCountry;
import com.cmcm.onews.util.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 配置文件
 */
public final class LoginConfigManager {
    private static LoginConfigManager sInstance = null;

    private String mSharedPreferenceName = null;
    private SharedPreferences mShardPreferences = null;

    private static final String LOGIN_OPTION = "login_option";
    private static final String FRESH_TIMES = "login_fresh_time_limit";
    private static final String LOGIN_USER_EMAIL= "com.cleanmaster.LOGIN_EMAIL";
    private static final String LOGIN_LAST_TIME_FRESH_TOKEN_FOR_GOOGLE = "com.cleanmaster.LOGIN_LAST_TIME_FRESH_TOKEN_FOR_GOOGLE";
    private static final String LOGIN_DATA = "com.cleanmaster.LOGIN_DATA";
    private static final String LOGIN_USER_INFO = "com.cleanmaster.LOGIN_USER_INFO";
    private static final String LOGIN_BASIC_STATE = "com.cleanmaster.LOGIN_BASIC_STATE";
    private static final String LOGIN_SID_INIT = "com.cleanmaster.LOGIN_SID_INIT";
    private static final String LOGIN_LOGIN_CM_CAPTURE_CODE_URL = "com.cleanmaster.LOGIN_LOGIN_CM_CAPTURE_CODE_URL";
    private static final String LOGIN_REGIST_CM_CAPTURE_CODE_URL = "com.cleanmaster.LOGIN_REGIST_CM_CAPTURE_CODE_URL";
    private static final String LOGIN_FACEBOOK_CM_CAPTURE_CODE_URL = "com.cleanmaster.LOGIN_FACEBOOK_CM_CAPTURE_CODE_URL";
    private static final String LOGIN_GOOGLE_CM_CAPTURE_CODE_URL = "com.cleanmaster.LOGIN_GOOGLE_CM_CAPTURE_CODE_URL";
    private static final String LOGIN_LAST_ADDRESS = "com.cleanmaster.LOGIN_LAST_ADDRESS";
    private static final String LOGIN_GOOGLE_ACCOUNT_DATA = "com.cleanmaster.LOGIN_GOOLE_ACCOUNT_DATA";


    private LoginConfigManager(Context context) {
        mSharedPreferenceName = new String(context.getPackageName() + "_login_preferences");
        mShardPreferences = context.getSharedPreferences(mSharedPreferenceName, Context.MODE_PRIVATE);
    }

    public synchronized static LoginConfigManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LoginConfigManager(context.getApplicationContext());
        }
        return sInstance;
    }

    private SharedPreferences getSharedPreference() {
        return mShardPreferences;
    }

    public int getIntValue(String key, int defValue) {
        return getSharedPreference().getInt(key, defValue);
    }

    public long getLongValue(String key, long defValue) {
        return getSharedPreference().getLong(key, defValue);
    }

    public boolean getBooleanValue(String key, boolean defValue) {
        return getSharedPreference().getBoolean(key, defValue);
    }

    public String getStringValue(String key, String defValue) {
        return getSharedPreference().getString(key, defValue);
    }

    public void setBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putBoolean(key, value);
        SharePreferenceUtil.applyToEditor(editor);
    }

    public void setIntValue(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putInt(key, value);
        SharePreferenceUtil.applyToEditor(editor);
    }

    public void setLongValue(String key, Long value) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putLong(key, value);
        SharePreferenceUtil.applyToEditor(editor);
    }

    public void setStringValue(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putString(key, value);
        SharePreferenceUtil.applyToEditor(editor);
    }



    public void setLoginLastAddress(String address){
        setStringValue(LOGIN_LAST_ADDRESS, address);
    }

    public String getLoginLastAddress(){
        return getStringValue(LOGIN_LAST_ADDRESS,"");
    }

    public void setGoogleAccountData(String data){
        setStringValue(LOGIN_GOOGLE_ACCOUNT_DATA, data);
    }

    public String getGoogleAccountData(){
        return getStringValue(LOGIN_GOOGLE_ACCOUNT_DATA,"");
    }

    public void setLoginCmCaptureCodeUrl(String data){
        setStringValue(LOGIN_LOGIN_CM_CAPTURE_CODE_URL,data);
    }

    public String getLoginCmCaptureCodeUrl(){
        return getStringValue(LOGIN_LOGIN_CM_CAPTURE_CODE_URL, "");
    }

    public void setRegistCmCaptureCodeUrl(String data){
        setStringValue(LOGIN_REGIST_CM_CAPTURE_CODE_URL,data);
    }

    public String getRegistCmCaptureCodeUrl(){
        return getStringValue(LOGIN_REGIST_CM_CAPTURE_CODE_URL, "");
    }

    public void setLoginFacebookCaptureCodeUrl(String data){
        setStringValue(LOGIN_FACEBOOK_CM_CAPTURE_CODE_URL, data);
    }

    public void setLoginGoogleCaptureCodeUrl(String data){
        setStringValue(LOGIN_GOOGLE_CM_CAPTURE_CODE_URL, data);
    }

    public String getLoginFacebookCaptureCodeUrl(){
        return getStringValue(LOGIN_FACEBOOK_CM_CAPTURE_CODE_URL,"");
    }

    public String getLoginGoogleCaptureCodeUrl(){
        return getStringValue(LOGIN_GOOGLE_CM_CAPTURE_CODE_URL,"");
    }

    public void setLoginSidInit(String data){
        setStringValue(LOGIN_SID_INIT, data);
    }

    public String getLoginSidInit(){
        return getStringValue(LOGIN_SID_INIT,"");
    }

    public int getLastLoginOption(){
        return getIntValue(LOGIN_OPTION, -1);
    }

    public void setLoginOption(int value){
        setIntValue(LOGIN_OPTION, value);
    }

    public String getFreshLimit(){
        return getStringValue(FRESH_TIMES, "");
    }

    public void putFreshTime(String time){
        setStringValue(FRESH_TIMES, time);
    }

    public void setLoginUserEmail(String data){
        if (!TextUtils.isEmpty(data) && isEmailReturnByServer(data)) {
            setStringValue(LOGIN_USER_EMAIL,data);
        } else {
            setStringValue(LOGIN_USER_EMAIL,"");
        }
    }

    public void setLoginBasicState(int isLogined){
        setIntValue(LOGIN_BASIC_STATE, isLogined);
    }

    /* BUILD_CTRL:IF:NOTCNVERSION */
    public int getLoginBasicState(){
        return getIntValue(LOGIN_BASIC_STATE, LoginDataHelper.LOGIN_OUT);
    }

    public String getLoginUserEmail(){
        return getStringValue(LOGIN_USER_EMAIL,"");
    }

    public void setLastTimeFreshGoogleToken(long time){
        setLongValue(LOGIN_LAST_TIME_FRESH_TOKEN_FOR_GOOGLE,time);
    }

    public long getLastTimeFreshGoogleToken(){
        return getLongValue(LOGIN_LAST_TIME_FRESH_TOKEN_FOR_GOOGLE, 0L);
    }

    public void setLoginData(String data){
        setStringValue(LOGIN_DATA, data);
    }

    public String getLoginData(){
        return getStringValue(LOGIN_DATA, "");
    }

    public void setLoginUserInfo(String value){
        setStringValue(LOGIN_USER_INFO, value);
    }

    public String getLoginUserInfo(){
        return getStringValue(LOGIN_USER_INFO, "");
    }


    public static boolean isEmailReturnByServer(String email) {
        final String mStrEmailRole =
                "^\\s*\\w+(?:\\.{0,1}[\\*\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";

        boolean ret = false;
        try {
            if(email.contains("@")) {
                ret = email.trim().matches(mStrEmailRole);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
