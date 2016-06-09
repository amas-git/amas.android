package com.cmcm.onews.util.push.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.cmcm.onews.NewsL;
import com.cmcm.onews.util.push.HandlePushIntentService;
import com.cmcm.onews.util.push.comm.PushLog;


/**
 * 负责接受gcm服务器的msg和cmd
 */
public class GCMReceiver extends BroadcastReceiver {

    private static String GCM_REFRESH = "push.googleapis.com/refresh";
    private Context mContext;

    // push parameter section part
    private static String TYPE_TWO_LINE = "11";
    private static final String SECTION_NOTIFY  = "1";
    private static final String SECTION_SPECIAL_OP  = "2";

    // push parameter type part
    private static final String TYPE_RECALL_PUSH = "21";
    private static final String TYPE_SET_NOTIFICATION_LIMIT = "22";

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        // 接收到gcm的訊息
        if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
            PushLog.log(" into com.google.android.c2dm.intent.REGISTRATION ");

            String strRegistrationId = intent.getStringExtra("registration_id");
            String strFrom = intent.getStringExtra("from");
            // 如果註冊失敗
            if (intent.getStringExtra("error") != null) {

                NewsL.push("push registration fail");
                //当用户在无网络切换语言时，无法向server端上报语言环境 ，需要将状态更改为false ，以便切换到有网络时 再次上报
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                sharedPreferences.edit().putBoolean(GCMRegister.SENT_TOKEN_TO_SERVER, false).apply();

                // Registration 成功
            } else if (strRegistrationId != null) {
                // 把registration_id傳到我們server
                NewsL.push("push registration : " + strRegistrationId);
                // TODO: Implement this method to send any registration to your app's servers.
                sendRegistrationToServer(context, strRegistrationId);

            } else if( "google.com/iid".equals(strFrom) || GCM_REFRESH.equals(strFrom)) {
//                String senderid = StringUtils.getString(mContext, com.cmcm.onews.sdk.R.string.onews__gcm_senderid);
//                GCMRegister.registerToGoogle(context, senderid);
            }

        } else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
            PushLog.log(" into com.google.android.c2dm.intent.RECEIVE ");
            String msg_type = intent.getStringExtra("message_type");
            if(msg_type == null) {
                msg_type = "gcm";
            }

            byte control_type = -1;
            switch(msg_type.hashCode()) {
                case -2062414158:
                    if(msg_type.equals("deleted_messages")) {
                        control_type = 1;
                    }
                    break;
                case 102161:
                    if(msg_type.equals("gcm")) {
                        control_type = 0;
                    }
                    break;
                case 814694033:
                    if(msg_type.equals("send_error")) {
                        control_type = 3;
                    }
                    break;
                case 814800675:
                    if(msg_type.equals("send_event")) {
                        control_type = 2;
                    }
            }


            switch(control_type) {
                case 0:
                    this.getMessage(intent.getExtras());
                    break;
                case 1:
                    this.onDeletedMessages();
                    break;
                case 2:
                    this.onMessageSent(intent.getStringExtra("google.message_id"));
                    break;
                case 3:
                    this.onSendError(intent.getStringExtra("google.message_id"), intent.getStringExtra("error"));
                    break;
                default:
                    NewsL.push("Received message with unknown type: " + msg_type);
            }

        }

        PushLog.log(" out from gcm push receiver ");
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param regid The new token.
     */
    private void sendRegistrationToServer(Context context, String regid) {
        // Add custom implementation, as needed.
        GCM_CMRegServer register = new GCM_CMRegServer(context, regid);
        register.regToCMServer();


    }

    public void onDeletedMessages() {
    }

    public void onMessageSent(String msgId) {
    }

    public void onSendError(String msgId, String error) {
    }


    private void getMessage(Bundle bundle) {
        bundle.remove("message_type");
        bundle.remove("android.support.content.wakelockid");

        if(GCM_Notification.isGoogle_Notification(bundle)) {
            GCM_Notification.getInstance(mContext).zzv(bundle);
        } else {
           /* String var2 = bundle.getString("from");
            bundle.remove("from");*/

            Intent intent = new Intent(mContext,HandlePushIntentService.class);
            intent.putExtras(bundle);
            mContext.startService(intent);
        }

    }


}
