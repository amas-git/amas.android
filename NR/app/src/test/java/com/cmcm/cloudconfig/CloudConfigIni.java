package com.cmcm.cloudconfig;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cm.CH;
import com.cmcm.onews.C;
import com.cmcm.onews.MainEntry;
import com.cmcm.onews.service.ONewsService;
import com.cmcm.onews.util.ConflictCommons;
import com.cmcm.onews.util.DeviceUtils;
import com.cmcm.onews.util.LanguageCountry;
import com.cmcm.onews.util.UIConfigManager;
import com.ijinshan.cloudconfig.callback.InnerCallBack;
import com.ijinshan.cloudconfig.callback.InnerCallBackHelper;
import com.ijinshan.cloudconfig.deepcloudconfig.CloudConfig;
import com.ijinshan.cloudconfig.deepcloudconfig.PullCloudConfig;
import com.ijinshan.cloudconfig.init.CloudConfigEnv;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jason.Su on 2016/2/22.
 * com.cmcm.cloudconfig
 * des:
 * email:suyanjiao@conew.com
 * 1553202383
 */
public class CloudConfigIni {
    public static final int MIN_CUBE_VERSION_REQUEST_INTERVAL_MINUTES = 10;
    public static final int MAX_CUBE_VERSION_REQUEST_INTERVAL_MINUTES = (int) TimeUnit.HOURS.toMinutes(24L);
    public static final long DEFAULT_CUBE_VERSION_REQUEST_INTERVAL = TimeUnit.MINUTES.toMillis(10L);
    public static final long DEFAULT_CUBE_CHECK_CLOUD_CONFIG_INTERVAL = TimeUnit.MINUTES.toMillis(10L);

    private static long sCurrentCubeCheckUpdateInterval = -1;


    public static void init() {
        CloudConfigEnv.setApplicationContext(MainEntry.getAppContext());
        if (ConflictCommons.isCNVersion()) {
            CloudConfigEnv.init(String.valueOf(CH.getChannelId()), "ijinshannews_cn");
        } else {
            CloudConfigEnv.init(String.valueOf(CH.getChannelId()), "instanews");
        }
        InnerCallBackHelper.initCallBack(new InnerCallBack() {
            @Override
            public String getApkVersion() {
                long version = 0l;
                try {
                    version = DeviceUtils.getVersionCode(C.getAppContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return String.valueOf(version);
            }

            //TODO 这里有一个神一样的坑，因为原来apk升级了，但是配置文件要更新

            @Override
            public String getPkgName() {
                if (ConflictCommons.isCNVersion()) {
                    return "com.ijinshan.news_cn";
                } else {
                    return "com.cmcm.newsindia";
                }
            }

            @Override
            public String getLanParams() {
                final LanguageCountry languageSelected = UIConfigManager.getInstanse(MainEntry.getAppContext()).getLanguageSelected(MainEntry.getAppContext());
                if (languageSelected != null) {
                    return languageSelected.getLanguage();
                } else {
                    return DeviceUtils.getLocale(MainEntry.getAppContext());
                }
            }

            @Override
            public String getChannelId() {
                return String.valueOf(CH.getChannelId());
            }
        });
        PullCloudConfig.getInstance().init();
        CloudConfigEnv.setInitRcmdFinished();
        // 展示用户引导就是首次安装进入
        // 总觉得是神一般的坑，算了，先和user guide 绑定吧
        if (!UIConfigManager.getInstanse(MainEntry.getAppContext()).getNEWS_USER_GUIDE()) {
            manualRequestCube();
            CloudConfig.getInstance().reloadData();
        }
    }

    public static void scheduleUpdate() {
        // 魔方后台更新的逻辑
        Intent intent = new Intent(ONewsService.ACTION_CUBE_CHECK_UPDATE_FREQUENCY);
        intent.setPackage(MainEntry.getAppContext().getPackageName());
        intent.setComponent(new ComponentName(MainEntry.getAppContext(), ONewsService.class));
        PendingIntent pendingIntent = PendingIntent.getService(MainEntry.getAppContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) MainEntry.getAppContext().getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
        final long interval = DEFAULT_CUBE_CHECK_CLOUD_CONFIG_INTERVAL;
        alarm.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
                interval, pendingIntent);
    }

    public static void onAlarmCheckUpdateFrequency() {
        // ONewsService调用这里的地方已经是在线程里面了，无需再加异步处理的逻辑

        int currentTimeHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> timeValues = getCloudVersionRequestTimeDividerList();
        if (timeValues == null || timeValues.size() <= 1) {
            updateCurrentCubeCheckUpdateInterval(DEFAULT_CUBE_VERSION_REQUEST_INTERVAL);
            return;
        }

        // 查找当前处于哪个时间段里面

        int atWhichInterval = timeValues.size() - 1;
        for (int i = 0; i < timeValues.size(); ++i) {
            int timeDividerInt = timeValues.get(i).getKey();
            if (currentTimeHour < timeDividerInt) {
                if (i != 0) {
                    atWhichInterval = i - 1;
                }
                break;
            }
        }

        // 更新定时器的时间

        long updateInterval = TimeUnit.MINUTES.toMillis(timeValues.get(atWhichInterval).getValue());
        updateCurrentCubeCheckUpdateInterval(updateInterval);
    }

    private static ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> getCloudVersionRequestTimeDividerList() {
        String cloudTimeDividersRawString = CloudConfigGetter.getStringValue(
                CloudConfigKey.FUCTION_TYPE_MAIN,
                CloudConfigKey.CLOUD_SECTION_KEY_CM_PUSH,
                CloudConfigKey.CLOUD_SUBKEY_CM_PUSH_CUBE_GET_VERSION_TIME_DIVIDER,
                "6#12#17#21#24"
        );
        String cloudTimeIntervalsRawString = CloudConfigGetter.getStringValue(
                CloudConfigKey.FUCTION_TYPE_MAIN,
                CloudConfigKey.CLOUD_SECTION_KEY_CM_PUSH,
                CloudConfigKey.CLOUD_SUBKEY_CM_PUSH_CUBE_GET_VERSION_TIME_INTERVAL,
                "15#60#15#60#360"
        );

        if (TextUtils.isEmpty(cloudTimeDividersRawString) || TextUtils.isEmpty(cloudTimeIntervalsRawString)) {
            return null;
        }

        String[] timeDividerStringArr = cloudTimeDividersRawString.split("#");
        String[] timeIntervalStringArr = cloudTimeIntervalsRawString.split("#");
        if (timeDividerStringArr.length < 1 || timeIntervalStringArr.length < 1
                || timeDividerStringArr.length != timeIntervalStringArr.length) {
            // 云控的值无效，啥也不做
            return null;
        }

        ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> retVal = null;

        for (int i = 0; i < timeDividerStringArr.length; ++i) {
            String timeDividerString = timeDividerStringArr[i];
            String timeIntervalString = timeIntervalStringArr[i];
            int timeDividerValue = -1;
            int timeIntervalValue = -1;
            try {
                timeDividerValue = Integer.parseInt(timeDividerString);
            } catch (NumberFormatException e) {
                // pass
            }
            try {
                timeIntervalValue = Integer.parseInt(timeIntervalString);
            } catch (NumberFormatException e) {
                // pass
            }

            if (timeDividerValue < 0 || timeDividerValue > 24
                    || timeIntervalValue <= 0) {
                // 格式不正确，啥也不做
                return null;
            }

            if (timeIntervalValue < MIN_CUBE_VERSION_REQUEST_INTERVAL_MINUTES) {
                timeIntervalValue = MIN_CUBE_VERSION_REQUEST_INTERVAL_MINUTES;
            }
            if (timeIntervalValue > MAX_CUBE_VERSION_REQUEST_INTERVAL_MINUTES) {
                timeIntervalValue = MAX_CUBE_VERSION_REQUEST_INTERVAL_MINUTES;
            }

            // 把divider和interval加入到map

            if (retVal == null) {
                retVal = new ArrayList<>();
            } else {
                int lastValue = retVal.get(retVal.size() - 1).getKey();
                if (timeDividerValue <= lastValue) {
                    // 格式不正确，啥也不做
                    return null;
                }
            }
            retVal.add(new AbstractMap.SimpleEntry<>(timeDividerValue, timeIntervalValue));
        }
        return retVal;
    }

    public static void onAlarmRequestCube() {
        manualRequestCube();
    }

    private static void manualRequestCube() {
        PullCloudConfig.getInstance().getConfig();
    }

    private static void updateCurrentCubeCheckUpdateInterval(long interval) {
        if (sCurrentCubeCheckUpdateInterval != interval) {
            sCurrentCubeCheckUpdateInterval = interval;
            manualRequestCube();
            scheduleRequestCube();
        }
    }

    private static void scheduleRequestCube() {
        Intent intent = new Intent(ONewsService.ACTION_CUBE_ALARM_REQUEST_CUBE);
        intent.setPackage(MainEntry.getAppContext().getPackageName());
        intent.setComponent(new ComponentName(MainEntry.getAppContext(), ONewsService.class));
        PendingIntent pendingIntent = PendingIntent.getService(MainEntry.getAppContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) MainEntry.getAppContext().getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
        alarm.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + sCurrentCubeCheckUpdateInterval,
                sCurrentCubeCheckUpdateInterval, pendingIntent);
    }
}
