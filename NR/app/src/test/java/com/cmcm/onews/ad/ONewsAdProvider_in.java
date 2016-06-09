package com.cmcm.onews.ad;

import android.support.annotation.Nullable;

import com.cleanmaster.ui.app.utils.MarketContext;
import com.cmcm.adsdk.nativead.NativeAdListLoader;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.onews.C;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.model.ONewsScenarioCategory;
import com.cmcm.onews.sdk.L;

public class ONewsAdProvider_in {
    /**
     * 广告posid
     */
    private static final String SC_1D = "1226109";
    private static final String SC_1C = "1226101";
    private static final String SC_03 = "1226102";
    private static final String SC_20 = "1226103";
    private static final String SC_01 = "1226104";
    private static final String SC_04 = "1226105";
    private static final String SC_1E = "1226106";
    private static final String SC_1F = "1226107";
    public static final String MID = "1226";

    private static final int MIN_SIZE = 3;
    private static final int PRE_NUM = 25;
//    private Map<String,NativeAdListLoader> ads = Collections.synchronizedMap(new HashMap<String, NativeAdListLoader>());
    private NativeAdListLoader adListLoader = null;

    private static ONewsAdProvider_in sInstance = null;

    public static ONewsAdProvider_in getInstance() {
        if (sInstance == null) {
            synchronized (ONewsAdProvider_in.class) {
                if (sInstance == null) {
                    sInstance = new ONewsAdProvider_in();
                }
            }
        }
        return sInstance;
    }

    /**
     * 国际初始化
     */
    public void initAds() {
        if(L.DEBUG) L.ad("ads init in");
        MarketContext marketContext = new MarketContext(C.getAppContext());
        adListLoader = new NativeAdListLoader(marketContext,SC_1D, MIN_SIZE);
        adListLoader.loadAds(PRE_NUM);
    }

    /**
     * 国际广告
     * @return
     */
    @Nullable
    public IONewsAd getNewsAd(ONewsScenario scenario) {
        if(null == adListLoader){
            return null;
        }

        if(null == scenario || scenario.getCategory() == ONewsScenarioCategory.SC_1C){
            return null;
        }

        INativeAd iNativeAd = adListLoader.getAd();
        if(null == iNativeAd){
            return null;
        }else {
            return new ONewsAd(iNativeAd);
        }
    }

//    /**
//     * 国际广告
//     * @param scenario
//     * @return
//     */
//    @Nullable
//    public IONewsAd getNewsAd(ONewsScenario scenario) {
//        if(null == ads || null == scenario){
//            return null;
//        }
//
//        if(L.DEBUG) L.ad(String.format("ads getINativeAd %1s ",scenario.getStringValue()));
//
//        NativeAdListLoader adListLoader = ads.get(scenario.getStringValue());
//        if(null == adListLoader){
//            return null;
//        }
//
//        INativeAd iNativeAd = adListLoader.getAd();
//        if(null == iNativeAd){
//            return null;
//        }else {
//            return new ONewsAd(iNativeAd);
//        }
//    }

//    /**
//     * 国际初始化
//     * 初始化hot列表页
//     */
//    public void initAds(ONewsScenario scenario) {
//        if(L.DEBUG) L.ad("ads init");
//        if(null == scenario){
//            if(L.DEBUG) L.ad("ads ONewsScenario null");
//            return;
//        }
//
//        if(ads.containsKey(scenario.getStringValue())){
//            if(L.DEBUG) L.ad("ads ONewsScenario has init");
//            return;
//        }
//
//        if(scenario.getCategory() == ONewsScenarioCategory.SC_1C){
//            if(L.DEBUG) L.ad("ads ONewsScenario video");
//            return;
//        }
//
//        if(L.DEBUG) L.ad("ads init  " +scenario.getStringValue());
//
//        MarketContext marketContext = new MarketContext(C.getAppContext());
//        NativeAdListLoader adListLoader = null;
//        switch (scenario.getCategory()){
//            case ONewsScenarioCategory.SC_1D:
//                adListLoader  = new NativeAdListLoader(marketContext,SC_1D, MIN_SIZE);
//                break;
//            case ONewsScenarioCategory.SC_1C:
//                adListLoader  = new NativeAdListLoader(marketContext,SC_1C, MIN_SIZE);
//                break;
//            case ONewsScenarioCategory.SC_03:
//                adListLoader  = new NativeAdListLoader(marketContext,SC_03, MIN_SIZE);
//                break;
//            case ONewsScenarioCategory.SC_20:
//                adListLoader  = new NativeAdListLoader(marketContext,SC_20, MIN_SIZE);
//                break;
//            case ONewsScenarioCategory.SC_04:
//                adListLoader  = new NativeAdListLoader(marketContext,SC_04, MIN_SIZE);
//                break;
//            case ONewsScenarioCategory.SC_01:
//                adListLoader  = new NativeAdListLoader(marketContext,SC_01, MIN_SIZE);
//                break;
//            case ONewsScenarioCategory.SC_1E:
//                adListLoader  = new NativeAdListLoader(marketContext,SC_1E, MIN_SIZE);
//                break;
//            case ONewsScenarioCategory.SC_1F:
//                adListLoader  = new NativeAdListLoader(marketContext,SC_1F, MIN_SIZE);
//                break;
//        }
//
//        if(null != adListLoader){
//            adListLoader.loadAds(PRE_NUM);
//            ads.put(scenario.getStringValue(),adListLoader);
//        }
//
//        if(L.DEBUG) L.ad(String.format("ads init %1s ",scenario.getStringValue()));
//    }
}
