package com.cmcm.onews.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

/**
 *  todo
 *  fixed 2619534321
 *  http://trace-abord.cm.ijinshan.com/index/lists?thever=66&field=%E6%97%A0&field_content=&date=20151126&version=
 */
public class NewsViewPager extends ViewPager{
    public NewsViewPager(Context context) {
        super(context);
    }

    public NewsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        return i;
    }

    @Override
    public void draw(Canvas canvas) {
        try {
            super.draw(canvas);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("NewsViewPager",e.toString());
        }
    }
}
