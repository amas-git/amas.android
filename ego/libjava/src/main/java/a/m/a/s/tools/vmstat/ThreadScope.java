package a.m.a.s.tools.vmstat;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import a.m.a.s.tools.OS;
import a.m.a.s.tools.console.TConsole;
import a.m.a.s.tools.console.TConsoleCommand;
import a.m.a.s.utils.T;

/**
 * Created by amas on 8/30/17.
 *
 * 1. 进程相关
 */

public class ThreadScope {
    static final String VERSION = "1.0.0";
    static TConsole console = new TConsole();


    private static final Pattern FQCN = Pattern.compile("[a-z-_A-Z0-1]+\\.[a-z-_A-Z0-1].*");


    static class threadscope extends TConsoleCommand {
        public threadscope() {
            super("ts");
        }

        @Parameter
        public List<String> parameters = new ArrayList<>();

        @Parameter(names={"-t"}, description = "监控时间(N(d/m/h/s))")
        public String timeSpec = "";

        public List<String> getPackages() {
            ArrayList<String> px = new ArrayList<>();
            for(String s : parameters) {
                Matcher m = FQCN.matcher(s);
                if(m.matches()) {
                    px.add(s);
                }
            }
            return px;
        }

        @Override
        public void exec(String[] argv) {
            JCommander.newBuilder()
                    .addObject(this)
                    .build()
                    .parse(argv);

            if(parameters.size() == 1) {
                System.out.println(OS.adb_shell_batch("pm list package -3 | cut -d: -f2"));
                return;
            }

            String subcmd = parameters.get(1);
            long stopTime = T.parseTime(timeSpec);

            if ("start".equals(subcmd)) {
                cmd_start(getPackages(), stopTime);
            } else if ("stop".equals(subcmd)) {
                cmd_stop();
            } else {

            }
        }

        public void cmd_start(List<String> pns, long stopTime) {
            for (final String pn : pns) {
                ThreadAnalyzer.startSession(pn);
                if (stopTime > 0) {
                    TimerTask stoptask = new TimerTask() {

                        @Override
                        public void run() {
                            cmd_stop();
                        }
                    };

                    Timer timer = new Timer(true);
                    timer.schedule(stoptask, stopTime);
                }

            }
        }


        public void cmd_stop() {
            ThreadAnalyzer.stopSession(null);
        }
    }

    static class listpackage extends TConsoleCommand {

        public listpackage() {
            super("listpackage");
        }

        @Override
        public void exec(String[] argv) {
            System.out.println(OS.adb_shell_batch("pm list package -3 | cut -d: -f2"));
        }
    }

    public static final String WELCOME =
              "HELLO "
                      + VERSION               +"\n"
                      + "=========================================\n"
                      + "使用前请确保adb命令在PATH中, 并且已USB连接手机\n"
                      + "1. ts      : 列出可以监控的包\n"
                      + "2. ts start <package-name> -t 12h" + "\n"
                      + "   * -t: h=hour d=day s=second m=minute\n"
                      + "3. ts stop : 手动停止监控\n"
                      + "=========================================\n"
                      + "\n";

    public static void TEST(String[] argv) {
        console.setWelcome(TConsole.COLOR_CYAN(WELCOME));
        console.addCommand(new threadscope());
        console.addCommand(new listpackage());
        console.start();
    }
}
