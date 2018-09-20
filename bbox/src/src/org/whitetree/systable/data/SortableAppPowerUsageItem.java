package org.whitetree.systable.data;

import android.os.BatteryStats.Uid;

public class SortableAppPowerUsageItem implements Comparable<SortableAppPowerUsageItem> {
	public double mUsage = 0.0;
	public Uid mUid = null;

	public SortableAppPowerUsageItem(Uid uid, double usage) {
		mUsage = usage;
		mUid = uid;
	}

	public int compareTo(SortableAppPowerUsageItem another) {
		return Double.compare(another.mUsage, mUsage);
	}

	public String toString() {
		return String.format("(:UID %d :USAGE %f)", mUid.getUid(), mUsage);
	}
}

