package com.cmcm.onews.infoc;

import com.cm.kinfoc.BaseTracer;
import com.cmcm.onews.util.ConflictCommons;

/**
 * CREATE FROM: newsindia_interest:136 uptime2:int ifclick:byte clicknum:int interesttype:int action:byte
 */
public class newsindia_interest extends BaseTracer {

    private static String sSuperName = ConflictCommons.isCNVersion() ? "newscn_interest" : "newsindia_interest";

    public newsindia_interest() {
        super(sSuperName);
    }

    @Override
    public void reset() {
        ifclick(0);
        clicknum(0);
        interesttype(0);
        action(0);
    }


    /**
     * 是否点击了选择1.是2否
     */
    public newsindia_interest ifclick(int ifclick) {
        set("ifclick", ifclick);
        return this;
    }

    /**
     * 选择个数
     */
    public newsindia_interest clicknum(int clicknum) {
        set("clicknum", clicknum);
        return this;
    }

    /**
     * 选择了哪些类别1-8位上报
     */
    public newsindia_interest interesttype(int interesttype) {
        set("interesttype", interesttype);
        return this;
    }

    /**
     * 操作 1-正常进入 2-skip 3-退出
     */
    public newsindia_interest action(int action) {
        set("action", action);
        return this;
    }
}
