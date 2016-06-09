package com.cmcm.onews.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

 /* BUILD_CTRL:IF:OU_VERSION_ONLY */
import com.cmcm.feedback.FeedBackActivity;
import com.cmcm.login.EventLogin;
import com.cmcm.login.EventLogout;
import com.cmcm.login.LoginActivity;
import com.cmcm.login.LoginDataHelper;
import com.cmcm.login.LoginService;
  /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
import com.cmcm.onews.C;
import com.cmcm.onews.ONewsScenarios;
import com.cmcm.onews.R;
import com.cmcm.onews.bitmapcache.VolleySingleton;
import com.cmcm.onews.event.EventClearOffline;
import com.cmcm.onews.event.EventOffline;
import com.cmcm.onews.event.ONewsEvent;
import com.cmcm.onews.fragment.NewsOnePageDetailFragment;
import com.cmcm.onews.infoc.newsindia_act3;
import com.cmcm.onews.infoc.newsindia_clean;
import com.cmcm.onews.infoc.newsindia_textonly;
import com.cmcm.onews.model.ONews;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.model.ONewsSupportAction;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.service.LanguageChange;
import com.cmcm.onews.service.LocalService;
import com.cmcm.onews.storage.ONewsProviderManager;
import com.cmcm.onews.ui.slidr.SliderPanel;
import com.cmcm.onews.ui.slidr.SlidrConfig;
import com.cmcm.onews.ui.wave.NewsItemRootLayout;
import com.cmcm.onews.ui.widget.CommonNewsDialog;
import com.cmcm.onews.ui.widget.CommonSwitchButton;
import com.cmcm.onews.ui.widget.INewsDialogLanguage;
import com.cmcm.onews.ui.widget.INewsDialogOffline;
import com.cmcm.onews.ui.widget.INewsNotifyDialogClick;
import com.cmcm.onews.ui.widget.MareriaProgressBar;
import com.cmcm.onews.ui.widget.NewsLanguageDialog;
import com.cmcm.onews.ui.widget.NewsNotifyDialogClearCache;
import com.cmcm.onews.ui.widget.NewsNotifyDialogOffline;
import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.ConflictCommons;
import com.cmcm.onews.util.LanguageCountry;
import com.cmcm.onews.util.LanguageUtils;
import com.cmcm.onews.util.NetworkUtil;
import com.cmcm.onews.util.NewsDebugConfigUtil;
import com.cmcm.onews.util.PackageUtils;
import com.cmcm.onews.util.SDKConfigManager;
import com.cmcm.onews.util.StringUtils;
import com.cmcm.onews.util.TimeUtils;
import com.cmcm.onews.util.ToastUtil;
import com.cmcm.onews.util.UIConfigManager;
 /* BUILD_CTRL:IF:OU_VERSION_ONLY */
import com.facebook.FacebookSdk;
  /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
import java.io.File;
import java.util.List;

public class NewsSettingActivity extends NewsBaseUIActivity implements View.OnClickListener,INewsNotifyDialogClick,INewsDialogLanguage{

    private boolean isDebugSetting;
    private CommonSwitchButton mImgEnable;
    private CommonSwitchButton mWifiImgEnable;
    private NewsNotifyDialogClearCache mClearCacheDialog;
    private NewsNotifyDialogOffline mOfflineDialog;
    private NewsLanguageDialog mLanguageDialog;

    private Handler mHandler = new Handler();

    private TextView mCacheText;
    private TextView mVersion;
    private TextView mDebugSetting;

    private TextView mOfflineDes;
    private MareriaProgressBar mMareriaPB;

    private TextView mLanguageContent;
    private TextView mTitle;
    private TextView mOfflineTtitle;
    private TextView mCacheTitle;
    private TextView mImgTitle;
    private TextView mLanguageTitle;
    private TextView mVersionTitle;
    private TextView mDownloadImgTitle;
    private NewsItemRootLayout mWriterLayout;

    /**
     * 下载离线数据
     */
    private volatile boolean isOffline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        slidr();

        setContentView(R.layout.onews__activity_setting);

        isOffline = C.getInstance().isOffline();
        mLanguageContent = (TextView) findViewById(R.id.setting_language_title_content);
        mTitle = (TextView) findViewById(R.id.setting_title_text);
        mOfflineTtitle = (TextView) findViewById(R.id.setting_offline_title);
        mCacheTitle = (TextView) findViewById(R.id.setting_cache_title);
        mImgTitle = (TextView) findViewById(R.id.setting_enable_img_title);
        mLanguageTitle = (TextView) findViewById(R.id.setting_language_title);
        mVersionTitle = (TextView) findViewById(R.id.setting_version_title);
        mDownloadImgTitle = (TextView) findViewById(R.id.setting_enable_wifi_img_title);

        findViewById(R.id.setting_back).setOnClickListener(this);
        findViewById(R.id.setting_clear_cache).setOnClickListener(this);
        findViewById(R.id.onews__setting_version_layout).setOnClickListener(this);
        findViewById(R.id.setting_offline).setOnClickListener(this);
        findViewById(R.id.setting_language).setOnClickListener(this);
        findViewById(R.id.setting_feedback).setOnClickListener(this);
        mWriterLayout = (NewsItemRootLayout)findViewById(R.id.setting_writer);
        mWriterLayout.setOnClickListener(this);
        mOfflineDes = (TextView) findViewById(R.id.onews__setting_offline_des);
        mMareriaPB = (MareriaProgressBar) findViewById(R.id.news_offline_progress);
        showOfflineDes();

        mCacheText = (TextView) findViewById(R.id.setting_cache_size);
        mVersion = (TextView) findViewById(R.id.setting_version_name);
        mDebugSetting = (TextView)findViewById(R.id.setting_debug);
        mVersion.setText("v."+ PackageUtils.getAppVersionName(C.getAppContext()) +"(" + PackageUtils.getAppVersionCode(C.getAppContext()) + ")");

        mImgEnable = (CommonSwitchButton) findViewById(R.id.setting_img_enable);
        mImgEnable.setOnClickListener(this);
        mImgEnable.slideToChecked(!SDKConfigManager.getInstanse(C.getAppContext()).getNEWS_ITEM_SHOWIMG());

        mWifiImgEnable = (CommonSwitchButton) findViewById(R.id.setting_wifi_img_enable);
        mWifiImgEnable.setOnClickListener(this);
        mWifiImgEnable.slideToChecked(SDKConfigManager.getInstanse(C.getAppContext()).getNEWS_ITEM_WIFI_DOWNLOAD());

        isDebugSetting = SDKConfigManager.getInstanse(C.getAppContext()).getNEWS_DEBUG_SETTING();
        initDebugSettingText();

        mClearCacheDialog = new NewsNotifyDialogClearCache(this,this);
        mOfflineDialog = new NewsNotifyDialogOffline(this,new INewsDialogOffline(){

            @Override
            public void clickCount(int count) {
                mOfflineDialog.dismissDialog();

                if(NetworkUtil.isNetworkActive(C.getAppContext())){
                    if(needOffline(count)){
                        showBottomToast(StringUtils.getString(C.getAppContext(),R.string.onews__offline_toast,count));
                        isOffline = true;
                        showOfflineDes();
                        LocalService.start_ACTION_OFFLINE_NEWS(C.getAppContext(),count,getOfflineSize(count));
                    }
                }else {
                    showBottomToast(StringUtils.getString(C.getAppContext(),R.string.onews__offline_no_network));
                }

            }
        });
        mLanguageDialog = new NewsLanguageDialog(this,this);
        mLogInfoPic = (ImageView) findViewById(R.id.login_pic);
        mLogInfoName = (TextView) findViewById(R.id.login_name);
        mLogInfoOut = (TextView) findViewById(R.id.logout);
        mLogIn = findViewById(R.id.setting_login);
        mLogIn.setOnClickListener(this);

        initNotify();

        queryCacheSize();
        new newsindia_act3().report();
        language();

        versionCtrl();
        initFaceBookLogin();
        initLoginInfo();
    }
    private ImageView mLogInfoPic;
    private TextView mLogInfoName;
    private TextView mLogInfoOut;
    private View mLogIn;

    private void initLoginInfo() {
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogOutDialog();
            }
        });
         /* BUILD_CTRL:IF:OU_VERSION_ONLY */
        if (LoginDataHelper.getInstance().isLogined()) {
            final LoginDataHelper.LoginUserInfo userInfo = LoginDataHelper.getInstance().getLoginUserInfo();
            final String avatar = userInfo.getAvatar();
            final String nickname = userInfo.getNickname();
            showLogIn(avatar, nickname);
            if (L.DEBUG){
                Log.e("suj",avatar);
            }
        } else {
            showLogOut();
        }
          /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */

    }

    private void showLogOut(){
        mLogInfoPic.setImageResource(R.drawable.login_nobody);
        mLogInfoOut.setVisibility(View.GONE);
        mLogInfoName.setText(R.string.log_in);
    }

    private void showLogIn(String picUrl,String name){
        if (TextUtils.isEmpty(picUrl)){
            mLogInfoPic.setImageResource(R.drawable.login_nobody);
        }else {
           VolleySingleton.getInstance().loadImage(mLogInfoPic,picUrl);
        }
        mLogInfoOut.setVisibility(View.VISIBLE);
        mLogInfoOut.setText(R.string.faceboo_log_out);
        mLogInfoName.setText(name);
    }

    private void initFaceBookLogin() {
         /* BUILD_CTRL:IF:OU_VERSION_ONLY */
        FacebookSdk.sdkInitialize(getApplicationContext());
          /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initNotify() {
        mToastTop = (RelativeLayout) findViewById(R.id.news_toast_top);
        mToastTopText = (TextView) findViewById(R.id.news_top_toast_text);
        mToastTopAnimator = ObjectAnimator.ofFloat(mToastTop, "alpha", 1, 1, 1, 1, 0);

        mToastBottom = (RelativeLayout) findViewById(com.cmcm.onews.sdk.R.id.news_toast_bottom);
        mToastBottomText = (TextView) findViewById(com.cmcm.onews.sdk.R.id.news_bottom_toast_text);
        mToastBottomAnimator = ObjectAnimator.ofFloat(mToastBottom, "alpha", 1, 1, 1, 1, 0);
    }

    @Override
    public void onClick(View v) {
        if(null == v){
            return;
        }

        switch (v.getId()){
            case R.id.setting_back:
                NewsSettingActivity.this.finish();
                break;
            case R.id.setting_img_enable:
                if(SDKConfigManager.getInstanse(C.getAppContext()).getNEWS_ITEM_SHOWIMG()){
                    SDKConfigManager.getInstanse(C.getAppContext()).setNEWS_ITEM_SHOWIMG(false);
                    mImgEnable.slideToChecked(true);
                }else {
                    mImgEnable.slideToChecked(false);
                    SDKConfigManager.getInstanse(C.getAppContext()).setNEWS_ITEM_SHOWIMG(true);
                }
                infocaction = 1;
                break;
            case R.id.setting_wifi_img_enable:
                if (SDKConfigManager.getInstanse(C.getAppContext()).getNEWS_ITEM_WIFI_DOWNLOAD()) {
                    SDKConfigManager.getInstanse(C.getAppContext()).setNEWS_ITEM_WIFI_DOWNLOAD(false);
                    mWifiImgEnable.slideToChecked(false);
                } else {
                    SDKConfigManager.getInstanse(C.getAppContext()).setNEWS_ITEM_WIFI_DOWNLOAD(true);
                    mWifiImgEnable.slideToChecked(true);
                }
                break;
            case R.id.setting_clear_cache:
                if(null != mClearCacheDialog && size_all > 0){
                    mClearCacheDialog.showDialog();
                }
                break;
            case R.id.onews__setting_version_layout:
                if(SDKConfigManager.getInstanse(C.getAppContext()).getNEWS_DEBUG_SETTING()){
                    NewsSdk.startDebugSetting(this);
                }else{
                    if(getfastClik()){
                        mFastClikNum = 0;
                        mDebugSetting.setText(StringUtils.getString(C.getAppContext(), R.string.onews__debug_close));
                        SDKConfigManager.getInstanse(C.getAppContext()).setNEWS_DEBUG_SETTING();
                        NewsSdk.startDebugSetting(this);
                    }
                }
                break;
            case R.id.setting_offline:
                if(isOffline){
                    toastOfflineLater();
                }else {
                    queryNoOfflineSize();
                }
                break;
            case R.id.setting_language:
                if(null != mLanguageDialog){
                    mLanguageDialog.showDialog();
                }
                break;
            case R.id.setting_writer:
                openWriter();
                break;

            case R.id.setting_feedback:
                FeedBackActivity.startFeedBackFromSettings(NewsSettingActivity.this);
                break;

            case R.id.setting_login:{
                 /* BUILD_CTRL:IF:OU_VERSION_ONLY */
                if (LoginDataHelper.getInstance().isLogined()){
//                    showLogOutDialog();
                }else {
                    LoginActivity.startLoginActivity(getBaseContext(),LoginActivity.FROM_SETTINGS);

                }
                /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */

                break;

            }
        }
    }

    /**
     * 公众号
     */
    private void openWriter(){
        ONewsScenario scenario = new ONewsScenario();
        ONews oNews = new ONews();
        oNews.originalurl("http://www.baidu.com");
        oNews.action(ONewsSupportAction.supportAction(ONewsSupportAction.SA_01));
        NewsSdk.INSTAMCE.openOnewsWithSource(NewsSettingActivity.this,scenario,oNews,NewsBaseActivity.DETAIL_FROM_WRITE);
    }

    private void showLogOutDialog() {
        final CommonNewsDialog dialog = new CommonNewsDialog(this, new INewsNotifyDialogClick() {
            @Override
            public void clickContinue() {
                // do log out
                 /* BUILD_CTRL:IF:OU_VERSION_ONLY */
                LoginService.logout();
                  /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
            }

            @Override
            public void clickCancel() {

            }
        }, getString(R.string.logout_tips));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setIfExitWhitBack(false);
        dialog.showDialog();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initDebugSettingText();
        initLoginInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null != mClearCacheDialog){
            mClearCacheDialog.dismissDialog();
        }

        if(null != mOfflineDialog){
            mOfflineDialog.dismissDialog();
        }

        if(null != mLanguageDialog){
            mLanguageDialog.dismissDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        infoctextonly();
        isOffline = false;
    }

    @Override
    public void clickContinue() {
        if(null != mClearCacheDialog){
            mClearCacheDialog.dismissDialog();
            LocalService.start_ACTION_CLEAR_CAHCE(this);

            infocclean(1);
        }
    }

    @Override
    public void clickCancel() {
        if(null != mClearCacheDialog){
            mClearCacheDialog.dismissDialog();

            infocclean(2);
        }
    }

    @Override
    protected void onEventInUiThread(ONewsEvent event) {
        super.onEventInUiThread(event);
        if(isFinishing()){
            return;
        }
        if(event instanceof EventClearOffline){
            showTopToast(StringUtils.getString(C.getAppContext(),R.string.onews__clear_cache_success));
            deleteImgCache();
            queryCacheSize();
            showOfflineDes();
        }else if(event instanceof EventOffline){
            onHandleEvent_EventOffline((EventOffline) event);
        }
         /* BUILD_CTRL:IF:OU_VERSION_ONLY */
        else if(event instanceof EventLogin){
            initLoginInfo();
        }else if(event instanceof EventLogout){
            initLoginInfo();
        }
         /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
    }

    private void onHandleEvent_EventOffline(EventOffline event) {
        isOffline = false;
        if(!event.isSuccess()){
            showBottomToast(StringUtils.getString(C.getAppContext(),R.string.onews__download_faile));
        }

        showOfflineDes();
        queryCacheSize();
    }

    private void showOfflineDes() {
        if(isOffline){
            mOfflineDes.setText(StringUtils.getString(C.getAppContext(),R.string.onews__downloading));
            mMareriaPB.setVisibility(View.VISIBLE);
            mMareriaPB.start();
        }else {
            mMareriaPB.setVisibility(View.GONE);
            mMareriaPB.stop();
            long time = UIConfigManager.getInstanse(C.getAppContext()).getNEWS_LAST_OFFLINE();
            if(time > 0){
                mOfflineDes.setText(StringUtils.getString(C.getAppContext(),R.string.onews__download_last, TimeUtils.timeFormatOffline(time)));
            }else {
                mOfflineDes.setText(StringUtils.getString(C.getAppContext(),R.string.onews__setting_offline_des));
            }
        }
    }

    private void initDebugSettingText(){
        if(isDebugSetting){
            if(NewsDebugConfigUtil.getInstance().isEnableDebug()){
                mDebugSetting.setText(StringUtils.getString(C.getAppContext(),R.string.onews__debug_open));
            }else{
                mDebugSetting.setText(StringUtils.getString(C.getAppContext(),R.string.onews__debug_close));
            }
        }

    }

    private int mFastClikNum = 0;
    private int mClickToTalNum = 8;
    private boolean getfastClik(){
        mFastClikNum++;
        if(mClickToTalNum - mFastClikNum != 0){
            ToastUtil.showToast(NewsSettingActivity.this, mFastClikNum);
        }
        if(mFastClikNum >= mClickToTalNum){
            return true;
        }
        return false;
    }

    private long size_all = 0;
    private void queryCacheSize(){
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                size_all = 0;

                List<ONewsScenario> scenarios = ONewsScenarios.getInstance().scenarios();
                int size = scenarios.size();
                for (int i = 0; i < size; i++) {
                    size_all = size_all + ONewsProviderManager.getInstance().queryCacheSize(scenarios.get(i));
                }
                mHandler.removeCallbacks(upCacheSize);
                mHandler.post(upCacheSize);
            }
        });
    }

    private void deleteImgCache(){
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                String IMG_FOLDER = "/img_cache/";
                String path = NewsSdk.INSTAMCE.getAppContext().getExternalFilesDir(null)+ IMG_FOLDER;
                File dir = new File(path);
                if (dir.isDirectory())
                {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++)
                    {
                        new File(dir, children[i]).delete();
                    }
                }
            }
        });
    }

    private long size_tw = 0;
    private long size_th = 0;
    private long size_fy = 0;
    private void queryNoOfflineSize(){
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                size_tw = 0;
                size_th = 0;
                size_fy = 0;

                List<ONewsScenario> scenarios = ONewsScenarios.getInstance().scenarios();
                int size = scenarios.size();
                for (int i = 0; i < size; i++) {
                    size_tw = size_tw + ONewsProviderManager.getInstance().queryNoBodySize(20, scenarios.get(i));
                    size_th = size_th + ONewsProviderManager.getInstance().queryNoBodySize(30, scenarios.get(i));
                    size_fy = size_fy + ONewsProviderManager.getInstance().queryNoBodySize(50, scenarios.get(i));
                }

                mHandler.removeCallbacks(showOffline);
                mHandler.post(showOffline);
            }
        });
    }

    private Runnable showOffline = new Runnable() {
        @Override
        public void run() {
            if(size_tw > 0  || size_th > 0  || size_fy > 0){
                if(!isOffline){
                    mOfflineDialog.showDialog(size_tw / 1024l,size_th / 1024l,size_fy / 1024l);
                }else{
                    toastOfflineLater();
                }
            }else {
                toastOfflineLater();
            }
        }
    };

    private int getOfflineSize(int count) {
        if(count == 20){
            return (int) size_tw;
        }

        if(count == 30){
            return (int) size_th;
        }

        if(count == 50){
            return (int) size_fy;
        }

        return 0;
    }

    private boolean needOffline(int count) {
        if(count == 20 && size_tw == 0){
            toastOfflineLater();
            return false;
        }

        if(count == 30 && size_th == 0){
            toastOfflineLater();
            return false;
        }

        if(count == 50 && size_fy == 0){
            toastOfflineLater();
            return false;
        }
        return true;
    }

    private void toastOfflineLater() {
        if(!isFinishing()){
            showBottomToast(StringUtils.getString(C.getAppContext(),R.string.onews__offline_later));
        }
    }

    private Runnable upCacheSize = new Runnable() {
        @Override
        public void run() {
            if(size_all > 0){
                mCacheText.setText(StringUtils.getString(C.getAppContext(),R.string.onews__download_size, size_all / 1024l));
            }else{
                mCacheText.setText(StringUtils.getString(C.getAppContext(),R.string.onews__need_clear_cahce));
            }
        }
    };

    /**
     * infoc newsindia_clean
     */
    private void infocclean(int type){
        newsindia_clean clean = new newsindia_clean();
        clean.click(type);
        clean.size((int) size_all);
        clean.report();
    }

    private int infocaction = 2;
    /**
     * infoc newsindia_textonly
     */
    private void infoctextonly(){
        newsindia_textonly textonly = new newsindia_textonly();
        textonly.textonly(SDKConfigManager.getInstanse(C.getAppContext()).getNEWS_ITEM_SHOWIMG() ? 1 : 2);
        textonly.action(infocaction);
        textonly.network(NetworkUtil.getNetWorkType(C.getAppContext()));
        textonly.report();
    }

    @Override
    public void clickCount(LanguageCountry other) {
        LanguageCountry languageCountry = UIConfigManager.getInstanse(this).getLanguageSelected(this);

        if(LanguageCountry.LANGUAGE_OPTION_EN.equals(other.getLanguage())){
            reportLanguage(1);
        }else if(LanguageCountry.LANGUAGE_OPTION_HI.equals(other.getLanguage())){
            reportLanguage(2);
        }

        if(!other.getLanguage().equals(languageCountry.getLanguage())){
            UIConfigManager.getInstanse(this).setLanguageSelected(other);
            LanguageUtils.setLanguage(other, C.getAppContext());
            BackgroundThread.post(new LanguageChange());
//            LocalService.start_ACTION_LANGUAGE_CHANGE(C.getAppContext());
        }

        changeLanguage();
        writerVisible(other);
    }

    private void changeLanguage(){
        mTitle.setText(R.string.onews__setting_title);
        mOfflineTtitle.setText(R.string.onews__setting_offline);
        mCacheTitle.setText(R.string.onews__clear_cache);
        mImgTitle.setText(R.string.onews__setting_hide_image);
        mLanguageTitle.setText(R.string.onews__setting_language);
        mVersionTitle.setText(R.string.onews__version);
        mDownloadImgTitle.setText(R.string.onews__setting_wifi_download);
        ((TextView)findViewById(R.id.setting_feedback_img_title)).setText(R.string.onews__setting_feedback);
        mHandler.post(upCacheSize);
        showOfflineDes();
        initLoginInfo();
        language();
    }

    private void language() {
        LanguageCountry languageCountry = UIConfigManager.getInstanse(this).getLanguageSelected(this);
        if(LanguageCountry.LANGUAGE_OPTION_EN.equals(languageCountry.getLanguage())){
            mLanguageContent.setText(R.string.settings_language_en);
        }else if(LanguageCountry.LANGUAGE_OPTION_HI.equals(languageCountry.getLanguage())){
            mLanguageContent.setText(R.string.settings_language_hi);
        }
        writerVisible(languageCountry);
    }

    private void writerVisible(LanguageCountry other){
        if(LanguageCountry.LANGUAGE_OPTION_EN.equals(other.getLanguage())){
            mWriterLayout.setVisibility(View.VISIBLE);
        }else if(LanguageCountry.LANGUAGE_OPTION_HI.equals(other.getLanguage())){
            mWriterLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 版本控制ui显示
     */
    private void versionCtrl(){
        if(ConflictCommons.isCNVersion()){
            findViewById(R.id.setting_language).setVisibility(View.GONE);
            findViewById(R.id.setting_language_separated).setVisibility(View.GONE);

            findViewById(R.id.setting_offline).setVisibility(View.GONE);
            findViewById(R.id.setting_offline_separated).setVisibility(View.GONE);

            findViewById(R.id.setting_clear_cache).setVisibility(View.GONE);
            findViewById(R.id.setting_clear_cache_separated).setVisibility(View.GONE);
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
                finish();
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

    /**
     * 头部提示
     * @param msg
     */
    protected void showTopToast(String msg) {
        if(null == mToastTopAnimator
            || null == mToastTop
            || null == mToastTopText ){
            return;
        }

        mToastTopAnimator.cancel();
        mToastTop.setVisibility(View.VISIBLE);
        mToastTopText.setText(msg);
        mToastTopAnimator.setDuration(TIME_TOAST);
        mToastTopAnimator.start();
    }

    /**
     * 底部提示
     * @param msg
     */
    protected void showBottomToast(String msg) {
        if(null == mToastBottomAnimator
            || null == mToastBottom
            || null == mToastBottomText ){
            return;
        }

        mToastBottomAnimator.cancel();
        mToastBottom.setVisibility(View.VISIBLE);
        mToastBottomText.setText(msg);
        mToastBottomAnimator.setDuration(TIME_TOAST);
        mToastBottomAnimator.start();
    }

    protected RelativeLayout mToastBottom;
    protected TextView mToastBottomText;
    protected ObjectAnimator mToastBottomAnimator;

    protected static final long TIME_TOAST = 2 * 1000l;
    protected RelativeLayout mToastTop;
    protected TextView mToastTopText;
    protected ObjectAnimator mToastTopAnimator;
}
