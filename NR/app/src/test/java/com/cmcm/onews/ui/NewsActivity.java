package com.cmcm.onews.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcm.cloudconfig.CloudConfigGetter;
import com.cmcm.cloudconfig.CloudConfigKey;
import com.cmcm.onews.C;
import com.cmcm.onews.MainEntry;
import com.cmcm.onews.NewsL;
import com.cmcm.onews.R;
import com.cmcm.onews.event.EventNetworkChanged;
import com.cmcm.onews.event.EventDeleteSingle;
import com.cmcm.onews.event.ONewsEvent;
import com.cmcm.onews.fragment.NewsFragment;
import com.cmcm.onews.infoc.newsindia_gcm;
import com.cmcm.onews.model.ONews;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.system.NET_STATUS;
import com.cmcm.onews.ui.widget.DislikePopup;
import com.cmcm.onews.ui.widget.FaceBookDialogLike;
import com.cmcm.onews.ui.widget.INewsNotifyDialogClick;
import com.cmcm.onews.ui.widget.PopupWindowUtils;
import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.ConflictCommons;
import com.cmcm.onews.util.DimenSdkUtils;
import com.cmcm.onews.util.DimenUtils;
import com.cmcm.onews.util.SDKConfigManager;
import com.cmcm.onews.util.SdkPackageUtils;
import com.cmcm.onews.util.StringUtils;
import com.cmcm.onews.util.UIConfigManager;
import com.cmcm.onews.util.gcm.GCM_CMTrackServer;

import java.lang.reflect.Field;

import com.cmcm.update.UpdateHelper;

public class NewsActivity extends NewsBaseUIActivity implements View.OnClickListener {
    private long mlExitTime = 0;

    private ImageView mTitleLogo;
    private PopupWindow mSettingPopup;

    private FaceBookDialogLike mFBLikeDialog;

    private FragmentManager mFragmentManager;
    private NewsFragment newsFragment;
    private ViewStub mStubDislike;
    private RelativeLayout mGuideDislike;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, NewsActivity.class);
        activity.startActivity(intent);
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, NewsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != savedInstanceState) {
            try {
                newsFragment = (NewsFragment) getSupportFragmentManager().findFragmentByTag(NewsFragment.class.getSimpleName());
            } catch (Exception e) {
                e.printStackTrace();
                newsFragment = null;
            }
        }

        setContentView(R.layout.onews__activity_news);
        mFragmentManager = getSupportFragmentManager();

        findViewById(R.id.news_setting).setOnClickListener(this);
        findViewById(R.id.title_logo).setOnClickListener(this);
        mTitleLogo = (ImageView) findViewById(R.id.title_logo);
        anchor = findViewById(R.id.news_dislike_anchor);
        mStubDislike = (ViewStub) findViewById(R.id.onews__guide_dislike);

        initNotify();
        initNews();
        versionCtrl();

        showNotify(NET_STATUS.current(C.getAppContext()));
    }

    private void initNotify() {
        mNotify = (RelativeLayout) findViewById(R.id.news_notify);
        mNotify.setOnClickListener(this);
        findViewById(R.id.news_notify_cancle).setOnClickListener(this);
        findViewById(R.id.news_notify_ok).setOnClickListener(this);
        mNotifyWifi = (ImageView) findViewById(R.id.news_notify_wifier);
        mNotify2G = (LinearLayout) findViewById(R.id.news_notify_rl);
        mNotifyDes = (TextView) findViewById(R.id.onews_notify_des);
    }

    public void initNews() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (newsFragment != null) {
            if (L.DEBUG) L.newslist("NewsActivity NewsFragment not null");
            transaction.show(newsFragment);
        } else {
            if (L.DEBUG) L.newslist("NewsActivity NewsFragment null");
            newsFragment = NewsFragment.newInstance(0);
            transaction.add(R.id.news_fragment, newsFragment, NewsFragment.class.getSimpleName());
        }
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                initFaceBookDialog();
            }
        }, 10000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissSettingPopup();

        reportDuration(APP_TIME_LIST);
        adder.zero();

        if (null != dislikePopup) {
            dislikePopup.dismiss();
        }

        if(null != guidePopup){
            guidePopup.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (null == v) {
            return;
        }
        switch (v.getId()) {
            case R.id.news_setting:
                Intent intent = new Intent(NewsActivity.this, NewsSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.news_notify_cancle:
                dimissNotify();
                break;
            case R.id.news_notify_ok:
                clickNotifyOk();
                break;
            case R.id.news_notify:
                dimissNotify();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
            && event.getAction() == KeyEvent.ACTION_DOWN) {

            if (System.currentTimeMillis() - mlExitTime > 2000) {
                Toast.makeText(this, StringUtils.getString(C.getAppContext(), R.string.onews__exitTips), Toast.LENGTH_SHORT).show();
                mlExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NewsL.timeline("ON_DESTORY");
        reportGCMStatus();
        hasShow2GNotify = false;
        BackgroundThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                fixInputMethodManagerLeak(NewsActivity.this);
            }
        }, 500);
    }

    private void prepareSettingPopup() {
        if (mSettingPopup == null) {
            mSettingPopup = PopupWindowUtils.getBasePopupWindow(R.layout.onews__to_setting_popup, this, DimenUtils.dp2px(C.getAppContext(), 210), WindowManager.LayoutParams.WRAP_CONTENT, R.drawable.onews__to_setting_pop_bg);
        }

        initSettingPopup();
    }

    private void initSettingPopup() {
        if (null == mSettingPopup) {
            return;
        }

        View root = mSettingPopup.getContentView();

        if (null == root) {
            return;
        }

        View click = root.findViewById(R.id.popup_root);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSettingPopup();
            }
        });

        TextView title = (TextView) root.findViewById(R.id.popup_title);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSettingPopup();
                Intent intent = new Intent(NewsActivity.this, NewsSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onEventInUiThread(ONewsEvent event) {
        super.onEventInUiThread(event);
        if (isFinishing()) {
            return;
        }

        if (event instanceof EventNetworkChanged) {
            onHandleEvent_EventNetworkChanged((EventNetworkChanged) event);
        } else {
            if (null != newsFragment) {
                newsFragment.onEventInUiThread(event);
            }
        }
    }

    protected void reportGCMStatus() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String regID = sharedPreferences.getString(GCM_CMTrackServer.GCM_TOKEN, "");
        int gcm_status = 0;
        if (regID.length() > 0) {
            gcm_status = newsindia_gcm.GCM_SUCCESS;
        } else {
            gcm_status = newsindia_gcm.GCM_FAIL;
        }
        newsindia_gcm report_gcm_status = new newsindia_gcm();
        report_gcm_status.successful(gcm_status);
        report_gcm_status.report();
    }

    /**
     * 版本控制ui显示
     */
    private void versionCtrl() {
        if (ConflictCommons.isCNVersion()) {
            /* BUILD_CTRL:IF:CNVERSION */
            mTitleLogo.setImageResource(R.drawable.onews__title_logo_cn);
            /* BUILD_CTRL:ENDIF:CNVERSION */
        } else {
            /* BUILD_CTRL:IF:NOTCNVERSION */
            mTitleLogo.setImageResource(R.drawable.onews__title_logo);
            /* BUILD_CTRL:ENDIF:NOTCNVERSION */
        }
    }

    /********
     * 我是分割线
     ********************************/

    @Override
    protected void onStart() {
        super.onStart();
        checkUpdate();
    }

    private void checkUpdate() {
        UpdateHelper.getInstance().checkUpdate(this);
    }

    /**************************************************/

    /**
     * 解决Activity退出InputmethodManager没有销毁的问题
     *
     * @param destContext
     */
    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                f = imm.getClass().getDeclaredField(param);
                if (f.isAccessible() == false) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (destContext.getClass().isInstance(v_get.getContext()) ||
                        (null != v_get.getContext() && v_get.getContext() instanceof MainEntry)) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void initFaceBookDialog() {
        if (null == mFBLikeDialog) {
            /**
             * 引导逻辑
             * 1、手机必须已安装Facebook
             * 2、是否被点击过GO 如果点击过 则永远不展示
             * 3、启动次数大于4 并且数字是云端可控制
             * 4、当用户点击No,thinks过后，一个月内不再显示
             * 5、在新闻列表页延时10秒 才弹窗引导
             */
            int remoteServerNum = geFbShowNum();//魔方配置开关次数
            int mDay = 30 * 24;//一个月
            int mHour = 60 * 60 * 1000;//24小时
            int countNum = UIConfigManager.getInstanse(NewsActivity.this).getNEWS_APP_COUNT_NUM();

            boolean isInstall = SdkPackageUtils.isHasPackage(NewsActivity.this, "com.facebook.katana");
            boolean isClickGo = UIConfigManager.getInstanse(NewsActivity.this).getNEWS_FB_GUIDE_GO_IS_CLICK();
            boolean isClickNo = UIConfigManager.getInstanse(NewsActivity.this).getNEWS_FB_GUIDE_NO_IS_CLICK();

            long lastTime = UIConfigManager.getInstanse(NewsActivity.this).getLAST_TIME_NEWS_FB_GUIDE_CLICK_NO();
            long currentTime = System.currentTimeMillis();
            long timeDiffDay = (currentTime - lastTime) / mHour;

            if (L.DEBUG) {
                L.newslist("fackbook--> app is install===" + isInstall);
                L.newslist("fackbook--> app enter num===" + countNum);
                L.newslist("fackbook--> go is click ===" + isClickGo);
                L.newslist("fackbook--> no is click ===" + isClickNo);
                L.newslist("fackbook--> currentTime===" + currentTime);
                L.newslist("fackbook--> lastTime ===" + lastTime);
                L.newslist("fackbook--> no is click time diff day ===" + timeDiffDay);
                L.newslist("fackbook--> no is click time day ===" + mDay);
                L.newslist("fackbook--> no is click time is  ===" + (timeDiffDay > mDay));
            }

            //1、手机必须已安装Facebook
            if (isInstall) {
                //2、是否被点击过GO 如果点击过 则永远不展示
                if (isClickGo) {
                    if (isClickNo) {
                        //3、启动次数大于4 并且数字是云端可控制
                        if (countNum >= remoteServerNum) {
                            mFBLikeDialog = new FaceBookDialogLike(this, new INewsNotifyDialogClick() {
                                @Override
                                public void clickContinue() {
                                    if (null != mFBLikeDialog) {
                                        UIConfigManager.getInstanse(NewsActivity.this).setNEWS_FB_GUIDE_GO_IS_CLICK();//点击过
                                        startActivity(getOpenFacebookIntent(NewsActivity.this));
                                        mFBLikeDialog.dismissDialog();
                                    }
                                }

                                @Override
                                public void clickCancel() {
                                    if (null != mFBLikeDialog) {
                                        if (L.DEBUG) L.newslist("fackbook--> clickCancel");
                                        UIConfigManager.getInstanse(NewsActivity.this).setNEWS_FB_GUIDE_NO_IS_CLICK();
                                        UIConfigManager.getInstanse(NewsActivity.this).setLAST_TIME_NEWS_FB_GUIDE_CLICK_NO(System.currentTimeMillis());
                                        mFBLikeDialog.dismissDialog();
                                    }
                                }
                            });
                            mFBLikeDialog.showDialog();
                        }
                    } else {
                        //4、当用户点击No,thinks过后，一个月内不再显示
                        if (timeDiffDay > mDay) {
                            if (countNum >= remoteServerNum) {
                                mFBLikeDialog = new FaceBookDialogLike(this, new INewsNotifyDialogClick() {
                                    @Override
                                    public void clickContinue() {
                                        if (null != mFBLikeDialog) {
                                            UIConfigManager.getInstanse(NewsActivity.this).setNEWS_FB_GUIDE_GO_IS_CLICK();//点击过
                                            startActivity(getOpenFacebookIntent(NewsActivity.this));
                                            mFBLikeDialog.dismissDialog();
                                        }
                                    }

                                    @Override
                                    public void clickCancel() {
                                        if (null != mFBLikeDialog) {
                                            if (L.DEBUG) L.newslist("fackbook--> clickCancel");
                                            UIConfigManager.getInstanse(NewsActivity.this).setNEWS_FB_GUIDE_NO_IS_CLICK();
                                            UIConfigManager.getInstanse(NewsActivity.this).setLAST_TIME_NEWS_FB_GUIDE_CLICK_NO(System.currentTimeMillis());
                                            mFBLikeDialog.dismissDialog();
                                        }
                                    }
                                });
                                mFBLikeDialog.showDialog();
                            }
                        }
                    }
                }
            }

        }
    }

    private int geFbShowNum() {
        return CloudConfigGetter.getIntValue(CloudConfigKey.FUCTION_TYPE_MAIN, CloudConfigKey.FB_GUIDE_SECTION, CloudConfigKey.FB_GUIDE_SECTION_FREQ, 5);
    }

    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/202844560051401"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/cminstanews/"));
        }
    }

    /**
     *  网络提示，数据刷新提示等
     */
    protected RelativeLayout mNotify;
    protected ImageView mNotifyWifi;
    protected LinearLayout mNotify2G;
    protected TextView mNotifyDes;
    protected volatile boolean hasShow2GNotify = false;

    protected static final int NOTIFYSTATE_2G = 1;
    protected int NOTIFYSTATE = 0;

    private void showNotify2g() {
        if(!SDKConfigManager.getInstanse(C.getAppContext()).getNEWS_ITEM_SHOWIMG()){
            return;
        }

        if(hasShow2GNotify){
            return;
        }

        NOTIFYSTATE = NOTIFYSTATE_2G;
        hasShow2GNotify = true;
        mNotify.setVisibility(View.VISIBLE);
        mNotify2G.setVisibility(View.VISIBLE);
        mNotifyDes.setText(R.string.onews__notify_wifier_2g);
        mNotifyWifi.setImageResource(R.drawable.onews__notify_2g);
        mNotifyWifi.setVisibility(View.VISIBLE);

        reportChangestyle(1, 1, 2);
    }

    private void clickNotifyOk() {
        dimissNotify();

        if(NOTIFYSTATE == NOTIFYSTATE_2G){
            SDKConfigManager.getInstanse(C.getAppContext()).setNEWS_ITEM_SHOWIMG(false);
            if(UIConfigManager.getInstanse(C.getAppContext()).getNEWS_2G_GUIDE_TOAST()){
                UIConfigManager.getInstanse(C.getAppContext()).setNEWS_2G_GUIDE_TOAST();
                showSettingPopup();
            }

            reportChangestyle(1, 2, 1);
        }
    }

    private void showNotify(NET_STATUS current) {
        if(current == NET_STATUS.MOBILE && current.getSupType() == NET_STATUS.MOBILE_STATUS._2G) {
            showNotify2g();
        }else {
            dimissNotify();
        }
    }

    private void dimissNotify(){
        if (null != mNotify) {
            mNotify.setVisibility(View.GONE);
        }
    }

    protected void onHandleEvent_EventNetworkChanged(EventNetworkChanged event){
        showNotify(event.curr());
        if(null != newsFragment){
            newsFragment.onEventInUiThread(event);
        }
    }

    private void showSettingPopup() {
        prepareSettingPopup();
        dismissSettingPopup();

        mSettingPopup.showAsDropDown(findViewById(R.id.news_title_popup_anchor), -DimenUtils.dp2px(C.getAppContext(), 220), 0);

        BackgroundThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            dismissSettingPopup();
                        }
                    }
                });
            }
        }, 10 * 1000l);
    }

    private void dismissSettingPopup() {
        if (mSettingPopup == null) {
            return;
        }

        if (mSettingPopup.isShowing()) {
            mSettingPopup.dismiss();
        }
    }

    @Override
    protected void darken() {
        findViewById(R.id.news_root).animate().rotation(0).alpha(0.5f);
    }

    @Override
    protected void reply() {
        findViewById(R.id.news_root).animate().rotation(0).alpha(1f);
    }

    @Override
    protected void remove(ONews news, ONewsScenario scenario) {
        if (null != newsFragment) {
            newsFragment.onEventInUiThread(new EventDeleteSingle(news, scenario));
        }
    }

    @Override
    public void showDislikeGuide(final int[] location) {
        if(null == mStubDislike || null == location || location.length < 2){
            return;
        }
        if(null == mGuideDislike){
            mGuideDislike = (RelativeLayout) mStubDislike.inflate();
        }
        mGuideDislike.setVisibility(View.VISIBLE);
        ValueAnimator tansX = ObjectAnimator.ofFloat(mGuideDislike, "translationX", 0, location[0]);
        tansX.setDuration(0);
        ValueAnimator tansY = ObjectAnimator.ofFloat(mGuideDislike, "translationY", 0, location[1]);
        tansY.setDuration(0);
        AnimatorSet start = new AnimatorSet();
        start.playTogether(tansX, tansY);
        if(mGuideDislike.getHeight() > 0){
            showPopDislikeGuide(location);
        }else{
            mGuideDislike.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mGuideDislike.getHeight() > 0) {
                        showPopDislikeGuide(location);
                        mGuideDislike.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        }
        start.start();
    }

    private void showPopDislikeGuide(int[] location){
        UIConfigManager.getInstanse(C.getAppContext()).setNEWS_GUIDE_DISLIKE(false);
        if (null == guidePopup) {
            guidePopup = new NewsGuidePopup(new DislikePopup.IDislikeDismiss() {
                @Override
                public void onDismiss() {
                    if (null != mGuideDislike) {
                        mGuideDislike.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onRemove(ONews news, ONewsScenario scenario) {

                }
            });
        }

        guidePopup.showDislikeGuide(mGuideDislike, location[0]);
    }

    @Override
    public void showOfflineGuide(final int[] location) {
        if(null == anchor || null == location || location.length < 2){
            return;
        }
        ValueAnimator tansX = ObjectAnimator.ofFloat(anchor, "translationX", 0, location[0]);
        tansX.setDuration(0);
        ValueAnimator tansY = ObjectAnimator.ofFloat(anchor, "translationY", 0, location[1]);
        tansY.setDuration(0);
        AnimatorSet start = new AnimatorSet();
        start.playTogether(tansX,tansY);
        start.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                UIConfigManager.getInstanse(C.getAppContext()).setNEWS_GUIDE_OFFLINE(false);
                if (null == guidePopup) {
                    guidePopup = new NewsGuidePopup(new DislikePopup.IDislikeDismiss() {
                        @Override
                        public void onDismiss() {
                            if (null != mGuideDislike) {
                                mGuideDislike.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onRemove(ONews news, ONewsScenario scenario) {

                        }
                    });
                }

                guidePopup.showOfflineGuide(anchor, location[0]);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        start.start();
    }

    private NewsGuidePopup guidePopup;

    public void dismissDislikeGuidePopup() {
        if(null != guidePopup){
            guidePopup.dismiss();
        }
    }
}
