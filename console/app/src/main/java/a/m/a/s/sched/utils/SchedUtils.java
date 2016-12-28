package a.m.a.s.sched.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import java.util.Calendar;

import a.m.a.s.sched.android.SchedTester;

/**
 * re-schedule all pending alarms.
 * @author amas
 *
 */
public class SchedUtils {
	/**
	 * @param context
	 * @param intent
	 * @param atTimeInMillis
	 */
	public static void schedAlarm(Context context, Intent intent, long atTimeInMillis) {
		if(atTimeInMillis < 0) {
			android.util.Log.i("sched", "OVER");
		}
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(atTimeInMillis);
		android.util.Log.i("sched", "[schedAlarm] -> " + SchedTester.__(c));
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, atTimeInMillis, sender);
	}
		
}
