package a.m.a.s.tools.apkdiff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by amas on 8/4/17.
 */

public class Proguard {
    Map<String, String> mapping = new HashMap<>();

    public static Proguard create(String file) {
        Proguard proguard = new Proguard();
        proguard.mapping = mapProguardMapping(file);
        return proguard;
    }


    static Pattern PROGUAD_MAPPING_ITEM = Pattern.compile("^([\\w.$]+)\\s+->\\s+([\\w.$]+)");

    public static Map<String, String> mapProguardMapping(String mappingFile) {
        final Map<String, String> map = new HashMap<>();
        File f = new File(mappingFile);
        if (!f.exists()) {
            return map;
        }

        readFile(f, new ReadlineCallback() {
            @Override
            public void onProcessLine(String line) {
                Matcher m = null;
                if ((m = PROGUAD_MAPPING_ITEM.matcher(line)).find()) {
                    String orig = m.group(1);
                    String mapped = m.group(2);
                    if (!orig.equals(mapped)) {
//                        System.out.println(orig.replace('.','/')+"   -   " +mapped.replace('.','/'));
                        map.put(mapped.replace('.','/'), orig.replace('.','/'));
                    }
                } else {
                }
            }
        });
        return map;
    }

    public static class ReadlineCallback {
        public void onProcessLine(String line) {

        }
    }

    public static void readFile(File path, ReadlineCallback callback) {
        BufferedReader b = null;
        try {
            b = new BufferedReader(new FileReader(path));
            String line = "";
            while ((line = b.readLine()) != null) {
                if (callback != null) {
                    callback.onProcessLine(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (b != null) {
                try {
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
