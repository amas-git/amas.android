package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;

/**
 * CREATE FROM: newsindia_language:147 uptime2:int action:byte
 */
public class newsindia_language extends BaseTracer {
    public newsindia_language() {
        super("newsindia_language");
    }

    @Override
    public void reset() {
      action(0);
    }


   /**
    * 1英语2印地语3其他操作进入4退出
    */
    public newsindia_language action(int action) {
        set("action", action);
        return this;
    }
}
