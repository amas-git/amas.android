package lab.whitetree.bonny.box.ui;

import lab.whitetree.bonny.box.R;

import org.whitetree.bidget.moom.MoomParser;
import org.whitetree.bidget.moom.MoomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

public class RainbowButton extends Button {

	PopupWindow mPopup = null;
	private LayoutInflater mInflater;
	MoomView mProgress = null;
	
	public RainbowButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public void dissmiss() {
		if(mPopup.isShowing()) {
			mPopup.dismiss();
		}
	}
	
	public void setProgress(int progress) {
		mProgress.setPercent(":percent", progress);
		mProgress.setText(":pwercent", String.format("%d%%", progress));
	}

	private void init(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.rainbowbutton, null);
		mProgress = (MoomView)view.findViewById(R.id.moom);
		mPopup = new PopupWindow(context);
		
		mPopup.setContentView(view);
		mPopup.setFocusable(true);
		mPopup.setTouchable(true);
		mPopup.setOutsideTouchable(false);
		mPopup.setBackgroundDrawable(null);
		mPopup.setWidth(MoomParser.DP_TO_PIX(200));
		mPopup.setHeight(MoomParser.DP_TO_PIX(200));
	}
	
//	@Override
//	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//		super.onLayout(changed, left, top, right, bottom);
//	}
//
//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//	}

	public void showProgress() {
		mPopup.showAtLocation(this, Gravity.CENTER, 0, 0);
	}
}
