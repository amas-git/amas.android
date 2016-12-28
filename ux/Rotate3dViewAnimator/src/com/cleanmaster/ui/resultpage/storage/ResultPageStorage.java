package com.cleanmaster.ui.resultpage.storage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by amas on 11/6/14.
 */
public class ResultPageStorage {
    private static ResultPageStorage sInstance = new ResultPageStorage();
    private Context mContext = null;
    private CleanItem_DbHelper mDbHelper = null;
    
    public void init (Context context) {
    	 mContext  = context;
    	 mDbHelper = new CleanItem_DbHelper(context);
    }
    
    public SQLiteDatabase getDatabase() {
    	SQLiteDatabase db = null;
    	try {
    		db = mDbHelper.getWritableDatabase();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return db;
    }
    
    private ResultPageStorage() {
    }

	public static List<String> getAllTables(SQLiteDatabase db) {
		List<String> tables = new ArrayList<String>();
		Cursor cursor = null;
		
		try {
			cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				String tableName = cursor.getString(1);
				if (!tableName.equals("android_metadata") && !tableName.equals("sqlite_sequence")) {
					tables.add(tableName);
				}
				cursor.moveToNext();
			}
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return tables;
	}
	
    public static ResultPageStorage getInstance() {
        return sInstance;
    }

    public static class CleanItem_DbHelper extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "rp.db";

        public CleanItem_DbHelper(Context context) {
            super(context, DATABASE_NAME, null, CleanItem.VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	System.out.println("CREATE DB ZHOUJB:");
            CleanItem.onCreateTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
            CleanItem.onUpgradeTable(db, oldVersion, currentVersion);
        }
    }

    static int mCurrentTop1Day  = -1; /// 日排行
    static int mCurrentTop7Day  = -1; /// 周排行

    public void onPackageRemove(final String pn) {

    }
    
	public void addCleanResult(List<PackageCleanResult> result) {
		addCleanResult(result, true);
	}
	
	protected void addCleanResult(List<PackageCleanResult> result, boolean useTransaction) {
		if(result == null || result.isEmpty()) {
			return;
		}
		
        SQLiteDatabase db = getDatabase();
        if (db == null ) {
            return;
        }
        
        int top1 = top1day();
        int top7 = top7day();
        
        if(top1 != getTop1Point()) {
        	CleanItem.reset_TABLE_NAME_TOP1(db);
        	android.util.Log.d("TOP7", "CURRENT="+getTop1Point() + "NEXT=" + top1);
        	setCurrentTop1(top1);
        }
        
        if(top7 != getTop7Point()) {
        	CleanItem.reset_TABLE_NAME_TOP7(db);
        	android.util.Log.d("TOP7", "CURRENT="+getTop7Point() + "NEXT=" + top7);
        	setCurrentTop7(top7);
        }
        
        if(useTransaction) {
        	db.beginTransaction();
        }
		try {
			for (PackageCleanResult x : result) {
				recordTop1(db, x);
				recordTop7(db, x);
			}
			if (useTransaction) {
				db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(useTransaction) {
				endTransaction(db);
			}
		}

		if (true) {
			dumpTop1Day();
			dumpTop7Day();
		}
	}

	private static final void endTransaction(SQLiteDatabase db) {
		try {
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    public void onCleanAppJunk(CleanItem item) {
        SQLiteDatabase db = getDatabase();
        if (db == null) {
            return;
        }
        
        int top1 = top1day();
        int top7 = top7day();
        
        if(top1 != getTop1Point()) {
        	CleanItem.reset_TABLE_NAME_TOP1(db);
        	android.util.Log.d("TOP7", "CURRENT="+getTop1Point() + "NEXT=" + top1);
        	setCurrentTop1(top1);
        }
        
        if(top7 != getTop7Point()) {
        	CleanItem.reset_TABLE_NAME_TOP7(db);
        	android.util.Log.d("TOP7", "CURRENT="+getTop7Point() + "NEXT=" + top7);
        	setCurrentTop7(top7);
        }
        
        recordTop1(db,item);
        recordTop7(db,item);
    }
  

    
    public void remove(String pn) {
        SQLiteDatabase db = getDatabase();
        if(db == null) {
            return;
        }
        CleanItem.remove(db, pn);
    }


    public List<CleanItem> getTop1(int max) {
    	
        SQLiteDatabase db = getDatabase();
        List<CleanItem> xs = new ArrayList<CleanItem>(5);
        if(db == null) {
            return xs;
        }

        Cursor cursor = null;
        try {
            cursor = db.query(CleanItem.TABLE_NAME_TOP1, null, null, null, null, null, CleanItem.Columns.VALUE + " DESC");
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                xs.add(new CleanItem().fromCursor(cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        return xs;
    }

    public List<CleanItem> getTop7(int max) {
        SQLiteDatabase db = getDatabase();
        List<CleanItem> xs = new ArrayList<CleanItem>(5);
        if(db == null) {
            return xs;
        }
        Cursor cursor = null;
        try {
            cursor = db.query(CleanItem.TABLE_NAME_TOP7, null, null, null, null, null, CleanItem.Columns.VALUE + " DESC");
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                xs.add(new CleanItem().fromCursor(cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        return xs;
    }



    public int top1day() {
        Calendar c  = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        return c.get(Calendar.DAY_OF_YEAR);
    }

    public int top7day()  {
        Calendar c  = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        return c.get(Calendar.WEEK_OF_YEAR);
    }


    private void dumpTop1Day() {
    	dump("TOP1", getTop1(Integer.MAX_VALUE));
    }
    
    private void dump(String tag, List<CleanItem> xs) {
    	for(CleanItem x : xs) {
    		Log.d(tag, ""+x);
    	}
    	Log.d(tag, "--------------------------------------");
	}
    
	private void dumpTop7Day() {
    	dump("TOP7", getTop7(Integer.MAX_VALUE));
    }

    private void setCurrentTop7(int day) {
    	mCurrentTop7Day = day;
    	mContext.getSharedPreferences("rp", Context.MODE_PRIVATE).edit().putInt(":TOP7", day).commit();
	}
	
    private int getTop7Point() {
    	if(mCurrentTop7Day < 0)  {
    		mCurrentTop7Day = mContext.getSharedPreferences("rp", Context.MODE_PRIVATE).getInt(":TOP7", top7day());
    	}
    	return mCurrentTop7Day;
	}
	
	private void setCurrentTop1(int day) {
		mCurrentTop1Day = day;
		mContext.getSharedPreferences("rp", Context.MODE_PRIVATE).edit().putInt(":TOP1", day).commit();
	}
	
	private int getTop1Point() {
		if(mCurrentTop1Day < 0) {
			mCurrentTop1Day = mContext.getSharedPreferences("rp", Context.MODE_PRIVATE).getInt(":TOP1", top1day());
		}
		return mCurrentTop1Day;
	}
	
	
	private void recordTop1(SQLiteDatabase db, final CleanItem item) {
		Cursor cursor = db.query(CleanItem.TABLE_NAME_TOP1, null, CleanItem.Columns.NAME+"=?", new String[] { item.name() }, null, null, null);
		CleanItem _item = new CleanItem(item);
		long value = 0;
		try {
			
			if(cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				value = cursor.getLong(cursor.getColumnIndex(CleanItem.Columns.VALUE));
				_item.value_acc(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		
		db.replace(CleanItem.TABLE_NAME_TOP1, "", _item.toContentValues());
    }

	private void recordTop7(SQLiteDatabase db, CleanItem item) {
		Cursor cursor = db.query(CleanItem.TABLE_NAME_TOP7, null, CleanItem.Columns.NAME+"=?", new String[] { item.name() }, null, null, null);
		CleanItem _item = new CleanItem(item);
		long value = 0;
		try {
			
			if(cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				value = cursor.getLong(cursor.getColumnIndex(CleanItem.Columns.VALUE));
				_item.value_acc(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		
		db.replace(CleanItem.TABLE_NAME_TOP7, "", _item.toContentValues());
    }
}
