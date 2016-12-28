package s.a.m.a.ux.moom;



import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;

public class MoomHandConfig extends MoomArtImageConfig {
	int sweepAngle = 270;
	int startAngle = 90;
	
	public int maxScale = 100;
	public PointF center = null;

	public void setBounds(Rect bounds) {
		super.setBounds(bounds);
		center = new PointF(bounds.exactCenterX(), bounds.exactCenterY());
	}

	// 指针向12点方向时按照如下公式计算偏移角
	public float getRotateAngle() {
		return (sweepAngle * (mPercent / 100.0f)) + startAngle +90;
	}

	public void setStartAngle(int angle) {
		startAngle = angle;
	}
	
	public int getStartAngle() {
		return startAngle;
	}

	public void setSweepAngle(int angle) {
		sweepAngle = angle;
	}
	
	public int getSweepAngle() {
		return sweepAngle;
	}

	@Override
	public void draw(Canvas canvas) {
		MoomArt.drawHand(canvas, this);
	}
}
