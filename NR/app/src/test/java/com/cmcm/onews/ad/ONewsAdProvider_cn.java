package com.cmcm.onews.ad;

import android.support.annotation.Nullable;

import com.cleanmaster.ui.app.utils.MarketContext;
import com.cmcm.adsdk.nativead.NativeAdListLoader;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.onews.C;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.sdk.L;

public class ONewsAdProvider_cn {
    private NativeAdListLoader adListLoader = null;
    public static final String MID = "1075";
    public static final String POSID = "1075101";
    private static final int MIN_SIZE = 3;
    private static final int PRE_NUM = 25;

    private static ONewsAdProvider_cn sInstance = null;

    public static ONewsAdProvider_cn getInstance() {
        if (sInstance == null) {
            synchronized (ONewsAdProvider_cn.class) {
                if (sInstance == null) {
                    sInstance = new ONewsAdProvider_cn();
                }
            }
        }
        return sInstance;
    }

    /**
     * 国内初始化
     */
    public void initAds() {
        if(L.DEBUG) L.ad("ads init cn");
        MarketContext marketContext = new MarketContext(C.getAppContext());
        adListLoader = new NativeAdListLoader(marketContext,POSID, MIN_SIZE);
        adListLoader.loadAds(PRE_NUM);
    }

    /**
     * 国内广告
     * @return
     */
    @Nullable
    public IONewsAd getNewsAd(ONewsScenario scenario) {
        if(null == adListLoader){
            return null;
        }

        if(null == scenario){
            return null;
        }

        INativeAd iNativeAd = adListLoader.getAd();
        if(null == iNativeAd){
            return null;
        }else {
            return new ONewsAd(iNativeAd);
        }
    }
}
