package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;

/**
 * CREATE FROM: newsindia_clean:115 uptime2:int size:int click:byte
 */
public class newsindia_clean extends BaseTracer {
    public newsindia_clean() {
        super("newsindia_clean");
    }

    @Override
    public void reset() {
        size(0);
        click(0);
    }


    /**
     * 清楚大小KB
     */
    public newsindia_clean size(int size) {
        set("size", size);
        return this;
    }

    /**
     * 操作类别1清理2取消3空白位置
     */
    public newsindia_clean click(int click) {
        set("click", click);
        return this;
    }
}
