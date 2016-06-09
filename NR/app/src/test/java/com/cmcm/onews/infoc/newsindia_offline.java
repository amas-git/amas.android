package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;
import com.cmcm.onews.util.NetworkUtil;

/**
 * CREATE FROM: newsindia_offline:111 uptime2:int size:int num:short clicknum:int fail:byte network:byte
 */
public class newsindia_offline extends BaseTracer {
    public newsindia_offline() {
        super("newsindia_offline");
    }

    @Override
    public void reset() {
        size(0);
        num(0);
        clicknum(0);
        fail(0);
        network(0);
    }


    /**
     * 下载大小
     */
    public newsindia_offline size(int size) {
        set("size", size);
        return this;
    }

    /**
     * 下载条数
     */
    public newsindia_offline num(int num) {
        set("num", num);
        return this;
    }

    /**
     * 点击下载次数
     */
    public newsindia_offline clicknum(int clicknum) {
        set("clicknum", clicknum);
        return this;
    }

    /**
     * 1成功2以后数值代表失败原因
     */
    public newsindia_offline fail(int fail) {
        set("fail", fail);
        return this;
    }

    /**
     * 当前网络网络类别1 2G2 3G3 4G4 WIFI
     */
    public newsindia_offline network(int network) {
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
