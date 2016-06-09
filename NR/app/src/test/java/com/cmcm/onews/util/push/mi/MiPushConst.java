package com.cmcm.onews.util.push.mi;

/**
 * 小米上报接入常量
 * Created by houlin on 2015/12/23.
 */
public class MiPushConst {

    public static final String APP_ID = "2882303761517323208";
    public static final String APP_KEY = "5441732331208";
    // 此TAG在adb logcat中检索自己所需要的信息， 只需在命令行终端输入 adb logcat | grep
    // com.xiaomi.mipushdemo
    public static final String TAG = "PushLog";

    public static final String EXTENDED_MSG = "msg";
    public static final String EXTENDED_TYPE = "extended_type";
    /**
     * mipush行为上报地址
     */
    public static final String REPORT_RPC_URL = "http://apns.ios.ijinshan.com/rpc/taskback/mi";
    /**
     * mipush设备上报地址
     */
    public static final String REGIST_RPC_URL = "http://apns.ios.ijinshan.com/rpc/mireport";

}
