package s.a.m.a.sched.sched.log;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class WtLog {
	public static final Uri CONTENT_URI = Uri.parse("content://"+LogProvider.AUTHORITY+"/logs");
	public static final String CONTENT_LOGS       = "vnd.android.cursor.dir/vnd.bonny.logs";
	public static final String MESG               = "mesg";
	public static final String TIME               = "ctime";
	public static boolean mTee = true;
	
	
	public static void m(Context context, String message) {
		ContentValues values = new ContentValues();
		values.put(WtLog.MESG, message);
		context.getContentResolver().insert(WtLog.CONTENT_URI, values);
		
		if(mTee) {
			Log.e(":-------:",message);
		}
	}
	
	public static void clearAll(Context context) {
		context.getContentResolver().delete(WtLog.CONTENT_URI, null, null);
	}
};
