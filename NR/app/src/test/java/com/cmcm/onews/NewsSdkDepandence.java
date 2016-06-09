package com.cmcm.onews;

import android.content.Context;

import com.cm.kinfoc.AbstractTracer;
import com.cm.kinfoc.BaseTracer;
import com.cmcm.onews.infoc.newsindia_apifailed;
import com.cmcm.onews.infoc.newsindia_apiperformance;
import com.cmcm.onews.infoc.newsindia_loadtime;
import com.cmcm.onews.infoc.newsindia_net_tracer;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.sdk.BaseDependence;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.service.TranslatorBody;
import com.cmcm.onews.service.TranslatorTitle;
import com.cmcm.onews.stat.TimeCounter;
import com.cmcm.onews.system.NET_STATUS;
import com.cmcm.onews.transport.ONewsHttpClient;
import com.cmcm.onews.transport.ONewsRequestBuilder;
import com.cmcm.onews.ui.NewsAlbumActivity;
import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.LanguageCountry;
import com.cmcm.onews.util.NetworkUtil;
import com.cmcm.onews.util.NewsDebugConfigUtil;
import com.cmcm.onews.util.SDKConfigManager;
import com.cmcm.onews.util.UIConfigManager;

/**
 * Created by yzx on 2015/12/1.
 */
public class NewsSdkDepandence extends BaseDependence{

    @Override
    public void report(AbstractTracer tracer) {
        BaseTracer.report(tracer);
    }

    @Override
    public void infocloadtime(long time) {
        newsindia_loadtime loadtime = new newsindia_loadtime();
        loadtime.textonly(SDKConfigManager.getInstanse(C.getAppContext()).getNEWS_ITEM_SHOWIMG() ? 1 : 2);
        loadtime.listime((int) time);
        loadtime.network(NetworkUtil.getNetWorkType(C.getAppContext()));
        loadtime.loadingtime(0);
        loadtime.report();
    }

    @Override
    public void infocloadtime(int time, int ifweb ) {
        newsindia_loadtime loadtime = new newsindia_loadtime();
        loadtime.textonly(SDKConfigManager.getInstanse(C.getAppContext()).getNEWS_ITEM_SHOWIMG() ? 1 : 2);
        loadtime.network(NetworkUtil.getNetWorkType(C.getAppContext()));
        loadtime.loadingtime(time);
        loadtime.ifweb(ifweb);
        loadtime.report();
    }

    @Override
    public void apiperformance_report(String url, int time1, String currentNetworkShortName) {
        new newsindia_apiperformance()
                    .url(url)
                    .time1(time1)
                    .net(currentNetworkShortName)
                    .report();
    }

    @Override
    public void apifailed_report(String url, int time1, String errMessage, String currentNetworkShortName) {
        new newsindia_apifailed()
                    .url(url)
                    .time1(time1)
                    .message(errMessage)
                    .net(currentNetworkShortName)
                    .report();
    }

    @Override
    public ONewsHttpClient.IHttpTracer getHttpTracer() {
        return new ONewsHttpClient.IHttpTracer() {
            TimeCounter node = new TimeCounter("HttpTracer");
            @Override
            public void onPreHttpRequest(ONewsRequestBuilder request) {
                node.start();
            }

            @Override
            public void onPostHttpRequestFailed(ONewsRequestBuilder request, Exception e, int code) {
                String net = NET_STATUS.getCurrentNetworkShortName(NewsSdk.INSTAMCE.getAppContext());
                new newsindia_apifailed()
                        .url(request.toReportUrl())
                        .time1((int)node.stop().n())
                        .message(null != e ? e.getMessage() : "")
                        .net(net)
                        .report();
                new newsindia_net_tracer().net(net).code(code).api_name(request.getApiId()).report();
            }

            @Override
            public void onPostHttpRequestSuccess(ONewsRequestBuilder request, String json) {
                String net = NET_STATUS.getCurrentNetworkShortName(NewsSdk.INSTAMCE.getAppContext());
                new newsindia_apiperformance()
                        .url(request.toReportUrl())
                        .time1((int) node.stop().n())
                        .net(net)
                        .report();
                new newsindia_net_tracer().net(net).code(200).api_name(request.getApiId()).report();
            }
        };
    }

//    @Override
//    public String supportInterest() {
//        return UIConfigManager.getInstanse(C.getAppContext()).getNEWS_CATEGORY_INTEREST();
//    }

    
    // setDetailsBackTo(Intent intent)
//    @Override
//    public void goBack(Context context) {
//        try{
//            Intent intent = new Intent();
//            intent.setClass(context, NewsActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            context.startActivity(intent);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    @Override
    public void debugOpenAlbum(Context context,ONewsScenario scenario, String contentId) {
        NewsAlbumActivity.startByDebug(context,scenario,contentId);
    }

    @Override
    public void translateNewsBody(String body) {
        BackgroundThread.post(new TranslatorBody(body));
    }
}
