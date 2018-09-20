package a.m.a.s.cs;

/**
 * Created by amas on 9/5/17.
 */

public abstract class LongRunningThread extends Thread {
    volatile boolean stop = false;

    @Override
    public void run() {
        while (!stop && !isInterrupted() ) {
            onLoop();
        }
    }

    protected abstract void onLoop();

    public void start() {
        synchronized (this) {
            stop = false;
        }
        super.start();
    }

    public void shutdown() {
        synchronized (this) {
            stop = true;
            //interrupt();
        }
    }
}
