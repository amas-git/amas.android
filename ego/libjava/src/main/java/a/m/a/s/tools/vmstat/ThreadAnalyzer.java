package a.m.a.s.tools.vmstat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import a.m.a.s.cs.LongRunningThread;
import a.m.a.s.er.ErFileWriter;
import a.m.a.s.tools.OS;
import a.m.a.s.tools.console.TConsole;
import a.m.a.s.utils.T;

/**
 * Created by amas on 8/31/17.
 */

public class ThreadAnalyzer extends Thread {
    public ThreadAnalyzer(String packageName) {
        this.pn = packageName;
    }

    ThreadPoolExecutor workers = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
    String sessionId = "";
    String pn = "";
    HashMap<String, ProcStat> prev = null;

    volatile boolean stop = true;

    private static HashMap<String, ScanThread> sessions = new HashMap<>();
    private File target = null;

    static class ScanThread extends LongRunningThread {
        String packageName = "";
        ThreadAnalyzer ta = null;
        long interval = 1000;

        public ScanThread(String packageName) {
            this.packageName = packageName;
            this.ta = new ThreadAnalyzer(packageName);
        }

        @Override
        public void start() {
            super.start();
            ta.start();
        }

        @Override
        protected void onLoop() {
            ta.update();
            OS.SLEEP(interval);
        }

        @Override
        public void shutdown() {
            super.shutdown();
            ta.shutdown();
        }
    }

    public static void startSession(String packageName) {
        ScanThread st = new ScanThread(packageName);
        sessions.put(packageName, st);
        st.start();
    }

    public static void stopSession(String packageName) {
        if(null != packageName) {
            ScanThread st = sessions.get(packageName);
            if (st != null) {
                st.shutdown();
            }
        } else {
            for(ScanThread ts : sessions.values()) {
                ts.shutdown();
            }
        }
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                Runnable r = queue.take();
                if (r instanceof AnalyzerTask) {
                    workers.execute(r);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public synchronized void start() {
        this.sessionId = pn + "." + now_yyMMdd() + getVersionCode(pn);
        this.target = new File(this.sessionId+".ts");
        System.out.println(TConsole.COLOR_GREEN("THREAD SCOPE START SESSION : " + sessionId));

        if (target.exists()) {
            target.delete();
        }
        stop = false;
        writter.start();
        writter.push(ErFileWriter.WriteObject.createWithTimeOption(target, ProcStat.HEADER));
        super.start();
    }



    public static ErFileWriter writter = new ErFileWriter();

    class AnalyzerTask implements Runnable {
        String data = "";
        String packageName = "";
        boolean stop = false;


        public AnalyzerTask(String packageName, String data, boolean stop) {
            this.packageName = packageName;
            this.data = data;
            this.stop = stop;
        }

        @Override
        public void run() {
            doAnalyzer(data);
        }


        private void doAnalyzer(String data) {
            HashMap<String, ProcStat> current = ProcStat.createMore(data);
            if (prev == null) {
                prev = current;
                return;
            }

            prev = next(target, prev, current, stop);
            if (stop) {
                setStop();
                onFinished();
            }
        }
    }

    protected void onFinished() {
        System.out.println(TConsole.COLOR_CYAN(String.format("THREAD SCOP STOPED SESSION %s", sessionId)));
        //writter.push();
    }

    protected void setStop() {
        this.stop = true;
    }


    static HashMap<String,ProcStat> cache = new HashMap<>();

    static class ThreadLogTask {
        List<ProcStat> data = new ArrayList<ProcStat>();

        public void add(long endTime, ProcStat one) {
            one.endTime(endTime);
            data.add(one);


            ProcStat dupcheck = cache.get(one.uuid);
            if(dupcheck == null) {
                cache.put(one.uuid, one);
            } else {
                System.err.println("== DUP ");
                System.err.println("==   1." + dupcheck);
                System.err.println("==   2." + one);
            }
        }

        public boolean isEmpty() {
            return data.isEmpty();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            boolean isFirst = true;
            for (ProcStat x : data) {
                if (isFirst) {
                    isFirst = false;
                    sb.append(x.toString());
                    continue;
                }
                sb.append('\n').append(x.toString());
            }
            return sb.toString();
        }
    }

    private static HashMap<String, ProcStat> next(File target, HashMap<String, ProcStat> prev, HashMap<String, ProcStat> current, boolean stop) {
        ThreadLogTask logtask = new ThreadLogTask();
        long now = System.currentTimeMillis();

        for (String uuid : prev.keySet()) {
            ProcStat _p = current.get(uuid);
            if (_p == null) {
                ProcStat p = prev.get(uuid);
                logtask.add(now, p);
                TConsole.printlnB("- : " + p);
            } else {
                //System.out.println("NOT CHANGED: " + p);
                ProcStat p = prev.get(uuid);
                _p.age(p.age() + 1);
                if (_p.cpuTime() > p.cpuTime()) {
                    _p.uage(p);
                    //System.out.println("* : " + _p);
                }
                if (stop) {
                    //System.err.println(" * " + p);
                    logtask.add(now, p);
                }
            }
        }

        for (String c : current.keySet()) {
            ProcStat p = current.get(c);
            if (!prev.containsKey(c)) {
                //System.err.println("+ : " + p);
                TConsole.printlnC("+ " + p);
                if (stop) {
                    logtask.add(now, p);
                }
            }
        }

        if (!logtask.isEmpty()) {
            writter.push(ErFileWriter.WriteObject.createWithTimeOption(target, logtask));
        }

        return current;
    }


    public void shutdown() {
        update(true);
    }

    public void update() {
        update(false);
    }

    protected void update(boolean stop) {
        String threadsInfo = getProcessThreadStat(pn);
        if (threadsInfo != null && threadsInfo.length() > 0) {
            queue.add(new AnalyzerTask(pn, threadsInfo, stop));
        } else {
            System.err.println("TARGET PROC NOT FOUND : " + pn);
        }
    }

    public static final String cgroup = "x=$(cat /proc/$pid/cgroup | grep bg_non_interactive); cgroup=BG; [ \"$x\" == '' ] && cgroup=FG;";
    public static final String SH_LIST_TASKS = "for pid in %s; do pname=$(cat /proc/$pid/cmdline); %s for x in /proc/$pid/task/*; do echo $pname $pid $cgroup $(cat $x/stat); done done";

    public static String getProcessThreadStat(String packagename) {
        //long start = System.currentTimeMillis();
        Map<String, String> pidInfo = OS.getPids(packagename);
        if (pidInfo.isEmpty()) {
            return "";
        }

        String script = String.format(SH_LIST_TASKS, T.join(' ', pidInfo.values()), cgroup);
        String result = OS.adb_shell_batch(script);
        return result;
    }

    static String now_yyMMdd() {
        return new SimpleDateFormat("yyMMdd").format(new Date(System.currentTimeMillis()));
    }


    public static String getVersionCode(String pn) {
        Pattern regex = Pattern.compile(".*versionCode=(\\d+).*");
        String text = OS.adb_shell_batch("dumpsys package " + pn + " | grep version");
        Matcher m = regex.matcher(text.replace("\n", " "));
        if (m.matches()) {
            return ".V" + m.group(1);
        }
        return "";
    }
}
