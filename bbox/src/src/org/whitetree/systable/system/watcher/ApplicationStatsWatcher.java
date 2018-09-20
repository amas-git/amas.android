package org.whitetree.systable.system.watcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.systable.LOG;
import org.whitetree.systable.data.PkgInfo;
import org.whitetree.systable.system.SystemChangedEvent;
import org.whitetree.systable.system.U;
import org.whitetree.systable.system.Walue;

import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import client.core.model.Notifiers;

public class ApplicationStatsWatcher extends WatcherEventSource {
	private ArrayList<PkgInfo> mPkgInfoList = null;
	private HashMap<String, PkgInfo> mLookup = null;
	private PkgSizeObserver mPkgSizeObserver = new PkgSizeObserver();
	private int mCounter = 0;
	private long mTotalCacheSize = 0;
	
	public ApplicationStatsWatcher(Context context, Notifiers to) {
		setContext(context);
		setId(LocalService.CHID_APPS_STATS);
		setAsynchronousNotifyEvent(to);
	}

	public class PkgSizeObserver extends IPackageStatsObserver.Stub {
		public PkgSizeObserver() {

		}

		// collect package size
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {
			mCounter--;
			if (mCounter > 0) {
				mTotalCacheSize += pStats.cacheSize;
				setPackageStats(pStats.packageName, pStats.cacheSize, pStats.codeSize, pStats.dataSize);
			} else if (mCounter == 0) {
				//
				LOG._("............... 总大小: " + mTotalCacheSize + "");
				mValue.put(":apps.cache.total", mTotalCacheSize);
				SystemChangedEvent event = new SystemChangedEvent(LocalService.CHID_APPS_STATS, mValue);
				send(event);
			}
		}
	}

	public void setPackageStats(String pkgName, long cacheSize, long codeSize, long dataSize) {
		if (mLookup != null) {
			PkgInfo x = mLookup.get(pkgName);
			if (x != null) {
				x.mCacheSize = cacheSize;
				x.mCodeSize = codeSize;
				x.mDateSize = dataSize;
			}
		}
	}

	public ApplicationStatsWatcher() {
		setUpdateInterval(Long.MAX_VALUE);
	}

	public static final long getTotalCacheSize(Walue walue) {
		return walue.getLong(":apps.cache.total");
	}

	public static final ArrayList<PkgInfo> getInstalledApps(Walue walue) {
		return (ArrayList<PkgInfo>) walue.getAsList(":apps.installed");
	}

	@Override
	protected void update(Context context, Walue walue, String filterType) {
		mPkgInfoList = U.getInstalledApps(context, false);
		mValue.put(":apps.installed", mPkgInfoList);
		
		if (mPkgInfoList != null) {
			mCounter = mPkgInfoList.size();
			mLookup = new HashMap<String, PkgInfo>(mPkgInfoList.size());
			mTotalCacheSize = 0;
			for (PkgInfo info : mPkgInfoList) {
				mLookup.put(info.mPackageName, info);
				getPkgSize(context, info.mPackageName, mPkgSizeObserver);
			}
		}
	}

	/**
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
