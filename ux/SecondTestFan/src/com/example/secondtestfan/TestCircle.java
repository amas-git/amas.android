package com.example.secondtestfan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class TestCircle extends View{
	
	private Paint mCirclePaint;
	private Paint mPaint;
	private Path mPath;
	private Path mClipPath;
	private int mCurrentSweep = 0;
	private RectF mRectF;
	
	private float mWidth = 0;
	private float mHeight = 0;
	
	private int mGoal = 80;

	public TestCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mCirclePaint = new Paint();
		mCirclePaint.setColor(0x880000ff);
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setDither(true);
		mCirclePaint.setStyle(Paint.Style.FILL);
		
		mPaint = new Paint();
		mPaint.setColor(0x33ffffff);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.FILL);
		
		
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				
				mWidth = getWidth();
				mHeight = getHeight();
				mRectF = new RectF(0,0,mWidth,mHeight);
				
				mClipPath = new Path();
				mClipPath.addCircle(mWidth/2, mWidth/2, mWidth/2, Direction.CW);
				mClipPath.close();
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		
		ValueAnimator value = new ValueAnimator();
		value.setIntValues(0,360);
		value.setDuration(2000);
		value.setInterpolator(new LinearInterpolator());
		value.setRepeatCount(Animation.INFINITE);
		value.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int value = (Integer) arg0.getAnimatedValue();
				mCurrentSweep = value;
				invalidate();
			}
		});
		value.start();
		
		ValueAnimator goalValue = new ValueAnimator();
		goalValue.setIntValues(0,100);
		goalValue.setDuration(3000);
		goalValue.setInterpolator(new LinearInterpolator());
		goalValue.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int goal = (Integer) arg0.getAnimatedValue();
				mGoal = goal;
			}
		});
		goalValue.start();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		canvas.clipPath(mClipPath);
		canvas.drawCircle(mWidth/2,mHeight/2,mWidth/2,mCirclePaint);
		setPath(mCurrentSweep);
		mPaint.setColor(0x33ffffff);
		canvas.drawPath(mPath,mPaint);
		setPath(mCurrentSweep+180);
		mPaint.setColor(0x55ffffff);
		canvas.drawPath(mPath,mPaint);
		canvas.restore();
	}
	
	private void setPath(int sweep){
		mPath = new Path();
		
		float height = (float)mGoal/100*mWidth;
		
		float singW = 0;
		if(height < mWidth/2){
			singW = (float) (Math.acos(height/(mWidth/2))/Math.PI*180);
		}
		
		float diff = mWidth/20;
		float firstHeight = (float)Math.sin(sweep*Math.PI/180) * diff;
		float secondHeight = (float)Math.sin((sweep + 90)*Math.PI/180) * diff;
		float thirdHeight = (float)Math.sin((sweep + 180)*Math.PI/180) * diff;
		float forthHeight = (float)Math.sin((sweep + 270)*Math.PI/180) * diff;
		mPath.moveTo(0,height + firstHeight);
		mPath.cubicTo(mWidth/2,height + secondHeight,
				      mWidth,height + thirdHeight,
				      mWidth/2*3,height + forthHeight);
		mPath.lineTo(mWidth, mWidth);
		mPath.lineTo(0, mWidth);
		mPath.close();
	}

}
