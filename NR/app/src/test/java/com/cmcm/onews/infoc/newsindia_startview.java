package com.cmcm.onews.infoc;

/**
 * CREATE FROM: newsindia_startview:163 uptime2:int ctype:string ifclick:byte
 */
public class newsindia_startview extends act {

    public newsindia_startview() {
        super("newsindia_startview");
    }

    @Override
    public void reset() {
        ctype("");
        ifclick(0);
    }

    /**
     * 卡片展示类别：活动的id
     */
    public newsindia_startview ctype(String ctype) {
        set("ctype", ctype);
        return this;
    }

    /**
     * 是否点击跳转：1:点击，2：手动跳过，3：自动跳过
     */
    public newsindia_startview ifclick(int ifclick) {
        set("ifclick", ifclick);
        return this;
    }
}
