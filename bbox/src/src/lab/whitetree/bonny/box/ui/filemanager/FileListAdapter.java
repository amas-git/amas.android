package lab.whitetree.bonny.box.ui.filemanager;

import java.io.File;
import java.util.ArrayList;

import lab.whitetree.bonny.box.R;

import org.whitetree.systable.system.U;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter {

	LayoutInflater                   mInflater = null;
	ArrayList<FileSystemNode>        mNodes    = null;
	Context                          mContext  = null;
	
	
	public FileListAdapter(Context context, ArrayList<FileSystemNode> nodes) {
		mInflater = LayoutInflater.from(context);
		mNodes    = nodes;
		mContext  = context;
	}
	
	public boolean remove(FileSystemNode node, boolean notifyChange) {
		if (node != null && node.delete()) {
			mNodes.remove(node);
			if(notifyChange) {
				notifyDataSetChanged();
			}
			return true;
		}
		return false;
	}
	
	public void addItem(FileSystemNode node	) {
		mNodes.add(node);
		notifyDataSetChanged();
	}
	
	public int getCount() {
		return mNodes.size();
	}

	public Object getItem(int position) {
		return mNodes.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder _H = null;
		if (convertView == null) {
			_H = new ViewHolder();
			convertView = mInflater.inflate(R.layout.fmanager_list_items, null);
			_H.tvName = (TextView ) convertView.findViewById(R.id.tv_name);
			_H.tvSize = (TextView ) convertView.findViewById(R.id.tv_size);
			_H.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
			convertView.setTag(_H);
		} else {
			_H = (ViewHolder) convertView.getTag();
		}
		
		
		// Use ViewHolder bind target view 
		final    FileSystemNode node = mNodes.get(position);
		MimeInfo mi = node.getMimeInfo();
		
		_H.tvName.setText(node.getName());
		if (node.isDirectory()) {
			_H.tvSize.setVisibility(View.GONE);
		} else {
			_H.tvSize.setVisibility(View.VISIBLE);
			_H.tvSize.setText(U.formatBytes(node.getSize()));
		}
		_H.ivIcon.setImageDrawable(mContext.getResources().getDrawable(mi.getIconId()));
		return convertView;
	}

	/**
	 * class ViewHolder
	 * */
	static class ViewHolder {
		TextView  tvName;
		TextView  tvSize;
		ImageView ivIcon;
	}


	/**
	 * @param node
	 * @param new name without parent path
	 * @return
	 */
	public boolean rename(FileSystemNode node, String text) {
		int i = mNodes.indexOf(node);
		if(i > 0) {
			File target = new File(node.getParent().getAbsolutePath()+"/"+text);
			boolean success = node.rename(target);
			mNodes.set(i,new FileSystemNode(target));
			notifyDataSetChanged();
			return success;
		}
		return false;
	}
}