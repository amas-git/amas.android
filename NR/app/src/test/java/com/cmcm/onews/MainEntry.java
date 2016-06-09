package com.cmcm.onews;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.cm.CH;
import com.cmcm.onews.model.ONews;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.model.ONewsSupportAction;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.ui.NewsActivity;
import com.cmcm.onews.ui.NewsAlbumActivity;
import com.cmcm.onews.ui.NewsBaseActivity;
import com.cmcm.onews.ui.NewsOpenCmsActivity;
import com.cmcm.onews.ui.NewsVideoActivity;
import com.cmcm.onews.ui.NewsVideoFullScreenActivity;
import com.cmcm.onews.util.ConflictCommons;
import com.cmcm.onews.util.UIConfigManager;
import com.cmcm.onews.util.push.PushOutAPI;

/* BUILD_CTRL:IF:CNVERSION */
import com.cmcm.update.UpdateHelper;
/* BUILD_CTRL:ENDIF:CNVERSION */


/* BUILD_CTRL:IF:OU_VERSION_ONLY */
import com.cmcm.login.LoginDataHelper;
/* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */


public class MainEntry extends Application{
    private final static String TAG = "MainEntry";
    private C mMoApp = null;
    private static MainEntry theApp = null;
    public static long mStartTime;

    public MainEntry() {
        theApp = this;
    }

    public static Context getAppContext() {
        return theApp.getApplicationContext();
    }

    public static MainEntry getInstance() {
        return theApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStartTime = System.currentTimeMillis();
        NewsSdk.INSTAMCE
                .context(getAppContext())
                .setProductId(ConflictCommons.ProductId())
                        //.setSupportedCType()
                        //.setSupportedDisplay()
                        //.setSupportedAction()
                .setChannelId(CH.getChannelId())
                .setLogLevel(BuildConfig.DEBUG ? 1 : 0)
                .setTracerLogEnabled(BuildConfig.DEBUG)
                .dump()
                .setDetailsBackTo(NewsActivity.getIntent(getAppContext()))
                .setBaseDependence(new NewsSdkDepandence())
                .setPushMessageBehavior(new NewsSdk.PushMessageBehavior() {
                    @Override
                    public void reportPushMessageBehavior(Context context, int action, String pushid) {
                        PushOutAPI.reportMessageBehavior(context, action, pushid);
                    }
                })
                .setONewsLanguage(UIConfigManager.getInstanse(getAppContext()).getLanguageSelected(getAppContext()).getLanguageWithCountryUnderline())
                        // 添加一个事件响应的拦阻器。
                .addCustomOpenNews(new NewsSdk.CustomOpenNews() {
                    @Override
                    public boolean openOnews(Context context, ONewsScenario oNewsScenario, ONews news, int type, int source) {
                        if (type == ONewsSupportAction.SA_04) {
                            NewsVideoActivity.ON_START_VIDEOACTIVITY(context, news, oNewsScenario);
                            return true;
                        } else if(type == ONewsSupportAction.SA_20){
                            NewsAlbumActivity.startNewsAlbum(context, news, oNewsScenario,source);
                            return true;
                        } else if(type == ONewsSupportAction.SA_2000){
                            NewsOpenCmsActivity.startNewsOpenCms(context,news,oNewsScenario,source);
                            return true;
                        }else {
                            return false;
                        }
                    }
                })
                .isCNVersion(ConflictCommons.isCNVersion())
                .setCustomNewsIntent(setCustomNewsIntent());


        if(ConflictCommons.isCNVersion()){
            NewsSdk.INSTAMCE.useDomestic(getAppContext());
        }else {
            NewsSdk.INSTAMCE.useOverseasIN(getAppContext());
        }

        /**
         * todo 初始化逻辑放到C.onCreate()方法里面
         */
        mMoApp.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            mMoApp = (C)Class.forName(C.class.getName()).newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (mMoApp == null) {

            Log.e(TAG, "CRITICAL EXCEPTION!! UNABLE INSTANCE MoSecurityApplication.");
            android.os.Process.killProcess(android.os.Process.myPid());

            return;
        }
        mMoApp.SetAppBaseContext(base);
    }



    private NewsSdk.CustomNewsIntent setCustomNewsIntent() {
        return
                new NewsSdk.CustomNewsIntent() {
                    @Override
                    public Intent getCustomNewsIntent(Context context, ONewsScenario oNewsScenario,
                                                      ONews oNews, int source) {

                        if (null == context || null == oNews ){
                            return null;
                        }

                        Intent intent = null;
                        if (ONewsSupportAction.supportAction(ONewsSupportAction.SA_20).equalsIgnoreCase(oNews.action())){
                            // 專題打開
                            intent = new Intent(context, NewsAlbumActivity.class);
                            if (!(context instanceof Activity)) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            }
                        }else if (ONewsSupportAction.supportAction(ONewsSupportAction.SA_04).equalsIgnoreCase(oNews.action())) {
                            // 撥放視頻
                            if (false|| Build.VERSION.SDK_INT < 16) {
                                intent = new Intent(context, NewsVideoFullScreenActivity.class);
                            } else {
                                intent = new Intent(context, NewsVideoActivity.class);
                            }
                            if (!(context instanceof Activity)) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            }
                        }

                        if (null != intent) {
                            intent.putExtra(NewsBaseActivity.KEY_NEWS, oNews.toContentValues());

                            Bundle bundle = new Bundle();
                            bundle.putParcelable(NewsBaseActivity.KEY_SCENARIO, oNewsScenario);
                            intent.putExtra(NewsBaseActivity.KEY_BUNDLE, bundle);

                            intent.putExtra(NewsBaseActivity.KEY_FROM, source);
                            intent.putExtra(NewsVideoActivity.CONTENT_ID_EXTRA, "video");
                            intent.putExtra(NewsVideoActivity.CONTENT_TYPE_EXTRA, NewsVideoActivity.TYPE_OTHER);
                        }

                        return intent;
                    }
                };
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mMoApp.onConfigurationChanged(newConfig);
    }
}
