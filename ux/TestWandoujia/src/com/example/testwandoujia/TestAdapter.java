package com.example.testwandoujia;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TestAdapter extends BaseAdapter{
	
	private Context mContext;
	private ArrayList<String> mList;
	
	public TestAdapter(Context context, ArrayList<String> list){
		mContext = context;
		mList = list;
	}

	@Override
    public int getCount() {
	    return mList.size();
    }

	@Override
    public Object getItem(int position) {
	    return mList.get(position);
    }

	@Override
    public long getItemId(int position) {
	    return position;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		if(null == convertView){
			convertView = new TestitemLayout(mContext);
		}
		((TestitemLayout)convertView).load(mList.get(position));
	    return convertView;
    }


}
