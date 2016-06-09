package com.cmcm.onews.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.RelativeLayout;

import com.cmcm.onews.C;
import com.cmcm.onews.R;
import com.cmcm.onews.ui.NewsActivity;
import com.cmcm.onews.ui.widget.NewsDisplayView;
import com.cmcm.onews.ui.widget.NewsElephantView;
import com.cmcm.onews.util.UIConfigManager;
import com.cmcm.splash.SplashHelper;

/**
 * 印度新闻闪屏页
 */
public class NewsInSpFragment extends NewsBaseSplFragment {
    private RelativeLayout mNodeCategory;
    private ViewStub mVSInterests;

    private ViewStub mElephantStub;
    private NewsElephantView mElephantView;

    private RelativeLayout mDisplayLayout;
    private ViewStub mDispalyStub;
    private NewsDisplayView mNewsDisplayView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.onews__activity_splash_fragment, container, false);
        mNodeCategory = (RelativeLayout) rootView.findViewById(R.id.splash_node_category);
        mNodeCategory.animate().translationX(NewsElephantView.W);
        mVSInterests = (ViewStub) rootView.findViewById(R.id.vs_interests);
        mElephantStub = (ViewStub) rootView.findViewById(R.id.splash_elephant);

        mDisplayLayout = (RelativeLayout) rootView.findViewById(R.id.splash_display_layout);
        mDispalyStub = (ViewStub) rootView.findViewById(R.id.splash_display);

        long splashDuration;
        boolean isShowGuide = UIConfigManager.getInstanse(C.getAppContext()).getNEWS_USER_GUIDE();
        // TODO 是否需要展示直播开屏
        boolean isDisplay = SplashHelper.getIns().isShouldShowSplash();
        if (isDisplay && !isShowGuide) {
            splashDuration = 3000L;
            if (null == mNewsDisplayView) {
                mNewsDisplayView = (NewsDisplayView) mDispalyStub.inflate();
                mNewsDisplayView.initView(activity);
                mNewsDisplayView.changeLanguage();
                mNewsDisplayView.loadRes();
            }
        } else {
            splashDuration = 2500L;
            mDisplayLayout.animate().translationX(NewsElephantView.W);
            if(null == mElephantView){
                mElephantView = (NewsElephantView) mElephantStub.inflate();
                mElephantView.init(mNodeCategory,mVSInterests,mDisplayLayout,mDispalyStub,activity);
                mElephantView.layout();
            }
        }

        mHandler.removeCallbacks(pendingToNews);
        mHandler.postDelayed(pendingToNews, isShowGuide ? 1500 : splashDuration);

        return rootView;
    }

    private Runnable pendingToNews = new Runnable() {
        @Override
        public void run() {
            boolean isShowGuide = UIConfigManager.getInstanse(C.getAppContext()).getNEWS_USER_GUIDE();
            if(isShowGuide && null != mElephantView){
                mElephantView.showGuide();
            }else {
                if (null != activity && !activity.isFinishing()){
                    startActivity(new Intent(activity, NewsActivity.class));
                    activity.finish();
                    if (mNewsDisplayView != null && !mNewsDisplayView.isReported){
                        mNewsDisplayView.reportStartview(3);
                    }
                }
            }
        }
    };

    @Override
    public void initReport() {
        if(null != mElephantView){
            mElephantView.initReport();
        }
    }
}
