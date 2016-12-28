package s.a.m.a.ux.moom;



import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;

public class MoomTextBoardConfig extends MoomDrawConfig {
	protected String text = "";
	
	public MoomTextBoardConfig() {
		mBaseTextPaint = new TextPaint(MoomView.defaultScaleLineTextPaint);
		mBaseTextPaint.setTextAlign(Align.CENTER);
		mBaseTextPaint.setTextSize(20);
	}
	
	public String getText() {
		return text;
	}


//	public void setRect(Rect drawRect) {
//		mRect = drawRect;
//	}
	
	@Override
	public void setBounds(Rect bounds) {
		super.setBounds(bounds);
	}
	
	public void setScaleX(float factor) {
		mBaseTextPaint.setTextScaleX(factor);
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setTextSize(int size) {
		mBaseTextPaint.setTextSize(size);
	}
	
	public void setAlpha(int alpha) {
		mBaseTextPaint.setAlpha(alpha);
	}
	
	public void setTextColor(int color) {
		mBaseTextPaint.setColor(color);
	}
	
	public void setTypeface(Typeface typeface) {
		if(typeface != null) {
			mBaseTextPaint.setTypeface(typeface);
		}
	}

	public void setAlign(Paint.Align align) {
		mBaseTextPaint.setTextAlign(align);
	}

	@Override
	public void draw(Canvas canvas) {
		MoomArt.drawTextBoard(canvas, this);
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setColorFilter(ColorFilter arg0) {
		// TODO Auto-generated method stub
		
	}
}
