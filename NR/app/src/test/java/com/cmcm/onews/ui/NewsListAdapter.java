package com.cmcm.onews.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.cmcm.onews.NewsL;
import com.cmcm.onews.R;
import com.cmcm.onews.api.IndiaOpenNews;
import com.cmcm.onews.event.EventTranslate;
import com.cmcm.onews.event.FireEvent;
import com.cmcm.onews.infoc.newsindia_listtime;
import com.cmcm.onews.infoc.reportHelper;
import com.cmcm.onews.loader.ONewsStick;
import com.cmcm.onews.model.ONews;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.model.ONewsSupportAction;
import com.cmcm.onews.model.sports.ONewsMatch;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.service.TranslatorTitle;
import com.cmcm.onews.service.UpdateONewsStatus;
import com.cmcm.onews.ui.item.BaseItem;
import com.cmcm.onews.ui.item.BaseNewsItem;
import com.cmcm.onews.ui.item.NewsAlgorithmReport;
import com.cmcm.onews.ui.item.NewsCricketGame;
import com.cmcm.onews.ui.item.Types;
import com.cmcm.onews.util.BackgroundThread;
import com.cmcm.onews.util.NewsDebugConfigUtil;
import com.cmcm.onews.util.SDKConfigManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsListAdapter extends BaseAdapter {
    private static final int MAX_NEW = 3;
    private boolean isShowImg;
    private Context mContext;
    private LayoutInflater inflater;
    private InvalidateNotifyHelper notifyHelper;
    private ListView mListView;

    private boolean isAlgorithmListShow = false;
    private Map<String, BaseNewsItem> reportShowMap = new HashMap<String, BaseNewsItem>();
    private Map<String, BaseNewsItem> reportClickMap = new HashMap<String, BaseNewsItem>();
    private Map<String, BaseItem> reportInfocMap = new HashMap<String, BaseItem>();
    private List mList = new ArrayList();

    public NewsListAdapter(Context context, ListView l, boolean showImg) {
        this.mContext = context;
        this.mListView = l;
        this.inflater = LayoutInflater.from(mContext);
        this.isShowImg = showImg;
        this.notifyHelper = new InvalidateNotifyHelper() {
            @Override
            protected void onNeedNotify() {
                infocpos();
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public BaseItem getItem(int i) {
        return (BaseItem) mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return Types.ITEM_SIZE;
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= mList.size()) {
            return Types.BASE_ITEM_TYPE;
        }

        BaseItem item = (BaseItem) mList.get(position);
        if (item != null) {
            return item.type();
        } else {
            return Types.BASE_ITEM_TYPE;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        long time = 0;
        if(NewsL.DEBUG) time = System.currentTimeMillis();

        final BaseItem item = getItem(position);
        if (null != item) {
            convertView = item.getView(inflater, convertView, isShowImg);
        }

        convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        convertView.clearAnimation();

        View click = convertView.findViewById(R.id.item_container);
        if(null != click){
            click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListView.performItemClick(v, position, getItemId(position));
                    item.reportClick();
                    reportOpenCms(item);
                    cacheClickReportItem(item);
                }
            });
        }
        cacheShowReportItem(item);

        if(NewsL.DEBUG){
            NewsL.item(String.format("news item create time %s, %s",item.title(),item.id()));
            NewsL.item(String.format("news item create time %d",System.currentTimeMillis() - time));
            NewsL.item("****************************");
        }

        return convertView;
    }

    /**
     * put
     *
     * @param items
     */
    public void put(List<BaseItem> items) {
        if (null == items || items.isEmpty() || null == mList) {
            return;
        }

        mList.clear();
        mList.addAll(items);

        ONewsStick.stick_first(mList);
        label_new(mList);
        chechRefresh();
        notifyHelper.add(true);
    }

    /**
     * bottom load more
     * @param items
     */
    public void putMore(List<BaseItem> items) {
        if (null == items || items.isEmpty() || null == mList) {
            return;
        }
        filterDuplicate(mList, items, false);
    }

    /**
     * top refresh
     *
     * @param items
     */
    public void putRefresh(List<BaseItem> items) {
        if (null == items || items.isEmpty() || null == mList) {
            return;
        }
        filterDuplicate(mList, items, true);
        chechRefresh();
    }

    /**
     * todo 1.去重增量添加 2.新数据和老数据对比
     * @param items
     * @param mores
     * @param isAddTop
     */
    private void filterDuplicate(List<BaseItem> items, List<BaseItem> mores, boolean isAddTop) {
        List<BaseItem> duplicates = duplicates(items, mores);
        if (null != duplicates && !duplicates.isEmpty()) {
            for (BaseItem item : duplicates) {
                items.remove(item);
            }
        }

        if (isAddTop) {
            addToTop(items, mores);
        } else {
            addToBottom(items, mores);
        }

        label_new(items);
        notifyHelper.add(true);
    }

    /**
     * todo  去重 新数据和老数据对比
     * @param items
     * @param mores
     * @return
     */
    private List<BaseItem> duplicates(List<BaseItem> items, List<BaseItem> mores) {
        List<BaseItem> duplicates = new ArrayList<BaseItem>();
        for (BaseItem item : items) {
            if (isContains(mores, item)) {
                duplicates.add(item);
            }
        }
        return duplicates;
    }

    /**
     * isSame
     *
     * @param items
     * @param item
     * @return
     */
    private boolean isContains(List<BaseItem> items, BaseItem item) {
        boolean isContains = false;
        for (BaseItem baseItem : items) {
            if (item instanceof BaseNewsItem && baseItem instanceof BaseNewsItem) {
                if (BaseNewsItem.isSame((BaseNewsItem) baseItem, (BaseNewsItem) item)) {
                    isContains = true;
                    break;
                }
            }
        }
        return isContains;
    }

    /**
     * 添加到列表顶部
     *
     * @param list
     * @param realItems
     */
    private void addToTop(List list, List realItems) {
        ONewsStick.stick_refresh(list, realItems);
    }

    /**
     * 添加到列表底部 底部无置顶新闻
     *
     * @param items
     * @param mores
     */
    private void addToBottom(List items, List mores) {
        ONewsStick.stick_more(items, mores);
    }

    /**
     * todo new标签过滤  保证一个列表只有三个
     * @param list
     */
    private void label_new(List<BaseItem> list) {
        int count = 0;
        for(int i=0;i<list.size();i++){
            BaseItem item = list.get(i);
            if(item.isNew() && count < MAX_NEW){
                count += 1;
            }else {
                item.isNew(false);
            }
        }
    }

    public void onImageConfigChange() {
        this.isShowImg = SDKConfigManager.getInstanse(mContext).getNEWS_ITEM_SHOWIMG();
        notifyHelper.add(true);
    }

    public int getItemIndex(ONews news, ONewsScenario scenario) {
        if (null == mList || mList.isEmpty() || null == news) {
            return -100;
        }
        for (int i = 0; i < mList.size(); i++) {
            BaseItem item = (BaseItem) mList.get(i);
            if (item instanceof BaseNewsItem) {
                if (BaseNewsItem.isSame(news, scenario, (BaseNewsItem) item)) {
                    return i;
                }
            }
        }
        return -100;
    }

    public void removeItem(ONews news, ONewsScenario scenario) {
        BaseItem item = getItem(news, scenario);
        if (null == item || null == mList) {
            return;
        }
        mList.remove(item);
        notifyHelper.add(true);
    }

    public BaseItem getItem(ONews news, ONewsScenario scenario) {
        for (int i = 0; i < mList.size(); i++) {
            BaseItem item = (BaseItem) mList.get(i);
            if (BaseNewsItem.isSame(news, scenario, (BaseNewsItem) item)) {
                return item;
            }
        }
        return null;
    }

    public void dataChange() {
        notifyHelper.add(true);
    }

    /**
     * 算法展示上报缓存
     *
     * @param item
     */
    private void cacheShowReportItem(BaseItem item) {
        if (null == item) {
            return;
        }

        //todo 无图模式部分类型卡片不展示不用统计展示上报
        if(!isShowImg && !item.isShowContainer()){
            return;
        }

        if (!reportShowMap.containsKey(item.id()) && item instanceof BaseNewsItem && needShowCache(item)) {
            reportShowMap.put(item.id(), (BaseNewsItem) item);
        }

        if (!reportInfocMap.containsKey(item.id())) {
            reportInfocMap.put(item.id(), item);
        }

        reportHelper.getInstance().addView(item.id());
    }

    private boolean needShowCache(BaseItem item) {
        BaseNewsItem newsItem = (BaseNewsItem) item;
        ONews onews = newsItem.oNews();
        if (onews.x_stimes() > 0) {
            return false;
        }
        return true;
    }

    /**
     * 算法点击上报缓存
     *
     * @param item
     */
    private void cacheClickReportItem(BaseItem item) {
        if (null == item) {
            return;
        }

        if (!reportClickMap.containsKey(item.id()) && item instanceof BaseNewsItem && needClickCache(item)) {
            reportClickMap.put(item.id(), (BaseNewsItem) item);
        }
    }

    private boolean needClickCache(BaseItem item) {
        BaseNewsItem newsItem = (BaseNewsItem) item;
        ONews onews = newsItem.oNews();
        if (onews.x_ctimes() > 0) {
            return false;
        }
        return true;
    }

    /**
     * 算法上报
     * 1.算法点击和展示上报
     * 2.展示上报必须再点击上报前面
     */
    public void reportAlgorithm(ONewsScenario scenario) {
        NewsAlgorithmReport.algorithmNewsList(scenario, geAlgorithmtCacheItems(reportShowMap), geAlgorithmtCacheItems(reportClickMap));
    }

    /**
     * 算法上报
     * 1.算法点击和展示上报
     * 2.展示上报必须再点击上报前面
     */
    public void reportAlgorithm_ALBUM(ONewsScenario scenario,String albumid) {
        NewsAlgorithmReport.algorithmNewsList_ALBUM(scenario, geAlgorithmtCacheItems(reportShowMap), geAlgorithmtCacheItems(reportClickMap), albumid);
    }

    /**
     * 算法上报
     * 1.算法点击和展示上报
     * 2.展示上报必须再点击上报前面
     */
    public void reportAlgorithm_ALBUM_GCM(ONewsScenario scenario,String albumid,String upack) {
        NewsAlgorithmReport.algorithmNewsList_ALBUM_GCM(scenario, geAlgorithmtCacheItems(reportShowMap), geAlgorithmtCacheItems(reportClickMap), albumid, upack);
    }

    public void reportAlgorithm_OPENCMS(ONewsScenario scenario,String upack){
        NewsAlgorithmReport.algorithmNewsList_OPENCMS(scenario, geAlgorithmtCacheItems(reportShowMap), geAlgorithmtCacheItems(reportClickMap), upack);
    }

    private List<BaseNewsItem> geAlgorithmtCacheItems(Map<String, BaseNewsItem> reportMap) {
        List<BaseNewsItem> items = new ArrayList<BaseNewsItem>();
        for (Map.Entry<String, BaseNewsItem> entry : reportMap.entrySet()) {
            if (entry.getValue() instanceof BaseNewsItem) {
                items.add(entry.getValue());
                entry.setValue(null);
            }
        }
        return items;
    }

    public void onClearAllOffline() {
        if (null != mList && !mList.isEmpty()) {
            for (Object item : mList) {
                if (item instanceof BaseNewsItem) {
                    ((BaseNewsItem) item).oNews().x_bookmark(ONews.STATUS_INIT);
                }
            }
            notifyHelper.add(true);
        }
    }

    public void onNewsUpdateRead(ONewsScenario scenario, String contentId) {
        if (null != mList && !mList.isEmpty()) {
            for (Object item : mList) {
                if (item instanceof BaseNewsItem) {
                    if (BaseNewsItem.isSame(((BaseNewsItem) item), scenario, contentId)) {
                        ((BaseNewsItem) item).oNews().isread(ONews.STATUS_READ);
                        notifyHelper.add(true);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 卡片点击事件
     *
     * @param position
     * @param activity
     */
    public void onItemClick(int position, Activity activity) {
        if (position >= getCount()) {
            return;
        }

        BaseItem item = getItem(position);

        if (item instanceof BaseNewsItem) {
            onItemClickNews((BaseNewsItem) item, activity);
        }

        reportHelper.getInstance().addRead(item.id());
    }

    private void onItemClickNews(BaseNewsItem item, Activity activity) {
        if(NewsDebugConfigUtil.getInstance().isTransLateEnabled()){
            if(!item.isTranslate()){
                item.isTranslate(true);
                BackgroundThread.post(new TranslatorTitle(item.scenario(),item.oNews().title(),item.oNews().contentid()));
                return;
            }
        }

        if (!item.oNews().isRead() && !ONewsSupportAction.supportAction(ONewsSupportAction.SA_40).equals(item.oNews().action())) {
            item.oNews().isread(ONews.STATUS_READ);
            dataChange();
            BackgroundThread.post(new UpdateONewsStatus(item.scenario(), item.id()));
        }

        IndiaOpenNews.openNews(item, activity);
    }

    public void transalteONewsTitle(EventTranslate event) {
        for (Object item : mList) {
            if (item instanceof BaseNewsItem && ((BaseNewsItem)item).id().equals(event.contentId())) {
                ((BaseNewsItem) item).oNews().title(event.translate());
                if (NewsL.DEBUG) NewsL.event(event.translate());
                notifyHelper.add(false);
                break;
            }
        }
    }

    public void updateONewsBody(List<ONews> newses) {
        boolean needNotify = false;
        for (ONews news : newses) {
            if (!needNotify) {
                needNotify = updateONewsBody(news);
            } else {
                updateONewsBody(news);
            }
        }

        if (needNotify) {
            notifyHelper.add(false);
        }
    }

    private boolean updateONewsBody(ONews news) {
        for (Object item : mList) {
            if (item instanceof BaseNewsItem && ((BaseNewsItem)item).id().equals(news.contentid())) {
                ((BaseNewsItem) item).oNews().body(news.body());
                ((BaseNewsItem) item).oNews().headimage(news.headimage());
                ((BaseNewsItem) item).oNews().bodyimages(news.bodyimages());
                if(null != news.openCms()){
                    ((BaseNewsItem) item).oNews().opencms_info(news.opencms_info());
                }

                if (NewsL.DEBUG) NewsL.event(news.body());

                return true;
            }
        }
        return false;
    }

    public void clear(){
        if(null != mList){
            mList.clear();
        }
    }

    public void onHandleEvent_EventNewsAdClick(ONews news, ONewsScenario scenario) {
        if(null == mList || mList.isEmpty()){
            return;
        }

        for(Object item : mList){
            if(item instanceof BaseNewsItem){
                BaseNewsItem newsItem = (BaseNewsItem) item;
                if(BaseNewsItem.isSame(news,scenario,newsItem)){
                    cacheClickReportItem(newsItem);
                    break;
                }
            }
        }
    }

    public void onHandleEvent_EventLive(ONewsMatch match) {
        if(null != mList && !mList.isEmpty()){
            updateCricketGame(match, mList);
        }
    }

    private void updateCricketGame(ONewsMatch match,List list) {
        NewsCricketGame game = null;
        for(int i=0;i<list.size();i++){
            Object obj = list.get(i);
            if(obj instanceof NewsCricketGame) {
                game = (NewsCricketGame) obj;
                break;
            }
        }

        if(null == game){
            return;
        }

        game.updateStatus(match);
        notifyHelper.add(true);
    }

    /**
     * todo 只针对未结束板球卡片
     * 触发刷新逻辑
     */
    public void chechRefresh() {
        if(L.DEBUG) L.news_item_refresh("NewsListAdapter chechRefresh");
        if(null == mList || mList.isEmpty()){
            return;
        }

        ONews news = null;
        for(Object obj : mList){
            boolean bounce = false;
            if(obj instanceof NewsCricketGame){
                BaseNewsItem item = (BaseNewsItem) obj;
                for(ONewsMatch match : item.oNews().matchList()){
                    if(ONewsMatch.MATCH_STATUS_END != match.getStatus()){
                        news = item.oNews();
                        bounce = true;
                        if(!refreshes.containsKey(news.contentid())){
                            refreshes.put(news.contentid(),item);
                        }
                        break;
                    }
                }
            }

            if(bounce){
                break;
            }
        }
        if(null != news){
            refreshCricketMatch(news);
        }
    }

    private void refreshCricketMatch(ONews news){
        FireEvent.FIRE_EventRefreshMatch(news.matchList());
    }

    private Map<String,BaseNewsItem> refreshes = new HashMap<String,BaseNewsItem>();
    public void onResume() {
        if(L.DEBUG) L.news_item_refresh("NewsListAdapter onResume");
        if(null == mList || mList.isEmpty()){
            return;
        }

        if(refreshes.isEmpty()){
            return;
        }

        for (Map.Entry<String, BaseNewsItem> entry : refreshes.entrySet()) {
            entry.getValue().onResume();
        }
    }

    public void onPause() {
        if(L.DEBUG) L.news_item_refresh("NewsListAdapter onPause");
        if(null == mList || mList.isEmpty()){
            return;
        }

        if(refreshes.isEmpty()){
            return;
        }

        for (Map.Entry<String, BaseNewsItem> entry : refreshes.entrySet()) {
            entry.getValue().onPause();
        }
    }

    /*****************************************************************
     * 上报逻辑
     ****************************************************************/
    private List<BaseItem> getCacheItems(Map<String, BaseItem> reportMap) {
        List<BaseItem> items = new ArrayList<BaseItem>();
        for (Map.Entry<String, BaseItem> entry : reportMap.entrySet()) {
            if (entry.getValue() instanceof BaseItem) {
                items.add(entry.getValue());
                entry.setValue(null);
            }
        }
        return items;
    }

    public void infocShow() {
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                List<BaseItem> items = getCacheItems(reportInfocMap);
                for (BaseItem item : items) {
                    item.infocnewsaction(false);
                }
            }
        });
    }

    /**
     * todo 1.记录新闻位置
     */
    private void infocpos() {
        for (int i = 0; i < mList.size(); i++) {
            ((BaseNewsItem)mList.get(i)).infocpos(i + 1);
        }
    }

    /**
     * 列表页算法上报
     *
     * @param duration
     */
    public void algorithmListReport(int duration,ONews news,ONewsScenario scenario) {
        if (null != scenario) {
            if (!isAlgorithmListShow) {
                NewsAlgorithmReport.algorithmListShow(scenario,news);
                isAlgorithmListShow = true;
            }
            NewsAlgorithmReport.algorithmListTime(scenario, news, duration);
        }
    }

    /**
     * 列表页infoc上报
     *
     * @param duration
     * @param infoc_refresh
     * @param infoc_bookmark
     * @param category
     */
    public void infocListTime(long duration, int infoc_refresh, int infoc_bookmark, byte category, int infoc_upload) {
        newsindia_listtime listtime = new newsindia_listtime();
        listtime.duration((int) duration);
        listtime.bookmark(infoc_bookmark);
        listtime.listid(category);
        listtime.refresh(infoc_refresh);
        listtime.loadnum(infoc_upload);
        listtime.listnum(null != reportShowMap ? reportShowMap.size() : 0);
        listtime.report();
    }

    public int showCount(){
        return null != reportInfocMap ? reportInfocMap.size() : 0;
    }

    private void reportOpenCms(BaseItem item){
        BaseNewsItem news = null;
        if(item instanceof BaseNewsItem){
            news = (BaseNewsItem) item;
        }

        if(null == news){
            return;
        }

        if(null != news.scenario()
            && null != news.oNews()
            && news.scenario().isOpenCmsScenario()){
            NewsOpenCmsActivity.reportOpenCms(news.oNews(),2);
        }
    }
}
