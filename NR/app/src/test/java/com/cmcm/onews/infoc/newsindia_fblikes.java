package com.cmcm.onews.infoc;


import com.cmcm.onews.sdk.NewsSdk;


public class newsindia_fblikes extends act {


    private static String sSuperName = NewsSdk.INSTAMCE.isCNVersion() ? "newscn_fblikes":"newsindia_fblikes";


    public newsindia_fblikes() {
        super(sSuperName);
    }


    @Override
    public void reset() {
        action(0);
    }


    /**
     *
     * @param action 1-点击go  2-点击thanks 3-返回或点击其它区域
     * @return
     */
    public newsindia_fblikes action(int action) {
        set("action", action);
        return this;
    }


}
