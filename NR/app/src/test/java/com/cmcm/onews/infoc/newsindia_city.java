package com.cmcm.onews.infoc;


import com.cmcm.onews.sdk.NewsSdk;

public class newsindia_city extends act {


    private static String sSuperName = NewsSdk.INSTAMCE.isCNVersion() ? "newscn_city":"newsindia_city";


    public newsindia_city() {
        super(sSuperName);
    }


    @Override
    public void reset() {
        action((byte)0);
        citya(0);
        citym(0);
    }


    public newsindia_city action(byte action) {
        set("action", action);
        return this;
    }


    public newsindia_city citya(int citya) {
        set("citya", citya);
        return this;
    }


    public newsindia_city citym(int citym) {
        set("citym", citym);
        return this;
    }


}
