package com.cmcm.onews.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cmcm.onews.R;

public class NewsNotifyDialogClearCache {
    private NewsAlertDialog.Builder builder;
    private NewsAlertDialog mDialog;
    private Context mContext;
    private TextView mCancle;
    private TextView mOk;
    private TextView mDes;

    private INewsNotifyDialogClick mClick;

    public NewsNotifyDialogClearCache(Context context, INewsNotifyDialogClick l) {
        this.mContext = context;
        this.mClick = l;
        initDialog();
    }

    private void initDialog(){
        View root = LayoutInflater.from(mContext).inflate(R.layout.onews__notify_dialog, null);
        builder = new NewsAlertDialog.Builder(mContext);

        mCancle = (TextView) root.findViewById(R.id.dialog_cancle);
        mCancle.setText(R.string.onews__cancle);
        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mClick) {
                    mClick.clickCancel();
                }
            }
        });

        mOk = (TextView) root.findViewById(R.id.dialog_ok);
        mOk.setText(R.string.onews__clear);
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mClick) {
                    mClick.clickContinue();
                }
            }
        });

        mDes = (TextView) root.findViewById(R.id.dialog_des);
        mDes.setText(R.string.onews__clear_cache_des);

        root.findViewById(R.id.btns_container).setVisibility(View.VISIBLE);

        builder.setView(root);
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);
    }

    public void showDialog() {
        if(null != mDialog && !mDialog.isShowing()){
            mCancle.setText(R.string.onews__cancle);
            mOk.setText(R.string.onews__clear);
            mDes.setText(R.string.onews__clear_cache_des);
            mDialog.show();
        }
    }

    public void dismissDialog() {
        if(null != mDialog && mDialog.isShowing()){
            mDialog.dismiss();
        }
    }
}
