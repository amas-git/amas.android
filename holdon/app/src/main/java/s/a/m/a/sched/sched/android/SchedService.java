package s.a.m.a.sched.sched.android;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

/**
 * Created by amas on 15-5-23.
 */
public class SchedService extends IntentService {
    public static final String NAMESPACE = SchedService.class.getPackage().getName();

    public static final String ACTION_SCHED  = NAMESPACE+".ACTION_SCHED";
    public static final String ACTION_CANCEL = NAMESPACE+".ACTION_CANCEL";

    public SchedService() {
        super("SchedService");
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
        }
    }

    private void onHandle_ACTION_SCHED(Intent intent) {
        // TODO: 这里位于worker线程
        System.out.println("LocalService -| " + intent.toString());
    }

    private void onHandle_ACTION_CANCEL(Intent intent) {
        // TODO: 这里位于worker线程
        System.out.println("LocalService -| " + intent.toString());
    }

}
