package a.m.a.s.coco;

import a.m.a.s.coco.old.EventDispatcher;

public abstract class CocoEvent extends CocoObject {
	int did = 0;
	int from = 0;

	final public void send(EventDispatcher ds) {
		if (ds == null) {
			return;
		}
		ds.push(this);
	}

	final public void send(int did) {
		send(Coco.getInstance().getEventDispatcher(did));
	}

	final public void send() {
		send(this.did);
	}

	@Override
	public String toString() {
		return String.format("(:did %d %s)",did,super.toString());
	}

	public void from(int from) {
		this.from = from;
	}
}
