package com.example.secondtestfan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class MainCircleFan extends View{
	
	private Context mContext;
	
	private float mWidth;
	private float mHeight;
	private RectF mFanRect;
	private RectF mSmallRect;
	private RectF mBigRect;
	
	private Paint mPaintRightCircle;
	private Paint mPaintLeftCircle;
	private Paint mPaintRotatedLight;
	private Paint mPaintTeaching;
	private Paint mPaintCleaningCircleSmall;
	private Paint mPaintCleaningCircleBig;
	private Paint mPaintTvRightCircle;
	private Paint mPaintTvLeftGoal;
	private Paint mPaintTvLeftDetial;
	
	private int mCircleRadius = 100;
	private int mLightCircleRadius = 70;
	private int mTeachingCircleRadius = 10;
	private int mToEndCircleDx = 65;
	private int mStrokeWidth = 0;
	private int mSweep = 0;
	private float mCircleDx = 0;
	
	private int mSmallCircleStrokeWidth = 0;
	private int mBigCircleStrokeWidth = 0;
	
	private int mLeftCircleUpTvSize = 0;
	private int mLeftCircleDownTvSize = 0;
	
	private boolean mIsShowTeaching = false;
	
	private static final int CIRCLE_RADIUS = 67;
	private static final int LIGHT_CIRCLE_RADIUS = 60;
	private static final int TEACHING_CIRCLE_RADIUS = 30;
	private static final int TO_END_CIRCLE_DX = 65;
	private static final int STROKE_WIDTH = 3;
	private static final int SMALL_CIRCLE_STROKE_WIDTH = 3;
	private static final int BIG_CIRCLE_STROKE_WIDTH = 40;
	
	private ValueAnimator mCaculCircle;
	private AnimatorSet mShowGoal;
	private AnimatorSet mShowTeaching;
	private AnimatorSet mClickCircle;
	private AnimatorSet mCloseCircle;
	private AnimatorSet mCleaningBigCircle;
	private AnimatorSet mCleaningSmallCircle;
	
	private int mCurrentRGB [] = new int[]{50,97,180};
	private int mCurrentGoal = 100;
	
	private final int[] RgbLeftCircleBlue = new int[]{0,100,196};
	private final int[] RgbLeftCircleGreen = new int[]{0,165,88};
	private final int[] RgbLeftCircleYellow = new int[]{223,105,22};
	private final int[] RgbLeftCircleRed = new int[]{209,41,31};
	private final int[] mCurrentLeftCircleRGB = new int[]{RgbLeftCircleBlue[0],RgbLeftCircleBlue[1],RgbLeftCircleBlue[2]};
	
	private ValueAnimator mRedLeftCircleAnimator;
	private ValueAnimator mGreenLeftCircleAnimator;
	private ValueAnimator mBlueLeftCircleAnimator;
	
	private static enum ANIM_STAT {INIT,CALCULATING,SHOW_GOAL,TEACHING,WAITING_FOR_CLICK,CLICK_CLEAN,CLOSE_CIRCLE,CLEANING,CLEAN_FINISHING,CLEAN_FINISHED};
	
	private ANIM_STAT mCurrentStat = ANIM_STAT.INIT;
	private GestureDetector mGestureDetector;

	public MainCircleFan(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    mContext = context;
	    init();
    }


	public MainCircleFan(Context context) {
	    this(context,null);
    }

	private void init() {
		
		mCircleRadius = dip2px(mContext, CIRCLE_RADIUS);
		mLightCircleRadius = dip2px(mContext, LIGHT_CIRCLE_RADIUS);
		mTeachingCircleRadius = dip2px(mContext, TEACHING_CIRCLE_RADIUS);
		mToEndCircleDx = dip2px(mContext, TO_END_CIRCLE_DX);
		mStrokeWidth = dip2px(mContext, STROKE_WIDTH);
		mSmallCircleStrokeWidth = dip2px(mContext, SMALL_CIRCLE_STROKE_WIDTH);
		mBigCircleStrokeWidth = dip2px(mContext, BIG_CIRCLE_STROKE_WIDTH);
		mLeftCircleUpTvSize = sp2px(mContext, 12);
		mLeftCircleDownTvSize = sp2px(mContext, 14);
		
		mPaintRightCircle = new Paint();
		mPaintRightCircle.setColor(0xFFFAFAFA);
		mPaintRightCircle.setAntiAlias(true);
		mPaintRightCircle.setStyle(Paint.Style.FILL);
		
		mPaintLeftCircle = new Paint();
		mPaintLeftCircle.setColor(0xD1291F);
		mPaintLeftCircle.setAntiAlias(true);
		mPaintLeftCircle.setStyle(Paint.Style.FILL);
		
		mPaintRotatedLight = new Paint();
		mPaintRotatedLight.setColor(Color.GREEN);
		mPaintRotatedLight.setAntiAlias(false);
		mPaintRotatedLight.setStyle(Paint.Style.STROKE);
		mPaintRotatedLight.setStrokeWidth(mStrokeWidth);
		
		mPaintCleaningCircleSmall = new Paint();
		mPaintCleaningCircleSmall.setColor(0X34FFFFFF);
		mPaintCleaningCircleSmall.setAntiAlias(false);
		mPaintCleaningCircleSmall.setStyle(Paint.Style.STROKE);
		mPaintCleaningCircleSmall.setStrokeWidth(mSmallCircleStrokeWidth);
		
		mPaintCleaningCircleBig = new Paint();
		mPaintCleaningCircleBig.setColor(0X28FFFFFF);
		mPaintCleaningCircleBig.setAntiAlias(false);
		mPaintCleaningCircleBig.setStyle(Paint.Style.STROKE);
		mPaintCleaningCircleBig.setStrokeWidth(mBigCircleStrokeWidth);
		
		mPaintTeaching = new Paint();
		mPaintTeaching.setColor(0x00CDC9C9);
		mPaintTeaching.setAntiAlias(false);
		mPaintTeaching.setStyle(Paint.Style.FILL);
		
		mPaintTvRightCircle = new Paint();
		mPaintTvRightCircle.setColor(getCurrentColor());
		mPaintTvRightCircle.setAntiAlias(true);
		mPaintTvRightCircle.setTextSize(sp2px(mContext, 18));
		
		mPaintTvLeftGoal = new Paint();
		mPaintTvLeftGoal.setColor(0xFFFFFFFF);
		mPaintTvLeftGoal.setAntiAlias(true);
		mPaintTvLeftGoal.setTextSize(sp2px(mContext, 50));
		
		mPaintTvLeftDetial = new Paint();
		mPaintTvLeftDetial.setColor(0x80FFFFFF);
		mPaintTvLeftDetial.setAntiAlias(true);
		mPaintTvLeftDetial.setTextSize(mLeftCircleUpTvSize);
		
		mGestureDetector = new GestureDetector(mContext, new MyOnGestureListener());
		
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				mWidth = getWidth();
				mHeight = getHeight();
				
				mPaintRotatedLight.setColor(getCurrentColor());
				
				mFanRect = new RectF(mWidth/2 - mLightCircleRadius, mHeight/2 - mLightCircleRadius, 
								mWidth/2 + mLightCircleRadius, mHeight/2 + mLightCircleRadius);
				
				mSmallRect = new RectF(mWidth/2 - mCircleRadius + mSmallCircleStrokeWidth/2, mHeight/2 - mCircleRadius + mSmallCircleStrokeWidth/2, 
													mWidth/2 + mCircleRadius - mSmallCircleStrokeWidth/2, mHeight/2 + mCircleRadius - mSmallCircleStrokeWidth/2);
				mBigRect = new RectF(mWidth/2 - mCircleRadius - mBigCircleStrokeWidth/2, mHeight/2 - mCircleRadius - mBigCircleStrokeWidth/2, 
													mWidth/2 + mCircleRadius + mBigCircleStrokeWidth/2, mHeight/2 + mCircleRadius + mBigCircleStrokeWidth/2);
				
				mCaculCircle.start();
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		
		initLeftCircleColor();
		
		initCaculCircleAnimation();
		
		initShowGoalAnimation();
		
		initShowTeachingAnimation();
		
		initShowClickAnimation();
		
		initShowCloseCircle();
		
		initCleaning();
	}
	
	private static final int LEFT_CIRCLE_COLOR_DURATION = 5000;
	private void initLeftCircleColor() {
		mRedLeftCircleAnimator = new ValueAnimator();
		mRedLeftCircleAnimator.setIntValues(RgbLeftCircleBlue[0],RgbLeftCircleGreen[0],RgbLeftCircleYellow[0],RgbLeftCircleRed[0]);
		mRedLeftCircleAnimator.setDuration(LEFT_CIRCLE_COLOR_DURATION);
		mRedLeftCircleAnimator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int red = (Integer) arg0.getAnimatedValue();
				mCurrentLeftCircleRGB[0] = red;
			}
		});
		
		mGreenLeftCircleAnimator = new ValueAnimator();
		mGreenLeftCircleAnimator.setIntValues(RgbLeftCircleBlue[1],RgbLeftCircleGreen[1],RgbLeftCircleYellow[1],RgbLeftCircleRed[1]);
		mGreenLeftCircleAnimator.setDuration(LEFT_CIRCLE_COLOR_DURATION);
		mGreenLeftCircleAnimator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int green = (Integer) arg0.getAnimatedValue();
				mCurrentLeftCircleRGB[1] = green;
			}
		});
		
		mBlueLeftCircleAnimator = new ValueAnimator();
		mBlueLeftCircleAnimator.setIntValues(RgbLeftCircleBlue[2],RgbLeftCircleGreen[2],RgbLeftCircleYellow[2],RgbLeftCircleRed[2]);
		mBlueLeftCircleAnimator.setDuration(LEFT_CIRCLE_COLOR_DURATION);
		mBlueLeftCircleAnimator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int blue = (Integer) arg0.getAnimatedValue();
				mCurrentLeftCircleRGB[2] = blue;
			}
		});
    }


	private void initCleaning() {
		final ValueAnimator bigAlpha = new ValueAnimator();
		bigAlpha.setIntValues(10,50,0);
		bigAlpha.setInterpolator(new LinearInterpolator());
		bigAlpha.setRepeatCount(Animation.INFINITE);
		bigAlpha.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int alpha = (Integer) arg0.getAnimatedValue();
				mPaintCleaningCircleBig.setAlpha(alpha);
				invalidate();
			}
		});
		
		final ValueAnimator bigRadius = new ValueAnimator();
		int currentBigStroke = mBigCircleStrokeWidth;
		bigRadius.setIntValues(0,currentBigStroke);
		bigRadius.setInterpolator(new LinearInterpolator());
		bigRadius.setRepeatCount(Animation.INFINITE);
		bigRadius.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int strokeWidth = (Integer) arg0.getAnimatedValue();
				mBigCircleStrokeWidth = strokeWidth;
				mBigRect.left = mWidth/2 - mCircleRadius - mBigCircleStrokeWidth/2;
				mBigRect.right = mWidth/2 + mCircleRadius + mBigCircleStrokeWidth/2;
				mBigRect.top = mHeight/2 - mCircleRadius - mBigCircleStrokeWidth/2;
				mBigRect.bottom = mHeight/2 + mCircleRadius + mBigCircleStrokeWidth/2;
			}
		});
		
		mCleaningBigCircle = new AnimatorSet();
		mCleaningBigCircle.playTogether(bigAlpha,bigRadius);
		mCleaningBigCircle.setDuration(1000);
		
		final ValueAnimator smallAlpha = new ValueAnimator();
		smallAlpha.setIntValues(30,50,0);
		smallAlpha.setInterpolator(new LinearInterpolator());
		smallAlpha.setRepeatCount(Animation.INFINITE);
		smallAlpha.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int alpha = (Integer) arg0.getAnimatedValue();
				mPaintCleaningCircleSmall.setAlpha(alpha);
				invalidate();
			}
		});
		
		final ValueAnimator smallRadius = new ValueAnimator();
		int currentRadius = mCircleRadius;
		smallRadius.setIntValues(currentRadius,currentRadius+currentRadius/3*2);
		smallRadius.setInterpolator(new LinearInterpolator());
		smallRadius.setRepeatCount(Animation.INFINITE);
		smallRadius.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int radius = (Integer) arg0.getAnimatedValue();
				mSmallRect.left = mWidth/2 - radius + mSmallCircleStrokeWidth/2;
				mSmallRect.right = mWidth/2 + radius - mSmallCircleStrokeWidth/2;
				mSmallRect.top = mHeight/2 - radius + mSmallCircleStrokeWidth/2;
				mSmallRect.bottom = mHeight/2 + radius - mSmallCircleStrokeWidth/2;
			}
		});
		
		new MyAnimationListenerControll(bigRadius){
			
			@Override
			public void onAnimationRepeat() {
				if(mCurrentStat == ANIM_STAT.CLEAN_FINISHING){
					bigRadius.setRepeatCount(0);
					bigAlpha.setRepeatCount(0);
					smallAlpha.setRepeatCount(1);
					smallRadius.setRepeatCount(1);
				}
			}
			
		};
		
		mCleaningSmallCircle = new AnimatorSet();
		mCleaningSmallCircle.setStartDelay(200);
		mCleaningSmallCircle.playTogether(smallAlpha,smallRadius);
		mCleaningSmallCircle.setDuration(1000);
		
		new MyAnimationListenerControll(smallAlpha){
			
			@Override
			public void onFinish() {
				setShowTeaching(false);
				setShowCurrentGoal(90);
				setFinishCalculating();
			}
		};
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
	    return true;
	}
	
	private void initShowCloseCircle() {
		ValueAnimator alpha = new ValueAnimator();
		alpha.setIntValues(50,0);
		alpha.setInterpolator(new LinearInterpolator());
		alpha.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int alpha = (Integer) arg0.getAnimatedValue();
				mPaintLeftCircle.setAlpha(alpha);
				invalidate();
			}
		});
		
		ValueAnimator textGoalAlpha = new ValueAnimator();
		textGoalAlpha.setIntValues(255,0);
		textGoalAlpha.setInterpolator(new LinearInterpolator());
		textGoalAlpha.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int alpha = (Integer) arg0.getAnimatedValue();
				mPaintTvLeftGoal.setAlpha(alpha);
			}
		});
		
		ValueAnimator textDetailAlpha = new ValueAnimator();
		textDetailAlpha.setIntValues(80,0);
		textDetailAlpha.setInterpolator(new LinearInterpolator());
		textDetailAlpha.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int alpha = (Integer) arg0.getAnimatedValue();
				mPaintTvLeftDetial.setAlpha(alpha);
			}
		});
		
		ValueAnimator xTranslate = new ValueAnimator();
		xTranslate.setIntValues(mToEndCircleDx,0);
		xTranslate.setInterpolator(new LinearInterpolator());
		xTranslate.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int dx = (Integer) arg0.getAnimatedValue();
				mCircleDx = dx;
			}
		});
		
		mCloseCircle = new AnimatorSet();
		mCloseCircle.playTogether(alpha,xTranslate,textGoalAlpha,textDetailAlpha);
		mCloseCircle.setDuration(350);
		mCloseCircle.setInterpolator(new LinearInterpolator());
		
		
		new MyAnimationListenerControll(alpha){
			@Override
			public void onFinish() {
				mCurrentStat = ANIM_STAT.CLEANING;
				mCleaningBigCircle.start();
				mCleaningSmallCircle.start();
			}
		};
    }


	private void initShowClickAnimation() {
		ValueAnimator alpha = new ValueAnimator();
		alpha.setIntValues(0,255,255,50);
		alpha.setInterpolator(new LinearInterpolator());
		alpha.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int alpha = (Integer) arg0.getAnimatedValue();
				mPaintTeaching.setAlpha(alpha);
				invalidate();
			}
		});
		
		ValueAnimator scale = new ValueAnimator();
		scale.setIntValues(0,mCircleRadius);
		scale.setInterpolator(new LinearInterpolator());
		scale.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int raius = (Integer) arg0.getAnimatedValue();
				mTeachingCircleRadius = raius;
				invalidate();
			}
		});
		
		
		mClickCircle = new AnimatorSet();
		mClickCircle.playTogether(alpha,scale);
		mClickCircle.setDuration(400);
		
		new MyAnimationListenerControll(scale){
			@Override
			public void onFinish() {
				mCurrentStat = ANIM_STAT.CLOSE_CIRCLE;
				mCloseCircle.start();
			}
		};
    }


	private void initShowTeachingAnimation() {
		ValueAnimator alpha = new ValueAnimator();
		alpha.setIntValues(255,0);
		alpha.setRepeatCount(1);
		alpha.setInterpolator(new LinearInterpolator());
		alpha.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int alpha = (Integer) arg0.getAnimatedValue();
				mPaintTeaching.setAlpha(alpha);
				invalidate();
			}
		});
		
		int currentRadius = mTeachingCircleRadius;
		ValueAnimator scale = new ValueAnimator();
		scale.setIntValues(currentRadius/2,currentRadius);
		scale.setRepeatCount(1);
		scale.setInterpolator(new LinearInterpolator());
		scale.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int raius = (Integer) arg0.getAnimatedValue();
				mTeachingCircleRadius = raius;
				invalidate();
			}
		});
		
		mShowTeaching = new AnimatorSet();
		mShowTeaching.playTogether(alpha,scale);
		mShowTeaching.setDuration(800);
		mShowTeaching.start();
    }

	private void initShowGoalAnimation() {
		ValueAnimator alpha = new ValueAnimator();
		alpha.setIntValues(0,50);
		alpha.setInterpolator(new LinearInterpolator());
		alpha.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int alpha = (Integer) arg0.getAnimatedValue();
				mPaintLeftCircle.setAlpha(alpha);
				invalidate();
			}
		});
		
		ValueAnimator textGoalAlpha = new ValueAnimator();
		textGoalAlpha.setIntValues(0,255);
		textGoalAlpha.setInterpolator(new LinearInterpolator());
		textGoalAlpha.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int alpha = (Integer) arg0.getAnimatedValue();
				mPaintTvLeftGoal.setAlpha(alpha);
			}
		});
		
		ValueAnimator textDetailAlpha = new ValueAnimator();
		textDetailAlpha.setIntValues(0,80);
		textDetailAlpha.setInterpolator(new LinearInterpolator());
		textDetailAlpha.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int alpha = (Integer) arg0.getAnimatedValue();
				mPaintTvLeftDetial.setAlpha(alpha);
			}
		});
		
		ValueAnimator xTranslate = new ValueAnimator();
		xTranslate.setIntValues(0,mToEndCircleDx);
		xTranslate.setInterpolator(new LinearInterpolator());
		xTranslate.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int dx = (Integer) arg0.getAnimatedValue();
				mCircleDx = dx;
			}
		});
		
		mShowGoal = new AnimatorSet();
		mShowGoal.playTogether(alpha,xTranslate,textGoalAlpha,textDetailAlpha);
		mShowGoal.setDuration(350);
		mShowGoal.setInterpolator(new LinearInterpolator());
		
		new MyAnimationListenerControll(alpha){
			@Override
			public void onFinish() {
				if(mIsShowTeaching){
					postDelayed(new Runnable() {
	                    public void run() {
	                    	mCurrentStat = ANIM_STAT.TEACHING;
	                    	mShowTeaching.start();
	                    }
                    }, 300);
				}else{
					mCurrentStat = ANIM_STAT.WAITING_FOR_CLICK;
				}
			}
		};
    }

	private void initCaculCircleAnimation() {
		mCaculCircle = new ValueAnimator();
		mCaculCircle.setIntValues(360,0);
		mCaculCircle.setDuration(1000);
		mCaculCircle.setInterpolator(new LinearInterpolator());
		mCaculCircle.setRepeatCount(ValueAnimator.INFINITE);
		mCaculCircle.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int sweep = (Integer) arg0.getAnimatedValue();
				mSweep = sweep;
				invalidate();
			}
		});
    }

	public void setCalculatingStat(){
		mCurrentStat = ANIM_STAT.CALCULATING;
		mCaculCircle.start();
	}
	
	public void setShowCurrentGoal(int goal){
		mCurrentGoal = goal;
	}
	
	public void setFinishCalculating(){
		mCaculCircle.cancel();
		mCurrentStat = ANIM_STAT.SHOW_GOAL;
		mShowGoal.start();
	}
	
	public void setShowTeaching(boolean showTeaching){
		mIsShowTeaching = showTeaching;
	}
	
	private void setClickedCircle(){
		mShowTeaching.cancel();
		mPaintTeaching.setAlpha(255);
		mCurrentStat = ANIM_STAT.CLICK_CLEAN;
		mClickCircle.start();
	}
	
	public void setCleanFinished(){
		mCurrentStat = ANIM_STAT.CLEAN_FINISHING;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    if(null == mFanRect || mCurrentStat == ANIM_STAT.INIT){
	    	return;
	    }
	    
	    //画左边的圆
	    if(mCurrentStat != ANIM_STAT.CALCULATING || mCurrentStat != ANIM_STAT.CLEANING){
	    	mPaintLeftCircle.setColor(Color.argb(255, mCurrentLeftCircleRGB[0], mCurrentLeftCircleRGB[1], mCurrentLeftCircleRGB[2]));
	    	canvas.drawCircle(mWidth/2 - mCircleDx, mHeight/2, mCircleRadius, mPaintLeftCircle);
	    	//画左边圆上的字
		    drawLeftCircleText(canvas);
	    }
	    
	    //画右边的圆
	    canvas.drawCircle(mWidth/2 + mCircleDx, mHeight/2, mCircleRadius, mPaintRightCircle);
	    //画右边圆上的字
	    drawRightCircleText(canvas);
	    
	    //画带透明的头的扇环
	    if (mCurrentStat == ANIM_STAT.CALCULATING) {
	    	canvas.save();
	    	canvas.rotate(mSweep, mFanRect.centerX(), mFanRect.centerY());
	    	canvas.drawArc(mFanRect, 0, 180, false, mPaintRotatedLight);
	    	canvas.restore();
	    }
	    
	    //画教育用户点击优化的圈圈
	    if (mCurrentStat == ANIM_STAT.TEACHING || mCurrentStat == ANIM_STAT.CLICK_CLEAN){
	    	 canvas.drawCircle(mWidth/2 + mCircleDx, mHeight/2, mTeachingCircleRadius, mPaintTeaching);
	    }
	    
	    //画优化的时环的动画
	    if(mCurrentStat == ANIM_STAT.CLEAN_FINISHING || mCurrentStat == ANIM_STAT.CLEANING){
	    	canvas.drawArc(mBigRect, 0, 360, false, mPaintCleaningCircleBig);
	    	canvas.drawArc(mSmallRect, 0, 360, false, mPaintCleaningCircleSmall);
	    }
	    
	}
	
	private void drawLeftCircleText(Canvas canvas) {
		String goal = String.valueOf(mCurrentGoal);
		float textHeight = mPaintTvLeftGoal.descent() - mPaintTvLeftGoal.ascent();
		float textOffset = (textHeight / 2) - mPaintTvLeftGoal.descent();
		float width = mPaintTvLeftGoal.measureText(goal);
		canvas.drawText(goal, mWidth/2 - mCircleDx - width/2, mHeight/2 + textOffset - mCircleRadius/8, mPaintTvLeftGoal);
		
		mPaintTvLeftDetial.setTextSize(mLeftCircleUpTvSize);
		String detailUp = mContext.getString(R.string.main_goal_unit);
		float textDetailUpHeight = mPaintTvLeftDetial.descent() - mPaintTvLeftDetial.ascent();
		float textDetailUpOffset = (textDetailUpHeight / 2) - mPaintTvLeftDetial.descent();
		canvas.drawText(detailUp, mWidth/2 - mCircleDx + width/2 + mCircleRadius/13, mHeight/2 + textDetailUpOffset - mCircleRadius*3/9, mPaintTvLeftDetial);
		
		mPaintTvLeftDetial.setTextSize(mLeftCircleDownTvSize);
		String detailDown = mContext.getString(R.string.main_goal_need_optimization);
		float textDetailDownHeight = mPaintTvLeftDetial.descent() - mPaintTvLeftDetial.ascent();
		float textDetailDownOffset = (textDetailDownHeight / 2) - mPaintTvLeftDetial.descent();
		float detailDownwidth = mPaintTvLeftDetial.measureText(detailDown);
		canvas.drawText(detailDown, mWidth/2 - mCircleDx - detailDownwidth/2, mHeight/2 + textDetailDownOffset + mCircleRadius/2, mPaintTvLeftDetial);
    }


	private void drawRightCircleText(Canvas canvas) {
		String text = getRightCircleText();
		float textHeight = mPaintTvRightCircle.descent() - mPaintTvRightCircle.ascent();
		float textOffset = (textHeight / 2) - mPaintTvRightCircle.descent();
		float width = mPaintTvRightCircle.measureText(text);
		canvas.drawText(text, mWidth/2 + mCircleDx - width/2, mHeight/2 + textOffset, mPaintTvRightCircle);
    }


	private String getRightCircleText() {
		switch (mCurrentStat) {
		case CALCULATING:
			return mContext.getString(R.string.main_goal_calculating);
		case CLOSE_CIRCLE:
		case CLEANING:
			return mContext.getString(R.string.main_goal_optimaizating);
		default:
			break;
		}
		return mContext.getString(R.string.main_goal_just_optimization);
    }


	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
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
	
	class MyOnGestureListener extends SimpleOnGestureListener {
		
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			playOnClick(e);
		    return super.onSingleTapUp(e);
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			playOnClick(e);
		    return super.onSingleTapConfirmed(e);
		}
	}
	
	private void playOnClick(MotionEvent e){
		if (mCurrentStat == ANIM_STAT.WAITING_FOR_CLICK || mCurrentStat == ANIM_STAT.TEACHING) {
			Rect rect = new Rect();
			rect.left = (int) (mWidth / 2 + mCircleDx - mCircleRadius);
			rect.right = (int) (mWidth / 2 + mCircleDx + mCircleRadius);
			rect.top = (int) (mHeight / 2 - mCircleRadius);
			rect.bottom = (int) (mHeight / 2 + mCircleRadius);
			if (rect.contains((int) e.getX(), (int) e.getY())) {
				setClickedCircle();
				mOnClickCleanListener.onClick();
			}
		}
	}
	
	public interface OnClickCleanListener{
		public void onClick();
	}
	
	private OnClickCleanListener mOnClickCleanListener;
	
	public void setOnClickCleanListener(OnClickCleanListener l){
		mOnClickCleanListener = l;
	}


	public void onChangeColor(int rgb[], int goal) {
		mCurrentRGB = rgb;
		if(mCurrentStat == ANIM_STAT.CALCULATING){
			mPaintRotatedLight.setColor(getCurrentColor());
		}
		mPaintTvRightCircle.setColor(getCurrentColor());
		toLeftCircleColor(goal);
    }
	
	private int getCurrentColor(){
		return Color.argb(255, mCurrentRGB[0],mCurrentRGB[1], mCurrentRGB[2]);
	}
	
	private void toLeftCircleColor(int toGoal) {
		long currentTime = (long) ((float)(100-toGoal)/100 * LEFT_CIRCLE_COLOR_DURATION);
		mRedLeftCircleAnimator.setCurrentPlayTime(currentTime);
		mGreenLeftCircleAnimator.setCurrentPlayTime(currentTime);
		mBlueLeftCircleAnimator.setCurrentPlayTime(currentTime);
    }
	
}
