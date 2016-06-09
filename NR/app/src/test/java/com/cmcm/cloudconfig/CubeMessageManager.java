package com.cmcm.cloudconfig;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.push.HandlePushIntentService;
import com.ijinshan.cloudconfig.deepcloudconfig.CloudConfig;
import com.ijinshan.cloudconfig.deepcloudconfig.ConfigInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CubeMessageManager {

    private static CubeMessageManager sInstance = null;

    private final Context mContext;
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            handleMessages();
        }
    };

    private CubeMessageManager(Context context) {
        mContext = context == null ? null : context.getApplicationContext();
    }

    public static synchronized CubeMessageManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CubeMessageManager(context);
        }
        return sInstance;
    }

    private void handleMessages() {
        List<String> messages = getMessages();
        if (messages != null && messages.size() > 0) {
            for (final String message : messages) {
                Intent intent = new Intent();
                intent.setPackage(mContext.getPackageName());
                intent.setComponent(new ComponentName(mContext, HandlePushIntentService.class));
                intent.putExtra(HandlePushIntentService.INTENT_EXTRA_MESSAGE_CONTENT, message);
                try {
                    mContext.startService(intent);
                } catch (Exception e) {
                    // pass
                }
            }
        }
    }

    private ArrayList<String> getMessages() {
        List<ConfigInfo> configInfos = CloudConfig.getInstance().getConfigInfoList(
                CloudConfigKey.FUCTION_TYPE_PUSH_MESSAGE, CloudConfigKey.CLOUD_SECTION_KEY_PUSH_MESSAGE);
        ArrayList<String> messages = new ArrayList<>();

        if (configInfos != null && !configInfos.isEmpty()) {
            for (ConfigInfo configInfo : configInfos) {

                // 解析魔方数据
                String rawContent = configInfo.getData();
                if (TextUtils.isEmpty(rawContent)) {
                    continue;
                }

                JSONObject contentData;
                try {
                    contentData = new JSONObject(rawContent);
                } catch (JSONException e) {
                    continue;
                }

                // 获取消息内容
                String content = contentData.optString(CloudConfigKey.CLOUD_SUBKEY_PUSH_MESSAGE_CONTENT, "");
                if (!TextUtils.isEmpty(content)) {
                    messages.add(content);
                }
            }
        }
        return messages;
    }

    public void handleMessagesAsync() {
        // 读取魔方内容中包含的消息
        BackgroundThread.removeTask(mRunnable);
        BackgroundThread.post(mRunnable);
    }
}
