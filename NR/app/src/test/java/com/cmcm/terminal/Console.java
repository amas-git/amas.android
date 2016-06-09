package com.cmcm.terminal;

/**
 * Created by amas on 1/16/16.
 */
public class Console {
    private static Console sInstance = null;

    public static short TYPE_WARN  = 100;
    public static short TYPE_ERROR = 101;
    public static short TYPE_INFO  = 102;

    public static class Message {
        String message = "";
        short type = TYPE_INFO;
        String tag = "";
    }

    private Console() {
    }

    private MessageListener mMessageListener = null;

    public void setMessageListener(MessageListener l) {
        this.mMessageListener = l;
    }
    public static Console getInstance() {
        if (sInstance == null) {
            synchronized (Console.class) {
                if (sInstance == null) {
                    sInstance = new Console();
                }
            }
        }
        return sInstance;
    }


    CircularBuffer<Message> messages = new CircularBuffer<>(512);

    public interface MessageListener {
        public void onNewMessage(Message message);
    }

    /**
     * 性能问题,可能会阻塞
     * @param tag
     * @param message
     */
    public void write(String tag, final String message) {
        w(tag, message);
    }

    protected synchronized void w(String tag, final String message) {
        Message m = new Message();
        m.message = message;
        m.tag = tag;

        messages.put(m);
        if(mMessageListener != null) {
            mMessageListener.onNewMessage(m);
        }
    }

    public synchronized CircularBuffer<Message> getAllMessages() {
        return messages.copy();
    }

}
