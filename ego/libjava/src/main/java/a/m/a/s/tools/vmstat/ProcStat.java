package a.m.a.s.tools.vmstat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import a.m.a.s.utils.T;

/**
 * Created by amas on 8/31/17.
 * $pname $pid 1049 (Timer-62) S 301 301 0 0 -1 1077936192 263 0 0 0 0 0 0 0 20 0 107 0 20315065 1962151936 40089 4294967295 1 1 0 0 0 0 4612 0 38136 4294967295 0 0 -1 0 0 0 0 0 0 0 0 0
 */

public class ProcStat {
    String packageName = "";
    String pid = "";
    String pname = "";
    String id = "";
    String name = "";
    String[] data = null;
    public String uuid  = "";
    String cgroup = "";
    int utime;      // #14 USER MODE JIFFER
    int stime;      // #15 SYSTEMM MODE JIFFER
    long startTime; // # 22 START
    long time = 0;
    int age   = 1;    // 被监控的次数
    int ageFG = 0;    // 前台Uptime次数
    int ageBG = 0;    // 后台Uptime次数
    long endTime;



    static Pattern REGEX_THREAD_STAT = Pattern.compile("([\\w+:.]+)\\s+(\\d+)\\s+(FG|BG)\\s(\\d+)\\s+\\((.*)\\)\\s+(.*)");
    // 1: PID 2: PNAME 3:BG|FG 4: TID 5: TNAME

    public static HashMap<String, ProcStat> createMore(String procStat) {
        String xs[] = procStat.split("\n");
        HashMap<String, ProcStat> set = new HashMap<>();

        if(xs != null) {
            for (String x : xs) {
                ProcStat s = create(x);
                if(s != null) {
                    set.put(s.uuid, s);
                }
            }
        }
        return set;
    }

    public int age() {
        return age;
    }

    public void age(int age) {
        this.age = age;
    }

    public int cpuTime() {
        return utime + stime;
    }

    public static HashSet<ProcStat> createMore(ArrayList<String> procStat) {
        HashSet<ProcStat> set = new HashSet<>();

        for (String x : procStat) {
            ProcStat s = create(x);
            if(s != null) {
                set.add(s);
            }
        }
        return set;
    }



    public static ProcStat create(String text) {
        ProcStat stat = new ProcStat();

        Matcher m = REGEX_THREAD_STAT.matcher(text);
        if (m.matches()) {
            stat.pname  = m.group(1);
            stat.pid    = m.group(2);
            stat.cgroup = m.group(3);
            stat.id     = m.group(4);
            stat.name   = m.group(5).replace(' ', '~');
            stat.data   = m.group(6).split("\\s");
            stat.utime  = Integer.valueOf(stat.data[14-3]);
            stat.stime  = Integer.valueOf(stat.data[15-3]);
            stat.uuid   = stat.pname+"/"+stat.pid+"/"+stat.name+"@"+stat.id;
            stat.startTime = Long.valueOf(stat.data[22-3]);
            stat.time = System.currentTimeMillis();
            return stat;
        }
        return null;
    }

    final static String HEADER = "1:PROCNAME 2:PID 3:THREAD_NAME@TID 4:UTIME 5:STIME 6:START_TIME 7:END_TIME 8:<REVERSE> 9:AGE.FG 10:AGE.BG 11:AGE.FG+AGE.BG 12:AGE 13:LAST_CGROUP ";
    @Override
    public String toString() {
        //return String.format("%s %s %s %d %d %d %d %d %d %d %d %d %s", pname, pid, name+"@"+id, utime, stime, startTime, endTime,    time, ageFG, ageBG, (ageFG+ageBG), age, cgroup);
        return uuid + T.join(" ", data);
    }


    @Override
    public boolean equals(Object o) {
        return this.uuid.equals(((ProcStat)o).uuid);
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public void uage(ProcStat p) {
        if("BG".equals(cgroup)) {
            this.ageBG = p.ageBG + 1;
            this.ageFG = p.ageFG;
        } else {
            this.ageFG = p.ageFG +1;
            this.ageBG = p.ageBG;
        }
    }

    /**
     * 结束时间
     * @param now
     */
    public void endTime(long now) {
        this.endTime = now;
    }
}
