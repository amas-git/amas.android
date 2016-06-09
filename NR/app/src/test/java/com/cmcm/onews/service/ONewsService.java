package com.cmcm.onews.service;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cmcm.cloudconfig.CloudConfigIni;
import com.cmcm.onews.BuildConfig;
import com.cmcm.onews.NewsL;
import com.cmcm.onews.infoc.newsindia_actbg;
import com.cmcm.onews.receiver.OnAlarmReceiver;

/**
 * Created by amas on 11/19/15.
 * 目前先保留机制
 */
public class ONewsService extends PollingService {
    public static final String PACKAGE = BuildConfig.APPLICATION_ID;
    public static final String ACTION_ACT_BG = BuildConfig.APPLICATION_ID+".ACTION_ACT_BG";

    public static final String ACTION_CUBE_CHECK_UPDATE_FREQUENCY = PACKAGE + "."
            + OnAlarmReceiver.class.getSimpleName() + ".action_cube_check_update_frequency";

    public static final String ACTION_CUBE_ALARM_REQUEST_CUBE = PACKAGE + "."
            + OnAlarmReceiver.class.getSimpleName() + ".action_request_cube";

    private boolean mIsCubeUpdateScheduled = false;

    public ONewsService() {
    }

    /**
     * 上报后台活跃
     * @param context
     */
    public static void start_ACTION_ACT_BG(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ONewsService.class);
        intent.setAction(ACTION_ACT_BG);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent == null) {
            return;
        }

        // 服务第一次启动的时候，设置魔方的自动更新
        if (!mIsCubeUpdateScheduled) {
            CloudConfigIni.scheduleUpdate();
            mIsCubeUpdateScheduled = true;
        }

        String action = intent.getAction();
        if(ACTION_ACT_BG.equals(action)) {
            onHandle_ACTION_ACT_BG(intent);
        }


        // 处理魔方的定时器

        if (TextUtils.equals(action, ACTION_CUBE_CHECK_UPDATE_FREQUENCY)) {
            // 这里已经是在线程里面
            CloudConfigIni.onAlarmCheckUpdateFrequency();
        }

        if (TextUtils.equals(action, ACTION_CUBE_ALARM_REQUEST_CUBE)) {
            CloudConfigIni.onAlarmRequestCube();
        }
    }

    private void onHandle_ACTION_ACT_BG(Intent intent) {
        NewsL.alarm("ACT: +1");
        new newsindia_actbg().report();
        OnAlarmReceiver.ACTION_ALARM(this, System.currentTimeMillis() + newsindia_actbg.BG_ACT_INTERVAL, "newsindia_actbg");
    }
}
