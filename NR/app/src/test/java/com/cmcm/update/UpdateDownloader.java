package com.cmcm.update;

import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cm.util.Md5Util;
import com.cmcm.onews.MainEntry;
import com.cmcm.onews.R;
import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.FileUtils;
import com.cmcm.onews.util.NetworkUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import cmcm.com.updatelib.IUpdateDownloader;

public class UpdateDownloader implements IUpdateDownloader {
    private final static String path = "update.apk";

    @Override
    public DownloadState getDownloadState() {
        // check state
        final String md5 = Md5Util.getFileMD5(new File(getUpdateApkUri().getPath()));
        if (UpdateCloudConfig.getUpdateSectionMd5().equalsIgnoreCase(md5)) {
            return DownloadState.Success;
        }
        return DownloadState.Unkown;
    }

    // 等等这里要不要修改一下呢
    private File getTargetPath() {
        // 先下载在sdcard ,这样子比较保险
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            final File storageDirectory = Environment.getExternalStorageDirectory();
            return new File(storageDirectory, path);
        } else {
            final File cacheDirectory = Environment.getDownloadCacheDirectory();
            return new File(cacheDirectory, path);
        }
    }

    private void log(String content) {
        Log.e("update_apk_version", content);
    }

    @Override
    public boolean download(String urlString, DownloadListener downloadListener) {
        if (TextUtils.isEmpty(urlString)) {
            return false;
        }
        log("start to update download");
        FileOutputStream fos = null;
        try {
            if (!NetworkUtil.isNetworkUp(MainEntry.getAppContext())) {
                BackgroundThread.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainEntry.getAppContext(), R.string.onews__offline_no_network, Toast.LENGTH_LONG).show();
                    }
                });

            }
            URL url = new URL(urlString);
            final URLConnection connection = url.openConnection();
            final InputStream inputStream = connection.getInputStream();
            final File targetPath = getTargetPath();
            log("download path" + targetPath.getPath());
            // 下载成功了，就啥都不搞了
            if (getDownloadState() == DownloadState.Success) {
                if (downloadListener != null) {
                    downloadListener.onSuccess();
                }
                return true;
            }
            // 下载没有成功，就删除了，下次在搞
            FileUtils.checkFileAndDelete(targetPath.getPath());
            File file = targetPath;
            file.createNewFile();
            fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), path));
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.flush();
            if (downloadListener != null) {
                downloadListener.onSuccess();
            }
            log("download success");
        } catch (Exception e) {
            e.printStackTrace();
            log("down meets a problem");
            if (downloadListener != null) {
                downloadListener.onFailed();
            }
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean download(Uri uri, DownloadListener downloadListener) {
        return download(uri.getPath(), downloadListener);
    }

    @Override
    public boolean cancelDownload(Uri uri, DownloadListener downloadListener) {
        return false;
    }

    @Override
    public Uri getUpdateApkUri() {
        return Uri.fromFile(getTargetPath());
    }
}
