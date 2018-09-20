package lab.whitetree.bonny.box;

import java.util.ArrayList;

import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.storage.LocalStorage;
import lab.whitetree.bonny.box.ui.BaseActivity;
import lab.whitetree.bonny.box.ui.Hell;
import lab.whitetree.bonny.box.ui.MoomCellLayout;
import lab.whitetree.bonny.box.ui.MoomViewGalleryActivity;
import lab.whitetree.bonny.box.ui.SettingActivity;
import lab.whitetree.bonny.box.util.CustomMoomUIUtil;

import org.whitetree.systable.system.SystemChangedEvent;
import org.whitetree.systable.system.U;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

public class Main extends BaseActivity implements MoomCellLayout.onSubscribeChangedListener {

	private static final String UMENG_CONFIG_KEY_VERSION_CODE = "version_code";
	public static final int REQUEST_SELECT_MOOM = 1527;

	private ImageView mUpdateBtn = null;
	private ImageView mSettingBtn = null;
	private ImageView mEditModeBtn = null;

	private boolean mIsInEditMode = false;

	private ArrayList<MoomCellLayout> mMoomCellList = null;

	private ArrayList<String> mSubscribeChIDs = new ArrayList<String>();

	public static Intent getLaunchIntent(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, Main.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_1);
		init();

		// 弹 评分 dialog
		//U.checkRateUsPrompt(Main.this);

		// 显示升级提示
		checkAppUpdateInfo();

		loadDefaultUISettings();
	}

	// ------------------------------------------------------------------------------[
	// onClick 
	public void gotoHell(View _) {
		Hell.startDefault(this);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void onClickSetting(View view) {
		SettingActivity.startDefault(this);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	private void startOnekeyTask() {
		// TODO:
		return;
	}

	public void onClickEditMode(View view) {
		mIsInEditMode = !mIsInEditMode;
		for (MoomCellLayout mcl : mMoomCellList) {
			if (mcl != null) {
				mcl.setInEditMode(mIsInEditMode);
			}
		}
		if (mIsInEditMode) {
			mSettingBtn.setVisibility(View.GONE);
			if (mUpdateBtn != null) {
				mUpdateBtn.setVisibility(View.GONE);
			}
		} else {
			mSettingBtn.setVisibility(View.VISIBLE);
			if (mUpdateBtn != null) {
				mUpdateBtn.setVisibility(View.VISIBLE);
			}
		}
	}

	private void init() {
		mSettingBtn = (ImageView) findViewById(R.id.btn_setting);
		mEditModeBtn = (ImageView) findViewById(R.id.btn_edit);

		// MoomCellLayout
		mMoomCellList = new ArrayList<MoomCellLayout>();
		mMoomCellList.add((MoomCellLayout) findViewById(R.id.moom_cell_layout_0));
		mMoomCellList.add((MoomCellLayout) findViewById(R.id.moom_cell_layout_1));
		mMoomCellList.add((MoomCellLayout) findViewById(R.id.moom_cell_layout_2));
		mMoomCellList.add((MoomCellLayout) findViewById(R.id.moom_cell_layout_3));
		mMoomCellList.add((MoomCellLayout) findViewById(R.id.moom_cell_layout_4));
		mMoomCellList.add((MoomCellLayout) findViewById(R.id.moom_cell_layout_5));
		mMoomCellList.add((MoomCellLayout) findViewById(R.id.moom_cell_layout_6));

		// 需要在注册listener之后，再让每个MoomCellLayout loadMoomData， 这样才能在Activity中注册频道
		for (MoomCellLayout mcl : mMoomCellList) {
			if (mcl != null) {
				mcl.setOnSubscribeChangedListener(this);
				mcl.setActivityResultCode(REQUEST_SELECT_MOOM);
				mcl.setUseAnimation(true);
			}
		}
	}

	@Override
	protected void onSystemChange(SystemChangedEvent event) {
		System.err.println("你妹");
		final SystemChangedEvent se = (SystemChangedEvent) event;
		LOG._("[MAIN]: " + event);
		String tag = se.id;
		if (!TextUtils.isEmpty(tag)) {
			for (MoomCellLayout mcl : mMoomCellList) {
				if (mcl != null) {
					mcl.refreshMoomView(tag);
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// NOTICE onResume时不需要 startSubscribe
		// 因为所有MoomCell重新loadData时，会由Listener上报需注册的频道
		for (MoomCellLayout mcl : mMoomCellList) {
			if (mcl != null) {
				mcl.loadMoomData();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocalService.startUnsubscribe(this, (String[]) mSubscribeChIDs.toArray(new String[mSubscribeChIDs.size()]));
		mSubscribeChIDs.clear();
	}

	@Override
	public void onBackPressed() {
		if (mIsInEditMode) {
			onClickEditMode(null);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_SELECT_MOOM:
				// MoomCellLayout 选择config，从MoomViewGalleryActivity获取回复
				if (data != null && data.hasExtra(MoomViewGalleryActivity.EXTRA_KEY_MOOM_CONFIG)
				        && data.hasExtra(MoomViewGalleryActivity.EXTRA_KEY_MOOMCELL_TAG)) {

					String tag = data.getStringExtra(MoomViewGalleryActivity.EXTRA_KEY_MOOMCELL_TAG);
					String cfg = data.getStringExtra(MoomViewGalleryActivity.EXTRA_KEY_MOOM_CONFIG);

					for (MoomCellLayout mcl : mMoomCellList) {
						if (!TextUtils.isEmpty((String) mcl.getTag()) && ((String) mcl.getTag()).equals(tag)) {
							mcl.setMoomViewData(cfg, mcl.getMoomViewCHID(), mcl.getMoomViewTag(), mcl.getCellViewType());
						}
					}
				}
				break;
			default:
				break;
			}
		}
	}

	private void checkAppUpdateInfo() {
		MobclickAgent.updateOnlineConfig(Main.this);
		// 获取VersionCode，如果比当前应用的VersionCode大，就提示用户更新版本
		int umVerCode = 0;
		try {
			umVerCode = Integer.valueOf(MobclickAgent.getConfigParams(Main.this, UMENG_CONFIG_KEY_VERSION_CODE)).intValue();
		} catch (Exception e) {
		}

		if (umVerCode > U.getVersionCode(Main.this)) {

			// 需要提示用户有更新
			mUpdateBtn = new ImageView(Main.this);
			mUpdateBtn.setImageResource(R.drawable.update);
			mUpdateBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					//U.showAppUpdateDialog(Main.this);
				}
			});
			int padding = U.getPixByDip(10);
			mUpdateBtn.setPadding(padding, padding, padding, padding);

			int size = U.getPixByDip(60);
			WindowManager windowManager = (WindowManager) Main.this.getSystemService(Context.WINDOW_SERVICE);
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams(size, size, WindowManager.LayoutParams.TYPE_APPLICATION,
			        WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING, PixelFormat.TRANSLUCENT);
			lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
			lp.gravity = Gravity.LEFT | Gravity.BOTTOM;

			windowManager.addView(mUpdateBtn, lp);
		} else {
			mUpdateBtn = null;
		}
	}

	@Override
	public void onSubscribe(String chid) {
		if (!TextUtils.isEmpty(chid)) {
			mSubscribeChIDs.add(chid);
			LocalService.startSubscribe(this, new String[] { chid });
		}
	}

	@Override
	public void onUnsubscribe(String chid) {
		if (!TextUtils.isEmpty(chid) && mSubscribeChIDs.contains(chid)) {
			LocalService.startUnsubscribe(this, new String[] { chid });
			mSubscribeChIDs.remove(chid);
		}
	}

	private void loadDefaultUISettings() {
		// 如果是第一次进来，就使用默认的UI setting
		if (LocalStorage.getInstance().isFirstEnteredMainActivity()) {
			for (MoomCellLayout mcl : mMoomCellList) {
				if (mcl != null) {
					if (CustomMoomUIUtil.MAIN_ACTIVITY_MOOM_CELL_TAG_0.equals((String) mcl.getTag())) {
						mcl.setMoomViewData("res:///moom_200_200_mem_7", LocalService.CHID_MEMORY, LocalService.CHID_MEMORY);
					} else if (CustomMoomUIUtil.MAIN_ACTIVITY_MOOM_CELL_TAG_1.equals((String) mcl.getTag())) {
						mcl.setMoomViewData("res:///moom_default_cpu_1", LocalService.CHID_CPU, LocalService.CHID_CPU);
					} else if (CustomMoomUIUtil.MAIN_ACTIVITY_MOOM_CELL_TAG_2.equals((String) mcl.getTag())) {
						mcl.setMoomViewData("res:///moom_default_sdcard_1", LocalService.CHID_STORAGE_SDCARD, LocalService.CHID_STORAGE_SDCARD);
					} else if (CustomMoomUIUtil.MAIN_ACTIVITY_MOOM_CELL_TAG_3.equals((String) mcl.getTag())) {
						mcl.setMoomViewData("res:///moom_default_battery_1", LocalService.CHID_POWER, LocalService.CHID_POWER);
					} else if (CustomMoomUIUtil.MAIN_ACTIVITY_MOOM_CELL_TAG_4.equals((String) mcl.getTag())) {
						mcl.setMoomViewData(null, null, null);
					} else if (CustomMoomUIUtil.MAIN_ACTIVITY_MOOM_CELL_TAG_5.equals((String) mcl.getTag())) {
						mcl.setMoomViewData("res:///moom_userapps", LocalService.CHID_APPS, LocalService.CHID_APPS);
					} else if (CustomMoomUIUtil.MAIN_ACTIVITY_MOOM_CELL_TAG_6.equals((String) mcl.getTag())) {
						mcl.setMoomViewData("res:///moom_networks", LocalService.CHID_NETWORKS, LocalService.CHID_NETWORKS);
					}
				}
			}

			LocalStorage.getInstance().setFirstEnteredMainActivityFlag(false);
		}
	}
}
