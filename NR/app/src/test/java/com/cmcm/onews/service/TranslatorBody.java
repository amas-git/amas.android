package com.cmcm.onews.service;

import com.cmcm.onews.event.FireEvent;
import com.cmcm.onews.sdk.L;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/**
 * 英语翻译印度语(新闻 body)
 */
public class TranslatorBody implements  Runnable{

    private String mBody;

    public TranslatorBody(String content){
        this.mBody = content;
    }

    @Override
    public void run() {
        translate();
    }

    private void translate(){
        if(L.DEBUG) L.runnable("translate to hindi ");
        String _translatedResult = "";
        try {
            _translatedResult = Translate.execute(mBody,Language.ENGLISH,Language.HINDI);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FireEvent.FIRE_Event_TranslateBody(_translatedResult);
    }
}
