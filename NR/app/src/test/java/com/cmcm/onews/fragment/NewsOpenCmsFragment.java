package com.cmcm.onews.fragment;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.cmcm.onews.C;
import com.cmcm.onews.NewsL;
import com.cmcm.onews.R;
import com.cmcm.onews.bitmapcache.VolleySingleton;
import com.cmcm.onews.loader.LOAD_OPENCMS;
import com.cmcm.onews.loader.ONewsLoadResult;
import com.cmcm.onews.loader.ONewsLoadResult_LOAD_REMOTE;
import com.cmcm.onews.loader.ONewsLoader;
import com.cmcm.onews.loader.ONewsLoaderParams;
import com.cmcm.onews.loader.ONewsNResult;
import com.cmcm.onews.model.ONews;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.ui.NewsListAdapter;
import com.cmcm.onews.ui.item.BaseItem;
import com.cmcm.onews.ui.item.Types;
import com.cmcm.onews.ui.pulltorefresh.NewsListView;
import com.cmcm.onews.ui.pulltorefresh.OnLoadListener;
import com.cmcm.onews.ui.wave.NewsItemRootLayout;
import com.cmcm.onews.ui.widget.CmViewAnimator;
import com.cmcm.onews.ui.widget.MareriaProgressBar;
import com.cmcm.onews.ui.widget.OpenCmsHeaderView;
import com.cmcm.onews.ui.widget.OpenCmsTopView;
import com.cmcm.onews.util.NetworkUtil;
import com.cmcm.onews.util.SDKConfigManager;
import com.cmcm.ui.CircleImageView;

import java.util.List;

public class NewsOpenCmsFragment extends NewsBaseListFragment{
    private static final int PROFILE_LOADING = 0;
    private static final int PROFILE_NONET = 1;
    private static final int PROFILE_LIST = 2;

    public static NewsOpenCmsFragment newInstance(ONewsScenario scenario, ONews news,int from) {
        NewsOpenCmsFragment fragment = new NewsOpenCmsFragment();
        return setArgument(fragment, scenario, news,from);
    }

    public static NewsOpenCmsFragment setArgument(NewsOpenCmsFragment fragment,ONewsScenario scenario,ONews news,int from) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SCENARIO, scenario);
        bundle.putParcelable(KEY_NEWS, news.toContentValues());
        bundle.putInt(KEY_FROM,from);
        fragment.setArguments(bundle);
        return fragment;
    }

    private CmViewAnimator mOpenCms;
    private MareriaProgressBar mMareria;
    private LinearLayout mRefreshNotify;
    private MareriaProgressBar mRefreshMareria;
    private ImageView mError;
    private TextView mErrorR2;
    private NewsItemRootLayout mRefresh;
    private NewsListView mNewsListView;
    private ListView mListView;

    private String offset = "0";
    private volatile boolean isPullDown = false;// 下拉
    private volatile boolean isPullUp = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (null != bundle) {
            ContentValues values = bundle.getParcelable(KEY_NEWS);
            mONews = ONews.fromContentValues(values);
            mFrom = bundle.getInt(KEY_FROM);
            mAlbumid = mONews.contentid();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.onews__fragment_opencms_list, container, false);
        mTopLayout = (OpenCmsTopView) root.findViewById(R.id.top_layout);
        mTopLayout.setIOpenCmsTopListener(new OpenCmsTopView.IOpenCmsTopListener() {
            @Override
            public void onHeightChanged(int h) {
                mTopHeight = h;
            }
        });
        mTopImage = (CircleImageView) root.findViewById(R.id.top_brief_image);
        mTopImage.setImageResource(R.drawable.onews__opencms_header);
        mTopName = (TextView) root.findViewById(R.id.top_brief_name);
        mOpenCms = (CmViewAnimator) root.findViewById(R.id.opencms);
        mMareria = (MareriaProgressBar) root.findViewById(R.id.news_list_progress);

        mRefreshNotify = (LinearLayout) root.findViewById(R.id.news_refresh_notify);
        mRefreshMareria = (MareriaProgressBar) root.findViewById(R.id.news_refresh_progress);
        mError = (ImageView) root.findViewById(R.id.onews_list_error);
        mErrorR2 = (TextView) root.findViewById(R.id.onews__list_empty_r2);
        mRefresh = (NewsItemRootLayout) root.findViewById(R.id.news_button_refresh);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        mNewsListView = (NewsListView) root.findViewById(R.id.news_list);
        mNewsListView.supportPullToRefresh(false);
        mNewsListView.setCanLoadMore(true);
        mNewsListView.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onPullToRefresh() {
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

        mListView = mNewsListView.getRefreshableView();
        mListView.setOnItemClickListener(new NewsOnItemClick());
        mListView.setSelector(R.drawable.onews__drawable_transparent);
        mListView.setDivider(null);
        mListView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mListView.setFooterDividersEnabled(false);
        mListView.setHeaderDividersEnabled(false);
        mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // add to ListView
        addHeadView();

        View footView = LayoutInflater.from(getContext()).inflate(R.layout.onews__pulltorefresh_foot_view, null);
        mNewsListView.setLoadMoreView(footView);

        mAdapter = new NewsListAdapter(getContext(), mListView, SDKConfigManager.getInstanse(getContext()).getNEWS_ITEM_SHOWIMG());
        mNewsListView.setAdapter(mAdapter);
        isInitialize = true;
        setUserVisibleHint(true);
        pullLoadFirst();
        initNotify(root);

        return root;
    }

    private int mTopHeight;
    private int mHeaderHeight;
    private OpenCmsTopView mTopLayout;
    private CircleImageView mTopImage;
    private TextView mTopName;

    private OpenCmsHeaderView mHeaderView;
    private CircleImageView mHeadNormalImage;// 头像
    private TextView mHeadNormalName;// 名字
    private TextView mHeadNormalDesc;// 描述

    private void addHeadView() {
        mHeaderView = (OpenCmsHeaderView) View.inflate(getContext(), R.layout.onews__view_opencms_profile_normal, null);
        mHeaderView.setIOpenCmsHeaderListener(new OpenCmsHeaderView.IOpenCmsHeaderListener() {
            @Override
            public void onHeightChanged(int h) {
                mHeaderHeight = h;
            }
        });
        mHeadNormalImage = (CircleImageView) mHeaderView.findViewById(R.id.profile_header_image);
        mHeadNormalImage.setImageResource(R.drawable.onews__opencms_header);
        mHeadNormalName = (TextView) mHeaderView.findViewById(R.id.profile_header_name);
        mHeadNormalDesc = (TextView) mHeaderView.findViewById(R.id.profile_header_desc);
        mListView.addHeaderView(mHeaderView);

        mNewsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (null != mTopLayout && mHeaderHeight > 0 && mTopHeight > 0) {
                    if (null != mHeaderView) {
                        int bottom = mHeaderView.getBottom();
                        if(bottom <= mTopHeight){
                            mTopLayout.setTranslationY(-bottom);
                        }else {
                            mTopLayout.onBack();
                        }

                        if(firstVisibleItem > 1){
                            mTopLayout.setTranslationY(0);
                        }
                    }
                }
            }
        });
    }

    private void pullLoadFirst() {
        if (NewsL.DEBUG) NewsL.newslist("pullLoadFirst");
        if (isPullDown) {
            return;
        }

        mOpenCms.setDisplayedChild(PROFILE_LOADING);
        loadRefresh();
    }

    /**
     * 首次拉取数据
     */
    private void loadRefresh() {
        LOAD_OPENCMS OPENCMS = new LOAD_OPENCMS(mScenario);
        OPENCMS.ACT_INIT().opencms_id(mONews.openCms().id()).offset(offset);

        new ONewsLoader(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isPullDown = true;
            }

            @Override
            protected void onLoadResultInBackground(ONewsLoaderParams param, ONewsLoadResult r) {
                super.onLoadResultInBackground(param, r);
                putLoadRefresh(r);
            }

            @Override
            protected void onLoadFinishedInBackground(ONewsNResult result) {
                super.onLoadFinishedInBackground(result);
                isPullDown = false;
                loadFinish(result.time());
            }

        }.execute(OPENCMS);
    }

    /**
     * 首次加载数据
     * @param r
     */
    private void putLoadRefresh(final ONewsLoadResult r) {
        if (isFinish()) {
            return;
        }

        final List<BaseItem> items = Types.transform(r);
        if (null != mAdapter && null != mOpenCms) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != r && null != r.response() && null != r.response().header().openCms()){
                        showPic(mHeadNormalImage, r.response().header().openCms().icon());
                        showPic(mTopImage, r.response().header().openCms().icon());
                        mHeadNormalName.setText(r.response().header().openCms().name());
                        mHeadNormalDesc.setText(r.response().header().openCms().desc());

                        mTopName.setText(r.response().header().openCms().name());
                        offset = r.response().header().offset();

                        mUpack = r.response().header().upack();
                    }

                    if (null != items && !items.isEmpty()) {
                        mAdapter.put(items);
                    }

                    showNewsList();
                }
            });
        }
    }

    private void showNewsList() {
        if (mAdapter.getCount() > 0) {
            mMareria.stop();
            refreshFinish();

            if(PROFILE_LIST != mOpenCms.getDisplayedChild()){
                mOpenCms.setDisplayedChild(PROFILE_LIST);
            }
        }
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

    private void loadFinish(long time) {
        if (null != mAdapter && mAdapter.getCount() == 0) {
            long delay = time > TIME_LIMIT ? 0 : TIME_LIMIT;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter.getCount() == 0) {
                        mMareria.stop();
                        refreshFinish();
                        mOpenCms.setDisplayedChild(PROFILE_NONET);
                        if (NetworkUtil.isNetworkActive(C.getAppContext())) {
                            mErrorR2.setText(R.string.onews__list_empty_r3);
                            mError.setImageResource(R.drawable.onews__list_no_data);
                        } else {
                            mErrorR2.setText(R.string.onews__list_empty_r2);
                            mError.setImageResource(R.drawable.onews__list_wifierror);
                        }
                    }
                }
            }, delay);
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
        loadRefresh();
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

        LOAD_OPENCMS OPENCMS = new LOAD_OPENCMS(mScenario);
        OPENCMS.ACT_MORE().opencms_id(mONews.openCms().id()).offset(offset);

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
        }.execute(OPENCMS);
    }

    /**
     * 加载更多数据
     * @param r
     */
    public void putLoadUp(final ONewsLoadResult r) {
        if (isFinish()) {
            return;
        }

        final ONewsLoadResult_LOAD_REMOTE remote = (ONewsLoadResult_LOAD_REMOTE) r;
        long delay = r.time() > TIME_LIMIT ? TIME_LIMIT : 0;
        final List<BaseItem> items = Types.transform(r);
        if (null != mAdapter && null != mNewsListView) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(null != r && null != r.response() && null != r.response().header()){
                        offset = r.response().header().offset();
                    }

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

    private void showPic(final CircleImageView img,String picUrl){
        if(null == img){
            return;
        }

        if (TextUtils.isEmpty(picUrl)){
            img.setImageResource(R.drawable.onews__opencms_header);
        }else {
            VolleySingleton.getInstance().loadImage(img, picUrl, new VolleySingleton.IResponseError() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (isFinish()) {
                        return;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            img.setImageResource(R.drawable.onews__opencms_header);
                        }
                    });
                }
            });
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
}
