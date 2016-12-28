package s.a.m.a.holdon.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import client.core.model.Event;
import s.a.m.a.holdon.R;
import s.a.m.a.holdon.ui.base.BaseFragmentActivity;
import s.a.m.a.holdon.ui.fragment.MyPageViewAdapter;

public class MainPageActivity extends BaseFragmentActivity {
    ViewPager mViewPager = null;

    public static Intent getLaunchIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MainPageActivity.class);
        return intent;
    }

    public static void startDefault(Context context) {
        context.startActivity(getLaunchIntent(context));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pageactivity);

        mViewPager = (ViewPager) findViewById(R.id.vp);

        MyPageViewAdapter adapter = new MyPageViewAdapter(getSupportFragmentManager());
        adapter.addFragment(HTaskListFragment.newInstance());
        //adapter.addFragment(NewHTaskFragment.newInstance());
        //adapter.addFragment(HTaskDoneListFragment.newInstance());
        mViewPager.setAdapter(adapter);
        setTitle(R.string.app_name);
        setTitleRightButtonText("DEBUG");
    }

    @Override
    protected void onEventInUiThread(Event event) {
        System.out.println("EVENT: " + event);
        MyPageViewAdapter adapter = (MyPageViewAdapter) mViewPager.getAdapter();
        if(adapter != null) {
            adapter.onHandleEvent(event);
        }
    }

    public void onClick_title_button_height(View v) {
        ChangeTimeActivity.startDefault(this);
    }
}
