package com.example.testwandoujia;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TestitemLayout extends RelativeLayout{

	private Context mContext;
	private ViewHolder mViewHolder;
	
	public TestitemLayout(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    mContext=context;
	    initView();
    }

	public TestitemLayout(Context context){
		this(context,null);
	}
	
	private void initView(){
		LayoutInflater.from(mContext).inflate(R.layout.test_item_layout, this);
		setBackgroundResource(R.drawable.list_group_selector_lrtb);
		setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, dip2px(mContext, 70)));
		mViewHolder = new ViewHolder();
		mViewHolder.mIconImg = (ImageView)findViewById(R.id.browser_icon_imgid);
		mViewHolder.name = (TextView)findViewById(R.id.name);
		mViewHolder.size = (TextView)findViewById(R.id.size);
	}
	
	public void load(final String value) {
		mViewHolder.name.setText(value);
		mViewHolder.size.setText(value);
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	
	
	static class ViewHolder {
		ImageView mIconImg;
		TextView name;
		TextView size;
	}
	
	
}
