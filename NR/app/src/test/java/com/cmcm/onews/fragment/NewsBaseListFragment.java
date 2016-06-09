package com.cmcm.onews.fragment;

import android.animation.ObjectAnimator;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcm.onews.C;
import com.cmcm.onews.R;
import com.cmcm.onews.event.EventNewsAdClick;
import com.cmcm.onews.event.EventNewsBody;
import com.cmcm.onews.event.EventNewsRead;
import com.cmcm.onews.event.EventOffline;
import com.cmcm.onews.event.EventTranslate;
import com.cmcm.onews.model.ONews;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.model.ONewsScenarioCategory;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.ui.NewsListAdapter;
import com.cmcm.onews.ui.widget.RemoveItemAnimation;
import com.cmcm.onews.util.StringUtils;

public class NewsBaseListFragment extends NewsBaseFragment {
    protected static final long TIME_LIMIT = 1 * 1000l;//加载延迟
    protected NewsListAdapter mAdapter;

    public static NewsBaseFragment newInstance(ONewsScenario scenario) {
        NewsBaseListFragment fragment;
        if (scenario.getCategory() == ONewsScenarioCategory.SC_2D) {
            fragment = new NewsLocalListFragment();
        } else {
            fragment = new NewsListFragment();
        }
        return setArgument(fragment, scenario);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (L.DEBUG) L.newslist("NewsListFragment onPause");
        report();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (L.DEBUG) L.newslist("NewsListFragment onResume");
    }

    protected void report() {
        if (null != mAdapter && isInitialize) {
            if (isFromGcm() && mScenario.isAlbumScenario()) {
                mAdapter.reportAlgorithm_ALBUM_GCM(mScenario, mAlbumid, mUpack);
            } else if (mScenario.isOpenCmsScenario()) {
                mAdapter.reportAlgorithm_OPENCMS(mScenario, mUpack);
            } else if (mScenario.isAlbumScenario()) {
                mAdapter.reportAlgorithm_ALBUM(mScenario, mAlbumid);
            } else {
                mAdapter.reportAlgorithm(mScenario);
            }

            if (adder.end() > 0) {
                if (!mScenario.isAlbumScenario()) {
                    mAdapter.algorithmListReport(adder.end(), mONews, mScenario);
                }
                mAdapter.infocListTime(adder.end(), infoc_refresh, 0, mScenario.getCategory(), infoc_upload);
                mAdapter.infocShow();
                adder.zero();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInitialize = false;
        infoc_refresh = 0;
        infoc_upload = 0;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (L.DEBUG) L.newslist("NewsListFragment onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (L.DEBUG) L.newslist("NewsListFragment onDestroy");
    }

    @Override
    protected void onHandleEvent_EventNewsBody(EventNewsBody event) {
        super.onHandleEvent_EventNewsBody(event);
        if (null == mScenario || mScenario.getCategory() != event.scenario().getCategory()) {
            return;
        }

        if (null != mAdapter && null != event.oNewses() && !event.oNewses().isEmpty()) {
            mAdapter.updateONewsBody(event.oNewses());
        }

    }

    @Override
    protected void onHandleEvent_EventTranslate(EventTranslate event) {
        super.onHandleEvent_EventTranslate(event);

        if (null == mScenario || mScenario.getCategory() != event.scenario().getCategory()) {
            return;
        }

        if (null != mAdapter && !TextUtils.isEmpty(event.contentId()) && !TextUtils.isEmpty(event.translate())) {
            mAdapter.transalteONewsTitle(event);
        }
    }

    @Override
    protected void onHandleEvent_EventImageConfig() {
        super.onHandleEvent_EventImageConfig();
        if (null != mAdapter) {
            mAdapter.onImageConfigChange();
        }
    }

    @Override
    protected void onHandleEvent_EventNewsRead(EventNewsRead event) {
        super.onHandleEvent_EventNewsRead(event);
        if (L.DEBUG) L.newslist("onHandleEvent_EventNewsRead ");
        if (null == mScenario || mScenario.getCategory() != event.scenario().getCategory() && mScenario.getCategory() != 0x00) {
            return;
        }
        mAdapter.onNewsUpdateRead(event.scenario(), event.contentId());
    }

    @Override
    protected void onHandleEvent_EventOffline(EventOffline event) {
        super.onHandleEvent_EventOffline(event);
    }

    @Override
    protected void onHandleEvent_EventNewsAdClick(EventNewsAdClick event) {
        super.onHandleEvent_EventNewsAdClick(event);
        if (null == event.news() || null == event.scenario() || null == mScenario) {
            if (L.DEBUG)
                L.newslist(String.format("onHandleEvent_EventNewsAdClick  %s", "ONewsScenario NULL"));
            return;
        }

        if (mScenario.getStringValue().equals(event.scenario().getStringValue())) {
            if (null != mAdapter) {
                mAdapter.onHandleEvent_EventNewsAdClick(event.news(), event.scenario());
            } else {
                if (L.DEBUG)
                    L.newslist(String.format("onHandleEvent_EventNewsAdClick  %s", "NewsListAdapter NULL"));
            }
        } else {
            if (L.DEBUG)
                L.newslist(String.format("onHandleEvent_EventNewsAdClick  %s", "different ONewsScenario"));
        }
    }

    /**
     * 卡片跳转
     */
    protected class NewsOnItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (L.DEBUG) L.newslist("onItemClick  " + position);
            if (null != mAdapter) {
                mAdapter.onItemClick(position, getActivity());
            }
        }
    }

    protected void removeItemWithAnim(final ONews news, final ONewsScenario scenario, final ListView listView) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                View view = getItemView(news, scenario, listView);
                if (null != view) {
                    new RemoveItemAnimation(view) {
                        @Override
                        protected void onAnimFinish() {
                            removeItem(news, scenario);
                        }
                    }.load();
                } else {
                    removeItem(news, scenario);
                }
            }
        });
    }

    protected View getItemView(ONews news, ONewsScenario scenario, ListView listView) {
        if (null == mAdapter || null == listView) {
            return null;
        }

        int index = mAdapter.getItemIndex(news, scenario);
        int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount();
        int wantedChild = index - firstPosition;
        if (wantedChild < 0 || wantedChild >= listView.getChildCount()) {
            return null;
        }
        return listView.getChildAt(wantedChild);
    }

    protected void removeItem(ONews news, ONewsScenario scenario) {
        if (null == mAdapter) {
            return;
        }
        mAdapter.removeItem(news, scenario);

        if (mAdapter.getCount() == 0) {
            empty();
        }
    }

    protected void empty() {
        if (L.DEBUG) L.newslist("removeItemWithAnim empty");
    }

    protected int infoc_refresh = 0;
    protected int infoc_upload = 0;

    /**
     *  下拉刷新提示
     */
    protected void toastLoadRefresh(int count){
        if(count == 0){
            showTopToast(StringUtils.getString(C.getAppContext(), R.string.onews__no_time_network));
        }else if(count == -1){
            showTopToast(StringUtils.getString(C.getAppContext(),R.string.onews__no_network));
        }else if(count == -100){
            showTopToast(StringUtils.getString(C.getAppContext(),R.string.onews__refresh_latest));
        }else{
            if (null != mScenario && ONewsScenarioCategory.SC_1C == mScenario.getCategory()) {
                showTopToast(StringUtils.getString(C.getAppContext(),R.string.onews__refresh_toast_video,count));
            } else {
                showTopToast(StringUtils.getString(C.getAppContext(),R.string.onews__refresh_toast,count));
            }
        }
    }

    /**
     * 加载更多提示
     */
    protected void toastLoadMore(int count){
        mToastBottomAnimator.cancel();
        mToastBottom.setVisibility(View.VISIBLE);

        if(count == 0){
            mToastBottomText.setText(R.string.onews__no_more_news);
        }else {
            mToastBottomText.setText(R.string.onews__no_network);
        }

        mToastBottomAnimator.setDuration(TIME_TOAST);
        mToastBottomAnimator.start();
    }

    /**
     * 头部提示
     * @param msg
     */
    protected void showTopToast(String msg) {
        if(null == mToastTopAnimator
            || null == mToastTop
            || null == mToastTopText ){
            return;
        }

        mToastTopAnimator.cancel();
        mToastTop.setVisibility(View.VISIBLE);
        mToastTopText.setText(msg);
        mToastTopAnimator.setDuration(TIME_TOAST);
        mToastTopAnimator.start();
    }

    protected RelativeLayout mToastBottom;
    protected TextView mToastBottomText;
    protected ObjectAnimator mToastBottomAnimator;

    protected static final long TIME_TOAST = 2 * 1000l;
    protected RelativeLayout mToastTop;
    protected TextView mToastTopText;
    protected ObjectAnimator mToastTopAnimator;
}
