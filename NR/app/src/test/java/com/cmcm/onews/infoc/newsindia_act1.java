package com.cmcm.onews.infoc;

import com.cmcm.onews.util.ConflictCommons;

/**
 * 主界面
 * CREATE FROM: newsindia_act1:101 uptime2:int listid:int loadtype:byte
 */
public class newsindia_act1 extends act {

    private static String sSuperName = ConflictCommons.isCNVersion() ? "newscn_act1" : "newsindia_act1";

    public newsindia_act1() {
        super(sSuperName);
    }

    @Override
    public void reset() {
        listid(0);
        loadtype(0);
    }


    /**
     * 新闻分类
     */
    public newsindia_act1 listid(int listid) {
        set("listid", listid);
        return this;
    }

    /**
     * 1 点击2 滑动3 启动时默认（Hot）4 通知栏
     */
    public newsindia_act1 loadtype(int loadtype) {
        set("loadtype", loadtype);
        return this;
    }
}

