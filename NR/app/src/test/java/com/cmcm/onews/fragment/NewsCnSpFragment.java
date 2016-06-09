package com.cmcm.onews.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cmcm.onews.C;
import com.cmcm.onews.R;
import com.cmcm.onews.ui.NewsActivity;
import com.cmcm.onews.ui.widget.NewsCategoryView_CN;
import com.cmcm.onews.util.DimenSdkUtils;
import com.cmcm.onews.util.UIConfigManager;

/**
 * 国内闪屏页
 */
public class NewsCnSpFragment extends NewsBaseSplFragment {

    private RelativeLayout mNodeCategoryCN;
    private ViewStub mInterestsCN;

    private NewsCategoryView_CN mCategoryViewCN;
    private ImageView mStartLogoCN;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.onews__activity_splash_fragment_cn, container, false);
        mStartLogoCN = (ImageView) rootView.findViewById(R.id.iv_startlogo_cn);
        mNodeCategoryCN = (RelativeLayout) rootView.findViewById(R.id.splash_node_category_cn);
        mNodeCategoryCN.animate().translationX(W);
        mInterestsCN = (ViewStub) rootView.findViewById(R.id.vs_interests_cn);

        mHandler.removeCallbacks(pendingToNews);
        boolean isShowGuide = UIConfigManager.getInstanse(C.getAppContext()).getNEWS_USER_GUIDE();
        mHandler.postDelayed(pendingToNews, isShowGuide ? 1500 : 2500);

        return rootView;
    }

    private Runnable pendingToNews = new Runnable() {
        @Override
        public void run() {
            boolean isShowGuide = UIConfigManager.getInstanse(C.getAppContext()).getNEWS_USER_GUIDE();
            if(isShowGuide && null != mInterestsCN){
                if (mCategoryViewCN == null){
                    mCategoryViewCN = (NewsCategoryView_CN) mInterestsCN.inflate();
                    mCategoryViewCN.initView(getActivity());
                    animaToCategory();
                }
            }else {
                if(null != activity && !activity.isFinishing()){
                    startActivity(new Intent(activity, NewsActivity.class));
                    activity.finish();
                }
            }
        }
    };

    @Override
    public void initReport() {
        if (mCategoryViewCN != null){
            mCategoryViewCN.initReport();
        }
    }

    public static int W = DimenSdkUtils.getScreenWidth();
    private ObjectAnimator animaIn = null;
    private ObjectAnimator animaOut = null;
    private void animaToCategory(){
        animaOut = ObjectAnimator.ofFloat(mStartLogoCN,"x",-W);
        animaOut.setInterpolator(new LinearInterpolator());
        animaOut.setDuration(450);

        animaIn = ObjectAnimator.ofFloat(mNodeCategoryCN,"x",0);
        animaIn.setInterpolator(new LinearInterpolator());
        animaIn.setDuration(450);

        AnimatorSet animaToCategory = new AnimatorSet();
        animaToCategory.playTogether(animaOut, animaIn);
        animaToCategory.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animaToCategory.start();
    }

}
