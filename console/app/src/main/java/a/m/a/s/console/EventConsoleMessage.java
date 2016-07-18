package a.m.a.s.console;


/**
 * Created by amas on 1/21/16.
 */
public class EventConsoleMessage {
    Console.ConsoleMessage message = null;

    public EventConsoleMessage(String msg) {
        this.message = new Console.ConsoleMessage();
        message.message = msg;
    }

    public EventConsoleMessage(Console.ConsoleMessage msg) {
        message = msg;
    }


    public EventConsoleMessage setMessage(String msg) {
        message.message = msg;
        return this;
    }

    public Console.ConsoleMessage message() {
        return this.message;
    }
}
