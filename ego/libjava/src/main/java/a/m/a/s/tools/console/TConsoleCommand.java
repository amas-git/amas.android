package a.m.a.s.tools.console;

import a.m.a.s.utils.T;

/**
 * Created by amas on 9/4/17.
 */

public abstract class TConsoleCommand {
    public TConsoleCommand(String name) {
        this.name = name;
    }

    String name = null;

    public abstract void exec(String[] argv);


    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("TConsoleCommand: "+ name);
    }
}
