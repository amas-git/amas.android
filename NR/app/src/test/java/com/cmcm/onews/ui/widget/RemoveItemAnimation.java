package com.cmcm.onews.ui.widget;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

public class RemoveItemAnimation {
	private int mHeight;
	private View mView;
	
	public RemoveItemAnimation(final View view){
		mView = view;
		mHeight = mView.getHeight();
		
		mView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				if(1 == mView.getHeight()){
					mView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					onAnimFinish();
				}
			}
		});
	}

	protected void onAnimFinish(){
		
	}

	public void load() {
		animTransLeft();
	}
	
	
	private void animTransLeft() {
		TranslateAnimation animation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_PARENT, 0f,
						TranslateAnimation.RELATIVE_TO_PARENT, -1f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0f,
						TranslateAnimation.RELATIVE_TO_PARENT, 0f);
		animation.setDuration(300);
		animation.setFillEnabled(true);
		animation.setFillAfter(true);
		animation.setFillBefore(true);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				animHeight();
			}
		});
		mView.startAnimation(animation);
    }

	private void animHeight(){
		ValueAnimator animation = new ValueAnimator();
		animation.setIntValues(mHeight, 1);
		animation.setDuration(200);
		animation.setInterpolator(new LinearInterpolator());
		animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (Integer) animation.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
				if(null == layoutParams){
					mView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, value));
				}else{
					layoutParams.height = value;
					mView.setLayoutParams(layoutParams);
				}
			}
		});
		animation.start();
	}
}
