package lab.whitetree.bonny.box.ui.fragments;

import java.util.ArrayList;
import java.util.HashSet;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.service.task.EsGetPkgSize;
import lab.whitetree.bonny.box.ui.AppListAdapter;

import org.whitetree.systable.data.PkgInfo;
import org.whitetree.systable.system.SystemChangedEvent;
import org.whitetree.systable.system.U;
import org.whitetree.systable.system.watcher.ApplicationWatcher;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import client.core.Core;
import client.core.model.Event;
import client.core.model.Notifiers;
import client.core.model.Task;

public class UninstallerFragment extends Fragment {
	private static final long HANDLER_DELAY_MILLIS = 100;

	private ArrayList<PkgInfo> mPkgInfoArr = null;
	private HashSet<String> mSelectedApp = null;
	private ListView mListView = null;
	private AppListAdapter mListAdapter = null;
	private RelativeLayout mBtnLayout = null;
	private Button mDelBtn = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return initView(inflater, container);
	}

	private View initView(LayoutInflater inflater, ViewGroup container) {
		if (inflater == null) {
			return null;
		}

		View root = inflater.inflate(R.layout.fragment_apps_layout, container, true);
		final Context context = root.getContext();

		mListView = (ListView) root.findViewById(R.id.list_view);
		mBtnLayout = (RelativeLayout) root.findViewById(R.id.footer_layout);
		mDelBtn = (Button) root.findViewById(R.id.btn_delete);
		mDelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mSelectedApp.clear();
				U.uninstallApps(context, mPkgInfoArr);
			}
		});

		mBtnLayout.setVisibility(View.GONE);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				onItemSelected(mPkgInfoArr.get(arg2));
			}
		});

		mSelectedApp = new HashSet<String>();

		// TODO - need set empty text here

		return root;
	}

	public void getEvent(Context context, Event event) {
		if (event instanceof EsGetPkgSize) {
			mRefreshDataHandler.removeCallbacks(mRefreshDataRunnable);
			mRefreshDataHandler.postDelayed(mRefreshDataRunnable, HANDLER_DELAY_MILLIS);
		}
	}

	public void getSystemChangeEvent(Context context, SystemChangedEvent event) {
		if (event == null) {
			return;
		}

		if (LocalService.CHID_APPS.equals(event.id)) {
			mPkgInfoArr = ApplicationWatcher.getUserApplications(event.walue);
			if (mPkgInfoArr != null) {
				mListAdapter = new AppListAdapter(context, mPkgInfoArr);
				mListView.setAdapter(mListAdapter);

				onGetEsGetPkgInfo(context, mPkgInfoArr);
			}
		}
	}

	public int getSelectedAppCount() {
		return mSelectedApp.size();
	}

	public void unselectAll() {
		mSelectedApp.clear();
		syncSelectedStatus();
		setBtnLayoutStatus();
	}

	private void onGetEsGetPkgInfo(Context context, ArrayList<PkgInfo> apps) {
		syncSelectedStatus();
		setBtnLayoutStatus();

		//Task task = new GetPkgSizeTask(context, mPkgInfoArr);
		//exec(task, "ui");
	}

	private void onItemSelected(PkgInfo info) {
		info.mIsSelected = !info.mIsSelected;
		refreshData();

		if (info.mIsSelected) {
			mSelectedApp.add(info.mPackageName);
		} else if (mSelectedApp.contains(info.mPackageName)) {
			mSelectedApp.remove(info.mPackageName);
		}

		setBtnLayoutStatus();
	}

	private void setBtnLayoutStatus() {
		if (mSelectedApp.size() > 0) {
			mBtnLayout.setVisibility(View.VISIBLE);
		} else {
			mBtnLayout.setVisibility(View.GONE);
		}
	}

	private void syncSelectedStatus() {
		if (mPkgInfoArr != null) {
			for (PkgInfo info : mPkgInfoArr) {
				if (mSelectedApp.contains(info.mPackageName)) {
					info.mIsSelected = true;
				} else {
					info.mIsSelected = false;
				}
			}
		}

		refreshData();
	}

	protected void exec(Task task, String targetGroup) {
		task.setTo(new Notifiers(targetGroup));
		Core.I().exec(task);
	}

	private Handler mRefreshDataHandler = new Handler();
	private Runnable mRefreshDataRunnable = new Runnable() {
		@Override
		public void run() {
			refreshData();
		}
	};

	private void refreshData() {
		mListAdapter.notifyDataSetChanged();
	}
}
