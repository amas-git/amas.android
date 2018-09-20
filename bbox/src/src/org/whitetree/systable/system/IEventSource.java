package org.whitetree.systable.system;


public interface IEventSource {
	public SystemChangedEvent getEvent(String key, boolean forceQuery, String filterType);
	public void onCreate();
	public void onDestroy();

	public void subscribe(String key);
	public void unsubscribe(String key);
}
