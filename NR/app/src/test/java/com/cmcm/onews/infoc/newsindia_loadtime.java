package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;
import com.cmcm.onews.util.NetworkUtil;

/**
 * CREATE FROM: newsindia_loadtime:160 uptime2:int listime:int loadingtime:int network:byte textonly:byte ifweb:byte
 */
public class newsindia_loadtime extends BaseTracer {
    public newsindia_loadtime() {
        super("newsindia_loadtime");
    }

    @Override
    public void reset() {
        listime(0);
        loadingtime(0);
        network(0);
        textonly(0);
        ifweb(0);
    }


    /**
     * 列表载入时间ms
     */
    public newsindia_loadtime listime(int listime) {
        set("listime", listime);
        return this;
    }

    /**
     * 详情页载入时间ms
     */
    public newsindia_loadtime loadingtime(int loadingtime) {
        set("loadingtime", loadingtime);
        return this;
    }

    /**
     * 网络类别1 2G2 3G3 4G4 WIFI
     */
    public newsindia_loadtime network(int network) {
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

    /**
     * 模式1有图2无图
     */
    public newsindia_loadtime textonly(int textonly) {
        set("textonly", textonly);
        return this;
    }

    /**
     * 详情页样式：1：外链，2：正常客户端native
     */
    public newsindia_loadtime ifweb(int ifweb) {
        set("ifweb", ifweb);
        return this;
    }
}

