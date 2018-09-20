package lab.whitetree.bonny.box.ui;

import org.whitetree.systable.system.SystemChangedEvent;
import org.whitetree.systable.system.watcher.PowerWatcher;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.ui.fragments.BatteryFragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

public class BatteryActivity extends BaseActivity implements SensorEventListener {

	private SensorManager mSensorManager = null;
	private BatteryFragment mBatteryFragment = null;
	
	public static Intent getLaunchIntent(Context context) {
		Intent intent = new Intent(context, BatteryActivity.class);
		return intent;
	}
	
	public static Intent getLaunchIntentWithMoomCellTag(Context context, String moomCellTag) {
		Intent intent = new Intent();
		intent.setClass(context, BatteryActivity.class);
		intent.putExtra(INTENT_EXTRA_KEY_FROM_MOOMCELL_TAG, moomCellTag);
		return intent;
	}

	public static void startDefault(Context context) {
		context.startActivity(getLaunchIntent(context));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery);

		mBatteryFragment = (BatteryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_battery);
	}

	@Override
	protected void onSystemChange(SystemChangedEvent event) {
		super.onSystemChange(event);
		if (mBatteryFragment != null) {
			mBatteryFragment.getSystemChangeEvent(BatteryActivity.this, event);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		LocalService.startQuery(this, new String[] { PowerWatcher.CHID_POWER });
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(this);
		LocalService.startUnsubscribe(this, new String[] {LocalService.CHID_POWER});
		super.onStop();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int arg1) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (mBatteryFragment != null) {
			mBatteryFragment.getSensorChangedEvent(BatteryActivity.this, event);
		}
	}

	public void onClickOverheat(View _) {
		if (mBatteryFragment != null) {
			mBatteryFragment.onClickOverheat(_);
		}
	}
	
	public void onClickOvervoltage(View _) {
		if (mBatteryFragment != null) {
			mBatteryFragment.onClickOvervoltage(_);
		}
	}
	
	public void onClickDead(View _) {
		if (mBatteryFragment != null) {
			mBatteryFragment.onClickDead(_);
		}
	}
	
	public void onClickUnknown(View _) {
		if (mBatteryFragment != null) {
			mBatteryFragment.onClickUnknown(_);
		}
	}
	
	public void onClickGood(View _) {
		if (mBatteryFragment != null) {
			mBatteryFragment.onClickGood(_);
		}
	}
	
	public void onClickAc(View _) {
		if (mBatteryFragment != null) {
			mBatteryFragment.onClickAc(_);
		}
	}
	
	public void onClickUsb(View _) {
		if (mBatteryFragment != null) {
			mBatteryFragment.onClickUsb(_);
		}
	}
	
	public void onClickAcUsb(View _) {
		if (mBatteryFragment != null) {
			mBatteryFragment.onClickAcUsb(_);
		}
	}
	
	public void onClickUnplugged(View _) {
		if (mBatteryFragment != null) {
			mBatteryFragment.onClickUnplugged(_);
		}
	}
	
	public void onClick(View view) {
		if (mBatteryFragment != null) {
			mBatteryFragment.onClick(view);
		}
	}
}