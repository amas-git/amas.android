package com.cm.kinfoc.report;

import com.cm.kinfoc.BaseTracer;




public class newsrepublic_readnum extends BaseTracer {



    public newsrepublic_readnum() {
        super("newsrepublic_readnum");
    }


    @Override
    public void reset() {
        readnum(0);
        view(0);
        source(0);
        ifoffline(0);
    }


    public newsrepublic_readnum readnum(int readnum) {
        set("readnum", readnum);
        return this;
    }


    public newsrepublic_readnum view(int view) {
        set("view", view);
        return this;
    }


    public newsrepublic_readnum source(int source) {
        set("source", source);
        return this;
    }


    public newsrepublic_readnum ifoffline(int ifoffline) {
        set("ifoffline", ifoffline);
        return this;
    }


}
