package com.cmcm.onews.infoc;


import com.cm.kinfoc.BaseTracer;

/**
 * CREATE FROM: newsindia_net:174 uptime2:int nettype:string creattime:string
 */
public class newsindia_net extends BaseTracer {

    public newsindia_net() {
        super("newsindia_net");
    }

    @Override
    public void reset() {
        nettype("");
        creattime("");
    }

    public newsindia_net nettype(String nettype) {
        set("nettype", nettype);
        return this;
    }

    public newsindia_net creattime(String creattime) {
        set("creattime", creattime);
        return this;
    }

}
