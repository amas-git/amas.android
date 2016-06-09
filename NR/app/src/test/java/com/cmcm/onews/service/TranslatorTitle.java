package com.cmcm.onews.service;

import com.cmcm.onews.event.FireEvent;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.sdk.L;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/**
 * 印度语翻译英语(新闻标题)
 */
public class TranslatorTitle implements Runnable{

    private String mContent;
    private String mContentid;
    private ONewsScenario mScenario;

    public TranslatorTitle(ONewsScenario scenario,String content,String contentid){
        this.mScenario = scenario;
        this.mContent = content;
        this.mContentid = contentid;
    }

    @Override
    public void run() {
        translate();
    }

    private void translate(){
        if(L.DEBUG) L.runnable("translate");
        String _translatedResult = "";
        try {
            _translatedResult = Translate.execute(mContent, Language.HINDI, Language.ENGLISH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FireEvent.FIRE_Event_Translate(_translatedResult, mContentid, mScenario);
    }
}
