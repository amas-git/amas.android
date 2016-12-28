package s.a.m.a.holdon;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import s.a.m.a.sched.sched.TimeUtils;

public class CheckInRecord implements Serializable {

    public static final String TABLE_NAME = "tbl_checkinrecord";
    public static final int VERSION = 1;

    public static CheckInRecord create(long taskid, long checkintime) {
        CheckInRecord x = new CheckInRecord();
        x.task_id(taskid);
        x.checkin_time(checkintime);
        return x;
    }

    public boolean isSameDateWith(long now) {
        return String.valueOf(checkin_date).equals(TimeUtils.yyyyMMdd(now));
    }

    public int distance(long now) {
        Date d  = TimeUtils.yyyyMMdd(String.valueOf(checkin_date()));
        Date d_ = TimeUtils.yyyyMMdd(TimeUtils.yyyyMMdd(now));

        long diff = d.getTime() - d_.getTime();
        System.out.println("DIFF="+diff);
        return 1;
    }


    public interface Columns extends BaseColumns {
        public static final String TASK_ID = "task_id";
        public static final String CHECKIN_DATE = "checkin_date";
        public static final String CHECKIN_TIME = "checkin_time";
    }


    private long task_id;
    private int checkin_date;
    private long checkin_time;

    public CheckInRecord task_id(long task_id) {
        this.task_id = task_id;
        return this;
    }

    public long task_id() {
        return this.task_id;
    }

    public CheckInRecord checkin_date(int checkin_date) {
        this.checkin_date = checkin_date;
        return this;
    }

    public int checkin_date() {
        return this.checkin_date;
    }

    public CheckInRecord checkin_time(long checkin_time) {
        this.checkin_time = checkin_time;
        this.checkin_date = Integer.valueOf(TimeUtils.yyyyMMdd(checkin_time));
        return this;
    }

    public long checkin_time() {
        return this.checkin_time;
    }

    public CheckInRecord fromJSONObject(JSONObject o) {
        try {

            task_id = o.getInt("task_id");
            checkin_date = o.getInt("checkin_date");
            checkin_time = o.getLong("checkin_time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ContentValues toContentValues() {
        ContentValues c = new ContentValues();

        c.put(Columns.TASK_ID, task_id);
        c.put(Columns.CHECKIN_DATE, checkin_date);
        c.put(Columns.CHECKIN_TIME, checkin_time);
        return c;
    }

    public CheckInRecord fromCursor(Cursor cursor) {

        task_id = cursor.getLong(cursor.getColumnIndexOrThrow("task_id"));
        checkin_date = cursor.getInt(cursor.getColumnIndexOrThrow("checkin_date"));
        checkin_time = cursor.getLong(cursor.getColumnIndexOrThrow("checkin_time"));
        return this;
    }

    public static void onCreateTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + Columns._ID + " INTEGER PRIMARY KEY" + ","
                + Columns.TASK_ID + " INTEGER,"
                + Columns.CHECKIN_DATE + " INTEGER,"
                + Columns.CHECKIN_TIME + " INTEGER"
                + ");");
    }

    public static void onUpgradeTable(SQLiteDatabase db, int oldVersion, int currentVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        try {
            o.put(Columns.TASK_ID, task_id);
            o.put(Columns.CHECKIN_DATE, checkin_date);
            o.put(Columns.CHECKIN_TIME, checkin_time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o;
    }

    @Override
    public String toString() {
        return toJSONObject().toString();
    }

    public static class SORT_BY_TIME implements Comparator<CheckInRecord> {
        public SORT_BY_TIME() {

        }

        @Override
        public int compare(CheckInRecord lhs, CheckInRecord rhs) {
            return _compare(lhs.checkin_date(), rhs.checkin_date());
        }
    }


    public static int _compare(int lhs, int rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }
}
