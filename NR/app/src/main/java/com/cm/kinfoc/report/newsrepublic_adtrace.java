package com.cm.kinfoc.report;

import com.cm.kinfoc.BaseTracer;




public class newsrepublic_adtrace extends BaseTracer {
    public newsrepublic_adtrace() {
        super("newsrepublic_adtrace");
    }


    @Override
    public void reset() {
        status(0);
        network("");
        pos(0);
    }


    public newsrepublic_adtrace status(int status) {
        set("status", status);
        return this;
    }


    public newsrepublic_adtrace network(String network) {
        set("network", network);
        return this;
    }


    public newsrepublic_adtrace pos(int pos) {
        set("pos", pos);
        return this;
    }


}
