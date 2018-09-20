package a.m.a.s.tools.console;

import java.util.HashMap;
import java.util.Scanner;

import a.m.a.s.tools.OS;
import a.m.a.s.utils.T;

/**
 * Created by amas on 9/4/17.
 */

public class TConsole extends Thread {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static String RESET() {
        return ANSI_RESET;
    }

    public static String COLOR_RED(String text) {
        return ANSI_RED + text + ANSI_RESET;
    }

    public static String COLOR_CYAN(String text) {
        return ANSI_CYAN + text + ANSI_RESET;
    }

    public static String COLOR_GREEN(String text) {
        return ANSI_GREEN + text + ANSI_RESET;
    }

    public static void printlnG(String text) {
        System.out.println(ANSI_GREEN + text + ANSI_RESET);
    }

    public static void printlnR(String text) {
        System.out.println(ANSI_RED + text + ANSI_RESET);
    }

    public static void printlnB(String text) {
        System.out.println(ANSI_BLUE + text + ANSI_RESET);
    }

    public static void printlnC(String text) {
        System.out.println(ANSI_CYAN + text + ANSI_RESET);
    }

    public static void printlnY(String text) {
        System.out.println(ANSI_YELLOW + text + ANSI_RESET);
    }

    public static void printlnP(String text) {
        System.out.println(ANSI_PURPLE + text + ANSI_RESET);
    }

    HashMap<String,TConsoleCommand> cmds = new HashMap<>();
    boolean stop = false;
    String welcome = null;
    @Override
    public void run() {
        while (!stop) {
            loop();
        }
    }

    public void setWelcome(String welcome) {
        this.welcome = welcome;
    }

    public TConsole addCommand(TConsoleCommand command) {
        cmds.put(command.name(), command);
        return this;
    }

    public void shutdown() {
        stop = true;
    }

    String PROMT = "> ";
    private void loop() {
        Scanner sc = new Scanner(System.in);
        String line = "";
        if(welcome  != null) {
            System.out.println(welcome);
        }
        System.out.print(COLOR_CYAN(PROMT));
        while(sc.hasNext()){
            line = sc.nextLine();
            String[] argv = line.trim().split(" ");
            if(onExectueCommand(argv)) {
                printlnR("TCONSOLE EXIT!");
                break;
            }
            System.out.print(COLOR_CYAN(PROMT));
        }
    }

    private boolean onExectueCommand(String[] argv) {
        if(T.isEmpty(argv)) {
            return false;
        }

        String cmd = argv[0].trim();
        //System.err.println("cmd="+cmd);
        if(T.isEmpty(cmd)) {
            return false;
        }
        if("exit".equals(cmd)){
            shutdown();
            OS.SLEEP(5000);
            return true;
        }

        TConsoleCommand tcmd = cmds.get(cmd);
        if(tcmd != null) {
            tcmd.exec(argv);
        } else {
            printlnR("NOT FOUND COMMAND : " + cmd);
        }
        return false;
    }
}
