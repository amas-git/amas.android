package lab.whitetree.bonny.box.ui.fragments;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.ui.filemanager.FileManager;

import org.whitetree.bidget.moom.MoomView;
import org.whitetree.systable.system.SystemChangedEvent;
import org.whitetree.systable.system.U;
import org.whitetree.systable.system.watcher.DiskWatcher;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class DiskFragment extends Fragment {
	
	private MoomView mSystem = null;
	private MoomView mData = null;
	private MoomView mSdcard = null;
	private MoomView mSdcardExt =  null;
	
	private boolean mSdcardOnline = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return initView(inflater, container);
	}

	private View initView(LayoutInflater inflater, ViewGroup container) {
		if (inflater == null) {
			return null;
		}
		
		View root = inflater.inflate(R.layout.fragment_disk_layout, container, true);
		
		mSystem = (MoomView) root.findViewById(R.id.fs_system);
		mData = (MoomView) root.findViewById(R.id.fs_data);
		mSdcardExt = (MoomView) root.findViewById(R.id.fs_sdcard_ext);
		mSdcard = (MoomView) root.findViewById(R.id.fs_sdcard);
		if(U.getExternalSdcardMountPoint() == null) {
			mSdcardExt.setVisibility(View.GONE);
		}
		
		return root;
	}
	
	public void getSystemChangeEvent(Context context, SystemChangedEvent event) {
		if (event == null) {
			return;
		}
		
		String evId = event.id;
		
		if(LocalService.CHID_STORAGE_DATA.equals(evId)) {
			mData.invalidate();
		} else if (LocalService.CHID_STORAGE_SDCARD.equals(evId)) {
			mSdcard.invalidate();
			mSdcardOnline = (Boolean)event.walue.get(DiskWatcher.WALUE_STORAGE_STATUS);
		} else if (LocalService.CHID_STORAGE_SDCARD_EXT.equals(evId)) {
			mSdcardExt.invalidate();
		} else if (LocalService.CHID_STORAGE_SYSTEM.equals(evId)) {
			mSystem.invalidate();
		}
	}
	
	public void onClickSdcard(View view) {
		if (Environment.getExternalStorageDirectory().exists() && mSdcardOnline) {
			FileManager.startFileManager(view.getContext(), Environment.getExternalStorageDirectory().getAbsolutePath());
		} else {
			warn(view.getContext(), R.string.sdcardoffline);
		}
	}
	
	public void onClickExtSdcard(View view) {
		if(U.getExternalSdcardMountPoint().exists()) {
			FileManager.startFileManager(view.getContext(), U.getExternalSdcardMountPoint().getAbsolutePath());
		}
	}

	public void onClickData(View view) {
		warn(view.getContext(), R.string.mimi);
	}

	public void onClickSystem(View view) {
		FileManager.startFileManager(view.getContext(), "/system");
	}
	
	
	/**
	 * Make a short toast to show some messsage
	 * @param msg
	 * @return
	 */
	@SuppressWarnings("unused")
	private void warn(Context context, String msg) {
		Toast.makeText(context, msg,Toast.LENGTH_SHORT).show();	
	}
	
	private void warn(Context context, int resId) {
		Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show();	
	}
}
