package org.whitetree.systable.system;

import android.content.Context;
import android.text.format.DateUtils;
import client.core.Core;
import client.core.model.Notifiers;


public class Watcher {
	public static boolean ENABLE_TIME_TRACE = true;
	public static int PASSIVE = -1;
	
	protected Walue mValue = null;
	
	/**
	 * 需要时加载一次
	 */
	private long   mInterval   =  PASSIVE;
	private long   mLastupdate =  0;
	private long   mCounter    =  0;
	private TimeStamp mTs = ENABLE_TIME_TRACE ? new TimeStamp() : null;
	public Notifiers mEventTo = null;
	protected Context mContext = null;
	private String mId = "";
	
	public String getId() {
		return mId;
	}
	
	protected void setId(String id) {
		mId = id;
    }
	
	public Watcher() {
		mInterval = PASSIVE;
	}
	
	public Watcher(int i) {
		mInterval = i;
	}

	public void setContext(Context context) {
		mContext = context;
	}
	
	/**
	 * 调用update不会立刻产生SystemChangeEvent, 而是由Watcher自行控制，最终将结果发送到mEventTo指定的地方即可
	 * @param to
	 */
	public void setAsynchronousNotifyEvent(Notifiers to) {
		mEventTo = to;
	}
	
	public boolean isAsynchronousNotify() {
		return mEventTo != null;
	}
	
	public void send(SystemChangedEvent event) {
		if(isAsynchronousNotify()) {
			event.setTo(mEventTo);
			Core.I().push(event);
		} else {
			throw new IllegalStateException("only asynchronous watcher can be use send method");
		}
	}
	public void send(SystemChangedEvent event, Notifiers to) {
		event.setTo(to);
		Core.I().push(event);
	}

	public void reset() {
		mValue = null;
		mLastupdate = 0;
	}
	
	/**
	 * It's time to sync ?
	 * @return
	 */
	public boolean isExpired() {
		return mInterval == PASSIVE ? false : (System.currentTimeMillis() - mLastupdate) >= mInterval;
	}
	
	public long getInterval() {
		return mInterval;
	}
	
	/**
	 * Get latest updated value
	 * @return
	 */
	public Walue get(String filterType) {
		if(mValue == null || isExpired()) {
			// TODO: reduce duplicate null check
			mLastupdate = System.currentTimeMillis();
			if(mValue == null) {
				mValue = new Walue();
			}
			
			if(ENABLE_TIME_TRACE) {
				mTs.reset();
			}
			
			// XXX(zhoujb): time trace 是否妥当???
			synchronized (this) {
				update(mContext, mValue, filterType);
			}
			mCounter++;

			
			if(ENABLE_TIME_TRACE) {
				mValue.put(":time-trace", mTs.getLifeTime());
			}
		}
		
		return isAsynchronousNotify() ? null : mValue;
	}
	
	public Walue forceGet() {
		return forceGet(null);
	}
	
	public Walue forceGet(String filterType) {
		if(mValue == null) {
			mValue = new Walue();
		}
		
		synchronized (this) {
			update(mContext, mValue, filterType);
		}
		mCounter++;
		return isAsynchronousNotify() ? null : mValue;
	}
	
	/**
	 * Subclass should override this method to implements update logic 
	 * @param context
	 * @return
	 */
	protected void update(Context context, Walue walue, String filterType) {
		
	}
	
	/**
	 * sync interval
	 * @param msec
	 */
	public void setUpdateInterval(long msec) {
		mInterval = msec;
	}
	
	@Override
	public String toString() {
		int flags = DateUtils.FORMAT_SHOW_DATE 
				   | DateUtils.FORMAT_SHOW_YEAR 
				   | DateUtils.FORMAT_SHOW_TIME 
				   | DateUtils.FORMAT_24HOUR;
		String lastupdate = DateUtils.formatDateTime(mContext, mLastupdate, flags);
		return String.format("(%s :WALUE %s :INTERVAL %d :MTIME %s :COUNTER %d :TS %sms)", 
				getClass().getSimpleName(), formatted(mValue), mInterval, lastupdate, mCounter, ts(mValue));
	}


	/**
	 * add time trace information
	 * @param _
	 * @return
	 */
	private String ts(Walue _) {
		//  _ == null ? "" : String.valueOf(""+_.getLong(":time-trace"));
		return "disabled";
	}


	/**
	 * 重写此函数可定义内部打印格式
	 * @param _
	 * @return
	 */
	protected String  formatted(Walue _) {
		return _ == null ? "" : _.toString();
	}
	
	public static String K(String parent, String child) {
		return String.format("%s.%s", parent, child);
	}


}
