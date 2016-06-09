package com.cmcm.onews.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cmcm.onews.sdk.NewsSdk;

/**
 * Created by amas on 12/14/15.
 */
public class MainReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NewsSdk.INSTAMCE.onReceive(context, intent);
    }
}
