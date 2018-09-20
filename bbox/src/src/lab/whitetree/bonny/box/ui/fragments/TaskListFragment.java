package lab.whitetree.bonny.box.ui.fragments;

import java.util.ArrayList;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.ui.KillerTask;
import lab.whitetree.bonny.box.ui.RainbowButton;
import lab.whitetree.bonny.box.ui.TaskListAdapter;

import org.whitetree.systable.data.Application;
import org.whitetree.systable.system.SystemChangedEvent;
import org.whitetree.systable.system.watcher.RunningProcessWatcher;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class TaskListFragment extends ListFragment {
	
	private RainbowButton mBtnKillAll = null;
	private TaskListAdapter mAdpt = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (inflater == null) {
			return null;
		}
	
		View root = inflater.inflate(R.layout.fragment_tasklist_layout, container, true);
		final Context context = root.getContext();
		
		mBtnKillAll = (RainbowButton) root.findViewById(R.id.killall);
		mBtnKillAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onStartKillAll(context);
			}
		});
		
		return root;
	}
	
	public void getSystemChangeEvent(Context context, SystemChangedEvent event) {
		if (event == null) {
			return;
		}
		String tag = event.id;
		if(LocalService.CHID_PROC_RUNNING.equals(tag)) {
			ArrayList<Application> data = RunningProcessWatcher.getRunningApplications(event.walue);
			mAdpt = new TaskListAdapter(context, data);
			setListAdapter(mAdpt);
		}
	}

	protected void onStartKillAll(final Context context) {
		if (mAdpt == null) {
			return;
		}
		ArrayList<Application> targets = mAdpt.getAll();
		if(targets == null || targets.isEmpty()) {
			return;
		}
		
		new KillerTask(context, targets) {
			@Override
			protected void onPreExecute() {
				mBtnKillAll.setProgress(0);
				mBtnKillAll.showProgress();
			}
			
			@Override
			protected boolean onPreKill(Application target) {
				return !target.packageName.equals(context.getPackageName());
			}
			
			@Override
			protected void onPostKill(Application app, int percent, int i) {
				mBtnKillAll.setProgress(percent);
				mAdpt.remove(app, true);
			}
			
			@Override
			protected void onPostExecute(Integer parsedText) {
				//U.suicide(this);
				mBtnKillAll.dissmiss();
			}
		}.execute();
	}
}
