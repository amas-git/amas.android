package s.a.m.a.ux.moom;


import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

/**
 * Created by amas on 5/19/15.
 */
public class MoomView {
    public static boolean OPTION_DISCARD_BG = false;

    public static final int ALPHA_LOW = 50;
    public static final int ALPHA_MID = 100;
    public static final int ALPHA_HIG = 200;
    public static final int ALPHA_MAX = 255;

    public static Paint defaultArchStrokePaint = null;
    public static Paint defaultScaleLinePaint = null;
    public static TextPaint defaultScaleLineTextPaint = null;
    public static Paint defaultHandPaint = null;

    static {
        defaultArchStrokePaint = new Paint();
        defaultArchStrokePaint.setStyle(Paint.Style.STROKE);
        defaultArchStrokePaint.setStrokeWidth(16);
        defaultArchStrokePaint.setColor(Color.RED);
        defaultArchStrokePaint.setAntiAlias(true);
        defaultArchStrokePaint.setAlpha(ALPHA_MAX);
        // Shader s = new RadialGradient(rect.exactCenterX(),
        // rect.exactCenterY(), 50, Color.RED, Color.YELLOW, TileMode.MIRROR);
        // defaultArchStrokePaint.setShader(s);

        defaultScaleLineTextPaint = new TextPaint();
        defaultScaleLineTextPaint.setColor(Color.RED);
        defaultScaleLineTextPaint.setAntiAlias(true);
        defaultScaleLineTextPaint.setAlpha(ALPHA_MID);
        defaultScaleLineTextPaint.setStrokeWidth(2);
        defaultScaleLineTextPaint.setTextSize(16);
        // defaultScaleLinePaint.setTextScaleX(1.2f);

        defaultScaleLinePaint = new Paint();
        defaultScaleLinePaint.setStyle(Paint.Style.STROKE);
        defaultScaleLinePaint.setColor(Color.RED);
        defaultScaleLinePaint.setAntiAlias(true);
        defaultScaleLinePaint.setAlpha(ALPHA_MAX);
        defaultScaleLinePaint.setStrokeWidth(2);

        defaultHandPaint = new Paint();
        defaultHandPaint.setStyle(Paint.Style.FILL);
        defaultHandPaint.setColor(Color.RED);
        defaultHandPaint.setAntiAlias(true);
        defaultHandPaint.setAlpha(ALPHA_MAX);
        // defaultHandPaint.setStrokeWidth(2);
        defaultHandPaint.setStrokeCap(Paint.Cap.ROUND);
    }

}
