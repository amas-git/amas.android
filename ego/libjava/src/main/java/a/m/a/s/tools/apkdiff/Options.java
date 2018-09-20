package a.m.a.s.tools.apkdiff;

import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amas on 8/11/17.
 */

public class Options {
    @Parameter
    public List<String> parameters = new ArrayList<>();

    @Parameter(names = {"--log", "--verbose"}, description = "Level of verbosity")
    public Integer verbose = 1;

    @Parameter(names = "--groups", description = "Comma-separated list of group names to be run")
    public String groups;

    @Parameter(names = "--debug", description = "Debug mode")
    public boolean debug = false;

    @Parameter(names = {"--help", "-h"}, description = "Show help message")
    public boolean help = false;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("debug="+debug).append('\n');
//        sb.append("APK.1="+getFirstApk()).append('\n');
//        sb.append("APK.2="+getSecondApk()).append('\n');
        sb.append(parameters.size()).append('\n');
        return sb.toString();
    }

    public static final File EMPTY = new File("");
    public File getFirstApk() {
        if(parameters.isEmpty()) {
            return EMPTY;
        }

        File f = new File(parameters.get(0));
        return f;
    }

    public File getSecondApk() {
        File f = null;
        try {
            f = new File(parameters.get(1));
        } catch (Exception e) {
            e.printStackTrace();
            f = EMPTY;
        }
        return f;
    }

    public boolean isValidate() {
        return parameters.size() != 0;
    }
}
