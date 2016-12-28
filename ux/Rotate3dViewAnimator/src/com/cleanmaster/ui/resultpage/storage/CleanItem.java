package com.cleanmaster.ui.resultpage.storage;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class CleanItem implements Cloneable,Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3571250171997207634L;
	public static final String TABLE_NAME_TOP1 = "tbl_cleanitem_top1";
    public static final String TABLE_NAME_TOP7 = "tbl_cleanitem_top7";

    public static final int VERSION = 4;

    public CleanItem(CleanItem item) {
    	name  = item.name;
    	value = item.value;
    	time  = item.time;
    }

	public CleanItem() {
	}

	public static void remove(SQLiteDatabase db, String pn) {

    }


    public interface Columns extends BaseColumns {
        public static final String NAME = "name";
        public static final String VALUE = "value";
        public static final String TIME = "time";
    }


    private String name;
    private long value;
    private int time;

    public CleanItem name(String name) {
        this.name = name;
        return this;
    }

    public String name() {
        return this.name;
    }

    public CleanItem value(long value) {
        this.value = value;
        return this;
    }

    public long value() {
        return this.value;
    }

    public CleanItem time(int time) {
        this.time = time;
        return this;
    }

    public int time() {
        return this.time;
    }

    public CleanItem fromJSONObject(JSONObject o) {
        return this;
    }

    public ContentValues toContentValues() {
        ContentValues c = new ContentValues();

        c.put(Columns.NAME, name);
        c.put(Columns.VALUE, value);
        c.put(Columns.TIME, time);
        return c;
    }

    public CleanItem fromCursor(Cursor cursor) {
        this.name  = cursor.getString(cursor.getColumnIndex(Columns.NAME));
        this.value = cursor.getLong(cursor.getColumnIndex(Columns.VALUE));
        this.time  = cursor.getInt(cursor.getColumnIndex(Columns.TIME));
        return this;
    }

    public static void onCreateTable(SQLiteDatabase db) {
        onCreateTable(db,TABLE_NAME_TOP1);
        onCreateTable(db,TABLE_NAME_TOP7);
        UNIQ_INDEX(db, TABLE_NAME_TOP1, TABLE_NAME_TOP1, Columns.NAME);
        UNIQ_INDEX(db, TABLE_NAME_TOP7, TABLE_NAME_TOP7, Columns.NAME);
    }
    
    public static void UNIQ_INDEX(SQLiteDatabase db, String indexName, String tablename, String colname) {
    	String sql  =String.format("CREATE UNIQUE INDEX idx_%s ON %s (%s);", indexName, tablename, colname);
    	db.execSQL(sql);
    }

    public static void onCreateTable(SQLiteDatabase db, String tableName) {
        String sql = "CREATE TABLE " + tableName + "("
                + Columns._ID + " INTEGER PRIMARY KEY" + ","
                + Columns.NAME + " TEXT,"
                + Columns.VALUE + " INTEGER,"
                + Columns.TIME + " INTEGER"
                + ");";
        System.out.println("ZHOUJB=" + sql);
        db.execSQL(sql);
    }

    public static void onUpgradeTable(SQLiteDatabase db, int oldVersion, int currentVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TOP1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TOP7);
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        try {
            o.put(Columns.NAME, name);
            o.put(Columns.VALUE, value);
            o.put(Columns.TIME, time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o;
    }
    
    @Override
    public String toString() {
    	return toJSONObject().toString();
    }

	public static void reset_TABLE_NAME_TOP1(SQLiteDatabase db) {
		db.execSQL("TRUNCATE TABLE " + TABLE_NAME_TOP1+";");
	}

	public static void reset_TABLE_NAME_TOP7(SQLiteDatabase db) {
		db.execSQL("TRUNCATE TABLE " + TABLE_NAME_TOP7+";");
	}

	public void value_acc(long value) {
		this.value += value;
	}
}