package com.cmcm.onews.service;

import com.cmcm.onews.event.EventLive;
import com.cmcm.onews.event.FireEvent;
import com.cmcm.onews.event.ONewsEventManager;
import com.cmcm.onews.model.sports.ONewsMatch;
import com.cmcm.onews.model.sports.ONewsTeam;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.transport.ONewsHttpClient;

import java.util.ArrayList;
import java.util.List;

public class DelayRefreshMatch implements Runnable{
    private ONewsMatch match;
    public DelayRefreshMatch match(ONewsMatch m){
        this.match = m;
        return this;
    }

    @Override
    public void run() {
        if(null != match){
            List<ONewsTeam> teams = match.getTeamsList();
            if(null != teams && teams.size() > 1){
                ONewsMatch  result = ONewsHttpClient.getInstance().requestMatchInfo(match.getId());
                if(null != result && ONewsMatch.MATCH_STATUS_PLAYING == result.getStatus()){
                    ONewsEventManager.getInstance().sendEvent(new EventLive(result));
                    fireRefresh(result);
                    if(L.DEBUG) L.news_item_refresh("********** DelayRefreshMatch MATCH_STATUS_PLAYING ");
                }else if(null != result && ONewsMatch.MATCH_STATUS_NO_START == result.getStatus()){
                    fireRefresh(match);
                    if(L.DEBUG) L.news_item_refresh("********** DelayRefreshMatch MATCH_STATUS_NO_START ");
                }else if(null != result && ONewsMatch.MATCH_STATUS_END == result.getStatus()){
                    ONewsEventManager.getInstance().sendEvent(new EventLive(result));
                    fireRefresh(result);
                    if(L.DEBUG) L.news_item_refresh("********** DelayRefreshMatch MATCH_STATUS_END");
                }else {
                    fireRefresh(match);
                    if(L.DEBUG) L.news_item_refresh("********** DelayRefreshMatch ONewsMatch null");
                }
            }
        }
    }

    private void fireRefresh(ONewsMatch match) {
        List<ONewsMatch> matches = new ArrayList<ONewsMatch>();
        matches.add(match);
        FireEvent.FIRE_EventRefreshMatch(matches);
    }

    public boolean isNull(){
        if(null == this.match){
            return true;
        }

        if(ONewsMatch.MATCH_STATUS_END == this.match.getStatus()){
            return true;
        }

        return false;
    }
}
