package com.cmcm.login;

import com.cmcm.onews.event.ONewsEvent;


/**
 * Created by blue on 14-12-30.
 */
public class EventLoginNickname extends ONewsEvent {

    private int mResult = LoginService.LOGIN_EXCEPTION;

    public EventLoginNickname(){
    }

    public void setResult(int result){
        mResult = result;
    }

    public boolean isSuccess(){
        return mResult == LoginService.LOGIN_RET_SUCCESS;
    }

    @Override
    public String toString() {
        return "EventLoginNickname\tmResult:\t"+mResult;
    }

}
