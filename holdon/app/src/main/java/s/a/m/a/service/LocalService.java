package s.a.m.a.service;

/**
 * Created by amas on 15-5-31.
 */

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import client.core.Core;
import client.core.model.Event;
import client.core.model.Notifiers;
import s.a.m.a.holdon.CheckInRecord;
import s.a.m.a.holdon.HTask;
import s.a.m.a.holdon.event.EVENT;
import s.a.m.a.holdon.event.EventCheckinResult;
import s.a.m.a.holdon.event.EventNewTask;
import s.a.m.a.holdon.storage.LocalStorage;

public class LocalService extends IntentService {
    public static final String ACTION_REMOVE_TASK = ".ACTION_REMOVE_TASK";
    public static final String ACTION_ADD_TASK = ".ACTION_ADD_TASK";
    public static final String ACTION_CHECK_IN = ".ACTION_CHECK_IN";
    public static final String ACTION_SCHED_ALL = ".ACTION_SCHED_ALL";

    public LocalService() {
        super("LocalService");
    }

    public static void start_ACTION_REMOVE_TASK(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, LocalService.class);
        intent.setAction(ACTION_REMOVE_TASK);
        context.startService(intent);
    }

    public static void start_ACTION_ADD_TASK(Context context, HTask hTask) {
        Intent intent = new Intent();
        intent.setClass(context, LocalService.class);
        intent.putExtra(":htask", hTask);
        intent.setAction(ACTION_ADD_TASK);
        context.startService(intent);
    }

    public static void start_ACTION_CHECK_IN(Context context, HTask hTask) {
        Intent intent = new Intent();
        intent.setClass(context, LocalService.class);
        intent.setAction(ACTION_CHECK_IN);
        intent.putExtra(":htask", hTask);
        context.startService(intent);
    }



    public static void start_ACTION_SCHED_ALL(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, LocalService.class);
        intent.setAction(ACTION_SCHED_ALL);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (false) {
        } else if (ACTION_REMOVE_TASK.equals(action)) {
            onHandle_ACTION_REMOVE_TASK(intent);
        } else if (ACTION_ADD_TASK.equals(action)) {
            onHandle_ACTION_ADD_TASK(intent);
        } else if (ACTION_CHECK_IN.equals(action)) {
            onHandle_ACTION_CHECK_IN(intent);
        } else if (ACTION_SCHED_ALL.equals(action)) {
            onHandle_ACTION_SCHED_ALL(intent);
        }
    }

    private void onHandle_ACTION_REMOVE_TASK(Intent intent) {
        // TODO: 这里位于worker线程
        System.out.println("LocalService -| " + intent.toString());
    }

    private void onHandle_ACTION_ADD_TASK(Intent intent) {
        // TODO: 这里位于worker线程
        System.out.println("LocalService -| " + intent.toString());
        HTask htask = (HTask) intent.getSerializableExtra(":htask");
        if(htask == null) {
            return;
        }
        htask.sync();
        long _id = LocalStorage.getsInstance().addHTask(htask);
        android.util.Log.i("htask", "insert: " + _id);
        EVENT.send(new EventNewTask(htask));

    }

    private void onHandle_ACTION_CHECK_IN(Intent intent) {
        System.out.println("LocalService -| " + intent.toString());

        HTask htask = (HTask) intent.getSerializableExtra(":htask");
        if(htask == null) {
            return;
        }


        CheckInRecord cr = htask.checkin_io();
        sendToUI(EventCheckinResult.create(htask));
    }

    static Notifiers UI = new Notifiers("ui");

    public static void sendToUI(Event event) {
        event.setTo(UI);
        Core.I().push(event);
    }

    private void onHandle_ACTION_SCHED_ALL(Intent intent) {
        // TODO: 这里位于worker线程
        System.out.println("LocalService -| " + intent.toString());
        List<HTask> xs = LocalStorage.getsInstance().getAllHTask();
        for(HTask x : xs) {
            x.updateStatus(null);
           android.util.Log.i("htask", x.toString());
        }
    }
}