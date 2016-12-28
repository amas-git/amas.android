package s.a.m.a.holdon.ui;

import client.core.model.Event;
import s.a.m.a.holdon.R;
import s.a.m.a.holdon.event.EventDayChanged;
import s.a.m.a.holdon.event.TimeMonitor;
import s.a.m.a.holdon.ui.base.BaseFragmentActivity;
import s.a.m.a.sched.sched.TimeUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * @author amas
 * @since 2015年 06月 28日 星期日 16:16:01 CST
 */
public class ChangeTimeActivity extends BaseFragmentActivity {
    TextView mTv_Time;
    public static Intent getLaunchIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ChangeTimeActivity.class);
        return intent;
    }

    public static void startDefault(Context context) {
        context.startActivity(getLaunchIntent(context));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changetimeactivity);
        mTv_Time = (TextView) findViewById(R.id.tv_time);
        mTv_Time.setText(""+TimeUtils.ofInt_YYYYMMDD(-1));
        TimeMonitor.getInstance().start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimeMonitor.getInstance().stop();
    }

    public void onIncDay(View v) {
        TimeUtils.offset_inc(TimeUtils.ONE_DAY);
    }

    public void onDecDay(View v) {
        TimeUtils.offset_dec(TimeUtils.ONE_DAY);
    }

    @Override
    protected void onEventInUiThread(Event event) {
        if(event instanceof EventDayChanged) {
            onEvent_EventDayChanged((EventDayChanged)event);
        }
    }

    private void onEvent_EventDayChanged(EventDayChanged event) {
        mTv_Time.setText(event.toString());
    }
}
