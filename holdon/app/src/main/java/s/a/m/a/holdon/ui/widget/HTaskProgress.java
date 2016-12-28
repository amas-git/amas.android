package s.a.m.a.holdon.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import s.a.m.a.ux.moom.MoomArcStrokeConfig;
import s.a.m.a.ux.moom.MoomRectConfig;
import s.a.m.a.ux.moom.MoomLineScaleConfig;
import s.a.m.a.ux.moom.MoomWaveLine;

/**
 * Created by amas on 15-5-23.
 */
public class HTaskProgress extends View {
    MoomArcStrokeConfig main_circle    = new MoomArcStrokeConfig();
    MoomArcStrokeConfig main_circle_bg = new MoomArcStrokeConfig();

    MoomLineScaleConfig scale = new MoomLineScaleConfig();
    MoomWaveLine wave = new MoomWaveLine();

    MoomRectConfig progress = new MoomRectConfig();

    public HTaskProgress(Context context) {
        super(context);
        init(context);

    }

    public HTaskProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Rect bounds = new Rect(0,0,getWidth(),getHeight());

        scale.setBounds(bounds);

        main_circle.setBounds(bounds);
        main_circle.setStrokeWidth(getWidth()*0.1f);

        main_circle_bg.setStatic(true);
        main_circle_bg.setBounds(bounds);
        main_circle_bg.setStrokeWidth(getWidth()*0.1f);

        progress.setBounds(bounds);
        progress.setAlpha(80);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        MoomArt.drawArcStroke(canvas, main_circle_bg);
//        MoomArt.drawArcStroke(canvas, main_circle);
        progress.draw(canvas);
        scale.draw(canvas);
//        wave.draw(canvas);
    }

    public void setProgress(float progress) {
        main_circle.setPercent(progress);
        this.progress.setPercent(progress);
        invalidate();
    }

    public void setProgressWithAnimation(float progress) {
        if(animator != null) {
            if(animator.isRunning()) {
                animator.cancel();
            }
            animator = null;
        }

        if(animator == null) {
            animator = new ValueAnimator();
        }

        animator = new ValueAnimator();
        animator.setDuration(500);
        animator.setFloatValues(this.progress.getPercent(),progress);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                float f = (Float) arg0.getAnimatedValue();
                setProgress(f);
                invalidate();
            }
        });
        animator.setRepeatCount(0);
        animator.setStartDelay(0);
        animator.start();
    }

    public void startAnimation() {
        if(animator == null) {
            animator = new ValueAnimator();
        }


        animator = new ValueAnimator();
        animator.setDuration(1000);
        //animator.setFloatValues(0f, 180f, 0f); 左右震动

        animator.setFloatValues(0, 0.99f);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                float f = (Float) arg0.getAnimatedValue();
                float theta = f * 180;
                wave.theta(theta);
                android.util.Log.i("amas", ""+f);
                invalidate();
            }
        });

        animator.setRepeatCount(-1);
        animator.setStartDelay(0);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();
    }


    ValueAnimator animator = null;


    boolean isHwEqual = false; // 长宽相等

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (isHwEqual) {
            int w = MeasureSpec.getSize(widthMeasureSpec);
//        int h = MeasureSpec.getSize(heightMeasureSpec);
//        int s = Math.min(w, h);

            setMeasuredDimension(w, w);
        }
    }

}
