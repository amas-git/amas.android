package org.whitetree.systable.system.watcher;

import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.systable.system.U;
import org.whitetree.systable.system.Walue;

import android.content.Context;

public class HistoryWatcher extends WatcherEventSource {
	
	public HistoryWatcher(Context context) {
		setContext(context);
		setId(LocalService.CHID_HISTORY);
	}
	
	@Override
	protected void update(Context context, Walue walue, String filterType) {
		int count = U.historyCount(context);
		walue.put(":history.count", count);
	}
	
	public static int getHistoryCount(Walue walue) {
		return walue.getInt(":history.count");
	}
}
