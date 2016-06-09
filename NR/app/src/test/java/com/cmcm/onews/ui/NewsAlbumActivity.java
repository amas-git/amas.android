package com.cmcm.onews.ui;

import android.animation.ArgbEvaluator;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcm.onews.R;
import com.cmcm.onews.event.EventDeleteSingle;
import com.cmcm.onews.event.ONewsEvent;
import com.cmcm.onews.fragment.NewsAlbumFragment;
import com.cmcm.onews.infoc.newsindia_act2;
import com.cmcm.onews.infoc.newsindia_notification;
import com.cmcm.onews.model.ONews;
import com.cmcm.onews.model.ONewsContentType;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.ui.item.NewsAlgorithmReport;
import com.cmcm.onews.ui.slidr.SliderPanel;
import com.cmcm.onews.ui.slidr.SlidrConfig;
import com.cmcm.onews.util.SDKConfigManager;
import com.cmcm.onews.util.push.PushOutAPI;
import com.cmcm.onews.util.push.comm.PushConst;

/**
 * 专题
 */
public class NewsAlbumActivity extends NewsBaseUIActivity implements View.OnClickListener{
    private NewsAlbumFragment mFragment;
    private ONewsScenario mScenario;
    private ONews mONews;

    private RelativeLayout mTitle;
    private RelativeLayout mBackWhite;
    private RelativeLayout mBackBlack;
    private FrameLayout mAlbumFrame;
    private TextView mTopicTV;

    public static void startNewsAlbum(Context context, ONews news, ONewsScenario scenario, int source) {
        if (null == context || null == news || null == scenario) {
            return;
        }
        Intent intent = new Intent(context, NewsAlbumActivity.class);
        intent.putExtra(KEY_NEWS, news.toContentValues());

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SCENARIO, scenario);
        intent.putExtra(KEY_BUNDLE, bundle);

        intent.putExtra(KEY_FROM, source);
        context.startActivity(intent);
    }

    /**
     * debug 页面进入
     * @param context
     * @param scenario
     * @param contentId
     */
    public static void startByDebug(Context context,ONewsScenario scenario,String contentId){
        if(TextUtils.isEmpty(contentId) || null == scenario){
            return;
        }

        ONews news = new ONews();
        news.contentid(contentId);
        Intent intent = new Intent(context, NewsAlbumActivity.class);
        intent.putExtra(KEY_NEWS, news.toContentValues());

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SCENARIO, scenario);
        intent.putExtra(KEY_BUNDLE, bundle);

        intent.putExtra(KEY_FROM, FROM_DEBUG_SETTING);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(null == intent){
            finish();
        }
        try {
            getIntent().getStringExtra("SecurityCheck");
        } catch (Exception e) {
            finish();
            return;
        }

        try{
            Bundle bundle  = intent.getBundleExtra(KEY_BUNDLE);
            if(null != bundle){
                bundle.setClassLoader(ONewsScenario.class.getClassLoader());
                this.mScenario = bundle.getParcelable(KEY_SCENARIO);
            }

            ContentValues values = intent.getParcelableExtra(KEY_NEWS);
            this.mFrom = intent.getIntExtra(KEY_FROM,DETAIL_FROM_LIST);
            this.mONews = ONews.fromContentValues(values);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(null == mONews || null == mScenario){
            return;
        }
        //TODO 转为专题场景
        mScenario = mScenario.toAlbumScenario();

        //TODO 专题页ctype 为0x20
        if(TextUtils.isEmpty(mONews.ctype())){
            mONews.ctype(ONewsContentType.getSupportedContentType(ONewsContentType.CT_20));
        }

        if(isFromPush()){
            String pushId = intent.getStringExtra(NewsBaseActivity.KEY_PUSHID);
            reportClickToServer(pushId, this.mONews.contentid());
            reportNewsIndia_act2();
        }

        slidr();
        setContentView(R.layout.onews__activity_album);
        anchor = findViewById(R.id.news_dislike_anchor);
        mAlbumFrame = (FrameLayout) findViewById(R.id.news_album_frame);
        mTitle = (RelativeLayout) findViewById(R.id.news_album_title);
        mTopicTV = (TextView) findViewById(R.id.tv_topic);
        mBackWhite = (RelativeLayout) findViewById(R.id.news_album_title_white);
        mBackWhite.setOnClickListener(this);
        mBackBlack = (RelativeLayout) findViewById(R.id.news_album_title_black);
        mBackBlack.setOnClickListener(this);
        init();
        show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void show() {
        boolean showimg = SDKConfigManager.getInstanse(this).getNEWS_ITEM_SHOWIMG();
        if (showimg){// 有图
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAlbumFrame.getLayoutParams();
            layoutParams.addRule(RelativeLayout.BELOW, 0);
            mAlbumFrame.setLayoutParams(layoutParams);
            mTopicTV.setVisibility(View.GONE);

            mTitle.setBackgroundResource(R.drawable.onews__drawable_transparent);
            mBackWhite.setVisibility(View.GONE);
            mBackBlack.setVisibility(View.VISIBLE);
        } else {// 无图
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAlbumFrame.getLayoutParams();
            layoutParams.addRule(RelativeLayout.BELOW, R.id.news_album_title);
            mAlbumFrame.setLayoutParams(layoutParams);
            mTopicTV.setVisibility(View.VISIBLE);

            mTitle.setBackgroundResource(R.drawable.onews__title_bg);
            mBackWhite.setVisibility(View.VISIBLE);
            mBackBlack.setVisibility(View.GONE);
        }
    }

    public void init(){
        mFragment = NewsAlbumFragment.newInstance(mScenario, mONews, mFrom);
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.news_album_frame, mFragment).commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onEventInUiThread(ONewsEvent event) {
        super.onEventInUiThread(event);
        if(isFinishing()){
            return;
        }

        if(null != mFragment){
            mFragment.onEventInUiThread(event);
        }
    }

    @Override
    public void onClick(View v) {
        if(null == v){
            return;
        }

        switch (v.getId()){
            case R.id.news_album_title_white:
            case R.id.news_album_title_black:
                goBack();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        reportDuration(APP_TIME_ALBUM);
        if(null != adder && null != mONews && null != mScenario && !isDebug()){
            reportAlgorithm();
            adder.zero();
        }

        if(null != dislikePopup){
            dislikePopup.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    private void goBack() {
        if (isFromPush() || isFromSplash()) {
            Intent intent = NewsSdk.INSTAMCE.getDetailsBackTo();
            startActivity(intent);
        }
        finish();
    }

    /**
     * 算法上报
     */
    private void reportAlgorithm() {
        if(isFromPush()){
            NewsAlgorithmReport.algorithmNewsReadTime_Album_GCM(mONews, mScenario, adder.end(),upack);
        }else {
            NewsAlgorithmReport.algorithmNewsReadTime_Album(mONews, mScenario, adder.end());
        }
    }

    public String contentid(){
        return null != mONews ? mONews.contentid() : "";
    }

    private String upack;
    public void upack(String u){
        this.upack = u;
    }

    /**
     * push infoc 上报
     * @param pushId
     * @param contentId
     */
    private void reportClickToServer(String pushId, String contentId){
        try {
            //上报点击到后台
            PushOutAPI.reportMessageBehavior(this, PushConst.ACTION.BODY_CLICK, pushId);

            //infoC report
            new newsindia_notification()
                .action(2)
                .newsid(contentId)
                .clicktime((int) (System.currentTimeMillis() / 1000l))
                .report();
        } catch (Exception e ) {e.printStackTrace();}

    }

    /**
     * INFOC埋点 通知栏上报
     */
    private static void reportNewsIndia_act2(){
        newsindia_act2 act2 = new newsindia_act2();
        act2.source(NewsBaseActivity.FROM_PUSH);
        act2.report();
        L.shorcut("从通知栏进入app report");
    }

    @Override
    protected void darken() {
        findViewById(R.id.album_alpha).setVisibility(View.VISIBLE);
    }

    @Override
    protected void reply() {
        findViewById(R.id.album_alpha).setVisibility(View.GONE);
    }

    @Override
    protected void remove(ONews news, ONewsScenario scenario) {
        if(null != mFragment){
            mFragment.onEventInUiThread(new EventDeleteSingle(news, scenario));
        }
    }

    private SlidrConfig config;
    private void slidr() {
        config = new SlidrConfig();
        config.primaryColor(Color.parseColor("#689F38"));
        config.secondaryColor(Color.parseColor("#00000000"));
        config.velocityThreshold(2400);
        config.distanceThreshold(0.4f);
        config.edge(true);//默认右滑退出

        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        View oldScreen = decorView.getChildAt(0);
        decorView.removeViewAt(0);

        // Setup the slider panel and attach it to the decor
        SliderPanel panel = new SliderPanel(this, oldScreen, config);
        panel.setId(com.cmcm.onews.sdk.R.id.onews__slidable_panel);
        oldScreen.setId(com.cmcm.onews.sdk.R.id.onews__slidable_content);
        panel.addView(oldScreen);
        decorView.addView(panel, 0);

        // Set the panel slide listener for when it becomes closed or opened
        panel.setOnPanelSlideListener(new SliderPanel.OnPanelSlideListener() {
            private final ArgbEvaluator mEvaluator = new ArgbEvaluator();

            @Override
            public void onStateChanged(int state) {

            }

            @Override
            public void onClosed() {
                goBack();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onOpened() {

            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSlideChange(float percent) {
                // Interpolate the statusbar color
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && config.areStatusBarColorsValid()) {
                    int newColor = (int) mEvaluator.evaluate(percent, config.getPrimaryColor(), config.getSecondaryColor());
                    getWindow().setStatusBarColor(newColor);
                }
            }
        });
    }
}
