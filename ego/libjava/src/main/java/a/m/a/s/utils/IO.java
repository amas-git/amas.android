package a.m.a.s.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by amas on 8/24/17.
 */

public class IO {
//    public static String cat(Path target) {
//        try {
//            return new String(Files.readAllBytes(target));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }


    public static String cat(File file) {
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static ArrayList<String> catAsList(File file) {
        ArrayList<String> xs = new ArrayList();
        String line = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                xs.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return xs;
    }

    public static String head(File file) {
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                sb.append(line);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
