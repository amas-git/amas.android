package a.m.a.s.console;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import a.m.a.s.apidemos.ui.BaseTracer;
import a.m.a.s.cs.CircularBuffer;


/**
 * Created by amas on 1/16/16.
 * Console - MessageHandler - Fragment
 *
 * TODO: 支持多BUFFER
 */
public class Console {
    private static Console sInstance     = null;
    private static final int BUFFER_SIZE = 512;

    private static final int MESSAGE_ID_CONSOLE = 1983;
    private static final int MESSAGE_ID_COMMAND = 1987;
    private static final int MESSAGE_ID_COMMAND_LINE = 1988;


    public static short TYPE_WARN  = 100;
    public static short TYPE_ERROR = 101;
    public static short TYPE_INFO  = 102;
    public static short TYPE_INTERNAL_ERROR = 911;


    public static AtomicInteger sConsoleNum = new AtomicInteger(0);
    public String name = null;
    boolean isEnabled = true;

    public Console(String name) {
        this.name = name + ":" + sConsoleNum.incrementAndGet();
        prepare(this.name);
    }

    public Console() {
        this.name = "console:" + sConsoleNum.incrementAndGet();
        prepare(this.name);
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
            prepare(this.name);
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

    private synchronized void prepare(String name) {
        mHT = new HandlerThread(name);
        mHT.start();
        mH  = new Handler(mHT.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(!isEnabled) {
                    return;
                }

                switch (msg.what) {
                    case MESSAGE_ID_CONSOLE: {
                        //-------------------------------------------[ handle message ]
                        ConsoleMessage m = (ConsoleMessage) msg.obj;
                        messages.put(m);
                        if (mMessageListener != null) {
                            try {
                                mMessageListener.onNewMessage(m);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }

                    case MESSAGE_ID_COMMAND: {
                        ConsoleCommand c = (ConsoleCommand) msg.obj;
                        ConsoleMessage m = c.execWithProfile();
                        if (mMessageListener != null) {
                            try {
                                mMessageListener.onNewMessage(m);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }

                    case MESSAGE_ID_COMMAND_LINE: {
                        String commandLine = (String) msg.obj;
                        ConsoleMessage m = null;
                        ConsoleCommand c = searchCommand(commandLine);
                        if(c != null) {
                            m = c.execWithProfile();
                        } else {
                            m = ConsoleMessage.create("Can't find the command : " + commandLine);
                        }

                        if (mMessageListener != null) {
                            try {
                                mMessageListener.onNewMessage(m);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                }
            }
        };

    }

    protected ConsoleCommand searchCommand(String commandLine) {
        String[] xs = commandLine.split(" ");
        if(xs.length == 0) {
            return null;
        }
        String name = xs[0];
        ConsoleCommand c = commands.get(name);
        if(c != null) {
            c = c.clone();
            c.name = name;
            c.setArgv(xs);
            return c;
        }

        return null;
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

    public synchronized void exec(ConsoleCommand cmd) {
        if(isEnabled) {
            ConsoleCommand m = cmd;

            Message mm = mH.obtainMessage(MESSAGE_ID_COMMAND);
            mm.obj = m;
            mm.sendToTarget();
        }
    }

    public synchronized void exec(String commandLine) {
        if(isEnabled) {
            Message mm = mH.obtainMessage(MESSAGE_ID_COMMAND_LINE);
            mm.obj = commandLine;
            mm.sendToTarget();
        }
    }

    static HashMap<String,ConsoleCommand> commands = new HashMap<>(0);

    public static void addCommand(ConsoleCommand command) {
        commands.put(command.name, command);
    }

    static int round = 1000;
    static {
        addCommand(new ConsoleCommand("pid") {
            @Override
            public ConsoleMessage exec(ArrayList<String> argv) {
                String result = ""+android.os.Process.myPid();
                return ConsoleMessage.create(result);
            }
        });



        addCommand(new ConsoleCommand("q") {
            @Override
            public ConsoleMessage exec(ArrayList<String> argv) {
                long t0 = System.nanoTime();

                BaseTracer b = new BaseTracer(30);
                for(int i=0; i<round; ++i) {
                    b.toInfocString();
                }
                return ConsoleMessage.create(""+(System.nanoTime()-t0));
            }
        });

        addCommand(new ConsoleCommand("w") {
            @Override
            public ConsoleMessage exec(ArrayList<String> argv) {
                long t0 = System.nanoTime();
                BaseTracer b = new BaseTracer(30);
                for(int i=0; i<round; ++i) {
                    b.toInfocString2();
                }
                return ConsoleMessage.create(""+(System.nanoTime()-t0));
            }
        });
    }
}
