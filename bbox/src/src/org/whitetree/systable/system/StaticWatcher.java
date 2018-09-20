package org.whitetree.systable.system;


/**
 * 只取一次值
 * @author amas
 *
 */
public class StaticWatcher extends Watcher {
	public StaticWatcher() {
		super(Watcher.PASSIVE);
	}

	/**
	 *  never expoired
	 * @see org.whitetree.lab.system.Watcher#isExpired()
	 */
	@Override
	public boolean isExpired() {
		return false;
	}
	
	@Override
	protected String formatted(Walue _) {
		//return (_ == null) ? "NOTUSED" : String.format("%d", _.getLong());
		return "";
	}
}
