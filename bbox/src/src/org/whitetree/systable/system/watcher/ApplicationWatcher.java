package org.whitetree.systable.system.watcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.data.filter.PkgInfoComparator;
import org.whitetree.systable.data.PkgInfo;
import org.whitetree.systable.system.SystemChangedEvent;
import org.whitetree.systable.system.U;
import org.whitetree.systable.system.Walue;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;

public class ApplicationWatcher extends WatcherEventSource {
	private static Intent APPS_INTENT = null;
	private PkgInfoComparator comp = new PkgInfoComparator();
	public int mCounter = 0;

	public ApplicationWatcher(Context context) {
		setContext(context);
		setId(LocalService.CHID_APPS);
	}

	PackageManager mPm = null;

	public class PkgSizeObserver extends IPackageStatsObserver.Stub {
		public PkgSizeObserver() {

		}

		// collect package size
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {

			// TODO: 将其归入一个新类型的Event中
			Walue walue = new Walue();
			walue.put(":name", pStats.packageName);
			walue.put(":size", pStats.codeSize + pStats.dataSize);
			//LOG._(".... ooo .... " + walue);
			SystemChangedEvent event = new SystemChangedEvent(LocalService.EVID_APP_SIZE, walue);
			send(event, LocalService.DEFAULT_BROADCAST);

			mCounter--;
			if (mCounter > 0) {

			} else if (mCounter == 0) {
			}
		}
	}

	public static String getPackageName(Walue walue) {
		return walue.getString(":name");
	}

	public static long getPackageSize(Walue walue) {
		return walue.getLong(":size");
	}

	@Deprecated
	private static boolean sHaveCouldMove2SdcardFlag = false;

	static {
		APPS_INTENT = new Intent("android.intent.action.MAIN", null);
		APPS_INTENT.addCategory("android.intent.category.LAUNCHER");
	}



	@Override
	protected void update(Context context, Walue walue, String filterType) {

		List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(APPS_INTENT, 0);
		// TODO: 避免重复分配容器
		ConcurrentHashMap<String, ResolveInfo> apps = new ConcurrentHashMap<String, ResolveInfo>(infos.size());

		ArrayList<PkgInfo> userApps = new ArrayList<PkgInfo>();

		if (mPm == null) {
			mPm = context.getPackageManager();
		}

		HashSet<String> packagesUniq = new HashSet<String>();

		for (ResolveInfo i : infos) {
			// LOG.dump(i);
			apps.put(i.activityInfo.packageName, i);
			if (isUserApp(i)) {
				// LOG._("USER APPS ============================== " + i);
				if (!packagesUniq.contains(i.activityInfo.packageName)) {
					userApps.add(PkgInfo.create(context, mPm, i));
					packagesUniq.add(i.activityInfo.packageName);
					//getPkgSize(context, i.activityInfo.packageName,mPkgSizeObserver);
				}
			}
		}

		Collections.sort(userApps, comp);
		walue.put(":all", apps);
		walue.put(":apps.userapps", userApps);
		walue.put(":apps.userapps.number", userApps.size());
		// walue.put("apps.total", apps.size());
		// setUpdateInterval(Long.MAX_VALUE);
	}

//	@Override
//	public Walue forceGet() {
//		Walue w = super.forceGet();
//		setUpdateInterval(50);
//		return w;
//	}

	public static final int APP_STORAGE_INTERNAL_FLAG = 0;

	public static boolean isUserApp(ResolveInfo ri) {
		ApplicationInfo i = ri.activityInfo.applicationInfo;
		return ((i.flags & ApplicationInfo.FLAG_SYSTEM) == 0 || (i.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
	}

	public static ResolveInfo get(Walue w, String packageName) {
		ConcurrentHashMap<String, ResolveInfo> apps = (ConcurrentHashMap<String, ResolveInfo>) w.getAsMap(":all");
		return apps == null ? null : apps.get(packageName);
	}

	public static ArrayList<PkgInfo> getUserApplications(Walue walue) {
		return (ArrayList<PkgInfo>) walue.get(":apps.userapps");
	}

	@Deprecated
	private static boolean hasCouldMove2SdcardFlag(Context context) {
		boolean flag = false;
		if (!U.isLowerThanFroyo(context)) {
			Class<ApplicationInfo> temp = ApplicationInfo.class;
			try {
				Field field = temp.getField("installLocation");
				if (field != null) {
					flag = true;
				}
			} catch (NoSuchFieldException e) {
			}
		}
		return flag;
	}

	/**
	 * TODO: 重复的方法
	 * 
	 * @param context
	 * @param pkgName
	 *            package name
	 * @param observer
	 *            observer to fetch notification
	 */
	public static void getPkgSize(Context context, String pkgName, PkgSizeObserver observer) {
		try {
			if (observer != null) {
				Method getPackageSizeInfo = context.getPackageManager().getClass()
				        .getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
				getPackageSizeInfo.invoke(context.getPackageManager(), pkgName, observer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
