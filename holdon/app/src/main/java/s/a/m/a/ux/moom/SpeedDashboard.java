package s.a.m.a.ux.moom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import s.a.m.a.ux.moom.ux.DimenUtils;

/**
 * Created by amas on 5/19/15.
 */
public class SpeedDashboard extends View{
    public SpeedDashboard(Context context) {
        super(context);
        init(context);
    }

    String[] SCALE = new String[]{"0K","128K","256K","512K","1M","2M","5M","10M","20M"};

    public SpeedDashboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    MoomArcScaleConfig scale = new MoomArcScaleConfig() {
        @Override
        public String onFormatScaleText(int n) {
            return SCALE[n/10];
        }
    };
    MoomHandConfig hand = new MoomHandConfig();
    MoomArcStrokeConfig load1 = new MoomArcStrokeConfig();

    MoomArcStrokeConfig load2 = new MoomArcStrokeConfig();
    MoomArcStrokeConfig load3 = new MoomArcStrokeConfig();
    MoomArcStrokeConfig load4 = new MoomArcStrokeConfig();
    MoomArcStrokeConfig load5 = new MoomArcStrokeConfig();
    MoomArcStrokeConfig load6 = new MoomArcStrokeConfig();

    Drawable handBitmap = null;

    private void init(Context context) {
        //handBitmap = getResources().getDrawable(R.drawable.h6);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Rect bounds = new Rect(0, 0, getHeight(), getWidth());
        // 配置刻度
        scale.setBounds(bounds);
        scale.setStartAngle(135);
        scale.mSweepAngle = 270;
        scale.getBasePaint().setColor(0x8c5ce4);
        scale.setAlpha(255);
        scale.setScaleWidth(DimenUtils.dp2px(getContext(), 2));
        scale.mMainScaleLineOffset = DimenUtils.dp2px(getContext(),-20);
        scale.mScaleLength = DimenUtils.dp2px(getContext(),10);
        scale.mScaleTextPadding = DimenUtils.dp2px(getContext(),-20);
        scale.getScaleTextPaint().setTextSize(DimenUtils.dp2px(getContext(),16));
        scale.getScaleTextPaint().setColor(0x2e9ae7);
        scale.getScaleTextPaint().setAlpha(255);
        scale.scaleIn(DimenUtils.dp2px(getContext(),20));
        scale.setStatic(true);

        // 配置指针
        hand.setStartAngle(135);
        hand.setSweepAngle(270);
        hand.setDrawable(handBitmap);
        hand.mZoom = 0.4f;
        hand.setBounds(bounds);

        // load1
        load1.setBounds(bounds);
        load1.getBasePaint().setColor(Color.GREEN);
        load1.getBasePaint().setAlpha(100);
        load1.zoom(DimenUtils.dp2px(getContext(),54));
        load1.setStrokeWidth(DimenUtils.dp2px(getContext(),10));


        // load1
        load2.setBounds(bounds);
        load2.getBasePaint().setColor(Color.CYAN);
        load2.getBasePaint().setAlpha(200);
        load2.setPercent(30);
        load2.zoom(DimenUtils.dp2px(getContext(),64));
        load2.setStrokeWidth(DimenUtils.dp2px(getContext(),2));
        load2.setVisible(false,false);

        load3.setBounds(bounds);
        load3.getBasePaint().setColor(Color.CYAN);
        load3.getBasePaint().setAlpha(255);
        load3.setPercent(30);
        load3.mStartAngle+=60;
        load3.zoom(DimenUtils.dp2px(getContext(),68));
        load3.setStrokeWidth(DimenUtils.dp2px(getContext(),1));
        load3.setClockwise(false);
        load3.setVisible(false,false);

        load5.setBounds(bounds);
        load5.getBasePaint().setColor(Color.CYAN);
        load5.getBasePaint().setAlpha(255);
        load5.setPercent(30);
        load5.mStartAngle+=90;
        load5.zoom(DimenUtils.dp2px(getContext(),72));
        load5.setStrokeWidth(DimenUtils.dp2px(getContext(),2));
        load5.setVisible(false,false);

        load6.setBounds(bounds);
        load6.getBasePaint().setColor(Color.CYAN);
        load6.getBasePaint().setAlpha(255);
        load6.setPercent(30);
        load6.mStartAngle+=120;
        load6.zoom(DimenUtils.dp2px(getContext(),76));
        load6.setStrokeWidth(DimenUtils.dp2px(getContext(),1));
        load6.setClockwise(false);
        load6.setVisible(false,false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        MoomArt.drawArcScale(canvas, scale);
        MoomArt.drawArcStroke(canvas, load1);

        MoomArt.drawArcStroke(canvas, load2);
        MoomArt.drawArcStroke(canvas, load3);
        MoomArt.drawArcStroke(canvas, load5);
        MoomArt.drawArcStroke(canvas, load6);
    }

    ValueAnimator loadingAnim = null;
    public void startLoading() {
        if(loadingAnim == null) {
            loadingAnim = new ValueAnimator();
            loadingAnim.setDuration(500);
            loadingAnim.setIntValues(0, 350);
            loadingAnim.setInterpolator(new LinearInterpolator());
            loadingAnim.setRepeatCount(-1);
            loadingAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator arg0) {
                    int offset = (Integer) arg0.getAnimatedValue();
                    load2.mStartAngle = (load2.mStartAngle + offset) % 360;
                    load3.mStartAngle = (load2.mStartAngle + offset) % 360;
                    load5.mStartAngle = (load2.mStartAngle + offset) % 360;
                    load6.mStartAngle = (load2.mStartAngle + offset) % 360;
                    load2.setVisible(true,false);
                    load3.setVisible(true,false);
                    load5.setVisible(true,false);
                    load6.setVisible(true,false);
                    invalidate();
                }
            });
            loadingAnim.start();
        }
    }

    public void stopLoading() {
        if(loadingAnim != null) {
            loadingAnim.cancel();
            loadingAnim = null;
            load2.setVisible(false,false);
            load3.setVisible(false,false);
            load5.setVisible(false,false);
            load6.setVisible(false,false);
        }
    }



    int mProgress = 0;
    ValueAnimator cursorAnim = null;//new ValueAnimator();

    public void setProgress(int progress) {
        if(progress == mProgress) {
            return;
        }

        if(cursorAnim!=null) {
            cursorAnim.cancel();
        }
        int oldProgress = mProgress;

        cursorAnim = new ValueAnimator();
        cursorAnim.setDuration(200);
        cursorAnim.setIntValues(oldProgress, progress);
        cursorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                mProgress = (Integer) arg0.getAnimatedValue();
                hand.setPercent(mProgress);
                load1.setPercent(mProgress);
                //System.out.println(" 进度进度进度: " + mProgress);
                invalidate();
            }
        });

        cursorAnim.start();
    }

}
