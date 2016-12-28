package com.example.testfloatwindow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class FloatActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_float);

		// 启动悬浮窗
		Intent service = new Intent();
		service.setClass(this, FloatService.class);
		startService(service);// 启动服务
		finish();
	}

}
