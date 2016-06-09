package com.cmcm.login;

import com.cmcm.onews.event.ONewsEvent;


/**
 * Created by blue on 15-1-29.
 */
public class EventFreshUserInfo extends ONewsEvent {

    private long mRaw = -1;

    public EventFreshUserInfo(int raw){
        mRaw = raw;
    }

    public long getRaw(){
        return mRaw;
    }

    @Override
    public String toString() {
        return "EventFreshUserInfo\tmRaw:\t"+mRaw;
    }

    public boolean isSuccess(){
        return mRaw == LoginService.LOGIN_RET_SUCCESS;
    }
}
