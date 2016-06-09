package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;

/**
 * CREATE FROM: newsindia_gcm:150 uptime2:int64 successful:byte
 */
public class newsindia_gcm extends BaseTracer {

    public static int GCM_SUCCESS = 1;
    public static int GCM_FAIL    = 2;

    public newsindia_gcm() {
        super("newsindia_gcm");
    }

    @Override
    public void reset() {
      successful(0);
    }


   /**
    * int : 1 success 2:fail
    */
    public newsindia_gcm successful(int successful) {
        set("successful", successful);
        return this;
    }
}
