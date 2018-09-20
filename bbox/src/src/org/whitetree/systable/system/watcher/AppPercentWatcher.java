package org.whitetree.systable.system.watcher;

import java.util.ArrayList;

import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.systable.data.AppPercent;
import org.whitetree.systable.system.U;
import org.whitetree.systable.system.Walue;

import android.content.Context;

public class AppPercentWatcher extends WatcherEventSource {

	public AppPercentWatcher(Context context, int updateInterval) {
		setContext(context);
		setUpdateInterval(updateInterval);
		setId(LocalService.CHID_CPU_APP_PERCENT);
	}
	
	@Override
	protected void update(Context context, Walue walue, String filterType) {
		ArrayList<AppPercent> appPercents = U.getAppPercent();
		walue.put(":cpu.app.percent", appPercents);
	}
	
	public static ArrayList<AppPercent> getAppPercentList(Walue walue) {
		return (ArrayList<AppPercent>) walue.getAsList(":cpu.app.percent");
	}
	
	public static int getAppCount(Walue walue){
		return ((ArrayList<AppPercent>) walue.getAsList(":cpu.app.percent")).size();
	}
	
}
