package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;
/**
 * newsindia_voice:171 uptime2:int pubid:string action:byte
 */
public class newsindia_voice extends BaseTracer {
    private static String sSuperName = "newsindia_voice";

    public newsindia_voice() {
        super(sSuperName);
    }

    @Override
    public void reset() {
        pubid("");
        action(0);
    }

    /**
     * 公众号id
     */
    public newsindia_voice pubid(String pubid) {
        set("pubid", pubid);
        return this;
    }

    /**
     * 1.点加入公众号 2. 点新闻
     */
    public newsindia_voice action(int action) {
        set("action", action);
        return this;
    }
}
