package com.cmcm.onews;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.util.Log;

import com.cleanmaster.base.crash.MyCrashHandler;
import com.cleanmaster.base.crash.util.system.ConflictCommons;
import com.cleanmaster.base.crash.util.system.ProductId;
import com.cleanmaster.sdk.cmloginsdkjar.Settings;
import com.cm.CH;
import com.cm.kinfoc.api.InfocInitHelper;
import com.cmcm.adsdk.CMAdManager;
import com.cmcm.cloudconfig.CloudConfigIni;
import com.cmcm.cloudconfig.CloudConfigMgr;
import com.cmcm.config.NewsLoginConfigMgr;

import com.cmcm.onews.ad.NativeAdProvider;
import com.cmcm.onews.ad.NewsAdAdapter;
import com.cmcm.onews.ad.ONewsAdProvider;
import com.cmcm.onews.ad.ONewsAds;
import com.cmcm.onews.api.ActivitiesMgr;
import com.cmcm.onews.bitmapcache.DbPath;
import com.cmcm.onews.bitmapcache.VolleySingleton;
import com.cmcm.onews.crash.CrashDependence;
import com.cmcm.onews.event.ONewsEventManager;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.service.LocalService;
import com.cmcm.onews.service.NetworkStateRecordService;
import com.cmcm.onews.service.ONewsService;
import com.cmcm.onews.system.NET_STATUS;
import com.cmcm.onews.ui.NewsActivity;
import com.cmcm.onews.ui.NewsAlbumActivity;
import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.Commons;
import com.cmcm.onews.util.NewsDebugConfigUtil;
import com.cmcm.onews.util.PushForceUpdateHelper;
import com.cmcm.onews.util.push.PushOutAPI;
import com.cmcm.onews.util.template.WebViewPreparer;
import com.cmcm.splash.SplashHelper;
import com.cmcm.terminal.Console;
import com.cmcm.terminal.EventConsoleMessage;
import com.memetix.mst.translate.Translate;
import java.util.ArrayList;
import com.cmcm.update.UpdateHelper;


/* BUILD_CTRL:IF:OU_VERSION_ONLY */
import com.cmcm.onews.util.share.FBShare;
import com.facebook.FacebookSdk;
import com.cmcm.login.LoginDataHelper;
import com.cmcm.onews.util.share.ShareToFacebook;
/* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */

/**
 * Global Context
 * 全局context
 * 客户端公共初始化
 */
public class
        C {
    /**
     * InstaNews崩溃平台上报KEY
     */
    public static final String NEWS_INDIA_CRASH_KEY = "24";

    /**
     * 今日快报崩溃平台上报KEY
     */
    public static final String NEWS_INDIA_CRASH_KEY_CN = "5";

    private static C theApp;
    private static Context mAppContext = null;
    public static long START_TIME = 0;

    public C() {
        theApp = this;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public static C getInstance() {
        return theApp;
    }

    public void SetAppBaseContext(Context cxt) {
        mAppContext = cxt;
    }

    //todo 初始化操作
    public void onCreate() {
        /**
         *   不
         *   要
         *   在
         *   此
         *   添
         *   加
         *   任
         *   何
         *   代
         *   码, 保证你的代码尽可能工作在CRASH监控注册完成之后
         */
        CRASH_FIRST();
        ADD_YOUR_ONCREATE_CODE_HERE();
    }

    private void CRASH_FIRST() {
        CrashDependence dependence = new CrashDependence(mAppContext);
        MyCrashHandler.initCrashHandler(dependence);
        MyCrashHandler.getInstance().register(mAppContext);
        if(com.cmcm.onews.util.ConflictCommons.isCNVersion()){
            ConflictCommons.setProductId(ProductId.CN);
        }else{
            ConflictCommons.setProductId(ProductId.OU);
        }
        // 初始化网络状态
        NET_STATUS.last(NET_STATUS.current(getAppContext()));
        Console.getInstance().setMessageListener(new Console.MessageListener() {
            @Override
            public void onNewMessage(Console.Message message) {
                ONewsEventManager.getInstance().sendEvent(new EventConsoleMessage(message));
            }
        });
    }

    private void ADD_YOUR_ONCREATE_CODE_HERE() {
        DbPath.init(mAppContext, "newsindia");

        Commons.updateLanguage(mAppContext);

        VolleySingleton.setAppContext(mAppContext);
        // 初始化崩溃上报
        START_TIME = System.currentTimeMillis();


        // 初始化INFOC

        InfocInitHelper.init(mAppContext);

        WebViewPreparer.getInstance();


        BackgroundThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                ONewsService.start_ACTION_ACT_BG(getAppContext());
            }
        }, 60 * 1000);

        //初始化debug数据
        INIT_DEBUG();

        //分类初始化
        ONewsScenarios.getInstance();
        //推送sdk注册
        PushOutAPI.register(mAppContext);

        // 聚合SDK接入
        initCMAd();

        // facebook sdk init
        /* BUILD_CTRL:IF:OU_VERSION_ONLY */
        if(!ConflictCommons.isCNVersion()) {
            FacebookSdk.sdkInitialize(mAppContext);
            FBShare.getInstance().initFBShare(ShareToFacebook.getInstance());
        }
        /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */

        //翻译初始化
        Translate.setClientId("instanews123");
        Translate.setClientSecret("4/7wdhSTX5tGTtvv09EhldWclpU+oq/UiwVmuLenSc0=");

        asyncInit();
        syncInit();
    }

    private void asyncInit() {


        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                //魔方初始化
                CloudConfigIni.init();
                //
                UpdateHelper.getInstance().init();
                registerCloudUpdateReceiver(MainEntry.getAppContext());
                PushForceUpdateHelper.getIns().init();
                CloudConfigMgr.getIns().init();

                // 启动网络状态记录
                NetworkStateRecordService.start_ACTION_ON_APP_START(getAppContext());

//                autoOffline();
            }
        });

    }

    private void autoOffline() {
        NET_STATUS STATUS = NET_STATUS.current(C.getAppContext());
        if(STATUS == NET_STATUS.WIFI){
            LocalService.start_ACTION_AUTO_OFFLINE_NEWS(getAppContext());
        }
    }

    private void registerCloudUpdateReceiver(Context context){
        PullConfigUpdateReceiver receiver = new PullConfigUpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.cmplay.activesdk.cloud_cfg.update");
        context.registerReceiver(receiver, filter);
    }

    static class PullConfigUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 魔方数据更新了
            // 开屏页面去下载图片
            if (L.DEBUG){
                Log.e("PullConfigUpdate","魔方更新了");
            }
            SplashHelper.getIns().loadResBitmap();

        }
    }

    /**
     * 初始化debug 开关数据
     */
    private void INIT_DEBUG() {
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                NewsDebugConfigUtil.getInstance();
            }
        });
    }

    public void addActivity(Activity act) {
        if(null != ActivitiesMgr.INSTANCE){
            ActivitiesMgr.INSTANCE.addActivity(act);
        }
    }

    public void removeActivity(Activity act) {
        if(null != ActivitiesMgr.INSTANCE){
            ActivitiesMgr.INSTANCE.removeActivity(act);
        }
    }

    /**
     * 标记是否离线
     */
    private volatile boolean isOffline = false;

    public void isOffline(boolean offline) {
        this.isOffline = offline;
    }

    public boolean isOffline() {
        return isOffline;
    }

    // TODO 需要修改 AppId 和 产品渠道号Id，没有可填""
    private void initCMAd() {
        //设置是否是猎豹移动公司内部产品
        CMAdManager.setIsInner();
        //初始化聚合sdk
        CMAdManager.applicationInit(mAppContext, ONewsAdProvider.MID(), String.valueOf(CH.getChannelId()));
        if (L.DEBUG) {
            //开启Debug模式，默认不开启不会打印log
            CMAdManager.setDebug();
        }

        ONewsAdProvider.updatePicksConfigForInterstatial();
        // 初始化詳情頁廣告提供
        NativeAdProvider.getInstance().init(new NewsAdAdapter());
        ONewsAds.getInstance().setProvider(ONewsAdProvider.getInstance());
        ONewsAdProvider.getInstance().init();
    }

    public boolean isAlbumToNews() {
        ArrayList<Activity> activities = ActivitiesMgr.INSTANCE.activities();
        if (null == activities || activities.isEmpty()) {
            return true;
        }

        int cout = 0;
        for (Activity activity : activities) {
            if (activity instanceof NewsActivity) {
                return false;
            }

            if (activity instanceof NewsAlbumActivity) {
                cout += 1;
                if (cout > 1) {
                    return false;
                }
            }
        }

        return true;
    }


    private void syncInit(){
        try {
            Settings.loadDefaultsFromMetadata(getAppContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initLoginConfig();
    }

    private void initLoginConfig() {
        NewsLoginConfigMgr.getInstance().setOnLoginConfigCallback(new NewsLoginConfigMgr.OnLoginConfigCallback() {
            @Override
            public boolean isLogined() {
                /* BUILD_CTRL:IF:OU_VERSION_ONLY */
                if (!com.cmcm.onews.util.ConflictCommons.isCNVersion()) {
                    return LoginDataHelper.getInstance().isLogined();
                }
                /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
                return false;
            }

            @Override
            public String getUserName() {
                /* BUILD_CTRL:IF:OU_VERSION_ONLY */
                LoginDataHelper.LoginUserInfo userInfo = LoginDataHelper.getInstance().getLoginUserInfo();
                if (null != userInfo) {
                    return userInfo.getNickname();
                }
                /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
                return null;
            }

            @Override
            public String getUserIconUrl() {
                 /* BUILD_CTRL:IF:OU_VERSION_ONLY */
                LoginDataHelper.LoginUserInfo userInfo = LoginDataHelper.getInstance().getLoginUserInfo();
                if (null != userInfo) {
                    return userInfo.getAvatar();
                }
                /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
                return null;
            }

            @Override
            public String getCmSSoToken() {
                /* BUILD_CTRL:IF:OU_VERSION_ONLY */
                LoginDataHelper.LoginInfo cmInfo = LoginDataHelper.getInstance().getLoginInfo();
                if (null != cmInfo) {
                    return cmInfo.getSso_token();
                }
                /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
                return null;
            }

            @Override
            public String getCmSid() {
                /* BUILD_CTRL:IF:OU_VERSION_ONLY */
                LoginDataHelper.LoginInfo cmInfo = LoginDataHelper.getInstance().getLoginInfo();
                if (null != cmInfo) {
                    return cmInfo.getSid();
                }
                 /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
                return null;
            }

            @Override
            public String getOpenId() {
                  /* BUILD_CTRL:IF:OU_VERSION_ONLY */
                LoginDataHelper.LoginUserInfo userInfo = LoginDataHelper.getInstance().getLoginUserInfo();
                if (null != userInfo) {
                    return userInfo.getOpenId();
                }
                /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
                return null;
            }
        });
    }

    public void onConfigurationChanged(Configuration newConfig) {
        Commons.updateLanguage(mAppContext);
    }
}
