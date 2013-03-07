package client.core;



class Log {
	/**
	 * Must be false on release version !!!
	 */
	private static final boolean sLogEnabled = true;
	
	public static final void _(String msg) {
		if(sLogEnabled) android.util.Log.e("-------:", "---------- " + msg);
	}
	
	public static final void _(String tag, String msg) {
		if(sLogEnabled) android.util.Log.e("[STABLE]","["+tag+"] ┣ "+msg);
	}

	public static void i(String tag, String msg) {
		if(sLogEnabled) android.util.Log.i("[STABLE]","["+tag+"] ┣ "+msg);
		
	}
}

/**
 * Log wrapper
 * TODO(zhoujb): to interface
 * @author amas
 *
 */
public class AndroidLogger implements ILogger{
	private static String LOGFORMAT="[%s] -- %s";
	public void e(String tag, String msg) {
		Log._(tag, msg);
	}
	
	public void d(String tag, String msg) {
		Log._(tag, msg);
	}
	
	public void i(String tag, String msg) {
		Log._(tag, msg);
	}
}
