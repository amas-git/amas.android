package com.example.testfan;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.example.testfan.FanGoalView.OnGoalChangeListener;
import com.example.testfan.FanGoalView.STAT;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class MainActivity extends Activity {
	
	private FanGoalView mGoal;
	private ColorBackLayout mColorBack;
	private TextView mTextGoal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGoal = (FanGoalView) findViewById(R.id.goal);
		mColorBack = (ColorBackLayout) findViewById(R.id.color_back);
		mTextGoal = (TextView) findViewById(R.id.text);
//		
//		Typeface fontFace = Typeface.createFromAsset(getAssets(),
//                        "number.ttf");
//		mTextGoal.setTypeface(fontFace);
		
		playGoalAnim();
		mGoal.setGoal(100);
		mGoal.setCurrentStat(STAT.SCANING);
		mGoal.setOnGoalChangeListener(new OnGoalChangeListener() {
			
			@Override
			public void onChange(int goal) {
				mColorBack.setGoal(goal);
//				mTextGoal.setText(String.valueOf(goal));
			}

			@Override
            public void onAnimFinish() {
				int current = mGoal.getCurrentGoal();
				if(current == 100){
					mGoal.setGoal(0);
				}
				
				if(current == 0){
					mGoal.setGoal(100);
				}
//				mTextGoal.setText(String.valueOf(current));
            }
		});
		
//		mGoal.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				mGoal.setCurrentStat(STAT.NORMAL);
//				mGoal.invalidate();
//				mGoal.setGoal(50);
//			}
//		}, 4000);
//		
//		mGoal.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				mGoal.setGoal(20);
//				mGoal.setCurrentStat(STAT.IDEL);
//			}
//		}, 6000);
	}

	private static final long INIT_ANIM_DURATION = 800;
	private void playGoalAnim() {
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(mGoal, "scaleX", 0.0F,1F);
		scaleX.setDuration(INIT_ANIM_DURATION);
		scaleX.setInterpolator(new OvershootInterpolator(2f));
		
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(mGoal, "scaleY", 0.0F,1F);
		scaleY.setDuration(INIT_ANIM_DURATION);
		scaleY.setInterpolator(new OvershootInterpolator(2f));
		
		ObjectAnimator alpha = ObjectAnimator.ofFloat(mGoal, "alpha", 0.0F,1F);
		alpha.setDuration(INIT_ANIM_DURATION);
		
		AnimatorSet set = new AnimatorSet();
		set.playTogether(scaleX,scaleY,alpha);
		set.start();
		
		ObjectAnimator scaleTX = ObjectAnimator.ofFloat(mTextGoal, "scaleX", 0.0F,1F);
		scaleX.setDuration(INIT_ANIM_DURATION);
		
		ObjectAnimator scaleTY = ObjectAnimator.ofFloat(mTextGoal, "scaleY", 0.0F,1F);
		scaleY.setDuration(INIT_ANIM_DURATION);
		
		ObjectAnimator alphaT = ObjectAnimator.ofFloat(mTextGoal, "alpha", 0.0F,1F);
		alpha.setDuration(INIT_ANIM_DURATION);
		
		AnimatorSet setT = new AnimatorSet();
		setT.playTogether(scaleTX,scaleTY,alphaT);
		setT.start();
    }
	
	
}
