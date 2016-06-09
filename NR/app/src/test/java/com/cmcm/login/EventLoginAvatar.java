package com.cmcm.login;

import com.cmcm.onews.event.ONewsEvent;


/**
 * Created by blue on 14-12-30.
 */
public class EventLoginAvatar extends ONewsEvent {

    private int mResult = LoginService.LOGIN_EXCEPTION;
    private String mAvatarUrl = "";

    public EventLoginAvatar(){
    }

    public EventLoginAvatar(int result,String avatarUrl){
        mResult = result;
        mAvatarUrl = avatarUrl;
    }

    public boolean isSuccess(){
        return mResult == LoginService.LOGIN_RET_SUCCESS;
    }

    public void setResultRet(int ret){
        mResult = ret;
    }

    public void setAvatarUrl(String url){
        mAvatarUrl = url;
    }

    public int getResultRet(){
        return mResult;
    }

    public String getAvatarUrl(){
        return mAvatarUrl;
    }

    @Override
    public String toString() {
        return "EventLoginAvatar\tmResult:\t"+mResult+"\tmAvatarUrl:\t"+mAvatarUrl;
    }

}
