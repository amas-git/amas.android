package s.a.m.a.ux.moom;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by amas on 15-5-30.
 */
public class MoomRectConfig extends MoomDrawConfig {
    public MoomRectConfig() {
        mBasePaint = new Paint(MoomView.defaultArchStrokePaint);
        mBasePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas) {
        float length = getBounds().width() * getNormalizedPercent();
        canvas.drawRect(0,0,length,getBounds().height(), mBasePaint);
    }
}
