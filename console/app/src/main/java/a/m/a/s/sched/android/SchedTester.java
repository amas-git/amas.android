package a.m.a.s.sched.android;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import a.m.a.s.sched.Sched;
import a.m.a.s.sched.SchedParser;


/**
 * Created by amas on 15-5-23.
 */
public class SchedTester {
    public static final SimpleDateFormat DATEFORMATER = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 E ");

    private static void test002() {
        Sched x;

        // 周 年 月 日 时 分
        // * 随便
        // 9,11,22
        x = SchedParser.parse("* 2010 10 1 18 0");
        expect(1, x.evalLatestTriggerTime(C("2010.09.29.08.36.00")), "2010.10.01.18.00.00");

        x = SchedParser.parse("* 2010 10 1 10 0");
        expect(2, x.evalLatestTriggerTime(C("2010.10.02.00.00.01")), null);

        x = SchedParser.parse("* 2000 * * 6 0");
        expect(3, x.evalLatestTriggerTime(C("2000.02.29.07.00.00")), "2000.03.01.06.00.00");

        x = SchedParser.parse("* * * * * *");
        expect(4, x.evalLatestTriggerTime(C("2000.07.09.00.00.00")), "2000.07.09.00.00.00");

        x = SchedParser.parse("* 2000 7 9 0 0");
        expect(5, x.evalLatestTriggerTime(C("2000.07.09.00.00.00")), "2000.07.09.00.00.00");

        x = SchedParser.parse("* * * * * 30");
        expect(6, x.evalLatestTriggerTime(C("2000.12.31.07.00.00")), "2000.12.31.07.30.00");

        x = SchedParser.parse("* 2000 12 31 6 0");
        expect(7, x.evalLatestTriggerTime(C("2000.12.31.07.00.00")), null);

        x = SchedParser.parse("* 2010 * * 6 30");
        _(x);
        expect(101, x.evalLatestTriggerTime(C("2000.12.10.07.00.00")), "2010.01.01.06.30.00"/*星期五*/);

        x = SchedParser.parse("1,2,3 2010 * * 6 30");
        expect(102, x.evalLatestTriggerTime(C("2000.12.10.07.00.00")), "2010.01.04.06.30.00");

        x = SchedParser.parse("2,3 2010 * * 6 30");
        expect(103, x.evalLatestTriggerTime(C("2000.12.10.07.00.00")), "2010.01.05.06.30.00");

        x = SchedParser.parse("5 2010 * * 6 30");
        expect(104, x.evalLatestTriggerTime(C("2000.12.10.07.00.00")), "2010.01.01.06.30.00");

        x = SchedParser.parse("0 2010 * * 6 30");
        expect(105, x.evalLatestTriggerTime(C("2000.12.10.07.00.00")), "2010.01.03.06.30.00");

        x = SchedParser.parse("* * * * 10-11 *");
        expect(119, x.evalLatestTriggerTime(C("2012.02.28.10.59.00")), "2012.02.28.10.59.00");

        // 2. 闰年：每年的2月29号闹铃。
        x = SchedParser.parse("* * 2 29 10 0");
        expect(112, x.evalLatestTriggerTime(C("2010.02.28.01.00.01")), "2012.02.29.10.00.00");

        // 3/ 写case的时候不小心把日期的值写到月份上了， 这个是错误用例，冠男参考一下吧。
        x = SchedParser.parse("* * 31 10 0 *");
        expect(115, x.evalLatestTriggerTime(C("2012.02.28.09.00.00")), "2012.03.31.10.00.00");

        // 4. 每月的３１号
        x = SchedParser.parse("* * * 31 10 0");
        expect(116, x.evalLatestTriggerTime(C("2012.02.28.09.00.00")), "2012.03.31.10.00.00");

    }

    public static void expect(int tag, Calendar nextAlarm, String expected) {
        if (nextAlarm != null) print(nextAlarm);
        if (nextAlarm != null) print(C(expected));

        if (nextAlarm == null && expected == null) {
            System.out.println(String.format("[ TEST%3d ] :------------ PASSED\n\n", tag));
        } else if (!C(expected).equals(nextAlarm)) {
            System.out.println(String.format("[ TEST%3d ] :------------ FAILED\n\n", tag));
        } else {
            System.out.println(String.format("[ TEST%3d ] :------------ PASSED\n\n", tag));
        }
    }

    /**
     * 格式类似于: "2002.01.29.08.36.33"
     *
     * @param dateString
     * @return
     */
    public static Calendar C(String dateString) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
            Date date = formatter.parse(dateString);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            return c;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    private static void _(Object text) {
        System.out.println(text);
    }

    public static void printNow(Calendar now) {
        System.out.println(" * MINUTE      = " + now.get(Calendar.MINUTE));
        System.out.println(" * HOUR_OF_DAY = " + now.get(Calendar.HOUR_OF_DAY));
        System.out.println(" * DAY_OF_MONTH= " + now.get(Calendar.DAY_OF_MONTH));
        System.out.println(" * MONTH       = " + now.get(Calendar.MONTH));
        System.out.println(" * DAY_OF_WEEK = " + now.get(Calendar.DAY_OF_WEEK));
        System.out.println(" * YEAR        = " + now.get(Calendar.YEAR));
        print(now);
    }

    public static String __(Calendar c) {
        return DATEFORMATER.format(new Date(c.getTimeInMillis()));
    }

    public static void print(Calendar c) {
        System.out.println(__(c));
    }

}
