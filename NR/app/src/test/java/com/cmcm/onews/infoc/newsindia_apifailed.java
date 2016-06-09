package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;
import com.cmcm.onews.util.ConflictCommons;

/**
 * CREATE FROM: newsindia_apifailed:132 uptime2:int url:string message:string time1:int net:string
 */
public class newsindia_apifailed extends BaseTracer {

    private static String sSuperName = ConflictCommons.isCNVersion() ? "newscn_apifailed" : "newsindia_apifailed";

    public newsindia_apifailed() {
        super(sSuperName);
    }

    @Override
    public void reset() {
      url("");
      message("");
      time1(0);
      net("");
    }


   /**
    * 请求url
    */
    public newsindia_apifailed url(String url) {
        set("url", url);
        return this;
    }

   /**
    * 失败原因
    */
    public newsindia_apifailed message(String message) {
        if(message != null) {
            int limit = 250;
            if (message.length() > limit) {
                message = message.substring(0, limit);
            }
            set("message", message);
        }
        return this;
    }

   /**
    * 执行时间
    */
    public newsindia_apifailed time1(int time1) {
        set("time1", time1);
        return this;
    }

   /**
    * 网络wifi/2G/3G/4G/5G
    */
    public newsindia_apifailed net(String net) {
        set("net", net);
        return this;
    }
}

