package org.whitetree.systable.system.watcher;

import java.util.concurrent.atomic.AtomicInteger;

import org.whitetree.systable.system.IEventSource;
import org.whitetree.systable.system.SystemChangedEvent;
import org.whitetree.systable.system.Watcher;


public class WatcherEventSource extends Watcher implements IEventSource {
	AtomicInteger mRc = new AtomicInteger(0);
	
	@Override
	public SystemChangedEvent getEvent(String key, boolean forceQuery, String filterType) {
		if(forceQuery) {
			forceGet(filterType);
		} else {
			get(filterType);
		}
		if(isAsynchronousNotify()) {
			return null;
		}
		SystemChangedEvent event = new SystemChangedEvent(getId(), mValue);
		return event;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {
		mContext = null;
		mValue = null;
	}

	@Override
	public void subscribe(String key) {
		mRc.getAndIncrement();
	}

	@Override
	public void unsubscribe(String key) {
		mRc.getAndDecrement();
	}
	
	@Override
	public String toString() {
	    return String.format("[%4d] : %s",mRc.get(),super.toString());
	}

}
