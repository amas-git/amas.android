package com.cmcm.onews.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcm.onews.C;
import com.cmcm.onews.R;
import com.cmcm.onews.util.StringUtils;

public class NewsNotifyDialogOffline {
    private NewsAlertDialog.Builder builder;
    private NewsAlertDialog mDialog;
    private Context mContext;
    private TextView mTwenty;
    private TextView mThirty;
    private TextView mFifty;
    private TextView mTitle;
    private TextView mCancle;
    private INewsDialogOffline iClick;

    public NewsNotifyDialogOffline(Context context,INewsDialogOffline click) {
        this.mContext = context;
        this.iClick = click;
        initDialog();
    }

    private void initDialog(){
        View root = LayoutInflater.from(mContext).inflate(R.layout.onews__offline_dialog, null);
        builder = new NewsAlertDialog.Builder(mContext);

        mTitle = (TextView) root.findViewById(R.id.offline_title);
        mCancle = (TextView) root.findViewById(R.id.offline_cancle);

        RelativeLayout cancle = (RelativeLayout) root.findViewById(R.id.dialog_offline_cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        mTwenty = (TextView) root.findViewById(R.id.offline_twenty_text);
        root.findViewById(R.id.offline_twenty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClick.clickCount(20);
            }
        });

        mThirty = (TextView) root.findViewById(R.id.offline_thirty_text);
        root.findViewById(R.id.offline_thirty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClick.clickCount(30);
            }
        });

        mFifty = (TextView) root.findViewById(R.id.offline_fifty_text);
        root.findViewById(R.id.offline_fifty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClick.clickCount(50);
            }
        });

        builder.setView(root);
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);
    }

    public void showDialog(long twenty,long thirty,long fifty) {
        if(null != mDialog && !mDialog.isShowing()){
            mTitle.setText(R.string.onews__setting_offline_each);
            mCancle.setText(R.string.onews__cancle);
            mTwenty.setText(StringUtils.getString(C.getAppContext(),R.string.onews__download_twenty,twenty));
            mThirty.setText(StringUtils.getString(C.getAppContext(),R.string.onews__download_thirty,thirty));
            mFifty.setText(StringUtils.getString(C.getAppContext(),R.string.onews__download_fifty,fifty));

            mDialog.show();
        }
    }

    public void dismissDialog() {
        if(null != mDialog && mDialog.isShowing()){
            mDialog.dismiss();
        }
    }
}
