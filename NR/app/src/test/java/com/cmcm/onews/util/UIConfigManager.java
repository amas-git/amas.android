package com.cmcm.onews.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.cmcm.onews.ONewsScenarios;
import com.cmcm.onews.sdk.NewsSdk;

/**
 * 配置文件
 */
public final class UIConfigManager {
    private static UIConfigManager instance = null;

    private String mSharedPreferenceName = null;
    private SharedPreferences mshardPreferences = null;

    /**
     * 2g 弹窗提示
     */
    private static final String NEWS_2G_GUIDE_TOAST = "news_2g_guide_toast";

    /**
     * app创建快捷方式
     */
    private static final String NEWS_APP_CREATE_SHORCUT = "news_app_create_shorcut";

    /**
     * GCM save push id
     */
    private static final String NEWS_GCM_PUSH_ID = "news_gcm_push_id";

    /**
     * GCM  NOTIDY-id - push id Map
     */
    private static final String NEWS_GCM_NOTIFY_ID_TABLE = "news_gcm_notify_id_table";
    /**
     * GCM  NOTIDY-id-map-idx
     */
    private static final String NEWS_GCM_NOTIFY_ID_TABLE_IDX = "news_gcm_notify_id_table_idx";
    /**
     * GCM  NOTIDY-id-map-limit
     */
    private static final String NEWS_GCM_NOTIFY_ID_TABLE_LIMIT = "news_gcm_notify_id_table_limit";

    /**
     * 分类
     */
    private static final String NEWS_CATEGORY_SHOW = "news_category_show";

    /**
     * Category Interest
     */
    private static final String NEWS_CATEGORY_INTEREST = "news_category_interest";

    /**
     * 最近一次离线数据
     */
    private static final String NEWS_LAST_OFFLINE = "news_last_offline";

    /**
     * 用户引导
     */
    private static final String NEWS_USER_GUIDE = "news_language_select";

    /**
     * 统计app进入次数
     */
    private static final String NEWS_APP_COUNT_NUM = "news_app_count_num";

    /**
     * Facebook引导是否被点击过Go
     */
    private static final String NEWS_FB_GUIDE_GO_IS_CLICK = "news_fb_guide_go_is_click";

    /**
     * Facebook引导是否被点击过NO
     */
    private static final String NEWS_FB_GUIDE_NO_IS_CLICK = "news_fb_guide_no_is_click";

    /**
     * Facebook引导点击NO的时间
     */
    private static final String LAST_TIME_NEWS_FB_GUIDE_CLICK_NO= "last_time_news_fb_guide_click_no";

    /**
     * 自动下载离线内容是否提示过
     */
    private static final String OFFLINE_DOWNLOAD_CONTEN_TIP = "offline_download_conten_tip";


    /**
     * guide dislike
     */
    private static final String NEWS_GUIDE_DISLIKE = "news_guide_dislike";

    /**
     * guide offline
     */
    private static final String NEWS_GUIDE_OFFLINE = "news_guide_offline";

    private UIConfigManager(Context context) {
        mSharedPreferenceName = new String(context.getPackageName() + "_ui_preferences");
        mshardPreferences = context.getSharedPreferences(mSharedPreferenceName, Context.MODE_PRIVATE);
    }

    public static UIConfigManager getInstanse(Context context) {
        if (instance == null) {
            instance = new UIConfigManager(context.getApplicationContext());
        }
        return instance;
    }

    private SharedPreferences getSharedPreference() {
        return mshardPreferences;
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

    public void setNEWS_2G_GUIDE_TOAST() {
        setBooleanValue(NEWS_2G_GUIDE_TOAST, false);
    }

    public boolean getNEWS_2G_GUIDE_TOAST() {
        return getBooleanValue(NEWS_2G_GUIDE_TOAST, true);
    }

    public void setNEWS_APP_CREATE_SHORCUT() {
        setBooleanValue(NEWS_APP_CREATE_SHORCUT, true);
    }

    public boolean getNEWS_APP_CREATE_SHORCUT() {
        return getBooleanValue(NEWS_APP_CREATE_SHORCUT, false);
    }

    public void setNEWS_GCM_PUSH_ID(String data) {
        setStringValue(NEWS_GCM_PUSH_ID, data);
    }

    public String getNEWS_GCM_PUSH_ID() {
        return getStringValue(NEWS_GCM_PUSH_ID, "");

    }

    public void setNEWS_GCM_NOTIFY_ID_TABLE(String data) {
        setStringValue(NEWS_GCM_NOTIFY_ID_TABLE, data);
    }

    public String getNEWS_GCM_NOTIFY_ID_TABLE() {
        return getStringValue(NEWS_GCM_NOTIFY_ID_TABLE, "");

    }

    public void setNEWS_GCM_NOTIFY_ID_TABLE_IDX(int idx) {
        setIntValue(NEWS_GCM_NOTIFY_ID_TABLE_IDX, idx);
    }

    public int getNEWS_GCM_NOTIFY_ID_TABLE_IDX() {
        return getIntValue(NEWS_GCM_NOTIFY_ID_TABLE_IDX, 0);
    }

    public void setNEWS_GCM_NOTIFY_ID_TABLE_LIMIT(int limit) {
        setIntValue(NEWS_GCM_NOTIFY_ID_TABLE_LIMIT, limit);
    }

    public int getNEWS_GCM_NOTIFY_ID_TABLE_LIMIT() {
        return getIntValue(NEWS_GCM_NOTIFY_ID_TABLE_LIMIT, 0);
    }

    public void setNEWS_CATEGORY_INTEREST(String value) {
        setStringValue(NEWS_CATEGORY_INTEREST, value);
        ONewsScenarios.getInstance().initScenarios(true);
        NewsSdk.INSTAMCE.setSupportInterest(value);

        if (!TextUtils.isEmpty(value)) {
            NewsSdk.INSTAMCE.reportInterests();
        }
    }

    public String getNEWS_CATEGORY_INTEREST() {
        return getStringValue(NEWS_CATEGORY_INTEREST, "");
    }

    public long getNEWS_LAST_OFFLINE() {
        return getLongValue(NEWS_LAST_OFFLINE, -1);
    }

    public void setNEWS_LAST_OFFLINE(long time) {
        setLongValue(NEWS_LAST_OFFLINE, time);
    }

    private static final String LANGUAGE_SELECTED = "language_selected";
    private static final String COUNTRY_SELECTED = "country_selected";

    public LanguageCountry getLanguageSelected(Context context) {
        String language = getStringValue(LANGUAGE_SELECTED, LanguageCountry.LANGUAGE_OPTION_DEFAULT);
        String country = getStringValue(COUNTRY_SELECTED, LanguageCountry.COUNTRY_OPTION_DEFAULT);
        if (language.equalsIgnoreCase(LanguageCountry.LANGUAGE_OPTION_DEFAULT)) {
            language = context.getResources().getConfiguration().locale.getLanguage();
        }
        if (country.equalsIgnoreCase(LanguageCountry.COUNTRY_OPTION_DEFAULT)) {
            country = context.getResources().getConfiguration().locale.getCountry();
        }
        return new LanguageCountry(language, country);
    }

    public void setLanguageSelected(LanguageCountry languageCountry) {
        setStringValue(LANGUAGE_SELECTED, languageCountry.getLanguage());
        setStringValue(COUNTRY_SELECTED, languageCountry.getCountry());
    }

    public void setNEWS_USER_GUIDE(){
        setBooleanValue(NEWS_USER_GUIDE, false);
    }

    public boolean getNEWS_USER_GUIDE(){
        return getBooleanValue(NEWS_USER_GUIDE,true);
    }
    private final static String LAST_VERSION_UPDATE_CHECK="last_version_update_check";
    public long getLastVersionUpdateCheck() {
        return getLongValue(LAST_VERSION_UPDATE_CHECK,0);
    }
    public void setLastVersionUpdateCheck(long time){
        setLongValue(LAST_VERSION_UPDATE_CHECK,time);
    }
    private final static String LAST_CHECK_SHOW_VERSION = "last_check_show_version";
    public   void setLastCheckVersion(long version){
        setLongValue(LAST_CHECK_SHOW_VERSION,version);
    }
    public long getLastCheckVersion(){
        return getLongValue(LAST_CHECK_SHOW_VERSION,0l);
    }

    public void setNEWS_APP_COUNT_NUM(int countNumN) {
        setIntValue(NEWS_APP_COUNT_NUM, countNumN);
    }

    public int getNEWS_APP_COUNT_NUM() {
        return getIntValue(NEWS_APP_COUNT_NUM, 0);
    }

    public void setNEWS_FB_GUIDE_GO_IS_CLICK(){
        setBooleanValue(NEWS_FB_GUIDE_GO_IS_CLICK, false);
    }

    public boolean getNEWS_FB_GUIDE_GO_IS_CLICK(){
        return getBooleanValue(NEWS_FB_GUIDE_GO_IS_CLICK, true);
    }

    public void setNEWS_FB_GUIDE_NO_IS_CLICK(){
        setBooleanValue(NEWS_FB_GUIDE_NO_IS_CLICK, false);
    }

    public boolean getNEWS_FB_GUIDE_NO_IS_CLICK(){
        return getBooleanValue(NEWS_FB_GUIDE_NO_IS_CLICK, true);
    }

    public void setLAST_TIME_NEWS_FB_GUIDE_CLICK_NO(long time){
        setLongValue(LAST_TIME_NEWS_FB_GUIDE_CLICK_NO, time);
    }

    public long getLAST_TIME_NEWS_FB_GUIDE_CLICK_NO(){
        return getLongValue(LAST_TIME_NEWS_FB_GUIDE_CLICK_NO, 0l);
    }

    public void setOFFLINE_DOWNLOAD_CONTEN_TIP(){
        setBooleanValue(OFFLINE_DOWNLOAD_CONTEN_TIP, true);
    }

    public boolean isOFFLINE_DOWNLOAD_CONTEN_TIP(){
        return getBooleanValue(OFFLINE_DOWNLOAD_CONTEN_TIP, false);
    }

    public boolean getNEWS_GUIDE_DISLIKE(){
        return getBooleanValue(NEWS_GUIDE_DISLIKE, true);
    }

    public void setNEWS_GUIDE_DISLIKE(boolean isDislike){
        setBooleanValue(NEWS_GUIDE_DISLIKE,isDislike);
    }

    public boolean getNEWS_GUIDE_OFFLINE(){
        return getBooleanValue(NEWS_GUIDE_OFFLINE,true);
    }

    public void setNEWS_GUIDE_OFFLINE(boolean isOffline){
        setBooleanValue(NEWS_GUIDE_OFFLINE,isOffline);
    }
}
