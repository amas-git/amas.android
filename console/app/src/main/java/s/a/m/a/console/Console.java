package s.a.m.a.console;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import s.a.m.a.cs.CircularBuffer;

/**
 * Created by amas on 1/16/16.
 */
public class Console {
    private static Console sInstance     = null;
    private static final int BUFFER_SIZE = 512;

    public static short TYPE_WARN  = 100;
    public static short TYPE_ERROR = 101;
    public static short TYPE_INFO  = 102;
    public static short TYPE_INTERNAL_ERROR = 911;
    boolean isEnabled = true;

    public static class ConsoleMessage {
        String message = "";
        short type = TYPE_INFO;
        String tag = "";
        long time = System.currentTimeMillis();
    }

    private Console() {
        prepare();
    }

    /**
     * 关闭控制台
     */
    public synchronized void disable() {
        if(isEnabled) {
            isEnabled = false;
            cleanup();
        }
    }

    /**
     * 打开控制台
     */
    public synchronized void enabled() {
        if(!isEnabled) {
            isEnabled = true;
            prepare();
        }
    }

    protected synchronized void cleanup() {
        if(mHT != null) {
            mHT.quit();
            mHT = null;
        }

        if(mH  != null) {
            mH = null;
        }

        messages = new CircularBuffer<>(BUFFER_SIZE);
    }

    private MessageListener mMessageListener = null;

    public void setMessageListener(MessageListener l) {
        this.mMessageListener = l;
    }

    public void removeMessageListener(MessageListener l) {
        this.mMessageListener = null;
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

    HandlerThread mHT = null;
    Handler mH = null;

    private synchronized void prepare() {
        mHT = new HandlerThread("console");
        mHT.start();
        mH  = new Handler(mHT.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_ID_CONSOLE: {
                        ConsoleMessage m = (ConsoleMessage) msg.obj;
                        messages.put(m);
                        if (mMessageListener != null) {
                            try {
                                mMessageListener.onNewMessage(m);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };

    }


    CircularBuffer<ConsoleMessage> messages = new CircularBuffer<>(BUFFER_SIZE);

    public interface MessageListener {
        public void onNewMessage(ConsoleMessage message);
    }

    /**
     * 性能问题,可能会阻塞
     * @param tag
     * @param message
     */
    public void write(String tag, final String message) {
        w(tag, message);
    }

    public void write(final String message) {
        write("main", message);
    }

    private static final int MESSAGE_ID_CONSOLE = 1983;
    protected synchronized void w(String tag, final String message) {
        if(isEnabled) {
            ConsoleMessage m = new ConsoleMessage();
            m.message = message;
            m.tag = tag;

            Message mm = mH.obtainMessage(MESSAGE_ID_CONSOLE);
            mm.obj = m;
            mm.sendToTarget();
        }
    }

    public synchronized CircularBuffer<ConsoleMessage> getAllMessages() {
        return messages.copy();
    }

}
