package com.cmcm.cloudconfig;

//
// WARNING：请不要format这个文件
//
public class CloudConfigKey {
    
    // =============================== 主配置文件 ===============================
    
    public static final int FUCTION_TYPE_MAIN = 2;
    
    // 更新功能
    public final static String CLOUD_SECTION_KEY_UPDATE = "update_section_key";
    public final static String CLOUD_SUBKEY_UPDATE_VERSION = "update_section_version";
    public final static String CLOUD_SUBKEY_UPDATE_MD5 = "update_section_md5";
    public final static String CLOUD_SUBKEY_UPDATE_URL = "update_section_url";
    public final static String CLOUD_SUBKEY_UPDATE_CTL = "update_section_ctl";
    public final static String CLOUD_SUBKEY_UPDATE_PKG = "update_section_pkg";
    public final static String CLOUD_SUBKEY_UPDATE_TITLE="update_section_title";
    

    // 推送功能
    public static final String CLOUD_SECTION_KEY_CM_PUSH = "cloud_cm_push_main";
    public static final String CLOUD_SUBKEY_CM_PUSH_CUBE_GET_VERSION_TIME_DIVIDER = "cloud_subkey_cm_pusk_cube_get_version_time_divider";
    public static final String CLOUD_SUBKEY_CM_PUSH_CUBE_GET_VERSION_TIME_INTERVAL = "cloud_subkey_cm_pusk_cube_get_version_time_interval";

    // 记录用户网络状态
    public static final String CLOUD_SECTION_KEY_NET_STAT = "net_stat_record";
    public static final String CLOUD_SUBKEY_NET_STAT_ON   = "net_stat_record_on";



    // =============================== 消息推送配置文件 ===============================

    public static final int FUCTION_TYPE_PUSH_MESSAGE = 7;

    public static final String CLOUD_SECTION_KEY_PUSH_MESSAGE = "gcm_rcmd_cfg";
    public static final String CLOUD_SUBKEY_PUSH_MESSAGE_CONTENT = "content";



    /**************************************开屏页面*************************************/
    public final static String SPLASH_PIC_URL = "splash_pic_url";
    public final static String SPLASH_SECTION = "splash_section";
    public final static String SPLASH_ACTION_TYPE = "splash_action_type";
    public final static String SPLASH_ACTION_START_TIME = "splash_action_start_time";
    public final static String SPLASH_ACTION_END_TIME = "splash_action_end_time";
    public final static String SPLASH_ACTION_DATA = "splash_action_data";
    public final static String SPLASH_WIFI_SWITCH = "splash_wifi_switch";
    public final static String SPLASH_TITLE_STRING = "splash_title_string";
    public final static String SPLASH_ACTION_ID = "splash_action_id";
    public final static String SPLASH_MATCH_ID = "splash_match_id";


    /*************************************facebook guide**********************************/
    public final static String FB_GUIDE_SECTION="fb_guide_section";
    public final static String FB_GUIDE_SECTION_FREQ="fb_guide_section_freq";
    /********************************************直播刷新************************************/
    public final static String CAST_SECTION = "cast_section";
    public final static String CAST_SECTION_COMMENT = "cast_section_comment";
    public final static String CAST_SECTION_DIRECT = "cast_section_direct";
    public final static String CAST_SECTION_UI = "cast_section_ui";

    public final static String CAST_SECTION_SHARE_HI = "cast_section_share_hi";//直播分享印度语下发文案
    public final static String CAST_SECTION_SHARE_EN = "cast_section_share_en";//直播分享英语下发文案
    public final static String CAST_SECTION_SHARE_START = "cast_section_share_start";//直播分享弹泡时间
    public final static String CAST_SECTION_SHARE_CONTINUED = "cast_section_share_continued";//直播分享弹泡持续时间
    public final static String CAST_SECTION_COMMENTRAY = "cast_section_commentray";//直播 commentray 频道开启
    public final static String CAST_SECTION_LIVE_INDEX = "cast_section_live_index";//配置奇偶数来确定Live和ScoreCard页面的位置
    public final static String CAST_SECTION_LIVE_TABS_EN = "cast_section_live_tabs_en";//配置直播tabs顺序
    public final static String CAST_SECTION_LIVE_TABS_HI = "cast_section_live_tabs_hi";//配置直播tabs顺序
}
