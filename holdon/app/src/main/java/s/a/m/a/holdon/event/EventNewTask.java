package s.a.m.a.holdon.event;

import client.core.model.Event;
import s.a.m.a.holdon.HTask;

/**
 * Created by amas on 6/8/15.
 */
public class EventNewTask extends Event{
    public HTask htask = null;

    public EventNewTask(HTask hTask) {
        htask = hTask;
    }
}
