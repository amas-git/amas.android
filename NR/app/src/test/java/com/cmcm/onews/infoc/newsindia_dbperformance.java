package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;

/**
 * CREATE FROM: newsindia_dbperformance:130 uptime2:int function1:string size:int col_len:byte time1:int
 */
public class newsindia_dbperformance extends BaseTracer {
    public newsindia_dbperformance() {
        super("newsindia_dbperformance");
    }

    @Override
    public void reset() {
        function1("");
        size(0);
        col_len(0);
        time1(0);
    }


    /**
     * API名字
     */
    public newsindia_dbperformance function1(String function1) {
        set("function1", function1);
        return this;
    }

    /**
     * 加载数据量
     */
    public newsindia_dbperformance size(int size) {
        set("size", size);
        return this;
    }

    /**
     * 数据项条目数
     */
    public newsindia_dbperformance col_len(int col_len) {
        set("col_len", col_len);
        return this;
    }

    /**
     * 消耗时间
     */
    public newsindia_dbperformance time1(int time1) {
        set("time1", time1);
        return this;
    }
}

