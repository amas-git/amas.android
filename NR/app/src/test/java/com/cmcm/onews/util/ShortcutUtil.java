package com.cmcm.onews.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;

import com.cmcm.onews.NewsL;
import com.cmcm.onews.R;
import com.cmcm.onews.ui.NewsBaseActivity;


/**
 * 快捷方式
 * Created by yuanshouhui on 2015/11/21.
 */
public class ShortcutUtil {
    /**
     * 为当前应用添加桌面快捷方式
     *
     * @param cx
     *
     */
    public static void addShortcut(Context cx) {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

        Intent shortcutIntent = cx.getPackageManager()
                .getLaunchIntentForPackage(cx.getPackageName());
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        shortcutIntent.putExtra(NewsBaseActivity.KEY_FROM, NewsBaseActivity.FROM_SHOTCUT);

        // 获取当前应用名称
        String title = null;
        try {
            final PackageManager pm = cx.getPackageManager();
            title = pm.getApplicationLabel(
                    pm.getApplicationInfo(cx.getPackageName(),
                            PackageManager.GET_META_DATA)).toString();
        } catch (Exception e) {
        }
        // 快捷方式名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        // 不允许重复创建（不一定有效）
        shortcut.putExtra("duplicate", false);
        // 快捷方式的图标
        int ic_launcher = -1;//R.drawable.onews__ic_launcher;
        if(ConflictCommons.isCNVersion()){
                 /* BUILD_CTRL:IF:CNVERSION */
            ic_launcher = R.drawable.onews__ic_launcher_cn;
                /* BUILD_CTRL:ENDIF:CNVERSION */
        }else{
               /* BUILD_CTRL:IF:OU_VERSION_ONLY */
            ic_launcher = R.drawable.onews__ic_launcher;
                /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
        }
        Parcelable iconResource = Intent.ShortcutIconResource.fromContext(cx, ic_launcher);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
        cx.sendBroadcast(shortcut);
        NewsL.shorcut("shortcutIntent=" + shortcutIntent);
    }

    /**
     * 删除当前应用的桌面快捷方式
     *
     * @param cx
     */
    public static void delShortcut(Context cx) {
        Intent shortcut = new Intent(
                "com.android.launcher.action.UNINSTALL_SHORTCUT");

        // 获取当前应用名称
        String title = null;
        try {
            final PackageManager pm = cx.getPackageManager();
            title = pm.getApplicationLabel(
                    pm.getApplicationInfo(cx.getPackageName(),
                            PackageManager.GET_META_DATA)).toString();
        } catch (Exception e) {
        }
        // 快捷方式名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        Intent shortcutIntent = cx.getPackageManager()
                .getLaunchIntentForPackage(cx.getPackageName());
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        cx.sendBroadcast(shortcut);
    }

    /**
     * 判断桌面是否已添加快捷方式
     *
     * @param cx
     *
     * @return
     */
    public static boolean hasShortcut(Context cx) {
        boolean result = false;
        // 获取当前应用名称
        String title = null;
        try {
            final PackageManager pm = cx.getPackageManager();
            title = pm.getApplicationLabel(
                    pm.getApplicationInfo(cx.getPackageName(),
                            PackageManager.GET_META_DATA)).toString();
        } catch (Exception e) {
        }

        final String uriStr;
        if (android.os.Build.VERSION.SDK_INT < 8) {
            uriStr = "content://com.android.launcher.settings/favorites?notify=true";
        } else {
            uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
        }
        final Uri CONTENT_URI = Uri.parse(uriStr);
        final Cursor c = cx.getContentResolver().query(CONTENT_URI, null,
                "title=?", new String[] { title }, null);
        if (c != null && c.getCount() > 0) {
            result = true;
        }
        return result;
    }
}
