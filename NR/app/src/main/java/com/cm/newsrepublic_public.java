package com.cm;

import android.content.Context;

import com.cm.kinfoc.BaseTracer;

/**
 * Created by amas on 11/5/15.
 */
//newsrepublic_public
// newsrepublic_public:1 uuid:binary xaid:string ver:int mcc:short mnc:short cl:string cn:int cn2:string prodid:int root:byte capi:byte brand:string model:string serial:string uptime:int mode:byte
public class newsrepublic_public extends BaseTracer {
    public newsrepublic_public() {
        super("newsrepublic_public");
    }

    /*
    newsrepublic_public:1
    uuid:binary
    xaid:string
    ver:int
    mcc:short
    mnc:short
    cl:string
    cn:int
    cn2:string
    prodid:int
    root:byte
    capi:byte
    brand:string
    model:string
    serial:string
    uptime:int
    mode:byte
    */

    @Override
    public void reset() {
        uuid("");
        xaid("");
        ver(0);
        mcc(0);
        mnc(0);
        cl("");
        //cn(CampaignTrackingReceiver_cm.getChannel(MainEntry.getAppContext()));
        cn(0);
        cn2("");
        prodid(1);
        root(0);
        capi(0);
        brand("");
        model("");
        serial("");
        uptime(0);
        mode(0);
    }




    private static newsrepublic_public instance = new newsrepublic_public();

    public static newsrepublic_public getInstance(Context context) {
//        instance.uuid("");
//        instance.ver(NewsPublicDataHelper.getInstance().getVersionCode());
//        instance.mcc(NewsPublicDataHelper.getInstance().getMCC());
//        instance.cl(NewsPublicDataHelper.getInstance().getmCurrentLanguage());
//        instance.xaid(NewsPublicDataHelper.getInstance().getAndroidID());
//        instance.brand(NewsPublicDataHelper.getInstance().getDeviceBrand());
//        instance.model(NewsPublicDataHelper.getInstance().getDeviceModel());
//        instance.mnc(NewsPublicDataHelper.getInstance().getMNC());
//        instance.cn(NewsPublicDataHelper.getInstance().getChannel());
//        // 是离线模式还是在线模式
//        instance.mode(0);
        return instance;
    }

    public newsrepublic_public mode(int mode) {
        set("mode", mode);
        return this;
    }


    /**
     *
     */
    public newsrepublic_public uptime(int uptime) {
        set("uptime", uptime);
        return this;
    }

    /**
     *
     */
    public newsrepublic_public uuid(String uuid) {
        set("uuid", uuid);
        return this;
    }

    /**
     *
     */
    public newsrepublic_public xaid(String xaid) {
        set("xaid", xaid);
        return this;
    }

    /**
     *
     */
    public newsrepublic_public ver(int ver) {
        set("ver", ver);
        return this;
    }

    /**
     *
     */
    public newsrepublic_public mcc(int mcc) {
        set("mcc", mcc);
        return this;
    }

    /**
     *
     */
    public newsrepublic_public mnc(int mnc) {
        set("mnc", mnc);
        return this;
    }

    /**
     *
     */
    public newsrepublic_public cl(String cl) {
        set("cl", cl);
        return this;
    }

    /**
     *
     */
    public newsrepublic_public cn(int cn) {
        set("cn", cn);
        return this;
    }

    /**
     *
     */
    public newsrepublic_public cn2(String cn2) {
        set("cn2", cn2);
        return this;
    }

    /**
     *
     */
    public newsrepublic_public prodid(int prodid) {
        set("prodid", prodid);
        return this;
    }

    /**
     *
     */
    public newsrepublic_public root(int root) {
        set("root", root);
        return this;
    }

    /**
     *
     */
    public newsrepublic_public capi(int capi) {
        set("capi", capi);
        return this;
    }

    /**
     *
     */
    public newsrepublic_public brand(String brand) {
        set("brand", brand);
        return this;
    }

    /**
     *
     */
    public newsrepublic_public model(String model) {
        set("model", model);
        return this;
    }

    /**
     *
     */
    public newsrepublic_public serial(String serial) {
        set("serial", serial);
        return this;
    }
}