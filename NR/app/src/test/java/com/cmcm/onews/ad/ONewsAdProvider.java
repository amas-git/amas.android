package com.cmcm.onews.ad;

import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.ConflictCommons;
import com.picksinit.PicksConfig;
import com.picksinit.PicksMob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ONewsAdProvider implements IONewsAdProvider{
    //    public static final String TEST_MID = "1094";
//    private static final String TEST_POSID = "1094100";
    private static ONewsAdProvider sInstance = null;

    private ONewsAdProvider(){

    }

    public static ONewsAdProvider getInstance() {
        if (sInstance == null) {
            synchronized (ONewsAdProvider.class) {
                if (sInstance == null) {
                    sInstance = new ONewsAdProvider();
                }
            }
        }
        return sInstance;
    }

    public void init(){
        if(ConflictCommons.isCNVersion()){
            ONewsAdProvider_cn.getInstance().initAds();
        }else {
            ONewsAdProvider_in.getInstance().initAds();
        }
    }

    @Override
    public IONewsAd getINativeAd(ONewsScenario scenario) {
        if(ConflictCommons.isCNVersion()){
            return ONewsAdProvider_cn.getInstance().getNewsAd(scenario);
        }else {
            return ONewsAdProvider_in.getInstance().getNewsAd(scenario);
        }
    }

    /**
     * todo 保证70003类型不被picks sdk过滤
     */
    public static void updatePicksConfigForInterstatial(){
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> configMap = new HashMap<String, Object>();
                Set<Integer> configSet = new HashSet<Integer>();
                Set<Integer> filter = PicksMob.getInstance().getConfig().filter_show_type;
                if (filter != null) {
                    configSet.addAll(filter);
                }
                configSet.add(70003);
                configSet.add(70002);
                configMap.put(PicksConfig.KEY_SHOW_TYPE, configSet);
                PicksMob.getInstance().updateConfig(configMap);
            }
        });
    }

    public static String MID() {
        if(ConflictCommons.isCNVersion()){
            return ONewsAdProvider_cn.MID;
        }else {
            return ONewsAdProvider_in.MID;
        }
    }
}
