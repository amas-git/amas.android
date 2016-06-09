/**
 * Created by Jason.Su on 2016/2/18.
 * com.cmcm.update
 * des:
 * email:suyanjiao@conew.com
 * 1553202383
 */
package com.cmcm.update;

import android.net.Uri;
import android.text.TextUtils;

import com.cm.util.Md5Util;
import com.cmcm.onews.MainEntry;
import com.cmcm.onews.util.DeviceUtils;

import java.io.File;

import cmcm.com.updatelib.IUpdateChecker;


public class UpdateChecker implements IUpdateChecker {
    @Override
    public String getUpdateApkUrl() {
        return UpdateCloudConfig.getUpdateSectionUrl();
    }

    @Override
    public long getCurrentVersion() {
        try {
            return DeviceUtils.getVersionCode(MainEntry.getAppContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long getUpdateVersion() {
        return (UpdateCloudConfig.getUpdateVersion());
    }

    @Override
    public String getUpdateApkMd5() {
        return UpdateCloudConfig.getUpdateSectionMd5();
    }

    @Override
    public String getUpdatePackageName() {
        return UpdateCloudConfig.getUpdateSectionPkg();
    }

    @Override
    public String getFileMd5(Uri updateApkUri) {
        if (updateApkUri == null) {
            return null;
        }
        final String path = updateApkUri.getPath();
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return Md5Util.getFileMD5(new File(updateApkUri.getPath()));
    }

    @Override
    public boolean canInstall() {
        if (UpdateCloudConfig.getUpdateSectionCtl() == 1 || UpdateCloudConfig.getUpdateSectionCtl() == 2) {
            return true;
        }
        return false;
    }
}