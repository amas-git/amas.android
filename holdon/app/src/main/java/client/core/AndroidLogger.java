package client.core;



class Log {
	/**
	 * Must be false on release version !!!
	 */
	private static final boolean sLogEnabled = false;
	
/*	public static final void _(String msg) {
		if(sLogEnabled) android.util.Log.e("bcore", "---------- " + msg);
	}*/
	
	public static final void print(String tag, String msg) {
		if(sLogEnabled) android.util.Log.e("bcore","["+tag+"] ┣ "+msg);
	}

/*	public static void i(String tag, String msg) {
		if(sLogEnabled) android.util.Log.i("bcore","["+tag+"] ┣ "+msg);
		
	}*/
}

/**
 * Log wrapper
 * TODO(zhoujb): to interface
 * @author amas
 *
 */
public class AndroidLogger implements ILogger{
//	private static String LOGFORMAT="[%s] -- %s";
	public void e(String tag, String msg) {
		Log.print(tag, msg);
	}
	
	public void d(String tag, String msg) {
		Log.print(tag, msg);
	}
	
	public void i(String tag, String msg) {
		Log.print(tag, msg);
	}
}
