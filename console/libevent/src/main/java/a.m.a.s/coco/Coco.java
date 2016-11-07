package a.m.a.s.coco;

import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import a.m.a.s.coco.old.EventDispatcher;
import a.m.a.s.coco.old.ICocoEventListener;

/*
 * 1. 多个目的地址应该可以实用固定的分发器
 */
public class Coco {
	private static Coco sInstance = new Coco();
	//TaskDispatcher tdHigh = new TaskDispatcher();
	ReentrantLock mainlock = new ReentrantLock();
	TaskDispatcher works  = new TaskDispatcher();

	Vector<EventDispatcher> channels = new Vector<EventDispatcher>(3);
	
	public static int DID_MAIN = 0;
	
	Coco() {
		installTaskDispatcher();
		installEventDispatcher();
	}

	private void installTaskDispatcher() {
		
	}

	private void installEventDispatcher() {
		channels.add(EventDispatcher.FIFO());
		channels.add(EventDispatcher.LIFO());
	}
	
	public static Coco getInstance() {
		return sInstance;
	}

	
	int maxEdSize = 10; // to many dispatchers is not a good idea

	public void exec(CocoTask task) {
		works.start();
		works.push(task);
	}
	
	@Deprecated
	public void addEventListener(int did, ICocoEventListener l) {
		EventDispatcher d = getEventDispatcher(did);
		if(d != null) {
			d.start();
			d.addEventListener(l);
		}
	}
	
	@Deprecated
	public boolean removeEventListener(int did, ICocoEventListener l) {
		EventDispatcher d = getEventDispatcher(did);
		if(d != null) {
			return (!d.removeEventListener(l));
		}
		return false;
	}
	
	public void push(CocoEvent event) {
		event.send();
	}

	
	public EventDispatcher getEventDispatcher(int did) {
		if(did >= channels.size()) {
			return null;
		}
		return channels.get(did);
	}
	
	public int nextdid() {
		return 0;
	}
	
	public int addEventDispatcher() {
		int did = -1;
		
		return did;
	}
}
