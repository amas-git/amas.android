package com.cmcm.onews.util.push;

import com.cmcm.onews.sdk.L;

import org.json.JSONObject;

/**
 * Created by cm on 2016/3/14.
 */
public class PushCallbackMgr {
    private static PushCallbackMgr sInstance = null;
    private iPushCallBackProvider mPushCallBackProvider;

    public void init(iPushCallBackProvider pushCallBackProvider){
        mPushCallBackProvider = pushCallBackProvider;
    }

    public static PushCallbackMgr getInstance() {
        if (sInstance == null) {
            synchronized (PushCallbackMgr.class) {
                if (sInstance == null) {
                    sInstance = new PushCallbackMgr();
                }
            }
        }
        return sInstance;
    }

    public void handlePush(JSONObject jsonObject) {
        if (null != mPushCallBackProvider){
            mPushCallBackProvider.handlePush(jsonObject);
        } else {
//            L.push_handle("mPushCallBackProvider is null! please init mPushCallBackProvider");
        }
    }
}
