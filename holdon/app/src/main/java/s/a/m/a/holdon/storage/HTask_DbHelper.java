package s.a.m.a.holdon.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import s.a.m.a.holdon.CheckInRecord;
import s.a.m.a.holdon.HTask;


public class HTask_DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = ".main.db";
    private static HTask_DbHelper mInstance = null;
    private static int VERSION =  HTask.VERSION + CheckInRecord.VERSION;

    public static synchronized HTask_DbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HTask_DbHelper(context);
        }
        return mInstance;
    }

    public HTask_DbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        HTask.onCreateTable(db);
        CheckInRecord.onCreateTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
        HTask.onUpgradeTable(db, oldVersion, currentVersion);
        CheckInRecord.onUpgradeTable(db, oldVersion, currentVersion);
    }
}