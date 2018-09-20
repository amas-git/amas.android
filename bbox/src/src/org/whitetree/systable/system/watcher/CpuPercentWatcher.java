package org.whitetree.systable.system.watcher;

import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.systable.system.U;
import org.whitetree.systable.system.Walue;

import android.content.Context;

public class CpuPercentWatcher extends WatcherEventSource {
	
	public CpuPercentWatcher(Context context) {
		setContext(context);
		setId(LocalService.CHID_CPU_PERCENT);
	}
	
	@Override
	protected void update(Context context, Walue walue, String filterType) {
		int [] percents = U.getCpuPercent();
		walue.put(":cpu.user.percent", percents[0]);
		walue.put(":cpu.kernel.percent", percents[1]);
		walue.put(":cpu.both.percent", percents[2]);
	}
	
	public static int getCpuUserPercent(Walue walue) {
		return walue.getInt(":cpu.user.percent");
	}
	
	public static int getCpuKernelPercent(Walue walue) {
		return walue.getInt(":cpu.kernel.percent");
	}
	
	public static int getCpuPercent(Walue walue) {
		return walue.getInt(":cpu.both.percent");
	}
	
}