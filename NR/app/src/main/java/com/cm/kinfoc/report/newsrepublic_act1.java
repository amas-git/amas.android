package com.cm.kinfoc.report;

import com.cm.kinfoc.BaseTracer;




public class newsrepublic_act1 extends BaseTracer {




    public newsrepublic_act1() {
        super("newsrepublic_act1");
    }


    @Override
    public void reset() {
        listid(0);
        loadtype(0);
    }


    public newsrepublic_act1 listid(int listid) {
        set("listid", listid);
        return this;
    }


    public newsrepublic_act1 loadtype(int loadtype) {
        set("loadtype", loadtype);
        return this;
    }


}
