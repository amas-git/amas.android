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
 * Created by Jason.Su on 2016/2/23.
 * com.cmcm.onews.ui.widget
 * des:
 * email:suyanjiao@conew.com
 * 1553202383
 */
public class CommonSingleDialog extends CommonsDialog {

    public CommonSingleDialog(Context context, INewsNotifyDialogClick l) {
        super(context, l);
    }

    public CommonSingleDialog(Context context, INewsNotifyDialogClick l, String des) {
        this(context, l);
        if (!TextUtils.isEmpty(des)) {
            mContent.setText(des);
        }
    }

    @Override
    protected void initDialog() {
        View root = LayoutInflater.from(mContext).inflate(R.layout.onews_notify_single_dialog, null);
        builder = new NewsAlertDialog.Builder(mContext);
        root.findViewById(R.id.single_choice).setOnClickListener(new View.OnClickListener() {
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
