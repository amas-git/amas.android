package lab.whitetree.bonny.box.ui;

import java.util.ArrayList;

import lab.whitetree.bonny.box.R;

import org.whitetree.bidget.moom.MoomView;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MoomViewGalleryAdapter extends BaseAdapter {
	private static final int DEFAULT_BACKGROUND_COLOR = 0x05000000;
	
	ArrayList<String> mMoomsConfig = new ArrayList<String>();
	private LayoutInflater mInflater;
	private Context mContext;
	private boolean mDisplayMoomBg = true;

	public MoomViewGalleryAdapter(Context context, ArrayList<String> configures) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mMoomsConfig = configures;
	}

	@Override
	public int getCount() {
		return mMoomsConfig == null ?  0 : mMoomsConfig.size();
	}

	@Override
	public Object getItem(int position) {
		return mMoomsConfig.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.moom_gallery_item, null);
			holder = new ViewHolder();
			holder.moom = (MoomView)convertView.findViewById(R.id.moom);

			if (!mDisplayMoomBg) {
				holder.moom.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// XXX set widget content
		
		String configureUrl = (String)getItem(position);
		if(!TextUtils.isEmpty(configureUrl)) {
			holder.moom.loadConfigure(mContext, configureUrl);
		}
		return convertView;
	}

	static class ViewHolder {
		MoomView moom;
	}

	public int getItem(String url) {
		return mMoomsConfig.indexOf(url);
	}
	
	public void displayMoomBackground(boolean display) {
		mDisplayMoomBg = display;
	}
	
	public boolean isDisplayMoomBackground() {
		return mDisplayMoomBg;
	}
}
