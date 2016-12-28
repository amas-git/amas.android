package com.example.secondtestfan;

import android.app.Activity;
import android.os.Bundle;

import com.example.secondtestfan.ColorBackLayout.OnColorChangeListener;
import com.example.secondtestfan.MainCircleFan.OnClickCleanListener;

public class MainActivity extends Activity {
	
	private ColorBackLayout mColorBack;
	private MainCircleFan mFan;
//	private BaseMainIcon mIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mColorBack = (ColorBackLayout) findViewById(R.id.color_back);
		mFan = (MainCircleFan) findViewById(R.id.fan);
//		mIcon = (BaseMainIcon)findViewById(R.id.icon);
		mColorBack.setGoal(50,false);
		
//		mFan.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				mIcon.setGoal(70);
//			}
//		}, 2000);
		
//		mColorBack.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				mColorBack.setGoal(50,true);
//				mFan.setShowCurrentGoal(50);
//			}
//		}, 2000);
//		
//		mColorBack.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				mFan.setFinishCalculating();
//			}
//		}, 3000);
		
		mFan.setShowTeaching(true);
		mFan.setCalculatingStat();
		
		mFan.setOnClickCleanListener(new OnClickCleanListener() {
			
			@Override
			public void onClick() {
				mFan.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mFan.setShowCurrentGoal(90);
						mFan.setCleanFinished();
						mColorBack.setGoal(90,true);
					}
				}, 3000);
			}
		});
		
		mColorBack.setOnColorChangeListener(new OnColorChangeListener() {
			
			@Override
			public void onColorChanged(int rgb[],int goal) {
				mFan.onChangeColor(rgb,goal);
			}
		});
	}

}
