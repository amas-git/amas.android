package lab.whitetree.bonny.box.ui;

import org.whitetree.systable.system.SystemChangedEvent;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.ui.fragments.TaskListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import client.core.model.Event;

public class TaskList extends BaseActivity {

	private TaskListFragment mTaskListFragment = null;
	
	public static Intent getLaunchIntent(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, TaskList.class);
		return intent;
	}
	
	public static Intent getLaunchIntentWithMoomCellTag(Context context, String moomCellTag) {
		Intent intent = new Intent();
		intent.setClass(context, TaskList.class);
		intent.putExtra(INTENT_EXTRA_KEY_FROM_MOOMCELL_TAG, moomCellTag);
		return intent;
	}
	
	public static void startDefault(Context context) {
		context.startActivity(getLaunchIntent(context));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_2);

		startQuery();
		mTaskListFragment = (TaskListFragment) getSupportFragmentManager().findFragmentById(R.id.listfragment);
	}
	
	@Override
	protected void onEventInUiThread(Event event) {
		if (event instanceof SystemChangedEvent && mTaskListFragment != null) {
			mTaskListFragment.getSystemChangeEvent(TaskList.this, (SystemChangedEvent) event);
		}
	}
	
	public void startQuery() {
		LocalService.startForceQuery(this, new String[] {LocalService.CHID_PROC_RUNNING});
	}
}
