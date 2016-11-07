package a.m.a.s.console;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by amas on 8/4/16.
 */
public class ConsoleCommand implements Cloneable {
    public String name = "";
    public ArrayList<String> argv = new ArrayList<>(0);

    public ConsoleCommand(String name) {
        if(TextUtils.isEmpty(name)) {
            throw new IllegalStateException("");
        }
        this.name = name;
    }

    public ConsoleMessage exec(ArrayList<String> argv) {
        return null;
    }

    public final ConsoleMessage execWithProfile() {
        long time = System.currentTimeMillis();
        ConsoleMessage m = exec(argv);
        time = System.currentTimeMillis() - time;
        return m;
    }

    @Override
    protected ConsoleCommand clone() {
        ConsoleCommand o =  null;
        try {
            o = (ConsoleCommand)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }

    public static ConsoleCommand parse(String commandLine) {

        return  null;
    }

    public void setArgv(String[] argv) {
       for(String x : argv) {
           this.argv.add(x);
       }
    }
}
