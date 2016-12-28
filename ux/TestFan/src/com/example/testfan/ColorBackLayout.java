package com.example.testfan;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class ColorBackLayout extends RelativeLayout {
	
	private final int[] RgbBlue = new int[]{50,97,180};
	private final int[] RgbGreen = new int[]{20,158,92};
	private final int[] RgbYellow = new int[]{198,158,51};
	private final int[] RgbRed = new int[]{220,68,57};
	
	private final int[] mCurrentRGB = new int[]{RgbBlue[0],RgbBlue[1],RgbBlue[2]};
	
	private static final int DURATION = 5000;
	
	private ValueAnimator mRedAnimator;
	private ValueAnimator mGreenAnimator;
	private ValueAnimator mBlueAnimator;
	
	public ColorBackLayout(Context context) {
		this(context, null);
	}

	public ColorBackLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		mRedAnimator = new ValueAnimator();
		mRedAnimator.setIntValues(RgbBlue[0],RgbGreen[0],RgbYellow[0],RgbRed[0]);
		mRedAnimator.setDuration(DURATION);
		mRedAnimator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int red = (Integer) arg0.getAnimatedValue();
				mCurrentRGB[0] = red;
				invalidate();
			}
		});
		
		mGreenAnimator = new ValueAnimator();
		mGreenAnimator.setIntValues(RgbBlue[1],RgbGreen[1],RgbYellow[1],RgbRed[1]);
		mGreenAnimator.setDuration(DURATION);
		mGreenAnimator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int green = (Integer) arg0.getAnimatedValue();
				mCurrentRGB[1] = green;
			}
		});
		
		mBlueAnimator = new ValueAnimator();
		mBlueAnimator.setIntValues(RgbBlue[2],RgbGreen[2],RgbYellow[2],RgbRed[2]);
		mBlueAnimator.setDuration(DURATION);
		mBlueAnimator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int blue = (Integer) arg0.getAnimatedValue();
				mCurrentRGB[2] = blue;
			}
		});
    }

	@Override
	protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    canvas.drawARGB(255, mCurrentRGB[0], mCurrentRGB[1], mCurrentRGB[2]);
	}

	public void setGoal(int i) {
		long currentTime = (long) ((float)(100-i)/100 * DURATION);
		mRedAnimator.setCurrentPlayTime(currentTime);
		mGreenAnimator.setCurrentPlayTime(currentTime);
		mBlueAnimator.setCurrentPlayTime(currentTime);
    }
}
