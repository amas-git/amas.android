package a.m.a.s.console;


/**
 * Created by amas on 1/21/16.
 */
public class EventConsoleMessage {
    ConsoleMessage message = null;

    public EventConsoleMessage(String msg) {
        this.message = new ConsoleMessage();
        message.message = msg;
    }

    public EventConsoleMessage(ConsoleMessage msg) {
        message = msg;
    }


    public EventConsoleMessage setMessage(String msg) {
        message.message = msg;
        return this;
    }

    public ConsoleMessage message() {
        return this.message;
    }
}
