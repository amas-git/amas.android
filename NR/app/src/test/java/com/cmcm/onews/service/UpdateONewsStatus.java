package com.cmcm.onews.service;

import android.text.TextUtils;

import com.cmcm.onews.event.FireEvent;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.storage.ONewsProviderManager;

/**
 * 更新新闻阅读状态
 */
public class UpdateONewsStatus implements Runnable{
    private ONewsScenario mScenario;
    private String mContentid;

    public UpdateONewsStatus(ONewsScenario scenario,String contentid){
        this.mScenario = scenario;
        this.mContentid = contentid;
    }

    @Override
    public void run() {
        update_onews_status();
    }

    private void update_onews_status() {
        if(L.DEBUG) L.runnable("update_onews_status");
        if(TextUtils.isEmpty(mContentid) || null == mScenario){
            return;
        }
        ONewsProviderManager.getInstance().updateONewsRead(mScenario,mContentid);
        FireEvent.FIRE_EventNewsRead(mScenario,mContentid);
    }
}
