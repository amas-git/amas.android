package com.cmcm.onews.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmcm.onews.ONewsScenarios;
import com.cmcm.onews.R;
import com.cmcm.onews.event.EventCheckCricketMatch;
import com.cmcm.onews.event.EventLanguageChange;
import com.cmcm.onews.event.EventNewsSelectLocation;
import com.cmcm.onews.event.EventRefreshMatch;
import com.cmcm.onews.event.EventTestCategory;
import com.cmcm.onews.event.ONewsEvent;
import com.cmcm.onews.infoc.newsindia_listaction;
import com.cmcm.onews.model.ONewsCity;
import com.cmcm.onews.model.ONewsScenarioCategory;
import com.cmcm.onews.model.sports.ONewsMatch;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.service.DelayRefreshMatch;
import com.cmcm.onews.ui.NewsPageAdapter;
import com.cmcm.onews.ui.NewsViewPager;
import com.cmcm.onews.ui.indicator.PagerSlidingTabStrip;
import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.LanguageCountry;
import com.cmcm.onews.util.UIConfigManager;
import com.cmcm.onews.loader.AsyncTaskEx;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.util.LocalCityUtils;
import com.cmcm.terminal.ConsoleFragment;

import java.util.List;

public class NewsFragment extends NewsBaseFragment {

    public static NewsFragment setArgument(NewsFragment fragment, int tabSelectedId) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TABSELECTED, tabSelectedId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static NewsFragment newInstance(int tabSelectedId) {
        NewsFragment fragment = new NewsFragment();
        return setArgument(fragment, tabSelectedId);
    }

    /**
     * todo 直播卡片刷新事件间隔
     */
    protected static final long DELAY_REFRESH_NO_START = 3 * 60 * 1000l;
    protected static final long DELAY_REFRESH_PLAYING = 20 * 1000l;

    private static final String KEY_TABSELECTED = ":tabselected";
    private static final int MAX_CACHE = 1;

    private NewsViewPager mViewPager = null;
    private PagerSlidingTabStrip mTabStrip = null;
    private NewsPageAdapter mAdapter = null;
    private int tabSelectedId = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.onews__news_fragment, container, false);
        mTabStrip = (PagerSlidingTabStrip) root.findViewById(R.id.news_indicator);
        mTabStrip.setTabInfocListAction(mTabInfocListAction);
        mViewPager = (NewsViewPager) root.findViewById(R.id.news_vp);
        mAdapter = new NewsPageAdapter(getActivity(), getActivity().getSupportFragmentManager());
        mAdapter.addCustomizedFragment(ConsoleFragment.newInstance(), "console");
        mViewPager.setAdapter(mAdapter);
        mTabStrip.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabSelectedId = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initCategory();
        return root;
    }

    public void initCategory() {
        mTabStrip.notifyDataSetChanged();
        mViewPager.setOffscreenPageLimit(MAX_CACHE);
        if (tabSelectedId < mAdapter.getCount()) {
            mTabStrip.tabSelectedId(tabSelectedId);
            mViewPager.setCurrentItem(tabSelectedId);
        }
    }

    public void notifyLocalTabChange(ONewsCity oNewsCity) {
        int positoin = -1;
        if (mAdapter != null) {
            int count = mAdapter.getCount();
            for (int i = 0; i < count; i++) {
                if (mAdapter.scenario(i).getCategory() == ONewsScenarioCategory.SC_2D) {
                    positoin = i;
                    break;
                }
            }
        }
        if (positoin != -1) {
            mTabStrip.notifyLocalTabChange(positoin, oNewsCity);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (null != bundle) {
            tabSelectedId = bundle.getInt(KEY_TABSELECTED);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != delayRefreshMatch && !delayRefreshMatch.isNull()) {
            BackgroundThread.removeTask(delayRefreshMatch);
            BackgroundThread.postDelayed(delayRefreshMatch, DELAY_REFRESH_PLAYING);
            if (L.DEBUG) L.news_item_refresh("NewsFragment post DelayRefreshMatch ");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != delayRefreshMatch) {
            BackgroundThread.removeTask(delayRefreshMatch);
            if (L.DEBUG)
                L.news_item_refresh("NewsFragment remove DelayRefreshMatch " + delayRefreshMatch.hashCode());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (null != outState) {
            outState.putInt(KEY_TABSELECTED, tabSelectedId);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != delayRefreshMatch) {
            BackgroundThread.removeTask(delayRefreshMatch);
            if (L.DEBUG)
                L.news_item_refresh("NewsFragment remove DelayRefreshMatch " + delayRefreshMatch.hashCode());
        }
    }

    @Override
    public void onEventInUiThread(ONewsEvent event) {
        if (event instanceof EventLanguageChange) {
            onHandleEvent_EventLanguageChange();
        } else if (event instanceof EventTestCategory) {
            onHandleEvent_EventTestCategory((EventTestCategory) event);
        } else if (event instanceof EventRefreshMatch) {
            onHandleEvent_EventRefreshMatch((EventRefreshMatch) event);
        } else if (event instanceof EventNewsSelectLocation) {
            onHandleEvent_EventNewsSelectLocation((EventNewsSelectLocation) event);
        } else {
            if (null != mAdapter) {
                mAdapter.onEventInUiThread(event);
            }
        }
    }

    public void onHandleEvent_EventLanguageChange() {
        LanguageCountry languageCountry = UIConfigManager.getInstanse(getContext()).getLanguageSelected(getContext());
        if (LanguageCountry.LANGUAGE_OPTION_EN.equalsIgnoreCase(languageCountry.getLanguage())) {
            ONewsScenarios.getInstance().addVideo();
        } else if (LanguageCountry.LANGUAGE_OPTION_HI.equalsIgnoreCase(languageCountry.getLanguage())) {
            ONewsScenarios.getInstance().removeVideo();
        }
        mAdapter.notifyONewsScenarios();
        mTabStrip.notifyDataSetChanged();
        updateLocalCities();
    }

    private void updateLocalCities() {
        List<ONewsCity> allCities = LocalCityUtils.getAllCities(getActivity());
        if (allCities == null || allCities.size() == 0) {
            new GetCitysTask().execute();
        } else {
            updateRecentLocations(allCities);
        }
    }

    private void onHandleEvent_EventTestCategory(EventTestCategory event) {
        if (event.isAddTest()) {
            ONewsScenarios.getInstance().addTest();
        } else {
            ONewsScenarios.getInstance().removeTest();
        }

        mAdapter.notifyONewsScenarios();
        mTabStrip.notifyDataSetChanged();
    }

    @Override
    protected void onHandleEvent_EventNewsSelectLocation(EventNewsSelectLocation event) {
        super.onHandleEvent_EventNewsSelectLocation(event);
        if (null == event || null == event.oNewsCity() || TextUtils.isEmpty(event.oNewsCity().getCity())) {
            return;
        }
        notifyLocalTabChange(event.oNewsCity());
        if (null != mAdapter) {
            mAdapter.onEventInUiThread(event);
        }
    }

    private PagerSlidingTabStrip.TabInfocListAction mTabInfocListAction = new PagerSlidingTabStrip.TabInfocListAction() {

        @Override
        public void onClickTabReport(int position) {
            infoclistaction(position, 4);
        }

        @Override
        public void onPageSelectedReport(int position) {
            infoclistaction(position, 5);
        }
    };

    private void infoclistaction(int position, int action) {
        if (null == mViewPager) {
            return;
        }

        PagerAdapter adapter = mViewPager.getAdapter();
        if (null == adapter || !(adapter instanceof NewsPageAdapter)) {
            return;
        }

        NewsPageAdapter newsPageAdapter = (NewsPageAdapter) adapter;
        byte category = newsPageAdapter.scenario(position).getCategory();
        if (category <= 0) {
            return;
        }

        newsindia_listaction listaction = new newsindia_listaction();
        listaction.listid(newsPageAdapter.scenario(position).getCategory());
        listaction.action(action);
        listaction.report();
    }

    private void onHandleEvent_EventRefreshMatch(EventRefreshMatch event) {
        if (L.DEBUG) L.news_item_refresh("onHandleEvent_EventRefreshMatch");
        if (null == event || null == event.matches() || event.matches().isEmpty()) {
            return;
        }

        delayRefreshMatch(event.matches());
    }

    private void delayRefreshMatch(List<ONewsMatch> matches) {
        ONewsMatch live = null;
        for (ONewsMatch match : matches) {
            if (ONewsMatch.MATCH_STATUS_PLAYING == match.getStatus()) {
                live = match;
                break;
            } else if (null == live && ONewsMatch.MATCH_STATUS_END != match.getStatus()) {
                live = match;
            } else if (null != live && ONewsMatch.MATCH_STATUS_END != match.getStatus() && match.getStartTime() <= live.getStartTime()) {
                live = match;
            }
        }

        if (null == live) {
            BackgroundThread.removeTask(delayRefreshMatch);
            if (null != mAdapter) {
                mAdapter.onEventInUiThread(new EventCheckCricketMatch());
            }
            if (L.DEBUG) L.news_item_refresh("NewsFragment NULL");
            return;
        }

        if (ONewsMatch.MATCH_STATUS_NO_START == live.getStatus()) {
            long delay = live.getStartTime() * 1000l - System.currentTimeMillis();
            BackgroundThread.removeTask(delayRefreshMatch);
            BackgroundThread.postDelayed(delayRefreshMatch.match(live), delay >= 0 ? delay + 5 * 1000l : DELAY_REFRESH_NO_START);
            if (L.DEBUG) L.news_item_refresh("NewsFragment MATCH_STATUS_NO_START  " + delay);
        } else if (ONewsMatch.MATCH_STATUS_PLAYING == live.getStatus()) {
            BackgroundThread.removeTask(delayRefreshMatch);
            BackgroundThread.postDelayed(delayRefreshMatch.match(live), DELAY_REFRESH_PLAYING);
            if (L.DEBUG) L.news_item_refresh("NewsFragment MATCH_STATUS_PLAYING");
        } else if (ONewsMatch.MATCH_STATUS_END == live.getStatus()) {
            BackgroundThread.removeTask(delayRefreshMatch.match(live));
            if (null != mAdapter) {
                mAdapter.onEventInUiThread(new EventCheckCricketMatch());
            }
            if (L.DEBUG) L.news_item_refresh("NewsFragment MATCH_STATUS_END");
        }
    }

    private DelayRefreshMatch delayRefreshMatch = new DelayRefreshMatch();

    public class GetCitysTask extends AsyncTaskEx<Void, Void, List<ONewsCity>> {

        @Override
        protected List<ONewsCity> doInBackground(Void... params) {
            android.util.Log.i("cc", "get GetCitysTask...");

            List<ONewsCity> oNewsCities = NewsSdk.INSTAMCE.requestCitys();
            if (oNewsCities != null && oNewsCities.size() > 0) {
                java.util.Collections.sort(oNewsCities, new com.cmcm.onews.util.CityComparator());
            }
            return oNewsCities;
        }

        @Override
        protected void onPostExecute(List<ONewsCity> result) {
            android.util.Log.i("cc", "result:" + result);
            if (!isFinish() && result != null && result.size() > 0) {
                LocalCityUtils.saveAllLocations(getActivity(), result);
                updateRecentLocations(result);
            }

        }

    }

    private void updateRecentLocations(List<com.cmcm.onews.model.ONewsCity> result) {
        com.cmcm.onews.model.ONewsCity oNewsCity = LocalCityUtils.updateRecentLocations(getActivity(), result);
        if (oNewsCity != null) {
            com.cmcm.onews.event.FireEvent.FIRE_EventNewsSelectLocation(oNewsCity);
        }
    }
}
