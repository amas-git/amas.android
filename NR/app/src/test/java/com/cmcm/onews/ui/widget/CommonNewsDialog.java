package com.cmcm.onews.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cmcm.onews.R;

/**
 * Created by Jason.Su on 2015/12/28.
 * com.cmcm.onews.ui.widget
 * des:
 * email:suyanjiao@conew.com
 * 1553202383
 */
public class CommonNewsDialog extends CommonsDialog {
    private TextView mTvCancle;
    private TextView mTvOk;

    public CommonNewsDialog(Context context, INewsNotifyDialogClick l) {
        super(context, l);
    }

    public CommonNewsDialog(Context context, INewsNotifyDialogClick l, String des) {
        this(context, l);
        if (!TextUtils.isEmpty(des)) {
            mContent.setText(des);
        }
    }

    public CommonNewsDialog(Context context, INewsNotifyDialogClick l, String content, String cancleString, String okString) {
        this(context, l, content);
        if (!TextUtils.isEmpty(cancleString)) {
            mTvCancle.setText(cancleString);
        }
        if (!TextUtils.isEmpty(okString)) {
            mTvOk.setText(okString);
        }
    }

    public void setContentText(String content) {
        if (!TextUtils.isEmpty(content)) {
            mContent.setText(content);
        }
    }

    protected void initDialog() {
        View root = LayoutInflater.from(mContext).inflate(R.layout.onews__notify_dialog, null);
        builder = new NewsAlertDialog.Builder(mContext);
        root.findViewById(R.id.btns_container).setVisibility(View.VISIBLE);

        TextView cancle = (TextView) root.findViewById(R.id.dialog_cancle);
        mTvCancle = cancle;
        cancle.setText(R.string.onews__cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mClick) {
                    mClick.clickCancel();
                    dismissDialog();
                }
            }
        });

        TextView ok = (TextView) root.findViewById(R.id.dialog_ok);
        mTvOk = ok;
        ok.setText(R.string.onews_accpet);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mClick) {
                    mClick.clickContinue();
                    dismissDialog();
                }
            }
        });

        TextView des = (TextView) root.findViewById(R.id.dialog_des);
        mContent = des;
        des.setText(R.string.onews__clear_cache_des);

        builder.setView(root);
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                try {
                    if (KeyEvent.KEYCODE_BACK == KeyEvent.KEYCODE_BACK && mIfExitWithBack) {
                        if (mContext != null && mContext instanceof Activity) {
                            ((Activity) mContext).finish();
                            return true;
                        }
                    } else {
                        mDialog.onKeyDown(keyCode, event);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }
}
