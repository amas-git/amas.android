package org.whitetree.systable.system.watcher;

import java.util.ArrayList;
import java.util.List;

import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.data.filter.And;
import org.whitetree.data.filter.IFilter;
import org.whitetree.data.filter.Not;
import org.whitetree.systable.LOG;
import org.whitetree.systable.data.Application;
import org.whitetree.systable.data.AppsFlagsFilter;
import org.whitetree.systable.data.ProcNameExcludeFilter;
import org.whitetree.systable.system.Walue;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;

public class RunningProcessWatcher extends WatcherEventSource {

	// 是系统应用
	public static IFilter<RunningAppProcessInfo> NOT_SYS_APPS = new Not<RunningAppProcessInfo>(new AppsFlagsFilter(ApplicationInfo.FLAG_SYSTEM));
	public static IFilter<RunningAppProcessInfo> NOT_VIP_APPS = new ProcNameExcludeFilter(new String[] { "system", "com.android.phone",
	        "android.process.acore", "com.android.providers.telephony" });
	// 不是以下包
	public static IFilter<RunningAppProcessInfo> DEFAULT_FILTER = new And<RunningAppProcessInfo>(NOT_SYS_APPS, NOT_VIP_APPS);

	public RunningProcessWatcher(Context context) {
		setId(LocalService.CHID_PROC_RUNNING);
		setContext(context);
	}
	
	@Override
	protected void update(Context context, Walue walue, String filterType) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> xs = am.getRunningAppProcesses();
		ArrayList<Application> mApps = new ArrayList<Application>();

		IFilter<RunningAppProcessInfo> filter = DEFAULT_FILTER;
		for (RunningAppProcessInfo x : xs) {
			if (filter.onFilter(x)) {
				Application app = new Application();
				app.pid = x.pid;
				app.processName = x.processName;
				app.importance = x.importance;
				app.packageName = x.pkgList == null ? "" : x.pkgList[0];
				app.uid = x.uid;
				mApps.add(app);
				LOG._("+APP = " + app);
			}
		}
		walue.put(":value", mApps);
	}

	public static ArrayList<Application> getRunningApplications(Walue walue) {
		return (ArrayList<Application>) walue.get(":value");
	}

	public static int getRunningApplicationsNumber(Walue walue) {
		ArrayList<Application> procs = (ArrayList<Application>) walue.get(":value");
		return procs != null ? procs.size() : 0;
	}
}
