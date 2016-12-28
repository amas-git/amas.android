package s.a.m.a.ux.moom;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class MoomArtImageConfig extends MoomDrawConfig {
	Bitmap mBitmap = null;
	Drawable mDrawable = null;
	protected float mZoom = 1f;
	int alpha = 255;
	
	protected boolean mIsClockwise = true;
	
	public void setClockwise(boolean isClockwise) {
		mIsClockwise = isClockwise;
	}
	
	@Override
	public void setBounds(Rect bounds) {
		super.setBounds(MoomArt.zoomIn(bounds, mZoom));
	}

	public Drawable getDrawable() {
		return mDrawable;
	}

	public void setDrawable(Drawable drwable) {
		mDrawable = drwable;
		mDrawable.setAlpha(alpha);
	}

	// TODO: move to base class???
	public boolean needDraw() {
		return mDrawable != null;
	}

	/**
	 * 绘图区域占指定区域的百分比
	 * 
	 * @param factor
	 */
	public void setZoom(float factor) {
		mZoom = factor;
		setBounds(MoomArt.zoomIn(getBounds(), mZoom));
	}

	/**
	 * @param alpha
	 */
	public void setAlpha(int alpha) {
		this.alpha = alpha;
		if(mDrawable != null) {
			mDrawable.setAlpha(alpha);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		MoomArt.drawImage(canvas, this);
	}
	
	public boolean isClockwise() {
		return mIsClockwise;
	}
}
