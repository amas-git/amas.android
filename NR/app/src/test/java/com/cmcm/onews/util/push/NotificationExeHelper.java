package com.cmcm.onews.util.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.cmcm.onews.C;
import com.cmcm.onews.NewsL;
import com.cmcm.onews.R;
import com.cmcm.onews.bitmapcache.ImageLoaderSingleton;
import com.cmcm.onews.infoc.newsindia_notification;
import com.cmcm.onews.loader.LOAD_DETAILS;
import com.cmcm.onews.loader.ONewsDetailLoader;
import com.cmcm.onews.loader.ONewsLoadResult_LOAD_REMOTE;
import com.cmcm.onews.model.ONews;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.model.ONewsSupportAction;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.ui.NewsBaseActivity;
import com.cmcm.onews.ui.NewsOnePageDetailActivity;
import com.cmcm.onews.util.BitmapUtil;
import com.cmcm.onews.util.ConflictCommons;
import com.cmcm.onews.util.LanguageCountry;
import com.cmcm.onews.util.NetworkUtil;
import com.cmcm.onews.util.StringUtils;
import com.cmcm.onews.util.UIConfigManager;
import com.cmcm.onews.util.push.comm.NotifyHelper;
import com.cmcm.onews.util.push.comm.PushConst;
import com.cmcm.onews.util.push.comm.PushLog;

import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * 通知栏消息类 ，负责具体通知的执行和显示
 * Created by yuanshouhui on 2015/12/2.
 */
public class NotificationExeHelper {
    /**
     * 直拨业打开
     */
    public static final String SA_999 = "0x999";

    // push parameter type part
    private static final String TYPE_TWO_LINE = "11";
    private static final String TYPE_IMAGE_NOTIFICATION = "12";

    private static final String CARD_MODE_THREE_LINE = "112";//多行展示
    private static final String CARD_MODE_TYPE_IMAGE = "122";//大卡通知
    private static final String ABTEST_BLUE_STYLE = "1";
    private static final String ABTEST_WHITE_STYLE = "2";

    private static final String  PHONE_LANGUAGE ="language";
    private static final String  OPEN_ACTION ="open_action";
    private static byte IMGVIEW_LOAD_TYPE = 0;//0、纯文字 1、成功 2、失败
    private static byte IMGVIEW_LOAD_TEXT = 0;//0、纯文字
    private static byte IMGVIEW_LOAD_SUCCESS = 1;//1、成功
    private static byte IMGVIEW_LOAD_ERROR = 2;//2、失败

    private static byte SHOW_TYPE = 1;// 1. 双行样式 2. 多行文字样式 3. 有小图样式 4. 大卡全图样式
    private static byte SHOW_TYPE_TWO_LINE = 1;//1. 双行样式
    private static byte SHOW_TYPE_MORE_LINE = 2;//2. 多行文字样式
    private static byte SHOW_TYPE_SMAIL_IMAGE = 3;//3. 有小图样式
    private static byte SHOW_TYPE_BIG_IMAGE = 4;//4. 大卡全图样式

    private static byte PUSH_TYPE = 1;//0.无动作 1. 新闻  2. 专题  3.视频
    private static byte PUSH_TYPE_NONE = 0;//0. 无动作
    private static byte PUSH_TYPE_NEWS = 1;//1. 新闻
    private static byte PUSH_TYPE_SPECIAL = 2;//2. 专题
    private static byte PUSH_TYPE_VIDEO = 3;//3.视频
    private static byte PUSH_TYPE_OUTLINK = 4;//4.外鍊
    private static byte PUSH_TYPE_FLUX = 5;//5.雙計
    private static byte PUSH_TYPE_LIVE = 6;//6. 直拨
    private static byte PUSH_TYPE_PHOTO = 7;//7. 图集

    public static void sendNotification(JSONObject jsonObject) {
        try {
            // SECTION decide which action , type decide layout detail
            String action = jsonObject.getString("action");
            String pushId = jsonObject.getString("pushid");
            String contentId = jsonObject.getString("newsid");
            String title = jsonObject.getString("title");
            String language = jsonObject.optString(PHONE_LANGUAGE);

            String openAction = (jsonObject.optString(OPEN_ACTION)).replace("\'", "");

            if(ConflictCommons.isCNVersion()){
            /* BUILD_CTRL:IF:CN_VERSION_ONLY */

            /* BUILD_CTRL:ENDIF:CN_VERSION_ONLY */
            }else{
            /* BUILD_CTRL:IF:OU_VERSION_ONLY */
                if(!TextUtils.isEmpty(language)){
                    LanguageCountry lang = UIConfigManager.getInstanse(C.getAppContext()).getLanguageSelected(C.getAppContext());
                    if(!language.equals(lang.getLanguage())){
                        //如果push国际语言与当前手机设置的语言不一样 不展示消息
                        return;
                    }
                }
            /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
            }

            int notify_id = NotifyHelper.getInstance().getNewNotifyId(pushId);
            getNewsPreload(C.getAppContext(), jsonObject,notify_id);

        } catch (Exception e) {
            NewsL.push("[send message] parse msg fail!");
            e.printStackTrace();
        }
    }


    /**
     * 预加载
     * @param context
     * @param jsonObject
     * @return
     */
    private static void getNewsPreload(Context context, JSONObject jsonObject,int notify_id) {

        try {
            // init data for build notification
            String contentid = jsonObject.getString("newsid");if(NetworkUtil.isNetworkActive(context)){
                loadDetail(context,contentid,jsonObject,notify_id);
            }

        } catch (Exception e) {
            NewsL.push("getNewsIntent fail!");
            e.printStackTrace();
        }
    }

    private static void showNotification(Context context, JSONObject jsonObject,ONews news,int notify_id) {
        Intent intent = null;
        try {
            String openAction = (jsonObject.optString("open_action")).replace("\'", "");

            // init data for build notification
            String action = jsonObject.getString("action");
            String card_mode = jsonObject.optString("card_mode");
            String pushId = jsonObject.getString("pushid");
            String contentid = jsonObject.getString("newsid");
            String title = jsonObject.optString("title");
            String source = jsonObject.optString("source");
            String pubtime = jsonObject.optString("pubtime");


            ONews oNews = new ONews();
            if(null==news){
                oNews.contentid(contentid);
                oNews.title(title);
                oNews.source(source);
                oNews.pubtime(pubtime);
                oNews.action(openAction);
            }else{
                oNews = news;
            }

            String match =  jsonObject.optString("match");
            int matchIdx = 0;
            if (!("".equals(match))){
                matchIdx = Integer.parseInt(match);
            }

            intent = NewsSdk.INSTAMCE.getOpenNewsIntent(context
                    , ONewsScenario.getPushScenario()
                    , oNews
                    , NewsOnePageDetailActivity.FROM_PUSH
                    , matchIdx
            );
            if (null != intent) {
                switch (action) {
                    case TYPE_TWO_LINE:
                        IMGVIEW_LOAD_TYPE = IMGVIEW_LOAD_TEXT;
                        //多行展示
                        if (CARD_MODE_THREE_LINE.equals(card_mode)) {
                            SHOW_TYPE = SHOW_TYPE_MORE_LINE;
                        }else{
                            //双行展示
                            SHOW_TYPE = SHOW_TYPE_TWO_LINE;
                        }
                        break;
                    case TYPE_IMAGE_NOTIFICATION:
                        if (CARD_MODE_TYPE_IMAGE.equals(card_mode)) {
                            SHOW_TYPE = SHOW_TYPE_BIG_IMAGE;
                        }else{
                            SHOW_TYPE = SHOW_TYPE_SMAIL_IMAGE;
                        }
                        break;
                    default:
                        break;
                }

                intent.putExtra(NewsBaseActivity.KEY_PUSHID, pushId);
                intent.putExtra(NewsBaseActivity.KEY_PUSH_TYPE, getPushType(openAction));
                intent.putExtra(NewsBaseActivity.KEY_SHOW_TYPE, SHOW_TYPE);
                intent.putExtra(NewsBaseActivity.KEY_IMG_VIEW, IMGVIEW_LOAD_TYPE);

                if (null == intent) {
                    L.gcm("action intent not found! - not support push ");
                    return;
                }
                PendingIntent pendingIntent = PendingIntent.getActivity(C.getAppContext(), notify_id, intent, PendingIntent.FLAG_ONE_SHOT);

                NotificationManager notificationManager =
                        (NotificationManager) C.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                switch (action) {
                    case TYPE_TWO_LINE:
                        showNotificationFromTwoLine(title, jsonObject, pendingIntent, notificationManager, defaultSoundUri, notify_id);
                        break;
                    case TYPE_IMAGE_NOTIFICATION:
                        showNotificationFromImage(title, jsonObject, pendingIntent, notificationManager, defaultSoundUri, notify_id);
                        break;
                    default:
                        break;
                }
                //展示上报
                PushOutAPI.reportMessageBehavior(C.getAppContext(), PushConst.ACTION.DISPLAY,jsonObject.getString("pushid"));
                reportDisplayToInfoc(pushId, contentid, getShowtype(), IMGVIEW_LOAD_TYPE, getPushType(openAction));

            }
        } catch (Exception e) {
            NewsL.push("getNewsIntent fail!");
            e.printStackTrace();
        }
    }


    /**
     * 标准title样式及多行BigText
     *
     * @param title
     * @param jsonObject
     * @param pendingIntent
     * @param notificationManager
     * @param defaultSoundUri
     * @param notify_id
     */
    private static void showNotificationFromTwoLine(String title, JSONObject jsonObject, PendingIntent pendingIntent, NotificationManager notificationManager, Uri defaultSoundUri, int notify_id) {
        try {
            //创建标准的title
            String body = jsonObject.optString("description");
            String card_mode = jsonObject.optString("card_mode");
            String light = jsonObject.optString("light");
            String customed_light = jsonObject.optString("customed_light");
            NotificationCompat.Builder notificationBuilder = null;
            IMGVIEW_LOAD_TYPE = IMGVIEW_LOAD_TEXT;
            int icon = getIcon();
            //多行展示
            if (CARD_MODE_THREE_LINE.equals(card_mode)) {
//                showtype = 2;
                NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
                bigStyle.bigText(body);
                bigStyle.setBigContentTitle(title);

                notificationBuilder = new NotificationCompat.Builder(C.getAppContext())
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(pendingIntent)
                        .setSound(defaultSoundUri)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setStyle(bigStyle);
            } else {
                //双行展示
//                showtype = 1;
                notificationBuilder = new NotificationCompat.Builder(C.getAppContext())
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
            }
            setLightIfNotEmpty(notificationBuilder, light);
            setLightIfNotEmpty(notificationBuilder, customed_light);
            Notification notification = notificationBuilder.build();
            setNotifPriority(notification);
            notificationManager.notify(notify_id, notification);
//            //////////展示上报
//            PushOutAPI.reportMessageBehavior(C.getAppContext(), PushConst.ACTION.DISPLAY,jsonObject.getString("pushid"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setLightIfNotEmpty(NotificationCompat.Builder notificationBuilder, String light) {
        if (!TextUtils.isEmpty(light)) {
            try {
                notificationBuilder.setLights(android.graphics.Color.parseColor(light), 1000, 1000);
            } catch (IllegalArgumentException e) {
                com.cmcm.onews.sdk.L.gcm("Color.parseColor  IllegalArgumentException");
                notificationBuilder.setLights(android.graphics.Color.BLUE, 1000, 1000);
            }
        }
    }


    /**
     * 图片通知样式 （左边显示自定义网络图标）
     *
     * @param title
     * @param jsonObject
     * @param pendingIntent
     * @param notificationManager
     * @param defaultSoundUri
     * @param notify_id
     */
    private static void showNotificationFromImage(String title, JSONObject jsonObject, PendingIntent pendingIntent, NotificationManager notificationManager, Uri defaultSoundUri, int notify_id) {
        try {
            //创建图片通知样式 （左边显示网络图标）
            String imgUrl = jsonObject.optString("imgUrl");

            int icon = getIcon();
            int logo = getLogo();
            String AB_TEST = jsonObject.optString("ab_test");
            String card_mode = jsonObject.optString("card_mode");
            String light = jsonObject.optString("light");
            String customed_light = jsonObject.optString("customed_light");
            RemoteViews contentView = null;

            if (CARD_MODE_TYPE_IMAGE.equals(card_mode)) {
                showNotificationFromBigImage(title, jsonObject, pendingIntent, notificationManager, defaultSoundUri, notify_id);
//                showtype = 4;
            } else {
                switch (AB_TEST) {
                    case ABTEST_BLUE_STYLE:
                        contentView = new RemoteViews(C.getAppContext().getPackageName(), R.layout.onews__custom_notification);
                        contentView.setImageViewResource(R.id.icon, R.drawable.onews__ic_logo_notification_white);
                        break;
                    case ABTEST_WHITE_STYLE:
                        contentView = new RemoteViews(C.getAppContext().getPackageName(), R.layout.onews__custom_notification_white);
                        contentView.setImageViewResource(R.id.icon, logo);
                        break;
                    default:
                        contentView = new RemoteViews(C.getAppContext().getPackageName(), R.layout.onews__custom_notification_white);
                        contentView.setImageViewResource(R.id.icon, logo);
                        break;
                }

//                showtype = 3;
                contentView.setTextViewText(R.id.title, title);

                Bitmap bm = null;
                try {
                    bm = BitmapUtil.getHttpBitmap(imgUrl);
                    if (null != bm) {
                        IMGVIEW_LOAD_TYPE = IMGVIEW_LOAD_SUCCESS;
                        contentView.setImageViewBitmap(R.id.image, bm);
                    } else {
                        IMGVIEW_LOAD_TYPE = IMGVIEW_LOAD_ERROR;
                        contentView.setImageViewResource(R.id.image, icon);
                    }
                } catch (Exception e) {
                    IMGVIEW_LOAD_TYPE = IMGVIEW_LOAD_ERROR;
                    e.printStackTrace();
                    NewsL.push("Error getting bitmap");
                }
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(C.getAppContext())
                        .setSmallIcon(icon)
                        .setContent(contentView)
                        .setContentIntent(pendingIntent)
                        .setSound(defaultSoundUri)
                        .setDefaults(Notification.FLAG_ONLY_ALERT_ONCE)
                        .setAutoCancel(true);
                setLightIfNotEmpty(notificationBuilder, light);
                setLightIfNotEmpty(notificationBuilder, customed_light);
                Notification notification = notificationBuilder.build();
                setNotifPriority(notification);
                notificationManager.notify(notify_id, notification);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 大图片通知样式
     *
     * @param title
     * @param jsonObject
     * @param pendingIntent
     * @param notificationManager
     * @param defaultSoundUri
     * @param notify_id
     */
    private static void showNotificationFromBigImage(String title, JSONObject jsonObject, PendingIntent pendingIntent, NotificationManager notificationManager, Uri defaultSoundUri, int notify_id) {
        try {
            //创建图片通知样式 （标题下边显示网络图标）
            String imgUrl = jsonObject.optString("imgUrl");
            int icon = getIcon();
            String description = jsonObject.optString("description");
            String light = jsonObject.optString("light");
            String customed_light = jsonObject.optString("customed_light");
            Bitmap bm = null;
            try {
                bm = BitmapUtil.getHttpBitmap(imgUrl);
                if(null!=bm){
                    IMGVIEW_LOAD_TYPE = IMGVIEW_LOAD_SUCCESS;
                }else{
                    bm = BitmapFactory.decodeResource(C.getAppContext().getResources(),R.drawable.onews_sdk_item_big_default);
                    IMGVIEW_LOAD_TYPE = IMGVIEW_LOAD_ERROR;
                }
            } catch (Exception e) {
                IMGVIEW_LOAD_TYPE = IMGVIEW_LOAD_ERROR;
                PushLog.log("Error getting bitmap");
            }


            NotificationCompat.BigPictureStyle bigStyle = new NotificationCompat.BigPictureStyle();
//            bigStyle.bigLargeIcon(bm);
            bigStyle.bigPicture(bm);
            bigStyle.setSummaryText(description);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(C.getAppContext())
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setContentIntent(pendingIntent)
                    .setSound(defaultSoundUri)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setStyle(bigStyle);
            setLightIfNotEmpty(notificationBuilder, light);
            setLightIfNotEmpty(notificationBuilder, customed_light);
            Notification notification = notificationBuilder.build();
            setNotifPriority(notification);
            notificationManager.notify(notify_id, notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息置顶
     *
     * @param notification
     */
    private static void setNotifPriority(Notification notification) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 16) {
            try {
                int PRIORITY_MAX = 2;
                Field priority = Notification.class.getField("priority");
                priority.setInt(notification, PRIORITY_MAX);
            } catch (Exception e) {
            }
        } else {
            try {
                Field priority = Notification.class.getDeclaredField("FLAG_HIGH_PRIORITY");
                int FLAG_HIGH_PRIORITY = ((Integer) priority.getInt(null)).intValue();
                notification.flags = FLAG_HIGH_PRIORITY | notification.flags;
            } catch (Exception e) {
            }
        }
    }

    private static void reportDisplayToInfoc(String pushId, String contentid, byte showtype, byte imgview, byte pushtype) {
        //infoC report
        newsindia_notification notification_report = new newsindia_notification();
        notification_report.action(1);
        notification_report.newsid(contentid);
        notification_report.clicktime((int) (System.currentTimeMillis() / 1000l));
        notification_report.showtype(showtype);
        notification_report.imgview(imgview);
        notification_report.pushtype(pushtype);
        notification_report.pushid(pushId);
        notification_report.report();
    }


    /**
     *推送类型
     * @param openAction
     * @return
     */
    private static byte getPushType(String openAction){
        if (ONewsSupportAction.supportAction(ONewsSupportAction.SA_02).equalsIgnoreCase(openAction)){
            //打开新闻
            PUSH_TYPE = PUSH_TYPE_NEWS;
        }else if (ONewsSupportAction.supportAction(ONewsSupportAction.SA_20).equalsIgnoreCase(openAction)){
            //打开专题
            PUSH_TYPE = PUSH_TYPE_SPECIAL;
        }else if (ONewsSupportAction.supportAction(ONewsSupportAction.SA_04).equalsIgnoreCase(openAction)){
            //打开视频
            PUSH_TYPE = PUSH_TYPE_VIDEO;
        }else if (ONewsSupportAction.supportAction(ONewsSupportAction.SA_01).equalsIgnoreCase(openAction)){
            //打开外炼
            PUSH_TYPE = PUSH_TYPE_OUTLINK;
        }else if (ONewsSupportAction.supportAction(ONewsSupportAction.SA_08).equalsIgnoreCase(openAction)){
            //打开双计
            PUSH_TYPE = PUSH_TYPE_FLUX;
        }else if (ONewsSupportAction.supportAction(ONewsSupportAction.SA_1000).equalsIgnoreCase(openAction)){
            //打开直拨
            PUSH_TYPE = PUSH_TYPE_LIVE;
        }else if (SA_999.equals(openAction)){
            //打开直拨
            PUSH_TYPE = PUSH_TYPE_LIVE;
        }else if (ONewsSupportAction.supportAction(ONewsSupportAction.SA_200).equalsIgnoreCase(openAction)){
            PUSH_TYPE = PUSH_TYPE_PHOTO;
        } else {
            //无动作
            PUSH_TYPE = PUSH_TYPE_NONE;
        }

        return PUSH_TYPE;
    }

    private static  byte getShowtype(){
        return  SHOW_TYPE;
    }

    private static int getIcon(){
        int icon = -1;
        if(ConflictCommons.isCNVersion()){
                 /* BUILD_CTRL:IF:CNVERSION */
            icon = R.drawable.onews__logo_instanews_cn;
                /* BUILD_CTRL:ENDIF:CNVERSION */
        }else{
               /* BUILD_CTRL:IF:OU_VERSION_ONLY */
            icon = R.drawable.onews__logo_instanews;
                /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
        }

        return icon;
    }

    private static int getLogo(){
        int icon = -1;
        if(ConflictCommons.isCNVersion()){
                 /* BUILD_CTRL:IF:CNVERSION */
            icon = R.drawable.onews__ic_logo_notification_cn;
                /* BUILD_CTRL:ENDIF:CNVERSION */
        }else{
               /* BUILD_CTRL:IF:OU_VERSION_ONLY */
            icon = R.drawable.onews__ic_logo_notification;
                /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
        }

        return icon;
    }



    private static void loadDetail(final Context context, String contentId, final JSONObject jsonObject, final int notify_id){
        if(contentId == null || TextUtils.isEmpty(contentId)){
            return ;
        }
        ONewsScenario  scenario = new ONewsScenario();

        LOAD_DETAILS DETAILS = new LOAD_DETAILS(scenario);
        DETAILS.contetnIds().add(contentId);
        DETAILS.setIsEnableAsynSaveCache(true);

        new ONewsDetailLoader(){
            @Override
            protected void onLoadResultInBackground(ONewsLoadResult_LOAD_REMOTE r) {
                super.onLoadResultInBackground(r);
                if(null!=r && r.response!=null && r.response.newsList()!=null && r.response.newsList().size()>0){
                    ONews news = r.response.newsList().get(0);
                    showNotification(context,jsonObject,news,notify_id);
                    ImageLoaderSingleton.getInstance().preLoadImages(news);
                }else{
                    showNotification(context,jsonObject,null,notify_id);
                }



            }
        }.execute(DETAILS);

    }


}
