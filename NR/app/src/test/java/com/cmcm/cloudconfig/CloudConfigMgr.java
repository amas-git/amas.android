package com.cmcm.cloudconfig;

import com.cmcm.config.NewsConfigMgr;

/**
 * Created by Jason.Su on 2016/3/16.
 * com.cmcm.cloudconfig
 * des:
 * email:suyanjiao@conew.com
 * 1553202383
 */
public class CloudConfigMgr {
    private CloudConfigMgr() {

    }

    private static class SingleTonHolder {
        private static CloudConfigMgr sIn = new CloudConfigMgr();
    }

    public static CloudConfigMgr getIns() {
        return SingleTonHolder.sIn;
    }

    public void init() {
        NewsConfigMgr.getIns().setConfig(new NewsCloudConfig());
    }

    private static class NewsCloudConfig implements NewsConfigMgr.Config {
        @Override
        public int getCastRefreshCommenthDuring() {
            return CloudConfigGetter.getIntValue(CloudConfigKey.FUCTION_TYPE_MAIN, CloudConfigKey.CAST_SECTION, CloudConfigKey.CAST_SECTION_COMMENT, 5);

        }

        public int getCastRefreshDrectDuring() {
            return CloudConfigGetter.getIntValue(CloudConfigKey.FUCTION_TYPE_MAIN, CloudConfigKey.CAST_SECTION, CloudConfigKey.CAST_SECTION_DIRECT, 5);

        }

        public int getCastRefreshUiDuring() {
            return CloudConfigGetter.getIntValue(CloudConfigKey.FUCTION_TYPE_MAIN, CloudConfigKey.CAST_SECTION, CloudConfigKey.CAST_SECTION_UI, 5);

        }

        @Override
        public int getCastShareStart() {
            return CloudConfigGetter.getIntValue(CloudConfigKey.FUCTION_TYPE_MAIN, CloudConfigKey.CAST_SECTION, CloudConfigKey.CAST_SECTION_SHARE_START, 1);
        }

        @Override
        public int getCastShareContinued() {
            return CloudConfigGetter.getIntValue(CloudConfigKey.FUCTION_TYPE_MAIN, CloudConfigKey.CAST_SECTION, CloudConfigKey.CAST_SECTION_SHARE_CONTINUED, 10);
        }

        @Override
        public String getCastShareHI() {
            return CloudConfigGetter.getStringValue(CloudConfigKey.FUCTION_TYPE_MAIN, CloudConfigKey.CAST_SECTION, CloudConfigKey.CAST_SECTION_SHARE_HI, null);
        }

        @Override
        public String getCastShareEN() {
            return CloudConfigGetter.getStringValue(CloudConfigKey.FUCTION_TYPE_MAIN, CloudConfigKey.CAST_SECTION, CloudConfigKey.CAST_SECTION_SHARE_EN, null);
        }

        @Override
        public boolean getCastCommentrayEnabled() {
            return CloudConfigGetter.getBooleanValue(CloudConfigKey.FUCTION_TYPE_MAIN, CloudConfigKey.CAST_SECTION, CloudConfigKey.CAST_SECTION_COMMENTRAY, false);
        }

        @Override
        public String getCastLiveTabsHI() {
            return CloudConfigGetter.getStringValue(CloudConfigKey.FUCTION_TYPE_MAIN, CloudConfigKey.CAST_SECTION, CloudConfigKey.CAST_SECTION_LIVE_TABS_HI, null);
        }

        @Override
        public String getCastLiveTabsEN() {
            return CloudConfigGetter.getStringValue(CloudConfigKey.FUCTION_TYPE_MAIN, CloudConfigKey.CAST_SECTION, CloudConfigKey.CAST_SECTION_LIVE_TABS_EN, null);
        }

        @Override
        public int getCastLiveIndex() {
            return CloudConfigGetter.getIntValue(CloudConfigKey.FUCTION_TYPE_MAIN, CloudConfigKey.CAST_SECTION, CloudConfigKey.CAST_SECTION_LIVE_INDEX, -1);
        }

    }


}
