package com.cleanmaster.ui.resultpage.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.example.com.cm.litegame.demo.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

// xmlns:self="http://schemas.android.com/apk/res/com.example.com.cm.litegame.demo"
// <com.example.com.cm.litegame.demo.ui.widget.NewRpResultView/>
public class NewRpResultView extends RelativeLayout implements  OnGlobalLayoutListener {
	ViewAnimator mMainPanel = null;
	ViewAnimator mInfoPanel = null;
	
	OnFinishListener mOnFinishListener = null;
	
	public void setOnFinishListener(OnFinishListener listener) {
		mOnFinishListener = listener;
	}
	
	public interface OnFinishListener {
		public void onAllFinish();
	}
	
	private int mMainPanelSize = 300;
	
	/**
	 * 是否开启光波动画
	 */
	boolean mEnableLightWave = true;
	
	public NewRpResultView(Context context) {
		super(context);
		init(context);
	}
	
	ProgressCircle mResultPanel = new ProgressCircle() {
		protected void onFinish() {
			mMainPanel.setDisplayedChild(1);
			mInfoPanel.setDisplayedChild(1);
			mLightWave.startAnim();
		};
	};
	
	LightWave mLightWave = new LightWave();

	
//	private View mView0 = null;ddd
//	private View mView1 = null;
	
	static Paint P1 = new Paint();
	static Paint P2 = new Paint();
	
	static {
		P1.setColor(0xFFFFFFFF);
		P1.setStyle(Style.STROKE);
		P1.setStrokeWidth(10);
		P1.setAntiAlias(true);
		
		P2.setColor(0xFFFFFFFF);
		P2.setStyle(Style.FILL);
		P2.setAlpha(alpha(0.4f));
		P2.setAntiAlias(true);
	}
	
	class LightWave extends Drawable {
		AnimatorSet anim = null;
		float progress1 = 0f;
		float progress2 = 0f;
		Paint paint1 = new Paint();
		Paint paint2 = new Paint();
		int r = 150;
		int LIGHT_2_WIDTH = 10;
		
		public LightWave() {
			paint1.setColor(Color.WHITE);
			//paint1.setAlpha(100);
			paint1.setStyle(Style.STROKE);
			paint1.setAntiAlias(true);
			
			paint2 = new Paint(paint1);
			paint2.setStyle(Style.STROKE);
			//paint2.setAlpha(180);
			paint2.setStrokeWidth(LIGHT_2_WIDTH);
			
			anim = new AnimatorSet();
			
			ValueAnimator va = ValueAnimator.ofFloat(0f,1f);
			va.setInterpolator(new LinearInterpolator());
			va.setDuration(1000);
			//va.setRepeatCount(Animation.INFINITE);
			va.setRepeatCount(2);
			va.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator arg) {
					progress1 = (Float)arg.getAnimatedValue();
					invalidate();
				}
			});
			
			ValueAnimator vb = ValueAnimator.ofFloat(0f,1f);
			vb.setInterpolator(new LinearInterpolator());
			vb.setStartDelay(500);
			vb.setDuration(1000);
			//vb.setRepeatCount(Animation.INFINITE);
			va.setRepeatCount(1);
			vb.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator arg) {
					progress2 = (Float)arg.getAnimatedValue();
					invalidate();
				}
			});
			
			anim.playTogether(va,vb);
			anim.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator arg0) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animator arg0) {
					
				}
				
				@Override
				public void onAnimationEnd(Animator arg0) {
					onFinish();
				}
				
				@Override
				public void onAnimationCancel(Animator arg0) {
					
				}
			});
		}
		
		public void startAnim() {
			anim.start();
		}
		
		protected void onFinish() {
			
		}

		@Override
		public void draw(Canvas canvas) {
			if(progress1 > 0) {
				paint1.setStrokeWidth(getStrokWidth());
				paint1.setAlpha(alpha1());
				canvas.drawCircle(getCenterX(), getCenterY(), r + (getStrokWidth()/2), paint1);
			}
			
			if(progress2 > 0) {
				paint2.setAlpha(alpha2());
				canvas.drawCircle(getCenterX(), getCenterY(), getR2(), paint2);
			}
		}

		private int alpha1() {
			return (int) (255 * (1f - progress1));
		}

		private int alpha2() {
			return (int) (255 * (1f - progress2));
		}

		private float getStrokWidth() {
			return 100 * progress1;
		}
		
		private int getR2() {
			int r2 = (int)(100 * progress2) + r;
			return r2 + (LIGHT_2_WIDTH / 2);
		}

		@Override
		public void setAlpha(int alpha) {
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
		}

		@Override
		public int getOpacity() {
			return 0;
		} 
	}
	

	
	public static int alpha(float percent) {
		return (int)(255 * percent);
	}
	
	class ProgressCircle extends Drawable{
		AnimatorSet anim = null;
		float progress = 0f;
		
		public void startAnim() {
			anim = new AnimatorSet();
			
			ValueAnimator va = ValueAnimator.ofFloat(0f,1f);
			va.setInterpolator(new LinearInterpolator());
			va.setDuration(800);
			va.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator arg) {
					progress = (Float)arg.getAnimatedValue();
					invalidate();
					onPreogressUpdate();
				}
			});
			
			anim.playSequentially(va);
			anim.setDuration(500);
			anim.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator arg0) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animator arg0) {
					
				}
				
				@Override
				public void onAnimationEnd(Animator arg0) {
					onFinish();
				}
				
				@Override
				public void onAnimationCancel(Animator arg0) {
					
				}
			});
			anim.start();
		}	

		protected void onPreogressUpdate() {
			onProgressUpdate(progress);
		}

		protected void onFinish() {
			if(mOnFinishListener != null) {
				mOnFinishListener.onAllFinish();
			}
		}

		int w = 300;
		@Override
		public void draw(Canvas canvas) {
			int d = w / 2;
			canvas.save();
			canvas.translate(getCenterX() - d, getCenterY() - d);
			canvas.drawArc(new RectF(0, 0, w, w), -90, 360*progress, false, P1);
			canvas.restore();
		}

		@Override
		public void setAlpha(int alpha) {
			
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
			
		}

		@Override
		public int getOpacity() {
			return 0;
		}
	}
	
	public float getCenterX() {
		return getWidth() / 2;
	}
	
	public void onProgressUpdate(float progress) {
		mInfoPanelText1.setText("" + ((int)(mTotalSize * progress)));
	}


	public float getCenterY() {
		return getHeight() / 2;
	}
 
	public NewRpResultView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TypedArray a = getContext().obtainStyledAttributes(attrs,
		// R.styleable.NewRpResultView);
		// panelLeftResId =
		// a.getResourceId(R.styleable.NewRpResultView_panel_left, 0);
		// panelRightResId =
		// a.getResourceId(R.styleable.NewRpResultView_panel_right, 0);
		// a.recycle();
		init(context);
	}

	TextView mInfoPanelText1 = null;
	
	private void init(Context context) {
		// TODO: init resource here
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        inflate(context, R.layout.new_rp_resultview, this);
        mMainPanel = (ViewAnimator) findViewById(R.id.viewanimator);
        mInfoPanel = (ViewAnimator) findViewById(R.id.va_text);
        
        
        mInfoPanelText1 = (TextView) mInfoPanel.findViewById(R.id.tv1);
        mMainPanel.setDisplayedChild(0);
	}
	
	
	public void showResult(long size) {
		mTotalSize = size;
		
		postDelayed(new Runnable() {
			@Override
			public void run() {
				mResultPanel.startAnim();
			}
		}, 1000);
	}
	
	private long mTotalSize = 0;
	
	

	
	@Override
	protected void onDraw(Canvas canvas) {
		mResultPanel.draw(canvas);
		mLightWave.draw(canvas);
	}

	@Override
	public void onGlobalLayout() {
		hello();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
		
		showResult(50000);
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		LayoutParams lp = (LayoutParams) mMainPanel.getLayoutParams();
		lp.height = mMainPanelSize;
		lp.width  = mMainPanelSize;
	}

	private void hello() {
        Rotate3dAnimation animIn = new Rotate3dAnimation(-90, 0, mMainPanelSize/2, mMainPanelSize/2, 0, true);
        animIn.setInterpolator(new LinearInterpolator());
        animIn.setStartOffset(400);
        animIn.setDuration(400);
        
        Rotate3dAnimation animOut = new Rotate3dAnimation(0, 90, mMainPanelSize/2, mMainPanelSize/2, 0, true);
        animOut.setInterpolator(new AccelerateInterpolator());
        animOut.setDuration(400);
        
        mMainPanel.setOutAnimation(animOut);
        mMainPanel.setInAnimation(animIn);
        
        
        animIn = new Rotate3dAnimation(-90, 0, mMainPanelSize/2, mMainPanelSize/2, 0, true);
        animIn.setInterpolator(new LinearInterpolator());
        animIn.setStartOffset(400);
        animIn.setDuration(400);
        
        animOut = new Rotate3dAnimation(0, 90, mMainPanelSize/2, mMainPanelSize/2, 0, true);
        animOut.setInterpolator(new AccelerateInterpolator());
        animOut.setDuration(400);
        mInfoPanel.setOutAnimation(animOut);
        mInfoPanel.setInAnimation(animIn);
	}
}