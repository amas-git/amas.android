package com.cmcm.onews.util.push.mi;

import android.content.Context;
import android.text.TextUtils;

import com.cmcm.onews.C;
import com.cmcm.onews.util.PackageUtils;
import com.cmcm.onews.util.push.comm.AppEnvUtils;
import com.cmcm.onews.util.push.comm.LocationData;
import com.cmcm.onews.util.push.comm.PushLog;
import com.cmcm.onews.util.push.http.HttpManager;
import com.cmcm.onews.util.push.http.HttpMsg;

import java.net.URLEncoder;
import java.util.HashMap;

//import com.ijinshan.base.app.UserHelper;
//import com.ijinshan.base.http.HttpException;
//import com.ijinshan.base.http.HttpManager;
//import com.ijinshan.base.http.HttpMsg;
//import com.ijinshan.base.utils.AppEnvUtils;
//import com.ijinshan.browser.location_weather.LocationData;

/**
 * Created by pc on 2015/12/28.
 */
public class MiPushReportUtils {


    /**
     * •推送到达后 根据客户端行为，进行上报
     *
     * @param context
     * @param action
     * @param pushid
     */
    public static void reportMessageBehavior(
            final Context context,
            final int action, String pushid) {
        if (TextUtils.isEmpty(pushid)) {
            return;
        }
        // 使用GET方法发送请求,需要把参数加在URL后面，用？连接，参数之间用&分隔

        StringBuffer parameters = new StringBuffer();
        parameters.append("pushid=").append(pushid);
        parameters.append("&regid=").append(MiPushConfigManager.getInstanse(context).getRegID());
        parameters.append("&aid=").append(AppEnvUtils.GetAndroidID(context));
        parameters.append("&apkversion=").append(PackageUtils.getAppVersionName(C.getAppContext()));
        parameters.append("&action=").append(action);


        HttpMsg msg = new HttpMsg(MiPushConst.REPORT_RPC_URL);
        msg.setListener(new HttpMsg.AbstractHttpMsgListener() {
            @Override
            public void onResponse(int responseCode,
                                   HashMap<String, String> headers,
                                   int responseLength,
                                   final String respData) {
            }
        });
        msg.setReqTextData(parameters.toString());
        msg.setMethod(HttpMsg.Method.POST);
        HttpManager.getInstance().send(msg);
    }


    public static void report4RegID(Context cx, String sRegid, String sOldRegid , final LocationData location) {


        if(TextUtils.isEmpty(sRegid)){
            return;
        }

        boolean isChange = isChange(cx, location, sOldRegid, sRegid);
        long nowTime = System.currentTimeMillis() / 1000 ;
        if(!isChange){
            long lastReportTime = MiPushConfigManager.getInstanse(cx).getReportTime(); //sec
            if(nowTime - lastReportTime < 60*60*24){
                return;
            }
        }

        MiPushConfigManager.getInstanse(cx).setReportTime(nowTime);
        //the case of sRegid is null is impossible. so what ever soldRegid is, there will report to server.
        String sParamString = MiPushDeviceParams.getReportPushRegIDParam(cx);
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(sRegid)) {
            sb.append("&regid=").append(sRegid);
//                    MiLog.getLogInstance().log("mi report to server reg id:"+sRegid);
        }
        if (!TextUtils.isEmpty(sOldRegid) && !sOldRegid.equals(sRegid)) {
            sb.append("&oregid=").append(sOldRegid);
//                    MiLog.getLogInstance().log("mi report to server old reg id:"+sOldRegid);
        }
        sb.append("&regtime=").append(System.currentTimeMillis() / 1000);

        if(location!=null){
            try {
                sb.append("&s=").append(URLEncoder.encode(location.getProvince(), "utf-8"));// 省
                sb.append("&c=").append(URLEncoder.encode(location.getCity(), "utf-8"));//  城市
                sb.append("&x=").append(URLEncoder.encode(location.getCounty(), "utf-8"));// 县
                if(!TextUtils.isEmpty(location.getCityCode())){
                    sb.append("&cc=").append(URLEncoder.encode(location.getCityCode(), "utf-8"));//城市代码
                    MiPushConfigManager.getInstanse(cx).setCityCode(location.getCityCode());
                }
            }catch (Exception e){}
        }
        sParamString = sParamString + sb.toString();
        PushLog.log(sParamString);
        HttpMsg msg = new HttpMsg(MiPushConst.REGIST_RPC_URL);
        msg.setListener(new HttpMsg.AbstractHttpMsgListener() {
            @Override
            public void onResponse(int responseCode,
                                   HashMap<String, String> headers,
                                   int responseLength,
                                   final String respData) {
                PushLog.log("responseCode-------->"+responseCode);
                PushLog.log("respData-------->"+respData);
            }
        });
        msg.setReqTextData(sParamString);
        msg.setMethod(HttpMsg.Method.POST);
        HttpManager.getInstance().send(msg);
    }
    private static boolean isChange(Context cx, final LocationData location, String sOldRegid, String sRegid){
        if(location == null){
            return true ;
        }
        String oldCityCode = MiPushConfigManager.getInstanse(cx).getCityCode();

        if(!TextUtils.isEmpty(sRegid) && sRegid.equals(sOldRegid)
                && location!=null && !TextUtils.isEmpty(location.getCityCode()) && location.getCityCode().equals(oldCityCode))
        {
           return false ;
        }
        return  true;
    }

}
