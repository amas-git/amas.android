package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;
import com.cmcm.onews.util.ConflictCommons;

/**
 * CREATE FROM: newsindia_net_tracer:151 uptime2:int api_name:string net:string code:byte message:string
 */
public class newsindia_net_tracer extends BaseTracer {

    private static String sSuperName = ConflictCommons.isCNVersion() ? "newscn_net_tracer" : "newsindia_net_tracer";

    public newsindia_net_tracer() {
        super(sSuperName);
    }

    @Override
    public void reset() {
        api_name("");
        net("");
        code(0);
        message("");
    }


    /**
     *
     */
    public newsindia_net_tracer api_name(String api_name) {
        if(api_name!=null) {
            api_name = api_name.replace('?','_');
        }
        set("api_name", api_name);
        return this;
    }

    /**
     *
     */
    public newsindia_net_tracer net(String net) {
        set("net", net);
        return this;
    }

    /**
     *
     */
    public newsindia_net_tracer code(int code) {
        set("code", code);
        return this;
    }

    /**
     *
     */
    public newsindia_net_tracer message(String message) {
        set("message", message);
        return this;
    }
}
