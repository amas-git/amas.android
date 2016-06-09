package com.cmcm.onews.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.cm.kinfoc.KInfocCommon;
import com.cmcm.cloudconfig.CloudConfigGetter;
import com.cmcm.cloudconfig.CloudConfigKey;
import com.cmcm.onews.BuildConfig;
import com.cmcm.onews.infoc.newsindia_net;
import com.cmcm.onews.util.SharePreferenceUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NetworkStateRecordService extends PollingService {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "networkstate";

    private static final int DEFAULT_POLL_INTERVAL   = 1000 * 60 * 1;       // 默认检查网络状态周期 1分钟
    private static final int DEFAULT_REPORT_INTERVAL = 1000 * 60 * 60 * 24; // 默认数据上报间隔 1天

    private static final String PACKAGE = BuildConfig.APPLICATION_ID;
    private static final String ACTION_ON_NETWORK_CHANGE = PACKAGE + ".ACTION_ON_NETWORK_CHANGE";

    private static final String KEY_INTENT = ":intent";

    private static final String SP_NAME            = ".nwtyper";
    private static final String SP_KEY_CREATE_TIME = "createtime";
    private static final String SP_KEY_TYPE_STAT   = "network_stat";

    private static final String SP_TYPE_STAT_DEFAULT_VALUE = "{}";


    private static long sLastTimeMillis = 0;
    private static int sLastNetworkType = KInfocCommon.NET_OFF;

    private static final int CLOUD_FUNCTION   = 1;
    private static final String CLOUD_SECTION = "section";
    private static final String CLOUD_KEY     = "key";

    public NetworkStateRecordService() {
        super();
    }

    public static void start_ACTION_ON_APP_START(Context context) {
        if (null == context) {
            return;
        }

        startService(context, PollingService.getIntent_ACTION_START_POLL(context, NetworkStateRecordService.class, DEFAULT_POLL_INTERVAL));
    }

    public static void start_ACTION_ON_NETWORK_CHANGE(Context context, Intent intent) {
        if (null == context || TextUtils.isEmpty(intent.getAction())) {
            return;
        }

        Intent i = new Intent();
        i.setClass(context, NetworkStateRecordService.class);
        i.setAction(ACTION_ON_NETWORK_CHANGE);
        i.putExtra(KEY_INTENT, intent);
        startService(context, i);
    }

    private static void startService(Context context, Intent intent) {
        /* BUILD_CTRL:IF:OU_VERSION_ONLY */

        if (null == context || null == intent || TextUtils.isEmpty(intent.getAction())) {
            return;
        }

        // 云控 默认关闭
        if (CloudConfigGetter.getBooleanValue(CloudConfigKey.FUCTION_TYPE_MAIN,
                                              CloudConfigKey.CLOUD_SECTION_KEY_NET_STAT,
                                              CloudConfigKey.CLOUD_SUBKEY_NET_STAT_ON,
                                              false)) {
            context.startService(intent);
        }

        /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
    }



    /* BUILD_CTRL:IF:OU_VERSION_ONLY */

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);

        checkNetworkState();
    }


    @Override
    protected void onPoll(int interval, int round, long lastTime) {

        checkNetworkState();
    }

    private void checkNetworkState() {
        int networkType = KInfocCommon.getNetworkType(this);
        long timeMillis = System.currentTimeMillis();

        if (sLastNetworkType != networkType || timeMillis - sLastTimeMillis >= DEFAULT_POLL_INTERVAL) {

            // 加一下限定条件，剔除会干扰统计的数据。
            // 比如修改系统时间后，两次循环之间的时间间隔可能非常大
            // 所以加一个限制，若两次循环之间间隔超过3倍interval，此条数据不记录
            if (sLastTimeMillis == 0 || timeMillis - sLastTimeMillis <= DEFAULT_POLL_INTERVAL * 3) {
                long lastSeconds = (sLastTimeMillis == 0) ? 0 : (timeMillis - sLastTimeMillis) / 1000;
                recordNetworkState(sLastNetworkType, lastSeconds);
            }

            sLastNetworkType = networkType;
            sLastTimeMillis = timeMillis;
        }
    }

    private void recordNetworkState(int lastNetworkType, long lastSeconds) {
        long currTime = System.currentTimeMillis();

        try {
            SharedPreferences sp = this.getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            // 记录创建时间
            long createTime = sp.getLong(SP_KEY_CREATE_TIME, 0);

            if (createTime <= 0) {
                createTime = currTime;
                editor.putLong(SP_KEY_CREATE_TIME, createTime);
            }

            // 记录网络类型保持时长 以 {"网络类型":"时长-秒"} json格式存储
            JSONObject jsonStat = new JSONObject(sp.getString(SP_KEY_TYPE_STAT, SP_TYPE_STAT_DEFAULT_VALUE));
            jsonStat.put("" + lastNetworkType, jsonStat.optLong("" + lastNetworkType) + lastSeconds);
            editor.putString(SP_KEY_TYPE_STAT, jsonStat.toString());

            mylog("SP_KEY_CREATE_TIME = " + createTime + "\nSP_KEY_TYPE_STAT = " + jsonStat.toString());

            // 达到上报周期就上报并清空
            if (currTime - createTime >= DEFAULT_REPORT_INTERVAL) {
                infocReport(createTime, jsonStat.toString());

                editor.putLong(SP_KEY_CREATE_TIME, 0);
                editor.putString(SP_KEY_TYPE_STAT, SP_TYPE_STAT_DEFAULT_VALUE);
            }

            SharePreferenceUtil.applyToEditor(editor);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void infocReport(long createtime, String statData) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String startDate = sdf.format(new Date(createtime));

            mylog("infocReport  startDate = " + startDate + ";    statData = " + statData);

            new newsindia_net().nettype(statData).creattime(startDate).report();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mylog(String content) {
        if (DEBUG) {
            Log.d(TAG, content);
        }
    }
    /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
}
