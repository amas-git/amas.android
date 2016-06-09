package com.cmcm.update;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.cmcm.onews.MainEntry;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.ui.widget.CommonsDialog;
import com.cmcm.onews.ui.widget.INewsNotifyDialogClick;
import com.cmcm.onews.ui.widget.NewStyleDialog;
import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.ConflictCommons;
import com.cmcm.onews.util.NetworkUtil;
import com.cmcm.onews.util.UIConfigManager;

import cmcm.com.updatelib.IUpdateChecker;
import cmcm.com.updatelib.UpdateManager;
import cmcm.com.updatelib.UpdateService;

public class UpdateHelper {
    private static UpdateHelper sIns;

    private UpdateHelper() {

    }

    public static UpdateHelper getInstance() {
        if (sIns == null) {
            sIns = new UpdateHelper();
        }
        return sIns;
    }

    public void init() {
        UpdateManager.getIns().setDownloader(new UpdateDownloader());
        UpdateManager.getIns().setChecker(new UpdateChecker());
        UpdateManager.getIns().setDebug(L.DEBUG);
        // then check
        L.update("update sdk init");
    }


    public boolean checkIfCanUpdate() {
        final IUpdateChecker checker = UpdateManager.getIns().getChecker();
        if (checker == null) {
            log("checker is null");
            return false;
        }
        long currentVersion = checker.getCurrentVersion();
        long updateVersion = checker.getUpdateVersion();
        if (currentVersion >= updateVersion) {
            return false;
        }
        final String updateApkMd5 = checker.getUpdateApkMd5();
        if (TextUtils.isEmpty(updateApkMd5)) {
            log("md5 is null");
            return false;
        }
        final String updateApkUrl = checker.getUpdateApkUrl();
        if (TextUtils.isEmpty(updateApkUrl)) {
            log("url is null");
            return false;
        }
        final String packageName = checker.getUpdatePackageName();
        if (TextUtils.isEmpty(packageName)) {
            log("pkg is null");
            return false;
        }
        // 判定开关是否是开，如果是关闭的，那就完全锁死
        if (UpdateCloudConfig.getUpdateSectionCtl() != 1 && UpdateCloudConfig.getUpdateSectionCtl() != 2) {
            log("ctl is not match");
            return false;
        }

        return true;
    }

    private void log(String msg) {
        L.update(msg);
    }

    public void checkIfNeedUpdate() {
        // 这个里边直接用魔方检查了吧
        if (UpdateCloudConfig.getUpdateSectionCtl() == 1 || UpdateCloudConfig.getUpdateSectionCtl() == 2) {
            UpdateService.startUpdateService(MainEntry.getAppContext());
            L.update("update check ctl meet ");
        } else {
            L.update("update check ctl not meet ");

        }
    }

    public void checkUpdate(final Activity activity) {
        final int updateSectionCtl = UpdateCloudConfig.getUpdateSectionCtl();
        if (!(updateSectionCtl == 1 || updateSectionCtl == 2 || updateSectionCtl == 3)) {
            return;
        }
        if (!ConflictCommons.isCNVersion() && updateSectionCtl == 1) {
            return;
        }
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                // 没有网络, 没有网络都要弹窗
                if (!NetworkUtil.isNetworkUp(MainEntry.getAppContext())) {
                    return;
                }
                if (!ConflictCommons.isCNVersion() && updateSectionCtl == 3) {
                    // then show dialog ,go gp
                    if (checkIfGpUpdate()) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                showGpUpdateDialog(activity);
                            }
                        });
                    }
                    return;
                }
                //并且不是强制升级
                if (UIConfigManager.getInstanse(MainEntry.getAppContext()).getLastCheckVersion() == UpdateCloudConfig.getUpdateVersion()
                        && updateSectionCtl != 2) {
                    return;
                }
                //
                if (!checkIfCanUpdate()) {
                    return;
                }
                // 上次忽略的版本号是不是和云端配置的一致
                // 是不是强制升级
                if (updateSectionCtl == 1
                        || updateSectionCtl == 2) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            showUpdateDialog(activity);
                        }
                    });
                }
            }
        });

    }

    private boolean checkIfGpUpdate() {
        final IUpdateChecker checker = UpdateManager.getIns().getChecker();
        if (checker == null) {
            log("checker is null");
            return false;
        }
        if (TextUtils.isEmpty(checker.getUpdateApkUrl())) {
            log("update url is empty");
            return false;
        }
        return true;
    }

    private void showGpUpdateDialog(Activity activity) {
        saveLastShowDialog();
        CommonsDialog newsDialog = new NewStyleDialog(activity, new INewsNotifyDialogClick() {
            @Override
            public void clickContinue() {
                goGpStore();
            }

            @Override
            public void clickCancel() {
            }
        }, UpdateCloudConfig.getUpdateSectionTitle());
        ((NewStyleDialog) (newsDialog)).setSingleChoice(true);
        newsDialog.setCanceledOnTouchOutside(false);
        newsDialog.showDialog();
    }

    private void goGpStore() {
        String url = UpdateCloudConfig.getUpdateSectionUrl();
        NetworkUtil.go2GooglePlay(NewsSdk.INSTAMCE.getAppContext(), url);
    }

    private void showUpdateDialog(Activity activity) {
        saveLastShowDialog();
        CommonsDialog newsDialog = new NewStyleDialog(activity, new INewsNotifyDialogClick() {
            @Override
            public void clickContinue() {
                checkIfNeedUpdate();
            }

            @Override
            public void clickCancel() {
            }
        }, UpdateCloudConfig.getUpdateSectionTitle());
        if (UpdateCloudConfig.getUpdateSectionCtl() == 2) {
            ((NewStyleDialog) (newsDialog)).setSingleChoice(true);
        } else {
            newsDialog.setIfExitWhitBack(false);
        }
        newsDialog.setCanceledOnTouchOutside(false);
        newsDialog.showDialog();
    }

    private void saveLastShowDialog() {
        long version = UpdateCloudConfig.getUpdateVersion();
        UIConfigManager.getInstanse(MainEntry.getAppContext()).setLastCheckVersion(version);
    }
}
