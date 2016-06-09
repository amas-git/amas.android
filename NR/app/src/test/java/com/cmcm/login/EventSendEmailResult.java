package com.cmcm.login;

import com.cmcm.onews.event.ONewsEvent;


/**
 * Created by blue on 15-1-29.
 */
public class EventSendEmailResult extends ONewsEvent {

    private long mRaw = -1;

    public EventSendEmailResult(int raw){
        mRaw = raw;
    }

    public long getRaw(){
        return mRaw;
    }

    @Override
    public String toString() {
        return "EventSendEmailResult\tmRaw:\t"+mRaw;
    }
}
