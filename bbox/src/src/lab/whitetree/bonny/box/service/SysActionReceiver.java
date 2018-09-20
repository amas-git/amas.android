package lab.whitetree.bonny.box.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

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
			_(typeName + "/" + subtypeName + " : " + state.toString());
			boolean noConnection = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		}

	}

	private void _(String msg) {
		Log.d("SysActionReceiver", "------> " + msg);
	}
}
