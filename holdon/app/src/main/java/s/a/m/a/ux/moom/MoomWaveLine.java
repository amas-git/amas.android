package s.a.m.a.ux.moom;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by amas on 15-5-30.
 */
public class MoomWaveLine extends MoomDrawConfig {
    float dx = 0f;
    float period = 200f;
    float xspacing = 4;

    public MoomWaveLine() {
        mBasePaint = new Paint(MoomView.defaultArchStrokePaint);
        mBasePaint.setStrokeWidth(4);
        dx = (TWO_PI / period);
    }

    public MoomWaveLine period(float period) {
        this.period = period;
        return this;
    }

    public MoomWaveLine theta(float theta) {
        this.theta = theta;
        return this;
    }

    float TWO_PI = (float)(Math.PI * 2);

    @Override
    public void draw(Canvas canvas) {
        Path path = mainPath();
        sinPath(path, 100, getBounds().height(),getBounds().width());
        canvas.save();
        canvas.translate(0,getBounds().height()/2);
        canvas.drawPath(path, getBasePaint());
        canvas.restore();
    }


    float theta = 0f; // start angle
    float amplitude = 20.0f; // wave height

    public void sinPath(Path path, float r, int height, int width) {
        path.moveTo(0, 0);

        for(int x=0; x<width; x++) {
            float y = (float)Math.sin((x+theta) * dx) * amplitude;
            path.lineTo(x, y);
        }
    }
}
