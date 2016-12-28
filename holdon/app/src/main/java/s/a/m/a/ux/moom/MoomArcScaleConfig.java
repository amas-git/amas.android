package s.a.m.a.ux.moom;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;


/**
 * 标尺配置
 * @author amas
 */
public class MoomArcScaleConfig extends MoomDrawConfig {
	private int mStartAngle          = 135;
	public  int mSweepAngle          = 270;
	public  int mScaleLength         = 12;
	public  int mMainScaleLineOffset = 10;
	public  int mScaleTextPadding    = 10;
	public  int mScaleInterval       = 10;
	public  int mMaxScale            = 80;
	public boolean mIsDrawMainScale  = true;
	public boolean mIsDrawScaleText  = true;
	private boolean mIsClockwise     = true;

	Rect mScaleRect  = null;
	
	public MoomArcScaleConfig(Rect bounds) {
		super();
		setBounds(bounds);
	}
	
	public MoomArcScaleConfig() {
		mBasePaint     =  new Paint(MoomView.defaultScaleLinePaint);
		mBaseTextPaint =  new TextPaint(MoomView.defaultScaleLineTextPaint);
	}

	@Override
	public void setBounds(Rect bounds) {
		super.setBounds(bounds);
		mScaleRect = calcScaleRect(bounds);
	}
	
	public void setClockwise(boolean isClockwise) {
		mIsClockwise      = isClockwise;
	}
	
	public TextPaint getScaleTextPaint() {
		return mBaseTextPaint;
	}
	
	public int getStartAngle() {
		return mStartAngle;
	}
	
	public void setStartAngle(int angle) {
		mStartAngle = angle - 90;
	}
	
	public float getScaleWidth() {
		return mBasePaint.getStrokeWidth();
	}
	
	public Paint getScalePaint() {
		return mBasePaint;
	}
	
	public float getMaxScaleTextLength() {
		return getScaleTextPaint().measureText(String.valueOf(mMaxScale)) + 2;
	}
	
	/**
	 * TODO: 支持formatter
	 * @param n
	 * @return 若不想绘制刻度文字，返回null即可
	 */
	public String onFormatScaleText(int n) {
		return String.valueOf(n);
	}
	
	private Rect calcScaleRect(Rect rect) {
		Rect bounds = getBounds();
		Rect zoomIn = rect;
		
		if(getMaxScaleTextLength() + mScaleLength < 0) {
			zoomIn  = MoomArt.zoomIn(bounds, Math.abs(mMainScaleLineOffset + mScaleLength + (int)getMaxScaleTextLength()));
		} else {
			zoomIn  = MoomArt.zoomIn(bounds, mScaleLength);
		}
		return zoomIn;
	}
	
	public Rect getScaleRect() {
		return mScaleRect;
	}
	
	public void scaleIn(int offset) {
		setBounds(MoomArt.zoomIn(getBounds(), offset));
	}
	
	@Override
	public String toString() {
		return String.format("(drawRect %s)", getBounds());
	}

	public void setScaleWidth(float w) {
		getScalePaint().setStrokeWidth(w);
	}

	@Override
	public void draw(Canvas canvas) {
		MoomArt.drawArcScale(canvas, this);
	}
	
	public boolean isClockwise() {
		return mIsClockwise;
	}
}