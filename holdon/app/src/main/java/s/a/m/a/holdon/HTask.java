package s.a.m.a.holdon;

/**
 * Created by amas on 15-5-17.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import s.a.m.a.holdon.storage.LocalStorage;
import s.a.m.a.sched.sched.Sched;
import s.a.m.a.sched.sched.SchedParser;
import s.a.m.a.sched.sched.TimeUtils;

public class HTask implements Serializable {

    public static final String TABLE_NAME = "tbl_htask";
    public static final int VERSION = 5;
    private float percent;

    public boolean isFinishedCurrentCheckin() {
        CheckInRecord c = LocalStorage.getsInstance().getLastCheckIn(_ID());
        return c != null && c.isSameDateWith(TimeUtils.now());
    }

    public void _ID(long id) {
        _id = id;
    }


    enum Status {
        INIT(1), SUCCESS(2),PAUSE(3),ONGOING(4),FAILED(5);
        int value = 0;
        Status(int v) {
            value = v;
        }

        public int value() {
            return value;
        }

        public static Status fromInt(int v) {
            switch (v) {
                case 1 : return SUCCESS;
                case 2 : return PAUSE;
                case 3 : return ONGOING;
                case 4 : return FAILED;
            }
            return INIT;
        }
    }

    public long _ID() {
        return _id;
    }

    Sched milestone_sched_c = null;

    public Sched getMilestoneSched() {
        if(milestone_sched_c == null) {
            milestone_sched_c = SchedParser.parse(milestone_sched);
        }
        return milestone_sched_c;
    }

    /**
     * 是否可以checkin
     * @return
     */
    protected boolean canCheckin() {
        return status == Status.INIT || status == Status.ONGOING;
    }

    public CheckInRecord checkin_io() {
        Milestone current = getCurrentMilestone();
        if(current == null) {
            return null;
        }

        if(!canCheckin()) {
            return null;
        }

        CheckInRecord c = CheckInRecord.create(_ID(), TimeUtils.now());
        long r = LocalStorage.getsInstance().addCheckInRecord(c);
        if( r >= 0) {

            current.addCheckInRecord(c);

            // 更新任务状态
            updateStatus(current);

            // 成功或者进行状态下记录最后一次签到成功的milestone
            if(SUCCESS() || ONGOING()) {
               milestone_last_success = current.num;
            }
            LocalStorage.getsInstance().updateHTask(this);
            log(" *** SUCCESS : " +  c);
            return c;
        } else {
            log(" *** FAILED : " + r);
            return null;
        }
    }

<<<<<<< HEAD
    public void updateStatus(Milestone currentMilestone) {
=======
    public boolean PAUSE() {
        return status == Status.PAUSE;
    }

    public boolean ONGOING() {
        return status == Status.ONGOING;
    }

    public boolean SUCCESS() {
        return status == Status.SUCCESS;
    }

    /**
     * 更新任务状态
     * @param  currentMilestone
     * @return true -> 状态改变  false -> 状态没有改变
     */
    public boolean updateStatus(Milestone currentMilestone) {
>>>>>>> 42b98945adeba8d9a7e3a62a920f564a5396a8bf
        // 获取当前里程碑
        if(currentMilestone == null) {
            currentMilestone = getCurrentMilestone();
        }

        if(currentMilestone == null) {
            return status(Status.FAILED);
        }

        log(" CURRENT - LAST MILESTONE : " + (currentMilestone.num - milestone_last_success));
        if(currentMilestone.num - milestone_last_success > 1) {
            status(Status.FAILED);
        }


        // 1. 当前里程碑失败  或者 当前里程碑大于
        if(currentMilestone.isFailed(milestone_min_times)) {
            if(rescue > 0) {
                return status(Status.PAUSE);
            } else {
                return status(Status.FAILED);
            }
        } else {
            if(currentMilestone.num == milestone_target_round) {
                return status(Status.SUCCESS);
            }
        }
<<<<<<< HEAD

        if(status == Status.ONGOING) {
            milestone_last_success = currentMilestone.num;
        }
=======
        return false;
>>>>>>> 42b98945adeba8d9a7e3a62a920f564a5396a8bf
    }

    private boolean status(Status status) {
        log(" STATUS CHANGED : " + this.status + " -> " + status);
        Status before = this.status;
        this.status = status;
        return this.status != before;
    }


    public boolean isFinishedCurrentCeheckin() {
        int now = Integer.valueOf(TimeUtils.yyyyMMdd(TimeUtils.now()));
//        return checkins.containsKey(now);
        return LocalStorage.getsInstance().getCheckinRecord(_ID(), now) != null;
    }

    /**
     * 获得当前的里程碑
     * @return
     */
    public Milestone getCurrentMilestone() {
        long now = TimeUtils.normalizeNow();
        long distance = now - TimeUtils.normalizeTime(ctime);


        long ms_distance =  Sched.distance(getMilestoneSched());
        if(ms_distance == -1) {
            return null;
        }

        long ms_num = distance / ms_distance;
        long ms_start = Milestone.calcNthMilestoneStartTime(TimeUtils.normalizeTime(ctime), ms_num, ms_distance);
        Milestone m = Milestone.create(_ID(), ms_num,ms_start,ms_start+ms_distance);
        log("  * now      ="+TimeUtils.yyyy_MM_dd_HH_mm_ss(TimeUtils.normalizeTime(now)));
        log("  * ctime    ="+TimeUtils.yyyy_MM_dd_HH_mm_ss(TimeUtils.normalizeTime(ctime)));
        log("  * ms_num   ="+ms_num);
        log("  * ms_start ="+ms_start);
        log(" " + m);
        return m;
    }

    public long total_check_times() {
        total_check_times = LocalStorage.getsInstance().queryAllCheckinTimes(_ID());
        return total_check_times;
    }

    long total_check_times = 0;

    public static boolean DEBUG = true;
    public void sync() {

    }

    public String buildDebugInfo() {
        String debug_info = "";
//        List<Milestone> milestones = milestones();
//        for(Milestone m : milestones) {
//            if(DEBUG) {
//                debug_info += m.toString();
//            }
//        }

        if(DEBUG) {
            String header = "";
            long now = TimeUtils.normalizeNow();
            header += "===============================\n";
            header += "MILESTONES\n";
            header += "now : " +  now + " " + TimeUtils.yyyy_MM_dd(now)+  "\n";
            header += " total_check_times=" + total_check_times+"\n";
            header += "===============================\n";
            debug_info = header + debug_info;
        }
        return debug_info;
    }

    /**
     * 获取当前进度
     * @return
     */
    public float getPercent() {
        return total_check_times * 100f / target_checkin_times();
    }

    public interface Columns extends BaseColumns {
        public static final String TITLE = "title";
        public static final String DESC = "desc";
        public static final String CTIME = "ctime";
        public static final String ETIME = "etime";
        public static final String STATUS = "status";
        public static final String FAILED_TIMES = "failed_times";
        public static final String RESCUE = "rescue";
        public static final String TAGS = "tags";
        public static final String MILESTONE_TARGET_ROUND = "milestone_target_round";
        public static final String MILESTONE_MIN_TIMES     = "milestone_min_times";
        public static final String MILESTORE_SCHED         = "milestone_sched"; // 最近的里程碑是哪天
        public static final String MILESTORE_LAST_SUCCESS = "milestone_last_success"; // 当前的里程碑
    }

    private long _id;
    private String title;
    private String desc;
    private long ctime;
    private long etime;
<<<<<<< HEAD
    private Status status = Status.ONGOING;
=======
    private Status status = Status.INIT;
>>>>>>> 42b98945adeba8d9a7e3a62a920f564a5396a8bf
    private int rescue;
    private String tags;

    private long milestone_min_times;
    private long milestone_target_round;
    private String milestone_sched;
    private long milestone_last_success = -1L;

    /**
     * 当前出于第几个里程碑?
     * @return
     */
    public long milestone_last_success() {
        return milestone_last_success;
    }

    public long target_checkin_times() {
        return milestone_min_times * milestone_target_round;
    }

    public HTask title(String title) {
        this.title = title;
        return this;
    }

    public String title() {
        return this.title;
    }

    public HTask desc(String desc) {
        this.desc = desc;
        return this;
    }

    public String desc() {
        return this.desc;
    }

    public HTask ctime(long ctime) {
        this.ctime = ctime;
        return this;
    }

    public long ctime() {
        return this.ctime;
    }

    public HTask etime(long etime) {
        this.etime = etime;
        return this;
    }

    public long etime() {
        return this.etime;
    }

    public static void log(String msg) {
        android.util.Log.i("htask", msg);
    }

    public HTask rescue(int rescue) {
        this.rescue = rescue;
        return this;
    }

    public int rescue() {
        return this.rescue;
    }

    public HTask tags(String tags) {
        this.tags = tags;
        return this;
    }


    public String tags() {
        return this.tags;
    }

    public HTask fromJSONObject(JSONObject o) {
        try {
            _id = o.getInt("_id");
            title = o.getString("title");
            desc = o.getString("desc");
            ctime = o.getLong("ctime");
            etime = o.getLong("etime");
            status = Status.fromInt(o.getInt("status"));
            rescue = o.getInt("rescue");
            tags = o.getString("tags");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ContentValues toContentValues() {
        ContentValues c = new ContentValues();
        c.put(Columns.TITLE, title);
        c.put(Columns.DESC, desc);
        c.put(Columns.CTIME, ctime);
        c.put(Columns.ETIME, etime);
        c.put(Columns.STATUS, status.value);
        c.put(Columns.RESCUE, rescue);
        c.put(Columns.TAGS, tags);
        c.put(Columns.MILESTONE_TARGET_ROUND, milestone_target_round);
        c.put(Columns.MILESTONE_MIN_TIMES, milestone_min_times);
        c.put(Columns.MILESTORE_SCHED, milestone_sched);
        c.put(Columns.MILESTORE_LAST_SUCCESS, milestone_last_success);

        return c;
    }

    public HTask fromCursor(Cursor cursor) {
        _id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns._ID));
        title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        desc = cursor.getString(cursor.getColumnIndexOrThrow("desc"));
        ctime = cursor.getLong(cursor.getColumnIndexOrThrow("ctime"));
        etime = cursor.getLong(cursor.getColumnIndexOrThrow("etime"));
        status = Status.fromInt(cursor.getInt(cursor.getColumnIndexOrThrow("status")));
        rescue = cursor.getInt(cursor.getColumnIndexOrThrow("rescue"));
        tags = cursor.getString(cursor.getColumnIndexOrThrow("tags"));

        milestone_target_round = cursor.getLong(cursor.getColumnIndexOrThrow("milestone_target_round"));
        milestone_min_times = cursor.getLong(cursor.getColumnIndexOrThrow("milestone_min_times"));
        milestone_sched = cursor.getString(cursor.getColumnIndexOrThrow("milestone_sched"));
<<<<<<< HEAD
        milestone_last_success = cursor.getLong(cursor.getColumnIndexOrThrow("milestone_last_success"));
=======
        milestone_last_success = cursor.getLong(cursor.getColumnIndexOrThrow(Columns.MILESTORE_LAST_SUCCESS));
>>>>>>> 42b98945adeba8d9a7e3a62a920f564a5396a8bf
        return this;
    }

    public static void onCreateTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "("
                    + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Columns.TITLE + " TEXT,"
                    + Columns.DESC + " TEXT,"
                    + Columns.CTIME + " LONG,"
                    + Columns.ETIME + " LONG,"
                    + Columns.STATUS + " INTEGER,"
                    + Columns.FAILED_TIMES + " INTEGER,"
                    + Columns.RESCUE + " INTEGER,"
                    + Columns.TAGS + " TEXT,"
                    + Columns.MILESTONE_TARGET_ROUND + " LONG,"
                    + Columns.MILESTONE_MIN_TIMES + " LONG,"
                    + Columns.MILESTORE_SCHED + " TEXT,"
                    + Columns.MILESTORE_LAST_SUCCESS +" LONG"
                    +");");
        }
    public static void onUpgradeTable(SQLiteDatabase db, int oldVersion, int currentVersion)  {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreateTable(db);
    }

    public JSONObject toJSONObject() {
        boolean humanReadable = true;

        JSONObject o = new JSONObject();
        try {
            o.put(Columns._ID, _id);
            o.put(Columns.TITLE, title);
            o.put(Columns.DESC, desc);
            o.put(Columns.CTIME, humanReadable ? TimeUtils.yyyy_MM_dd_HH_mm_ss(ctime) : ctime);
            o.put(Columns.ETIME, etime);
            o.put(Columns.STATUS, status);
            o.put(Columns.RESCUE, rescue);
            o.put(Columns.TAGS, tags);
            o.put(Columns.MILESTONE_TARGET_ROUND, milestone_target_round);
            o.put(Columns.MILESTONE_MIN_TIMES,  milestone_min_times);
            o.put(Columns.MILESTORE_SCHED, milestone_sched);
            o.put(Columns.MILESTORE_LAST_SUCCESS, milestone_last_success);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            JSONObject o = toJSONObject();
            if (o != null) {
                sb.append(o.toString(4));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sb.append("\n");
        sb.append(buildDebugInfo()).append("\n");
        return sb.toString();
    }

    /**
     *
     * @param title
     * @param sched
     * @param target 1d/w/m OR 10
     * @param milestone_target_round
     * @return
     */
    public static HTask create(String title, String milestone_sched, long milestone_target_round) {
        HTask x = new HTask();
        x.title = title;
        //x.checkin_sched = sched;

        x.milestone_sched = milestone_sched;
        x.milestone_target_round = milestone_target_round;
        x.milestone_min_times = 1;
        x.ctime = TimeUtils.now();
        x.status(Status.ONGOING);
        return x;
    }

    public void milestone_target_round(long milestone_target_round) {
        this.milestone_target_round = milestone_target_round;
    }
}
