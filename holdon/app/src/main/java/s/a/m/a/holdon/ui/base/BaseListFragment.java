package s.a.m.a.holdon.ui.base;


import android.support.v4.app.ListFragment;

import client.core.model.Event;

/**
 * Created by amas on 15-5-31.
 */
public class BaseListFragment extends ListFragment {

    public final void onHandleEvent(Event event) {
        if(isAdded()) {
            onEventInUiThread(event);
        }
    }

    protected void onEventInUiThread(Event event) {

    }
}
