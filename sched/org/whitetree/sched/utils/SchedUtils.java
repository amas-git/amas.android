package org.whitetree.sched.utils;

import java.util.Calendar;

import org.whitetree.sched.Sched;
import org.whitetree.sched.SchedParser;
import org.whitetree.sched.log.WtLog;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.fanxer.midian.service.AppService;
import com.fanxer.midian.storage.LocalStorage;
import com.fanxer.midian.storage.Md;
import com.fanxer.midian.storage.Md.Alarm;

/**
 * re-schedule all pending alarms.
 * @author amas
 *
 */
public class SchedUtils {
	public static final int DIFFERENCE = 2000;
			
	public static void resched(Context context) {
		reschedAll(context);
		
		Cursor c = LocalStorage.getLatestSchedItem(context);
		if(c != null && c.getCount() > 0) {
			try {
				c.moveToFirst();
				Intent intent  = AppService.getSchedTriggerIntent(context, c.getLong(0));
				long nextAlarm = c.getLong(c.getColumnIndex(Md.Alarm.NEXT_TRIGGER_TIME));
				// TODO(amas): filtered with SQL ???
				if (nextAlarm > System.currentTimeMillis() - DIFFERENCE) {
					Calendar next = Calendar.getInstance();
					next.setTimeInMillis(nextAlarm);
					WtLog.m(context, "下次: " + Sched.__(next) + " ID="+c.getLong(0));
					schedAlarm(context, intent, nextAlarm);
				}
			} finally {
				c.close();
			}
		}
	}
	
	// slow but safe re-sched method 
	public static void reschedAll(Context context) {
		long now = System.currentTimeMillis();
		
		String select = Alarm.NEXT_TRIGGER_TIME+"!=-1";
		Cursor c = context.getContentResolver().query(Alarm.CONTENT_URI,/* TODO: ALAP !!!*/ null, select, null, Alarm.NEXT_TRIGGER_TIME+" DESC" /* limit + windowSize*/);
		ContentResolver cr = context.getContentResolver();
		
		long _id             = 0;
		long _next_alarm     = 0;
		long _next_try       = 0;
		
		String _sched_record = "";
		Sched sched          = null;
		
		if(c != null) {
			try {
				while(c.moveToNext()) {
					_id           = c.getLong(0);  // zero for _id
					_next_alarm   = c.getLong(c.getColumnIndex(Alarm.NEXT_TRIGGER_TIME));
					_sched_record = c.getString(c.getColumnIndex(Alarm.SCHED)); 
					
					// TODO(zhoujb): 需要将失效闹钟排除在外
					if(_next_alarm < now) {
						// 失效，尝试重新计算下一次触发时间
						sched = SchedParser.parse(_sched_record);
						_next_try = sched.evalLatestTriggerTimeAtTimeMillis();
						
						if(_next_try > now && _next_try >= _next_alarm) {
							// 更新排程
							_next_alarm = _next_try;
							ContentValues v = new ContentValues();
							v.put(Alarm.NEXT_TRIGGER_TIME, _next_try);
							cr.update(Uri.parse(Alarm.CONTENT_URI+"/"+_id), v, "_id="+_id, null);
							
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				c.close();
			}
		}
	}
	
	/**
	 * @param context
	 * @param intent
	 * @param atTimeInMillis
	 */
	public static void schedAlarm(Context context, Intent intent, long atTimeInMillis) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, atTimeInMillis, sender);
	}
		
}
