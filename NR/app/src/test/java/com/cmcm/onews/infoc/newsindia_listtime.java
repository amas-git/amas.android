package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;


/**
 * CREATE FROM: newsindia_listtime:128 uptime2:int duration:int listid:int listnum:int refresh:short loadnum:short bookmark:short
 */
public class newsindia_listtime extends BaseTracer {
    public newsindia_listtime() {
        super("newsindia_listtime");
    }

    @Override
    public void reset() {
        duration(0);
        listid(0);
        listnum(0);
        refresh(0);
        loadnum(0);
        bookmark(0);
    }


    /**
     * 列表页停留时长s
     */
    public newsindia_listtime duration(int duration) {
        set("duration", duration);
        return this;
    }

    /**
     * 新闻类别
     */
    public newsindia_listtime listid(int listid) {
        set("listid", listid);
        return this;
    }

    /**
     * 新闻展现条数
     */
    public newsindia_listtime listnum(int listnum) {
        set("listnum", listnum);
        return this;
    }

    /**
     * 下拉 刷新次数
     */
    public newsindia_listtime refresh(int refresh) {
        set("refresh", refresh);
        return this;
    }

    /**
     * 上滑加载次数
     */
    public newsindia_listtime loadnum(int loadnum) {
        set("loadnum", loadnum);
        return this;
    }

    /**
     * 点击收藏次数
     */
    public newsindia_listtime bookmark(int bookmark) {
        set("bookmark", bookmark);
        return this;
    }
}
