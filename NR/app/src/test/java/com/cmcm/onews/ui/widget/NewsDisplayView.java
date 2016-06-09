package com.cmcm.onews.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcm.onews.C;
import com.cmcm.onews.R;
import com.cmcm.onews.infoc.newsindia_startview;
import com.cmcm.onews.ui.NewsActivity;
import com.cmcm.onews.util.UIConfigManager;
import com.cmcm.splash.SplashHelper;

/**
 * 开屏页
 * Created by Sam on 2016/3/13.
 */
public class NewsDisplayView extends RelativeLayout implements View.OnClickListener {

    private Handler mHandler = new Handler();
    private Activity mActivity;
    private LinearLayout mSkipBtn;
    private ImageView mImage;
    private TextView mSkipTv;
    public boolean isReported = false;// 是否已经上报过了

    public NewsDisplayView(Context context) {
        super(context);
    }

    public NewsDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsDisplayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initView(Activity act){
        this.mActivity = act;
        findView();
        setListener();
    }

    private void findView() {
        mImage = (ImageView) findViewById(R.id.image_display);
        mSkipBtn = (LinearLayout) findViewById(R.id.btn_display_skip);
        mSkipTv = (TextView) findViewById(R.id.tv_display);
    }

    public void changeLanguage(){
        mSkipTv.setText(R.string.onews__guide_categoty_skip);
    }

    public void loadRes(){
        SplashHelper.getIns().loadResBitmap();
        SplashHelper.getIns().showResBitmap(mImage);
    }

    private void setListener() {
        mSkipBtn.setOnClickListener(this);
        mImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivity != null && !mActivity.isFinishing()) {
//                    NewsActivity.startActivity(mActivity);
                    mActivity.finish();
                }
                isReported = true;
                reportStartview(1);
                UIConfigManager.getInstanse(C.getAppContext()).setNEWS_USER_GUIDE();
                SplashHelper.getIns().onClick(mImage);
            }
        });
    }

    public void delayedToNews() {
        mHandler.removeCallbacks(pendingToNews);
        mHandler.postDelayed(pendingToNews, 3000L);
    }


    private Runnable pendingToNews = new Runnable() {
        @Override
        public void run() {
            if (null != mActivity && !mActivity.isFinishing()){
                NewsActivity.startActivity(mActivity);
                mActivity.finish();
                UIConfigManager.getInstanse(C.getAppContext()).setNEWS_USER_GUIDE();
                if (!isReported){
                    reportStartview(3);
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_display_skip:
                skipToNews();
                break;
        }
    }

    private void skipToNews() {
        if (null != mActivity && !mActivity.isFinishing()){
            mHandler.removeCallbacks(pendingToNews);
            NewsActivity.startActivity(mActivity);
            mActivity.finish();
            UIConfigManager.getInstanse(C.getAppContext()).setNEWS_USER_GUIDE();
            if (!isReported){
                reportStartview(2);
            }
        }

    }

    public void reportStartview(int ifclick) {
        newsindia_startview startview = new newsindia_startview();
        startview.ifclick(ifclick);
        startview.ctype(SplashHelper.getIns().getSplashId());
        startview.report();
    }

}
