package com.example.testfan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.LinearInterpolator;

public class FanGoalView extends View {

	private RectF mFanRect;
	private RectF mFanLightRect;
	
	private Paint mInnerFanPaint;
	private Paint mFanBackPaint;
	private Paint mFanFrontPaint;
	private Paint mBitmapPaint;
	
	private Paint mTextPaint;
	
	private SweepGradient mFrontSweepGradient;
	
	private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG
	                | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
	                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
	                | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
	
	private Bitmap mPinBitmap;
	private Bitmap mInnerBoardBitmap;
	private Bitmap mInnerBoardLightBitmap;
	
	private static final int STROKE_WIDTH = 8;
	private int mStrokeWidth = 0;
	private Context mContext;
	
	private int mToSweep = 0;
	private int mCurrentGoal;
	
	private int mTextOffsetDy = 0;
	
	private float mMaxWidth = 0;
	
	public static enum STAT { SCANING,NORMAL,IDEL};
	private STAT mCurrentStat = STAT.SCANING;

	public FanGoalView(Context context) {
		this(context, null);
	}

	public FanGoalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		mStrokeWidth = dip2px(mContext, STROKE_WIDTH);
		mFanBackPaint = new Paint();
		mFanBackPaint.setColor(0x90ffffff);
		mFanBackPaint.setAntiAlias(false);
		mFanBackPaint.setStyle(Paint.Style.STROKE);
		mFanBackPaint.setStrokeWidth(mStrokeWidth);
		mFanBackPaint.setStrokeCap(Paint.Cap.ROUND);

		mFanFrontPaint = new Paint();
		mFanFrontPaint.setColor(Color.WHITE);
		mFanFrontPaint.setAntiAlias(false);
		mFanFrontPaint.setStyle(Paint.Style.STROKE);
		mFanFrontPaint.setStrokeWidth(mStrokeWidth);
		mFanFrontPaint.setStrokeCap(Paint.Cap.ROUND);
		
		mInnerFanPaint = new Paint();
		mInnerFanPaint.setAntiAlias(false);
		mInnerFanPaint.setStyle(Paint.Style.FILL);
		
//		Typeface fontFace = Typeface.createFromAsset(mContext.getAssets(),"number.ttf");
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(false);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(sp2px(mContext,50));
//		mTextPaint.setTypeface(fontFace);
		mMaxWidth = mTextPaint.measureText(String.valueOf(4));
		
		mBitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
		mBitmapPaint.setAntiAlias(false);
		mBitmapPaint.setDither(true);
		mBitmapPaint.setFilterBitmap(true);
		
		mPinBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cm_home_dashboard_pointer);
		mInnerBoardBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cm_home_dashboard_bg);
		mInnerBoardLightBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cm_home_dashboard_light);
		
		mTextOffsetDy = dip2px(mContext, 20);
		
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				mFanRect = new RectF(mStrokeWidth, mStrokeWidth, getWidth() - mStrokeWidth, getHeight() - mStrokeWidth);
				int fanLingWidth = mInnerBoardBitmap.getWidth();
				mFanLightRect = new RectF(mFanRect.centerX() - fanLingWidth/2,mFanRect.centerY() - fanLingWidth/2,
								mFanRect.centerX() + fanLingWidth/2,mFanRect.centerY() + fanLingWidth/2);

				float shaderCx = mFanRect.centerX();
				float shaderCy = mFanRect.centerY();
				int shaderColor0 = Color.WHITE;
				int shaderColor1 = 0x90ffffff;
				
				
				mInnerFanPaint.setShader(new SweepGradient(mFanLightRect.centerX(), mFanLightRect.centerY(), new int[] { 
                                Color.TRANSPARENT,
                                0x50ffffff}, null));
				
				mFrontSweepGradient = new SweepGradient(shaderCx, shaderCy, new int[]{shaderColor0,shaderColor1,shaderColor0,
								shaderColor0,shaderColor0,shaderColor0,shaderColor0,shaderColor0,shaderColor0,shaderColor0,shaderColor0
								,shaderColor0,shaderColor0,shaderColor0,shaderColor0,shaderColor0,shaderColor0,shaderColor1},null);
				mFanFrontPaint.setShader(mFrontSweepGradient);
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	/** 
     * 将sp值转换为px值，保证文字大小不变 
     *  
     * @param spValue 
     * @param fontScale 
     *            （DisplayMetrics类中属性scaledDensity） 
     * @return 
     */  
    public static int sp2px(Context context, float spValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }

	private int mSweep = 0;
	private int mDiff = 2;

	private int mLightSweep = 0;
	private int mLightDiff = 6;
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (null != mFanRect) {
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG)); 
			
			//画扇背景
			canvas.drawArc(mFanRect, 135, 270, false, mFanBackPaint);
			
			//画带透明的头的扇环
			canvas.save();
			canvas.rotate(139 + mSweep, mFanRect.centerX(), mFanRect.centerY());
			canvas.drawArc(mFanRect, -4 - mSweep, mSweep, false, mFanFrontPaint);
			canvas.restore();
			
//			//画里面的圆板背景
//			canvas.drawBitmap(mInnerBoardBitmap, mFanRect.centerX() - mInnerBoardBitmap.getWidth()/2,
//							mFanRect.centerY() - mInnerBoardBitmap.getHeight()/2, mBitmapPaint);
//			
//			//画圆板发光的板
//			canvas.save();
//			canvas.rotate(90,mFanRect.centerX(), mFanRect.centerY());
//			canvas.drawArc(mFanLightRect, 2, mSweep + 45, true, mInnerFanPaint);
//			canvas.restore();
//			
//			//画指针
//			canvas.save();
//			canvas.rotate(mSweep, mFanRect.centerX(), mFanRect.centerY());
//			canvas.drawBitmap(mPinBitmap, mFanRect.centerX() - mPinBitmap.getWidth()/2,
//							mFanRect.centerY() - mPinBitmap.getHeight()/2, mBitmapPaint);
//			canvas.restore();
//			
//			//画圆板边缘的光亮
//			if(mCurrentStat == STAT.SCANING){
//				canvas.save();
//				int alpha = getLightAlpha(mLightSweep);
//				canvas.saveLayerAlpha(mFanRect, alpha, LAYER_FLAGS);
//				canvas.rotate(mLightSweep, mFanRect.centerX(), mFanRect.centerY());
//				canvas.drawBitmap(mInnerBoardLightBitmap, mFanRect.centerX() - mInnerBoardLightBitmap.getWidth()/2,
//								mFanRect.centerY() - mInnerBoardLightBitmap.getHeight()/2, mBitmapPaint);
//				canvas.restore();
//			}
//			
//			//画字
//			if(mToSweep == mSweep){
//				String text = String.valueOf(mCurrentGoal);
//				canvas.drawText(text, mFanRect.centerX() - mMaxWidth * text.length() /2, mFanLightRect.bottom + mTextOffsetDy, mTextPaint);
//			}else{
//				String text = String.valueOf(mCurrentGoal - mCurrentGoal%3);
//				canvas.drawText(text, mFanRect.centerX() - mMaxWidth * text.length() /2, mFanLightRect.bottom + mTextOffsetDy, mTextPaint);
//			}
		}
		
		mLightSweep += mLightDiff;
		mLightSweep %= 360;
		if(mCurrentStat == STAT.SCANING){
			invalidate();
		}
		
		if(Math.abs(mToSweep - mSweep) < Math.abs(mDiff) && Math.abs(mToSweep - mSweep) != 0){
			mSweep = mToSweep;
			mCurrentGoal = (int)((float)mSweep/270 * 100);
			mOnGoalChangeListener.onChange(mCurrentGoal);
			invalidate();
		}else if(mToSweep != mSweep){
			mSweep += mDiff;
			mCurrentGoal = (int)((float)mSweep/270 * 100);
			mOnGoalChangeListener.onChange(mCurrentGoal);
			invalidate();
		}else{
			mOnGoalChangeListener.onAnimFinish();
		}
	}
	
	public void setCurrentStat(STAT stat){
		mCurrentStat = stat;
	}
	
	public STAT getCurrentStat(){
		return mCurrentStat;
	}
	
	private OnGoalChangeListener mOnGoalChangeListener;
	
	public void setOnGoalChangeListener(OnGoalChangeListener l){
		mOnGoalChangeListener = l;
	}
	
	public interface OnGoalChangeListener{
		public void onChange(int goal);
		public void onAnimFinish();
	}
	
	LinearInterpolator mInterpolator = new LinearInterpolator();
	private int mAlphaDuring = 90;
	private int mAlpha0 = 20;
	private int mAlphaOffset = 10;
	private int getLightAlpha(int sweep){
		if(sweep >= mAlphaDuring && sweep <= mAlphaDuring + mAlpha0*2){
			return 0;
		}else if(sweep > 0 + mAlphaOffset && sweep < mAlphaDuring + mAlpha0*2){
			return (int) (mInterpolator.getInterpolation((float)(mAlphaDuring - sweep)/mAlphaDuring)*255);
		}else if(sweep > mAlphaDuring + mAlpha0*2 && sweep < 180 + mAlphaOffset){
			return (int) (mInterpolator.getInterpolation((float)(sweep - (mAlphaDuring + mAlpha0*2))/(mAlphaDuring + mAlpha0*2))*255);
		}else{
			return 255;
		}
	}

	public void setGoal(int goal) {
	    mToSweep = (int) ((float)goal/100 * 270);
	    if(mToSweep > mSweep){
	    	mDiff = Math.abs(mDiff);
	    	invalidate();
	    }else if(mToSweep < mSweep){
	    	mDiff = -Math.abs(mDiff);
	    	invalidate();
	    }
    }
	
	public int getCurrentGoal(){
		return mCurrentGoal;
	}

}
