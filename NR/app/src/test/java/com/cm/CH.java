package com.cm;

import com.cmcm.onews.C;
import com.google.analytics.tracking.android.CampaignTrackingReceiver_nr;

/**
 * Created by amas on 11/4/15.
 */
public class CH {
    /* 老的默认渠道 public static final int CHANNEL_ID_GOOGLE = 200001; */
    public static final int CHANNEL_ID_GOOGLE = 200000;

    public static int getDefaultChannelId() {
        return CHANNEL_ID_GOOGLE;
    }

    public static int getChannelId() {
        return CampaignTrackingReceiver_nr.getChannel(C.getAppContext());
    }
}
