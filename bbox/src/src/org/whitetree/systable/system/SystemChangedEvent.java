package org.whitetree.systable.system;


import client.core.model.Event;
import client.core.model.Notifiers;


public class SystemChangedEvent extends Event {
	public String id   = "";
	public Walue walue = null;
	
	public SystemChangedEvent(String id, Walue walue) {
		this.id     = id;
		this.walue = walue;
		setTo(new Notifiers("ui"));
	}
	
	@Override
	public String toString() {
		// TODO: print funcs
		return String.format("(SystemChangedEvent :id '%s' :watcher %s (%s))", id, walue == null ? "null" : walue.toString(), super.toString());
	}
}