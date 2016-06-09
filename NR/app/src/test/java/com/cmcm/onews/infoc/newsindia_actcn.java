package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;

/**
 * CREATE FROM: newsindia_actcn:127 uptime2:int refer:int
 */
public class newsindia_actcn extends BaseTracer {
    public newsindia_actcn() {
        super("newsindia_actcn");
    }

    @Override
    public void reset() {
      refer(0);
    }


   /**
    * 收到广播之后的渠道号
    */
    public newsindia_actcn refer(int refer) {
        set("refer", refer);
        return this;
    }
}
