package s.a.m.a.holdon.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import s.a.m.a.holdon.CheckInRecord;
import s.a.m.a.holdon.GApplication;
import s.a.m.a.holdon.HTask;
import s.a.m.a.sched.sched.TimeUtils;

/**
 * Created by amas on 15-5-17.
 */
public class LocalStorage {
    private static LocalStorage sInstance = null;
    Context context = null;

    public static LocalStorage getsInstance() {
        if (sInstance == null) {
            synchronized (LocalStorage.class) {
                if (sInstance == null) {
                    sInstance = new LocalStorage();
                }
            }
        }
        return sInstance;
    }

    private LocalStorage() {
        context = GApplication.getContext();
    }

    protected SQLiteDatabase getDatabase() {
        SQLiteDatabase db = null;
        try {
            db = HTask_DbHelper.getInstance(context).getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return db;
    }

    protected static long insert(SQLiteDatabase db, HTask x) {
        return db.insert(HTask.TABLE_NAME, null, x.toContentValues());
    }


    /**
     * 更新任务
     *
     * @param htask
     */
    public long updateHTask(HTask htask) {
        SQLiteDatabase db = getDatabase();
        if (db == null) {
            return -1;
        }

        return update(db, htask);
    }

    protected static long update(SQLiteDatabase db, HTask x) {
        long r = db.update(HTask.TABLE_NAME, x.toContentValues(), HTask.Columns._ID + "=?", new String[]{String.valueOf(x._ID())});
        return r;
    }


    protected static boolean safeEndTransaction(SQLiteDatabase db) {
        boolean success = false;
        try {
            db.endTransaction();
            success = true;
        } catch (Exception e) {

        }
        return success;
    }

    public int insert_HTask(SQLiteDatabase db, List<HTask> xs) {
        int size = 0;
        try {
            db.beginTransaction();
            for (HTask x : xs) {
                insert(db, x);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            size = safeEndTransaction(db) ? xs.size() : 0;
        }
        return size;
    }

    public List<HTask> queryAll_HTask(SQLiteDatabase db) {
        List<HTask> xs = new ArrayList<HTask>();
        Cursor c = null;
        try {
            c = db.query(HTask.TABLE_NAME, null, null, null, null, null, null);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                xs.add(new HTask().fromCursor(c));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return xs;
    }

    public List<HTask> getAllHTask() {
        SQLiteDatabase db = getDatabase();
        if (db == null) {
            return null;
        }
        return queryAll_HTask(db);
    }

    public long addHTask(HTask hTask) {
        SQLiteDatabase db = getDatabase();
        if (db == null) {
            return -1;
        }
        long _id = insert(db, hTask);
        if(_id > 0) {
            hTask._ID(_id);
        }
        return _id;
    }

    /**
     * 针对指定任务签到
     * @param taskid 任务id
     * @param checkintime 签到时间
     * @return
     */
    public boolean addCheckInRecord(long taskid, long checkintime) {
        CheckInRecord r = CheckInRecord.create(taskid, checkintime);
        long result = addCheckInRecord(r);
        return result >= 0;
    }

    public long addCheckInRecord(CheckInRecord x) {
        SQLiteDatabase db = getDatabase();
        if(db == null) {
            return -1;
        }

        if(hasCheckInRecord(db, x.task_id(), x.checkin_time())) {
            android.util.Log.i("htask", "   addCheckInRecord -> -2");
            return -2;
        }
        android.util.Log.i("htask", "   addCheckInRecord -> " + x);
        return insert(db, x);
    }

    /**
     * 一天只允许签到一次，此函数用于检测指定时间是否已经有checkin
     * @param taskid
     * @param time
     * @return
     */
    public boolean hasCheckInRecord(SQLiteDatabase database, long taskid, long time) {

        SQLiteDatabase db = database == null ? getDatabase() : database;
        if(db == null) {
            return false;
        }


        Cursor c = null;
        boolean isExisted = false;
        try {
            c = db.query(CheckInRecord.TABLE_NAME,
                    new String[]{CheckInRecord.Columns._ID},
                    CheckInRecord.Columns.TASK_ID+"=? AND " + CheckInRecord.Columns.CHECKIN_DATE+"=?",
                    new String[]{String.valueOf(taskid), TimeUtils.yyyyMMdd(time)},
                    null,
                    null,
                    null);

            if(c != null && c.getCount() > 0 ) {
                isExisted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return isExisted;
    }

    /**
     * 获取指定任务的全部checkin记录
     * @param taskid
     * @return
     */
    public Map<Integer, CheckInRecord> getCheckInRecordByTaskId(int taskid) {
        TreeMap<Integer, CheckInRecord> xs = new TreeMap<Integer, CheckInRecord>();
        SQLiteDatabase db = getDatabase();
        if(db == null) {
            return xs;
        }

        Cursor c = null;
        try {
            c = db.query(CheckInRecord.TABLE_NAME,
                    null,
                    CheckInRecord.Columns.TASK_ID+"=?",
                    new String[] { String.valueOf(taskid) },
                    null,
                    null,
                    null);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                //xs.add(new CheckInRecord().fromCursor(c));
                CheckInRecord cr = new CheckInRecord().fromCursor(c);
                if(cr != null){
                    xs.put(cr.checkin_date(), cr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return xs;
    }

    public Map<Integer, CheckInRecord> getCheckInRecordByTaskId(long taskid, long start, long end) {
        TreeMap<Integer, CheckInRecord> xs = new TreeMap<Integer, CheckInRecord>();
        SQLiteDatabase db = getDatabase();
        if(db == null) {
            return xs;
        }

        if(start > end) {
            return xs;
        }

        Cursor c = null;
        try {
            c = db.query(CheckInRecord.TABLE_NAME,
                    null,
                    CheckInRecord.Columns.TASK_ID+"=?" +
                            " AND " + CheckInRecord.Columns.CHECKIN_TIME+">="+start+
                            " AND " + CheckInRecord.Columns.CHECKIN_TIME+"<"+end,
                    new String[] { String.valueOf(taskid) },
                    null,
                    null,
                    null);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                CheckInRecord cr = new CheckInRecord().fromCursor(c);
                if(cr != null){
                    xs.put(cr.checkin_date(), cr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return xs;
    }


    protected static long insert(SQLiteDatabase db, CheckInRecord x) {
        return db.insert(CheckInRecord.TABLE_NAME, null, x.toContentValues());
    }

    public int insert_CheckInRecord(SQLiteDatabase db, List<CheckInRecord> xs) {
        int size = 0;
        try {
            db.beginTransaction();
            for (CheckInRecord x : xs) {
                insert(db, x);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            size = safeEndTransaction(db) ? xs.size() : 0;
        }
        return size;
    }

    public List<CheckInRecord> queryAll_CheckInRecord(SQLiteDatabase db) {
        List<CheckInRecord> xs = new ArrayList<CheckInRecord>();
        Cursor c = null;
        try {
            c = db.query(CheckInRecord.TABLE_NAME, null, null, null, null, null, null);
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                xs.add(new CheckInRecord().fromCursor(c));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return xs;
    }

    public long queryAllCheckinTimes(long taskid) {
        SQLiteDatabase db = getDatabase();
        if(db == null) {
            return 0;
        }

        Cursor c = null;
        long sum = 0;
        try {
            c = db.query(CheckInRecord.TABLE_NAME,
                    new String[] {HTask.Columns._ID},
                    CheckInRecord.Columns.TASK_ID+"=?",
                    new String[]{String.valueOf(taskid)},
                    null,
                    null,
                    null,
                    null);

            sum = c.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        android.util.Log.i("htask","          total="+sum + " id="+taskid);
        return sum;
    }

    /**
     * 获取最近一条签到信息
     * @param taskid
     * @return
     */
    public CheckInRecord getLastCheckIn(long taskid) {
        SQLiteDatabase db = getDatabase();
        if(db == null) {
            return null;
        }


        Cursor c = null;
        CheckInRecord x  = null;
        try {
            c = db.query(CheckInRecord.TABLE_NAME,
                    null,
                    CheckInRecord.Columns.TASK_ID+"=?",
                    new String[]{String.valueOf(taskid)},
                    null,
                    null,
                    CheckInRecord.Columns.CHECKIN_DATE,
                    "1");

            if(c != null && c.getCount() > 0 ) {
                c.moveToFirst();
                x = new CheckInRecord();
                x.fromCursor(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return x;
    }

    public CheckInRecord getCheckinRecord(long taskid, int checkinId) {
        SQLiteDatabase db = getDatabase();
        if(db == null) {
            return null;
        }

        Cursor c = null;
        CheckInRecord cr = null;

        try {
            c = db.query(CheckInRecord.TABLE_NAME,
                    null,
                    CheckInRecord.Columns.TASK_ID+"=?"+" AND "+ CheckInRecord.Columns.CHECKIN_DATE+"=?",
                    new String[]{String.valueOf(taskid),String.valueOf(checkinId)},
                    null,
                    null,
                    null,
                    null);

           if(c!=null && c.getCount()>0) {
               c.moveToFirst();
               cr = new CheckInRecord();
               cr.fromCursor(c);
           }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return cr;
    }
}
