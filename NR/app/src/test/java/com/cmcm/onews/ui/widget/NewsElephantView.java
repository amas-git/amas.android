package com.cmcm.onews.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcm.newsindia.MainActivity;
import com.cmcm.onews.C;
import com.cmcm.onews.ONewsScenarios;
import com.cmcm.onews.R;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.ui.wave.NewsItemRootLayout;
import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.DimenSdkUtils;
import com.cmcm.onews.util.LanguageCountry;
import com.cmcm.onews.util.LanguageUtils;
import com.cmcm.onews.util.UIConfigManager;
import com.cmcm.splash.SplashHelper;
import com.ijinshan.cloudconfig.deepcloudconfig.PullCloudConfig;

public class NewsElephantView extends RelativeLayout implements View.OnClickListener{
    private FrameLayout mLogo;
    private FrameLayout mElephant;
    private ViewStub mLanguageStub;

    private View mLanguageView;
    private NewsItemRootLayout mLanguageEn;
    private NewsItemRootLayout mLanguageHi;
    private TextView mLanguageTitle;

    private RelativeLayout mNodeCategory;
    private ViewStub mVSInterests;
    private NewsCategoryView mNewsCategoryView;
    private MainActivity activity;

    private RelativeLayout mDisplayLayout;
    private ViewStub mDispalyStub;
    private NewsDisplayView mNewsDisplayView;

    public NewsElephantView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NewsElephantView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsElephantView(Context context) {
        super(context);
    }

    public void init(RelativeLayout nodeCategory,ViewStub mInterests, RelativeLayout mDisplayLayout, ViewStub mDispalyStub, MainActivity act) {
        this.activity = act;
        this.mNodeCategory = nodeCategory;
        this.mVSInterests = mInterests;
        this.mDisplayLayout = mDisplayLayout;
        this.mDispalyStub = mDispalyStub;
        mLogo = (FrameLayout) findViewById(R.id.splash_logo);
        mElephant = (FrameLayout) findViewById(R.id.splash_elephant);
        mLanguageStub = (ViewStub) findViewById(R.id.splash_choose_language);
    }

    public void showGuide(){
        flyLogo();
        flyElephant();
    }

    /**********************开屏动画***********************/
    private static int DEFAULT_W = DimenSdkUtils.dp2px(360);
    private static int DEFAULT_H = DimenSdkUtils.dp2px(640);
    private static int ELEPHANT_W = DimenSdkUtils.dp2px(360);
    private static int ELEPHANT_H = DimenSdkUtils.dp2px(558);
    private static int ELEPHANT_M = -DimenSdkUtils.dp2px(40);
    private static int LOGO_W = DimenSdkUtils.dp2px(220);
    private static int LOGO_H = DimenSdkUtils.dp2px(45);
    private static int LOGO_M_T = DimenSdkUtils.dp2px(45);
    private static int LANGUAGE_B = DimenSdkUtils.dp2px(72);
    private static int LANGUAGE_W = DimenSdkUtils.dp2px(165);
    private static int LANGUAGE_H = DimenSdkUtils.dp2px(42);
    public static int W = DimenSdkUtils.getScreenWidth();
    public static int H = DimenSdkUtils.getScreenHeight();
    private static int LANGUAGE_M_R = DimenSdkUtils.dp2px(35);
    private float scale = ELEPHANT_W * 1f / ELEPHANT_H;
    private float scaleW = (W * 1f) / DEFAULT_W;
    private float scaleH = (H * 1f) / DEFAULT_H;
    public void layout(){
        int elephantW;
        int elephantH;
        int elephantM = (int) (scaleH * ELEPHANT_M);
        int logoW;
        int logoH;
        int logoM = (int) (scaleH * LOGO_M_T);
        if(scaleW != scaleH){
            elephantW = (int) (scaleW * ELEPHANT_W);
            elephantH = (int) (elephantW / scale);
            elephantM = (int) (elephantM / scale);

            logoW = (int) (scaleH * LOGO_W);
            logoH = (int) (logoW / (LOGO_W *1f/LOGO_H));
        }else {
            elephantW = (int) (scaleW * ELEPHANT_W);
            elephantH = (int) (scaleH * ELEPHANT_H);
            logoW = (int) (scaleH * LOGO_W);
            logoH = (int) (scaleH * LOGO_H);
        }

        DimenSdkUtils.updateLayout(mElephant, elephantW, elephantH);
        DimenSdkUtils.updateLayoutMargin(mElephant, -3, -3, -3, elephantM);
        DimenSdkUtils.updateLayout(mLogo, logoW, logoH);
        DimenSdkUtils.updateLayoutMargin(mLogo,-3,logoM,-3,-3);
    }

    private void selectLanguage(final boolean isHi) {
        LanguageCountry languageCountry = null;
        if(isHi){
            languageCountry = new LanguageCountry(LanguageCountry.LANGUAGE_OPTION_HI);
        }else {
            languageCountry = new LanguageCountry(LanguageCountry.LANGUAGE_OPTION_EN);
        }
        UIConfigManager.getInstanse(C.getAppContext()).setLanguageSelected(languageCountry);
        LanguageUtils.setLanguage(languageCountry, C.getAppContext());

        if(isHi){
            ONewsScenarios.getInstance().removeVideo();
        }else {
            ONewsScenarios.getInstance().addVideo();
        }
        final LanguageCountry finalLanguageCountry = languageCountry;
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                updateLanguage(finalLanguageCountry);
                PullCloudConfig.getInstance().getConfig();
            }
        });
    }

    private ObjectAnimator animaFlyLogo = null;
    private void flyLogo(){
        if(null == mLogo){
            return;
        }

        if(null == animaFlyLogo){
            animaFlyLogo = ObjectAnimator.ofFloat(mLogo,"x",-mLogo.getWidth());
            animaFlyLogo.setDuration(300);
            animaFlyLogo.setInterpolator(new LinearInterpolator());
        }
        animaFlyLogo.start();
    }

    private AnimatorSet animaFlyElephant = null;
    private ObjectAnimator animaElephantX = null;
    private ObjectAnimator animaElephantY = null;
    private ObjectAnimator animaElephantScaleX = null;
    private ObjectAnimator animaElephantScaleY = null;
    private void flyElephant(){
        if(null == mElephant){
            return;
        }

        animaElephantX = ObjectAnimator.ofFloat(mElephant,"x",-DimenSdkUtils.dp2px(50));
        animaElephantX.setDuration(300);
        animaElephantY = ObjectAnimator.ofFloat(mElephant,"y",DimenSdkUtils.dp2px(60));
        animaElephantY.setDuration(300);

        animaElephantScaleX = ObjectAnimator.ofFloat(mElephant,"scaleX",1f,1.107f);
        animaElephantScaleX.setInterpolator(new AccelerateInterpolator(1.5f));
        animaElephantScaleX.setDuration(300);
        animaElephantScaleY = ObjectAnimator.ofFloat(mElephant,"scaleY",1f,1.107f);
        animaElephantScaleY.setInterpolator(new AccelerateInterpolator(1.5f));
        animaElephantScaleY.setDuration(300);

        animaFlyElephant = new AnimatorSet ();
        animaFlyElephant.playTogether(animaElephantX, animaElephantY, animaElephantScaleX, animaElephantScaleY);
        animaFlyElephant.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                flyLanguageChoose();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animaFlyElephant.start();
    }

    private void flyLanguageChoose(){
        if (null != mLanguageStub && null == mLanguageView) {
            mLanguageView = mLanguageStub.inflate();
            mLanguageEn = (NewsItemRootLayout) mLanguageView.findViewById(R.id.choose_language_en);
            mLanguageEn.setOnClickListener(this);
            mLanguageHi = (NewsItemRootLayout) mLanguageView.findViewById(R.id.choose_language_hi);
            mLanguageHi.setOnClickListener(this);
            mLanguageTitle = (TextView) mLanguageView.findViewById(R.id.choose_language_title);

            int languageW;
            int languageH;
            if(scaleW != scaleH){
                languageW = (int) (scaleW * LANGUAGE_W);
                languageH = (int) (languageW / (LANGUAGE_W *1f/LANGUAGE_H));
            }else {
                languageW = (int) (scaleW * LANGUAGE_W);
                languageH = (int) (scaleH * LANGUAGE_H);
            }

            int languageB = LANGUAGE_B;
            if(H <= 800){
                languageB -= DimenSdkUtils.dp2px(22);
            }

            DimenSdkUtils.updateLayoutMargin(mLanguageView,-3,-3,-3, (int) (scaleH * languageB));
            DimenSdkUtils.updateLayout(mLanguageEn, languageW, languageH);
            DimenSdkUtils.updateLayout(mLanguageHi, languageW, languageH);
            DimenSdkUtils.updateLayout(mLanguageTitle, languageW, -3);
        }

        // TODO 是否需要展示直播开屏
        boolean isDisplay = SplashHelper.getIns().isShouldShowSplash();
        if (isDisplay) {
            if (null != mDispalyStub){
                mNewsDisplayView = (NewsDisplayView) mDispalyStub.inflate();
                mNewsDisplayView.initView(activity);
            }
        } else {
            if (null != mVSInterests) {
                mNewsCategoryView = (NewsCategoryView) mVSInterests.inflate();
                mNewsCategoryView.initView(activity);
            }
        }

        animaLanguage(mLanguageTitle,150);
        animaLanguage(mLanguageEn, 250);
        animaLanguage(mLanguageHi, 350);
    }

    private void animaLanguage(View view,long time){
        int d = (int) (LANGUAGE_M_R * scaleW + LANGUAGE_W * scaleW);
        ObjectAnimator animaLanguage = ObjectAnimator.ofFloat(view,"x",W,W - d);
        animaLanguage.setInterpolator(new LinearInterpolator());
        animaLanguage.setDuration(time);

        ObjectAnimator animaLanguageAlpha = ObjectAnimator.ofFloat(view,"alpha",0f ,1f);
        animaLanguageAlpha.setDuration(time);
        animaLanguage.setInterpolator(new DecelerateInterpolator());

        AnimatorSet animaFlyLanguageChoose = new AnimatorSet ();
        animaFlyLanguageChoose.playTogether(animaLanguageAlpha, animaLanguage);
        animaFlyLanguageChoose.start();
    }

    private ObjectAnimator animaIn = null;
    private ObjectAnimator animaOut = null;
    private ObjectAnimator alphaOut = null;
    private void animaToCategory(){
        animaOut = ObjectAnimator.ofFloat(this,"x",-W);
        animaOut.setInterpolator(new LinearInterpolator());
        animaOut.setDuration(300);

        alphaOut = ObjectAnimator.ofFloat(this,"alpha",1f,0f);
        alphaOut.setInterpolator(new DecelerateInterpolator());
        alphaOut.setDuration(250);

        boolean isDisplay = SplashHelper.getIns().isShouldShowSplash();
        if (isDisplay){
            animaIn = ObjectAnimator.ofFloat(mDisplayLayout,"x",0);
        } else {
            animaIn = ObjectAnimator.ofFloat(mNodeCategory,"x",0);
        }
        animaIn.setInterpolator(new LinearInterpolator());
        animaIn.setDuration(300);

        AnimatorSet animaToCategory = new AnimatorSet();
        animaToCategory.playTogether(animaOut, animaIn, alphaOut);
        animaToCategory.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (null != mNewsCategoryView) {
                    mNewsCategoryView.changeLanguage();
                    mNewsCategoryView.startAnim();
                }
                if (null != mNewsDisplayView){
                    mNewsDisplayView.changeLanguage();
                    mNewsDisplayView.loadRes();
                    mNewsDisplayView.delayedToNews();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animaToCategory.start();
    }

    /**
     * 更新语言
     * @param languageCountry
     */
    private void updateLanguage(LanguageCountry languageCountry) {
        NewsSdk.INSTAMCE.changeNewsLanguage(languageCountry.getLanguageWithCountryUnderline());
    }

    @Override
    public void onClick(View v) {
        if(null == v){
            return;
        }

        switch (v.getId()) {
            case R.id.choose_language_en:
                reportLanguage(1);
                selectLanguage(false);
                animaToCategory();
                break;
            case R.id.choose_language_hi:
                reportLanguage(2);
                selectLanguage(true);
                animaToCategory();
                break;
        }
    }

    private void reportLanguage(int action) {
        if(null != activity){
            activity.reportLanguage(action);
        }
    }

    public void initReport() {
        if(null != mNewsCategoryView){
            mNewsCategoryView.initReport();
        }
    }
}
