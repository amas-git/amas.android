package lab.whitetree.bonny.box.ui;

import org.whitetree.systable.system.SystemChangedEvent;

import lab.whitetree.bonny.box.DEV;
import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.util.CustomMoomUIUtil;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import client.core.model.Event;
import client.ui.BaseCloudFragmentActivity;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends BaseCloudFragmentActivity {

	private static final String ADMOB_APP_UNIT_ID = "a14fc87f3799fa5";

	protected static final String INTENT_EXTRA_KEY_FROM_MOOMCELL_TAG = "from_moomcell_tag";
	private String mFromMoomCellTag = null; //记录从哪个MoomCell跳转过来的

	private LayoutInflater mInflater = null;
	private ViewGroup mRootView = null;
	private AdView mAdView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = (ViewGroup) mInflater.inflate(R.layout.base_activity, null);
		if (DEV.ENABLE_ADMOB) {
			initAdMob();
		}

		mFromMoomCellTag = getIntent().getStringExtra(INTENT_EXTRA_KEY_FROM_MOOMCELL_TAG);
	}

	@Override
	public void setContentView(int layoutResID) {
		FrameLayout container = (FrameLayout) mRootView.findViewById(R.id.frame_content_container);
		View contentView = mInflater.inflate(layoutResID, container, false);
		container.addView(contentView);
		super.setContentView(mRootView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (DEV.ENABLE_ADMOB) {
			try {
				// admob
				requestAd();
				// umeng
				MobclickAgent.onResume(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		if (DEV.ENABLE_UMENG) {
			try {
				// uemng
				MobclickAgent.onPause(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void initAdMob() {
		// Lookup R.layout.main
		LinearLayout layout = (LinearLayout) mRootView.findViewById(R.id.ad_layout);

		// Create the adView
		// Please replace ADMOB_APP_UNIT_ID with your AdMob Publisher ID
		mAdView = new AdView(this, AdSize.BANNER, ADMOB_APP_UNIT_ID);

		// Add the adView to it
		layout.addView(mAdView);
	}

	@SuppressWarnings("deprecation")
	private void requestAd() {
		// Initiate a generic request to load it with an ad
		AdRequest request = new AdRequest();

		request.setTesting(false);
		request.setBirthday("19800101");
		// request.setExtras(extras);
		// request.setGender(AdRequest.Gender.FEMALE);
		// request.setKeywords(keywords);
		// request.setLocation(location);
		// request.setTestDevices(testDevices);

		mAdView.loadAd(request);
	}

	@Override
	protected void onEventInUiThread(Event event) {
		if (event instanceof SystemChangedEvent) {
			onSystemChange((SystemChangedEvent) event);
		}
	}

	protected void onSystemChange(SystemChangedEvent event) {
	}

	@Override
	public void onBackPressed() {
		// 在退出时，根据不同MoomCell来源，调用动画效果
		super.onBackPressed();
		if (!TextUtils.isEmpty(mFromMoomCellTag)) {
			CustomMoomUIUtil.startActivityAnimWithMoomCellTag((Activity) this, mFromMoomCellTag, false);
		}
	}
}
