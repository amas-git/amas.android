package com.cmcm.onews.api;

import android.content.Context;
import android.content.Intent;

import com.cmcm.onews.service.LocalService;
import com.cmcm.onews.service.NetworkStateRecordService;

/**
 * Created by amas on 11/14/15.
 */
public class ONewsSdk {
    public static void onReceive(Context context, Intent intent) {
        LocalService.start_ACTION_ON_RECEIVE(context, intent);

        NetworkStateRecordService.start_ACTION_ON_NETWORK_CHANGE(context, intent);
    }
}
