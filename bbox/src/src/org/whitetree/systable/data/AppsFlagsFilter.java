package org.whitetree.systable.data;

import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.data.filter.IFilter;
import org.whitetree.systable.LOG;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.pm.ApplicationInfo;

public class AppsFlagsFilter implements IFilter<RunningAppProcessInfo> {
	public int mFlags = 0x00;
	
	public AppsFlagsFilter(int flags) {
		mFlags = flags;
	}
	
	@Override
	public boolean onFilter(RunningAppProcessInfo target) {
		// XXX: 一个进程之中可以运行多个package, 但是一个package的flags为系统应用，则可以断定
		// 整个进程都是系统应用, 故而此处使用processName进行查询，而不是packageName, 对于一般
		// 应用来说， packageName常常等于processName
		ApplicationInfo i = LocalService.getApplicationInfo(target.processName);
		if(i == null) {
			return true;
		}
		LOG.dump(target);
		return ((i.flags & mFlags) != 0);
	}
}
