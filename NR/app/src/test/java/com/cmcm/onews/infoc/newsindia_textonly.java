package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;
import com.cmcm.onews.util.NetworkUtil;

/**
 * CREATE FROM: newsindia_textonly:114 uptime2:int action:byte textonly:byte network:byte
 */
public class newsindia_textonly extends BaseTracer {
    public newsindia_textonly() {
        super("newsindia_textonly");
    }

    @Override
    public void reset() {
        action(0);
        textonly(0);
        network(0);
    }


    /**
     * 是否切换模式
     */
    public newsindia_textonly action(int action) {
        set("action", action);
        return this;
    }

    /**
     * 当前模式1有图2无图
     */
    public newsindia_textonly textonly(int textonly) {
        set("textonly", textonly);
        return this;
    }

    /**
     * 网络类别1 2G2 3G3 4G4 WIFI
     */
    public newsindia_textonly network(int network) {
        if(network == NetworkUtil.NETWORK_TYPE_UNKNOW){
            set("network", 1);
        } else if(network == NetworkUtil.NETWORK_TYPE_3G){
            set("network", 2);
        } else if(network == NetworkUtil.NETWORK_TYPE_WIFI){
            set("network", 4);
        }else {
            set("network", 0);
        }
        return this;
    }
}

