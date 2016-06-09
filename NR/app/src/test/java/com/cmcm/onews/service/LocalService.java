package com.cmcm.onews.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.cm.kinfoc.KInfocBatchManager;
import com.cm.kinfoc.KInfocClient;
import com.cmcm.feedback.service.EvFeedbackResult;
import com.cmcm.feedback.service.FeedBackDataBean;
import com.cmcm.feedback.service.FeedBackHelper;
import com.cmcm.onews.BuildConfig;
import com.cmcm.onews.C;
import com.cmcm.onews.NewsL;
import com.cmcm.onews.ONewsScenarios;
import com.cmcm.onews.bitmapcache.ImageLoaderSingleton;
import com.cmcm.onews.event.EventNetworkChanged;
import com.cmcm.onews.event.FireEvent;
import com.cmcm.onews.infoc.newsindia_actbg;
import com.cmcm.onews.infoc.newsindia_offline;
import com.cmcm.onews.loader.LOAD_DETAILS;
import com.cmcm.onews.loader.ONewsDetailLoader;
import com.cmcm.onews.loader.ONewsLoadResult_LOAD_REMOTE;
import com.cmcm.onews.loader.ONewsNResult;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.model.ONewsScenarioCategory;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.storage.ONewsProviderManager;
import com.cmcm.onews.system.NET_STATUS;
import com.cmcm.onews.util.LanguageUtils;
import com.cmcm.onews.util.NetworkUtil;
import com.cmcm.onews.util.UIConfigManager;
import com.cmcm.onews.util.push.gcm.GCMRegister;

import java.util.List;

public class LocalService extends IntentService{

    private static final String PACKAGE = BuildConfig.APPLICATION_ID;

    private static final String ACTION_OFFLINE_NEWS = PACKAGE+".ACTION_OFFLINE_NEWS";
    private static final String ACTION_CLEAR_CAHCE = PACKAGE+".ACTION_CLEAR_CAHCE";
    private static final String ACTION_ON_RECEIVE = PACKAGE+".ACTION_ON_RECEIVE";
    public static final String ACTION_UPLOAD_LOGS=PACKAGE+".ACTION_ON_UPLOADLOGS";
    public static final String ACTION_AUTO_OFFLINE_NEWS =PACKAGE+".ACTION_AUTO_OFFLINE_NEWS";

    private static final String KEY_COUNT = ":count";
    private static final String KEY_OFFLINE_SIZE = ":offline_size";
    private static final String KEY_INTENT = ":intent";

    private volatile boolean isOffline = false;
    private volatile boolean isClearCache = false;
    private volatile boolean isAutoOffline = false;

    public LocalService() {
        super("LocalService");
    }

    /**
     * 离线新闻
     * @param context
     */
    public static  void start_ACTION_OFFLINE_NEWS(Context context,int count,int size) {
        if(null == context){
            return;
        }

        Intent intent = new Intent();
        intent.setClass(context, LocalService.class);
        intent.setAction(ACTION_OFFLINE_NEWS);
        intent.putExtra(KEY_COUNT, count);
        intent.putExtra(KEY_OFFLINE_SIZE, size);

        if(NewsL.DEBUG) NewsL.localservice("start_ACTION_OFFLINE_NEWS");

        context.startService(intent);
    }

    /**
     * wifi 自动离线
     * @param context
     */
    public static void start_ACTION_AUTO_OFFLINE_NEWS(Context context){
        if(null == context){
            return;
        }

        Intent intent = new Intent();
        intent.setClass(context, LocalService.class);
        intent.setAction(ACTION_AUTO_OFFLINE_NEWS);
        intent.putExtra(KEY_COUNT, Integer.MAX_VALUE);

        if(NewsL.DEBUG) NewsL.localservice("start_ACTION_AUTO_OFFLINE_NEWS");

        context.startService(intent);
    }

    public static void start_ACTION_ON_RECEIVE(Context context, Intent intent) {
        if(TextUtils.isEmpty(intent.getAction())) {
            return;
        }

        if(null == context){
            return;
        }

        Intent i = new Intent();
        i.setClass(context, LocalService.class);
        i.setAction(ACTION_ON_RECEIVE);
        i.putExtra(KEY_INTENT, intent);
        context.startService(i);
    }

    /**
     * 清理缓存
     * @param context
     */
    public static  void start_ACTION_CLEAR_CAHCE(Context context) {
        if(null == context){
            return;
        }

        Intent intent = new Intent();
        intent.setClass(context, LocalService.class);
        intent.setAction(ACTION_CLEAR_CAHCE);

        if(NewsL.DEBUG) NewsL.localservice("start_ACTION_CLEAR_CAHCE");

        context.startService(intent);
    }


    public static void start_ACTION_UPLOAD_LOGS(Context context,FeedBackDataBean feedBackData) {
        Intent intent = new Intent();
        intent.setClass(context, LocalService.class);
        intent.putExtra(":feedback_data", feedBackData);
        intent.setAction(ACTION_UPLOAD_LOGS);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent == null) {
            return;
        }

        String action = intent.getAction();
        if (ACTION_OFFLINE_NEWS.equals(action)) {
            onHandle_ACTION_OFFLINE_NEWS(intent);
        } else if(ACTION_CLEAR_CAHCE.equals(action)){
            onHandle_ACTION_CLEAR_CAHCE(intent);
        } else if(ACTION_ON_RECEIVE.equals(action)) {
            onHandle_ACTION_ON_RECEIVE(intent);
        }else  if (ACTION_UPLOAD_LOGS.equalsIgnoreCase(action)){
            onHandle_ACTION_ON_FEEDBACK(intent);
        }else if(ACTION_AUTO_OFFLINE_NEWS.equalsIgnoreCase(action)){
            onHandle_ACTION_AUTO_OFFLINE_NEWS(intent);
        }
    }

    private void onHandle_ACTION_ON_FEEDBACK(Intent intent){
        if (L.DEBUG){
            Log.e("suj","onHandle_ACTION_ON_FEEDBACK");
        }
        long startCommitTime = System.currentTimeMillis();
        final String uploadLog = FeedBackHelper.uploadLog(intent);
        new EvFeedbackResult(uploadLog,startCommitTime).send();
    }

    private void onHandle_ACTION_ON_RECEIVE(Intent i) {
        Intent intent = i.getParcelableExtra(KEY_INTENT);
        if (intent == null)
            return;

        final String action = intent.getAction();
        if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            NET_STATUS currNet = NET_STATUS.current(this);
            NET_STATUS lastNet = NET_STATUS.last(currNet);
            if (currNet != lastNet) {
                EventNetworkChanged event = new EventNetworkChanged();

                switch (lastNet) {
                    case WIFI:
                        onWifiDn(this, currNet);
                        break;
                    case MOBILE:
                        onMobileDn(this, currNet);
                        break;
                    case NO:
                break;
            }

                switch (currNet) {
                    case WIFI:
                        onWifiUp(this);
//                        LocalService.start_ACTION_AUTO_OFFLINE_NEWS(C.getAppContext());
                        break;
                    case MOBILE:
                        onMobileUp(this);
                        break;
                    case NO:
                        break;
                }
                event.setStatus(currNet, lastNet);
                event.send();
            }

            //GCM
            registerGCM(this);
        } else if(Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            ONewsService.start_ACTION_ACT_BG(this);
        }
    }

    private void onWifiUp(Context context) {
        // 上报数据
        KInfocBatchManager.getInstance().requestReport();
        KInfocClient.getInstance().reportCache();
        new newsindia_actbg().report();
    }

    private void onMobileUp(Context context) {
        KInfocClient.getInstance().reportCache();
        ImageLoaderSingleton.getInstance().cancelImgRequest();
        new newsindia_actbg().report();
    }

    private void onWifiDn(Context context, NET_STATUS currNet) {
        new newsindia_actbg().report();
    }

    private void onMobileDn(Context context, NET_STATUS currNet) {
        new newsindia_actbg().report();
    }

    /**
     * 下载所有缓存新闻
     * @param intent
     */
    private void onHandle_ACTION_OFFLINE_NEWS(Intent intent) {
        if(NewsL.DEBUG) NewsL.localservice("onHandle_ACTION_OFFLINE_NEWS");

        if(isOffline){
            return;
        }

        final int count = intent.getIntExtra(KEY_COUNT, -1);
        final int offlinesize = intent.getIntExtra(KEY_OFFLINE_SIZE, -1);

        if(count <= 0){
            return;
        }

        List<ONewsScenario> scenarios = ONewsScenarios.getInstance().scenarios();
        int size = scenarios.size();
        LOAD_DETAILS[] DETAILS = new LOAD_DETAILS[size];

        for(int i=0;i<size;i++){
            DETAILS[i] = new LOAD_DETAILS(scenarios.get(i)).STATE_OFFLINE().count(count);
        }

        new ONewsDetailLoader(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isOffline = true;
                C.getInstance().isOffline(true);
            }

            @Override
            protected void onLoadResultInBackground(ONewsLoadResult_LOAD_REMOTE r) {
                super.onLoadResultInBackground(r);
            }

            @Override
            protected void onLoadFinishedInBackground(ONewsNResult result) {
                super.onLoadFinishedInBackground(result);
                isOffline = false;
                C.getInstance().isOffline(false);
                if(result.newsList().size() > 0){
                    UIConfigManager.getInstanse(C.getAppContext()).setNEWS_LAST_OFFLINE(System.currentTimeMillis());
                }
                FireEvent.FIRE_EventOfflineSuccess(count,result.newsList().size());

                reportoffline(count,offlinesize,result.newsList().size() > 0 ? 1 : 2);
            }

        }.execute(DETAILS);
    }

    /**
     * 清理缓存
     * @param intent
     */
    private void onHandle_ACTION_CLEAR_CAHCE(Intent intent) {
        if(NewsL.DEBUG) NewsL.localservice("onHandle_ACTION_DELETE_ALL_NEWS");

        if(isClearCache){
            return;
        }

        isClearCache = true;

        //清理离线
        List<ONewsScenario> scenarios = ONewsScenarios.getInstance().scenarios();
        for(ONewsScenario scenario : scenarios){
            ONewsProviderManager.getInstance().clearByUser(scenario);
        }

        UIConfigManager.getInstanse(C.getAppContext()).setNEWS_LAST_OFFLINE(-1);
        FireEvent.FIRE_EventClearOffline();
        isClearCache = false;
    }

    /**
     * 自动离线离线
     * 1.启动app 网络状态wifi自动离线
     * 2.网络状态切换  wifi自动离线
     * @param intent
     */
    private void onHandle_ACTION_AUTO_OFFLINE_NEWS(Intent intent) {
        if(NewsL.DEBUG) NewsL.localservice("onHandle_ACTION_AUTO_OFFLINE_NEWS");

        if(isAutoOffline){
            return;
        }

        final int count = intent.getIntExtra(KEY_COUNT, -1);
        if(count <= 0){
            return;
        }

        LOAD_DETAILS[] DETAILS = new LOAD_DETAILS[1];
        DETAILS[0] = new LOAD_DETAILS(ONewsScenario.getScenarioByCategory(ONewsScenarioCategory.SC_1D)).STATE_OFFLINE().count(count);

        new ONewsDetailLoader(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isAutoOffline = true;
            }

            @Override
            protected void onLoadResultInBackground(ONewsLoadResult_LOAD_REMOTE r) {
                super.onLoadResultInBackground(r);
            }

            @Override
            protected void onLoadFinishedInBackground(ONewsNResult result) {
                super.onLoadFinishedInBackground(result);
                isAutoOffline = false;
            }

        }.execute(DETAILS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isOffline = false;
        isClearCache = false;
        isAutoOffline = false;
    }

    /**
     * infoc newsindia_bookmark
     */
    private void reportoffline(int count,int offlinesize,int fail){
        newsindia_offline offline = new newsindia_offline();
        offline.size(offlinesize);
        offline.num(count);
        offline.clicknum(1);
        offline.network(NetworkUtil.getNetWorkType(C.getAppContext()));
        offline.fail(fail);
        offline.report();
    }

    private void registerGCM(Context context) {
        // Add custom implementation, as needed.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean sentToken = sharedPreferences.getBoolean(GCMRegister.SENT_TOKEN_TO_SERVER, false);
        L.gcm("sentToken=" + sentToken );
        if(!sentToken){
            LanguageUtils.registerGCM(context);
        }
    }
}
