package s.a.m.a.onevent;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by amas on 5/19/16.
 */
public class ONewsEventManager {
    HandlerThread mTH;
    Handler mH;

    private static ONewsEventManager sInstance = null;

    private ONewsEventManager() {
        restart();
    }

    Collection<IEventListener> stack = Collections.asLifoQueue(new LinkedBlockingDeque<IEventListener>());

    public synchronized void restart() {
        // TODO: 不是很严格的重启逻辑
        if (mH == null || mTH == null) {
            mTH = new HandlerThread("ONewsEventManager", Thread.NORM_PRIORITY);
            mTH.start();
            mH = new Handler(mTH.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (ONewsEvent.isEventMessage(msg)) {
                        onHandleEvent((ONewsEvent) msg.obj);
                    }
                }
            };
        }
    }

    public void addEventListener(IEventListener l) {
        if (!stack.contains(l)) {
            stack.add(l);
        }
    }

    public void removeEventListener(IEventListener l) {
        try {
            stack.remove(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onHandleEvent(ONewsEvent event) {
        for (IEventListener x : stack) {
            x.onHandleEvent(event);
        }
    }

    public static ONewsEventManager getInstance() {
        if (sInstance == null) {
            synchronized (ONewsEventManager.class) {
                if (sInstance == null) {
                    sInstance = new ONewsEventManager();
                }
            }
        }
        return sInstance;
    }

    public void sendEvent(ONewsEvent event) {
        Message m = Message.obtain();
        m.what = ONewsEvent.MSG_ID_EVENT;
        m.obj = event;
        mH.sendMessage(m);
    }

    public void sendEvent(ONewsEvent event, long delay) {
        Message m = Message.obtain();
        m.what = ONewsEvent.MSG_ID_EVENT;
        m.obj = event;
        mH.sendMessageDelayed(m, delay);
    }
}
