package s.a.m.a.holdon.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import client.core.Core;
import client.core.model.Event;
import s.a.m.a.holdon.HTask;
import s.a.m.a.holdon.R;
import s.a.m.a.holdon.editor.ListEditor;
import s.a.m.a.holdon.editor.TextEditor;
import s.a.m.a.holdon.event.EVENT;
import s.a.m.a.holdon.event.EventNewTask;
import s.a.m.a.holdon.storage.LocalStorage;
import s.a.m.a.sched.sched.Sched;
import s.a.m.a.service.LocalService;

/**
 * Created by amas on 15-5-17.
 */
public class NewHTaskFragment extends Fragment {
    public static NewHTaskFragment newInstance() {
        return new NewHTaskFragment();
    }

    RelativeLayout mPanel_1;
    RelativeLayout mPanel_2;
    RelativeLayout mPanel_3;
    RelativeLayout mPanel_4;


    HTask hTask = null;
    long milestone_target_round;

    TextView tvTitle;
    TextView tvSched;
    TextView tvTarget;
    Button btnNewTask;

    Button mSched3;
    Button mSched7;
    Button mSched21;
    Button mSched30;
    Button mSched_Any;

    List<Button> schedGroup = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_newhtask, container, false);
        mPanel_1 = (RelativeLayout) root.findViewById(R.id.panel_1);
        //mPanel_2 = (RelativeLayout) root.findViewById(R.id.panel_2);
        mPanel_3 = (RelativeLayout) root.findViewById(R.id.panel_3);
        mPanel_4 = (RelativeLayout) root.findViewById(R.id.panel_4);
        mPanel_1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPanel_1(v);
            }
        });
//        mPanel_2.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickPanel_2(v);
//            }
//        });
        mPanel_3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPanel_3(v);
            }
        });
        mPanel_4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPanel_4(v);
            }
        });

        tvTitle = (TextView) root.findViewById(R.id.title);
        //tvSched = (TextView) root.findViewById(R.id.sched);
        tvTarget = (TextView) root.findViewById(R.id.target);
        btnNewTask = (Button) root.findViewById(R.id.btn_new_task);
        btnNewTask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveNewTask();
            }
        });
        hTask = HTask.create("最难开始的地方", Sched.SCHED_EVERY_DAY().expr(), 15);

        mSched3 = (Button) root.findViewById(R.id.sched3);
        mSched7 = (Button) root.findViewById(R.id.sched7);
        mSched21 = (Button) root.findViewById(R.id.sched21);
        mSched30 = (Button) root.findViewById(R.id.sched30);
        mSched_Any = (Button) root.findViewById(R.id.sched_any);

        schedGroup.add(mSched3);
        schedGroup.add(mSched7);
        schedGroup.add(mSched21);
        schedGroup.add(mSched30);
        schedGroup.add(mSched_Any);

        select(mSched21);
        milestone_target_round = 21;
        mSched3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSched3(v);
            }
        });
        mSched7.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSched7(v);
            }
        });
        mSched21.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSched21(v);
            }
        });
        mSched30.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSched30(v);
            }
        });
        mSched_Any.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSched_Any(v);
            }
        });

        display(hTask);
        return root;
    }

    public void select(Button button) {
        for(Button b : schedGroup) {
            if(b.equals(button)) {
                b.setSelected(true);
            } else {
                b.setSelected(false);
            }
        }
    }

    public void display(HTask htask) {
        tvTitle.setText(htask.title());
        //tvSched.setText(htask.checkin_sched());
        //tvTarget.setText(htask.target());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onClickPanel_1(View v) {
        TextEditor.startEdit(this, EDIT_TITLE, R.string.promote_edit_title, R.string.hint_edit_title, 15);
    }

    private static final int EDIT_TITLE = 100;
    private static final int EDIT_SCHED = 200;
    private static final int EDIT_TARGET = 300;

    public void onClickPanel_2(View v) {
        ListEditor.startEdit(this, EDIT_SCHED, R.array.sched_name, R.array.sched_expr);
    }

    public void onClickPanel_3(View v) {
        ListEditor.startEdit(this, EDIT_TARGET, R.array.target_name, R.array.target_value);
    }

    public void onClickPanel_4(View v) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case EDIT_SCHED: {
                    int pos = ListEditor.getResultPosition(data);
                    //hTask.checkin_sched(getResources().getStringArray(R.array.sched_expr)[pos]);
                }
                break;
                case EDIT_TARGET: {
                    int pos = ListEditor.getResultPosition(data);
                    //hTask.setTarget(getResources().gettringArray(R.array.target_value)[pos]);
                }
                break;
                case EDIT_TITLE: {
                    String title = TextEditor.getResultText(data);
                    System.out.println("title=" + title);
                    hTask.title(title);
                }
                break;
                default:
                    break;
            }
            display(hTask);
        }
    }

    public void onSaveNewTask() {
        hTask.milestone_target_round(milestone_target_round);
        LocalService.start_ACTION_ADD_TASK(getActivity(), hTask);
        getActivity().finish();
    }

    public void fireEvent(Event event) {
        event.setTo(EVENT.GROUP_UI);
        Core.I().push(event);
    }

    public void onClickSched3(View v) {
        select((Button)v);
        milestone_target_round = 3;
    }

    public void onClickSched7(View v) {
        select((Button) v);
        milestone_target_round = 7;
    }

    public void onClickSched21(View v) {
        select((Button) v);
        milestone_target_round = 21;
    }

    public void onClickSched30(View v) {
        select((Button) v);
        milestone_target_round = 30;
    }

    public void onClickSched_Any(View v) {
        select((Button) v);
        milestone_target_round = 365;
    }

}
