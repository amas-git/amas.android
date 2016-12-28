package com.amask.mobile.demos;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.View;

// xmlns:self="http://schemas.android.com/apk/res/com.amask.mobile.demos"
// <com.amask.mobile.demos.ui.widget.CoinView/>
public class CoinView extends View {
    public CoinView(Context context) {
        super(context);
        init(context);
    }

    public CoinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //              TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CoinView);
        //              panelLeftResId = a.getResourceId(R.styleable.CoinView_panel_left, 0);
        //              panelRightResId = a.getResourceId(R.styleable.CoinView_panel_right, 0);
        //              a.recycle();
        init(context);
    }

    public CoinView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CoinView, defStyle, 0);
        //              Drawable icons[] = loadIcons(context.getResources(), a.getResourceId(R.styleable.CoinView_icons, 0));
        //              CharSequence modes[] = a.getTextArray(R.styleable.CoinView_modes);
        //    a.recycle();
        init(context);
    }
    GradientDrawable d = null;
    private void init(Context context) {
        d = (GradientDrawable) getResources().getDrawable(R.drawable.c1);
        mCardDrawble.face(d);
        mCardDrawble.back(getResources().getDrawable(R.drawable.c2));
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        mCardDrawble.setBounds(0,0,getHeight(),getWidth());
    }

    CardDrawable mCardDrawble = new CardDrawable();

    class CardDrawable extends Drawable {
        Drawable face = null;
        Drawable back = null;
        int w = 200;

        @Override
        public void draw(Canvas canvas) {
            face.setBounds(getBounds());
            canvas.save();
            face.draw(canvas);
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

        public void face(Drawable face) {
            this.face = face;

        }

        public void back(Drawable back) {
            this.back = back;
        }

        @Override
        public void setBounds(Rect bounds) {
            super.setBounds(bounds);
        }
    }

    static Paint paint = new Paint();
    static  {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
    }

    Drawable mTest = null;
    @Override
    protected void onDraw(Canvas canvas) {
        mCardDrawble.draw(canvas);
    }
}
