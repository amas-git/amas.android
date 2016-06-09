package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;
import com.cmcm.onews.util.ConflictCommons;

/**
 * CREATE FROM: newsindia_apiperformance:131 uptime2:int url:string net:string time1:int
 */
public class newsindia_apiperformance extends BaseTracer {

    private static String sSuperName = ConflictCommons.isCNVersion() ? "newscn_apiperformance" : "newsindia_apiperformance";

    public newsindia_apiperformance() {
        super(sSuperName);
    }

    @Override
    public void reset() {
      url("");
      net("");
      time1(0);
    }


   /**
    * 请求URL
    */
    public newsindia_apiperformance url(String url) {
        set("url", url);
        return this;
    }

   /**
    * wifi/2G/3G/4G/5G
    */
    public newsindia_apiperformance net(String net) {
        set("net", net);
        return this;
    }

   /**
    * 执行时间
    */
    public newsindia_apiperformance time1(int time1) {
        set("time1", time1);
        return this;
    }
}
