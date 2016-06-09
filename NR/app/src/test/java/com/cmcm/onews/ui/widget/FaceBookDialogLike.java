package com.cmcm.onews.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.cmcm.onews.R;



import com.cmcm.onews.infoc.newsindia_fblikes;

/**
 * 引导用户去FaceBook
 */
public class FaceBookDialogLike {
    private NewsAlertDialog.Builder builder;
    private NewsAlertDialog mDialog;
    private Context mContext;
    private Button mOk;
    private Button mCancle;

    private INewsNotifyDialogClick mClick;
    //1-点击go  2-点击thanks 3-返回或点击其它区域
    private final  int FB_LIKE_CLICK_ACTION_GO = 1;
    private final  int FB_LIKE_CLICK_ACTION_THANKS = 2;
    private final  int FB_LIKE_CLICK_ACTION_OTHER = 3;

    public FaceBookDialogLike(Context context, INewsNotifyDialogClick l) {
        this.mContext = context;
        this.mClick = l;
        initDialog();
    }

    private void initDialog(){
        View root = LayoutInflater.from(mContext).inflate(R.layout.facebook_like_dialog, null);
        builder = new NewsAlertDialog.Builder(mContext);

        mCancle = (Button) root.findViewById(R.id.facebook_no_text);
        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mClick) {
                    mClick.clickCancel();
                    reportNewsindia_fblikes(FB_LIKE_CLICK_ACTION_THANKS);
                }
            }
        });

        mOk = (Button) root.findViewById(R.id.facebook_go_text);
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mClick) {
                    mClick.clickContinue();
                    reportNewsindia_fblikes(FB_LIKE_CLICK_ACTION_GO);
                }
            }
        });


        builder.setView(root);
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);
        //监听返回键
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (null != mClick) {
                    mClick.clickCancel();
                    reportNewsindia_fblikes(FB_LIKE_CLICK_ACTION_OTHER);
                }
                return false;
            }
        });

        //监听点击空白区域
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (null != mClick) {
                    mClick.clickCancel();
                    reportNewsindia_fblikes(FB_LIKE_CLICK_ACTION_OTHER);
                }
            }
        });

    }

    public void showDialog() {
        if(null != mDialog && !mDialog.isShowing()){
            mDialog.show();
        }
    }

    public void dismissDialog() {
        if(null != mDialog && mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    private void reportNewsindia_fblikes(int action){

        newsindia_fblikes fblikes = new newsindia_fblikes();
        fblikes.action(action);
        fblikes.report();
    }
}
