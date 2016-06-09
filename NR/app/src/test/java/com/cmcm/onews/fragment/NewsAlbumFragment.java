package com.cmcm.onews.fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cmcm.onews.C;
import com.cmcm.onews.NewsL;
import com.cmcm.onews.R;
import com.cmcm.onews.event.EventDeleteSingle;
import com.cmcm.onews.loader.LOAD_ALBUM;
import com.cmcm.onews.loader.LOAD_CACHED_ALBUM;
import com.cmcm.onews.loader.ONewsLoadResult;
import com.cmcm.onews.loader.ONewsLoader;
import com.cmcm.onews.loader.ONewsLoaderParams;
import com.cmcm.onews.loader.ONewsNResult;
import com.cmcm.onews.model.ONews;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.service.DeleteSingleONews;
import com.cmcm.onews.ui.NewsAlbumActivity;
import com.cmcm.onews.ui.NewsBaseActivity;
import com.cmcm.onews.ui.NewsListAdapter;
import com.cmcm.onews.ui.item.BaseItem;
import com.cmcm.onews.ui.item.NewsAlgorithmReport;
import com.cmcm.onews.ui.item.Types;
import com.cmcm.onews.ui.wave.NewsItemRootLayout;
import com.cmcm.onews.ui.widget.CmViewAnimator;
import com.cmcm.onews.ui.widget.MareriaProgressBar;
import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.NetworkUtil;
import com.cmcm.onews.util.SDKConfigManager;

import java.util.List;

/**
 * 专题页面
 */
public class NewsAlbumFragment extends NewsBaseListFragment{

    private static final int ALBUM_LOADING = 0;
    private static final int ALBUM_NONET = 1;
    private static final int ALBUM_LIST = 2;

    public static NewsAlbumFragment newInstance(ONewsScenario scenario, ONews news,int from) {
        NewsAlbumFragment fragment = new NewsAlbumFragment();
        return setArgument(fragment, scenario, news,from);
    }

    public static NewsAlbumFragment setArgument(NewsAlbumFragment fragment,ONewsScenario scenario,ONews news,int from) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SCENARIO, scenario);
        bundle.putParcelable(KEY_NEWS, news.toContentValues());
        bundle.putInt(KEY_FROM,from);
        fragment.setArguments(bundle);
        return fragment;
    }

    private CmViewAnimator mAlbum;
    private MareriaProgressBar mMareria;

    private LinearLayout mRefreshNotify;
    private MareriaProgressBar mRefreshMareria;
    private ImageView mError;
    private TextView mErrorR2;
    private NewsItemRootLayout mRefresh;

    private ListView mList;

    private volatile boolean isPullDown = false;

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
        View root = inflater.inflate(R.layout.onews__fragment_album_list, container, false);
        mAlbum = (CmViewAnimator) root.findViewById(R.id.album);
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

        mList = (ListView) root.findViewById(R.id.news_album_list);
        mList.setOnItemClickListener(new NewsOnItemClick());
        mAdapter = new NewsListAdapter(getContext(), mList, SDKConfigManager.getInstanse(getContext()).getNEWS_ITEM_SHOWIMG());
        mList.setAdapter(mAdapter);
        isInitialize = true;
        setUserVisibleHint(true);
        pullLoadFirst();

        return root;
    }

    /**
     * 首次加载
     */
    private void pullLoadFirst() {
        if (NewsL.DEBUG) NewsL.newslist("pullLoadFirst");
        if (isPullDown) {
            return;
        }

        mAlbum.setDisplayedChild(ALBUM_LOADING);
        loadRefresh();
    }

    /**
     * 首次拉取数据 本地缓存+网络数据
     */
    private void loadRefresh() {
        LOAD_CACHED_ALBUM CACHED = new LOAD_CACHED_ALBUM(mScenario);
        CACHED.contentid(mONews.contentid());
        LOAD_ALBUM ALBUM = new LOAD_ALBUM(mScenario);
        ALBUM.contentid(mONews.contentid());
        ALBUM.enableCache = enableCache();
        ALBUM.setIsEnableAsynSaveCache(true);

        new ONewsLoader() {

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

        }.execute(CACHED, ALBUM);
    }

    /**
     *  首次加载数据 + 刷新按钮
     * @param r
     */
    public void putLoadRefresh(final ONewsLoadResult r) {
        if (isFinish()) {
            return;
        }

        final List<BaseItem> items = Types.transform(r);

        if (null != mAdapter && null != mAlbum) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != items && !items.isEmpty()) {
                        mAdapter.put(items);
                    }

                    gcmClickReport(r);
                    showNewsList();
                }
            });
        }
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
                        mAlbum.setDisplayedChild(ALBUM_NONET);
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
     * 结束刷新
     */
    private void refreshFinish(){
        mRefreshNotify.setVisibility(View.VISIBLE);
        mRefresh.setBackgroundResource(R.drawable.onews__btn_try);
        mRefreshMareria.setVisibility(View.GONE);
        mRefreshMareria.stop();
    }

    private void showNewsList() {
        if (mAdapter.getCount() > 0) {
            mMareria.stop();
            refreshFinish();

            if(ALBUM_LIST != mAlbum.getDisplayedChild()){
                mAlbum.setDisplayedChild(ALBUM_LIST);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null != mAdapter && mAdapter.getCount() > 0 && !isDebug()){
            int percent = (int) (mAdapter.showCount() * 100f / mAdapter.getCount());
            if(mFrom == NewsBaseActivity.FROM_PUSH){
                NewsAlgorithmReport.algorithmNewsPercent_ALBUM_GCM(mScenario, mONews, percent,mUpack);
            }else {
                NewsAlgorithmReport.algorithmNewsPercent_ALBUM(mScenario, mONews, percent);
            }
        }
    }

    private void gcmClickReport(ONewsLoadResult r) {
        if(isFromGcm()){
            if(null != r.header() && r.header().success()){
                mUpack = r.header().upack();
                NewsAlbumActivity activity = (NewsAlbumActivity) getActivity();
                activity.upack(mUpack);
                NewsAlgorithmReport.algorithmNewsClick_GCM_ALBUM(mUpack, mONews);
            }
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

        removeItemWithAnim(event.news(),event.scenario(),mList);
        BackgroundThread.post(new DeleteSingleONews(event.news(), event.scenario()));
    }
}
