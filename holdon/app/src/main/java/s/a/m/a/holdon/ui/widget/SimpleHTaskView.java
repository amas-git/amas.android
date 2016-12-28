package s.a.m.a.holdon.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import s.a.m.a.holdon.HTask;
import s.a.m.a.holdon.R;

/**
 * Created by amas on 15-5-23.
 */
public class SimpleHTaskView extends RelativeLayout {

    HTaskProgress htProgress = null;
    TextView tvTitle = null;
    Button checkin = null;
    HTask htask = null;
    TextView tvDetails = null;

    public SimpleHTaskView(Context context) {
        super(context);
        init(context);
    }

    public SimpleHTaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.widget_simplehtaskview, null);
        addView(v);

        htProgress = (HTaskProgress) v.findViewById(R.id.htprogress);
        tvTitle = (TextView) v.findViewById(R.id.title);
        checkin = (Button)v.findViewById(R.id.checkin);
        tvDetails = (TextView) v.findViewById(R.id.details);
        checkin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCheckin(getHTask());
            }
        });
    }

    protected HTask getHTask() {
        return htask;
    }

    private void onClickCheckin(HTask htask) {
        if(c != null) {
            c.onCheckIn(this, htask);
        }
    }

    public void setHTask(HTask htask) {
        this.htask = htask;
        tvTitle.setText(htask.title());
        tvDetails.setText(htask.total_check_times() + "/"+htask.target_checkin_times());
        htProgress.setProgressWithAnimation(htask.getPercent());
        if(htask.isFinishedCurrentCheckin()) {
            checkin.setText("已经签到");
        } else {
            checkin.setText("前进一步");
        }
        //tvDetails.setText(""+htask.checkin_times());

//        htProgress.startAnimation();
    }

    public void setHtProgress(int progress) {
        htProgress.setProgressWithAnimation(progress);
    }

    private CTRL c = null;
    public void setCTRL(CTRL c) {
        this.c = c;
    }

    public static class CTRL {
        public void onCheckIn(SimpleHTaskView v, HTask hTask) {
        }
    }
}
