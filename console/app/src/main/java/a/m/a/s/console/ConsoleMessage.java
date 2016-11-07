package a.m.a.s.console;

public class ConsoleMessage {
    public static ConsoleMessage create(String message) {
        ConsoleMessage m = new ConsoleMessage();
        m.message = message;
        return m;
    }

    String message = "";
    short type = Console.TYPE_INFO;
    String tag = "";
    long time = System.currentTimeMillis();
}