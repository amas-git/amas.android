package s.a.m.a.holdon.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;

import client.core.model.Event;
import s.a.m.a.holdon.event.EventCheckinResult;
import s.a.m.a.holdon.event.EventNewTask;
import s.a.m.a.holdon.ui.base.BaseListFragment;
import s.a.m.a.sched.sched.android.OnAlarmReceiver;

import java.util.ArrayList;
import java.util.List;

import s.a.m.a.holdon.HTask;
import s.a.m.a.holdon.R;

/**
 * Created by amas on 15-5-17.
 */
public class HTaskListFragment extends BaseListFragment {
    public static HTaskListFragment newInstance() {
        return new HTaskListFragment();
    }

    Button btn_Test = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_htasklist, container, false);
        btn_Test = (Button) root.findViewById(R.id.btn_test);
        btn_Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTest();
            }

        });
        return root;
    }

    private void onClickTest() {
        //OnAlarmReceiver.ACTION_ALARM(getActivity(), System.currentTimeMillis()+2*60*1000, "HELLO");
        //HTask.generateMilestone(System.currentTimeMillis(), HTask.SCHED_EVERY_WEEK(), 10);
        //HTask.generateMilestone(System.currentTimeMillis(), HTask.SCHED_EVERY_DAY(), 10);
        NewTaskActivity.startDefault(getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        new HTaskLoader() {
            @Override
            protected void onPostExecute(List<HTask> tasks) {
                setListAdapter(new HTaskListAdapter(getActivity(), (ArrayList)tasks));
            }
        }.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onEventInUiThread(Event event) {
        System.out.println(""+event);
        if(event instanceof EventCheckinResult) {
            onHandle_EventCheckinResult((EventCheckinResult)event);
        } else if(event instanceof EventNewTask) {
            onHandle_EventNewTask((EventNewTask)event);
        }
    }

    private void onHandle_EventNewTask(EventNewTask event) {
        HTaskListAdapter().add(event.htask);
    }

    public HTaskListAdapter HTaskListAdapter() {
        Adapter adapter = getListAdapter();
        if(adapter != null && adapter instanceof  HTaskListAdapter) {
            return (HTaskListAdapter)adapter;
        }

        return null;
    }

    private void onHandle_EventCheckinResult(EventCheckinResult event) {
        HTaskListAdapter adapter = HTaskListAdapter();
        if(adapter != null) {
            adapter.update(event.getHTask());
        }
    }
}
