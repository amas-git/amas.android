package com.cmcm.onews.util.push;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cmcm.onews.C;
import com.cmcm.onews.NewsL;
import com.cmcm.onews.util.NewsDebugConfigUtil;
import com.cmcm.onews.util.push.comm.NotifyHelper;
import com.cmcm.onews.util.push.comm.PushConst;
import com.cmcm.onews.util.push.comm.PushIdHelper;

import org.json.JSONObject;

/**
 * receiver消息的处理服务
 * Created by yuanshouhui on 2015/12/7
 */
public class HandlePushIntentService extends IntentService {

    public static final String INTENT_EXTRA_MESSAGE_CONTENT = "msg";

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    // push parameter section part
    private static String TYPE_TWO_LINE = "11";
    private static final String SECTION_NOTIFY  = "1";
    private static final String SECTION_SPECIAL_OP  = "2";
    // push parameter type part
    private static final String TYPE_RECALL_PUSH = "21";
    private static final String TYPE_SET_NOTIFICATION_LIMIT = "22";

    public HandlePushIntentService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
         if(null==intent){
             return;
         }
         Bundle bundle = intent.getExtras();
         onMessageReceived("",bundle);

    }


    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data ReportData bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    public void onMessageReceived(String from, Bundle data) {
        try {
            String message = data.getString(INTENT_EXTRA_MESSAGE_CONTENT);
//            NewsL.push("From: " + from);
            NewsL.push("msg: " + message);
            // [START_EXCLUDE]
            /**
             * Production applications would usually process the message here.
             * Eg: - Syncing with server.
             *     - Store message in local database.
             *     - Update UI.
             */

            /**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
             */
            handleMessage(message);
            // [END_EXCLUDE]
        } catch (Exception e) { e.printStackTrace();}
    }
    // [END receive_message]


    private void handleMessage(String message) {
        try{
            JSONObject jsonObject = new JSONObject(message);

            String pushId = jsonObject.getString("pushid");
            //push到达上报
            reportCMTrackArrival(pushId);


            //MIPSUH 通过含有isnew字段得知是国内新版，不是新版不处理
/* BUILD_CTRL:IF:CN_VERSION_ONLY_
            if(com.cleanmaster.base.crash.util.system.ConflictCommons.isCNVersion()) {
                if (!jsonObject.has("isnew")) {
                    return;
                }
            }
BUILD_CTRL:ENDIF:CN_VERSION_ONLY_ */


            String section = jsonObject.getString("section");

            if(NewsDebugConfigUtil.getInstance().isCancelPushRepeatEnable()){
                //Debug模式下 不做去重处理
                NewsL.push("请注意 Debug模式下 新闻推送不做去重处理");
            }else{
                if(PushIdHelper.isExistPushId(pushId)){
                    //如果存在 则不再展示
                    NewsL.push("请注意 此条新闻推送已被过滤去重处理");
                    return;
                }else{
                    PushIdHelper.pushIdSave(pushId);
                }
            }
            // SECTION decide which action , type decide layout detail
            switch (section) {
                case SECTION_NOTIFY:
                    NotificationExeHelper.sendNotification(jsonObject);
                    break;
                case SECTION_SPECIAL_OP:
                    handleSpecialOP(jsonObject);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleSpecialOP(JSONObject jsonObject) {
        try {
            // SECTION decide which action , type decide operation
            String action  = jsonObject.getString("action");
            String pushId  = jsonObject.getString("pushid");
            NotificationManager notificationManager =
                    (NotificationManager) C.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

            switch (action) {
                case TYPE_RECALL_PUSH:
                    String recall_pushId  = jsonObject.getString("recall_pusdid");
                    int NotifyID = NotifyHelper.getInstance().findNotifyID(recall_pushId);
                    notificationManager.cancel(NotifyID);
                    break;

                case TYPE_SET_NOTIFICATION_LIMIT:
                    int limit  = Integer.parseInt(jsonObject.getString("limit"));
                    String removeNotifyIDList = NotifyHelper.getInstance().setNotifyLimit(limit);
                    String[] mapArr = removeNotifyIDList.split(",");
                    for (int idx = 0; idx < mapArr.length; idx++) {
                        notificationManager.cancel(Integer.parseInt(mapArr[idx]));
                    }
                    break;

                default:
                    PushCallbackMgr.getInstance().handlePush(jsonObject);
                    break;
            }
        }
        catch (Exception e) {
            NewsL.push("[handleSpecialOP] parse action fail!");
            e.printStackTrace();
        }
    }

    private void reportCMTrackArrival(String pushId){
        PushOutAPI.reportMessageBehavior(this, PushConst.ACTION.ACCESS,pushId);
    }
}
