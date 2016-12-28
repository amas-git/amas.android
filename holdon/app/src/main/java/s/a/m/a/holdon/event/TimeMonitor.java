package s.a.m.a.holdon.event;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import s.a.m.a.sched.sched.TimeUtils;

/**
 * Created by amas on 15-6-28.
 */
public class TimeMonitor {
    private ScheduledExecutorService scheduler = null;
    private static TimeMonitor sInstance = null;

    private TimeMonitor() {
    }

    public static TimeMonitor getInstance() {
        if (sInstance == null) {
            synchronized (TimeMonitor.class) {
                if (sInstance == null) {
                    sInstance = new TimeMonitor();
                }
            }
        }
        return sInstance;
    }

    public static class TimeMonitorRunable implements Runnable {
        int currentDay = 0;

        @Override
        public void run() {
            //System.out.println("---------------> " + TimeUtils.now());
            long now = TimeUtils.now();
            if(currentDay == 0) {
                currentDay = TimeUtils.ofInt_YYYYMMDD(now);
                return;
            }

            int _currentDay = TimeUtils.ofInt_YYYYMMDD(now);
            if(currentDay != _currentDay) {
                onDayChanged(currentDay, _currentDay); //   System.out.println("---------> " + y + "-" + m + "-" + d);
                currentDay = _currentDay;
            }
        }

        private void onDayChanged(int from, int to) {
            //System.out.println(String.format("onDayChanged : %d -> %d", from, to));
            EventDayChanged event = new EventDayChanged(from, to);
            EVENT.send(event);
        }
    }
    public synchronized void start() {
        if(scheduler == null) {
            scheduler = Executors.newScheduledThreadPool(1);
        }
        scheduler.scheduleAtFixedRate(new TimeMonitorRunable(), 0, 1, TimeUnit.SECONDS);
    }

    public synchronized void stop() {
        scheduler.shutdown();
        scheduler = null;
    }

}
