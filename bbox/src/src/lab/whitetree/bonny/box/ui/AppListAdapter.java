package lab.whitetree.bonny.box.ui;

import java.util.ArrayList;

import lab.whitetree.bonny.box.R;

import org.whitetree.systable.data.PkgInfo;
import org.whitetree.systable.system.U;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<PkgInfo> mPkgData;
	private Context mContext = null;

	public AppListAdapter(Context context, ArrayList<PkgInfo> pkgData) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mPkgData = pkgData;
	}

	public ArrayList<PkgInfo> getData() {
		return mPkgData;
	}

	@Override
	public int getCount() {
		if (mPkgData == null) {
			return 0;
		} else {
			return mPkgData.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (mPkgData == null) {
			return null;
		}
		return mPkgData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.apps_list_item, null);

			holder = new ViewHolder();
			holder.layout = (LinearLayout) convertView.findViewById(R.id.item_layout);
			holder.img = (ImageView) convertView.findViewById(R.id.app_icon);
			holder.status = (ImageView) convertView.findViewById(R.id.app_storage_status);
			holder.name = (TextView) convertView.findViewById(R.id.app_name);
			holder.size = (TextView) convertView.findViewById(R.id.app_size);

			holder.selected = (CheckBox) convertView.findViewById(R.id.select);
			holder.selected.setClickable(false);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(mPkgData.get(position).mAppName);
		holder.size.setText(U.formatBytes(mPkgData.get(position).mCacheSize));

		holder.img.setImageDrawable(mPkgData.get(position).mAppIcon);
		holder.img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				U.showInstalledAppDetails(mContext, mPkgData.get(position).mPackageName);
			}
		});

		if (mPkgData.get(position).mCouldMove2Sdcard) {
			holder.status.setVisibility(View.VISIBLE);
			holder.status.setBackgroundResource(R.drawable.app_storage_status_could_move_to_sdcard);
		} else if (!mPkgData.get(position).mIsInternal) {
			holder.status.setVisibility(View.VISIBLE);
			holder.status.setBackgroundResource(R.drawable.app_storage_status_in_sdcard);
		} else {
			holder.status.setVisibility(View.INVISIBLE);
		}

		holder.selected.setChecked(mPkgData.get(position).mIsSelected);

		if (mPkgData.get(position).mIsSelected) {
			holder.layout.setBackgroundResource(R.drawable.list_item_selected_bg);
		} else {
			holder.layout.setBackgroundResource(R.drawable.list_item_normal_bg);
		}

		return convertView;
	}

	static class ViewHolder {
		LinearLayout layout;
		TextView name;
		TextView size;
		ImageView img;
		ImageView status;

		CheckBox selected;
	}
}
