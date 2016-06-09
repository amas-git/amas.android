package com.cmcm.onews.location;

import com.cmcm.onews.R;
import com.cmcm.onews.util.SdkPackageUtils;

import android.widget.TextView;
import android.widget.PopupWindow;
import android.graphics.Paint;
import android.os.Handler;
import android.content.Context;

public class BladeView extends android.view.View {
    private OnItemClickListener mOnItemClickListener;
    private String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
            "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z", "#"};
    private int choose = -1;
    private Paint paint = new Paint();
    private boolean showBkg = false;
    private PopupWindow mPopupWindow;
    private TextView mPopupText;
    private Handler handler = new Handler();
    private int mPopWidth;
    private int mPopTextHeight;

    public BladeView(Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BladeView(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public BladeView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(R.color.onews__normal_white));
        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / b.length;
        for (int i = 0; i < b.length; i++) {
            paint.setColor(getResources().getColor(R.color.onews_sdk_detail_comment_item_title_normal));
            paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.bladeview_fontsize));
            paint.setFakeBoldText(true);
            paint.setAntiAlias(true);
            float xPos = width / 2 - paint.measureText(b[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(b[i], xPos, yPos, paint);
            paint.reset();
        }

    }

    @Override
    public boolean dispatchTouchEvent(android.view.MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final int c = (int) (y / getHeight() * b.length);

        switch (action) {
            case android.view.MotionEvent.ACTION_DOWN:
                showBkg = true;
                if (oldChoose != c) {
                    if (c >= 0 && c < b.length) {
                        performItemClicked(c, y);
                        choose = c;
                        invalidate();
                    }
                }

                break;
            case android.view.MotionEvent.ACTION_MOVE:
                if (oldChoose != c) {
                    if (c >= 0 && c < b.length) {
                        performItemClicked(c, y);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case android.view.MotionEvent.ACTION_UP:
            case android.view.MotionEvent.ACTION_CANCEL:
                showBkg = false;
                choose = -1;
                dismissPopup();
                invalidate();
                break;
        }
        return true;
    }

    private void showPopup(int item, float y) {
        if (mPopupWindow == null) {
            handler.removeCallbacks(dismissRunnable);
            android.view.ViewGroup viewGroup = (android.view.ViewGroup) android.view.View.inflate(getContext(), R.layout.onews_local_pop_textview, null);
            mPopupText = (TextView) viewGroup.findViewById(R.id.pop_text);
            mPopWidth = SdkPackageUtils.dip2px(getContext(), 76);
            mPopTextHeight = SdkPackageUtils.dip2px(getContext(), 64);
            mPopupWindow = new PopupWindow(viewGroup, mPopWidth, mPopTextHeight);
            mPopupWindow.setBackgroundDrawable(new android.graphics.drawable.BitmapDrawable());
        }

        String text = "";
        if (item == b.length - 1) {
            text = "#";
        } else {
            text = Character.toString((char) ('A' + item));
        }
        mPopupText.setText(text);
        if (mPopupWindow.isShowing()) {
            mPopupWindow.update(this, 0, -(int) y, mPopWidth, mPopTextHeight);
        } else {
            mPopupWindow.showAsDropDown(this, 0, -(int) y);
        }
    }

    private void dismissPopup() {
        handler.postDelayed(dismissRunnable, 200);
    }

    Runnable dismissRunnable = new Runnable() {

        @Override
        public void run() {
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
            }
        }
    };

    public boolean onTouchEvent(android.view.MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private void performItemClicked(int item, float y) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(b[item]);
            showPopup(item, y);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String s);
    }

}
