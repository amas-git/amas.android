package lab.whitetree.bonny.box.ui;

import org.whitetree.systable.system.SystemChangedEvent;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.ui.fragments.DiskFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DiskActivity extends BaseActivity {
	
	private DiskFragment mDiskFragment = null;
	
	public static Intent getLaunchIntent(Context context) {
		Intent intent = new Intent(context, DiskActivity.class);
		return intent;
	}
	
	public static Intent getLaunchIntentWithMoomCellTag(Context context, String moomCellTag) {
		Intent intent = new Intent();
		intent.setClass(context, DiskActivity.class);
		intent.putExtra(INTENT_EXTRA_KEY_FROM_MOOMCELL_TAG, moomCellTag);
		return intent;
	}

	public static void startDefault(Context context) {
		context.startActivity(getLaunchIntent(context));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disk);

		mDiskFragment = (DiskFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_disk);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LocalService.startSubscribe(this, LocalService.CHID_GROUP_FS);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LocalService.startUnsubscribe(this, LocalService.CHID_GROUP_FS);
	}

	@Override
	protected void onSystemChange(SystemChangedEvent event) {
		if (mDiskFragment != null) {
			mDiskFragment.getSystemChangeEvent(DiskActivity.this, event);
		}
	}

	// for btn onClick actions
	public void onClickSdcard(View view) {
		if (mDiskFragment != null) {
			mDiskFragment.onClickSdcard(view);
		}
	}
	
	public void onClickExtSdcard(View view) {
		if (mDiskFragment != null) {
			mDiskFragment.onClickExtSdcard(view);
		}
	}

	public void onClickData(View view) {
		if (mDiskFragment != null) {
			mDiskFragment.onClickData(view);
		}
	}

	public void onClickSystem(View view) {
		if (mDiskFragment != null) {
			mDiskFragment.onClickSystem(view);
		}
	}
}
