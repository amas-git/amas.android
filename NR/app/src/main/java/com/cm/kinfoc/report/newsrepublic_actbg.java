package com.cm.kinfoc.report;

import com.cm.kinfoc.BaseTracer;




public class newsrepublic_actbg extends BaseTracer {

    public newsrepublic_actbg() {
        super("newsrepublic_actbg");
    }


    @Override
    public void reset() {
        installtype("");
        ram(0);
        rom(0);
    }


    public newsrepublic_actbg installtype(String installtype) {
        set("installtype", installtype);
        return this;
    }


    public newsrepublic_actbg ram(int ram) {
        set("ram", ram);
        return this;
    }


    public newsrepublic_actbg rom(int rom) {
        set("rom", rom);
        return this;
    }


}
