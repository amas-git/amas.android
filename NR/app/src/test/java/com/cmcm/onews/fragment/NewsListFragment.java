package com.cmcm.onews.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcm.onews.C;
import com.cmcm.onews.NewsL;
import com.cmcm.onews.R;
import com.cmcm.onews.event.EventCheckCricketMatch;
import com.cmcm.onews.event.EventDeleteSingle;
import com.cmcm.onews.event.EventLive;
import com.cmcm.onews.event.EventNetworkChanged;
import com.cmcm.onews.infoc.newsindia_act1;
import com.cmcm.onews.loader.LOAD_CACHED;
import com.cmcm.onews.loader.LOAD_REMOTE;
import com.cmcm.onews.loader.ONewsLoadResult;
import com.cmcm.onews.loader.ONewsLoadResult_LOAD_CACHED;
import com.cmcm.onews.loader.ONewsLoadResult_LOAD_REMOTE;
import com.cmcm.onews.loader.ONewsLoader;
import com.cmcm.onews.loader.ONewsLoaderParams;
import com.cmcm.onews.loader.ONewsNResult;
import com.cmcm.onews.model.ONewsScenarioCategory;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.service.DeleteSingleONews;
import com.cmcm.onews.system.NET_STATUS;
import com.cmcm.onews.ui.NewsActivity;
import com.cmcm.onews.ui.NewsListAdapter;
import com.cmcm.onews.ui.item.BaseItem;
import com.cmcm.onews.ui.item.Types;
import com.cmcm.onews.ui.pulltorefresh.Mode;
import com.cmcm.onews.ui.pulltorefresh.NewsListView;
import com.cmcm.onews.ui.pulltorefresh.OnLoadListener;
import com.cmcm.onews.ui.wave.NewsItemRootLayout;
import com.cmcm.onews.ui.widget.CmViewAnimator;
import com.cmcm.onews.ui.widget.MareriaProgressBar;
import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.DimenUtils;
import com.cmcm.onews.util.NetworkUtil;
import com.cmcm.onews.util.NewsDebugConfigUtil;
import com.cmcm.onews.util.SDKConfigManager;
import com.cmcm.onews.util.StringUtils;
import com.cmcm.onews.util.UIConfigManager;

import java.util.List;

public class NewsListFragment extends NewsBaseListFragment {
    private static final int CACHE_LIMIT = 20;
    private static final int BACK_Y = DimenUtils.dp2px(C.getAppContext(),46);
    private static final long BACK_TIME = 150;
    private static final int BACK_COUNT = 20;
    private static final int BACK_MAX = 2;

    private static final int NEWS_LOADING = 0;
    private static final int NEWS_NONET = 1;
    private static final int NEWS_LIST = 2;

    private CmViewAnimator mNews;
    private NewsListView mNewsListView;
    private ListView mListView;
    private MareriaProgressBar mMareria;

    private LinearLayout mRefreshNotify;
    private MareriaProgressBar mRefreshMareria;
    private NewsItemRootLayout mRefresh;
    private TextView mNotifyTextR2;
    private ImageView mNotifyError;

    private RelativeLayout mBack;

    private ViewStub mResultErrorCode;
    private View mResultRoot;
    private TextView mResultText;

    private volatile boolean isPullDown = false;
    private volatile boolean isPullUp = false;
    private volatile boolean isTTL_Expired = false;
    private int start_seq = Integer.MIN_VALUE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(L.DEBUG) L.newslist("onCreateView ");
        View root = inflater.inflate(R.layout.onews__fragment_news_list, container, false);
        mNews = (CmViewAnimator) root.findViewById(R.id.news);
        mMareria = (MareriaProgressBar) root.findViewById(R.id.news_list_progress);
        mBack = (RelativeLayout) root.findViewById(R.id.news_list_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.setSelection(0);
                mNewsListView.setRefreshing();
                hideBack(0);
            }
        });

        mRefreshNotify = (LinearLayout) root.findViewById(R.id.news_refresh_notify);
        mRefreshMareria = (MareriaProgressBar) root.findViewById(R.id.news_refresh_progress);
        mRefresh = (NewsItemRootLayout) root.findViewById(R.id.news_button_refresh);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        mNotifyTextR2 = (TextView) root.findViewById(R.id.onews__list_empty_r2);
        mNotifyError = (ImageView) root.findViewById(R.id.onews_list_error);
        mResultErrorCode = (ViewStub) root.findViewById(R.id.news__list_result_errorcode);

        mNewsListView = (NewsListView) root.findViewById(R.id.news_list);
        mNewsListView.setMode(Mode.BOTH);
        if (Build.VERSION.SDK_INT >= 9) {
            mNewsListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        mNewsListView.setCanLoadMore(true);

        mNewsListView.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onPullToRefresh() {
                if (!isTTL_Expired) {
                    pullLoadDown();
                }
            }

            @Override
            public void onLoadMore() {
                pullLoadUp();
            }

            @Override
            public void onPullUpToRefresh() {
                pullLoadUp();
            }
        });
        mNewsListView.setOnScrollListener(new NewsScrollListener());

        mListView = mNewsListView.getRefreshableView();
        mListView.setOnItemClickListener(new NewsOnItemClick());
        mListView.setSelector(R.drawable.onews__drawable_transparent);
        mListView.setDivider(null);
        mListView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mListView.setFooterDividersEnabled(false);
        mListView.setHeaderDividersEnabled(false);
        mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        View footView = LayoutInflater.from(getContext()).inflate(R.layout.onews__pulltorefresh_foot_view, null);
        mNewsListView.setLoadMoreView(footView);

        mAdapter = new NewsListAdapter(getContext(), mListView, SDKConfigManager.getInstanse(getContext()).getNEWS_ITEM_SHOWIMG());
        mNewsListView.setAdapter(mAdapter);

        mNews.setDisplayedChild(NEWS_LIST);
        isInitialize = true;

        firstLoadData();

        //默认隐藏back
        hideBack(0);
        initNotify(root);

        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            infocact();
            if(null != mAdapter){
                mAdapter.onResume();
            }
        }else {
            if(null != mAdapter){
                mAdapter.onPause();
            }

            mHandler.removeCallbacks(showDislikGuide);
            if(null != mScenario && mScenario.getCategory() == ONewsScenarioCategory.SC_1D && !isFinish()){
                Activity activity = getActivity();
                if(null != activity && activity instanceof NewsActivity){
                    NewsActivity baseActivity = (NewsActivity) activity;
                    baseActivity.dismissDislikeGuidePopup();
                }
            }
        }

        firstLoadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        handleAnimation(false, 0);

        if(null != mAdapter){
            mAdapter.onResume();
        }
    }

    /**
     * 首次加载
     */
    private void firstLoadData() {
        if (NewsL.DEBUG && null != mScenario) NewsL.newslist("isInitialize  " + isInitialize + "  isLoadFirst " + isLoadFirst + "  isVisibleToUser " + isVisibleToUser + "  " + mScenario.getCategoryTitle(getActivity()));
        if (isInitialize && !isLoadFirst && isVisibleToUser) {
            if(null != mAdapter){
                mAdapter.clear();
            }
            isLoadFirst = true;
            pullLoadFirst();
        }
    }

    /**
     * 无网络重试加载逻辑
     */
    private void refresh(){
        if(isPullDown){
            return;
        }

        mRefreshNotify.setVisibility(View.INVISIBLE);
        mRefresh.setBackgroundResource(R.drawable.onews__btn_try_disable);
        mRefreshMareria.setVisibility(View.VISIBLE);
        mRefreshMareria.start();
        loadFirst();
    }

    /**
     * 结束刷新
     */
    private void refreshFinish(){
        mRefreshNotify.setVisibility(View.VISIBLE);
        mRefresh.setBackgroundResource(R.drawable.onews__btn_try);
        mRefreshMareria.setVisibility(View.GONE);
        mRefreshMareria.stop();
    }

    /**
     *  首次加载数据 + 刷新按钮
     * @param r
     */
    public void putLoadFirst(final ONewsLoadResult r) {
        if (isFinish()) {
            return;
        }

        start_seq(r);

        final List<BaseItem> items = Types.transform(r);
        if (null != mAdapter && null != mNews) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (r instanceof ONewsLoadResult_LOAD_CACHED) {
                        mAdapter.put(items);
                        showNewsList();
                        ONewsLoadResult_LOAD_CACHED cached = (ONewsLoadResult_LOAD_CACHED) r;
                        if (cached.isTTL_Expired() && null != items && !items.isEmpty()) {
                            isTTL_Expired = true;
                            mNewsListView.setRefreshing();
                        }else {
                            showGuide();
                        }
                    } else if (r instanceof ONewsLoadResult_LOAD_REMOTE) {
                        delayShowTTL_ExpiredFirst(r, items, isTTL_Expired ? 1000 : 0);
                    }
                }
            });
        }
    }

    /**
     * todo ttl 过期添加刷新动画
     * @param r
     * @param items
     * @param delay
     */
    private void delayShowTTL_ExpiredFirst(final ONewsLoadResult r, final List<BaseItem> items, long delay) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (r.isNeedReset) {
                    mAdapter.put(items);
                }

                if (isTTL_Expired) {
                    mNewsListView.onRefreshComplete();
                    isTTL_Expired = false;
                    if (null != items && !items.isEmpty()) {
                        toastLoadRefresh(-100);
                    }
                } else {
                    showOfflineToast(items);
                }

                showDebugResultCode(r);

                loadFirstFinish();

                showGuide();
            }
        }, delay);
    }

    /**
     * 首次下载的时候toast提示用户在帮她自动离线内容
     * @param items
     */
    private void showOfflineToast(List<BaseItem> items){
        if (UIConfigManager.getInstanse(C.getAppContext()).isOFFLINE_DOWNLOAD_CONTEN_TIP()) {
            return;
        }

        if (items == null || items.isEmpty()) {
            return;
        }

        BaseItem baseItem = items.get(0);
        if (baseItem.getONews() == null) {
            return;
        }

        if (!TextUtils.isEmpty(baseItem.getONews().body())) {
            showTopToast(StringUtils.getString(C.getAppContext(), R.string.offline_download_content_tip));
            UIConfigManager.getInstanse(C.getAppContext()).setOFFLINE_DOWNLOAD_CONTEN_TIP();
        }
    }

    private void showNewsList() {
        if (mAdapter.getCount() > 0) {
            mMareria.stop();
            refreshFinish();

            if(NEWS_LIST != mNews.getDisplayedChild()){
                mNews.setDisplayedChild(NEWS_LIST);
            }
        }
    }

    /**
     * 加载更多数据
     * @param r
     */
    public void putLoadUp(final ONewsLoadResult r) {
        if (isFinish()) {
            return;
        }

        start_seq(r);

        final ONewsLoadResult_LOAD_REMOTE remote = (ONewsLoadResult_LOAD_REMOTE) r;
        long delay = r.time() > TIME_LIMIT ? TIME_LIMIT : 0;

        final List<BaseItem> items = Types.transform(r);

        if (null != mAdapter && null != mNewsListView) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (remote.IS_RESULT_STATE_NO_NETWORK()) {
                        mNewsListView.onLoadMoreComplete(false);
                        toastLoadMore(-1);
                    } else if (remote.IS_RESULT_STATE_NO_MORE()) {
                        mNewsListView.onLoadMoreComplete(true);
                        mNewsListView.setCanLoadMore(false);
                        toastLoadMore(0);
                    } else {
                        mAdapter.putMore(items);
                        mNewsListView.onLoadMoreComplete(false);
                    }
                }
            }, TIME_LIMIT - delay);
        }
    }

    private void start_seq(ONewsLoadResult r) {
        if(null != r && null != r.newsList() && !r.newsList().isEmpty()){
            start_seq = r.newsList().get(r.newsList().size() - 1).x_seq();
        }
    }

    /**
     * 下拉刷新数据填充
     * @param r
     */
    public void putLoadDown(ONewsLoadResult r) {
        if (isFinish()) {
            return;
        }

        final ONewsLoadResult_LOAD_REMOTE remote = (ONewsLoadResult_LOAD_REMOTE) r;
        final List<BaseItem> items = Types.transform(r);

        if (null != mAdapter && null != mNewsListView) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (remote.IS_RESULT_STATE_NO_NETWORK()) {
                        toastLoadRefresh(-1);
                    } else if (items.isEmpty()) {
                        toastLoadRefresh(0);
                    } else {
                        toastLoadRefresh(items.size());
                        mAdapter.putRefresh(items);
                    }
                    showOfflineToast(items);
                    mNewsListView.onRefreshComplete();
                }
            });
        }
    }

    private void loadFirstFinish() {
        if (null != mAdapter) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter.getCount() == 0) {
                        mMareria.stop();
                        refreshFinish();
                        mNews.setDisplayedChild(NEWS_NONET);
                        if (NetworkUtil.isNetworkActive(C.getAppContext())) {
                            mNotifyTextR2.setText(R.string.onews__list_empty_r3);
                            mNotifyError.setImageResource(R.drawable.onews__list_no_data);
                        } else {
                            mNotifyTextR2.setText(R.string.onews__list_empty_r2);
                            mNotifyError.setImageResource(R.drawable.onews__list_wifierror);
                        }
                    } else {
                        showNewsList();
                    }
                }
            }, TIME_LIMIT);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null != mBack){
            hideBack(BACK_TIME);
        }

        if(null != mAdapter){
            mAdapter.onPause();
        }

        mHandler.removeCallbacks(showDislikGuide);
        mHandler.removeCallbacks(showOfflineGuide);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isPullDown = false;
        isPullUp = false;
        isTTL_Expired = false;
    }

    private void pullLoadFirst() {
        if (NewsL.DEBUG) NewsL.newsListLoader("pullLoadFirst  " + isPullDown);
        if (isPullDown) {
            return;
        }

        mNews.setDisplayedChild(NEWS_LOADING);
        loadFirst();
    }

    /**
     * 首次拉取数据
     */
    private void loadFirst() {
        LOAD_CACHED CACHED = new LOAD_CACHED(mScenario);
        CACHED.limit(CACHE_LIMIT);

        LOAD_REMOTE REMOTE = new LOAD_REMOTE(mScenario);
        REMOTE.setIsEnableAsynSaveCache(true);

        new ONewsLoader() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isPullDown = true;
            }

            @Override
            protected void onLoadResultInBackground(ONewsLoaderParams param, ONewsLoadResult r) {
                super.onLoadResultInBackground(param, r);
                putLoadFirst(r);
                if (NewsL.DEBUG) NewsL.newsListLoader("pullLoadFirst  onLoadResultInBackground");
            }

            @Override
            protected void onLoadFinishedInBackground(ONewsNResult result) {
                super.onLoadFinishedInBackground(result);
                isPullDown = false;
                if (NewsL.DEBUG) NewsL.newsListLoader("pullLoadFirst  onLoadFinishedInBackground");
            }

        }.execute(CACHED, REMOTE.ACT_INIT());
    }

    /**
     * 下拉 刷新数据
     */
    private void pullLoadDown() {
        if (NewsL.DEBUG) NewsL.newsListLoader("pullLoadDown  " + isPullDown);
        infoc_refresh = infoc_refresh + 1;

        if (isPullDown) {
            return;
        }

        LOAD_REMOTE REMOTE = new LOAD_REMOTE(mScenario);
        REMOTE.setIsEnableAsynSaveCache(false);

        new ONewsLoader() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isPullDown = true;
            }

            @Override
            protected void onLoadResultInBackground(ONewsLoaderParams param, ONewsLoadResult r) {
                super.onLoadResultInBackground(param, r);
                isPullDown = false;
                putLoadDown(r);

                if (NewsL.DEBUG) NewsL.newsListLoader("pullLoadDown  onLoadResultInBackground");
            }
        }.execute(REMOTE.ACT_NEW());
    }

    /**
     * 上拉 加载更多
     */
    private void pullLoadUp() {
        if (NewsL.DEBUG) NewsL.newslist("pullLoadUp");
        infoc_upload = infoc_upload + 1;
        if (isPullUp) {
            return;
        }

        LOAD_REMOTE REMOTE = new LOAD_REMOTE(mScenario);
        REMOTE.setConsumeCachedFirst(true, start_seq, CACHE_LIMIT);
        REMOTE.setIsEnableAsynSaveCache(true);

        new ONewsLoader() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isPullUp = true;
            }

            @Override
            protected void onLoadResultInBackground(ONewsLoaderParams param, ONewsLoadResult r) {
                super.onLoadResultInBackground(param, r);
                isPullUp = false;
                putLoadUp(r);
            }
        }.execute(REMOTE.ACT_MORE());
    }

    @Override
    protected void onHandleEvent_EventClearOffline() {
        super.onHandleEvent_EventClearOffline();
        mAdapter.onClearAllOffline();
    }

    private int startBack = 0;
    private class NewsScrollListener implements AbsListView.OnScrollListener{
        private int before;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                if(before == 1 || before == 0){
                    hideBack(BACK_TIME);
                }else {
                    hideBackDelay();
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (before < firstVisibleItem && firstVisibleItem != 1) {
                hideBack(BACK_TIME);
            } else if (before > firstVisibleItem && before > BACK_COUNT) {
                if(startBack == 0){
                    startBack = before;
                }

                if(startBack - firstVisibleItem > BACK_MAX){
                    showBack(BACK_TIME);
                }else if(startBack - firstVisibleItem < 0){
                    startBack = 0;
                }
            }

            if(before == 1 || before == 0
                || firstVisibleItem == 1 || firstVisibleItem == 0){
                hideBack(BACK_TIME);
            }

            if (NewsL.DEBUG) NewsL.newslist("onScroll " + startBack);
            before = firstVisibleItem;
        }
    }

    private boolean isShow = false;//back显示状态
    private void showBack(long time){
        if(isShow){
            isShow = false;
            startBack = 0;
            handleAnimation(true,time);
            if (NewsL.DEBUG) NewsL.newslist("showBack");
        }
    }

    private void hideBack(long time){
        if(!isShow){
            isShow = true;
            startBack = 0;
            handleAnimation(false,time);
            if (NewsL.DEBUG) NewsL.newslist("hideBack");
        }
    }

    private ObjectAnimator translateY;
    private void handleAnimation(boolean show,long duration) {
        if(null == mBack){
            return;
        }

        if(show){
            translateY = ObjectAnimator.ofFloat(mBack, "translationY",-BACK_Y,0);
        }else{
            translateY = ObjectAnimator.ofFloat(mBack, "translationY",0,-BACK_Y);
        }
        translateY.setDuration(duration);
        translateY.start();
    }

    private void hideBackDelay(){
        mHandler.removeCallbacks(hideBack);
        mHandler.postDelayed(hideBack, 5 * 1000l);
    }

    private Runnable hideBack = new Runnable() {
        @Override
        public void run() {
            if(!isFinish()){
                hideBack(TIME_LIMIT);
            }
        }
    };

    /**
     * 语言切换
     * 1.清空数据库
     * 2.当前列表重新加载
     */
    @Override
    public void onLanguageChange() {
        super.onLanguageChange();
        isLoadFirst = false;
        isVisibleToUser = true;
        firstLoadData();
    }

    /**
     *  页面报活
     */
    private void infocact(){
        newsindia_act1 act = new newsindia_act1();
        act.listid(mScenario.getCategory());
        act.loadtype(2);
        act.report();
    }

    /**
     * debug 展示网络请求结果
     * @param r
     */
    private void showDebugResultCode(ONewsLoadResult r) {
        if(NewsDebugConfigUtil.getInstance().isEnableDebug()){
            if(null != r.response()){
                if(null == mResultRoot){
                    mResultRoot = mResultErrorCode.inflate();
                    mResultText = (TextView) mResultRoot.findViewById(R.id.error_code);
                }

                StringBuilder sb = new StringBuilder();

                sb.append("请求链接 ： ");
                sb.append("\n");
                sb.append(r.requestUrl());
                sb.append("\n");
                sb.append("\n");
                sb.append("请求异常 ：");
                sb.append("\n");
                sb.append(r.response().exception());
                sb.append("\n");
                sb.append("\n");
                sb.append("服务端返回结果　：");
                sb.append("\n");
                sb.append(r.response().reponseJson());
                mResultText.setText(sb.toString());
            }
        }
    }

    @Override
    protected void onHandleEvent_EventLive(EventLive event) {
        super.onHandleEvent_EventLive(event);
        if(null == event || null == event.match()){
            return;
        }

        if(null == mAdapter){
            return;
        }

        if(ONewsScenarioCategory.SC_1D == mScenario.getCategory() || ONewsScenarioCategory.SC_20 == mScenario.getCategory()){
            mAdapter.onHandleEvent_EventLive(event.match());
        }
    }

    @Override
    protected void onHandleEvent_EventCheckCricketMatch(EventCheckCricketMatch event) {
        super.onHandleEvent_EventCheckCricketMatch(event);
        if(null != mAdapter){
            mAdapter.chechRefresh();
        }
    }

    @Override
    protected void onHandleEvent_EventDeleteSingle(EventDeleteSingle event) {
        super.onHandleEvent_EventDeleteSingle(event);
        if(null == event || null == event.news() || null == event.scenario() || null == mScenario){
            return;
        }

        if(mScenario.getCategory() != event.scenario().getCategory()){
            return;
        }

        removeItemWithAnim(event.news(),event.scenario(),mListView);
        BackgroundThread.post(new DeleteSingleONews(event.news(), event.scenario()));
    }

    private void initNotify(View root) {
        mToastBottom = (RelativeLayout) root.findViewById(R.id.news_toast_bottom);
        mToastBottomText = (TextView) root.findViewById(R.id.news_bottom_toast_text);
        mToastBottomAnimator = ObjectAnimator.ofFloat(mToastBottom, "alpha", 1, 1, 1, 1, 0);

        mToastTop = (RelativeLayout) root.findViewById(R.id.news_toast_top);
        mToastTopText = (TextView) root.findViewById(R.id.news_top_toast_text);
        mToastTopAnimator = ObjectAnimator.ofFloat(mToastTop, "alpha", 1, 1, 1, 1, 0);
    }

    /**
     * todo 引导逻辑
     */
    private void showGuide() {
        if(!isVisibleToUser){
            return;
        }

        if(null == mScenario || mScenario.getCategory() != ONewsScenarioCategory.SC_1D){
            return;
        }

        if (UIConfigManager.getInstanse(C.getAppContext()).getNEWS_GUIDE_DISLIKE()) {
            mHandler.removeCallbacks(showDislikGuide);
            mHandler.postDelayed(showDislikGuide,180);
        }
    }

    private Runnable showDislikGuide = new Runnable() {
        @Override
        public void run() {
            if(isFinish() || null == mAdapter || mAdapter.getCount() == 0 || !isVisibleToUser){
                return;
            }

            mAdapter.getItem(0).showDislikeGuide();
        }
    };

    private Runnable showOfflineGuide = new Runnable() {
        @Override
        public void run() {
            if(isFinish() || null == mAdapter || mAdapter.getCount() == 0){
                return;
            }

            BaseItem item = mAdapter.getItem(0);
            if(null != item.getONews() && !TextUtils.isEmpty(item.getONews().body())){
                mAdapter.getItem(0).showOfflineGuide();
            }
        }
    };

//    @Override
//    protected void onHandleEvent_EventNetworkChanged(EventNetworkChanged event) {
//        super.onHandleEvent_EventNetworkChanged(event);
//        checkOfflineGuide(event);
//    }

    private void checkOfflineGuide(EventNetworkChanged event) {
        if(!isVisibleToUser){
            return;
        }

        if(event.curr() != NET_STATUS.NO){
            return;
        }

        if(null == mScenario || mScenario.getCategory() == ONewsScenarioCategory.SC_1C){
            return;
        }

        if(UIConfigManager.getInstanse(C.getAppContext()).getNEWS_GUIDE_OFFLINE()){
            mHandler.removeCallbacks(showOfflineGuide);
            mHandler.post(showOfflineGuide);
        }
    }
}
