package com.cmcm.onews.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Jason.Su on 2016/2/24.
 * com.cmcm.onews.ui.widget
 * des:
 * email:suyanjiao@conew.com
 * 1553202383
 */
public abstract class CommonsDialog {
    protected Context mContext;
    protected INewsNotifyDialogClick mClick;
    protected TextView mContent;
    protected NewsAlertDialog.Builder builder;
    protected NewsAlertDialog mDialog;
    protected boolean mIfExitWithBack = true;
    protected View mCancel = null;


    public CommonsDialog(Context context,INewsNotifyDialogClick l ) {
        this.mClick = l;
        this.mContext = context;
        initDialog();
    }
    public CommonsDialog(Context context, INewsNotifyDialogClick l, String des) {
        this(context, l);
        if (!TextUtils.isEmpty(des)) {
            mContent.setText(des);
        }
    }

    protected abstract void initDialog();

    public void setCanceledOnTouchOutside(boolean flag){
        if (mDialog!=null){
            mDialog.setCanceledOnTouchOutside(flag);
        }
    }
    public void setIfExitWhitBack(boolean ifShouldCancelWithBack){
        mIfExitWithBack = ifShouldCancelWithBack;
    }

    public void showDialog() {
        if (null != mDialog && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void dismissDialog() {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
