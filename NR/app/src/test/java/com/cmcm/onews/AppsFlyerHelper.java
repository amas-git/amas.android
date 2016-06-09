package com.cmcm.onews;

import com.appsflyer.AppsFlyerLib;

/**
 * AppsFlyer 统计通过广告过来安装的用户
 * Created by SAM on 2015/12/21.
 */
public class AppsFlyerHelper {
    private static AppsFlyerHelper mInstance = null;
    private static final String APPSFLYERKEY = "Ga5Y8wyWYUt5CsGqUoiJD5";

    public static final AppsFlyerHelper getInstance(){
        if(mInstance == null){
            mInstance = new AppsFlyerHelper();
        }

        return mInstance;
    }

    public void startTracking(){
        AppsFlyerLib.getInstance().startTracking(MainEntry.getInstance(), APPSFLYERKEY);
        AppsFlyerLib.getInstance().setCurrencyCode("CNY");
        AppsFlyerLib.getInstance().setCollectMACAddress(false);
        AppsFlyerLib.getInstance().setCollectIMEI(false);

        AppsFlyerLib.getInstance().setDebugLog(false);
    }


}
