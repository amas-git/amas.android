package com.cmcm.onews.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cmcm.onews.api.ONewsSdk;

/**
 * Created by amas on 11/4/15.
 */
public class ConnectivityChangeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ONewsSdk.onReceive(context, intent);
    }
}
