package com.cmcm.onews.util.push;

import android.app.ActivityManager;
import android.content.Context;

import com.cmcm.onews.C;
import com.cmcm.onews.R;
import com.cmcm.onews.util.ConflictCommons;
import com.cmcm.onews.util.StringUtils;
import com.cmcm.onews.util.gcm.GCM_CMTrackServer;
import com.cmcm.onews.util.push.comm.PushConst;
import com.cmcm.onews.util.push.gcm.GCMRegister;


/* BUILD_CTRL:IF:CN_VERSION_ONLY */
import com.xiaomi.mipush.sdk.MiPushClient;
import com.cmcm.onews.util.push.mi.MiPushConst;
import com.cmcm.onews.util.push.mi.MiPushReportUtils;
/* BUILD_CTRL:ENDIF:CN_VERSION_ONLY */
import java.util.List;

/**
 * push sdk 对外调用api
 * Created by houlin on 2016/2/20.
 */
public class PushOutAPI {
    public static void register(Context context) {
        if (ConflictCommons.isCNVersion()) {
            /* BUILD_CTRL:IF:CN_VERSION_ONLY */
            if (shouldInit(context)) {
                MiPushClient.registerPush(context, MiPushConst.APP_ID, MiPushConst.APP_KEY);
            }
            /* BUILD_CTRL:ENDIF:CN_VERSION_ONLY */
        } else {
            /* BUILD_CTRL:IF:OU_VERSION_ONLY */
            //push
            String senderid = StringUtils.getString(context, R.string.onews__gcm_senderid);
            GCMRegister.register(context, senderid);
            /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
        }

    }

    /**
     * 根据客户端行为，进行上报
     *
     * @param context
     * @param action
     * @param pushid
     */
    public static void reportMessageBehavior(
            final Context context,
            final int action, String pushid) {
        if (ConflictCommons.isCNVersion()) {
            /* BUILD_CTRL:IF:CN_VERSION_ONLY */
            if (action == PushConst.ACTION.ACCESS) {
                MiPushReportUtils.reportMessageBehavior(context, PushConst.ACTION.ACCESS, pushid);
            }
            if (action == PushConst.ACTION.DISPLAY) {
                MiPushReportUtils.reportMessageBehavior(context, PushConst.ACTION.DISPLAY, pushid);
            }
            if (action == PushConst.ACTION.BODY_CLICK) {
                MiPushReportUtils.reportMessageBehavior(context, PushConst.ACTION.BODY_CLICK, pushid);
            }
            /* BUILD_CTRL:ENDIF:CN_VERSION_ONLY */
        } else {
            /* BUILD_CTRL:IF:OU_VERSION_ONLY */
            if (action == PushConst.ACTION.ACCESS) {
                GCM_CMTrackServer trackServer = new GCM_CMTrackServer(C.getAppContext(), pushid);
                trackServer.reportToCMServer(GCM_CMTrackServer.GCM_ARRIVAL);
            }
            if (action == PushConst.ACTION.DISPLAY) {
                GCM_CMTrackServer trackServer = new GCM_CMTrackServer(C.getAppContext(), pushid);
                trackServer.reportToCMServer(GCM_CMTrackServer.GCM_DISPALY);
            }
            if (action == PushConst.ACTION.BODY_CLICK) {
                GCM_CMTrackServer trackServer = new GCM_CMTrackServer(context, pushid);
                trackServer.reportToCMServer(GCM_CMTrackServer.GCM_CLICK_NOTIFICATION);
            }
            /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
        }

    }

    private static boolean shouldInit(Context context) {
        if (context == null) {
            return false;
        }
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
