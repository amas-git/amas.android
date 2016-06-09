package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;

/**
 * CREATE FROM: newsindia_login:170 uptime2:int stage:byte ifsuccessful:byte page:byte
 */
public class newsindia_login extends BaseTracer {
    public newsindia_login() {
        super("newsindia_login");
    }

    @Override
    public void reset() {
      stage(0);
      ifsuccessful(0);
      page(0);
    }


   /**
    *
    */
    public newsindia_login stage(int stage) {
        set("stage", stage);
        return this;
    }

   /**
    *
    */
    public newsindia_login ifsuccessful(int ifsuccessful) {
        set("ifsuccessful", ifsuccessful);
        return this;
    }

   /**
    *
    */
    public newsindia_login page(int page) {
        set("page", page);
        return this;
    }
}
