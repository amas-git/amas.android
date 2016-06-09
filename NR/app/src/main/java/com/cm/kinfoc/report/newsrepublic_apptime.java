package com.cm.kinfoc.report;

import com.cm.kinfoc.BaseTracer;


public class newsrepublic_apptime extends BaseTracer {

    public newsrepublic_apptime() {
        super("newsrepublic_apptime");
    }


    @Override
    public void reset() {
        duration(0);
        network(0);
        textonly(0);
        feature(0);
        source1(0);
        ifoffline(0);
    }


    public newsrepublic_apptime duration(int duration) {
        set("duration", duration);
        return this;
    }


    public newsrepublic_apptime network(int network) {
        set("network", network);
        return this;
    }


    public newsrepublic_apptime textonly(int textonly) {
        set("textonly", textonly);
        return this;
    }


    public newsrepublic_apptime feature(int feature) {
        set("feature", feature);
        return this;
    }


    public newsrepublic_apptime source1(int source1) {
        set("source1", source1);
        return this;
    }


    public newsrepublic_apptime ifoffline(int ifoffline) {
        set("ifoffline", ifoffline);
        return this;
    }


}
