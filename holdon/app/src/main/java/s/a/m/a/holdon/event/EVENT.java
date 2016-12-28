package s.a.m.a.holdon.event;

import client.core.Core;
import client.core.model.Event;
import client.core.model.Notifiers;

/**
 * Created by amas on 6/8/15.
 */
public class EVENT {
    public static final Notifiers GROUP_UI = new Notifiers("ui");

    public static void send(Event event) {
        event.setTo(GROUP_UI);
        Core.I().push(event);
    }
}
