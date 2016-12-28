package s.a.m.a.holdon.event;

import client.core.model.Event;
import s.a.m.a.holdon.CheckInRecord;
import s.a.m.a.holdon.HTask;

/**
 * Created by amas on 15-5-31.
 */
public class EventCheckinResult extends Event {
    HTask htask = null;
    public EventCheckinResult() {

    }

    public static EventCheckinResult create(HTask htask) {
        EventCheckinResult event = new EventCheckinResult();
        event.htask = htask;
        return event;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + htask;
    }

    public HTask getHTask() {
        return htask;
    }
}
