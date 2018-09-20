package a.m.a.s.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import a.m.a.s.tools.apkdiff.ApkDiff;
import a.m.a.s.tools.apkdiff.Dex;

/**
 * Created by amas on 8/1/17.
 */

public class ZipUtils {
    public static Map<String, ZipEntry> listZipEntries(String file) {
        return listZipEntries(new File(file));
    }

    public static Map<String, ZipEntry> listZipEntries(File file) {
        TreeMap<String, ZipEntry> entries = new TreeMap();

        try {
            ZipFile zip = new ZipFile(file);
            Enumeration zipEntries = zip.entries();
            while (zipEntries.hasMoreElements()) {
                ZipEntry x = (ZipEntry) zipEntries.nextElement();
                entries.put(x.getName(), x);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return entries;
    }

    public static String getZipArchiveComment(String file) {
        try {
            ZipFile zip = new ZipFile(file);
            return zip.getComment(); // This is JAVA7 mathods
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setComment(String file, String comment) {
        ZipOutputStream os = null;
        try {
            os = new ZipOutputStream(new FileOutputStream(file));
            os.setComment(comment);
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void diff(File zipL, File zipR, ApkDiff.ZipDiffListener listener) {
        if (zipL != null && zipR != null && zipL.equals(zipR)) {
            return;
        }

        Map<String, ZipEntry> l = listZipEntries(zipL);
        assert l != null;
        Map<String, ZipEntry> r = listZipEntries(zipR);
        assert r != null;


        Set<String> xs = new HashSet<>();
        xs.addAll(l.keySet());
        xs.addAll(r.keySet());

        for (String x : xs) {
            if (listener != null) {
                listener.onDiffEntry(x, l.get(x), r.get(x));
            }
        }

    }

    public static List<String> getFileName(File apkFile, String regex) {
        ArrayList xs = new ArrayList(4);
        Map<String, ZipEntry> entries = listZipEntries(apkFile);
        for(ZipEntry x : entries.values()) {
            if(x.getName().matches(regex)) {
                xs.add(x.getName());
            }
        }
        return xs;
    }
}
