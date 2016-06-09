package com.cmcm.onews.util.push.mi;


public class ClientMessage {
    private String msg;
    private long mid;
    private long gid;

    public ClientMessage(String msg, long mid, long gid) {
        this.msg = msg;
        this.mid = mid;
        this.gid = gid;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getMid() {
        return this.mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    public long getGid() {
        return this.gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public boolean isPrivate() {
        return this.gid == 0L;
    }
}
