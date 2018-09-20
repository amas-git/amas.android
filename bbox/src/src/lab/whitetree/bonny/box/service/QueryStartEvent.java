package lab.whitetree.bonny.box.service;

import org.whitetree.systable.system.SystemChangedEvent;

public class QueryStartEvent extends SystemChangedEvent {
	String mQueryId = null;
	
	public QueryStartEvent(String queryId) {
		super("system/query", null);
		mQueryId = queryId;
	}
	
	@Override
	public String toString() {
		return String.format("(%s system/query :queryId %s)", getClass().getName(), mQueryId);
	}
	

	public String getQueryId() {
	    return mQueryId;
    }
}
