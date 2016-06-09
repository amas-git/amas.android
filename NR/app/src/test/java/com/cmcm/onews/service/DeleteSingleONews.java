package com.cmcm.onews.service;

import com.cmcm.onews.model.ONews;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.storage.ONewsProviderManager;

public class DeleteSingleONews implements Runnable{

    private ONews news;
    private ONewsScenario scenario;

    public DeleteSingleONews(ONews news, ONewsScenario scenario){
        this.news = news;
        this.scenario = scenario;
    }

    @Override
    public void run() {
        ONewsProviderManager.getInstance().deleteSingle(news, scenario);
    }
}
