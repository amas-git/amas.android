package s.a.m.a;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import s.a.m.a.sched.sched.TimeUtils;

/**
 * Created by amas on 15-6-6.
 */
public class L {

    public static String toString(long[] xs) {
        Vector<Long> _xs = new Vector<>();
        for(long x : xs) {
           _xs.add(x);
        }

        return _xs.toString();
    }

    private static String[] PADDINGS = new String[128];
    static {
        PADDINGS[0] = padding(0);
        PADDINGS[1] = padding(1);
        PADDINGS[2] = padding(2);
        PADDINGS[3] = padding(3);
        PADDINGS[4] = padding(4);
    }

    private static String PADDING_STRING = "  ";

    private static String padding(int n) {
        if(n > 127) {
            return "";
        }
        String p = PADDINGS[n];
        if(p == null) {
            PADDINGS[n] = repeat(n, PADDING_STRING);
        }
        return p;
    }

    private static String repeat(int n, String s) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<n; ++i) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String addPadding(String text, int n) {
        StringBuilder sb = new StringBuilder();
        String[] xs = text.split("\n");
        for(String x : xs) {
            sb.append(padding(n)).append(x).append("\n");
        }
        return sb.toString();
    }

}
