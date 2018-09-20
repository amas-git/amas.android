package lab.whitetree.bonny.box.ui;

import org.whitetree.systable.system.SystemChangedEvent;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.ui.fragments.UninstallerFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import client.core.model.Event;

public class UninstallerActivity extends BaseActivity {
	
	private UninstallerFragment mUninstallerFragment = null;
	
	public static Intent getLaunchIntent(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, UninstallerActivity.class);
		return intent;
	}
	
	public static Intent getLaunchIntentWithMoomCellTag(Context context, String moomCellTag) {
		Intent intent = new Intent();
		intent.setClass(context, UninstallerActivity.class);
		intent.putExtra(INTENT_EXTRA_KEY_FROM_MOOMCELL_TAG, moomCellTag);
		return intent;
	}
	
	public static void startDefault(Context context) {
		context.startActivity(getLaunchIntent(context));
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apps_activity);
        
        mUninstallerFragment = (UninstallerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_apps);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		LocalService.startQuery(this,  new String[] {LocalService.CHID_APPS});
	}
	
	@Override
	protected void onEventInUiThread(Event event) {
		super.onEventInUiThread(event);

		if (mUninstallerFragment != null) {
			mUninstallerFragment.getEvent(UninstallerActivity.this, event);
		}
	}
	
	@Override
	protected void onSystemChange(SystemChangedEvent event) {
		super.onSystemChange(event);

		if (mUninstallerFragment != null) {
			mUninstallerFragment.getSystemChangeEvent(UninstallerActivity.this, event);
		}
	}
    
    @Override
    public void onBackPressed() {
    	if (mUninstallerFragment != null && mUninstallerFragment.getSelectedAppCount() > 0) {
    		mUninstallerFragment.unselectAll();
    		return;
    	}
    	super.onBackPressed();
    }
}