package com.cmcm.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver{

	public static final String ALARM_UPDATE_LOGIN_DATA = "com.cleanmaster.service.ALARM_UPDATE_LOGIN_DATA";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(null != intent && ALARM_UPDATE_LOGIN_DATA.equalsIgnoreCase(intent.getAction())){
			LoginDataHelper.getInstance().log("onReceive\t" + ALARM_UPDATE_LOGIN_DATA);
//			LoginService.start_ACTION_FRESH_USER_INFO(context);
		}
	}
}
