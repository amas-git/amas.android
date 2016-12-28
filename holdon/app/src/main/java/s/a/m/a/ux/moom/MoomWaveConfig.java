package s.a.m.a.ux.moom;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by amas on 5/29/15.
 */
public class MoomWaveConfig extends MoomDrawConfig {

    public MoomWaveConfig() {
        mBasePaint = new Paint(MoomView.defaultScaleLinePaint);
    }
    @Override
    public void draw(Canvas canvas) {
        Haha(canvas, mMainPath, 100, getBounds().width(), getBounds().width());
    }
    private final float X_SPACE = 20;
    private final double PI2 = 2 * Math.PI;

    public void Haha(Canvas canvas, Path path, float height, long mWaveLength, long mMaxRight ) {
        long mAboveOffset = 12;
        long mWaveHeight = 100;
        //y=Asin(ωx+φ)+k
        double omega = PI2 / mWaveLength;
        float y;
        path.moveTo(0, 0);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (height * Math.sin(omega * x + mAboveOffset) + mWaveHeight);
            path.lineTo(x, y);
        }

        canvas.drawPath(path, getBasePaint());
    }
}
