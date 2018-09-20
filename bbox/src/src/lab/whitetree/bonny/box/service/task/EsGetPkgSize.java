package lab.whitetree.bonny.box.service.task;

import android.content.pm.PackageStats;
import client.core.model.Event;

public class EsGetPkgSize extends Event {
	public PackageStats stats = null;

	public EsGetPkgSize(PackageStats stats) {
		this.stats = stats;
	}

	@Override
	public String toString() {
		long totalSize = stats.cacheSize + stats.codeSize + stats.dataSize;
		return String.format("(%s :appSize %d)", super.toString(),totalSize);
	}
}