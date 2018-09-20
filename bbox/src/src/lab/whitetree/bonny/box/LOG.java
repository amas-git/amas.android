package lab.whitetree.bonny.box;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.util.Printer;

public class LOG {
	/**
	 * Must be false on release version !!!
	 */
	private static final boolean sLogEnabled = true;
	
	public static final String TAG = "BBOX";
	
	public static final Printer PRINTER = new Printer() {
		@Override
		public void println(String msg) {
			_(msg);
		}
	};
	
	public static void _(String msg) {
		if (sLogEnabled) {
			Log.d(TAG, msg);
		}
	}
	
	public static void _(String tag, String msg) {
		if (sLogEnabled) {
			Log.d(TAG, String.format("[%s] -- %s", tag, msg));
		}
	}
	
	public static void dump(ResolveInfo i) {
		i.dump(PRINTER, "ResolveInfo");
	}
	
	public static void dump(RunningAppProcessInfo i) {
		if (sLogEnabled) {
			StringBuilder sb = new StringBuilder();
			sb.append("\n");
			sb.append("    pid         ="+i.pid).append("\n");
			sb.append("    importance  ="+i.importance).append("\n");
			sb.append("    processName ="+i.processName).append("\n");
			sb.append("    pkgList     ="+flatten(i.pkgList)).append("\n");
			LOG._("RUNNING", sb.toString());
		}
	}
	
	public static String flatten(String[] xs) {
		StringBuilder sb = new StringBuilder();
		for(String x: xs) {
			sb.append("'").append(x).append("' ");
		}
		return sb.toString();
	}
}
