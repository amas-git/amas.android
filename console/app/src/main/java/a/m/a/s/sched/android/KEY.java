package a.m.a.s.sched.android;

import android.content.Intent;

/**
 * Created by amas on 15-5-23.
 */
class KEY {
    public static final String EXPECTED_TIME = ":expected-time";

    public static long EXPECTED_TIME(Intent intent, long v) {
        return intent.hasExtra(EXPECTED_TIME) ? intent.getLongExtra(EXPECTED_TIME, v) : v;
    }


}
