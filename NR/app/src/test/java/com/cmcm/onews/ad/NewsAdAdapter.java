
package com.cmcm.onews.ad;

import android.view.View;
import android.widget.RelativeLayout;

import com.cmcm.adsdk.nativead.NativeAdManager;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.onews.infoc.newsindia_adtrace;
import com.cmcm.onews.infoc.newsindia_detailaction;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.util.ConflictCommons;
import com.cmcm.onews.util.NetworkUtil;

import java.util.HashMap;
import java.util.Map;

public class NewsAdAdapter extends iAdProvider implements INativeAd.IAdOnClickListener , INativeAd.ImpressionListener{

    private NativeAdManager mNativeAdManager;


    public  static final String MID             = "1226";
    private static final String DETAIL_POSID    = "1226108";

    public  static final String MID_CN          = "1075";
    private static final String DETAIL_POSID_CN = "1075102";

    private String mCPid;
    private String mNewsId;
    private com.cleanmaster.ui.app.utils.MarketContext marketContext;

    private Map<Long, INativeAd> iAdMap = new HashMap<Long, INativeAd>();

    public NewsAdAdapter() {
        marketContext = new com.cleanmaster.ui.app.utils.MarketContext(NewsSdk.INSTAMCE.getAppContext());

        // init ad manager by postID
        if(ConflictCommons.isCNVersion()){
            mNativeAdManager = new NativeAdManager(marketContext, DETAIL_POSID_CN);
        }else {
            mNativeAdManager = new NativeAdManager(marketContext, DETAIL_POSID);
        }
        
        mNativeAdManager.preloadAd();
    }
    public void appendAdView(RelativeLayout nativeAdContainer, Map<String, String> params) {
        mCPid = params.get("cpid");;
        mNewsId = params.get("newsid");
        L.ad("appendAdView cpid : " + mCPid);

        INativeAd iAd = mNativeAdManager.getAd();
        mNativeAdManager.preloadAd();
        View nativeAdView;

        if (null != iAd) {
            String mainImageUrl = iAd.getAdCoverImageUrl();
            nativeAdView = NativeAdProvider.createDefaultAdView(mainImageUrl, iAd.getAdTitle(), iAd.getAdBody());

            if (nativeAdContainer != null) {
                // 将广告的View加到容器中
                RelativeLayout.LayoutParams ad_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                nativeAdContainer.addView(nativeAdView, ad_params);
                nativeAdContainer.setTag(System.currentTimeMillis());
                Long viewKey = (Long)nativeAdContainer.getTag();
                iAdMap.put(viewKey, iAd);
            }

            //将广告View和广告对象绑定起来
            iAd.registerViewForInteraction_withExtraReportParams(nativeAdView, params);
            iAd.setAdOnClickListener(this);
            iAd.setImpressionListener(this);
            reportADTrace(newsindia_adtrace.STATUS_SUCCESS);

        } else {
            L.ad("get ad fail !");
            reportADTrace(newsindia_adtrace.STATUS_FAIL);
        }
    }

    public void unregisterView(RelativeLayout nativeAdContainer) {
        Long viewKey = (Long)nativeAdContainer.getTag();
        INativeAd iAd = iAdMap.get(viewKey);
        if (iAd != null) {
            iAd.unregisterView();
            iAdMap.remove(viewKey);
        }
    }

    public void onAdClick() {
        reportDetailAction(newsindia_detailaction.DETAIL_ACTION_CLICK_AD, mNewsId);
    }

    public void onLoggingImpression() {
        reportDetailAction(newsindia_detailaction.DETAIL_ACTION_DISPLAY_AD, mNewsId);
    }

    //infoC report // 广告 展示 点击1展示2点击
    private static void reportDetailAction(int actionID,String newsid){
        newsindia_detailaction detailaction_report = new newsindia_detailaction();
        detailaction_report.ads(actionID);
        detailaction_report.newsid(newsid);
        detailaction_report.report();
    }


    public static void reportADTrace(int status) {
        //infoC report // 加载成功或失败：1：成功，2：失败
        newsindia_adtrace newsindia_adtrace_report = new newsindia_adtrace();
        newsindia_adtrace_report.network(NetworkUtil.getNetWorkType(NewsSdk.INSTAMCE.getAppContext()));
        newsindia_adtrace_report.status(status);
        newsindia_adtrace_report.pos(newsindia_adtrace.POS_DETAIL);
        newsindia_adtrace_report.report();
    }
}
