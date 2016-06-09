package com.cmcm.onews.util.push.mi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.cmcm.onews.util.push.HandlePushIntentService;
import com.cmcm.onews.util.push.comm.AppEnvUtils;
import com.cmcm.onews.util.push.comm.PushLog;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

//import com.cmcm.onews.util.push.comm.AppEnvUtils;

/**
 * 1、PushMessageReceiver是个抽象类，该类继承了BroadcastReceiver。
 * 2、需要将自定义的DemoMessageReceiver注册在AndroidManifest.xml文件中 <receiver
 * android:exported="true"
 * android:name="com.xiaomi.mipushdemo.DemoMessageReceiver"> <intent-filter>
 * <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" /> </intent-filter>
 * <intent-filter> <action android:name="com.xiaomi.mipush.ERROR" />
 * </intent-filter> <intent-filter>
 *  <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" /></intent-filter>
 *  </receiver>
 * 3、DemoMessageReceiver的onReceivePassThroughMessage方法用来接收服务器向客户端发送的透传消息
 * 4、DemoMessageReceiver的onNotificationMessageClicked方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发
 * 5、DemoMessageReceiver的onNotificationMessageArrived方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数
 * 6、DemoMessageReceiver的onCommandResult方法用来接收客户端向服务器发送命令后的响应结果
 * 7、DemoMessageReceiver的onReceiveRegisterResult方法用来接收客户端向服务器发送注册命令后的响应结果
 * 8、以上这些方法运行在非UI线程中
 * 
 * @author mayixiang
 */
public class MiPushMessageReceiver extends PushMessageReceiver {

    private static final String SUBSCRIBE_TOPIC = "all_new";
    private static final String UNSUBSCRIBE_TOPIC = "all";
    private static final String UNSUBSCRIBE_TOPIC_CN_ALL = "insta_all_cn";

    private Context mContext ;

    public MiPushMessageReceiver() {

    }
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message){

        mContext = context ;
//        String msg = "{\"extra\":{\"category\":1,\"url\":\"http://apns.ios.ijinshan.com/\"},\"type\":\"2\",\"combine\":3,\"title\":\"消息标题test\",\"content\":\"消息内容test\",\"icon_url\":\"\",\"priority\":\"2\",\"is_show_in_statusbar\":1,\"is_shown_on_lockscreen\":\"1\",\"taskid\":1450927809}";
        if(message!=null && !TextUtils.isEmpty(message.getContent())){
            PushLog.log(
                    "onReceivePassThroughMessage is called. " + message.getContent());
            final ClientMessage clientMessage = new ClientMessage(message.getContent(),0,0);
//            final ClientMessage clientMessage = new ClientMessage(msg,0,0);
            onReceivedMessage(clientMessage);
        }


    }

    private void onReceivedMessage(ClientMessage item) {
        if(item == null || TextUtils.isEmpty(item.getMsg())){
           return;
        }
        if(mContext!=null){

            Intent intent = new Intent(mContext,HandlePushIntentService.class);
            Bundle bundle = new Bundle();
            bundle.putString(MiPushConst.EXTENDED_MSG,item.getMsg());
            intent.putExtras(bundle);
            mContext.startService(intent);
        }
    }
    @Override








    public void onNotificationMessageClicked(Context context, MiPushMessage message){
        mContext = context ;
    }
    
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message){
        mContext = context ;
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        mContext = context ;
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        PushLog.log("mRegId->" + cmdArg1) ;
//        String mRegId = MiPushConfigManager.getInstanse(context).getRegID();
//        String oldRegId = MiPushConfigManager.getInstanse(context).getOldRegID();
//        MiPushReportUtils.report4RegID(context, mRegId, oldRegId, null);
//        MiPushClient.setAlias(context, AppEnvUtils.GetAndroidID(context), null);
//        PushLog.log("GetAndroidID--------->" + AppEnvUtils.GetAndroidID(context));
//        MiPushClient.subscribe(context ,"all", null);
    }
    
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message){
        mContext = context ;
        PushLog.log(
                "onReceiveRegisterResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        if (MiPushClient.COMMAND_REGISTER.equals(command)  && arguments.size() == 1) {
            String mRegId = arguments.get(0);
            PushLog.log("mRegId->" + mRegId);
            MiPushConfigManager.getInstanse(context).setRegID(mRegId);
            String oldRegId = MiPushConfigManager.getInstanse(context).getOldRegID();

            MiPushReportUtils.report4RegID(context, mRegId, oldRegId, null);
            MiPushClient.setAlias(context, AppEnvUtils.GetAndroidID(context), null);
            PushLog.log("GetAndroidID--------->" + AppEnvUtils.GetAndroidID(context));
            MiPushClient.subscribe(context, SUBSCRIBE_TOPIC, null);
            MiPushClient.unsubscribe(context, UNSUBSCRIBE_TOPIC, null);
            MiPushClient.unsubscribe(context, UNSUBSCRIBE_TOPIC_CN_ALL, null);
        }
    }

}
