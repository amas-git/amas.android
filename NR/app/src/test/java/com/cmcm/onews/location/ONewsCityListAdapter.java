package com.cmcm.onews.location;

import com.cmcm.onews.model.ONewsCity;
import com.cmcm.onews.location.PinnedHeaderListView.PinnedHeaderAdapter;
import com.cmcm.onews.R;


public class ONewsCityListAdapter extends android.widget.BaseAdapter implements
        PinnedHeaderAdapter, android.widget.AbsListView.OnScrollListener {
    private int[] mCounts;
    private java.util.List<ONewsCity> mList;
    private MySectionIndexer mIndexer;
    private android.content.Context mContext;
    private int mLocationPosition = -1;
    private android.view.LayoutInflater mInflater;

    public ONewsCityListAdapter(java.util.List<ONewsCity> mList, MySectionIndexer mIndexer, int[] counts,
                                android.content.Context context) {
        this.mList = mList;
        this.mIndexer = mIndexer;
        this.mContext = context;
        this.mCounts = counts;
        mInflater = android.view.LayoutInflater.from(mContext);
    }

    public void setData(java.util.List<ONewsCity> mList, MySectionIndexer mIndexer, int[] counts) {
        this.mList = mList;
        this.mIndexer = mIndexer;
        this.mCounts = counts;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
        android.view.View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.onews_location_top_city_item, null);

            holder = new ViewHolder();
            holder.group_title = (android.widget.TextView) view.findViewById(R.id.group_title);
            holder.city_name = (android.widget.TextView) view.findViewById(R.id.city_name);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        ONewsCity city = mList.get(position);

        int section = mIndexer.getSectionForPosition(position);
        if (mIndexer.getPositionForSection(section) == position) {
            holder.group_title.setVisibility(android.view.View.VISIBLE);
            holder.group_title.setText(city.getGid());
        } else {
            holder.group_title.setVisibility(android.view.View.GONE);
        }

        holder.city_name.setText(city.getCity());

        return view;
    }

    public static class ViewHolder {
        public android.widget.TextView group_title;
        public android.widget.TextView city_name;
    }

    @Override
    public int getPinnedHeaderState(int position) {
        if (mList != null) {
            int realPosition = position;
            if (realPosition < 0
                    || (mLocationPosition != -1 && mLocationPosition == realPosition)) {
                return PINNED_HEADER_GONE;
            }
            mLocationPosition = -1;
            int section = mIndexer.getSectionForPosition(realPosition);
            int nextSectionPosition = mIndexer.getPositionForSection(section + 1);
            if (nextSectionPosition != -1
                    && realPosition == nextSectionPosition - 1) {
                return PINNED_HEADER_PUSHED_UP;
            }
        }
        return PINNED_HEADER_VISIBLE;
    }

    @Override
    public void configurePinnedHeader(android.view.View header, int position, int alpha) {
        // TODO Auto-generated method stub
        if (mList != null) {
            int realPosition = position;
            int section = mIndexer.getSectionForPosition(realPosition);
            String title = (String) mIndexer.getSections()[section];
            ((android.widget.TextView) header.findViewById(R.id.group_title)).setText(title);
        }
    }

    @Override
    public void onScrollStateChanged(android.widget.AbsListView view, int scrollState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScroll(android.widget.AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        if (mList != null && view instanceof PinnedHeaderListView) {
            com.cmcm.onews.location.PinnedHeaderListView listView = (com.cmcm.onews.location.PinnedHeaderListView) view;
            int headerViewsCount = listView.getHeaderViewsCount();
            listView.configureHeaderView(firstVisibleItem - headerViewsCount);
        }

    }
}
