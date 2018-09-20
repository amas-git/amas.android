package lab.whitetree.bonny.box.ui;

import java.util.ArrayList;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WidgetChooseTitleLayout extends LinearLayout {

	public static final int TITLE_INDEX_CPU = 1;
	public static final int TITLE_INDEX_MEM = 2;
	public static final int TITLE_INDEX_CELL = 3;
	public static final int TITLE_INDEX_DISK = 4;
	public static final int TITLE_INDEX_APP = 5;
	public static final int TITLE_INDEX_NETWORK = 6;

	private ArrayList<WidgetTitleData> mWidgetTitleData = null;

	private HorizontialListView mListView = null;
	private WidgetTitleAdapter mAdapter = null;

	// === listeners ===
	OnTitleSelectedListener mTitleSelectedListener = null;

	public void setOnTitleSelectedListener(OnTitleSelectedListener listener) {
		mTitleSelectedListener = listener;
	}

	public interface OnTitleSelectedListener {
		public void onSelected(int titleIndex, String chid);
	}

	public WidgetChooseTitleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public WidgetChooseTitleLayout(Context context) {
		super(context);
		init();
	}

	public void setSelectedTitleIndex(int idx) {
		for (int i = 0; i < mWidgetTitleData.size(); ++i) {
			if (idx == mWidgetTitleData.get(i).mIndex) {
				mAdapter.setSelectedItemSeq(i);
				return;
			}
		}
	}

	private void initTitleData() {
		mWidgetTitleData = new ArrayList<WidgetTitleData>();

		mWidgetTitleData.add(new WidgetTitleData(TITLE_INDEX_CPU, R.string.widget_cpu, 0));
		mWidgetTitleData.add(new WidgetTitleData(TITLE_INDEX_MEM, R.string.widget_memory, 0));
		mWidgetTitleData.add(new WidgetTitleData(TITLE_INDEX_CELL, R.string.widget_power, 0));
		mWidgetTitleData.add(new WidgetTitleData(TITLE_INDEX_DISK, R.string.widget_sdcard, 0));
		// widget 不需要 app 类的
//		mWidgetTitleData.add(new WidgetTitleData(TITLE_INDEX_APP, R.string.widget_app, 0));
		mWidgetTitleData.add(new WidgetTitleData(TITLE_INDEX_NETWORK, R.string.widget_network, 0));
	}

	private String getCHIDbyTitleIdx(int titleIdx) {
		switch (titleIdx) {
		case TITLE_INDEX_CPU:
			return LocalService.CHID_CPU;
		case TITLE_INDEX_MEM:
			return LocalService.CHID_MEMORY;
		case TITLE_INDEX_CELL:
			return LocalService.CHID_POWER;
		case TITLE_INDEX_DISK:
			return LocalService.CHID_STORAGE_SDCARD;
		case TITLE_INDEX_APP:
			return LocalService.CHID_APPS;
		case TITLE_INDEX_NETWORK:
			return LocalService.CHID_NETWORKS;

		default:
			return LocalService.CHID_CPU;
		}
	}

	@SuppressWarnings("deprecation")
	private void init() {
		mListView = new HorizontialListView(getContext(), null);
		mListView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		this.addView(mListView);

		initTitleData();

		mAdapter = new WidgetTitleAdapter(getContext(), mWidgetTitleData);
		mAdapter.setSelectedItemSeq(0);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mAdapter.setSelectedItemSeq(arg2);
				if (mTitleSelectedListener != null) {
					mTitleSelectedListener.onSelected(
							mWidgetTitleData.get(arg2).mIndex,
							getCHIDbyTitleIdx(mWidgetTitleData.get(arg2).mIndex));
				}
			}
		});
	}

	private class WidgetTitleData {
		public int mIndex = TITLE_INDEX_CPU;
		public int mTitleResId = 0;
		public int mImgResId = 0;

		public WidgetTitleData(int idx, int titleResId, int imgResId) {
			mIndex = idx;
			mTitleResId = titleResId;
			mImgResId = imgResId;
		}
	}

	private class WidgetTitleAdapter extends BaseAdapter {

		private ArrayList<WidgetTitleData> mData = null;
		private LayoutInflater mInflater = null;
		private int mSelectedSeq = 0;

		public WidgetTitleAdapter(Context context,
				ArrayList<WidgetTitleData> data) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mData = data;
		}

		public void setSelectedItemSeq(int seq) {
			if (mSelectedSeq != seq) {
				mSelectedSeq = seq;
				this.notifyDataSetChanged();
			}
		}

		@Override
		public int getCount() {
			if (mData == null) {
				return 0;
			} else {
				return mData.size();
			}
		}

		@Override
		public Object getItem(int arg0) {
			if (arg0 >= 0 && mData != null && mData.size() > arg0) {
				return mData.get(arg0);
			}
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (null == convertView) {
				convertView = mInflater.inflate(
						R.layout.layout_widget_title_list_item, null);
				holder = new ViewHolder();
				holder.layout = (LinearLayout) convertView
						.findViewById(R.id.layout);
				holder.img = (ImageView) convertView.findViewById(R.id.image);
				holder.tv = (TextView) convertView.findViewById(R.id.text);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == mSelectedSeq) {
				holder.layout.setBackgroundResource(R.drawable.mz);
			} else {
				holder.layout.setBackgroundResource(R.drawable.mx);
			}

			int imgResId = ((WidgetTitleData) getItem(position)).mImgResId;
			int textResId = ((WidgetTitleData) getItem(position)).mTitleResId;
			if (imgResId > 0) {
				holder.img.setVisibility(View.VISIBLE);
				holder.img.setBackgroundResource(imgResId);
			} else {
				holder.img.setVisibility(View.GONE);
			}

			if (textResId > 0) {
				holder.tv.setVisibility(View.VISIBLE);
				holder.tv.setText(textResId);
			} else {
				holder.tv.setVisibility(View.GONE);
			}
			return convertView;
		}
	}

	static class ViewHolder {
		LinearLayout layout;
		ImageView img;
		TextView tv;
	}
}
