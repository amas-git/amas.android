package org.whitetree.systable.system.watcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.systable.LOG;
import org.whitetree.systable.data.SortableAppPowerUsageItem;
import org.whitetree.systable.system.PowerUtils;
import org.whitetree.systable.system.PowerUtils.UidPicker;
import org.whitetree.systable.system.Walue;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.BatteryStats;
import android.os.BatteryStats.Uid;

import com.android.internal.os.BatteryStatsImpl;

public class PowerStatsWatcher extends WatcherEventSource {
	public PowerStatsWatcher(Context context) {
		setContext(context);
		setId(LocalService.CHID_POWERSTATS);
	}

	protected int mStatsType = BatteryStats.STATS_SINCE_UNPLUGGED;
	protected BatteryStatsImpl mStatus = null;
	protected long mNow = 0;
	protected double mPercent = 0;

	class DefaultAppPowerPicker implements UidPicker {
		ArrayList<SortableAppPowerUsageItem> mData = new ArrayList<SortableAppPowerUsageItem>();
		double mTotalUsage = 0;
		Context mContext = null;
		PackageManager mPm = null;

		public DefaultAppPowerPicker(Context context) {
			mContext = context;
			mPm = context.getPackageManager();
		}

		public boolean onPick(Uid uid) {
			LOG._("UID ================ " + uid);
			double usage = PowerUtils.getAppPowerByUid(mContext, uid, mNow, mStatus, mStatsType);
			
			if (usage <= 0.0000001) {
				return false;
			}

			// skip system package
			String[] packages = mPm.getPackagesForUid(uid.getUid());
			if (packages == null || packages.length == 0) {
				return false;
			}
			
			mTotalUsage += usage;
			mData.add(new SortableAppPowerUsageItem(uid, usage));
			return false;
		}
		
		public ArrayList<SortableAppPowerUsageItem> getAll() {
			Collections.sort(mData);
			return mData;
		}

		public double getTotalUsage() {
			return mTotalUsage;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("(\n");
			for (int i = 0; i < mData.size(); ++i) {
				SortableAppPowerUsageItem item = (SortableAppPowerUsageItem) mData.get(i);
				sb.append(String.format("(%d %s)\n", i, item));
			}
			sb.append(")\n");
			return sb.toString();
		}
	}

	@Override
	protected void update(Context context, Walue walue, String filterType) {
		// TODO: singleinstance !!!
		DefaultAppPowerPicker dapp = new DefaultAppPowerPicker(mContext);
		mStatus = PowerUtils.getBatteryStatus();
		mNow = System.currentTimeMillis();
		PowerUtils.getAppsUsage(mContext, mNow, mStatus, mStatsType, dapp);
		ArrayList<SortableAppPowerUsageItem> items = dapp.getAll();

		walue.put(":totalusage", dapp.getTotalUsage());
		walue.put(":topapplist", items);
	}

	public static double getTotalUsage(Walue w) {
		return w.getDouble(":totalusage");
	}

	public static List<SortableAppPowerUsageItem> getTopApps(Walue w) {
		return (List<SortableAppPowerUsageItem>) w.getAsList(":topapplist");
	}
}
