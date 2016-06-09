package com.cmcm.onews.ui;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cmcm.onews.R;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.ui.widget.DislikePopup;
import com.cmcm.onews.ui.widget.PopupWindowUtils;
import com.cmcm.onews.util.DimenSdkUtils;

/**
 * dislike offline 引导弹窗
 */
public class NewsGuidePopup {
    private PopupWindow popupWindow;
    private ImageView mUp;
    private TextView mTitle;
    private static final int BELOW_H = DimenSdkUtils.dp2px(5);
    private DislikePopup.IDislikeDismiss l;

    public NewsGuidePopup(DislikePopup.IDislikeDismiss dislikeDismiss) {
        init();
        this.l = dislikeDismiss;
    }

    private void init() {
        popupWindow = PopupWindowUtils.getBasePopupWindow(R.layout.onews__guide_popup, NewsSdk.INSTAMCE.getAppContext(), WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, -1);
        View root = popupWindow.getContentView();
        mUp = (ImageView) root.findViewById(R.id.guide_up);
        mTitle = (TextView) root.findViewById(R.id.guide_title);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(null != l){
                    l.onDismiss();
                }
            }
        });
    }

    public void showDislikeGuide(View anchor, int anchorX){
        if(null == popupWindow){
            return;
        }

        if(popupWindow.isShowing()){
            return;
        }

        mTitle.setText(R.string.onews__dislike_guide);
        mUp.animate().setDuration(0).translationX(anchorX);
        popupWindow.showAsDropDown(anchor, 0, BELOW_H);
    }

    public void showOfflineGuide(View anchor, int anchorX){
        if(null == popupWindow){
            return;
        }

        if(popupWindow.isShowing()){
            return;
        }

        mTitle.setText(R.string.onews__offline_guide);
        mUp.animate().setDuration(0).translationX(anchorX / 3);
        popupWindow.showAsDropDown(anchor,0,0);
    }

    public void dismiss() {
        if(null != popupWindow){
            popupWindow.dismiss();
        }
    }
}
