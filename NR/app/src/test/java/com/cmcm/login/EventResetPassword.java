package com.cmcm.login;

import com.cmcm.onews.event.ONewsEvent;


/**
 * Created by blue on 14-12-30.
 */
public class EventResetPassword extends ONewsEvent {

    private int mResult = LoginService.LOGIN_EXCEPTION;

    public EventResetPassword(int result){
        mResult = result;
    }

    public boolean isSuccess(){
        return mResult == LoginService.LOGIN_RET_SUCCESS;
    }

    public int getResultRet(){
        return mResult;
    }

    @Override
    public String toString() {
        return "EventResetPassword\tmResult:\t"+mResult;
    }
}
