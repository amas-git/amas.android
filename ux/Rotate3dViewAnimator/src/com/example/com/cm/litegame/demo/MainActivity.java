package com.example.com.cm.litegame.demo;

import java.util.ArrayList;
import java.util.List;

import com.cleanmaster.ui.resultpage.storage.PackageCleanResult;
import com.cleanmaster.ui.resultpage.storage.ResultPageService;
import com.cleanmaster.ui.resultpage.storage.ResultPageStorage;
import com.cm.kinfoc.api.GameTracer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GameTracer.getInstance().init(this);
		GameTracer.getInstance().setCpId("YOUR_CP_ID");
		GameTracer.getInstance().onEnterApp(); // 进入游戏
		setContentView(R.layout.activity_main);
	}
	
	public void onSendEvent(View v) {
		//GameTracer.getInstance().sendEvent(1,"hello world!");
		ResultPageStorage.getInstance().init(this);
		
		List<PackageCleanResult> xs = new ArrayList<PackageCleanResult>();
				
		for(int i=0; i<10; ++i) {
			xs.add(PackageCleanResult.create("package.i."+i, 1));
		}
		
		//ResultPageStorage.getInstance().addCleanResult(xs);
		ResultPageService.start_ACTION_ADD_CLEAN_RESULT(this, xs);
	}
	@Override
	protected void onDestroy() {
		GameTracer.getInstance().onExitApp(); // 退出游戏
		super.onDestroy();
	}
}
