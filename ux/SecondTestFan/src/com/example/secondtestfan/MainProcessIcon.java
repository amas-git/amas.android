package com.example.secondtestfan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

public class MainProcessIcon extends BaseMainIcon{
	
	private Paint mPaintSweep;
	private RectF mRectf;
	private int mSweep = 360;
	
	public MainProcessIcon(Context context) {
	    this(context,null);
    }

	public MainProcessIcon(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    initPaint();
    }
	
	private void initPaint() {
		mPaintSweep = new Paint();
		mPaintSweep.setAntiAlias(true);
		mPaintSweep.setColor(0x33ffffff);
    }

	@Override
	protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    if(null != mRectf){
	    	canvas.save();
	    	canvas.rotate(-90, mWidth/2, mHeight/2);
	    	canvas.drawArc(mRectf, 0, mSweep, true, mPaintSweep);
	    	canvas.restore();
	    }
	}
	
	@Override
	protected void onMesureCompleted() {
	    super.onMesureCompleted();
	    mRectf = new RectF(0, 0, mWidth, mHeight);
	    invalidate();
	}
	
	@Override
	protected void onChangeValue(int to) {
	    super.onChangeValue(to);
	    mSweep = (int)((float)to/100*360);
	}

}
