package s.a.m.a.ux.moom;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;

/**
 * Created by amas on 15-5-24.
 */
public class MoomLineScaleConfig extends MoomDrawConfig {
    int mainScaleHeight = 0;
    int scaleHeight = 20;
    int scaleWidth = 10;
    int maxScale = 100;
    int mainScaleInterval = maxScale / 10;
    int scalePaddingBottom = 40;
    int scaleTextPadding = 10;
    private Paint scalePaint;
    private Path path;
    boolean isDrawMainText = true;

    int paddingLeft = 20;
    int paddingRight  = 20;

    /**
     * 基线的长度
     * @return
     */
    public float getScaleLineWidth() {
        return getBounds().width() - paddingLeft - paddingRight;
    }

    public MoomLineScaleConfig() {
        mBasePaint   = new Paint(MoomView.defaultArchStrokePaint);
        mBasePaint.setStrokeWidth(4);
        mBaseTextPaint = new TextPaint(MoomView.defaultScaleLineTextPaint);
        mBaseTextPaint.setTextSize(30);
        setScaleHeight(20);
        setMainScaleHeight(40);
    }

    public void setDrawMainText(boolean drawMainText) {
        isDrawMainText  = drawMainText;
    }

    public boolean isDrawMainText() {
        return isDrawMainText;
    }

    public void setMainScaleHeight(int height) {
        this.mainScaleHeight = height;
        //getBasePaint().setStrokeWidth(height);
    }

    /**
     * 刻度的高度
     * @param height
     */
    public void setScaleHeight(int height) {
        this.scaleHeight = height;
    }

    /**
     * 刻度的宽度
     * @param width
     */
    public void setScaleWidth(int width) {
        getScalePaint().setStrokeWidth(width);
    }

    @Override
    public void draw(Canvas canvas) {
        MoomArt.drawlineScale(canvas, this);
    }

    /**
     * 刻度之间的距离
     * @return
     */
    public float getScaleDistance() {
        return getScaleLineWidth() / Float.valueOf(maxScale());
    }

    public int maxScale() {
        return maxScale > 0 ? maxScale : 1;
    }

    public Paint getScalePaint() {
        if(scalePaint == null) {
            scalePaint = new Paint(mBasePaint);
        }
        return scalePaint;
    }

    public int scalePaddingBottom() {
        return scalePaddingBottom;
    }

    public String getScaleText(int i) {
        return String.valueOf(i);
    }

    public Path getPath() {
        if(path == null) {
            path = new Path();
        }
        return path;
    }

    public int getScaleTextPadding() {
        return scaleTextPadding;
    }
}
