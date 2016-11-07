package a.m.a.s.apidemos.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import a.m.a.s.console.Console;
import a.m.a.s.console.ConsoleFragment;
import s.a.m.a.apidemos.R;


public class MainActivity extends AppCompatActivity {

    ViewPager mViewPager = null;

    public static Intent getLaunchIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        return intent;
    }

    public static void startDefault(Context context) {
        context.startActivity(getLaunchIntent(context));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.vp);

        MyPageViewAdapter adapter = new MyPageViewAdapter(getSupportFragmentManager());
        adapter.addFragment("console", ConsoleFragment.newInstance(new Console()));
//        adapter.addFragment(PageFragment1.newInstance());
//        adapter.addFragment(PageFragment2.newInstance());
//        adapter.addFragment(PageFragment3.newInstance());
        mViewPager.setAdapter(adapter);
    }

}
