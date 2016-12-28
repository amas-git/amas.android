package s.a.m.a.ux.moom;



import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class MoomArcStrokeConfig extends MoomDrawConfig  {
	public int mStartAngle          = 135;
	public int mSweepAngle          = 270;
	public RectF mRectF             = null;
	protected boolean mIsClockwise = true;
	
	public MoomArcStrokeConfig() {
		mBasePaint   = new Paint(MoomView.defaultArchStrokePaint);
	}
	
	public MoomArcStrokeConfig(Rect rect) {
		super();
		setBounds(rect);
	}
	
	public void setClockwise(boolean isClockwise) {
		mIsClockwise = isClockwise;
	}
	
	public boolean isClockwise() {
		return mIsClockwise;
	}
	
	@Override
	public void setBounds(Rect bounds) {
		super.setBounds(bounds);
		mRectF  = new RectF(bounds);
		float inset = mBasePaint.getStrokeWidth() / 2;
		mRectF.inset(inset, inset);
	}
	
	public RectF getDrawRectF() {
		return mRectF;
	}
	
	public MoomArcStrokeConfig rotate(int angle) {
		mStartAngle += angle;
		return this;
	}
	
	public MoomArcStrokeConfig zoom(int offset) {
		setBounds(MoomArt.zoomIn(getBounds(), offset));
		return this;
	}

	public void setStrokeWidth(float width) {
		mBasePaint.setStrokeWidth(width);
	}
	
	public float getStrokeWidth() {
		return mBasePaint.getStrokeWidth();
	}
	
	@Override
	public String toString() {
		return String.format("(drawRect %s)", getBounds());
	}

	public float getSweepAngle() {
		return mIsStatic ? mSweepAngle : ((mSweepAngle / 100.0f) * mPercent);
	}

	@Override
	public void draw(Canvas canvas) {
		MoomArt.drawArcStroke(canvas, this);
	}
}
