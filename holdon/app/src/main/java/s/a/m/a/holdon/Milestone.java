package s.a.m.a.holdon;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import s.a.m.a.L;
import s.a.m.a.holdon.storage.LocalStorage;
import s.a.m.a.sched.sched.Sched;
import s.a.m.a.sched.sched.TimeUtils;

/**
 * Created by amas on 6/5/15.
 */
public class Milestone implements Serializable {
    TreeMap<Integer,CheckInRecord> checkins = new TreeMap<>();

    /**
     * 增加一条checkin记录
     * @param c
     */
    public void addCheckInRecord(CheckInRecord c) {
        checkins.put(c.checkin_date(), c);
    }

    enum Status implements  Serializable {
         NOT_START(1)
        ,ONGOING(2)
        ,FINISHED(3);

        int s = 1;
        Status(int i) {
            s = i;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            switch (this) {
                case NOT_START: sb.append(" "); break;
                case ONGOING  : sb.append("*"); break;
                case FINISHED : sb.append("+"); break;
                default:
            }

            return sb.toString();
        }
    }

    Status status = Status.NOT_START;

    public long num = 0;
    /**
     * 开始时间
     */
    public long startTime;

    /**
     * 结束时间,开区间
     */
    public long endTime;

    private int checkin_times = 0;

    private long taskid = 0;

    public boolean isOngoing() {
        updateStatus(-1);
        return status == Status.ONGOING;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%-3s] %s - %s [%3d] %b", status+""+num, TimeUtils.yyyyMMdd(startTime), TimeUtils.yyyyMMdd(endTime), checkin_times,
                isSuccess(1))).append("\n");
        sb.append(L.addPadding("LEFT : " + TimeUtils.yyyyMMdd(getLeftCheckinDays()), 1));
        sb.append(L.addPadding("RANGE: "+TimeUtils.yyyyMMdd(day_range()),1));
        sb.append(dumpCheckins());
        return sb.toString();
    }

    private String dumpCheckins() {
        updateCheckinRecords();
        StringBuilder sb = new StringBuilder();
        for(CheckInRecord c : checkins.values()) {
            sb.append(L.addPadding(c.toString(),3)).append("\n");
        }
        return sb.toString();
    }

    public static Milestone create(long taskid, long num, long ms_start, long ms_end) {
        Milestone m = new Milestone();
        m.startTime = ms_start;
        m.endTime = ms_end;
        m.num = num;
        m.taskid = taskid;
        m.updateStatus(-1);
        return m;
    }

    public void updateCheckinRecords() {
        updateStatus(-1);
        if(status != Status.NOT_START) {
            Map<Integer, CheckInRecord> c = LocalStorage.getsInstance().getCheckInRecordByTaskId(taskid, startTime, endTime);
            this.checkins.putAll(c);
        }
    }

    /**
     * 根据当前时间更新里程碑状态
     * @param now
     */
    public void updateStatus(long now) {
        if(now <= 0) {
            now = TimeUtils.now();//System.currentTimeMillis();
        }

        if (now >= endTime) {
            status = Status.FINISHED;
        } else if(now >= startTime && now < endTime) {
            status = Status.ONGOING;
        } else {
            status = Status.NOT_START;
        }
    }


    /**
     * 统计签到次数
     * @param checkins
     */
    public void countCheckinTimes(Map<Integer, CheckInRecord> checkins) {
        long[] day_range = day_range();
        checkin_times = 0;
        for(int i=0; i<day_range.length-1; ++i) {
            int key = Integer.valueOf(TimeUtils.yyyyMMdd(day_range[i]));
            if(checkins.containsKey(key)) {
                checkin_times += 1;
            }
        }
    }


    long[] day_range = null;

    public long[] day_range() {
        if(day_range == null && startTime < endTime) {
            day_range = TimeUtils.range_day(startTime, endTime);
        }
        return day_range;
    }

    public int checkin_times() {
        return checkin_times;
    }

    /**
     * 里程碑是否失败
     * @returno
     */
    public boolean isFailed(long min_checkin) {
        //checkin_times
        boolean failed = true;

        if(day_range == null || day_range.length == 0) {
            return failed;
        }

        // 当前的里程碑
        if(isOngoing()) {
            failed = (min_checkin - checkin_times) > getLeftCheckinDays().size();
            //android.util.Log.i("htask", "min_checkin="+min_checkin+" checkin_times="+checkin_times+" left="+getLeftCheckinDays().size());
        } else {
            failed = checkin_times < min_checkin;
        }

        return failed;
    }

    public boolean isSuccess(long min_checkin) {
        return !isFailed(min_checkin);
    }

    public List<Long> getLeftCheckinDays() {
        List<Long> xs = new ArrayList<>();
        long[] day_range = day_range();
        long now  = TimeUtils.normalizeNow();

        if(isOngoing()) {
            // day_range是开区间，所以最后一日不计入
            for(int i=0; i<day_range.length; ++i) {
                if(day_range[i] >= now) {
                    xs.add(day_range[i]);
                }
            }
        } else {
            return xs;
        }

        return xs;
    }

    /**
     * 根据指定周期推算出milestone
     * @param startTime
     * @param sched
     * @param rounds
     * @return
     */
    public static List<Milestone> generateMilestone(int taskid, long startTime, Sched sched, long rounds) {
        List<Milestone> milestones = new ArrayList<>();
        if(sched == null || rounds <=0 ) {
            return milestones;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(TimeUtils.yyyyMMdd(TimeUtils.yyyyMMdd(startTime)));

        long ms_start = startTime;
        long ms_end = 0;

        for(long i=0; i<rounds+1; ++i) {
            c = sched.evalLatestTriggerTime(c);
            if(c == null) {
                break;
            }

            ms_end =  c.getTimeInMillis();

            if(ms_start != 0 && ms_start < ms_end) {
                milestones.add(Milestone.create(taskid, i, ms_start, ms_end));
            }
            c.setTime(new Date(c.getTimeInMillis()+60*1000));
            ms_start = ms_end;
        }
        return milestones;
    }

    /**
     * 计算
     * @param startTime 一般是里程碑的最开始时间
     * @param round 第几个里程碑
     * @param milestoneLength 一个里程碑的时间
     * @return
     */
    public static long calcNthMilestoneStartTime(long startTime, long round, long milestoneLength) {
        return startTime + ((round) * milestoneLength);
    }

    /**
     * Milestone经历的时间长度
     * @return
     */
    public long getMilestoneDistance() {
        return endTime - startTime;
    }
}
