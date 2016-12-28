package s.a.m.a.holdon;

import android.app.Application;
import android.content.Context;

import client.core.Core;
import client.core.model.Event;
import client.core.model.EventListener;
import s.a.m.a.holdon.event.EventDayChanged;
import s.a.m.a.service.LocalService;

/**
 * Created by amas on 15-5-17.
 */
public class GApplication extends Application implements EventListener {
    static Context context = null;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Core.I().addListener("ui", this);
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventDayChanged) {
            LocalService.start_ACTION_SCHED_ALL(this);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Core.I().removeListener(this);
    }
}
