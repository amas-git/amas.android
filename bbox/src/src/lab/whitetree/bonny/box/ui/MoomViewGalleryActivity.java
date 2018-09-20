package lab.whitetree.bonny.box.ui;

import java.util.ArrayList;
import java.util.HashMap;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.storage.LocalStorage;
import lab.whitetree.bonny.box.ui.WidgetChooseTitleLayout.OnTitleSelectedListener;

import org.whitetree.bidget.moom.MoomMaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.LinearLayout;

public class MoomViewGalleryActivity extends BaseActivity {
	// 从何处跳转过来的
	public static final int VIEW_SOURCE_TYPE_NONE       = 0;
	public static final int VIEW_SOURCE_TYPE_MOOMCELL   = 1;
	public static final int VIEW_SOURCE_TYPE_1X1_WIDGET = 2;
	public static final int VIEW_SOURCE_TYPE_2X2_WIDGET = 3;
	
	public static final String EXTRA_KEY_SOURCE_TYPE      = "source_type";
	public static final String EXTRA_KEY_CHANNEL_ID       = "channel_id";
	public static final String EXTRA_KEY_MOOMCELL_TAG     = "moomcelllayout_tag";
	public static final String EXTRA_KEY_MOOM_CONFIG      = "moom_config";
	
	// 每一个chid 对应的可选config
	private ArrayList<String> mMoomMemory = new ArrayList<String>();
	private ArrayList<String> mMoomPower = new ArrayList<String>();
	private ArrayList<String> mMoomSdcard = new ArrayList<String>();
	private ArrayList<String> mMoomCpu = new ArrayList<String>();
	private ArrayList<String> mMoomApp = new ArrayList<String>();
	private ArrayList<String> mMoomNetwork = new ArrayList<String>();
	
	private HashMap<String, ArrayList<String>> mMoomAssests = new HashMap<String, ArrayList<String>>();
	
	private Gallery mGallery = null;
	protected int mViewSourceType = VIEW_SOURCE_TYPE_NONE;
	protected String mChannelID = null;
	protected String mMoomConfigFileUrl = null;
	private String mMoomCellTag = null;
	
	private LinearLayout mRootLayout = null;
	private WidgetChooseTitleLayout mTitleLayout = null;
	private MoomViewGalleryAdapter mAdpt = null;

	private boolean mDisplayMoomViewBg = true;

	public static Intent getLaunchIntent(Context context) {
		Intent intent = new Intent(context, MoomViewGalleryActivity.class);
		return intent;
	}

	// 从 MoomCellLayout 过来
	public static void startSelectMoom(Activity activity, int requestCode, String chid, String cfg, String moomCellLayoutTag) {
		Intent intent = getLaunchIntent(activity);
		intent.putExtra(EXTRA_KEY_SOURCE_TYPE, VIEW_SOURCE_TYPE_MOOMCELL);
		intent.putExtra(EXTRA_KEY_CHANNEL_ID, chid);
		intent.putExtra(EXTRA_KEY_MOOMCELL_TAG, moomCellLayoutTag);
		intent.putExtra(EXTRA_KEY_MOOM_CONFIG, cfg);
		activity.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moom_gallery);

		mViewSourceType = getIntent().getIntExtra(EXTRA_KEY_SOURCE_TYPE, VIEW_SOURCE_TYPE_NONE);
		mChannelID = getIntent().getStringExtra(EXTRA_KEY_CHANNEL_ID);
		mMoomCellTag = getIntent().getStringExtra(EXTRA_KEY_MOOMCELL_TAG);
		mMoomConfigFileUrl = getIntent().getStringExtra(EXTRA_KEY_MOOM_CONFIG);

		initMoomCfgData();

		mRootLayout = (LinearLayout) findViewById(R.id.title_layout);
		mTitleLayout = (WidgetChooseTitleLayout) findViewById(R.id.widget_title_layout);

		mAdpt = new MoomViewGalleryAdapter(this, mMoomAssests.get(mChannelID));
		mGallery = (Gallery) findViewById(R.id.gallery);
		mGallery.setAdapter(mAdpt);

		if (mViewSourceType == VIEW_SOURCE_TYPE_MOOMCELL) {
			findViewById(R.id.root_layout).setBackgroundResource(R.drawable.activity_bg);
			displayMoomViewBg(true);
			mRootLayout.setVisibility(View.GONE);
			mTitleLayout.setVisibility(View.GONE);

			setGalleryType(mChannelID);
		} 
		else if (mViewSourceType == VIEW_SOURCE_TYPE_1X1_WIDGET || mViewSourceType == VIEW_SOURCE_TYPE_2X2_WIDGET) {
			findViewById(R.id.root_layout).setBackgroundColor(Color.TRANSPARENT);
			displayMoomViewBg(false);
			mRootLayout.setVisibility(View.VISIBLE);
			mTitleLayout.setVisibility(View.VISIBLE);
			mTitleLayout.setOnTitleSelectedListener(new OnTitleSelectedListener() {
				@Override
				public void onSelected(int titleIndex, String chid) {
					setGalleryType(chid);
				}
			});
			mTitleLayout.setSelectedTitleIndex(WidgetChooseTitleLayout.TITLE_INDEX_MEM);
		}

		int selected = mAdpt.getItem(mMoomConfigFileUrl);
		if (selected > 0) {
			mGallery.setSelection(selected);
		}

		mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
				mMoomConfigFileUrl = (String) parent.getAdapter().getItem(position);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	private void displayMoomViewBg(boolean isDisplay) {
		mDisplayMoomViewBg = isDisplay;
		if (mAdpt != null) {
			mAdpt.displayMoomBackground(mDisplayMoomViewBg);
		}
	}
	private void setResult() {
		Intent data = new Intent();
		data.putExtra(EXTRA_KEY_MOOM_CONFIG, mMoomConfigFileUrl);
		data.putExtra(EXTRA_KEY_MOOMCELL_TAG, mMoomCellTag);
		setResult(RESULT_OK, data);
	}

	public void onClickSave(View view) {
		if (mViewSourceType == VIEW_SOURCE_TYPE_MOOMCELL) {
			// 不需要保存，交给MoomCell去保存
			setResult();
			finish();
			overridePendingTransition(R.anim.in_from_inside, R.anim.out_to_inside);
		} 
		else if (mViewSourceType == VIEW_SOURCE_TYPE_1X1_WIDGET || mViewSourceType == VIEW_SOURCE_TYPE_2X2_WIDGET) {
			String[] chids = new String[]{mChannelID};
			// 注册widget需要的频道
			LocalService.startSubscribe(this, chids, LocalService.SERVICE_REFRESH_INTERVAL_MAX);
			// 保存频道id 
			LocalStorage.getInstance().setWidgetsSubscribeChids(chids, true);
			LocalService.startTouch(this);
		}
		recycle();
	}

	private void setGalleryType(String chid) {
		if (TextUtils.isEmpty(chid)) {
			return;
		}
		mChannelID = chid;

		mAdpt = new MoomViewGalleryAdapter(this, mMoomAssests.get(mChannelID));
		mAdpt.displayMoomBackground(mDisplayMoomViewBg);
		mGallery = (Gallery) findViewById(R.id.gallery);
		mGallery.setAdapter(mAdpt);

		int selected = mAdpt.getItem(mMoomConfigFileUrl);
		if (selected > 0) {
			mGallery.setSelection(selected);
		}
	}

	private void recycle() {
		// 删除不需要的Moom配置
		ArrayList<String> moomUrls = mMoomAssests.get(mChannelID);
		for (String x : moomUrls) {
			if (!TextUtils.isEmpty(mMoomConfigFileUrl) && !mMoomConfigFileUrl.equals(x)) {
				MoomMaster.getInstance().remove(x);
			}
		}
		System.gc();
	}

	private void initMoomCfgData() {
		mMoomAssests.clear();
		mMoomAssests.put(LocalService.CHID_MEMORY, mMoomMemory);
		mMoomAssests.put(LocalService.CHID_POWER, mMoomPower);
		mMoomAssests.put(LocalService.CHID_STORAGE_SDCARD, mMoomSdcard);
		mMoomAssests.put(LocalService.CHID_CPU, mMoomCpu);
		mMoomAssests.put(LocalService.CHID_APPS, mMoomApp);
		mMoomAssests.put(LocalService.CHID_NETWORKS, mMoomNetwork);
		
		mMoomMemory.clear();
		mMoomPower.clear();
		mMoomSdcard.clear();
		mMoomCpu.clear(); 
		mMoomApp.clear(); 
		mMoomNetwork.clear();
		
		switch(mViewSourceType) {
		case VIEW_SOURCE_TYPE_1X1_WIDGET:
			// 添加专为 1X1_WIDGET 选择用的
			mMoomMemory.add("res:///moom_1x1_widget_mem_1");
			mMoomMemory.add("res:///moom_1x1_widget_mem_2");
			mMoomMemory.add("res:///moom_1x1_widget_mem_3");
			mMoomMemory.add("res:///moom_1x1_widget_mem_4");

			mMoomPower.add("res:///moom_1x1_widget_power_1");
			mMoomPower.add("res:///moom_1x1_widget_power_2");
			mMoomPower.add("res:///moom_1x1_widget_power_3");
			mMoomPower.add("res:///moom_1x1_widget_power_4");

			mMoomSdcard.add("res:///moom_1x1_widget_sdcard_1");
			mMoomSdcard.add("res:///moom_1x1_widget_sdcard_2");
			mMoomSdcard.add("res:///moom_1x1_widget_sdcard_3");
			mMoomSdcard.add("res:///moom_1x1_widget_sdcard_4");

			mMoomCpu.add("res:///moom_1x1_widget_cpu_1");
			mMoomCpu.add("res:///moom_1x1_widget_cpu_2");
			mMoomCpu.add("res:///moom_1x1_widget_cpu_3");
			mMoomCpu.add("res:///moom_1x1_widget_cpu_4");

			// 1x1 widget 不需要 app仪表
			mMoomAssests.remove(LocalService.CHID_APPS);
			
			// TODO 增加 Network 的 cfg
			
			break;
			
		case VIEW_SOURCE_TYPE_2X2_WIDGET:
			// 添加专为 2X2_WIDGET 选择用的
			mMoomMemory.add("res:///moom_200_200_mem_1");
			mMoomMemory.add("res:///moom_200_200_mem_2");
			mMoomMemory.add("res:///moom_200_200_mem_8");
			mMoomMemory.add("res:///moom_200_200_mem_3");
			mMoomMemory.add("res:///moom_200_200_mem_9");
			mMoomMemory.add("res:///moom_200_200_mem_4");
			mMoomMemory.add("res:///moom_200_200_mem_5");
			mMoomMemory.add("res:///moom_200_200_mem_6");
			mMoomMemory.add("res:///moom_200_200_mem_7");
			mMoomMemory.add("res:///moom_default_mem_1");

			mMoomPower.add("res:///moom_200_200_power_2");
			mMoomPower.add("res:///moom_200_200_power_1");
			mMoomPower.add("res:///moom_default_battery_1");
			mMoomPower.add("res:///moom_200_200_power_6");
			mMoomPower.add("res:///moom_200_200_power_7");
			mMoomPower.add("res:///moom_200_200_power_5");
			mMoomPower.add("res:///moom_200_200_power_8");
			mMoomPower.add("res:///moom_200_200_power_3");
			mMoomPower.add("res:///moom_200_200_power_9");
			mMoomPower.add("res:///moom_200_200_power_4");

			mMoomSdcard.add("res:///moom_200_200_sdcard_3");
			mMoomSdcard.add("res:///moom_default_sdcard_1");
			mMoomSdcard.add("res:///moom_200_200_sdcard_4");
			mMoomSdcard.add("res:///moom_200_200_sdcard_9");
			mMoomSdcard.add("res:///moom_200_200_sdcard_8");
			mMoomSdcard.add("res:///moom_200_200_sdcard_5");
			mMoomSdcard.add("res:///moom_200_200_sdcard_7");
			mMoomSdcard.add("res:///moom_200_200_sdcard_2");
			mMoomSdcard.add("res:///moom_200_200_sdcard_6");
			mMoomSdcard.add("res:///moom_200_200_sdcard_1");

			mMoomCpu.add("res:///moom_200_200_cpu_4");
			mMoomCpu.add("res:///moom_200_200_cpu_3");
			mMoomCpu.add("res:///moom_200_200_cpu_2");
			mMoomCpu.add("res:///moom_200_200_cpu_1");
			mMoomCpu.add("res:///moom_default_cpu_1");
			mMoomCpu.add("res:///moom_200_200_cpu_5");
			mMoomCpu.add("res:///moom_200_200_cpu_7");
			mMoomCpu.add("res:///moom_200_200_cpu_6");
			mMoomCpu.add("res:///moom_200_200_cpu_9");
			mMoomCpu.add("res:///moom_200_200_cpu_8");

			// 2x2 widget 不需要 app 仪表
			mMoomAssests.remove(LocalService.CHID_APPS);

			mMoomNetwork.add("res:///moom_networks");
			break;
			
		case VIEW_SOURCE_TYPE_MOOMCELL:
			// 添加专为 MoomCellLayout 选择用的
			mMoomMemory.add("res:///moom_200_200_mem_1");
			mMoomMemory.add("res:///moom_200_200_mem_2");
			mMoomMemory.add("res:///moom_200_200_mem_8");
			mMoomMemory.add("res:///moom_200_200_mem_3");
			mMoomMemory.add("res:///moom_200_200_mem_9");
			mMoomMemory.add("res:///moom_200_200_mem_4");
			mMoomMemory.add("res:///moom_200_200_mem_5");
			mMoomMemory.add("res:///moom_200_200_mem_6");
			mMoomMemory.add("res:///moom_200_200_mem_7");
			mMoomMemory.add("res:///moom_default_mem_1");

			mMoomPower.add("res:///moom_200_200_power_2");
			mMoomPower.add("res:///moom_200_200_power_1");
			mMoomPower.add("res:///moom_default_battery_1");
			mMoomPower.add("res:///moom_200_200_power_6");
			mMoomPower.add("res:///moom_200_200_power_7");
			mMoomPower.add("res:///moom_200_200_power_5");
			mMoomPower.add("res:///moom_200_200_power_8");
			mMoomPower.add("res:///moom_200_200_power_3");
			mMoomPower.add("res:///moom_200_200_power_9");
			mMoomPower.add("res:///moom_200_200_power_4");

			mMoomSdcard.add("res:///moom_200_200_sdcard_3");
			mMoomSdcard.add("res:///moom_default_sdcard_1");
			mMoomSdcard.add("res:///moom_200_200_sdcard_4");
			mMoomSdcard.add("res:///moom_200_200_sdcard_9");
			mMoomSdcard.add("res:///moom_200_200_sdcard_8");
			mMoomSdcard.add("res:///moom_200_200_sdcard_5");
			mMoomSdcard.add("res:///moom_200_200_sdcard_7");
			mMoomSdcard.add("res:///moom_200_200_sdcard_2");
			mMoomSdcard.add("res:///moom_200_200_sdcard_6");
			mMoomSdcard.add("res:///moom_200_200_sdcard_1");

			mMoomCpu.add("res:///moom_200_200_cpu_4");
			mMoomCpu.add("res:///moom_200_200_cpu_3");
			mMoomCpu.add("res:///moom_200_200_cpu_2");
			mMoomCpu.add("res:///moom_200_200_cpu_1");
			mMoomCpu.add("res:///moom_default_cpu_1");
			mMoomCpu.add("res:///moom_200_200_cpu_5");
			mMoomCpu.add("res:///moom_200_200_cpu_7");
			mMoomCpu.add("res:///moom_200_200_cpu_6");
			mMoomCpu.add("res:///moom_200_200_cpu_9");
			mMoomCpu.add("res:///moom_200_200_cpu_8");

			mMoomApp.add("res:///moom_userapps");

			mMoomNetwork.add("res:///moom_networks");
			break;
			
			
		default:
			break;
		}
	}
}
