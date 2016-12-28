package s.a.m.a.ux.moom;



import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.text.TextPaint;

/**
 * 
 * @author amas
 *
 */
public class MoomCircleLabelConfig extends MoomTextBoardConfig {
	public int r  = 0;
	public int cx = 0;
	public int cy = 0;
	
	public MoomCircleLabelConfig() {
		mBaseTextPaint = new TextPaint(MoomView.defaultScaleLineTextPaint);
		mBaseTextPaint.setTextAlign(Align.LEFT);
		mBaseTextPaint.setTextSize(20);
	}
	
	@Override
	public void setBounds(Rect bounds) {
		super.setBounds(bounds);
		cx = bounds.centerX();
		cy = bounds.centerY();
		r = Math.min(bounds.width(), bounds.height()) / 2;
	}
	
	@Override
	public void draw(Canvas canvas) {
		MoomArt.drawCircleLabel(canvas, this);
	}
}
