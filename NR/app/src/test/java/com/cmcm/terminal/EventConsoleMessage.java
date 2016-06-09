package com.cmcm.terminal;


import com.cmcm.onews.event.ONewsEvent;

/**
 * Created by amas on 1/21/16.
 */
public class EventConsoleMessage extends ONewsEvent {
    Console.Message message = null;

    public EventConsoleMessage(String msg) {
        this.message = new Console.Message();
        message.message = msg;
    }

    public EventConsoleMessage(Console.Message msg) {
        message = msg;
    }


    public EventConsoleMessage setMessage(String msg) {
        message.message = msg;
        return this;
    }

    public Console.Message message() {
        return this.message;
    }
}
