package a.m.a.s.sched.android;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.CopyOnWriteArrayList;

import a.m.a.s.sched.Sched;
import a.m.a.s.sched.utils.SchedUtils;

/**
 *  adb shell logcat -s System.out sched
 * Created by amas on 15-5-23.
 */
public class SchedService extends IntentService {
    public static final String NAMESPACE = "a.m.a.s.sched.android";

    public static final String ACTION_SCHED  = NAMESPACE+".ACTION_SCHED";
    public static final String ACTION_CANCEL = NAMESPACE+".ACTION_CANCEL";
    public static final String ACTION_WAKE_CHECK_POINT = NAMESPACE+".ACTION_WAKE_CHECK_POINT";

    public SchedService() {
        super("SchedService");
    }

    private static final String SCHED_DEFAULT = "* * * * * */2"; // every 2 min once

    public static void start(Context context, int offset) {
        start(context, SCHED_DEFAULT, offset);
    }

    static int i = 0;
    public static void start(Context context, String schedExpr, int offset) {
        Sched sched = Sched.obtainSched(schedExpr);
        SchedUtils.schedAlarm(context, OnAlarmReceiver.INTENT_ACTION_ALARM(context, "Hello: " + (i++)), sched.evalLatestTriggerTimeAtTimeMillis(offset));
    }

    public static void start_WAKE_CHECK_POINT(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SchedService.class);
        intent.setAction(ACTION_WAKE_CHECK_POINT);
        context.startService(intent);
    }

    /**
     *
     * @param context
     * @param expectedTime expected time for calculate the alarm delay time
     */
    public static void start_ACTION_SCHED(Context context, long expectedTime) {
        Intent intent = new Intent();
        intent.setClass(context, SchedService.class);
        intent.setAction(ACTION_SCHED);
        intent.putExtra(KEY.EXPECTED_TIME, expectedTime);
        context.startService(intent);
    }

    public static void start_ACTION_CANCEL(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SchedService.class);
        intent.setAction(ACTION_CANCEL);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (false) {
        } else if (ACTION_SCHED.equals(action)) {
            onHandle_ACTION_SCHED(intent);
        } else if (ACTION_CANCEL.equals(action)) {
            onHandle_ACTION_CANCEL(intent);
        } else if (ACTION_WAKE_CHECK_POINT.equals(action)) {
            onHandle_ACTION_WAKE_CHECK_POINT(intent);
        }
    }

    private void onHandle_ACTION_WAKE_CHECK_POINT(Intent intent) {
        // TODO: 这里位于worker线程
        System.out.println("SchedService -| " + intent.toString());
    }

    private void onHandle_ACTION_SCHED(Intent intent) {
        // TODO: 这里位于worker线程
        System.out.println("SchedService -| " + intent.toString());


        // NEXT ROUND
        start(this, 60*1000);
    }

    private void onHandle_ACTION_CANCEL(Intent intent) {
        // TODO: 这里位于worker线程
        System.out.println("SchedService -| " + intent.toString());
    }

}
