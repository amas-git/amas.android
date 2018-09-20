package org.whitetree.systable.system;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.whitetree.systable.LOG;


import android.content.Context;

/**
 * 1. watcher 装入并不会立刻更新 2. watcher 的更新应当尽量是被动的 3. 对于不变值，应当使用StaticWatcher 4.
 * SystemTable作为信息代理，应当提供两种取得信息的方式 4.1 同步方式,确保取到最新的值 4.2
 * 异步方式,总是先读取缓存的值,如果Watcher恰好过期，则先更新缓存而后取值
 * 
 * @author amas
 */
public class SystemTable {

	private static SystemTable INSTANCE = new SystemTable();

	private SystemTable() {
	}

	/**
	 * 这个一般是应用的Context
	 */
	private Context mContext = null;

	ConcurrentHashMap<String, Watcher> mWtable = new ConcurrentHashMap<String, Watcher>();

	public interface OnTraverseListener {
		/**
		 * @param key
		 * @param walue
		 * @return return false to stop
		 */
		public boolean onTraverse(String key, Watcher walue);
	}

	public static SystemTable getInstance() {
		return INSTANCE;
	}

	// XXX: macro table
	public void setContext(Context context) {
		mContext = context;
		
		// 内存
//		MoomParser.addExprMacro("%MEM_PERCENT", "(percent :memory.used.percent)");
//		MoomParser.addExprMacro("%MEM_USED", "(format :memory.used.percent %)");
//		MoomParser.addExprMacro("%MEM_USEDT", "(formatb :memory.used)");
//		MoomParser.addExprMacro("%MEM_TOTAL", "(formatb :memory.TOTAL)");
//		MoomParser.addExprMacro("%MEM_FREE", "(formatb :memory.free)");
//		MoomParser.addExprMacro("%MEM_FREEP", "(format :memory.free.percent %)");
//
//		// 电池
//		MoomParser.addExprMacro("%POW_LP", "(percent :power.level)");
//		MoomParser.addExprMacro("%POW_LT", "(format :power.level %)");
//		MoomParser.addExprMacro("%POW_VT", "(format :power.voltage.v V)");
//		MoomParser.addExprMacro("%POW_TT","(format :power.temperature)");
//		MoomParser.addExprMacro("%POW_LT_VALUE", "(format :power.level)");
//		
//		// CPU
//		MoomParser.addExprMacro("%CPU_UP","(percent :cpu.usage.total)");
//		MoomParser.addExprMacro("%CPU_UPT","(format :cpu.usage.total %)");
//		MoomParser.addExprMacro("%CPU_FC","(format :cpu.cur.freq.formatted Hz)");
//		MoomParser.addExprMacro("%CPU_CN","(format :cpu.count)");
//		MoomParser.addExprMacro("%CPU_UPT_VALUE","(format :cpu.usage.total)");
//		
//		MoomParser.addExprMacro("%CPU0_UP","(percent :cpu.usage.cpu0)");
//		MoomParser.addExprMacro("%CPU1_UP","(percent :cpu.usage.cpu1)");
//		MoomParser.addExprMacro("%CPU2_UP","(percent :cpu.usage.cpu2)");
//		MoomParser.addExprMacro("%CPU3_UP","(percent :cpu.usage.cpu3)");
//		
//		// 存储
//		MoomParser.addExprMacro("%DISK_UP", "(percent :space.used.percent)");
//		MoomParser.addExprMacro("%DISK_UPT","(format :space.used.percent %)");
//		MoomParser.addExprMacro("%DISK_UUT", "(formatb :space.used)");
//		MoomParser.addExprMacro("%DISK_UTT", "(formatb :space)");
//		MoomParser.addExprMacro("%DISK_UPT_VALUE","(format :space.used.percent)");
//		
//		// WIFI states
//		MoomParser.addExprMacro("%RSSI_LP", "(percent :wifi.rssi.level.percent)");
	}

	public void addWatcher(String wId, Watcher watcher) {
		watcher.setContext(mContext);
		mWtable.put(wId, watcher);
		LOG._("addWatcher :======= " +wId);
	}

	public Object removeWatcher(String wId) {
		if (mWtable != null) {
			return mWtable.remove(wId);
		}
		return null;
	}

	public void listAll() {
		// TODO: 打印所有频道
	}

	/**
	 * Get snapshot of system table
	 * 
	 * @return
	 */
	public/* synchronized */HashMap<String, Walue> snapshot() {
		HashMap<String, Walue> dict = new HashMap<String, Walue>();

		Iterator<Map.Entry<String, Watcher>> iter = mWtable.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Watcher> entry = (Map.Entry<String, Watcher>) iter.next();

			String k = (String) entry.getKey();
			Watcher w = (Watcher) entry.getValue();
			Walue v = w.get(null);
			dict.put(k, v);
			LOG._(String.format("%s=%s", k, w.toString()));
		}
		return dict;
	}

	/**
	 * 遍历SystemTable中的所有数据项
	 * 
	 * @param t
	 */
	public void foreach(OnTraverseListener t) {
		Iterator<Map.Entry<String, Watcher>> iter = mWtable.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Watcher> entry = (Map.Entry<String, Watcher>) iter.next();
			try {
				if (!t.onTraverse((String) entry.getKey(), (Watcher) entry.getValue())) {
					return;
				}
			} catch (Exception e) {
				LOG._(e.toString());
			}
		}
	}

	/**
	 * 立刻返回
	 * 
	 * @param wId
	 * @return
	 */
	public/* synchronized */Walue get(String wId, String filterType) {
		Watcher w = mWtable.get(wId);
		if (w == null) {
			LOG._("WATCHER NOT FOUND : " + wId);
			return null;
		}
		return w.get(filterType);
	}
	
	public/* synchronized */Walue get(String wId) {
		return get(wId, null);
	}

	public Walue forceGet(String wId, String filterType) {
		Watcher w = mWtable.get(wId);
		if (w == null) {
			LOG._("forceGet(): WATCHER NOT FOUND : " + wId);
			return null;
		}
		return w.forceGet(filterType);
	}
}
