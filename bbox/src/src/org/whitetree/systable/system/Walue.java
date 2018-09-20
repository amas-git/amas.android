package org.whitetree.systable.system;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Walue extends ConcurrentHashMap<String, Object>{
	private static final long serialVersionUID = -733296968358832650L;

	public Walue() {
	
	}
	
	public long getLong(String key) {
		//return (Long)mComplex.get(key);
		return (Long)get(key);
	}
	
	public boolean getBoolean(String key) {
		return (Boolean)get(key);
	}

	public int getInt(String key) {
		return (Integer)get(key);
	}
	
	public String getString(String key) {
		return (String)get(key);
	}

	public  List<?> getAsList(String key)  {
		return (List<?>)get(key);
	}
	
	public Map<?, ?> getAsMap(String key) {
		return (Map<?, ?>)get(key);
	}

	public double getDouble(String key) {
	    return (Double)get(key);
    }
}
