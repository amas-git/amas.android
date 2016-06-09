package com.cmcm.onews.fragment;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcm.onews.C;
import com.cmcm.onews.NewsL;
import com.cmcm.onews.R;
import com.cmcm.onews.event.EventDeleteSingle;
import com.cmcm.onews.event.FireEvent;
import com.cmcm.onews.infoc.newsindia_city;
import com.cmcm.onews.loader.ONewsLoadResult_LOAD_CACHED;
import com.cmcm.onews.model.ONewsCity;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.service.DeleteSingleONews;
import com.cmcm.onews.storage.ONewsProviderManager;
import com.cmcm.onews.ui.NewsListAdapter;
import com.cmcm.onews.ui.NewsLocationSelectActivity;
import com.cmcm.onews.ui.item.BaseItem;
import com.cmcm.onews.ui.item.Types;
import com.cmcm.onews.ui.pulltorefresh.Mode;
import com.cmcm.onews.ui.pulltorefresh.NewsListView;
import com.cmcm.onews.ui.pulltorefresh.OnLoadListener;
import com.cmcm.onews.ui.wave.NewsItemRootLayout;
import com.cmcm.onews.ui.widget.CmViewAnimator;
import com.cmcm.onews.ui.widget.CommonNewsDialog;
import com.cmcm.onews.ui.widget.INewsNotifyDialogClick;
import com.cmcm.onews.ui.widget.MareriaProgressBar;
import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.DimenUtils;
import com.cmcm.onews.util.LocalCityUtils;
import com.cmcm.onews.util.SDKConfigManager;

import java.util.ArrayList;

public class NewsLocalListFragment extends NewsBaseListFragment {
    private static final int CACHE_LIMIT = 20;
    private static final int BACK_Y = DimenUtils.dp2px(C.getAppContext(), 46);
    private static final long BACK_TIME = 150;
    private static final int BACK_COUNT = 20;
    private static final int BACK_MAX = 2;

    private static final int NEWS_LOADING = 0;
    private static final int NEWS_LIST = 1;
    private static final int NEWS_NONET = 2;
    private static final int NEWS_FAILED_LOCATE = 3;

    private CmViewAnimator mNewsCmView;
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
    private CommonNewsDialog mCommonNewsDialog;
    private View mHeaderView;
    private LinearLayout mPreLocationLl;
    private LinearLayout mFailedLocationLl;
    private TextView mLocationNameTv;
    private ImageView mLocationIv;
    private TextView mLocationChangeTv;
    private ONewsCity mCity;
    private ONewsCity mTempCity;
    private boolean mIsNeedShowDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (L.DEBUG) L.newslist("onCreateView ");
        View root = inflater.inflate(R.layout.onews__fragment_local_news_list, container, false);
        mNewsCmView = (CmViewAnimator) root.findViewById(R.id.news);
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
                if (!isTTL_Expired && mCity != null) {
                    pullLoadDown();
                } else {
                    if (mNewsListView != null) {
                        mNewsListView.onRefreshComplete();
                    }
                }
            }

            @Override
            public void onLoadMore() {
                if (mCity != null) {
                    pullLoadUp();
                } else {
                    if (mNewsListView != null) {
                        mNewsListView.onLoadMoreComplete(false);
                    }
                }
            }

            @Override
            public void onPullUpToRefresh() {
                if (mCity != null) {
                    pullLoadUp();
                } else {
                    if (mNewsListView != null) {
                        mNewsListView.onLoadMoreComplete(false);
                    }
                }
            }
        });
        mNewsListView.setOnScrollListener(new NewsScrollListener());
        mListView = mNewsListView.getRefreshableView();

        mListView.setSelector(R.drawable.onews__drawable_transparent);
        mListView.setDivider(null);
        mListView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mListView.setFooterDividersEnabled(false);
        mListView.setHeaderDividersEnabled(false);
        mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mHeaderView = getHeaderView(inflater);
        initHeaderView();
        mListView.addHeaderView(mHeaderView);

        View footView = LayoutInflater.from(getContext()).inflate(R.layout.onews__pulltorefresh_foot_view, null);
        mNewsListView.setLoadMoreView(footView);

        mAdapter = new NewsListAdapter(getContext(), mListView, SDKConfigManager.getInstanse(getContext()).getNEWS_ITEM_SHOWIMG());
        mNewsListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new NewsOnItemClick());
        mNewsCmView.setLocalDisplayedChild(NEWS_LIST, -1);
        isInitialize = true;

//        firstLoadData();
        new GetCurrCityTask().execute();
        //默认隐藏back
        hideBack(0);
        initNotify(root);

        return root;
    }

    private void initHeaderView() {
        ArrayList<ONewsCity> recentLocList = LocalCityUtils.getRecentLocList(getActivity());
        if (recentLocList != null && recentLocList.size() > 0) {
            mCity = recentLocList.get(0);
        }
        setHeaderView(true, mCity == null ? null : mCity.getCity());
    }

    private View getHeaderView(LayoutInflater inflater) {
        if (mHeaderView == null) {
            mHeaderView = inflater.inflate(R.layout.onews_local_list_header, null, false);
            mPreLocationLl = (LinearLayout) mHeaderView.findViewById(R.id.ll_pre_location);
            mFailedLocationLl = (LinearLayout) mHeaderView.findViewById(R.id.ll_failed_location);
            mLocationIv = (ImageView) mHeaderView.findViewById(R.id.iv_news_local);
            mLocationNameTv = (TextView) mHeaderView.findViewById(R.id.tv_city_name);
            mLocationChangeTv = (TextView) mHeaderView.findViewById(R.id.tv_location_change);
            mHeaderView.findViewById(R.id.tv_location_change).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewsLocationSelectActivity.startByResult(getActivity(), mCity);
                    if (mCity == null) {
                        new newsindia_city().action((byte) 1).report();
                    } else {
                        new newsindia_city().action((byte) 2).report();
                    }
                }
            });
            mHeaderView.setOnClickListener(null);
        }
        return mHeaderView;
    }

    private void setHeaderView(boolean isFirst, String cityName) {
        if (!TextUtils.isEmpty(cityName)) {
            setViewGone(mPreLocationLl);
            setViewVisible(mFailedLocationLl);
            mLocationChangeTv.setText(getResources().getString(R.string.onews__news_local_change));
            mLocationIv.setImageResource(R.drawable.onews_location_icon);
            mLocationNameTv.setText(cityName);
        } else {
            if (!isFirst) {
                setViewGone(mPreLocationLl);
                setViewVisible(mFailedLocationLl);
                mLocationChangeTv.setText(getResources().getString(R.string.onews__news_local_choose));
                mLocationIv.setImageResource(R.drawable.onews_unlocation_icon);
                mLocationNameTv.setText(getResources().getString(R.string.onews__news_local_not_find_position));
            }
        }
    }

    private void setViewVisible(View view) {
        if (view != null && view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void setViewGone(View view) {
        if (view != null && view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            infocact();
            if (null != mAdapter) {
                mAdapter.onResume();
            }
            if (mIsNeedShowDialog && mTempCity != null) {
                mIsNeedShowDialog = false;
                showPositionChangeDialog(mTempCity);
            }
        } else {
            if (null != mAdapter) {
                mAdapter.onPause();
            }
        }
        firstLoadData(false);
    }

    private void showPositionChangeDialog(final ONewsCity result) {
        if (mCommonNewsDialog == null) {
            mCommonNewsDialog = new CommonNewsDialog(getActivity(), new INewsNotifyDialogClick() {
                @Override
                public void clickContinue() {
                    FireEvent.FIRE_EventNewsSelectLocation(result);
                }

                @Override
                public void clickCancel() {
                    mTempCity = null;
                    mIsNeedShowDialog = false;
                    LocalCityUtils.saveUnChangeONewsCity(getActivity(), result);
                }
            }, getString(R.string.onews__notify_position_change), getString(R.string.onews__notify_position_change_cancel), getString(R.string.onews__notify_position_change_ok));
            mCommonNewsDialog.setCanceledOnTouchOutside(false);
        }
        mCommonNewsDialog.setContentText(String.format(getString(R.string.onews__notify_position_change), result.getCity()));
        if (isVisibleToUser) {
            mCommonNewsDialog.showDialog();
        } else {
            mTempCity = result;
            mIsNeedShowDialog = true;
        }
    }


    private void changeCity(ONewsCity result) {
        if (mCity != null && mCity.getCCode().equals(result.getCCode())) {
            mCity = result;
            setHeaderView(false, result.getCity());
            return;
        } else {
            mCity = result;
            setHeaderView(false, result.getCity());
            ONewsProviderManager.getInstance().deleteScenarioSingle(mScenario);
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
            isLoadFirst = false;
            isVisibleToUser = true;
            firstLoadData(true);
            LocalCityUtils.saveUnChangeONewsCity(getActivity(), null);
            saveONewsCityToConfig();
            try {
                if (mCity.isUserCity()) {
                    new newsindia_city().citym(Integer.valueOf(mCity.getCCode())).report();
                } else {
                    new newsindia_city().citya(Integer.valueOf(mCity.getCCode())).report();
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handleAnimation(false, 0);

        if (null != mAdapter) {
            mAdapter.onResume();
        }
    }

    /**
     * 首次加载
     */
    private void firstLoadData(boolean isIgnoreTTL) {
        if (mCity != null) {
            if (NewsL.DEBUG && null != mScenario)
                NewsL.newslist("isInitialize  " + isInitialize + "  isLoadFirst " + isLoadFirst + "  isVisibleToUser " + isVisibleToUser + "  " + mScenario.getCategoryTitle(getActivity()));
            if (isInitialize && !isLoadFirst && isVisibleToUser) {
                if (null != mAdapter) {
                    mAdapter.clear();
                }
                isLoadFirst = true;
                pullLoadFirst(isIgnoreTTL);
            }
        }
    }

    /**
     * 无网络重试加载逻辑
     */
    private void refresh() {
        if (isPullDown) {
            return;
        }

        mRefreshNotify.setVisibility(View.INVISIBLE);
        mRefresh.setBackgroundResource(R.drawable.onews__btn_try_disable);
        mRefreshMareria.setVisibility(View.VISIBLE);
        mRefreshMareria.start();
        loadFirst(true);
    }

    /**
     * 结束刷新
     */
    private void refreshFinish() {
        mRefreshNotify.setVisibility(View.VISIBLE);
        mRefresh.setBackgroundResource(R.drawable.onews__btn_try);
        mRefreshMareria.setVisibility(View.GONE);
        mRefreshMareria.stop();
    }

    /**
     * 首次加载数据 + 刷新按钮
     *
     * @param r
     */
    public void putLoadFirst(final com.cmcm.onews.loader.ONewsLoadResult r) {
        if (isFinish()) {
            return;
        }

        start_seq(r);

        final java.util.List<BaseItem> items = Types.transform(r);
        if (null != mAdapter && null != mNewsCmView) {
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
                        }
                    } else if (r instanceof com.cmcm.onews.loader.ONewsLoadResult_LOAD_REMOTE) {
                        delayShowTTL_ExpiredFirst(r, items, isTTL_Expired ? 1000 : 0);
                    }
                }
            });
        }
    }

    /**
     * todo ttl 过期添加刷新动画
     *
     * @param r
     * @param items
     * @param delay
     */
    private void delayShowTTL_ExpiredFirst(final com.cmcm.onews.loader.ONewsLoadResult r, final java.util.List<com.cmcm.onews.ui.item.BaseItem> items, long delay) {
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
                }

                showDebugResultCode(r);

                loadFirstFinish();
            }
        }, delay);
    }

    private void showNewsList() {
        if (mAdapter.getCount() > 0) {
            mMareria.stop();
            refreshFinish();

            if (NEWS_LIST != mNewsCmView.getDisplayedChild()) {
                mNewsCmView.setLocalDisplayedChild(NEWS_LIST, -1);
                mNewsListView.setMode(Mode.BOTH);
            }
        }
    }

    /**
     * 加载更多数据
     *
     * @param r
     */
    public void putLoadUp(final com.cmcm.onews.loader.ONewsLoadResult r) {
        if (isFinish()) {
            return;
        }

        start_seq(r);

        final com.cmcm.onews.loader.ONewsLoadResult_LOAD_REMOTE remote = (com.cmcm.onews.loader.ONewsLoadResult_LOAD_REMOTE) r;
        long delay = r.time() > TIME_LIMIT ? TIME_LIMIT : 0;

        final java.util.List<com.cmcm.onews.ui.item.BaseItem> items = com.cmcm.onews.ui.item.Types.transform(r);

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

    private void start_seq(com.cmcm.onews.loader.ONewsLoadResult r) {
        if (null != r && null != r.newsList() && !r.newsList().isEmpty()) {
            start_seq = r.newsList().get(r.newsList().size() - 1).x_seq();
        }
    }

    /**
     * 下拉刷新数据填充
     *
     * @param r
     */
    public void putLoadDown(com.cmcm.onews.loader.ONewsLoadResult r) {
        if (isFinish()) {
            return;
        }

        final com.cmcm.onews.loader.ONewsLoadResult_LOAD_REMOTE remote = (com.cmcm.onews.loader.ONewsLoadResult_LOAD_REMOTE) r;
        final java.util.List<com.cmcm.onews.ui.item.BaseItem> items = com.cmcm.onews.ui.item.Types.transform(r);

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
                        mNewsCmView.setLocalDisplayedChild(NEWS_NONET, NEWS_LIST);
                        mNewsListView.setMode(Mode.DISABLED);
                        if (com.cmcm.onews.util.NetworkUtil.isNetworkActive(C.getAppContext())) {
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
        if (null != mBack) {
            hideBack(BACK_TIME);
        }

        if (null != mAdapter) {
            mAdapter.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isPullDown = false;
        isPullUp = false;
        isTTL_Expired = false;
    }

    private void pullLoadFirst(boolean isIgnoreTTL) {
        if (com.cmcm.onews.NewsL.DEBUG)
            com.cmcm.onews.NewsL.newsListLoader("pullLoadFirst  " + isPullDown);
        if (isPullDown) {
            return;
        }

        mNewsCmView.setLocalDisplayedChild(NEWS_LOADING, -1);
        loadFirst(isIgnoreTTL);
    }

    /**
     * 首次拉取数据
     */
    private void loadFirst(boolean ignoreTTL) {
        if (mCity == null) {
            return;
        }
        boolean userCity = mCity.isUserCity();
        com.cmcm.onews.loader.LOAD_REMOTE REMOTE = new com.cmcm.onews.loader.LOAD_REMOTE(mScenario, userCity ? null : mCity.getCCode(), userCity ? mCity.getCCode() : null, ignoreTTL);
        REMOTE.setIsEnableAsynSaveCache(true);
        com.cmcm.onews.loader.LOAD_CACHED CACHED = new com.cmcm.onews.loader.LOAD_CACHED(mScenario);
        CACHED.limit(CACHE_LIMIT);

        new com.cmcm.onews.loader.ONewsLoader() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isPullDown = true;
            }

            @Override
            protected void onLoadResultInBackground(com.cmcm.onews.loader.ONewsLoaderParams param, com.cmcm.onews.loader.ONewsLoadResult r) {
                super.onLoadResultInBackground(param, r);
                putLoadFirst(r);
                if (com.cmcm.onews.NewsL.DEBUG)
                    com.cmcm.onews.NewsL.newsListLoader("pullLoadFirst  onLoadResultInBackground");
            }

            @Override
            protected void onLoadFinishedInBackground(com.cmcm.onews.loader.ONewsNResult result) {
                super.onLoadFinishedInBackground(result);
                isPullDown = false;
                if (com.cmcm.onews.NewsL.DEBUG)
                    com.cmcm.onews.NewsL.newsListLoader("pullLoadFirst  onLoadFinishedInBackground");
            }

        }.execute(CACHED, REMOTE.ACT_INIT());
    }

    /**
     * 下拉 刷新数据
     */
    private void pullLoadDown() {
        if (com.cmcm.onews.NewsL.DEBUG)
            com.cmcm.onews.NewsL.newsListLoader("pullLoadDown  " + isPullDown);
        infoc_refresh = infoc_refresh + 1;

        if (isPullDown) {
            return;
        }
        boolean userCity = mCity.isUserCity();
        com.cmcm.onews.loader.LOAD_REMOTE REMOTE = new com.cmcm.onews.loader.LOAD_REMOTE(mScenario, userCity ? null : mCity.getCCode(), userCity ? mCity.getCCode() : null, false);
        REMOTE.setIsEnableAsynSaveCache(false);

        new com.cmcm.onews.loader.ONewsLoader() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isPullDown = true;
            }

            @Override
            protected void onLoadResultInBackground(com.cmcm.onews.loader.ONewsLoaderParams param, com.cmcm.onews.loader.ONewsLoadResult r) {
                super.onLoadResultInBackground(param, r);
                isPullDown = false;
                putLoadDown(r);

                if (com.cmcm.onews.NewsL.DEBUG)
                    com.cmcm.onews.NewsL.newsListLoader("pullLoadDown  onLoadResultInBackground");
            }
        }.execute(REMOTE.ACT_NEW());
    }

    /**
     * 上拉 加载更多
     */
    private void pullLoadUp() {
        if (com.cmcm.onews.NewsL.DEBUG) com.cmcm.onews.NewsL.newslist("pullLoadUp");
        infoc_upload = infoc_upload + 1;
        if (isPullUp) {
            return;
        }
        boolean userCity = mCity.isUserCity();
        com.cmcm.onews.loader.LOAD_REMOTE REMOTE = new com.cmcm.onews.loader.LOAD_REMOTE(mScenario, userCity ? null : mCity.getCCode(), userCity ? mCity.getCCode() : null, false);
        REMOTE.setConsumeCachedFirst(true, start_seq, CACHE_LIMIT);
        REMOTE.setIsEnableAsynSaveCache(true);

        new com.cmcm.onews.loader.ONewsLoader() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isPullUp = true;
            }

            @Override
            protected void onLoadResultInBackground(com.cmcm.onews.loader.ONewsLoaderParams param, com.cmcm.onews.loader.ONewsLoadResult r) {
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

    private class NewsScrollListener implements android.widget.AbsListView.OnScrollListener {
        private int before;

        @Override
        public void onScrollStateChanged(android.widget.AbsListView view, int scrollState) {
            if (scrollState == android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                if (before == 1 || before == 0) {
                    hideBack(BACK_TIME);
                } else {
                    hideBackDelay();
                }
            }
        }

        @Override
        public void onScroll(android.widget.AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (before < firstVisibleItem && firstVisibleItem != 1) {
                hideBack(BACK_TIME);
            } else if (before > firstVisibleItem && before > BACK_COUNT) {
                if (startBack == 0) {
                    startBack = before;
                }

                if (startBack - firstVisibleItem > BACK_MAX) {
                    showBack(BACK_TIME);
                } else if (startBack - firstVisibleItem < 0) {
                    startBack = 0;
                }
            }

            if (before == 1 || before == 0
                    || firstVisibleItem == 1 || firstVisibleItem == 0) {
                hideBack(BACK_TIME);
            }

            if (com.cmcm.onews.NewsL.DEBUG) com.cmcm.onews.NewsL.newslist("onScroll " + startBack);
            before = firstVisibleItem;
        }
    }

    private boolean isShow = false;//back显示状态

    private void showBack(long time) {
        if (isShow) {
            isShow = false;
            startBack = 0;
            handleAnimation(true, time);
            if (com.cmcm.onews.NewsL.DEBUG) com.cmcm.onews.NewsL.newslist("showBack");
        }
    }

    private void hideBack(long time) {
        if (!isShow) {
            isShow = true;
            startBack = 0;
            handleAnimation(false, time);
            if (com.cmcm.onews.NewsL.DEBUG) com.cmcm.onews.NewsL.newslist("hideBack");
        }
    }

    private ObjectAnimator translateY;

    private void handleAnimation(boolean show, long duration) {
        if (null == mBack) {
            return;
        }

        if (show) {
            translateY = ObjectAnimator.ofFloat(mBack, "translationY", -BACK_Y, 0);
        } else {
            translateY = ObjectAnimator.ofFloat(mBack, "translationY", 0, -BACK_Y);
        }
        translateY.setDuration(duration);
        translateY.start();
    }

    private void hideBackDelay() {
        mHandler.removeCallbacks(hideBack);
        mHandler.postDelayed(hideBack, 5 * 1000l);
    }

    private Runnable hideBack = new Runnable() {
        @Override
        public void run() {
            if (!isFinish()) {
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
        firstLoadData(false);
    }

    /**
     * 页面报活
     */
    private void infocact() {
        com.cmcm.onews.infoc.newsindia_act1 act = new com.cmcm.onews.infoc.newsindia_act1();
        act.listid(mScenario.getCategory());
        act.loadtype(2);
        act.report();
    }

    /**
     * debug 展示网络请求结果
     *
     * @param r
     */
    private void showDebugResultCode(com.cmcm.onews.loader.ONewsLoadResult r) {
        if (com.cmcm.onews.util.NewsDebugConfigUtil.getInstance().isEnableDebug()) {
            if (null != r.response()) {
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
    protected void onHandleEvent_EventLive(com.cmcm.onews.event.EventLive event) {
        super.onHandleEvent_EventLive(event);
        if (null == event || null == event.match()) {
            return;
        }

        if (null == mAdapter) {
            return;
        }

        if (com.cmcm.onews.model.ONewsScenarioCategory.SC_1D == mScenario.getCategory() || com.cmcm.onews.model.ONewsScenarioCategory.SC_20 == mScenario.getCategory()) {
            mAdapter.onHandleEvent_EventLive(event.match());
        }
    }

    @Override
    protected void onHandleEvent_EventCheckCricketMatch(com.cmcm.onews.event.EventCheckCricketMatch event) {
        super.onHandleEvent_EventCheckCricketMatch(event);
        if (null != mAdapter) {
            mAdapter.chechRefresh();
        }
    }

    @Override
    protected void onHandleEvent_EventNewsSelectLocation(com.cmcm.onews.event.EventNewsSelectLocation event) {
        super.onHandleEvent_EventNewsSelectLocation(event);
        if (null == event || null == event.oNewsCity()) {
            return;
        }

        if (null == mAdapter) {
            return;
        }
        changeCity(event.oNewsCity());
    }

    public class GetCurrCityTask extends com.cmcm.onews.loader.AsyncTaskEx<Void, Void, com.cmcm.onews.model.ONewsCity> {

        @Override
        protected ONewsCity doInBackground(Void... params) {
            android.util.Log.i("cc", "get ONewsCity...");
            return NewsSdk.INSTAMCE.requestCurrCity();
        }

        @Override
        protected void onPostExecute(ONewsCity result) {
            android.util.Log.i("cc", "result:" + result);
            if (!isFinish()) {
                if (result != null) {
                    result.setIsUserCity(false);
                }
                saveONewsCity(result);
            }
        }
    }

    private void saveONewsCity(ONewsCity result) {
        if (result != null) {
            if (mCity == null) {
                com.cmcm.onews.event.FireEvent.FIRE_EventNewsSelectLocation(result);
            } else {
                if (mCity.getCCode() != null && !mCity.getCCode().equals(result.getCCode())) {
                    ONewsCity unChangeONewsCity = LocalCityUtils.getUnChangeONewsCity(getActivity());
                    if (unChangeONewsCity != null) {
                        if (!TextUtils.isEmpty(unChangeONewsCity.getCCode()) && !TextUtils.isEmpty(unChangeONewsCity.getCity())) {
                            if (unChangeONewsCity.getCCode().equals(result.getCCode()) && unChangeONewsCity.getCity().equals(result.getCity())) {
                                return;
                            }
                        }
                    }
                    showPositionChangeDialog(result);
                }
            }
        } else {
            if (mCity == null) {
                setHeaderView(false, null);
                mNewsCmView.setLocalDisplayedChild(NEWS_LIST, NEWS_FAILED_LOCATE);
                mNewsListView.setMode(Mode.DISABLED);
            }
        }
    }

    private void saveONewsCityToConfig() {
        ArrayList<ONewsCity> recentLocList = LocalCityUtils.getRecentLocList(getActivity());
        if (recentLocList != null) {
            recentLocList.remove(mCity);
            recentLocList.add(0, mCity);
            int size = recentLocList.size();
            if (size > 4) {
                for (int i = 0, len = recentLocList.size(); i < len; i++) {
                    if (i >= 4) {
                        recentLocList.remove(i);
                        --len;
                        --i;
                    }
                }
            }
            LocalCityUtils.saveRecentLocCityList(getActivity(), recentLocList);
        } else {
            ArrayList arrayList = new ArrayList();
            arrayList.add(mCity);
            LocalCityUtils.saveRecentLocCityList(getActivity(), arrayList);
        }
    }


    private void initNotify(View root) {
        mToastBottom = (RelativeLayout) root.findViewById(R.id.news_toast_bottom);
        mToastBottomText = (TextView) root.findViewById(R.id.news_bottom_toast_text);
        mToastBottomAnimator = ObjectAnimator.ofFloat(mToastBottom, "alpha", 1, 1, 1, 1, 0);

        mToastTop = (RelativeLayout) root.findViewById(R.id.news_toast_top);
        mToastTopText = (TextView) root.findViewById(R.id.news_top_toast_text);
        mToastTopAnimator = ObjectAnimator.ofFloat(mToastTop, "alpha", 1, 1, 1, 1, 0);
    }

    @Override
    protected void onHandleEvent_EventDeleteSingle(EventDeleteSingle event) {
        super.onHandleEvent_EventDeleteSingle(event);
        if (null == event || null == event.news() || null == event.scenario() || null == mScenario) {
            return;
        }

        if (mScenario.getCategory() != event.scenario().getCategory()) {
            return;
        }

        removeItemWithAnim(event.news(), event.scenario(), mListView);
        BackgroundThread.post(new DeleteSingleONews(event.news(), event.scenario()));
    }
}
