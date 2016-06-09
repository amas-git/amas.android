package com.cmcm.login;

import com.cmcm.onews.event.ONewsEvent;


/**
 * Created by blue on 14-12-30.
 */
public class EventLoginUpdate extends ONewsEvent {

    public static final int LOGIN_UPDATE_CLENA_USER_INFO = 10;
    public static final int LOGIN_UPDATE_CMB_USER_INFO = 11;

    private int mType = 0;
    private int mRaw = 0;

    public EventLoginUpdate(int type,int raw){
        mType = type;
        mRaw = raw;
    }

    public int getRaw(){
        return mRaw;
    }

    public int getType(){
        return mType;
    }

    @Override
    public String toString() {
        return "EventLoginUpdate\tmType:\t"+mType+"\tmRaw:\t"+mRaw;
    }
}
