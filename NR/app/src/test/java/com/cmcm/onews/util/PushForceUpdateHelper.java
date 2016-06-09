package com.cmcm.onews.util;

import android.util.Log;

import com.cmcm.onews.util.push.PushCallbackMgr;
import com.cmcm.onews.util.push.iPushCallBackProvider;
import com.cmcm.splash.SplashHelper;
import com.ijinshan.cloudconfig.deepcloudconfig.PullCloudConfig;

import org.json.JSONObject;

/**
 * Created by Jason.Su on 2016/3/14.
 * com.cmcm.onews.util.push
 * des:
 * email:suyanjiao@conew.com
 * 1553202383
 */
public class PushForceUpdateHelper {
    private PushForceUpdateHelper(){

    }
    private final static class SingletonHolder {
        static PushForceUpdateHelper sIns = new PushForceUpdateHelper();
    }
    public static PushForceUpdateHelper getIns(){
        return SingletonHolder.sIns;
    }

    public void init(){
        PushCallbackMgr.getInstance().init(new iPushCallBackProvider() {
            @Override
            public void handlePush(JSONObject jsonObject) {
                // 暂时不用分case ,以后解决吧

                // 开始预加载
                Log.e("suj","通过push的消息来了");
                PullCloudConfig.getInstance().getConfig();

            }
        });



    }

}
