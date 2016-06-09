package com.cmcm.splash;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.cmcm.cloudconfig.CloudConfigGetter;
import com.cmcm.cloudconfig.CloudConfigKey;
import com.cmcm.onews.MainEntry;
import com.cmcm.onews.bitmapcache.VolleySingleton;
import com.cmcm.onews.model.ONews;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.model.ONewsSupportAction;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.sdk.SOURCE;
import com.cmcm.onews.ui.NewsDirectSendingActivity;
import com.cmcm.onews.util.NetworkUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jason.Su on 2016/3/13.
 * com.cmcm.splash
 * des:
 * email:suyanjiao@conew.com
 * 1553202383
 */
public class SplashHelper {
    private SplashHelper() {

    }

    public interface SplashPolicy {
        boolean isShouldShouldShow();

        void showResBitmap(ImageView imageView);

        void loadResBitmap();

        void onClick(View view);


        String getTitleString();

        String getSplashId();

        String getMatchId();
    }

    /**
     * 添加一个setter，让外部可以修改，保证程序的健壮性
     * @param cloudPolicy
     */

    public void setCloudPolicy(SplashPolicy cloudPolicy) {
        mCloudPolicy = cloudPolicy;
    }

    private SplashPolicy mCloudPolicy = new SplashPolicy() {
        @Override
        public boolean isShouldShouldShow() {
            final boolean beforeTheEndTime = isBeforeTheEndTime();
            if (!beforeTheEndTime){
                log("超过截至时间");
                return false;
            }
            final boolean afterStartTime = isAfterStartTime();
            if (!afterStartTime){
                log("早于开始时间");
                return false;
            }

            final String splashPicUrl = getSplashPicUrl();
            if (TextUtils.isEmpty(splashPicUrl)) {
                return false;
            }
            final String filePahtByUrl = VolleySingleton.getInstance().getFilePahtByUrl(splashPicUrl);
            if (TextUtils.isEmpty(filePahtByUrl)) {
                return false;
            }
            return true;

        }

        @Override
        public void showResBitmap(final ImageView imageView) {
            final String splashPicUrl = getSplashPicUrl();
            if (TextUtils.isEmpty(splashPicUrl)) {
                log("url is null");
                return;
            }

            final String filePahtByUrl = VolleySingleton.getInstance().getFilePahtByUrl(splashPicUrl);
            log("url path" + filePahtByUrl);
            if (TextUtils.isEmpty(filePahtByUrl)) {
                return;
            }
            VolleySingleton.getInstance().loadImage(imageView, splashPicUrl);


        }


        @Override
        public void loadResBitmap() {
            log("loadResBitmap");
            final String filePahtByUrl = VolleySingleton.getInstance().getFilePahtByUrl(getSplashPicUrl());
            // 因为bitmap 已经下载好了，所以没有必要再去下载
            if (!TextUtils.isEmpty(filePahtByUrl)) {
                return;
            }
            log("loadResBitmap" + filePahtByUrl);
            // 不在截至日期前，不下载
            if (!isBeforeTheEndTime()) {
                log("beyond the end time");
                return;
            }
            // 网络不能用，就不搞了
            if (!NetworkUtil.isNetworkAvailable(MainEntry.getAppContext())) {
                return;
            }
            if (!NetworkUtil.isNetworkAvailable(MainEntry.getAppContext()) && judgeIfOnlyWifi()) {
                return;
            }
            VolleySingleton.getInstance().preLoadImage(getSplashPicUrl());

        }

        @Override
        public void onClick(View view) {
            if (view == null) {
                return;
            }
            if (view.getContext() == null) {
                return;
            }
            final int slpashActiontype = getSplashActionType();
            if (0 == slpashActiontype) {
                return;
            }
            final String splashActionData = getSplashActionData();
            if (TextUtils.isEmpty(splashActionData)) {
                return;
            }
            ONews oNews = new ONews();
            oNews.action(ONewsSupportAction.supportAction(slpashActiontype));
            oNews.contentid(splashActionData);
            ONewsScenario scenario = ONewsScenario.getSplScenario();
            Bundle extraData = new Bundle();
            try {
                extraData.putInt(NewsDirectSendingActivity.KEY_INDEX, Integer.parseInt(getMatchId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            NewsSdk.INSTAMCE.openOnewsWithSource(view.getContext(), scenario, oNews, SOURCE.DETAIL_FROM_SPLASH, extraData);
        }

        @Override
        public String getTitleString() {
            return getSplashTitleString();
        }

        @Override
        public String getSplashId() {
            return getSplashActionId();
        }

        public String getMatchId(){
            return getSplashMatchId();
        }
    };


    private static class SingletonHolder {
        public static SplashHelper sIns = new SplashHelper();
    }

    public static SplashHelper getIns() {
        return SingletonHolder.sIns;
    }

    private void log(String content) {
        if (L.DEBUG){
            L.log("suj", content);
        }
    }

    // 在截至日期之前
    private boolean isBeforeTheEndTime() {
        final String splashActionEndTime = getSplashActionEndTime();
        if (TextUtils.isEmpty(splashActionEndTime)) {
            return false;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final Date end = simpleDateFormat.parse(splashActionEndTime);
            final long endTime = end.getTime();
            return endTime >= System.currentTimeMillis();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }

    private boolean isAfterStartTime() {
        final String actionStartTime = getSplashActionStartTime();
        if (TextUtils.isEmpty(actionStartTime)) {
            return false;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // 不采用世界时区呢
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final Date start = simpleDateFormat.parse(actionStartTime);
            final long startTime = start.getTime();
            return System.currentTimeMillis() >= startTime;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    private final static String SPLASH_PIC_URL = CloudConfigKey.SPLASH_PIC_URL;
    private final static String SPLASH_SECTION = CloudConfigKey.SPLASH_SECTION;
    private final static String SPLASH_ACTION_TYPE = CloudConfigKey.SPLASH_ACTION_TYPE;
    private final static String SPLASH_ACTION_START_TIME = CloudConfigKey.SPLASH_ACTION_START_TIME;
    private final static String SPLASH_ACTION_END_TIME = CloudConfigKey.SPLASH_ACTION_END_TIME;
    private final static String SPLASH_ACTION_DATA = CloudConfigKey.SPLASH_ACTION_DATA;
    private final static String SPLASH_WIFI_SWITCH = CloudConfigKey.SPLASH_WIFI_SWITCH;
    private final static String SPLASH_TITLE_STRING = CloudConfigKey.SPLASH_TITLE_STRING;
    private final static String SPLASH_ACTION_ID = CloudConfigKey.SPLASH_ACTION_ID;
    private final static String SPLASH_MATCH_ID = CloudConfigKey.SPLASH_MATCH_ID;

    private String getSplashPicUrl() {
        return CloudConfigGetter.getStringValue(CloudConfigKey.FUCTION_TYPE_MAIN, SPLASH_SECTION, SPLASH_PIC_URL, "");
    }

    // 默认类型0 ，什么都不能打开
    //
    private int getSplashActionType() {
        return CloudConfigGetter.getIntValue(CloudConfigKey.FUCTION_TYPE_MAIN, SPLASH_SECTION, SPLASH_ACTION_TYPE, 0);
    }

    private String getSplashActionStartTime() {
        return CloudConfigGetter.getStringValue(CloudConfigKey.FUCTION_TYPE_MAIN, SPLASH_SECTION, SPLASH_ACTION_START_TIME, "1970-01-01 00:00");
    }

    private String getSplashActionEndTime() {
        return CloudConfigGetter.getStringValue(CloudConfigKey.FUCTION_TYPE_MAIN, SPLASH_SECTION, SPLASH_ACTION_END_TIME, "1970-01-01 00:00");
    }

    private String getSplashActionData() {
        return CloudConfigGetter.getStringValue(CloudConfigKey.FUCTION_TYPE_MAIN, SPLASH_SECTION, SPLASH_ACTION_DATA, "");
    }

    // 如果为1的话，就是仅在wifi下载,默认所有网络
    private int getSplashWifiSwitch() {
        return CloudConfigGetter.getIntValue(CloudConfigKey.FUCTION_TYPE_MAIN, SPLASH_SECTION, SPLASH_WIFI_SWITCH, 0);
    }

    private boolean judgeIfOnlyWifi() {
        return getSplashWifiSwitch() == 1;
    }

    private String getSplashTitleString() {
        return CloudConfigGetter.getStringValue(CloudConfigKey.FUCTION_TYPE_MAIN, SPLASH_SECTION, SPLASH_TITLE_STRING, "");
    }
    private String getSplashActionId(){
        return CloudConfigGetter.getStringValue(CloudConfigKey.FUCTION_TYPE_MAIN, SPLASH_SECTION, SPLASH_ACTION_ID, "");
    }
    private String getSplashMatchId(){
        return CloudConfigGetter.getStringValue(CloudConfigKey.FUCTION_TYPE_MAIN, SPLASH_SECTION, SPLASH_MATCH_ID, "0");
    }


    public void showResBitmap(ImageView img) {
        if (mCloudPolicy != null) {
            final boolean shouldShouldShow = mCloudPolicy.isShouldShouldShow();;
            if (shouldShouldShow){
                mCloudPolicy.showResBitmap(img);
            }else {
                if (mLocalPolicy!=null){
                    mLocalPolicy.showResBitmap(img);
                }
            }
        }
    }

    private SplashPolicy mLocalPolicy = null;

    public boolean isShouldShowSplash() {
        if (mCloudPolicy != null) {
             boolean shouldShouldShow = mCloudPolicy.isShouldShouldShow();
            if (!shouldShouldShow){
                if (mLocalPolicy != null) {
                    shouldShouldShow = mLocalPolicy.isShouldShouldShow();
                }
            }
            return shouldShouldShow;
        }
        return false;
    }


    public void onClick(View view) {
        if (mCloudPolicy != null) {
            boolean shouldShouldShow = mCloudPolicy.isShouldShouldShow();
            if (shouldShouldShow){
                mCloudPolicy.onClick(view);
            }else {
                if (mLocalPolicy != null) {
                    mLocalPolicy.onClick(view);
                }
            }
        }
    }

    public void loadResBitmap() {
        if (mCloudPolicy != null) {
            mCloudPolicy.loadResBitmap();
        }
    }

    public String getTitleString() {
        if (mCloudPolicy != null) {
            return mCloudPolicy.getTitleString();
        }
        return "";
    }

    public String getSplashId() {
        if (mCloudPolicy != null) {
            final boolean shouldShouldShow = mCloudPolicy.isShouldShouldShow();
            if (shouldShouldShow) {
                return mCloudPolicy.getSplashId();
            } else {
                if (mLocalPolicy != null) {
                    return mLocalPolicy.getSplashId();
                }
                return "";
            }
        }
        return "";
    }


}
