package lab.whitetree.bonny.box.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.whitetree.bidget.moom.MoomParser;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class NetworkInfoBoard  extends View {

	String mLocalIp = "";
	String mExIp = "";
	String mMac ="";
	
	/**
	 * 间距
	 */
	private int mBoardPadding = -10;
	
	static TextPaint DEFAULT_TP = new TextPaint();
	static Paint DEFAULT_P = new Paint();
	static {
		DEFAULT_TP.setColor(0xFF414f56);
		DEFAULT_TP.setTextSize(MoomParser.DP_TO_PIX(32));
		DEFAULT_TP.setTextScaleX(0.65f);
		DEFAULT_TP.setFakeBoldText(true);
		
		DEFAULT_P.setColor(Color.RED);
		DEFAULT_P.setStyle(Style.STROKE);
		DEFAULT_P.setStrokeWidth(6);
	}
	
	class XArrayList extends ArrayList<BeamText> {
		private static final long serialVersionUID = 1L;
		HashMap<String, Integer> mIndex = new HashMap<String, Integer>();
		public boolean add(String key, BeamText object) {
			boolean ret = add(object);
			if(ret) {
				mIndex.put(key, indexOf(object));
			}
			return ret;
		};
		
		public BeamText get(String key) {
			Integer i = mIndex.get(key);
			if(i != null) {
				return get(i);
			}
			return null;
		}
		
	}
	
	XArrayList mBoards = new XArrayList();
	int mStartHOffset = 0;
	
	public NetworkInfoBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void installBoard(String key, BeamText bt) {
		mBoards.add(key, bt);
	}
	
	public void setBoardContent(String key, String text) {
		BeamText bt = mBoards.get(key);
		if(bt != null) {
			bt.content = text;
			invalidate();
		}
	}
	
	public static class BeamText extends Drawable{
		public Paint  contentPaint = new TextPaint(DEFAULT_TP);
		public Paint  labelPaint   = new TextPaint(DEFAULT_TP);
		public Paint  pathPaint = new Paint(DEFAULT_P);
		public float angle = -30;
		public int textVOffset = 10; 
		
		
		
		
		public String content = "";
		public String label = "";
		public Drawable bg = null;
		public boolean enableDrawPath = false;
		
		
		//public BeamText(String text, )
		Path contentPath = new Path();
		Path labelPath = new Path();
		Context context = null;
		NetworkInfoBoard parent = null;
		int voff = 0;
		float mHAdjusment = 0;
		Rect mXBounds = new Rect();
		float mStart = 0f;
		float mEnd = 1f;
		
		float baseLine = -1;
		int height = MoomParser.DP_TO_PIX(40);
		int gravity = 1;
		float contentWeight = 0.5f;
		float labelWeight = 0.6f;
		int mTextOffset = 0;
		boolean disableText = false;
		
		public void setDisableText(boolean disabled) {
			disableText  = disabled;
		}
		
		public BeamText(Context ctx, NetworkInfoBoard parent) {
			context = ctx;
			labelPaint.setTextScaleX(1.0f);
			setTextOffset(10);
			this.parent = parent;
		}
		
		
		public void setTextOffset(int lrpadding) {
			mTextOffset = MoomParser.DP_TO_PIX(lrpadding);
		}
		
		public void setHAdjusment(float x) {
			mHAdjusment = x;
		}
		
		/**
		 * 0: left 1: right
		 * @param g
		 */
		public void setGravity(int g) {
			gravity = g;
		}
		
		public void setLayoutParam(float angle, float start, float end, int height) {
			this.angle  = angle;
			this.mStart = start;
			this.mEnd   = end;
			this.height = MoomParser.DP_TO_PIX(height);
		}
		
		private void updateXBounds() {
			//System.out.println("HHHddd->" +getBounds().width());
			int hOff = (int)(voff * Math.tan(Math.PI / 180.0 * angle));
			mXBounds.left  = hOff + hadjusment();
			mXBounds.top  = voff;
			mXBounds.right = hOff + hadjusment() + (int)(this.getBounds().width() * (mEnd - mStart) / Math.cos(Math.abs(angle) * Math.PI / 180f));
			mXBounds.bottom = voff+height;
			

			
			if(TextUtils.isEmpty(content)&&TextUtils.isEmpty(label)) {
				return;
			}
			
			float paddingLc = MoomParser.DP_TO_PIX(15);
			
			float contentTextSize = contentWeight * height();
			float labelTextSize   = labelWeight   * height();
			
			contentPaint.setTextSize(contentTextSize);
			labelPaint.setTextSize(labelTextSize);
			
			// 文字
			contentPath.reset();
			labelPath.reset();
			
			if(gravity == 0) {
				float contentLen = labelPaint.measureText(label);
				labelPath.moveTo(mXBounds.left  + mTextOffset,  mXBounds.bottom - labelPaddingBottom);
				labelPath.lineTo(mXBounds.right,  mXBounds.bottom - labelPaddingBottom);
				
				contentPath.moveTo(mXBounds.left  + mTextOffset + contentLen + paddingLc,  mXBounds.bottom - contentPaddingBottom);
				contentPath.lineTo(mXBounds.right,  mXBounds.bottom - contentPaddingBottom);
			
				
			} else if(gravity == 1) {
				float contentLen = contentPaint.measureText(content);
				//float labelLen = labelPaint.measureText(label);
				contentPaint.setTextAlign(Align.RIGHT);
				
				
				contentPath.moveTo(mXBounds.left,  mXBounds.bottom - contentPaddingBottom);
				contentPath.lineTo(mXBounds.right - mTextOffset, mXBounds.bottom - contentPaddingBottom);
				
				
				labelPaint.setTextAlign(Align.RIGHT);
				labelPath.moveTo(mXBounds.left,    mXBounds.bottom - labelPaddingBottom);
				labelPath.lineTo(mXBounds.right - contentLen - paddingLc - mTextOffset, mXBounds.bottom - labelPaddingBottom);
			}
		}
		
		public void setContentPaddingBottom(int x) {
			contentPaddingBottom = MoomParser.DP_TO_PIX(x);
		}
		
		public void setLabelPaddingBottom(int x) {
			labelPaddingBottom = MoomParser.DP_TO_PIX(x);
		}
		
		int contentPaddingBottom = MoomParser.DP_TO_PIX(16);
		int labelPaddingBottom   = MoomParser.DP_TO_PIX(16);
		
		private int hadjusment() {
			return (int)(parent.getWidth()* mHAdjusment / Math.cos(Math.abs(angle) * Math.PI / 180.0 ));
		}

		@Override
		public void draw(Canvas canvas) {
			updateXBounds();
			if(bg != null) {
				bg.setBounds(mXBounds);
				bg.draw(canvas);
			}
			
			if(!disableText) {
				canvas.drawTextOnPath(content, this.contentPath, 0, 0, contentPaint);
			}
			
			if(!disableText) {
				canvas.drawTextOnPath(label, this.labelPath, 0, 0, labelPaint);
			}
		}
		
		private int getLength() {
			return (int)(parent.getWidth()/Math.cos(Math.abs(angle) * Math.PI / 180f)) + adjusment(pathPaint.getStrokeWidth()/2);
		}
		
		public void setBaseLine(float factor) {
			baseLine = factor;
		}
		
		private int calcBaseline() {
			if(baseLine < 0) {
				int tlen = (int)contentPaint.measureText(content);
				return (int)((getLength()-tlen) / 2);
			} else  {
				return (int)(getLength() * baseLine );
			}
		} 
		
		public void setBackgroundResource(int resId) {
			bg = context.getResources().getDrawable(resId);
		}
		
		private int adjusment(float len) {
			return (int)(len * Math.tan(Math.abs(Math.abs(angle * Math.PI / 180f))));
		}

		@Override
		public int getOpacity() {
			return 0;
		}
		@Override
		public void setAlpha(int arg0) {
			
		}
		@Override
		public void setColorFilter(ColorFilter arg0) {
		}

		public int height() {
			//return (int)(contentPaint.getTextSize() / Math.cos(Math.abs(angle) * Math.PI / 180f));
			return height;
		}

		public void setVoffset(int voff) {
			this.voff = voff;
		}
	}
	
	

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		float theAngle = -30; 
			
		canvas.translate(0, (int)(getWidth() * Math.tan(Math.abs(theAngle) * Math.PI / 180f)));
		canvas.rotate(theAngle);
		BeamText current = null;
		BeamText prev = null;
		int lastBottom = 0;
		for(int i=0; i<mBoards.size(); ++i) {
			
			current = mBoards.get(i);
			current.angle = theAngle;
			current.setBounds(getLeft() ,getTop(), getRight(), getBottom());
			
			if(prev != null) {
				current.setVoffset(lastBottom);
			}
			current.draw(canvas);
			prev =  current;
			lastBottom += prev.height();
		}
		
		canvas.restore();
	}
	
}
