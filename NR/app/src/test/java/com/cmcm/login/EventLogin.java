package com.cmcm.login;

import com.cleanmaster.sdk.cmloginsdkjar.model.CmRawObject;
import com.cmcm.onews.event.ONewsEvent;


/**
 * Created by blue on 14-12-24.
 */
public class EventLogin extends ONewsEvent {

    private int mLoginRaw;
    private int mLoginType;
    private String mFrom = "";
    private int mSouce = 0;

    public int getSouce() {
        return mSouce;
    }

    public void setSouce(int souce) {
        mSouce = souce;
    }

    public EventLogin(int loginRaw,String from){
        mLoginRaw = loginRaw;
        mFrom = from;
    }

    public void setLoginType(int type){
        mLoginType = type;
    }

    public int getLoginRaw(){
        return mLoginRaw;
    }

    public boolean isLoginSuccess(){
        return mLoginRaw == LoginService.LOGIN_RET_SUCCESS;
    }

    public int getLoginType() {
        return mLoginType;
    }

    public String getFrom(){
        return mFrom;
    }

    @Override
    public String toString() {
        return "EventLogin\tmLoginRaw:\t"+mLoginRaw+"\tmLoginType:\t"+mLoginType+"\tmFrom:\t"+mFrom;
    }
}
