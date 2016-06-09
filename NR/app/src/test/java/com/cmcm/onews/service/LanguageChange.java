package com.cmcm.onews.service;

import com.cmcm.onews.C;
import com.cmcm.onews.event.FireEvent;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.util.UIConfigManager;
import com.ijinshan.cloudconfig.deepcloudconfig.PullCloudConfig;

public class LanguageChange implements Runnable{

    @Override
    public void run() {
        language_change();
    }

    /**
     * 切换语言
     */
    private void language_change(){
        if(L.DEBUG) L.runnable("language_change");
        PullCloudConfig.getInstance().getConfig();
        NewsSdk.INSTAMCE.changeNewsLanguage(UIConfigManager.getInstanse(C.getAppContext()).getLanguageSelected(C.getAppContext()).getLanguageWithCountryUnderline());
        FireEvent.FIRE_EventLanguageChange();
    }
}
