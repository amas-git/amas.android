package com.cmcm.onews.util.push.mi;

//import com.ijinshan.base.utils.JSONParser;
//import com.ijinshan.base.utils.KLog;
//import com.ijinshan.browser.service.PushServiceAssist;
//import com.ijinshan.browser.service.message.MessageEntry;
//import com.ijinshan.browser.service.message.NewsMessage;
//import com.ijinshan.browser.service.message.TopicMessage;

/**
 * Created by pc on 2015/12/24.
 */
public class MiPushManager {
//    public ICallInterface icallback;
//    public Context mContext ;
//    private Handler mMessageHandler = null;
//
//    public MiPushManager(ICallInterface icallback){
//        this.icallback = icallback;
//
//        HandlerThread messageThread = new HandlerThread("messageHandler", Thread.NORM_PRIORITY);
//        messageThread.start();
//        mMessageHandler = new Handler(messageThread.getLooper());
//    }
//    public interface ICallInterface
//    {
//        public boolean onMessage(MessageEntry entry);
//    }

//    public  void onHandleIntent(Context mContext , Intent workIntent) { //接收参数，做耗时的处理，处理完毕，发送Broadcat
//        final String msg = workIntent.getStringExtra(MiPushConst.EXTENDED_MSG) ;
//        this.mContext = mContext;
//        if(!TextUtils.isEmpty(msg) && icallback!= null){
//
//            mMessageHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject jsonItem = JSONParser.parseFromString(msg);
//                    processMessage(jsonItem);
//                }
//            });
//
//
//
//        }
//
//    }

//    /**开始处理收到的消息*/
//    public  void processMessage(JSONObject data) {
//        MessageEntry entry = MessageEntry.readFromJSONObject(data);
//        String msgId = entry.getTaskId();
//        //收到命令推送的到达也上报popup的埋点
//        if(!TextUtils.isEmpty(msgId)){
//            //到达上报
//            int action = MiPushConst.ACTION.ACCESS;
////                PushServiceAssist.reportMessageBehavior(this, action, msgId, msgType, entry.getType(), entry.getIsReport(), null);
//            //收到命令推送的到达也上报popup的埋点
//            MiPushReportUtils.reportMessageBehavior(mContext,action,msgId);
//        }
//
//
//
//        if (entry != null && entry.isValid()) {
//
//            MessageEntry.AvailableState state = entry.inAvailableArea();
//            if (state == MessageEntry.AvailableState.EXPIRED) {
//                // 过期消息，直接扔掉
//                return;
//            } else if (state == MessageEntry.AvailableState.HIBERNATE) {
//                // 休眠消息，不處理
//                // add2BlockedQueue(entry);
//                return ;
//            }
//
//            if(icallback!=null){
//                PushLog.log("解析后面的执行归调用方回调处理 ----- execute in here");
//                icallback.onMessage(entry);
//            }
//        }//~if
//    }//~processMessage
//
//    public void destroy() {
//        if (mMessageHandler != null) {
//            mMessageHandler.removeCallbacksAndMessages(null);
//            mMessageHandler.getLooper().quit();
//            mMessageHandler = null;
//        }
//    }
}
