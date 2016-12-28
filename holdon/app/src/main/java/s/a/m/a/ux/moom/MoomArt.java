package s.a.m.a.ux.moom;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;

public class MoomArt {
	static Paint DEFAULT_IMG_PAINT = new Paint(Paint.FILTER_BITMAP_FLAG);

	static {
		DEFAULT_IMG_PAINT.setAntiAlias(false);
		DEFAULT_IMG_PAINT.setFilterBitmap(true);
		DEFAULT_IMG_PAINT.setDither(true);
	}

	public static Rect zoomIn(Rect rect, int offset) {
		Rect zoomedIn = new Rect(rect.top + offset, rect.left+offset, rect.right-offset, rect.bottom-offset);
		return zoomedIn;
	}

	public static Rect zoomIn(Rect rect, float factor) {
		int b = (int)(rect.height() * factor);
		int r = (int)(rect.width()  * factor);
		Rect zoomedIn = new Rect(0,0,r,b);
		Rect out = new Rect();
		Gravity.apply(Gravity.CENTER, zoomedIn.width(), zoomedIn.height(), rect, out);
		return out;
	}

	/**
	 * 将widthxhwight的区域放置在anchor的中心
	 * @param anchor
	 * @param width
	 * @param height
	 * @return
	 */
	public static Rect centeralize(Rect anchor, int width, int height) {
		Rect out = new Rect();
		Gravity.apply(Gravity.CENTER, width, height, anchor, out);
		return out;
	}


	public static void drawCircleLabel(Canvas canvas, MoomCircleLabelConfig c) {
		drawTextOnCircle(canvas,c.getText(), c.cx, c.cy, c.r, c.getBaseTextPaint());
	}

	/**
	 * tp必须设置为左对齐
	 * TP.setTextAlign(Align.LEFT);
	 * @param canvas
	 * @param text
	 * @param cx
	 * @param cy
	 * @param r
	 * @param tp
	 */
	public static void drawTextOnCircle(Canvas canvas, String text, int cx, int cy, int r, TextPaint tp) {
		Path circle = new Path();
		circle.addCircle(cx, cy, r, Direction.CW);
		float textLength = tp.measureText(text);

		canvas.save();
		canvas.rotate(-270, cx, cy);
		canvas.drawTextOnPath(text, circle, -textLength/2+(int)(r*Math.PI), 0, tp);
		canvas.restore();
	}


	/**
	 * 绘制文字
	 * @param canvas
	 * @param c
	 */
	public static void drawTextBoard(Canvas canvas, MoomTextBoardConfig c) {
		String drawText = c.getText();
		float size =  c.getBaseTextPaint().getTextSize();
		canvas.drawText(drawText, c.getBounds().exactCenterX(),c.getBounds().exactCenterY() + size / 2, c.getBaseTextPaint());

	}

    public static void drawlineScale(Canvas canvas, MoomLineScaleConfig drawConfig) {
        if(!drawConfig.isVisible()) {
            return;
        }

        Rect  bounds  = drawConfig.getBounds();



        int scalePaddingBottom = drawConfig.scalePaddingBottom();

        canvas.save();
        // move to left bottom
        canvas.translate(drawConfig.paddingLeft, bounds.height() - scalePaddingBottom);

        // 绘制baseLine
        canvas.drawLine(0,0, drawConfig.getScaleLineWidth(),0, drawConfig.getBasePaint());
        canvas.save();

        int scaleHeight = 0;
        for(int i=0; i<drawConfig.maxScale+1; ++i) {
            canvas.save();
            canvas.translate(i*drawConfig.getScaleDistance(), 0);

            boolean isMainScale = i % drawConfig.mainScaleInterval == 0;
            if(isMainScale) {
                scaleHeight = drawConfig.mainScaleHeight;
            } else {
                scaleHeight = drawConfig.scaleHeight;
            }
            // DRAW SCALE
            canvas.drawLine(0,0,0,-scaleHeight, drawConfig.getScalePaint());

            // DRAW SCALE TEXT
            String scaleText = drawConfig.getScaleText(i);
            float textLength = drawConfig.getBaseTextPaint().measureText(scaleText);
            float textHeight = drawConfig.getBaseTextPaint().getTextSize();

            if(isMainScale && drawConfig.isDrawMainText) {
                Path path = drawConfig.getPath();
                path.reset();
                path.moveTo(-textLength/2.0f, drawConfig.getScaleTextPadding() + textHeight);
                path.lineTo(+textLength/2.0f, drawConfig.getScaleTextPadding() + textHeight);

                //canvas.drawPath(path, drawConfig.getScalePaint());
                canvas.drawTextOnPath(scaleText,path,0,0,drawConfig.getBaseTextPaint());
            }
            //canvas.drawTextOnPath();
            canvas.restore();
        }
        canvas.restore();
        canvas.restore();
    }

	public static void drawArcScale(Canvas canvas, MoomArcScaleConfig drawConfig) {
        if(!drawConfig.isVisible()) {
            return;
        }
		Rect  scaleRect  = drawConfig.getScaleRect();
		int   startAngle = drawConfig.getStartAngle();
		float scaleWidth = drawConfig.getScaleWidth();
		int scaleInterval= drawConfig.mScaleInterval;
		int maxScale     = drawConfig.mMaxScale;
		int sweepAngle   = drawConfig.mSweepAngle;
		boolean isClockwise = drawConfig.isClockwise();

		canvas.save();
		canvas.rotate(startAngle, scaleRect.exactCenterX(), scaleRect.exactCenterY());
		for (int i = 0; i <= maxScale; ++i) {
			PointF pa = new PointF(scaleRect.exactCenterX(), scaleRect.height() - scaleWidth);   // 刻度线开始坐标
			PointF pz = new PointF(scaleRect.exactCenterX(), pa.y + drawConfig.mScaleLength);    // 刻度线结束坐标

			float angle = i * (Float.valueOf(sweepAngle) / maxScale); // 计算画布旋转角度
			if (!isClockwise) {
				angle = -angle;
			}

			// 带文字的主刻度线
			if (drawConfig.mIsDrawMainScale) {
				if (i % scaleInterval == 0) {
					if (drawConfig.mIsDrawScaleText) {
						// 绘制文字
						drawArcScaleLine(canvas,
                                         drawConfig.mainPath(),
                                         drawConfig._rect_1(),
								         drawConfig.getScalePaint(),
						                 pa,
						                 pz,
								         angle+startAngle,
								         drawConfig.getScaleTextPaint(),
								         drawConfig.onFormatScaleText(i),
								         drawConfig.mScaleTextPadding,
								         drawConfig.mMainScaleLineOffset
								         );
					} else {
						// 无文字
						drawArcScaleLine(canvas,
                                 drawConfig.mainPath(),
                                 drawConfig._rect_1(),
						         drawConfig.getScalePaint(),
				                 pa,
				                 pz,
						         angle+startAngle,
						         null,
						         null,
						         0,
						         drawConfig.mMainScaleLineOffset
						         );
					}
				}
			}

			// 基本刻度线
			drawArcScaleLine(canvas, drawConfig.mainPath(), drawConfig._rect_1(), drawConfig.getScalePaint(), pa, pz);

			// 旋转一个基本单位
			angle = Float.valueOf(sweepAngle) / maxScale;
			if (!isClockwise) {
				angle = -angle;
			}
			canvas.rotate(angle, scaleRect.exactCenterX(), scaleRect.exactCenterY());
		}
		canvas.restore();
	}

	/**
	 * 绘制无刻度文字的刻度线
	 * @param canvas
	 * @param linePaint
	 * @param pa 刻度线起点(靠近画布中心的点)
	 * @param pz 刻度线终点
	 * @param angle
	 */
	public static void drawArcScaleLine(Canvas canvas, Path path, Rect _rect, Paint linePaint, PointF pa, PointF pz) {
		drawArcScaleLine(canvas, path, _rect, linePaint, pa, pz, 0, null, null, 0, 0);
	}

	/**
	 * 画刻度线
	 * @param canvas
     * @param path  纯粹是绘图用资源类似于画笔之类的
	 * @param linePaint
	 * @param pa 刻度线起点(靠近画布中心的点)
	 * @param pz 刻度线重点
	 * @param angle 刻度值偏移角
	 * @param textPaint 刻度值画笔
	 * @param scaleText 刻度文字
	 * @param scaleTextPadding 刻度文字与刻度线之间的间隔
	 * @param primaryScaleLineLength 主刻度线相对于基本刻度线增加的长度
	 */
	public static void drawArcScaleLine(
			Canvas canvas,
            Path path,
            Rect _bounds,
			Paint linePaint,
			PointF pa,
	        PointF pz,
			float angle,
			TextPaint textPaint,
			String scaleText,
			float scaleTextPadding,
			float primaryScaleLineLength) {

		if(!TextUtils.isEmpty(scaleText)) {
			// 主刻度读数
			canvas.save();

			// 画布移动到
			canvas.translate(pa.x, pa.y - scaleTextPadding - primaryScaleLineLength); // C1
			canvas.save();

			canvas.rotate(-angle);                                                // C2
			float numMarkLen = textPaint.measureText(scaleText);
			// 主刻度度数绘图路径
            path.reset();
            path.moveTo(-(numMarkLen / 2.0f), 0);
            path.lineTo(+(numMarkLen / 2.0f), 0);

			// 在绘图路径中央绘制文字
			float vOffset = measureTextHeight(textPaint, _bounds, scaleText) / 2;
			canvas.drawTextOnPath(scaleText, path, 0, vOffset, textPaint);

			canvas.restore(); // C1 END
			canvas.restore(); // C2 END
		}

        path.reset();

        path.moveTo(pa.x, pa.y - primaryScaleLineLength);
        path.lineTo(pz.x, pz.y);
		canvas.drawPath(path, linePaint);
	}

	public static void drawHand(Canvas canvas, MoomHandConfig drawConfig) {
		Drawable drawable = drawConfig.getDrawable();

		canvas.save();
		float angle = drawConfig.getRotateAngle();
		if (!drawConfig.isClockwise()) {
			angle = drawConfig.getSweepAngle() - angle % 360;
		}
		canvas.rotate(angle, drawConfig.center.x, drawConfig.center.y);
		drawable.setBounds(drawConfig.getBounds());
		drawable.draw(canvas);
		canvas.restore();
	}

	public static void drawImage(Canvas canvas, MoomArtImageConfig drawConfig) {
		Drawable drawable = drawConfig.getDrawable();
		if (drawConfig.needDraw()) {
			canvas.save();
			drawable.setBounds(drawConfig.getBounds());
			drawable.setFilterBitmap(true);
			drawable.setDither(true);
			drawable.draw(canvas);
			canvas.restore();
		}
	}

	/**
	 * 测量文字显示高度
	 * @param textPaint
	 * @param text
	 * @return
	 */
	public static float measureTextHeight(TextPaint textPaint, Rect _bounds, String text) {
		textPaint.getTextBounds(text, 0, text.length(), _bounds);
		return _bounds.height();
	}

	/**
	 * 绘制弧线
	 * @param canvas
	 * @param drawConfig
	 */
	public static void drawArcStroke(Canvas canvas, MoomArcStrokeConfig drawConfig) {
        if(!drawConfig.isVisible()) {
            return;
        }
		Path  path = drawConfig.mainPath();

		float sweepAngle = drawConfig.getSweepAngle();
		if (!drawConfig.isClockwise()) {
			sweepAngle = -sweepAngle;
		}


		path.addArc(drawConfig.getDrawRectF(), drawConfig.mStartAngle, sweepAngle);
		//drawConfig.mBasePaint.setShader(createSweepGradient(drawConfig.mRect.centerX(), drawConfig.mRect.centerY()));
		canvas.drawPath(path, drawConfig.getBasePaint());
	}

	// 两中颜色之间完全没有过度
	@Deprecated
	public static SweepGradient createSweepGradient(int cx, int cy) {
		int[] colors   = new int[] { Color.GREEN, Color.YELLOW};
		//float[] positions = new float[] {0.3f, 0.3f, 0.3f};
		SweepGradient sg = new SweepGradient(cx, cy, colors, null);
		return sg;

//		int[] mColors   = new int[] {Color.GREEN, Color.YELLOW, Color.RED};
//		float[] positions = new float[] {0.2f, 0.8f, 1.0f};
//		Shader s = new SweepGradient(100, 100, mColors, positions);
	}

	/**
	 * 设置阴影效果
	 * @param paint
	 */
	public static void setShadowLayer(Paint paint) {
		paint.setShadowLayer(2, 2f, 0.5f, Color.GRAY);
	}


}