package org.whitetree.systable.system.watcher;

import java.util.ArrayList;

import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.systable.system.U;
import org.whitetree.systable.system.Walue;

import android.content.Context;

public class CpuWatcher extends WatcherEventSource {
	public static final String WALUE_KEY_CPU = ":cpu";
	public static final String WALUE_KEY_CPU_STATIC = ":cpu.static"; // 静态值
	
	public static final String WALUE_KEY_CPU_NAME = ":cpu.name";		// cpu 名称  恒定的
	public static final String WALUE_KEY_CPU_COUNT = ":cpu.count";		// cpu 数量  1 2 4 8  恒定的
	public static final String WALUE_KEY_CPU_MAX_FREQ = ":cpu.max.freq";	// cpu 最高频率  恒定的
	public static final String WALUE_KEY_CPU_MIN_FREQ = ":cpu.min.freq";	// cpu 最低频率  恒定的
	public static final String WALUE_KEY_CPU_CUR_FREQ = ":cpu.cur.freq"; 	// cpu 当前频率
	public static final String WALUE_KEY_CPU_MAX_FREQ_FORMATTED = ":cpu.max.freq.formatted";	// cpu 最高频率 用于显示
	public static final String WALUE_KEY_CPU_MIN_FREQ_FORMATTED = ":cpu.min.freq.formatted";	// cpu 最低频率 用于显示
	public static final String WALUE_KEY_CPU_CUR_FREQ_FORMATTED = ":cpu.cur.freq.formatted"; 	// cpu 当前频率 用于显示
	public static final String WALUE_KEY_CPU_CUR_FREQ_PERCENT = ":cpu.cur.freq.percent"; 	// cpu 当前频率 比例
	public static final String WALUE_KEY_CPU_USAGE_TOTAL = ":cpu.usage.total";	// cpu 使用率 (总的)
	public static final String WALUE_KEY_CPU_USAGE_CPU0 = ":cpu.usage.cpu0";	// cpu0 使用率 
	public static final String WALUE_KEY_CPU_USAGE_CPU1 = ":cpu.usage.cpu1";	// cpu1 使用率 
	public static final String WALUE_KEY_CPU_USAGE_CPU2 = ":cpu.usage.cpu2";	// cpu2 使用率 
	public static final String WALUE_KEY_CPU_USAGE_CPU3 = ":cpu.usage.cpu3";	// cpu3 使用率 
 	
	
	String mCpuName="";
	String mMaxFreq="";
	String mMinFreq="";
	int mCpuNumber=1;
	
	public CpuWatcher(Context context, int interval) {
		setId(LocalService.CHID_CPU);
		setUpdateInterval(interval);
		setContext(context);
		
		mCpuName=U.getCpuName();
		mMaxFreq=U.getCpuMaxFreq();
		mMinFreq=U.getCpuMinFreq();
		mCpuNumber=U.getCpuCount();
	}
	
	@Override
	protected void update(Context context, Walue walue, String filterType) {
		walue.put(WALUE_KEY_CPU_NAME, mCpuName);
		walue.put(WALUE_KEY_CPU_MAX_FREQ, mMaxFreq);
		walue.put(WALUE_KEY_CPU_MIN_FREQ, mMinFreq);
		walue.put(WALUE_KEY_CPU_COUNT, mCpuNumber);
		
		walue.put(WALUE_KEY_CPU_USAGE_TOTAL, 0);
		walue.put(WALUE_KEY_CPU_USAGE_CPU0,  0);
		
		// CPU usage
		ArrayList<Integer> cpuUsages = U.getCpuUsed();
		if (cpuUsages != null && cpuUsages.size() > 0) {
			walue.put(WALUE_KEY_CPU_USAGE_TOTAL, cpuUsages.get(0));
			walue.put(WALUE_KEY_CPU_USAGE_CPU0, cpuUsages.get(1));
			walue.put(WALUE_KEY_CPU_USAGE_CPU1, 0);
			walue.put(WALUE_KEY_CPU_USAGE_CPU2, 0);
			walue.put(WALUE_KEY_CPU_USAGE_CPU3, 0);
			if (cpuUsages.size() > 2) {
				walue.put(WALUE_KEY_CPU_USAGE_CPU1, cpuUsages.get(2));
			}
			if (cpuUsages.size() > 3) {
				walue.put(WALUE_KEY_CPU_USAGE_CPU2, cpuUsages.get(3));
			}
			if (cpuUsages.size() > 4) {
				walue.put(WALUE_KEY_CPU_USAGE_CPU3, cpuUsages.get(4));
			}
		}
		
		String curCpuFreq = U.getCpuCurFreq();
		walue.put(WALUE_KEY_CPU_CUR_FREQ, curCpuFreq);
		walue.put(WALUE_KEY_CPU_CUR_FREQ_FORMATTED, U.formatCpuFrequence(curCpuFreq));

		walue.put(WALUE_KEY_CPU_MAX_FREQ_FORMATTED, U.formatCpuFrequence(mMaxFreq));
		walue.put(WALUE_KEY_CPU_MIN_FREQ_FORMATTED, U.formatCpuFrequence(mMinFreq));
		
		try {
			int maxFreq = Integer.valueOf(mMaxFreq).intValue();
			int minFreq = Integer.valueOf(mMinFreq).intValue();
			int curFreq = Integer.valueOf(curCpuFreq).intValue();
			int percent = (int) ((curFreq - minFreq)  * 100 / (maxFreq - minFreq));
			walue.put(WALUE_KEY_CPU_CUR_FREQ_PERCENT, percent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected String formatted(Walue walue) {
		return String.format("(:load %d)", getCpuTotalUsage(walue));
	}
	
	public static String getCpuName(Walue walue) {
		return walue.getString(WALUE_KEY_CPU_NAME);
	}
	
	public static String getCpuMaxFreq(Walue walue) {
		return walue.getString(WALUE_KEY_CPU_MAX_FREQ);
	}
	
	public static String getCpuMinFreq(Walue walue) {
		return walue.getString(WALUE_KEY_CPU_MIN_FREQ);
	}
	
	public static String getCpuCurFreq(Walue walue) {
		return walue.getString(WALUE_KEY_CPU_CUR_FREQ);
	}

	public static String getFormattedCurrentFreq(Walue walue) {
		return (String) walue.get(CpuWatcher.WALUE_KEY_CPU_CUR_FREQ_FORMATTED);
	}
	
	public static String getFormattedMaxFreq(Walue walue) {
		return (String) walue.get(CpuWatcher.WALUE_KEY_CPU_MAX_FREQ_FORMATTED);
	}
	
	public static String getFormattedMinFreq(Walue walue) {
		return (String) walue.get(CpuWatcher.WALUE_KEY_CPU_MIN_FREQ_FORMATTED);
	}
	
	public static int getCpuCount(Walue walue) {
		int count = 1;
		try {
			count = Integer.valueOf(walue.getString(WALUE_KEY_CPU_COUNT)).intValue();
		} catch (Exception e) {
		}
		return count;
	}
	
	public static long getCpuTotalUsage(Walue walue) {
		long usage = 0l;
		try {
			usage = Long.valueOf(walue.getString(WALUE_KEY_CPU_USAGE_TOTAL)).longValue();
		} catch (Exception e) {
		}
		return usage;
	}

}
