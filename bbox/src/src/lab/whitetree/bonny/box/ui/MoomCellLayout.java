package lab.whitetree.bonny.box.ui;

import java.util.ArrayList;
import java.util.HashSet;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.storage.LocalStorage;
import lab.whitetree.bonny.box.util.CustomMoomUIUtil;

import org.whitetree.bidget.moom.MoomView;
import org.whitetree.systable.data.MoomCellData;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MoomCellLayout extends LinearLayout {

	private boolean mIsChecked = false;
	private boolean mIsInCheckMode = false;
	private boolean mIsInEditMode = false;

	private FrameLayout mRootLayout = null;
	private LinearLayout mMoomViewLayout = null;
	private MoomView mMoomImgView = null;
	private ImageView mAddView = null;
	private ImageView mDeleteView = null;
	private ImageView mCheckView = null;

	private String mMoomViewConfig = null;
	private String mMoomViewCHID = null;
	private String mMoomViewTag = null;
	private int mMoomCellType = MoomCellData.MOOM_CELL_TYPE_ENTRANCE;

	private AlertDialog mChooseDialog = null;
	
	private boolean mUseAnimation = false;

	private int mActivityResultCode = 0;

	private ListView mMoomChooseDlgListView = null;
	private MoomViewSelectListAdapter mMoomChooseDlgAdapter = null;
	private HashSet<String> mUsedCHIDs = null;
	
	private static final int MSG_TYPE_REFRESH_CHOOSE_MOOM_DLG_LIST = 55;
	private class MoomCellHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case MSG_TYPE_REFRESH_CHOOSE_MOOM_DLG_LIST:
				if (mMoomChooseDlgListView != null) {
					mMoomChooseDlgListView.invalidateViews();
				}
				break;
			default:
				break;
			}
		}
	}
	private MoomCellHandler mHandler = new MoomCellHandler();

	public interface onSubscribeChangedListener {
		public void onSubscribe(String chid);

		public void onUnsubscribe(String chid);
	}

	private onSubscribeChangedListener mSubscribeChangedListener = null;

	public MoomCellLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MoomCellLayout(Context context) {
		super(context);
		init();
	}

	public void setChecked(boolean isChecked) {
		mIsChecked = isChecked;
		changeCheckedState(mIsChecked);
	}

	public boolean isChecked() {
		return mIsChecked;
	}

	public void setInEditMode(boolean isEditMode) {
		mIsInEditMode = isEditMode;
		changeEditMode(mIsInEditMode);
	}

	public boolean isInEditMode() {
		return mIsInEditMode;
	}

	public void setInCheckMode(boolean inCheckMode) {
		mIsInCheckMode = inCheckMode;
		mIsChecked = false;
		if (mIsInCheckMode) {
			mCheckView.setVisibility(View.VISIBLE);
		} else {
			mCheckView.setVisibility(View.GONE);
		}
	}

	public void setMoomViewData(String config, String chid, String tag) {
		setMoomViewData(config, chid, tag, MoomCellData.MOOM_CELL_TYPE_ENTRANCE);
	}

	public void setMoomViewData(String config, String chid, String tag, int cellType) {
		mMoomViewCHID = chid;
		mMoomViewConfig = config;
		mMoomViewTag = tag;
		mMoomCellType = cellType;
		setMoomViewImg(mMoomViewConfig, mMoomViewCHID, mMoomViewTag);

		saveMoomViewInfo();
	}

	public String getMoomViewConfig() {
		return mMoomViewConfig;
	}

	public String getMoomViewCHID() {
		return mMoomViewCHID;
	}

	public String getMoomViewTag() {
		return mMoomViewTag;
	}

	public int getCellViewType() {
		return mMoomCellType;
	}

	public void refreshMoomView(String chid) {
		if (!TextUtils.isEmpty(chid) && chid.equals(getMoomViewCHID())) {
			View view = findViewWithTag(chid);
			if (view != null && view instanceof MoomView) {
				view.invalidate();
			}
		}
	}

	public void removeMoomCell() {
		onDeleteViewClicked();
	}

	public void setOnSubscribeChangedListener(
			onSubscribeChangedListener listener) {
		mSubscribeChangedListener = listener;
	}

	public boolean loadMoomData() {
		if (TextUtils.isEmpty(mMoomViewConfig)) {
			String tag = (String) getTag();
			MoomCellData data = new MoomCellData(tag, null, null, null);
			if (LocalStorage.getInstance().getMoomCellData(data)) {
				mMoomViewConfig = data.mMoomViewCfg;
				mMoomViewTag = data.mMoomViewTag;
				mMoomViewCHID = data.mMoomCHID;
				mMoomCellType = data.mCellType;
			}
		}
		if (!TextUtils.isEmpty(mMoomViewConfig)) {
			if (!TextUtils.isEmpty(mMoomViewCHID)) {
				// 通知activity 注册chid
				if (mSubscribeChangedListener != null
						&& !TextUtils.isEmpty(mMoomViewCHID)) {
					mSubscribeChangedListener.onSubscribe(mMoomViewCHID);
				}
			}
			// 显示
			setMoomViewImg(mMoomViewConfig, mMoomViewCHID, mMoomViewTag);
			return true;
		}
		return false;
	}

	public void setActivityResultCode(int code) {
		mActivityResultCode = code;
	}

	public int getActivityResultCode() {
		return mActivityResultCode;
	}
	
	public boolean useAnimation() {
		return mUseAnimation;
	}
	public void setUseAnimation(boolean use) {
		mUseAnimation = use;
	}

	// ====== PRIVATE FUNCS ======
	@SuppressWarnings("deprecation")
	private void init() {
		mRootLayout = new FrameLayout(getContext());
		this.addView(mRootLayout, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));

		// MoomViewLayout
		mMoomViewLayout = new LinearLayout(getContext());
		mRootLayout.addView(mMoomViewLayout, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT));
//		mMoomViewLayout.setBackgroundResource(R.drawable.moomcelllayout_normal);
		mMoomViewLayout.setVisibility(View.GONE);
		mMoomViewLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onMoomViewClicked();
			}
		});

		// MoomImageView
		mMoomImgView = new MoomView(getContext());
		mMoomImgView.setClickable(false);
		mMoomViewLayout.addView(mMoomImgView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));

		// AddView
		mAddView = new ImageView(getContext());
		mRootLayout.addView(mAddView, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT));
		mAddView.setBackgroundResource(R.drawable.moomcelllayout_add);
		mAddView.setVisibility(View.GONE);
		mAddView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onAddViewClicked();
			}
		});

		// DeleteView
		mDeleteView = new ImageView(getContext());
		FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		flp.gravity = Gravity.TOP | Gravity.RIGHT;
		mRootLayout.addView(mDeleteView, flp);
		mDeleteView.setBackgroundResource(R.drawable.moomcelllayout_remove);
		mDeleteView.setVisibility(View.GONE);
		mDeleteView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onDeleteViewClicked();
			}
		});

		// CheckView
		mCheckView = new ImageView(getContext());
		mRootLayout.addView(mCheckView, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT));
		mCheckView.setBackgroundResource(R.drawable.moomcelllayout_check);
		mCheckView.setVisibility(View.GONE);
		mCheckView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onCheckViewClicked();
			}
		});
	}

	private boolean saveMoomViewInfo() {
		String tag = (String) getTag();
		// chid 和 moomviewTag 可以为空，虽然这样会无法更新MoomView，但是default的MoomView可以显示
		if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(mMoomViewConfig)) {
			return LocalStorage.getInstance().saveMoomCellData(
					new MoomCellData(tag, mMoomViewConfig, mMoomViewTag,
							mMoomViewCHID, 0, mMoomCellType));
		} else {
			return false;
		}
	}

	private void changeCheckedState(boolean isChecked) {
		if (!TextUtils.isEmpty(mMoomViewConfig)
				&& mCheckView.getVisibility() == View.VISIBLE) {
			mCheckView.setPressed(isChecked);
			mMoomViewLayout.setEnabled(isChecked);
		}
	}

	private void changeEditMode(boolean isEditMode) {
		if (TextUtils.isEmpty(mMoomViewConfig)) {
			mDeleteView.setVisibility(View.GONE);
			mMoomViewLayout.setBackgroundColor(Color.TRANSPARENT);
			if (isEditMode) {
				mAddView.setVisibility(View.VISIBLE);
			} else {
				mAddView.setVisibility(View.GONE);
			}
		} else {
			mAddView.setVisibility(View.GONE);
			if (isEditMode) {
				mDeleteView.setVisibility(View.VISIBLE);
				mMoomViewLayout.setBackgroundResource(R.drawable.moomcelllayout_normal);
			} else {
				mDeleteView.setVisibility(View.GONE);
				mMoomViewLayout.setBackgroundColor(Color.TRANSPARENT);
			}
		}
	}

	private void setMoomViewImg(String moomViewConfig, String moomViewCHID,
			String moomViewTag) {
		if (!TextUtils.isEmpty(moomViewConfig)) {
			mMoomViewLayout.setVisibility(View.VISIBLE);
			mMoomImgView.setTag(moomViewTag);
			mMoomImgView.loadConfigure(getContext(), moomViewConfig);
		}
	}

	private void onMoomViewClicked() {
		if (isClickable()) {
			if (isInEditMode()) {
				onChooseMoomViewConfig();
			} else {
				switch (mMoomCellType) {
				case MoomCellData.MOOM_CELL_TYPE_ENTRANCE:
				{
					Intent intent = CustomMoomUIUtil.getIntentByMoomCHIDandMoomCellTag(getContext(), mMoomViewCHID, (String)getTag());
					getContext().startActivity(intent);
					if (useAnimation()) {
						CustomMoomUIUtil.startActivityAnimWithMoomCellTag((Activity)getContext(), (String)getTag(), true);
					}
					break;
				}
					
				case MoomCellData.MOOM_CELL_TYPE_SWITCH:
				{ 
					CustomMoomUIUtil.callFuncByMoomCHID(getContext(), mMoomViewCHID);
					break;
				}
					
				default:
					break;
				}
			}
		}
	}

	private void onCheckViewClicked() {
		setChecked(!isChecked());
	}

	private void onDeleteViewClicked() {
		/*
		 * 1- 调用activity的listener，通知它删除了moomview，需要注销频道 
		 * 2- 删除storage保存的信息 
		 * 3- 清除掉 moomImageView的tag 
		 * 4- 隐藏 moomview 
		 * 5- 清除有关moom config的三个数据 
		 * 6- 显示addview
		 */
		if (mSubscribeChangedListener != null
				&& !TextUtils.isEmpty(mMoomViewCHID)) {
			mSubscribeChangedListener.onUnsubscribe(mMoomViewCHID);
		}
		LocalStorage.getInstance().clearMoomCellData((String) getTag());
		mMoomImgView.setTag("");
		mMoomViewLayout.setVisibility(View.GONE);
		mMoomViewCHID = null;
		mMoomViewConfig = null;
		mDeleteView.setVisibility(View.GONE);
		mAddView.setVisibility(View.VISIBLE);
	}

	private void onAddViewClicked() {
		/*
		 * 1- 弹出选择对话框 不管是个list还是gridview 
		 * 2- 用户选中一个moomview后，获取到对应的 config chid和tag数据 
		 * 3- 设置并显示 moomview的相关数据 
		 * 4- 调用activity的listener，通知它新添加了moomview，需要注册频道
		 */
		if (!TextUtils.isEmpty((String) getTag())) {
			showMoomViewSelectedDialog(getContext(), (String) getTag());
		}
	}

	private void onChooseMoomViewConfig() {
		MoomViewGalleryActivity.startSelectMoom((Activity) getContext(),
				getActivityResultCode(), mMoomViewCHID, mMoomViewConfig, (String) getTag());
	}

	private void showMoomViewSelectedDialog(final Context context, String moomCellTag) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View layout =  inflater.inflate(R.layout.layout_moomview_choose_list, null);
    	mMoomChooseDlgListView = (ListView) layout.findViewById(R.id.listview);
    	mMoomChooseDlgAdapter = new MoomViewSelectListAdapter(context, CustomMoomUIUtil.getMoomCellChannelList());
    	mMoomChooseDlgAdapter.setUsedCHID(mUsedCHIDs);
    	mMoomChooseDlgListView.setAdapter(mMoomChooseDlgAdapter);
		
		// 这里来获取Main page上所有MoomCell注册的CHID
		// 用来在弹出的list dialog中标出用户已经选择了的
		// 目前方式需要多次读取文件，所以使用 AsyncTask
		SetAdapterUsedCHIDsTask task = new SetAdapterUsedCHIDsTask();
		task.execute();

		mMoomChooseDlgListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				MoomCellData mcd = CustomMoomUIUtil.getMoomCellChannelList().get(arg2);
				if (mcd != null) {
					setMoomViewData(mcd.mMoomViewCfg, mcd.mMoomCHID, mcd.mMoomViewTag, mcd.mCellType);
					saveMoomViewInfo();

					if (mSubscribeChangedListener != null
							&& !TextUtils.isEmpty(mMoomViewCHID)) {
						mSubscribeChangedListener.onSubscribe(mMoomViewCHID);
					}
				}
				mChooseDialog.dismiss();
				changeEditMode(isInEditMode());
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.moomview_choose_dialog_title)
				.setNegativeButton(R.string.alert_dialog_cancel, null)
				.setView(layout);
		mChooseDialog = builder.create();
		mChooseDialog.show();
	}
	
	private HashSet<String> getAllMoomCellLayouUsedCHIDs() {
		ArrayList<String> tags = CustomMoomUIUtil.getAllMoomCellTags();
		HashSet<String> chidSet = new HashSet<String>();
		for (String tag : tags) {
			MoomCellData temp = new MoomCellData();
			temp.mTag = tag;
			if (LocalStorage.getInstance().getMoomCellData(temp)) {
				chidSet.add(temp.mMoomCHID);
			}
		}
		return chidSet;
	}
	
	class SetAdapterUsedCHIDsTask extends AsyncTask<Void, Void, Void> {  
        @Override  
        protected void onPreExecute() {  
            super.onPreExecute();  
        }  
        
        @Override
        protected Void doInBackground(Void... arg0) {
        	if (mMoomChooseDlgAdapter != null) {
        		mUsedCHIDs = getAllMoomCellLayouUsedCHIDs();
        		mMoomChooseDlgAdapter.setUsedCHID(mUsedCHIDs);
        		
        		// 刷新 ListView
        		Message msg = new Message();   
        		msg.what = MSG_TYPE_REFRESH_CHOOSE_MOOM_DLG_LIST; 
                mHandler.sendMessage(msg);
        	}
        	return null;
        }
  
        @Override
        protected void onPostExecute(Void result) {
        	super.onPostExecute(result);
        }
    }  

	private class MoomViewSelectListAdapter extends BaseAdapter {
		private ArrayList<MoomCellData> mData = null;
		private LayoutInflater mInflater = null;
		private HashSet<String> mUsedCHIDSet = null;

		public MoomViewSelectListAdapter(Context context, ArrayList<MoomCellData> data) {
			super();
			mData = data;
			mInflater = LayoutInflater.from(context);
		}
		
		public void setUsedCHID(HashSet<String> usedChid) {
			mUsedCHIDSet = usedChid;
		}

		@Override
		public int getCount() {
			if (mData != null) {
				return mData.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			if (mData != null && arg0 >= 0 && arg0 < mData.size()) {
				return mData.get(arg0);
			}
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			if (mData != null && arg0 >= 0 && arg0 < mData.size()) {
				return arg0;
			}
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.layout_moomview_choose_listitem, null);
				holder = new ViewHolder();
				holder.layout = (MoomCellLayout) convertView.findViewById(R.id.moomcelllayout);
				holder.desc = (TextView) convertView.findViewById(R.id.text);
	            holder.selected = (CheckBox) convertView.findViewById(R.id.select);
	            holder.selected.setClickable(false);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.desc.setText(mData.get(position).mTitleResId);
			holder.layout.setMoomViewData(
					mData.get(position).mMoomViewCfg,
					mData.get(position).mMoomCHID,
					mData.get(position).mMoomViewTag,
					mData.get(position).mCellType);
			
			if (mUsedCHIDSet == null) {
				holder.selected.setChecked(false);
			} else {
				holder.selected.setChecked(mUsedCHIDSet.contains(mData.get(position).mMoomCHID));
			}

			return convertView;
		}
	}

	static class ViewHolder {
		MoomCellLayout layout;
		TextView desc;
        CheckBox selected;
	}
}