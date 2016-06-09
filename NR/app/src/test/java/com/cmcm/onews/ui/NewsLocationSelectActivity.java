package com.cmcm.onews.ui;

import com.cmcm.onews.model.ONewsCity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cmcm.onews.fragment.NewsLocalListFragment;

import java.util.List;

import android.widget.RelativeLayout;
import android.widget.LinearLayout;

import com.cmcm.onews.location.MySectionIndexer;
import com.cmcm.onews.location.PinnedHeaderListView;
import com.cmcm.onews.location.ONewsCityListAdapter;
import com.cmcm.onews.ui.widget.MareriaProgressBar;
import com.cmcm.onews.ui.wave.NewsItemRootLayout;
import com.cmcm.onews.util.SDKConfigManager;
import com.cmcm.onews.R;
import com.cmcm.onews.loader.AsyncTaskEx;
import com.cmcm.onews.sdk.NewsSdk;

import java.util.ArrayList;

import android.widget.TextView;

import android.view.LayoutInflater;

import java.util.Comparator;

import android.widget.ImageView;
import android.view.ViewStub;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.cmcm.onews.ui.slidr.SliderPanel;

import android.os.Build;
import android.animation.ArgbEvaluator;
import android.text.TextUtils;

public class NewsLocationSelectActivity extends com.cmcm.onews.ui.NewsBaseActivity {
    private RelativeLayout rl_no_net_root;//无网络根布局
    private LinearLayout ll_loading;//加载状态
    private LinearLayout ll_no_net;//无网络显示
    private ImageView iv_no_net;//无网络图标
    private MareriaProgressBar progress;

    private NewsItemRootLayout news_button_refresh;//刷新按钮
    private ViewStub mNoNetViewStub;

    private boolean mIsNoNetViewInflated = false;
    private ONewsCity mCity;
    private ONewsCity mTempCity;
    private PinnedHeaderListView mListView;
    private MySectionIndexer mIndexer;
    private ONewsCityListAdapter mAdapter;
    private List<ONewsCity> mCityList;
    private static final String ALL_CHARACTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#";
    private String[] mSections = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
            "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z", "#"};
    private int[] mCounts;
    private View mCurrentLocHeader;
    private ViewGroup mRecentLocHeader;
    private TextView mTvLocation;
    private com.cmcm.onews.ui.widget.MareriaProgressBar mPbLocation;
    private ImageView mIvRefresh;
    private boolean mIsRefresh;
    private com.cmcm.onews.location.BladeView mBvLetterView;

    public static void startByResult(android.app.Activity activity, ONewsCity city) {
        android.content.Intent intent = new android.content.Intent(activity, NewsLocationSelectActivity.class);
        intent.putExtra(com.cmcm.onews.ui.NewsBaseActivity.KEY_CITY, city);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (com.cmcm.onews.sdk.L.DEBUG)
            com.cmcm.onews.sdk.L.news_item_refresh("NewsLocationSelectActivity onCreate");
        slidr();
        setContentView(R.layout.onews_activity_location_select);
        initData(getIntent());
        initView();
        isHasNet(false);
    }

    private com.cmcm.onews.ui.slidr.SlidrConfig config;

    private void slidr() {
        config = new com.cmcm.onews.ui.slidr.SlidrConfig();
        config.primaryColor(android.graphics.Color.parseColor("#689F38"));
        config.secondaryColor(android.graphics.Color.parseColor("#00000000"));
        config.velocityThreshold(2400);
        config.distanceThreshold(0.4f);
        config.edge(true);//默认右滑退出

        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        View oldScreen = decorView.getChildAt(0);
        decorView.removeViewAt(0);

        // Setup the slider panel and attach it to the decor
        SliderPanel panel = new SliderPanel(this, oldScreen, config);
        panel.setId(com.cmcm.onews.sdk.R.id.onews__slidable_panel);
        oldScreen.setId(com.cmcm.onews.sdk.R.id.onews__slidable_content);
        panel.addView(oldScreen);
        decorView.addView(panel, 0);

        // Set the panel slide listener for when it becomes closed or opened
        panel.setOnPanelSlideListener(new SliderPanel.OnPanelSlideListener() {
            private final ArgbEvaluator mEvaluator = new ArgbEvaluator();

            @Override
            public void onStateChanged(int state) {

            }

            @Override
            public void onClosed() {
                goBackOnEvent();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onOpened() {

            }

            @android.annotation.TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSlideChange(float percent) {
                // Interpolate the statusbar color
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && config.areStatusBarColorsValid()) {
                    int newColor = (int) mEvaluator.evaluate(percent, config.getPrimaryColor(), config.getSecondaryColor());
                    getWindow().setStatusBarColor(newColor);
                }
            }
        });
    }

    private void initData(android.content.Intent intent) {
        java.io.Serializable serializable = intent.getSerializableExtra(com.cmcm.onews.ui.NewsBaseActivity.KEY_CITY);
        if (serializable != null && serializable instanceof ONewsCity) {
            mCity = (ONewsCity) serializable;
        }
    }

    private void initView() {
        mListView = (PinnedHeaderListView) findViewById(R.id.mListView);
        mListView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mListView.setDivider(null);
        iniCurrentLocHeader();
        initRecentLocHeader();
        mBvLetterView = (com.cmcm.onews.location.BladeView) findViewById(R.id.bv_letter_view);

        mAdapter = new ONewsCityListAdapter(mCityList, mIndexer, mCounts, this);
        mListView.setAdapter(mAdapter);
        mNoNetViewStub = (ViewStub) findViewById(R.id.no_net_view_stub);

        //顶部返回
        findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackOnEvent();
            }
        });

    }

    private void initRecentLocHeader() {
        mRecentLocHeader = (ViewGroup) View.inflate(this, R.layout.onews_local_recent_list_header, null);
        ArrayList<ONewsCity> recentLocList = com.cmcm.onews.util.LocalCityUtils.getRecentLocList(this);
        if (recentLocList == null || recentLocList.size() == 0) {
            setViewGone(mRecentLocHeader);
        } else {
            for (int i = 0; i < recentLocList.size(); i++) {
                ViewGroup view = (ViewGroup) View.inflate(this, R.layout.onews_local_recent_list_header_item, null);
                TextView tv = (TextView) view.findViewById(R.id.tv_content);
                tv.setBackgroundColor(getResources().getColor(R.color.onews_sdk_normal_white));
                final ONewsCity city = recentLocList.get(i);
                tv.setText(city.getCity());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeCity(city);
                    }
                });
                mRecentLocHeader.addView(view, mRecentLocHeader.getChildCount(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            }
            mListView.addHeaderView(mRecentLocHeader);
        }

    }

    private void iniCurrentLocHeader() {
        mCurrentLocHeader = View.inflate(this, R.layout.onews_local_current_list_header, null);
        mTvLocation = (TextView) mCurrentLocHeader.findViewById(R.id.tv_location);
        mPbLocation = (MareriaProgressBar) mCurrentLocHeader.findViewById(R.id.pb_location);
        mIvRefresh = (ImageView) mCurrentLocHeader.findViewById(R.id.iv_refresh_location);
        mIvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrCity();
            }
        });
        setCurrentLocHeader(true, mCity);
        mListView.addHeaderView(mCurrentLocHeader);
    }

    private void setCurrentLocHeader(boolean isFirst, ONewsCity city) {
        if (city != null) {
            if (mCity != city) {
                mCity = city;
            }
            mTvLocation.setTextColor(getResources().getColor(R.color.onews_sdk_detail_comment_item_title_normal));
            mTvLocation.setText(mCity.getCity());
            setViewGone(mPbLocation);
            setViewVisible(mIvRefresh);
        } else {
            if (isFirst) {
                setViewVisible(mPbLocation);
                setViewGone(mIvRefresh);
                mTvLocation.setTextColor(getResources().getColor(R.color.onews_sdk_finding_location));
                mTvLocation.setText(getString(R.string.onews__news_local_position));
                getCurrCity();
            } else {
                setViewVisible(mIvRefresh);
                setViewGone(mPbLocation);
                if (mCity == null) {
                    mTvLocation.setTextColor(getResources().getColor(R.color.onews_sdk_not_find_location));
                    mTvLocation.setText(getString(R.string.onews__news_local_not_find_position));
                }
            }
        }
    }

    private void getCurrCity() {
        if (!mIsRefresh) {
            mIsRefresh = true;
            setViewGone(mIvRefresh);
            setViewVisible(mPbLocation);
            new ActivityGetCurrCityTask().execute();
        }
    }

    private void setListView(List<ONewsCity> cityList) {
        mCounts = new int[mSections.length];
        for (ONewsCity city : cityList) {
            String gid = city.getGid();
            int index = ALL_CHARACTER.indexOf(gid);
            mCounts[index]++;
        }
        mCityList = cityList;

        mIndexer = new MySectionIndexer(mSections, mCounts);
        mAdapter.setData(mCityList, mIndexer, mCounts);
        mListView.setOnScrollListener(mAdapter);
        mListView.setPinnedHeaderView(LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.onews_location_list_group_item, mListView, false));
        mListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
                onSelectCity(parent, view, position, id);
            }
        });
        mAdapter.notifyDataSetChanged();
        if (mBvLetterView.getVisibility() != View.VISIBLE) {
            mBvLetterView.setVisibility(View.VISIBLE);
            mBvLetterView.setOnItemClickListener(new com.cmcm.onews.location.BladeView.OnItemClickListener() {

                @Override
                public void onItemClick(String s) {
                    if (!TextUtils.isEmpty(s)) {
                        int section = ALL_CHARACTER.indexOf(s);
                        int position = mIndexer.getPositionForSection(section) + mListView.getHeaderViewsCount();
                        if (position > 0) {
                            mListView.setSelection(position);
                        }
                    }
                }
            });
        }
    }

    private void onSelectCity(android.widget.AdapterView<?> parent, View view, int position, long id) {
        if (parent != null && parent instanceof PinnedHeaderListView) {
            PinnedHeaderListView listView = (PinnedHeaderListView) parent;
            if (position - listView.getHeaderViewsCount() >= 0) {
                position = position - listView.getHeaderViewsCount();
                ONewsCity city = mCityList.get(position);
                changeCity(city);
            }
        }

    }

    private void changeCity(ONewsCity city) {
        if (mCity == null || (!mCity.getCCode().equals(city.getCCode()) || !mCity.getCity().equals(city.getCity()))) {
            city.setIsUserCity(true);
            com.cmcm.onews.event.FireEvent.FIRE_EventNewsSelectLocation(city);
            goBack();
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

    /**
     * 根据网络状态显示不同的ui
     */
    private void isHasNet(boolean clickFrom) {
        if (com.cmcm.onews.util.NetworkUtil.isNetworkUp(this)) {
            //有网络
            setViewGone(rl_no_net_root);
            requestData();
        } else {
            //from 刷新按钮
            if (clickFrom) {
                //无网络
                refreshStart();
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshFinish();
                    }
                }, 500);
            } else {
                //from onCreate
                if (null == rl_no_net_root) {
                    inflateNoNetLayout();
                }
                setViewVisible(rl_no_net_root);
            }
        }
    }

    private void requestData() {
        if (mCity == null && !mIsRefresh) {
            getCurrCity();
        }
        List<ONewsCity> allCities = com.cmcm.onews.util.LocalCityUtils.getAllCities(this);
        if (allCities == null || allCities.size() == 0) {
            new ActivityGetCitysTask().execute();
        } else {
            setListView(allCities);
        }
    }



    /**
     * 开始刷新
     */
    private void refreshStart() {
        if (!mIsNoNetViewInflated) {
            return;
        }
        news_button_refresh.setBackgroundResource(com.cmcm.onews.sdk.R.drawable.onews_sdk_btn_try_disable);
        ll_no_net.setVisibility(View.INVISIBLE);
        iv_no_net.setVisibility(View.GONE);
        ll_loading.setVisibility(View.VISIBLE);
        progress.start();
    }

    /**
     * 结束刷新
     */
    private void refreshFinish() {
        if (!mIsNoNetViewInflated) {
            return;
        }
        ll_no_net.setVisibility(View.VISIBLE);
        iv_no_net.setVisibility(View.VISIBLE);
        progress.stop();
        ll_loading.setVisibility(View.GONE);
        news_button_refresh.setBackgroundResource(com.cmcm.onews.sdk.R.drawable.onews_sdk_btn_try);
    }

    private void inflateNoNetLayout() {
        if (!mIsNoNetViewInflated) {
            mNoNetViewStub.inflate();
            rl_no_net_root = (RelativeLayout) findViewById(R.id.rl_no_net_root);
            ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
            ll_no_net = (LinearLayout) findViewById(R.id.ll_no_net);
            iv_no_net = (ImageView) findViewById(R.id.iv_no_net);
            news_button_refresh = (NewsItemRootLayout) findViewById(R.id.news_button_refresh);
            progress = (MareriaProgressBar) findViewById(R.id.progress);

            //无网络
            news_button_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isHasNet(true);
                }
            });
        }
        mIsNoNetViewInflated = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        goBackOnEvent();
    }

    /**
     * 顶部返回和back键 统一调用此方法
     */
    private void goBackOnEvent() {
        if (mTempCity != null) {
            com.cmcm.onews.event.FireEvent.FIRE_EventNewsSelectLocation(mTempCity);
        }
        goBack();
    }

    private void goBack() {
        finish();
    }

    private LocatoinSelectHandler mHandler = new LocatoinSelectHandler(this);

    private static class LocatoinSelectHandler extends android.os.Handler {

        private java.lang.ref.WeakReference<NewsLocationSelectActivity> reference;

        public LocatoinSelectHandler(NewsLocationSelectActivity activity) {
            this.reference = new java.lang.ref.WeakReference<>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            if (this.reference != null && this.reference.get() != null) {
                NewsLocationSelectActivity activity = this.reference.get();
                if (activity != null) {
                    switch (msg.what) {
                    }
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler = null;
    }


    public class ActivityGetCurrCityTask extends AsyncTaskEx<Void, Void, ONewsCity> {


        @Override
        protected ONewsCity doInBackground(Void... params) {
            android.util.Log.i("cc", "get ActivityGetCurrCityTask...");
            return NewsSdk.INSTAMCE.requestCurrCity();
        }

        @Override
        protected void onPostExecute(ONewsCity result) {
            android.util.Log.i("cc", "result:" + result);
            if (!isFinishing()) {
                mIsRefresh = false;
                if (result != null) {
                    mTempCity = result;
                    result.setIsUserCity(false);
                }
                setCurrentLocHeader(false, result);
            }
        }

    }

    public class ActivityGetCitysTask extends AsyncTaskEx<Void, Void, List<ONewsCity>> {

        @Override
        protected List<ONewsCity> doInBackground(Void... params) {
            android.util.Log.i("cc", "get ActivityGetCitysTask...");

            List<ONewsCity> oNewsCities = com.cmcm.onews.sdk.NewsSdk.INSTAMCE.requestCitys();
            if (oNewsCities != null && oNewsCities.size() > 0) {
                java.util.Collections.sort(oNewsCities, new com.cmcm.onews.util.CityComparator());
            }
            return oNewsCities;
        }

        @Override
        protected void onPostExecute(List<ONewsCity> result) {
            android.util.Log.i("cc", "result:" + result);
            if (!isFinishing() && result != null && result.size() > 0) {
                setListView(result);
                com.cmcm.onews.util.LocalCityUtils.saveAllLocations(NewsLocationSelectActivity.this, result);
            }
        }

    }
}

