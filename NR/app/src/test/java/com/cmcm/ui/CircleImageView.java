package com.cmcm.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 圆形ImageView
 *
 * @author Johnny
 */
public class CircleImageView extends ImageView {
    private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;

    private RectF mDstRect = new RectF();
    private Rect mSrcRect = new Rect();
    private Paint mRoundPaint;
    private Paint mPaint;
    private Bitmap mBitmap;
    private int mWidth;
    private int mHeight;

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mRoundPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        fillBitmap();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mWidth != w || mHeight != h) {
            mWidth = w;
            mHeight = h;
            mDstRect.set(0, 0, mWidth, mHeight);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        fillBitmap();
        if (mBitmap == null) {
//			super.onDraw(canvas);
            canvas.drawColor(Color.TRANSPARENT);
        } else {
            int i = canvas.saveLayer(0.0f, 0.0f, mWidth, mHeight, null, LAYER_FLAGS);
            canvas.drawRoundRect(mDstRect, mWidth / 2, mHeight / 2, mRoundPaint);
            canvas.drawBitmap(mBitmap, mSrcRect, mDstRect, mPaint);
            canvas.restoreToCount(i);
        }
    }

    private void fillBitmap() {
        Bitmap bitmap = BitmapDrawable.class.isInstance(getDrawable()) ? ((BitmapDrawable) getDrawable()).getBitmap() : null;
        if (bitmap != null) {
            mSrcRect = getDrawable().getBounds();
            int width = mSrcRect.width();
            int height = mSrcRect.height();
            int offset = Math.abs(width - height) / 2;
            mSrcRect.inset(width > height ? offset : 0, height > width ? offset : 0);
            mBitmap = bitmap;
        }
    }

    public void clear() {
        mBitmap = null;
    }

}
