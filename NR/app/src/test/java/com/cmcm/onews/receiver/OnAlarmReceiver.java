package com.cmcm.onews.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cmcm.cloudconfig.CubeMessageManager;
import com.cmcm.onews.BuildConfig;
import com.cmcm.onews.service.ONewsService;

public class OnAlarmReceiver extends BroadcastReceiver {
    public static final String PACKAGE = BuildConfig.APPLICATION_ID;
    public static final String ACTION_ALARM = PACKAGE + ".ACTION_ALARM";
    public static final String EXPECTED_TIME = ":expected_time";
    public static final String KEY_MESSAGE = ":message";

    public static final String ACTION_CUBE_UPDATE_OVER = "com.cmplay.activesdk.cloud_cfg.update";//魔方提供的数据变化时的广播
    public static final String ACTION_EXTRA_DATA_CUBE_UPDATE_OVER_NEW_DATA_FROM_SERVER = "com.cmplay.activesdk.cloud_cfg.update.EXTRA.network";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        if (ACTION_ALARM.equals(action)) {
            // NewsL.alarm("HELLO : " + intent.getStringExtra(KEY_MESSAGE));
            ONewsService.start_ACTION_ACT_BG(context);
        }

        if (TextUtils.equals(action, ACTION_CUBE_UPDATE_OVER)) {
            boolean needUpdate = intent.getBooleanExtra(ACTION_EXTRA_DATA_CUBE_UPDATE_OVER_NEW_DATA_FROM_SERVER, false);
            if (needUpdate) {
                // 处理魔方的数据更新

                // 处理来自魔方的消息
                CubeMessageManager.getInstance(context).handleMessagesAsync();
            }
        }
    }

    public static Intent INTENT_ACTION_ALARM(Context context, String message) {
        Intent intent = new Intent();
        intent.setClass(context, OnAlarmReceiver.class);
        intent.setAction(ACTION_ALARM);
        intent.putExtra(KEY_MESSAGE, message);
        return intent;
    }

    public static void ACTION_ALARM(Context context, long time, String message) {
        schedAlarm(context, INTENT_ACTION_ALARM(context, message), time);
    }

    public static void schedAlarm(Context context, Intent intent, long atTimeInMillis) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent.putExtra(EXPECTED_TIME, atTimeInMillis);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, atTimeInMillis, sender);
    }

}