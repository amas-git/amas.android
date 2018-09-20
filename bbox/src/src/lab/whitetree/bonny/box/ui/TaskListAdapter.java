package lab.whitetree.bonny.box.ui;

import java.util.ArrayList;

import lab.whitetree.bonny.box.R;

import org.whitetree.systable.data.Application;
import org.whitetree.systable.system.U;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<Application> mData;
	private Context mContext;

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		mInflater = null;
		mData = null;
	}

	public TaskListAdapter(Context context, ArrayList<Application> data) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
		mData = data;
		mContext = context;
	}

	/**
	 * Get all items
	 * 
	 * @return
	 */
	public synchronized ArrayList<Application> getAll() {
		ArrayList<Application> all = new ArrayList<Application>();
		for (Application _ : mData) {
			all.add(_);
		}
		return all;
	}

	public synchronized int getCount() {
		return mData.size();
	}

	public synchronized Object getItem(int position) {
		return mData.get(position);
	}

	public synchronized long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid unneccessary
		// calls to findViewById() on each row.
		final ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.tasklist_item, null);

			holder = new ViewHolder();
			holder.kill = (Button) convertView.findViewById(R.id.btn_kill);
			holder.iconView = (ImageView) convertView.findViewById(R.id.icon);
			holder.nameView = (TextView) convertView.findViewById(R.id.name);
			holder.memoryView = (TextView) convertView.findViewById(R.id.memory);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		final Application app = getApplication(position);
		holder.nameView.setText(app.getLabelName(mContext));
		holder.iconView.setImageDrawable(app.getIcon(mContext));
		String memInfo = U.formatBytes(app.getMemory(mContext));
		holder.memoryView.setText(memInfo);

		holder.kill.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				U.kill(mContext, app.packageName, app.pid);
				remove(app, true);
			}
		});
		
		holder.iconView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				U.showInstalledAppDetails(mContext, app.packageName);
			}
		});

		return convertView;
	}

	public synchronized Application getApplication(int pos) {
		return mData.get(pos);
	}

	static class ViewHolder {
		Button kill;
		ImageView iconView;
		TextView nameView;
		TextView memoryView;
	}

	public void remove(Application app) {
		remove(app, false);
	}

	public synchronized void remove(Application app, boolean notify) {
		mData.remove(app);
		if (notify) {
			notifyDataSetChanged();
		}
	}

}
