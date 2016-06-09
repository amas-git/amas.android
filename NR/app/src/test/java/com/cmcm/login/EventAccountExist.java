package com.cmcm.login;


import com.cmcm.onews.event.ONewsEvent;

/**
 * Created by blue on 14-12-30.
 */
public class EventAccountExist extends ONewsEvent {

    public static final int USERNAME_EXIST = 12005;
    public static final int EMAIL_EXIST = 12006;
    public static final int EMAIL_INVALID = 12000;
    public static final int ACCOUNT_CAN_BE_REGIST = 12018;

    private int mResult = LoginService.LOGIN_EXCEPTION;

    public EventAccountExist(int result){
        mResult = result;
    }

    public int getResultRet(){
        return mResult;
    }

    @Override
    public String toString() {
        return "EventResetPassword\tmResult:\t"+mResult;
    }
}
