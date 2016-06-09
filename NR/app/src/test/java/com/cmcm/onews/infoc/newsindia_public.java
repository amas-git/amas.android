package com.cmcm.onews.infoc;

import android.content.Context;

import com.cm.kinfoc.BaseTracer;
import com.cm.kinfoc.KInfocCommon;
import com.cmcm.onews.C;
import com.cmcm.onews.util.ConflictCommons;
import com.google.analytics.tracking.android.CampaignTrackingReceiver_nr;

/**
 * Created by amas on 11/5/15.
 */
public class newsindia_public extends BaseTracer {
    public newsindia_public() {
        super("newsindia_public");
    }

    @Override
    public void reset() {
        uuid("");
        xaid("");
        ver(0);
        mcc(0);
        mnc(0);
        cl("");
        //cn(CampaignTrackingReceiver_cm.getChannel(MainEntry.getAppContext()));
        cn(CampaignTrackingReceiver_nr.getChannel(C.getAppContext()));
        cn2(CampaignTrackingReceiver_nr.getCN2(C.getAppContext()));
        prodid(ConflictCommons.isCNVersion()?1:2);
        root(0);
        capi(0);
        brand("");
        model("");
        serial("");
        uptime(0);
        loadtype(0);
    }




    private static newsindia_public instance = new newsindia_public();

    public static newsindia_public getInstance(Context context) {
        instance.uuid("");
        instance.ver(getVersionCode(context, context.getPackageName()));
        instance.mcc(KInfocCommon.getMCC(context));
        instance.cl(getCurrentLanguange());
        instance.xaid(getAndroidID(context));
        instance.brand(brand(context));
        instance.model(model(context));
        instance.mnc(KInfocCommon.getMNC(context));
        instance.cn(CampaignTrackingReceiver_nr.getChannel(C.getAppContext()));
        // 是离线模式还是在线模式
        instance.mode(0);
        return instance;
    }

    public newsindia_public mode(int mode) {
        set("mode", mode);
        return this;
    }


    /**
     *
     */
    public newsindia_public uptime(int uptime) {
        set("uptime", uptime);
        return this;
    }


    /**
     *
     */
    public newsindia_public loadtype(int loadtype) {
        set("loadtype", loadtype);
        return this;
    }

    /**
     *
     */
    public newsindia_public uuid(String uuid) {
        set("uuid", uuid);
        return this;
    }

    /**
     *
     */
    public newsindia_public xaid(String xaid) {
        set("xaid", xaid);
        return this;
    }

    /**
     *
     */
    public newsindia_public ver(int ver) {
        set("ver", ver);
        return this;
    }

    /**
     *
     */
    public newsindia_public mcc(int mcc) {
        set("mcc", mcc);
        return this;
    }

    /**
     *
     */
    public newsindia_public mnc(int mnc) {
        set("mnc", mnc);
        return this;
    }

    /**
     *
     */
    public newsindia_public cl(String cl) {
        set("cl", cl);
        return this;
    }

    /**
     *
     */
    public newsindia_public cn(int cn) {
        set("cn", cn);
        return this;
    }

    /**
     *
     */
    public newsindia_public cn2(String cn2) {
        set("cn2", cn2);
        return this;
    }

    /**
     *
     */
    public newsindia_public prodid(int prodid) {
        set("prodid", prodid);
        return this;
    }

    /**
     *
     */
    public newsindia_public root(int root) {
        set("root", root);
        return this;
    }

    /**
     *
     */
    public newsindia_public capi(int capi) {
        set("capi", capi);
        return this;
    }

    /**
     *
     */
    public newsindia_public brand(String brand) {
        set("brand", brand);
        return this;
    }

    /**
     *
     */
    public newsindia_public model(String model) {
        set("model", model);
        return this;
    }

    /**
     *
     */
    public newsindia_public serial(String serial) {
        set("serial", serial);
        return this;
    }
}
