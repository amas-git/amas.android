package a.m.a.s.ego;

import android.app.Application;
import android.content.Context;
import android.os.Debug;

import a.m.a.s.DEBUG;

/**
 * Created by amas on 8/28/17.
 */

public class EgoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DEBUG.printStackTrace();
        Debug.stopMethodTracing();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Debug.startMethodTracing(base.getExternalFilesDir("mtrace").getAbsolutePath());
        DEBUG.printStackTrace();
    }
}
