package com.cmcm.cloudconfig;

import com.ijinshan.cloudconfig.deepcloudconfig.CloudConfigExtra;

/**
 * Created by Jason.Su on 2016/2/22.
 * com.cmcm.cloudconfig
 * des: 魔方的包装类，防止接口变化
 * email:suyanjiao@conew.com
 * 1553202383
 */
public class CloudConfigGetter {
    public static String getStringValue(int func_tion, String section, String key, String defValue) {
       return CloudConfigExtra.getStringValue(func_tion,section,key,defValue);
    }

    public static int getIntValue(int func_tion, String section, String key, int defValue) {
        return CloudConfigExtra.getIntValue(func_tion,section,key,defValue);
    }

    public static long getLongValue(int func_tion, String section, String key, long defValue) {
        return CloudConfigExtra.getLongValue(func_tion,section,key,defValue);
    }

    public static boolean getBooleanValue(int func_tion, String section, String key, boolean defValue) {
        return CloudConfigExtra.getBooleanValue(func_tion,section,key,defValue);
    }

}
