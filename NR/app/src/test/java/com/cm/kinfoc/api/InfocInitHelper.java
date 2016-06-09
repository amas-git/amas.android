package com.cm.kinfoc.api;

import android.content.Context;

import com.cm.kinfoc.base.InfocCommonBase;
import com.cm.kinfoc.base.InfocServerControllerBase;

public class InfocInitHelper {
    /**
     * infoc相关模块数据初始化入口,需要在Application启动的时候初始化
     * @param context
     */
    public static void init(final Context context) {
        InfocCommonBase.setInstance(new InfocCommonImp(context));
        InfocServerControllerBase.setInfocServerControllerInstance(new InfocServerControllerImp());
    }
}
