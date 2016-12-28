package s.a.m.a.holdon.event;

import client.core.model.Event;

/**
 * Created by amas on 15-6-28.
 */
public class EventDayChanged extends Event {
    private int from;
    private int to;
    //public long now = -1L;

    public EventDayChanged(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int now_YYYYmmdd() {
        return to;
    }

    @Override
    public String toString() {
        return String.valueOf(to);
    }
}
