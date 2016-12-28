package s.a.m.a.sched.sched;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by amas on 15-5-31.
 */
public class TimeUtils {
    public static long MINUTE = 60 * 1000;
    public static long ONE_DAY = 24 * 60 * 60 * 1000L;
    public static long WEEK  =  7 * 24 * 60 * 60 * 1000;
    public static long MONTH = 30 * 24 * 60 * 60 * 1000;

    static SimpleDateFormat yyyy_MM_dd = null;
    static SimpleDateFormat yyyyMMdd = null;
    static SimpleDateFormat yyyy_MM_dd_HH_mm_ss = null;

    /**
     * @param start
     * @param end
     * @return
     */
    public static long[] range_day(long start, long end) {
        int duration = (int) ((end - start) / ONE_DAY) + 1;
        //android.util.Log.i("htask", "start="+start+" end="+end + " duation="+duration + " " + TimeUtils.yyyy_MM_dd_HH_mm_ss(start)+" -- "+ TimeUtils.yyyy_MM_dd_HH_mm_ss(end));
        long[] range = new long[duration];
        if (duration == 0) {
            return range;
        }

        for (int i = 0; i < duration; ++i) {
            range[i] = start + i * ONE_DAY;
        }
        return range;
    }

    public synchronized static String yyyy_MM_dd(long time) {
        if (yyyy_MM_dd == null) {
            yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
        }
        return yyyy_MM_dd.format(new Date(time));
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public synchronized static String yyyy_MM_dd_HH_mm_ss(long time) {
        if (yyyy_MM_dd_HH_mm_ss == null) {
            yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }

        return yyyy_MM_dd_HH_mm_ss.format(new Date(time));
    }

    public synchronized static Date yyyyMMdd(String date) {
        if (yyyyMMdd == null) {
            yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
        }

        Date d = null;

        try {
            d = yyyyMMdd.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    /**
     * 去除时分秒
     * @param time
     * @return
     */
    public static long normalizeTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND,0);
        return  c.getTimeInMillis();
    }

    public static long normalizeNow() {
        return normalizeTime(now());
    }

    /**
     * yyyyMMdd
     *
     * @param time
     * @return
     */
    public static synchronized String yyyyMMdd(long time) {
        if (yyyyMMdd == null) {
            yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
        }
        return yyyyMMdd.format(new Date(time));
    }

    public static synchronized List<String> yyyy_MM_dd_HH_mm_ss(List<Long> xs) {
        List<String> sxs = new ArrayList<>(xs.size());
        for (Long x : xs) {
            sxs.add(TimeUtils.yyyy_MM_dd_HH_mm_ss(x));
        }
        return sxs;
    }

    public static synchronized List<String> yyyy_MM_dd_HH_mm_ss(long[] xs) {
        List<String> sxs = new ArrayList<>(xs.length);
        for (Long x : xs) {
            sxs.add(TimeUtils.yyyy_MM_dd_HH_mm_ss(x));
        }
        return sxs;
    }

    public synchronized static List<String> yyyyMMdd(long[] xs) {
        List<String> sxs = new ArrayList<>(xs.length);
        for (Long x : xs) {
            sxs.add(TimeUtils.yyyyMMdd(x));
        }
        return sxs;
    }

    public synchronized static List<String> yyyyMMdd(List<Long> xs) {

        List<String> sxs = new ArrayList<>(xs.size());
        for (Long x : xs) {
            sxs.add(TimeUtils.yyyyMMdd(x));
        }
        return sxs;
    }


    /**
     * 设置系统时间当前的偏移量
     * @param offset
     */
    public static void offset_inc(long offset) {
        TimeUtils.offset += offset;
    }

    /**
     * 设置系统时间当前的偏移量
     * @param offset
     */
    public static void offset_dec(long offset) {
        TimeUtils.offset -= offset;
    }

    private static long offset = 0;

    /**
     * 获取当前时间, 可以通过调整offset改变当前时间,不必调整系统设置
     * @return
     */
    public static long now() {
        return System.currentTimeMillis() + offset;
    }

    public static final Date DATE_ZERO = new Date(0);

    public static int now_YYYYMMDD() {
        return ofInt_YYYYMMDD(now());
    }

    public static int ofInt_YYYYMMDD(long time) {
        if(time < 0) {
            time = now();
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        return y*10000 + m*100+d;
    }
}
