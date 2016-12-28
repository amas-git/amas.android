package com.example.secondtestfan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class MainStroageIcon extends BaseMainIcon {

	private Paint mCurvePaint;
	private Path mFirstPath;
	private Path mSecondPath;
	private RectF mRect;
	
	private float mDiff = 0;
	private int mCurrentSweep = 120;

	public MainStroageIcon(Context context) {
		this(context, null);
	}

	public MainStroageIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	private void initPaint() {
		mCurvePaint = new Paint();
		mCurvePaint.setAntiAlias(true);
		mCurvePaint.setStyle(Paint.Style.FILL);
		mFirstPath = new Path();
		mSecondPath = new Path();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mFirstPath = new Path();
		makeFirstPath(mFirstPath);
		canvas.drawPath(mFirstPath, mCurvePaint);
//		canvas.drawPath(mSecondPath, mCurvePaint);
	}

	@Override
	protected void onMesureCompleted() {
		super.onMesureCompleted();
		mRect = new RectF(0, 0, mWidth, mHeight);
		mCurvePaint.setColor(0x30FFFFFF);
		makeFirstPath(mFirstPath);
//		mCurvePaint.setColor(0x20FFFFFF);
//		makeSecondPath(mSecondPath, 130,30);
		invalidate();
		
		ValueAnimator value = new ValueAnimator();
		value.setFloatValues(0,mWidth/3);
		value.setRepeatCount(Animation.INFINITE);
		value.setRepeatMode(Animation.REVERSE);
		value.setInterpolator(new LinearInterpolator());
		value.setDuration(1000);
		value.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				float diff = (Float) arg0.getAnimatedValue();
				float currentHeight = ((float) Math.cos(mCurrentSweep * Math.PI / 180) * mWidth / 2 + mWidth / 2)*2/3;
				mDiff = diff/(mWidth/3)*currentHeight;
				Log.i("yao", "mWidth/3:  "+mWidth/3+"   currentHeight:  "+currentHeight+"   diff/(mWidth/3):   "+diff/(mWidth/3));
				invalidate();
			}
		});
		value.start();
		
	}

	private void makeSecondPath(Path p) {
		if(mCurrentSweep > 90){
			float currentWidth = (float) Math.sin(mCurrentSweep * Math.PI / 180) * mWidth;
			float currentHeight = (float) Math.cos(mCurrentSweep * Math.PI / 180) * mWidth / 2 + mWidth / 2;
			float diffHeight = currentHeight / 3;
			
			p.moveTo(mWidth / 2 - currentWidth / 2, currentHeight);
			p.cubicTo(mWidth / 2 - currentWidth / 2 + currentWidth / 4 + mDiff,currentHeight - diffHeight, 
							mWidth / 2 - currentWidth / 2 + currentWidth / 4 * 3 + mDiff, currentHeight + diffHeight,	
							mWidth - (mWidth / 2 - currentWidth / 2), currentHeight);
			p.arcTo(mRect, 90 - mCurrentSweep, mCurrentSweep * 2);
			p.close();
		}else{
			float currentWidth = (float)Math.sin(mCurrentSweep*Math.PI/180) * mWidth;
			float currentHeight = (float)Math.cos(mCurrentSweep*Math.PI/180)*mWidth/2 +mWidth/2;
			float diffHeight = (mWidth - currentHeight)/3; 
			
			p.moveTo(mWidth/2 - currentWidth/2, currentHeight);
			p.cubicTo(mWidth/2 - currentWidth/2 + currentWidth/4 + mDiff, currentHeight - diffHeight, 
							mWidth/2 - currentWidth/2 + currentWidth/4*3 + mDiff, currentHeight + diffHeight, 
							mWidth - (mWidth/2 - currentWidth/2), currentHeight);
			p.arcTo(mRect, 90 - mCurrentSweep, mCurrentSweep*2);
			p.close();
		}
	}

	private void makeFirstPath(Path p) {
		if(mCurrentSweep > 90){
			float currentWidth = (float) Math.sin(mCurrentSweep * Math.PI / 180) * mWidth;
			float currentHeight = (float) Math.cos(mCurrentSweep * Math.PI / 180) * mWidth / 2 + mWidth / 2;
			float diffHeight = currentHeight / 3;
			
			p.moveTo(mWidth / 2 - currentWidth / 2, currentHeight);
			p.cubicTo(mWidth / 2 - currentWidth / 2 + currentWidth / 4 + mDiff,currentHeight - diffHeight, 
							mWidth / 2 - currentWidth / 2 + currentWidth / 4 * 3 + mDiff, currentHeight + diffHeight,	
							mWidth - (mWidth / 2 - currentWidth / 2), currentHeight);
			p.arcTo(mRect, 90 - mCurrentSweep, mCurrentSweep * 2);
			p.close();
		}else{
			float currentWidth = (float)Math.sin(mCurrentSweep*Math.PI/180) * mWidth;
			float currentHeight = (float)Math.cos(mCurrentSweep*Math.PI/180)*mWidth/2 +mWidth/2;
			float diffHeight = (mWidth - currentHeight)/3; 
			
			p.moveTo(mWidth/2 - currentWidth/2, currentHeight);
			p.cubicTo(mWidth/2 - currentWidth/2 + currentWidth/4, currentHeight - diffHeight + mDiff, 
							mWidth/2 - currentWidth/2 + currentWidth/4*3 , currentHeight + diffHeight - mDiff, 
							mWidth - (mWidth/2 - currentWidth/2), currentHeight);
			p.arcTo(mRect, 90 - mCurrentSweep, mCurrentSweep*2);
			p.close();
		}
	}
	
	

}
