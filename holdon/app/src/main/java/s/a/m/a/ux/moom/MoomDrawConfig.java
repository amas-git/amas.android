package s.a.m.a.ux.moom;


import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

public abstract class MoomDrawConfig extends Drawable {
    protected Path mMainPath = new Path();
    protected Paint mBasePaint;
    protected TextPaint mBaseTextPaint;
    protected String mTag = "";
    protected float mPercent = 0; // XXX(TODO): default percent
    protected boolean mIsStatic = false;
    protected String mId = "";
    protected Rect _rect_1 = null;
    protected String mExpr = null;

    public Rect _rect_1() {
        if(_rect_1 == null) {
            _rect_1 = new Rect();
        }
        return _rect_1;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public void setPercent(float percent) {
        if (mIsStatic) {
            return;
        }

        if (percent >= 100.f) {
            mPercent = 0 + percent % 101;
        } else {
            mPercent = percent;
        }
    }

    /**
     * 获取正规化的百分比, 0-1之间
     * @return
     */
    public float getNormalizedPercent() {
        return mPercent/100f;
    }

    public Path mainPath() {
        mMainPath.reset();
        return mMainPath;
    }

    public void setStatic(boolean isStatic) {
        mIsStatic = isStatic;
    }

    public boolean isStatic() {
        return mIsStatic;
    }

    public String getTag() {
        return mTag;
    }

    public boolean isTaggedWith(String tag) {
        return mTag.contains(tag);
    }

    public void setExpr(Context context, String expr) {
        mExpr = expr;
    }

    public String getExpr() {
        return mExpr;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public TextPaint getBaseTextPaint() {
        return mBaseTextPaint;
    }

    public Paint getBasePaint() {
        return mBasePaint;
    }

    @Override
    public void setAlpha(int alpha) {
        if (mBasePaint != null) {
            mBasePaint.setAlpha(alpha);
        }
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    public void setColorFilter(ColorFilter cf) {

    }

    public float getPercent() {
        return mPercent;
    }
}