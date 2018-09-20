package org.whitetree.systable.system.watcher;

import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.systable.system.U;
import org.whitetree.systable.system.Walue;

import android.content.Context;


public class MemoryWatcher extends WatcherEventSource {

	long mTotalMemory = 0;
	
	public MemoryWatcher(Context context, int updateInterval) {
		setUpdateInterval(updateInterval);
		setContext(context);
		setId(LocalService.CHID_MEMORY);
		mTotalMemory = U.getTotalMem();
	}
	
	
	@Override
	protected void update(Context context, Walue walue, String filterType) {
		long free = U.getAvailMem(context);
		
		walue.put(":memory.TOTAL", mTotalMemory);
		walue.put(":memory.free", free);
		walue.put(":memory.free.percent", (int)(free * 100 / mTotalMemory));
		walue.put(":memory.used", mTotalMemory - free);
		walue.put(":memory.used.percent", (int)(100 - (free * 100 / mTotalMemory)));
	}
	
	public static long getTotalMemory(Walue walue) {
		return walue.getLong(":memory.TOTAL");
	}
	
	public static long getUsed(Walue walue) {
		return walue.getLong(":memory.free");
	}
	
	public static long getFree(Walue walue) {
		return walue.getLong(":memory.used");
	}
	
	public static int getUsedPercent(Walue walue) {
		return walue.getInt(":memory.used.percent");
	}

}
