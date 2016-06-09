package com.cmcm.onews.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.cmcm.onews.BuildConfig;


/**
 * Created by amas on 12/26/14.
 * TODO:
 * 1. use alarm to handle long polling
 * 2. the message latency will case inaccuracy, so the handler thread must NOT be unblocked any time.
 * 3. One thread do both polling and command will case latency, MAYBE two thread.
 */
public abstract class PollingService extends Service {
    public static final int MSG_COMMAND = 1;
    public static final int MSG_NEXT_ROUND = 0;

    private static final String PACKAGE = BuildConfig.APPLICATION_ID;
    private static final String ACTION_START_POLL = PACKAGE + ".ACTION_START_POLL";
    private static final String ACTION_STOP_POLL = PACKAGE + ".ACTION_STOP_POLL";
    private static final String KEY_INTERVAL = ":interval";
    private static final int DEFAULT_POLL_INTERVAL = 5000;


    protected String name = "";

    private int mInterval = 1000;
    private int mRound = 0;
    private long mLastPollTime = 0;
    private boolean mStopPolling = false;
    private HandlerThread worker = null;
    private Handler handler = null;

    /**
     * Get intent for start polling
     *
     * @param context  the context
     * @param clazz    the class
     * @param interval polling interval, negative poll at once
     * @return
     */
    public static Intent getIntent_ACTION_START_POLL(Context context, Class<?> clazz, int interval) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        intent.putExtra(KEY_INTERVAL, interval);
        intent.setAction(ACTION_START_POLL);
        return intent;
    }

    /**
     * Get intent for stop polling
     *
     * @param context
     * @param clazz
     * @return
     */
    public static Intent getIntent_ACTION_STOP_POLL(Context context, Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        intent.setAction(ACTION_STOP_POLL);
        return intent;
    }

    public PollingService() {
        name = this.getClass().getSimpleName();
    }

    public class InternalHandler extends Handler {
        public InternalHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_COMMAND: {
                    Intent intent = (Intent) msg.obj;
                    onHandleIntent(intent);
                    break;
                }
                case MSG_NEXT_ROUND: {
                    if (isStopped()) {
                        return;
                    }

                    final int interval = msg.arg1;
                    // force poll
                    if (interval < 0) {
                        onPoll(interval, ++mRound, mLastPollTime);
                    } else {
                        onPoll(interval, ++mRound, mLastPollTime);
                        schduleNextPoll(interval);
                    }
                    mLastPollTime = System.currentTimeMillis();
                    break;
                }
                default:
                    /* NOP */
                    break;
            }
        }
    }

    protected void changeInterval(int interval, boolean imm) {
        if (interval < 0) {
            pollAtOnce();
        } else {
            if (mInterval != interval) {
                log(" SCHED POLLING FROM " + mInterval + " -> " + interval + " imm=" + imm);
                handler.removeMessages(MSG_NEXT_ROUND);
                if (imm) {
                    pollAtOnce();
                }
                mInterval = interval;
                schduleNextPoll(mInterval);
            }
        }
    }

    /**
     * The polling is stopped
     *
     * @return
     */
    protected boolean isStopped() {
        return mStopPolling;
    }

    /**
     * The polling callbacks
     *
     * @param interval
     * @param round
     * @param lastTime
     */
    protected void onPoll(int interval, final int round, final long lastTime) {
    }


    protected void onHandleIntent(Intent intent) {

        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            return;
        }

        if (ACTION_START_POLL.equals(action)) {
            ACTION_START_POLL(intent);
        } else if (ACTION_STOP_POLL.equals(action)) {
            ACTION_STOP_POLL(intent);
        }
    }

    private void ACTION_PAUSE_POLL(Intent intent) {

    }

    private void ACTION_START_POLL(Intent intent) {
        mStopPolling = false;
        int interval = intent.getIntExtra(KEY_INTERVAL, DEFAULT_POLL_INTERVAL);
        changeInterval(interval, true);
    }

    private void ACTION_STOP_POLL(Intent intent) {
        if (!mStopPolling) {
            mStopPolling = true;
            handler.removeMessages(MSG_NEXT_ROUND, null);
            mInterval = 0;
            mRound = 0;
            mLastPollTime = 0;
            onStopPolling();
        }
    }

    /**
     * 停止轮询时调用
     */
    protected void onStopPolling() {

    }

    protected void schduleNextPoll(int interval) {
        Message next = Message.obtain();
        next.what = MSG_NEXT_ROUND;
        next.arg1 = interval;
        handler.sendMessageDelayed(next, interval);
    }

    protected void pollAtOnce() {
        schduleNextPoll(-1);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        worker = new HandlerThread(name + ":worker");
        worker.start();
        handler = new InternalHandler(worker.getLooper());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        Message m = Message.obtain();
        m.what = MSG_COMMAND;
        m.obj = intent;
        handler.sendMessage(m);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy");
        if (worker != null) {
            worker.quit();
        }
    }

    protected void log(final String text) {
        Log.i(name, String.format("[%4d@%d] : %s", mRound, time(), text));
    }

    private long time() {
        return System.currentTimeMillis() / 1000;
    }
}
