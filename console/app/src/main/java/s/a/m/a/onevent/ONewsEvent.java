package s.a.m.a.onevent;

import android.os.Message;

/**
 * Created by amas on 11/13/15.
 */
public class ONewsEvent {
    public static final int MSG_ID_EVENT = 20140508;
    protected long sendTime = 0;
    protected long recvTime = 0;
    protected long doneTime = 0;
    protected int proccessTimes = 0;

    public void updateRecvTime() {
        if (recvTime == 0) {
            recvTime = System.currentTimeMillis();
        }
    }

    public static boolean isEventMessage(final Message msg) {
        return msg.what == ONewsEvent.MSG_ID_EVENT && msg.obj != null && msg.obj instanceof ONewsEvent;
    }

    public void updateDoneTime() {
        proccessTimes++;
        if (doneTime == 0) {
            doneTime = System.currentTimeMillis();
        }
    }

    /**
     * 投递到主线成中
     * TODO:
     * 1. 支持DELAY SENDING
     * 2. 支持BLOCK ON DEMOND, 诸如播放动画的时候可以先缓存消息, 播放完成后再进行投递
     */
    public void send() {
        sendTime = System.currentTimeMillis();
        ONewsEventManager.getInstance().sendEvent(this);
    }

    @Override
    public String toString() {
        return String.format("(:LIFE-TIME %d :IN-QUEUE %d :WORK-TIME %d)", (doneTime - sendTime), (recvTime - sendTime), (doneTime - recvTime));
    }
}
