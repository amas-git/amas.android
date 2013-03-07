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

package org.whitetree.sched.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fanxer.midian.service.AppService;

public class OnAlarmReceiver extends BroadcastReceiver {

	// This intent is sent from the notification when the user cancels the snooze alert.
	public static final String CANCEL_SNOOZE = "com.fanxer.midian.intent.action.cancel_snooze";

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("OnAlarmReceiver: RECV ----------------------- " + intent );
		// BaseWakefulIntentService.sendWakefulWork(context, AppService.class);
		if ("com.fanxer.midian.intent.action.alarm_started".equals(intent.getAction())) {
		    long alarmId = intent.getLongExtra("alarm_id", 0);
			context.startService(AppService.getLaunchIntentWithId(context, alarmId));
            return;
        } 
		
	}
}
