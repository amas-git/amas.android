package com.cm.kinfoc.report;

import com.cm.kinfoc.BaseTracer;




public class newsrepublic_push extends BaseTracer {




    public newsrepublic_push() {
        super("newsrepublic_push");
    }


    @Override
    public void reset() {
        newsid(0);
        pushid(0);
        action(0);
        showtype(0);
    }


    public newsrepublic_push newsid(int newsid) {
        set("newsid", newsid);
        return this;
    }


    public newsrepublic_push pushid(int pushid) {
        set("pushid", pushid);
        return this;
    }


    public newsrepublic_push action(int action) {
        set("action", action);
        return this;
    }


    public newsrepublic_push showtype(int showtype) {
        set("showtype", showtype);
        return this;
    }


}
