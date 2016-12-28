package com.cleanmaster.ui.resultpage.storage;

import java.util.ArrayList;
import java.util.Collection;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class ResultPageService extends IntentService {
	public static final String ACTION_ADD_CLEAN_RESULT = "com.cleanmaster.mguard_cn.ACTION_ADD_CLEAN_RESULT";

	public ResultPageService() {
		super("ResultPageService");
	}
	
	public static void start_ACTION_ADD_CLEAN_RESULT(Context context, Collection<PackageCleanResult> results) {
		ArrayList<PackageCleanResult> xs = new ArrayList<PackageCleanResult>();
		xs.addAll(results);
		if(xs.isEmpty()) {
			return;
		}
		
		Intent intent = new Intent();
		intent.setClass(context, ResultPageService.class);
		intent.putExtra(":result", xs);
		intent.setAction(ACTION_ADD_CLEAN_RESULT);
		context.startService(intent);
	}

	public static void start_ACTION_ADD_CLEAN_RESULT(Context context, PackageCleanResult result) {
		ArrayList<PackageCleanResult> xs = new ArrayList<PackageCleanResult>();
		xs.add(result);
		start_ACTION_ADD_CLEAN_RESULT(context, xs );
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		if (false) {
		} else if (ACTION_ADD_CLEAN_RESULT.equals(action)) {
			onHandle_ACTION_ADD_CLEAN_RESULT(intent);
		}
	}

	private void onHandle_ACTION_ADD_CLEAN_RESULT(Intent intent) {
		ArrayList<PackageCleanResult> xs = null;
		if(intent.hasExtra(":result")) {
			Object obj = intent.getSerializableExtra(":result");
			if(obj instanceof ArrayList<?>) {
				xs = (ArrayList<PackageCleanResult>)obj;
			}
		}
		ResultPageStorage.getInstance().addCleanResult(xs);
	}
}