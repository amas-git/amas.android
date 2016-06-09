package com.cmcm.onews.ui;

import android.animation.ArgbEvaluator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.cmcm.onews.R;
import com.cmcm.onews.event.ONewsEvent;
import com.cmcm.onews.fragment.NewsOpenCmsFragment;
import com.cmcm.onews.infoc.newsindia_voice;
import com.cmcm.onews.model.ONews;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.ui.item.NewsAlgorithmReport;
import com.cmcm.onews.ui.slidr.SliderPanel;
import com.cmcm.onews.ui.slidr.SlidrConfig;
import com.cmcm.onews.util.UIConfigManager;

public class NewsOpenCmsActivity extends NewsBaseUIActivity implements View.OnClickListener {
    /**
     * 公共账号注册页面
     */
    private static final String OPENCMS_WRITER = "http://iv.cmcm.com/joinus/?lang=";

    public static void startNewsOpenCms(Context context, ONews news, ONewsScenario scenario,int from) {
        if (null == context || null == news || null == scenario) {
            return;
        }

        reportOpenCmsClick(news,scenario);

        Intent intent = new Intent(context, NewsOpenCmsActivity.class);
        intent.putExtra(KEY_NEWS, news.toContentValues());

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SCENARIO, scenario);
        intent.putExtra(KEY_BUNDLE, bundle);

        intent.putExtra(KEY_FROM, from);
        if(!(context instanceof Activity)){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    private NewsOpenCmsFragment mFragment;
    private ONewsScenario mScenario;
    private ONews mONews;

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

        if(null == mONews || null == mONews.openCms()){
            return;
        }

        //TODO 转为细力度频道
        mScenario = ONewsScenario.getOpenCmsScenario();
        slidr();
        setContentView(R.layout.onews__activity_opencms);
        setListener();
        init();
    }

    public void init(){
        mFragment = NewsOpenCmsFragment.newInstance(mScenario, mONews, mFrom);
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.opencms_content, mFragment).commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
        findViewById(R.id.opencms_back).setOnClickListener(this);
        findViewById(R.id.opencms_writer_layout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.opencms_back:
                finish();
                break;
            case R.id.opencms_writer_layout:
                toRegisteredH5();
                break;
        }
    }

    private void toRegisteredH5() {
        reportOpenCms(mONews, 1);

        NewsWebViewDetailActivity.startNewsWebView(this, OPENCMS_WRITER + UIConfigManager.getInstanse(this).getLanguageSelected(this).getLanguageWithCountryUnderline(), DETAIL_FROM_OPENCMS);
    }

    public static void reportOpenCms(ONews onews,int action) {
        if(null != onews && null != onews.openCms()){
            new newsindia_voice().pubid(onews.openCms().id()).action(action).report();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        reportDuration(APP_TIME_OPENCMS);
        if(null != adder && null != mONews && null != mScenario){
            adder.zero();
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

    private void goBack() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    @Override
    protected void onEventInUiThread(ONewsEvent event) {
        super.onEventInUiThread(event);
        if(isFinishing()){
            return;
        }
    }

    /**
     * 公共账号点击上报
     */
    private static void reportOpenCmsClick(ONews news,ONewsScenario scenario){
        ONews report = ONews.fromContentValues(news.toContentValues());
        report.ctype("0x2000");
        NewsAlgorithmReport.algorithmReportNewsClick_OPENCMS(scenario, report);
    }
}
