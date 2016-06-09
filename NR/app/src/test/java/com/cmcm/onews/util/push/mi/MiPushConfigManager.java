package com.cmcm.onews.util.push.mi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.cmcm.onews.util.SharePreferenceUtil;
import com.cmcm.onews.util.push.comm.PushLog;

/**
 * 推送模块常用设置的管理器
 * @author houlin
 * @date 2014.6.9
 * */
public class MiPushConfigManager {
    // 上次上报reg id成功时的附加字段的key.
    public static final String PUSH_TOPIC_ALL_			= "all_push_topic";
    public static final String PUSH_TOPIC_MANUFACTURE_	= "manufacture_push_topic";
    public static final String PUSH_TOPIC_MCC_			= "mcc_push_topic";
    public static final String PUSH_TOPIC_CHANNEL_		= "channel_push_topic";
    public static final String PUSH_TOPIC_TIMEZONE_	= "timezone_push_topic";
    public static final String PUSH_TOPIC_MNC_			= "mnc_push_topic";
    public static final String PUSH_TOPIC_COUNTRY_			= "country_push_topic";
    public static final String PUSH_TOPIC_COUNTRY_LANGUAGE_= "country_language_push_topic";
    public static final String PUSH_TOPIC_LANGUAGE_	= "language_push_topic";
    public static final String PUSH_TOPIC_APP_APKVERSION_NAME_ = "apk_version_name_cm_push_topic";
    public static final String PUSH_TOPIC_APP_APKVERSION_CODE_ = "apk_version_code_cm_push_topic";

    // register
    private static final String PUSH_UNREG_ID_= "push_unreg_id";
    private static final String PUSH_REG_ID_= "push_reg_id";
    private static final String PUSH_REG_ID_OLD_= "push_reg_id_old";
    private static final String PUSH_REG_TIME_  = "push_reg_time";
    private static final String REPORT_REG_TIME  = "report_reg_time";

    private static final String PUSH_REG_VERSION_  = "push_reg_version";
    private static final String PUSH_REG_ON_SERVER_  = "push_reg_id_on_server";
    private static final String PUSH_REG_ID_EXPIRED_TIME_ON_SERVER_  = "push_reg_id_expired_time";
    private static final String PUSH_REG_ID_LIFE_SPAN_ON_SERVER_  = "push_reg_id_life_span";
    private static final String PUSH_RETRY_BACKOFF_MS_  = "push_retry_backoff_ms";


    // status.
    public static final String PUSH_DOWNLOAD_LAST_TRY_TIME_  = "push_last_download_try_ms";

    public static final String POSITION_CITY_CODE  = "position_city_code";


    private SharedPreferences mShardPreferences = null;
    private static MiPushConfigManager mInstance = null;
    private static Object mLock = new Object();

    private MiPushConfigManager(Context context) {
        String name = context.getPackageName() + "PushConfig_Pref";
        mShardPreferences = context.getSharedPreferences(name, 4);
    }

    public static MiPushConfigManager getInstanse(Context context) {
        synchronized (mLock) {
            if(mInstance == null && context != null){
                mInstance = new MiPushConfigManager(context.getApplicationContext());
            }
        }
        return mInstance;
    }

    /**
     * 设置字符串类型的值
     * @author houlin
     * @date 2014.6.9
     * */
    public void setStringValue(String key, String value) {
        if(TextUtils.isEmpty(key) || value == null){
            return;
        }

        Editor editor = getSharedPreference().edit();
        editor.putString(key, value);
        SharePreferenceUtil.applyToEditor(editor);
    }

    /**
     * 获取字符串类型的值
     * @author houlin
     * @date 2014.6.9
     * */
    public String getStringValue(String key, String defValue){
        if(TextUtils.isEmpty(key) || defValue == null){
            return null;
        }

        return getSharedPreference().getString(key, defValue);
    }

    /**
     * 设置long类型的值
     * @author houlin
     * @date 2014.6.9
     * */
    public void setLongValue(String key, long value) {
        if(TextUtils.isEmpty(key)){
            return;
        }

        Editor editor = getSharedPreference().edit();
        editor.putLong(key, value);
        SharePreferenceUtil.applyToEditor(editor);
    }

    /**
     * 获取long类型的值
     * @author houlin
     * @date 2014.6.9
     * */
    public long getLongValue(String key, long defValue){
        if(TextUtils.isEmpty(key)){
            return defValue;
        }

        return getSharedPreference().getLong(key, defValue);
    }

    /**
     * 设置int类型的值
     * @author houlin
     * @date 2014.6.9
     * */
    public void setIntValue(String key, int value) {
        if(TextUtils.isEmpty(key)){
            return;
        }

        Editor editor = getSharedPreference().edit();
        editor.putInt(key, value);
        SharePreferenceUtil.applyToEditor(editor);
    }

    /**
     * 获取int类型的值
     * @author houlin
     * @date 2014.6.9
     * */
    public int getIntValue(String key, int defValue){
        if(TextUtils.isEmpty(key)){
            return defValue;
        }

        return getSharedPreference().getInt(key, defValue);
    }

    /**
     * 设置boolean类型的值
     * @author houlin
     * @date 2014.6.9
     * */
    public void setBooleanValue(String key, boolean value) {
        if(TextUtils.isEmpty(key)){
            return;
        }

        Editor editor = getSharedPreference().edit();
        editor.putBoolean(key, value);
        SharePreferenceUtil.applyToEditor(editor);
    }

    /**
     * 获取boolean类型的值
     * @author houlin
     * @date 2014.6.9
     * */
    public boolean getBooleanValue(String key, boolean defValue){
        if(TextUtils.isEmpty(key)){
            return defValue;
        }

        return getSharedPreference().getBoolean(key, defValue);
    }

    /**
     * 获取当前的reg id。
     * @author houlin
     * @date 2014.6.9
     * */
    public String getRegID(){
        String reg_id = getStringValue(PUSH_REG_ID_, "");
        return reg_id;
    }


    /**
     * 设置新的reg id
     * @author houlin
     * @date 2014.6.9
     * */
    public void setRegID(String regid){
        String old_regid = getRegID();
        setOldRegID(old_regid);
        setStringValue(PUSH_REG_ID_, regid);
    }

    /**
     * 获取注销的reg id。
     * @author houlin
     * @date 2014.6.9
     * */
    public String getUnRegID(){
        String reg_id = getStringValue(PUSH_UNREG_ID_, "");
        return reg_id;
    }

    /**
     * 设置注销的的reg id
     * @author houlin
     * @date 2014.6.9
     * */
    public void setUnRegID(String regid){
        String old_regid = getRegID();
        setOldRegID(old_regid);
        setStringValue(PUSH_UNREG_ID_, regid);
    }

    /**
     * 获取老的reg id。
     * @author houlin
     * @date 2014.6.9
     * */
    public String getOldRegID(){
        String reg_id = getStringValue(PUSH_REG_ID_OLD_, "");
        return reg_id;
    }

    /**
     * 设置老的reg id
     * @author houlin
     * @date 2014.6.9
     * */
    public void setOldRegID(String regid){
        setStringValue(PUSH_REG_ID_OLD_, regid);
    }

    /**
     * 获取reg id的注册时间
     * @author houlin
     * @date 2014.6.9
     * */
    public long getRegTime(){
        return getLongValue(PUSH_REG_TIME_, 0);
    }

    /**
     * 设置reg id注册时间
     * @author houlin
     * @date 2014.6.9
     * */
    public void setRegTime(long time){
        setLongValue(PUSH_REG_TIME_, time);
    }


    /**
     * 上报reg id的注册时间
     * @author houlin
     * @date 2014.6.9
     * */
    public long getReportTime(){
        PushLog.log("getReportTime ->" + getLongValue(REPORT_REG_TIME, 0)) ;
        return getLongValue(REPORT_REG_TIME, 0);
    }

    /**
     * 上报reg id注册时间
     * @author houlin
     * @date 2014.6.9
     * */
    public void setReportTime(long time){
        setLongValue(REPORT_REG_TIME, time);
        PushLog.log("setReportTime ->" + time) ;
    }

    /**
     * 获取注册下reg id时的apk版本信息
     * @author houlin
     * @date 2014.6.9
     * */
    public int getRegVersion(){
        return getIntValue(PUSH_REG_VERSION_, 0);
    }

    /**
     * 设置注册下reg id时的apk版本信息
     * @author houlin
     * @date 2014.6.9
     * */
    public void setRegVersion(int versionCode){
        setIntValue(PUSH_REG_VERSION_, versionCode);
    }

    /**
     * 获取注册下的reg id是否已经上传到App‘s Server的标志量。
     * @author houlin
     * @date 2014.6.9
     * */
    public boolean getRegOnServer(){
        return getBooleanValue(PUSH_REG_ON_SERVER_, false);
    }

    /**
     * 设置注册下的reg id是否已经上传到App‘s Server。
     * @author houlin
     * @date 2014.6.9
     * */
    public void setRegOnServer(boolean onServer){
        setBooleanValue(PUSH_REG_ON_SERVER_, onServer);
    }

    /**
     * 获取reg id在App’s上的过期时间
     * @author houlin
     * @date 2014.6.9
     * */
    public long getRegIDExpiredTime(){
        return getLongValue(PUSH_REG_ID_EXPIRED_TIME_ON_SERVER_, 0);
    }

    /**
     * 设置reg id在App’s上的过期时间
     * @author houlin
     * @date 2014.6.9
     * */
    public void setRegIDExpiredTime(long time){
        setLongValue(PUSH_REG_ID_EXPIRED_TIME_ON_SERVER_, time);
    }

    /**
     * 获取reg id在App’s上的生命周期
     * @author houlin
     * @date 2014.6.9
     * */
    /**
     * 设置reg id在App’s上的生命周期
     * @author houlin
     * @date 2014.6.9
     * */
    public void setOnServerLifeSpan(long lifespan){
        this.setLongValue(PUSH_REG_ID_LIFE_SPAN_ON_SERVER_, lifespan);
    }

    /**
     * 获取网络重试的间隔
     * @author houlin
     * @date 2014.6.9
     * */
    public long getBackOff(){
        return getLongValue(PUSH_RETRY_BACKOFF_MS_, 3000);
    }

    /**
     * 设置网络重试的间隔
     * @author houlin
     * @date 2014.6.9
     * */
    public void setBackOff(long backoff){
        this.setLongValue(PUSH_RETRY_BACKOFF_MS_, backoff);
    }

    /**
     * 获取私有的数据存储句柄类
     * @author houlin
     * @date 2014.6.9
     * */
    private SharedPreferences getSharedPreference() {
        return mShardPreferences;
    }



    /**
     * 获取cityCode
     * @author houlin
     * @date 2014.6.9
     * */
    public String getCityCode(){
        String cityCode = getStringValue(POSITION_CITY_CODE, "");
        return cityCode;
    }

    /**
     * 设置cityCode
     * @author houlin
     * @date 2014.6.9
     * */
    public void setCityCode(String cityCode){
        setStringValue(POSITION_CITY_CODE, cityCode);
    }
}
