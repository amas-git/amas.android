package com.cm.util;

import android.content.Context;
import android.content.Intent;

public class CommonUtils {

    // 使用此方法打开外部activity,避免外部activity不存在而造成崩溃，
    public static boolean startActivity(Context context, Intent intent) {
        boolean bResult = true;
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            bResult = false;
        }
        return bResult;
    }
}
