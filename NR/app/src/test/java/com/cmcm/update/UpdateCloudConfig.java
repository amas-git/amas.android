/**
 * Created by Jason.Su on 2016/2/22.
 * com.cmcm.update
 * des:
 * email:suyanjiao@conew.com
 * 1553202383
 */
package com.cmcm.update;

import com.cmcm.cloudconfig.CloudConfigGetter;


public class UpdateCloudConfig {
    // md5
    //apk url
    // version
    // force update ctl 1,手动升级 2， 强制升级  3, gp 升级 其他的不升级
    //
    private final static int FUCTION_TYPE = 2;
    private final static String UPDATE_SECTION_KEY = "update_section_key";
    private final static String UPDATE_SECTION_VERSION = "update_section_version";
    private final static String UPDATE_SECTION_MD5 = "update_section_md5";
    private final static String UPDATE_SECTION_URL = "update_section_url";
    private final static String UPDATE_SECTION_CTL = "update_section_ctl";
    private final static String UPDATE_SECTION_PKG = "update_section_pkg";
    private final static String UPDATE_SECTION_TITLE="update_section_title";


    // 这个必须是整数类型的
    public static long getUpdateVersion() {
        return CloudConfigGetter.getLongValue(FUCTION_TYPE, UPDATE_SECTION_KEY, UPDATE_SECTION_VERSION, 0);
    }

    public static String getUpdateSectionMd5() {
        return CloudConfigGetter.getStringValue(FUCTION_TYPE, UPDATE_SECTION_KEY, UPDATE_SECTION_MD5, "");
    }

    public static String getUpdateSectionUrl() {
        return CloudConfigGetter.getStringValue(FUCTION_TYPE, UPDATE_SECTION_KEY, UPDATE_SECTION_URL, "");
    }

    // 默认不升级
    public static int getUpdateSectionCtl() {
        return CloudConfigGetter.getIntValue(FUCTION_TYPE, UPDATE_SECTION_KEY, UPDATE_SECTION_CTL, 0);
    }

    public static String getUpdateSectionPkg() {
        return CloudConfigGetter.getStringValue(FUCTION_TYPE, UPDATE_SECTION_KEY, UPDATE_SECTION_PKG, "");
    }

    public static String getUpdateSectionTitle(){
        final String value = CloudConfigGetter.getStringValue(FUCTION_TYPE, UPDATE_SECTION_KEY, UPDATE_SECTION_TITLE, "");
        final String replace = value.replace("\\n", "\n");
        return replace;
    }
}