package lab.whitetree.bonny.box.ui;

import org.whitetree.systable.system.SystemChangedEvent;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.ui.fragments.CpuFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class CpuActivity extends BaseActivity {
	
	private CpuFragment mCpuFragment = null;
	
	public static Intent getLaunchIntent(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, CpuActivity.class);
		return intent;
	}
	
	public static Intent getLaunchIntentWithMoomCellTag(Context context, String moomCellTag) {
		Intent intent = new Intent();
		intent.setClass(context, CpuActivity.class);
		intent.putExtra(INTENT_EXTRA_KEY_FROM_MOOMCELL_TAG, moomCellTag);
		return intent;
	}
	
	public static void startDefault(Context context) {
		context.startActivity(getLaunchIntent(context));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_cpu);
	    
		mCpuFragment = (CpuFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_cpu);
	}
	
	@Override
	protected void onSystemChange(SystemChangedEvent event) {
		super.onSystemChange(event);
		if (mCpuFragment != null) {
			mCpuFragment.getSystemChangeEvent(CpuActivity.this, event);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocalService.startSubscribe(this, new String[] {LocalService.CHID_SENSOR_TEMP, LocalService.CHID_CPU});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LocalService.startUnsubscribe(this, new String[] {LocalService.CHID_SENSOR_TEMP, LocalService.CHID_CPU});
	}
}
