package org.whitetree.systable.system.watcher;

import java.util.ArrayList;

import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.data.filter.PkgInfoComparator;
import org.whitetree.systable.data.ComponentInfo;
import org.whitetree.systable.system.U;
import org.whitetree.systable.system.Walue;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class BootupApplicationWatcher extends WatcherEventSource {
	private static Intent APPS_INTENT = null;
	PkgInfoComparator comp = new PkgInfoComparator();
	static {
		APPS_INTENT = new Intent("android.intent.action.BOOT_COMPLETED", null);
	}

	PackageManager mPm = null;

	public BootupApplicationWatcher(Context context) {
		setContext(context);
		setId(LocalService.CHID_APPS_BOOTUP);
	}
	
	@Override
	protected void update(Context context, Walue walue, String filterType) {
		ArrayList<ComponentInfo> userApps = U.getBootUpComponent(context);
		
		int count = 0;
		for(ComponentInfo info : userApps){
			if(info.isEnable()){
				count++;
			}
		}
		
//		Collections.sort(userApps, comp);
		walue.put(":all", userApps);
		walue.put(":apps.bootup", userApps);
		walue.put(":apps.bootup.number", count);
		// walue.put("apps.total", apps.size());
		setUpdateInterval(Long.MAX_VALUE);
	}

	@Override
	public Walue forceGet() {
		Walue w = super.forceGet();
		setUpdateInterval(50);
		return w;
	}

	public static final int APP_STORAGE_INTERNAL_FLAG = 0;

	public static int getBootupApplicationsSize(Walue walue) {
		return walue.getInt(":apps.bootup.number");
	}
	
	public static ArrayList<ComponentInfo> getgetBootupApplications(Walue walue){
		return (ArrayList<ComponentInfo>) walue.getAsList(":apps.bootup");
	}
	
}
