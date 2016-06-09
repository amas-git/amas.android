package com.cmcm.newsindia;

import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cmcm.onews.AppsFlyerHelper;
import com.cmcm.onews.MainEntry;
import com.cmcm.onews.R;
import com.cmcm.onews.fragment.NewsBaseSplFragment;
import com.cmcm.onews.infoc.newsindia_act2;

import com.cmcm.onews.ui.NewsBaseUIActivity;
import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.LauncherUtil;
import com.cmcm.onews.util.ShortcutUtil;
import com.cmcm.onews.util.UIConfigManager;
import com.cmcm.terminal.Console;

/* BUILD_CTRL:IF:OU_VERSION_ONLY */
import com.facebook.appevents.AppEventsLogger;

/* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */



public class MainActivity extends NewsBaseUIActivity{
    private FragmentManager mFragmentManager;
    private NewsBaseSplFragment mFragmentSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onews__activity_splash);
        mFragmentManager = getSupportFragmentManager();

        BackgroundThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                new newsindia_act2().source(getFrom()).report();
            }
        }, 1 * 1000);

        // 广告平台
        /* BUILD_CTRL:IF:OU_VERSION_ONLY */
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                AppsFlyerHelper.getInstance().startTracking();
            }
        });
        /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */

        /* BUILD_CTRL:IF:OU_VERSION_ONLY */
        initCount();
        /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */

        addFragment();
        if(MainEntry.mStartTime > 0){
            MainEntry.mStartTime = System.currentTimeMillis() - MainEntry.mStartTime;
            Console.getInstance().write("main", "LaunchTime: " + MainEntry.mStartTime);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* BUILD_CTRL:IF:OU_VERSION_ONLY */
        AppEventsLogger.activateApp(this);
        /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null != mFragmentSp){
            mFragmentSp.initReport();
        }
        /* BUILD_CTRL:IF:OU_VERSION_ONLY */
        AppEventsLogger.deactivateApp(this);
        /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
    }

    @WorkerThread
    @Override
    protected void onInitBackground() {
        // 创建快捷方式
        createShorcut();
    }

    /**
     * Facebook like
     */
    private void initCount(){
        UIConfigManager cfg = UIConfigManager.getInstanse(MainActivity.this);
        int countNum = cfg.getNEWS_APP_COUNT_NUM();
        countNum++;
        cfg.setNEWS_APP_COUNT_NUM(countNum);
    }

    private void createShorcut(){
        if(!LauncherUtil.isShortCutExist(this)&&!UIConfigManager.getInstanse(MainActivity.this).getNEWS_APP_CREATE_SHORCUT()){
            ShortcutUtil.addShortcut(this);
            UIConfigManager.getInstanse(MainActivity.this).setNEWS_APP_CREATE_SHORCUT();
        }
    }

    /**
     * 添加内容Fragment
     */
    private void addFragment(){
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mFragmentSp != null) {
            transaction.show(mFragmentSp);
        } else {
            mFragmentSp = mFragmentSp.newInstance();
            transaction.add(R.id.fragment_splash_content, mFragmentSp, NewsBaseSplFragment.class.getSimpleName());
        }
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
          /* BUILD_CTRL:IF:OU_VERSION_ONLY */
        AppEventsLogger.deactivateApp(this);
        /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
    }
}