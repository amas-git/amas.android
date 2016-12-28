package s.a.m.a.sched.sched.log;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import s.a.m.a.sched.sched.utils.Log;

public class LogProvider extends ContentProvider {
	public static final String AUTHORITY = "org.whitetree.log";
	public static final String TAG = "CoreProvider";
	private static final int MATCH_LOGS = 1;
	private static final int MATCH_LOGS_BY_TAG = 2;
	
	protected WtLogHelper      mCoreDbHelper;
	protected static UriMatcher mUrlMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static {
		setupUrlMatcher(mUrlMatcher, AUTHORITY);
	}
	


	private class WtLogHelper extends SQLiteOpenHelper {
		
		private static final String DATABASE_NAME = "logs.db" ;
		private static final int DATABASE_VERSION = 1;

		public static final String TABLE_LOGS    = "logs";
		public static final String TRIGGER_TOUCH_LOG_TIME= "trigger_touch_mesg_timestamp";
		
		public WtLogHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/** 
		 * 创建数据库
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_LOGS +" (" 
	                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
	                + "type INTEGER DEFAULT 0,"
	                + "mesg TEXT,"
	                + "ctime DATE DEFAULT (datetime('now','localtime')));");
			
//			db.execSQL("CREATE TRIGGER AFTER INSERT ON " + TABLE_LOGS
//			          +" BEGIN"
//			          +" UPDATE logs " + TRIGGER_TOUCH_LOG_TIME +" SET ctime = DATETIME('NOW')"
//			          +" WHERE rowid = new.rowid;"
//			          +" END;");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			int version = oldVersion;

			if (version != DATABASE_VERSION) {
				clearDatabase(db);
				onCreate(db);
			}
		}

		/**
		 * 删除数据库中的所有表/索引
		 * @param db
		 */
		public void clearDatabase(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
		}
	}
	
	protected static void setupUrlMatcher(UriMatcher matcher, String authority) {
		matcher.addURI(authority, "logs", MATCH_LOGS);
		matcher.addURI(authority, "logs/tag/*", MATCH_LOGS_BY_TAG);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        int result;
        SQLiteDatabase db = mCoreDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            result = __delete(db, uri, selection, selectionArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        System.out.println("-------------- delete --------------- " + result);
        if (result == 0) {
            getContext().getContentResolver().notifyChange(uri, null /* observer */, false /* sync */);
        }
        return result;
	}

	private int __delete(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
		int type = mUrlMatcher.match(uri);
		int result = 0;
		
		switch(type) {
		case MATCH_LOGS: 
			// 简单的删除全部!!!!!!!!!!!!!!!
			result = db.delete(WtLogHelper.TABLE_LOGS, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URL");
		}
		return result;
	}

	@Override
	public String getType(Uri uri) {
		int type = mUrlMatcher.match(uri);
		switch(type) {
		case MATCH_LOGS:
			return WtLog.CONTENT_LOGS;
		default:
			throw new IllegalArgumentException("Unknown URL");
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		__("[INSERT] : " + uri);
		Uri result;
		SQLiteDatabase db = mCoreDbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			result = __insert(uri, values);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		if (result != null) {
			getContext().getContentResolver().notifyChange(uri, null /* observer */, false /* sync */);
		}
		return result;
	}

	/**
	 * e
	 * @param uri
	 * @param values
	 * @return
	 */
	private Uri __insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mCoreDbHelper.getWritableDatabase();
		int type = mUrlMatcher.match(uri);
		long rowId = 0;
		Uri  resultUrl = null;
		
		switch(type) {
		case MATCH_LOGS: 
			rowId = db.insert(WtLogHelper.TABLE_LOGS, "", values);
			if(rowId > 0) {
				resultUrl = Uri.parse(WtLog.CONTENT_URI+"/"+rowId);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URL");
		}
		
		return resultUrl;
	}

	@Override
	public boolean onCreate() {
		mCoreDbHelper = new WtLogHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		// Generate the body of the query
		int match = mUrlMatcher.match(uri);
		switch (match) {
		case MATCH_LOGS:
			qb.setTables(WtLogHelper.TABLE_LOGS);
			sort=WtLog.TIME+" DESC";
			break;
		default:
			throw new IllegalArgumentException("Unknown URL : " + uri);
		}

		SQLiteDatabase db = mCoreDbHelper.getReadableDatabase();
		
		Cursor ret = qb.query(db, projection, selection, selectionArgs, null, null, sort);
		if (ret == null) {
		} else {
			ret.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return ret;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

	
	public static void __(String msg) {
		Log.e(TAG, ""+msg);
	}
}
