package lab.whitetree.bonny.box.ui;

import java.util.ArrayList;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.service.NotificationService;
import lab.whitetree.bonny.box.storage.LocalStorage;
import lab.whitetree.bonny.box.util.CustomMoomUIUtil;

import org.whitetree.bidget.moom.MoomMaster;
import org.whitetree.bidget.moom.MoomView;
import org.whitetree.systable.data.MoomCellData;
import org.whitetree.systable.system.SystemChangedEvent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationStyleActivity extends BaseActivity implements OnClickListener, OnItemSelectedListener {
	
	private String[] mCHIDS = null;
	
	private Gallery mGallery = null;
	private NotificationGalleryAdapter mAdapter = null;
	private TextView mTextView = null;
	
	private ArrayList<ImageView> mMoomViewList = null;
	private ArrayList<ImageView> mFrontViewList = null;
	
	private ArrayList<MoomCellData> mNotificationMoomData = null;
	
	private int mSelectedPos = 0;
	
	public static Intent getLaunchIntent(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, NotificationStyleActivity.class);
		return intent;
	}
	
	public static void startDefault(Context context) {
		context.startActivity(getLaunchIntent(context));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_style_choose);
		
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initNotificationData();
		refreshNotificationViews();
		
		LocalService.startSubscribe(this, mCHIDS);

		onMoomViewClicked(0);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mNotificationMoomData = null;
		
		LocalService.startUnsubscribe(this, mCHIDS);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mCHIDS = null;
	}

	@Override
	protected void onSystemChange(SystemChangedEvent event) {
		super.onSystemChange(event);

		// 1:1 的widget layout刷新
		refreshNotificationViews();
		
		// gallery 中的待选项目 要刷新
		String tag = event.id;
		if (!TextUtils.isEmpty(tag)) {
			View view = mGallery.findViewWithTag(tag);
			if (view != null) {
				view.invalidate();
			}
		}
	}
	
	public void onClickSaveBtn(View view) {
		// 保存widget数据
		LocalStorage.getInstance().saveNotificationMoomData(mNotificationMoomData);
		
		// 通知Notification Bar 刷新
		NotificationService.moomConfigChanged(NotificationStyleActivity.this);
		
		finish();
	}
	

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.moom_view_1:
			onMoomViewClicked(0);
			break;
		case R.id.moom_view_2:
			onMoomViewClicked(1);
			break;
		case R.id.moom_view_3:
			onMoomViewClicked(2);
			break;
		case R.id.moom_view_4:
			onMoomViewClicked(3);
			break;
			
		default:
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		MoomCellData mcd = ((MoomCellData) mAdapter.getItem(arg2));
		if (mcd != null) {
			mNotificationMoomData.get(mSelectedPos).mMoomCHID = mcd.mMoomCHID;
			mNotificationMoomData.get(mSelectedPos).mMoomViewCfg = mcd.mMoomViewCfg;
			
			refreshNotificationViews();
			
			mTextView.setText(mcd.mTitleResId);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
	
	private void init() {
		mCHIDS = CustomMoomUIUtil.getNotifyBarChooseCHIDs();
		
		mGallery = (Gallery) findViewById(R.id.gallery);
		mTextView = (TextView) findViewById(R.id.text);
		
		mMoomViewList = new ArrayList<ImageView>();
		mMoomViewList.add((ImageView) findViewById(R.id.moom_view_1));
		mMoomViewList.add((ImageView) findViewById(R.id.moom_view_2));
		mMoomViewList.add((ImageView) findViewById(R.id.moom_view_3));
		mMoomViewList.add((ImageView) findViewById(R.id.moom_view_4));
		for (ImageView iv : mMoomViewList) {
			iv.setOnClickListener(this);
		}
		
		mFrontViewList = new ArrayList<ImageView>();
		mFrontViewList.add((ImageView) findViewById(R.id.front_view_1));
		mFrontViewList.add((ImageView) findViewById(R.id.front_view_2));
		mFrontViewList.add((ImageView) findViewById(R.id.front_view_3));
		mFrontViewList.add((ImageView) findViewById(R.id.front_view_4));

		mAdapter = new NotificationGalleryAdapter(this, CustomMoomUIUtil.getNotifyBarChooseMoomList());
		mGallery.setAdapter(mAdapter);
		mGallery.setOnItemSelectedListener(this);
	}
	
	private void initNotificationData() {
		mNotificationMoomData = LocalStorage.getInstance().getNotificationMoomData();
		if (mNotificationMoomData == null) {
			// 如果获取通知栏配置数据失败了，就直接退出
			finish();
		}
	}
	
	private void refreshNotificationViews() {
		try {
			for (int i = 0; i < NotificationService.NOTIFICATION_MOOM_VIEW_COUNT; ++i) {
				mMoomViewList.get(i).setImageBitmap(MoomMaster.getInstance().getBitmap(this, mNotificationMoomData.get(i).mMoomViewCfg, 100, 100));
			}
		} catch (Exception e) {
		}
	}

	private void onMoomViewClicked(int pos) {
		// notification 就四个moomview
		if (pos >= 0 && pos < 4) {
			mSelectedPos = pos;
			for (int i = 0; i < 4; ++i) {
				if (i == pos) {
					mFrontViewList.get(i).setVisibility(View.VISIBLE);
				} else {
					mFrontViewList.get(i).setVisibility(View.GONE);
				}
			}
		}
		
		// 要调整gallery显示的位置，和当前moomview的chid匹配上
		String chid = mNotificationMoomData.get(mSelectedPos).mMoomCHID;
		int span = 0;
		for (; span < CustomMoomUIUtil.getNotifyBarChooseMoomList().size(); ++span) {
			if (CustomMoomUIUtil.getNotifyBarChooseMoomList().get(span).mMoomCHID.equals(chid)) {
				break;
			}
		}

		// 位置尽量在正中间
		int position = mAdapter.getCount() / 2;
		int remainder = position % CustomMoomUIUtil.getNotifyBarChooseMoomList().size(); // 取余 
		position = remainder == 1 ? position : (position + remainder - 1);
		mGallery.setSelection(position + span);
	}
	
	private class NotificationGalleryAdapter extends BaseAdapter {
		ArrayList<MoomCellData> mMoomDataList = null;
		private LayoutInflater mInflater;
		private Context mContext;
	  
	    public NotificationGalleryAdapter(Context context, ArrayList<MoomCellData> data)  
	    {  
	        mContext = context;
	        mMoomDataList = data;
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);                 
	    }  
 
	    // 返回最大值，可循环展示
	    public int getCount() {
            return Integer.MAX_VALUE;  
	    }  
	    
	    public Object getItem(int position) {
	    	if (mMoomDataList != null) {
	    		// 取余，获取正确的data
	    		return mMoomDataList.get(position % mMoomDataList.size());
	    	}
	        return null;
	    }
	  
	    public long getItemId(int position) {  
	        return position;
	    }
	    
	    public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.notification_choose_gallery_item, null);
				holder = new ViewHolder();
				holder.moom = (MoomView) convertView.findViewById(R.id.moom);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
            // 取余，获取正确的data  
			String configureUrl = ((MoomCellData) getItem(position % mMoomDataList.size())).mMoomViewCfg;
			
			if(!TextUtils.isEmpty(configureUrl)) {
				holder.moom.loadConfigure(mContext, configureUrl);
				holder.moom.setTag(((MoomCellData) getItem(position % mMoomDataList.size())).mMoomViewTag);
			}
			return convertView;
	    }
	}

	static class ViewHolder {
		MoomView moom;
	}
}
