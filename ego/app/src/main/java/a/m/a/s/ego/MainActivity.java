package a.m.a.s.ego;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.FileObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import a.m.a.s.ego.custom.Init;
import a.m.a.s.utils.IO;

public class MainActivity extends AppCompatActivity {
    TextView tv_message = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_message = (TextView) findViewById(R.id.tv_message);

        // Init is build flavor
        //addMessage(CN.hello());
        System.out.println("hello");
        addMessage("BUILD_TYPE_INNER="+BuildConfig.BUILD_TYPE_INNER);
        addMessage("_FLAVOR="+BuildConfig._FLAVOR);
        addMessage(getPackageName());
        addMessage("ID="+new Init().getId());
        addMessage("meta-data:applicationId = " + getMetaString(this, "applicationId",""));
        addMessage("meta-data:hostName = " + getMetaString(this, "hostName",""));


        new ProcCheckerThread().start();
        //new PROC_CGROUP_OBSERVER().startWatching();
        int i = 1 + 1;
    }


    public void addMessage(String text) {
        tv_message.append(text + "\n");
    }


    public static String getMetaString(Context context, String name, String fail) {
        String value = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            value = bundle.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            value = fail;
            e.printStackTrace();
        } catch (NullPointerException e) {
            value = fail;
            e.printStackTrace();
        }
        return value;
    }

    public static class PROC_CGROUP_OBSERVER extends FileObserver {
        /* FileObserver 无法监控文件只能监控目录级别 */
        public PROC_CGROUP_OBSERVER() {
            this("/proc/self/cgroup");
        }
        public PROC_CGROUP_OBSERVER(String path) {
            super(path);
        }

        @Override
        public void onEvent(int i, String s) {
            log(":"+i+" | " + s);
        }
    }


    static class ProcCheckerThread extends Thread {
        volatile boolean stop = false;
        long interval= 500;
        public ProcCheckerThread() {
            super("ProcCheckerThread:"+System.currentTimeMillis());
        }

        @Override
        public void run() {
            while (!stop) {
                log(""+android.os.Process.getElapsedCpuTime()+"-> " + IO.head(new File("/proc/self/cgroup")) + " oom_score="+IO.head(new File("/proc/self/oom_score")));
                SLEEP(interval);
            }
        }

        public void shutDown() {
            stop = true;
        }
    }

    static void log(String msg) {
        android.util.Log.i("ego", msg);
    }


    static void SLEEP(long msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void beFg(Context context) {

    }
}
