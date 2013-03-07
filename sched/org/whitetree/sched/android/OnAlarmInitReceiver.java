
package org.whitetree.sched.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnAlarmInitReceiver extends BroadcastReceiver {

    /**
     * Sets alarm on ACTION_BOOT_COMPLETED.  Resets alarm on TIME_SET, TIMEZONE_CHANGED
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (context.getContentResolver() == null) {
            // AlarmInitReceiver: FAILURE unable to get content resolver.  Alarms inactive.
            return;
        }
        
        // If action is ACTION_BOOT_COMPLETED, do some check work.
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            // ep: Alarms.saveSnoozeAlert(context, -1, -1);
            // ep: Alarms.disableExpiredAlarms(context);
        }
        
        // Resets alarm (ep: Alarms.setNextAlert(context);)
        
    }
}
