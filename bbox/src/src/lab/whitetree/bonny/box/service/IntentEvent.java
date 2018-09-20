
package lab.whitetree.bonny.box.service;

import java.util.HashMap;

import client.core.model.Event;

public class IntentEvent extends Event {
	protected HashMap<String, Object> mData;
	
	public void put(final String key, Object o) {
		mData.put(key, o);
	}
	
	public HashMap<String, Object> getAll() {
		return mData;
	}
}
