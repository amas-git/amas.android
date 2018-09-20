package org.whitetree.systable.data;

import org.whitetree.data.filter.IFilter;

import android.app.ActivityManager.RunningAppProcessInfo;

/**
 * 过滤掉进程名匹配指定模式的应用
 * @author amas
 *
 */
public class ProcNameExcludeFilter implements IFilter<RunningAppProcessInfo> {
	public String[] mNamePatterns = null;
	
	public ProcNameExcludeFilter(String[] patterns) {
		mNamePatterns = patterns;
	}
	
	@Override
	public boolean onFilter(RunningAppProcessInfo target) {
		for(String x : mNamePatterns) {
			if(target.processName.equals(x)) {
				return false;
			}
		}
		return true;
	}
}