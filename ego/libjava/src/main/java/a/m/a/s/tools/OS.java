package a.m.a.s.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by amas on 8/30/17.
 */

public class OS {

    public static String adb_shell_batch(String script) {
        return exec(new String[]{"adb","shell",script});
    }

    public static String exec(String cmd) {
        //System.err.println("[exec] : " + cmd);
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStream stdin = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            proc.waitFor();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String exec(String[] args) {
        //System.err.println("[exec] : " + cmd);
        try {
            Process proc = Runtime.getRuntime().exec(args);
            InputStream stdin = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            proc.waitFor();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String SHELL = "adb shell ";
    public static Map<String,String> getPids(String packageName) {
        String stdout = exec(SHELL+" ps " + packageName);
        Map<String,String> pids = new TreeMap<>();

        if(stdout != null) {
            String[] xs = stdout.split("\n");
            for(String x: xs) {
                if(x.startsWith("USER")) {
                    continue;
                }
                String[] yx = x.split("\\s+");
                if(yx != null && yx.length == 9) {
                    pids.put(yx[8], yx[1]);
                }
            }
        }

        return pids;
    }


    public static void SLEEP(long msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
