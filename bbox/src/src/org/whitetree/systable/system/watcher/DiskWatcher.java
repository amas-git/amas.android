package org.whitetree.systable.system.watcher;

import java.io.File;

import org.whitetree.systable.system.U;
import org.whitetree.systable.system.Walue;

import android.content.Context;
import android.os.StatFs;

public class DiskWatcher extends WatcherEventSource {
	public static final String WALUE_STORAGE_STATUS = ":status.mounted";
	File mTarget = null;
	
	public DiskWatcher(Context context, String id, File target, int interval) {
		setContext(context);
		setUpdateInterval(interval);
		mTarget = target == null ? new File("/") : target;
		setId(id);
	}
	
	@Override
	protected void update(Context context, Walue walue, String filterType) {
		add(mTarget.getAbsolutePath(), U.getFileSystemStat(mTarget), walue);
	}
	
	private static void add(String path, StatFs fs, Walue walue) {
		walue.put(":mount",                path);
		if(fs == null) {
			walue.put(WALUE_STORAGE_STATUS, false);
			return;
		}
			
		long blockNums = fs.getBlockCount();
		long blockSize = fs.getBlockSize();
		long blockFree = fs.getAvailableBlocks();
		
		walue.put(WALUE_STORAGE_STATUS, true);
		
		long all  = blockNums * blockSize;
		long free = blockFree * blockSize;
		if(all == 0) {
			// FIXME: 此种情况一般是sdcard不可用, 可以试用wangyan的手机重现此问题
			//LOG._("WARN: " + path + " zero space");
			all  = 1;
			free = 1;
			walue.put(":status.mounted", false);
		}
		
		long used = all - free;

		
		
		walue.put(":space",                all);
		walue.put(":space.free",           free);
		walue.put(":space.free.percent",   (int)(free*100/all));
		walue.put(":space.used",           used);
		walue.put(":space.used.percent",   (int)(100 - free*100/all));
		
		walue.put(":block",          blockNums);
		walue.put(":block.size",     blockSize);
		walue.put(":block.free",     blockFree);
		
	}
	
	public static long getSpace(Walue _) {
		return _.getLong(":space");
	}
	
	public static long getFreeSpace(Walue _) {
		return  _.getLong(":space.free");
	}
	
	public static long getFreeSpacePercent(Walue _) {
		return  _.getInt(":space.free.percent");
	}
	
	public static long getUsedSpace(Walue _) {
		return  _.getLong(":space.used");
	}
	
	public static long getUsedSpacePercent(Walue _) {
		return  _.getInt(":space.used.percent");
	}
	
	public static String getMountPoint(Walue _) {
		return  _.getString(":mount");
	}
	
	@Override
	protected String formatted(Walue _) {
		return formatTarget(mTarget.getAbsolutePath(), _);
	}

	private String formatTarget(String key, Walue _) {
		return String.format("(:MNT %s  :USED %d :FREE %d :TOTAL %d :FREE%% %d%% :USED%% %d%%)", 
				getMountPoint(_), getUsedSpace(_), getFreeSpace(_), getSpace(_),
				getFreeSpacePercent(_), getUsedSpacePercent(_));
	}
}
