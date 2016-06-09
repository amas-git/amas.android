package com.cmcm.feedback.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;

import com.cm.kinfoc.FormFile;
import com.cm.kinfoc.KHttpPoster;
import com.cm.util.ZipUtils;
import com.cmcm.onews.MainEntry;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.util.ConflictCommons;
import com.cmcm.onews.util.DeviceUtils;
import com.cmcm.onews.util.FileUtils;
import com.cmcm.onews.util.UIConfigManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jason.Su on 2016/3/30.
 * com.cmcm.feedback
 * des:
 * email:suyanjiao@conew.com
 * 1553202383
 */
public class FeedBackHelper {

//    app_id          int        无         是         系统分配的唯一id标示
//    chanel          int        0          否         上报渠道：0客户端上报，1客服人员反馈，2邮件反馈，3GP反馈
//    type            string     feedback   否         反馈类型，feedback一般反馈，uninstall卸载反馈
//    model           string     无         否         设备型号
//    sysversion      string     无         否         系统版本
//    uuid            string     无         否         uuid
//    version         string     无         否         客户端版本
//    contact         string     无         否         用户联系方式
//    syslang         string     无         否         系统语言
//    content         text       无         否         反馈主体内容
//    havelog         string     no         否         是否有日志：yes有，no无
//    log             file       无         否         日志
//    haveimage       string     no         否         是否有图片：yes有，no无
//    image_num       int        0          否         图片张数
//    image_*         file       无         否         图片，其中*位数字，标示第几张图片
//    auto_category   string     无         否         自动分类id，系统将根该字段对反馈进行分类，故此id必须为反馈平台中的分类id，否则不做处理，本字段对老接口不生效（即cm和locker无需上报本字段）

    public static String uploadLog(Intent intent) {
        if (intent == null) {
            return "";
        }
        FeedBackDataBean dataBean = (FeedBackDataBean) intent.getSerializableExtra(":feedback_data");
        if (dataBean == null) {
            return "";
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("app_id", "20");
        params.put("content", dataBean.getContent());
        params.put("contact", dataBean.getContact());
        params.put("model", Build.MODEL);
        params.put("sysversion", Build.VERSION.RELEASE);
        params.put("version", String.valueOf(getVersionCode(MainEntry.getAppContext())));
        params.put("uuid", DeviceUtils.getAndroidId(MainEntry.getAppContext()));
        params.put("syslang", UIConfigManager.getInstanse(MainEntry.getAppContext()).getLanguageSelected(MainEntry.getAppContext()).getLanguage());

        List<String> uploadImagePaths = dataBean.getPicPaths();

        FormFile[] files = new FormFile[4];

        FormFile log = new FormFile();
        File file = null;
        if (null != getZipPath()) {
            file = new File(getZipPath());
            if (file.length() > 2L * 1024 * 1024) {
                file = null;
            }
        }
        // 无论如何都要删除 zip
        zipLogs();
        if (file != null && file.exists()) {
            log.setFile(file);
        }
        log.setFormName("log");
        if (log.getFile() != null && log.getFile().exists() && log.getFile().length() > 0) {
            files[0] = log;
            params.put("havelog", "yes");
            params.put("log","news.log.zip");
        }

        if (null != uploadImagePaths && uploadImagePaths.size() > 0) {
            for (int i = 0; i < uploadImagePaths.size(); i++) {
                String path = uploadImagePaths.get(i);
                if (TextUtils.isEmpty(path)) {
                    continue;
                }
                FormFile image = new FormFile();
                image.setFile(new File(path));
                image.setFormName(getName(i));
                if (image.getFile().exists() && image.getFile().length() > 0) {
                    files[i + 1] = image;
                    L.feedback("feedback imges"+i);
                    params.put("haveimage", "yes");
                    params.put("image_num", String.valueOf(i + 1));
                }
            }
        }
        L.feedback("upload start");

        String result = "";
        if (ConflictCommons.isCNVersion()) {
            result = KHttpPoster.post(FeedbackConfig.UPLOAD_FEEDBACK_URL_CN, params, files);
        } else {
            result = KHttpPoster.postHttps(FeedbackConfig.UPLOAD_FEEDBACK_URL, params, files);
        }
        L.feedback("upload result"+result);
        return result;
    }

    private static String getName(int i) {
        switch (i) {
            case 0:
                return "image_0";
            case 1:
                return "image_1";
            case 2:
                return "image_2";
            default:
                return "image_0";
        }
    }

    private static int getVersionCode(Context context) {
        ComponentName cn = new ComponentName(context, context.getClass());
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    cn.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getLogsPath() {
        return OpLog.getLogFileTempPath()+"/logs";
    }


    private static  String getZipPath(){
        return OpLog.getLogFileTempPath()+"/news.log.zip";
    }
    public static void zipLogs(){
        final File file = FileUtils.checkFile(getZipPath());
        if (file != null) {
            FileUtils.deleteFile(getZipPath());
        }
        File zipTarget = new File(getZipPath());
        try {
            zipTarget.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            L.feedback(getLogsPath());
            ZipUtils.zip(getLogsPath(),getZipPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
