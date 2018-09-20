package org.whitetree.systable.system;

import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.systable.LOG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class SysActionReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			LocalService.startDefault(context);
		} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
			NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			String typeName = info.getTypeName();
			String subtypeName = info.getSubtypeName();
			State state = info.getState();
			LOG._(typeName + "/" + subtypeName + " : " + state.toString());
			//boolean noConnection = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		}
	}
}
