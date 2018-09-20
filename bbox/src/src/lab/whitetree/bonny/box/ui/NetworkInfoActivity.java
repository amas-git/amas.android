package lab.whitetree.bonny.box.ui;

import org.whitetree.systable.system.SystemChangedEvent;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.ui.fragments.NetworkInfoFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NetworkInfoActivity extends BaseActivity {
	private NetworkInfoFragment mNetworkInfoFragment = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_networkinfo);
		mNetworkInfoFragment = (NetworkInfoFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_networkinfo);
	}

	public static Intent getLaunchIntent(Context context) {
		Intent intent = new Intent(context, NetworkInfoActivity.class);
		return intent;
	}
	
	@Override
	protected void onSystemChange(SystemChangedEvent event) {
		if(mNetworkInfoFragment!=null) {
			mNetworkInfoFragment.onSystemChanged(event);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LocalService.startQuery(this, new String[]{LocalService.CHID_NETWORKS});
	}

	public static Intent getLaunchIntentWithMoomCellTag(Context context, String moomCellTag) {
		Intent intent = new Intent();
		intent.setClass(context, NetworkInfoActivity.class);
		intent.putExtra(INTENT_EXTRA_KEY_FROM_MOOMCELL_TAG, moomCellTag);
		return intent;
	}
	
}
