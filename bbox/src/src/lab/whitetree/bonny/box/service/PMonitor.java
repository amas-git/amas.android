package lab.whitetree.bonny.box.service;

import lab.whitetree.bonny.box.LOG;
import android.content.Context;

public class PMonitor extends PackageMonitor {
	Context mContext = null;
	
	public PMonitor(Context context) {
		mContext = context;
	}
	
	@Override
	public void onBeginPackageChanges() {
	}

	@Override
	public void onPackageAdded(String packageName, int uid) {
		LOG._("ZZZ", "onPackageAdded() >>>>>>> ++++++++，还冷这感冒? >>>> " + packageName + " uid="+uid);
		forceUpdate();
	}
	
	@Override
	public void onPackageRemoved(String packageName, int uid) {
		// TODO Auto-generated method stub
		LOG._("ZZZ","onPackageRemoved() >>>>>>> ---------- >>>> " + packageName + " uid="+uid);
		forceUpdate();
	}
	
	@Override
	public void onSomePackagesChanged() {
		LOG._("ZZZ","onSomePackagesChanged() >>>>>>> 你妈有包发生变化了，还冷这感冒?");
		forceUpdate();
	}

	private void forceUpdate() {
		LocalService.startForceQuery(mContext,  new String[] {LocalService.CHID_APPS});
	}
}
