/***
 Copyright (c) 2009 CommonsWare, LLC

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package a.m.a.s.sched.android;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnAlarmReceiver extends BroadcastReceiver {
    public static final String PACKAGE = "a.m.a.s.sched.android";
    public static final String ACTION_ALARM = PACKAGE + ".ACTION_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        android.util.Log.i("sched", "yeap !!! " + KEY.EXPECTED_TIME(intent, 0L));
        if(ACTION_ALARM.equals(action)) {
            SchedService.start_ACTION_SCHED(context, KEY.EXPECTED_TIME(intent, 0L));
        }
    }

    public static Intent INTENT_ACTION_ALARM(Context context, String message) {
        Intent intent = new Intent();
        intent.setClass(context, OnAlarmReceiver.class);
        intent.setAction(ACTION_ALARM);
        return intent;
    }

    public static void ACTION_ALARM(Context context, long time, String message) {
        schedAlarm(context, INTENT_ACTION_ALARM(context, message), time);
    }

    public static void schedAlarm(Context context, Intent intent, long atTimeInMillis) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent.putExtra(KEY.EXPECTED_TIME, atTimeInMillis);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, atTimeInMillis, sender);
    }

}
