package a.m.a.s.coco.old;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;

import a.m.a.s.coco.BaseDispatcher;
import a.m.a.s.coco.CocoEvent;
import a.m.a.s.coco.testrunner.CountEvent;
import a.m.a.s.coco.testrunner.TestListener;
import a.m.a.s.cs.CS;

public class EventDispatcher extends BaseDispatcher<CocoEvent> {
	private static final int DEFAULT_SIZE = 20;
	
	Collection<ICocoEventListener> listeners = new LinkedBlockingDeque<ICocoEventListener>(DEFAULT_SIZE);

	public EventDispatcher() {

	}
	
	public static EventDispatcher FIFO() {
		EventDispatcher ed = new EventDispatcher();
		return ed;
	}
	
	public static EventDispatcher LIFO() {
		EventDispatcher ed = new EventDispatcher();
		ed.listeners =  Collections.asLifoQueue(new LinkedBlockingDeque<ICocoEventListener>(DEFAULT_SIZE));
		return ed;
	}
	
	public static EventDispatcher PROI() {
		EventDispatcher ed = new EventDispatcher();
		ed.listeners = new PriorityBlockingQueue<ICocoEventListener>(DEFAULT_SIZE);
		return ed;
	}

	@Override
	public void onDispatch(CocoEvent elem) {
		// FIXME: if remove element on dispatch will case exception
		for(ICocoEventListener l : listeners) {
			l.onEvent(elem);
		}
//		Iterator<ICocoEventListener> iterator = listeners.iterator();
//		
//		ICocoEventListener l = null;
//		while((l = iterator.next()) != null) {
//			l.onEvent(elem);
//		}
	}

	public void addEventListener(ICocoEventListener listener) {
		if(listeners.contains(listener)) {
			return;
		}
		listeners.add(listener);
	}
	
	public boolean removeEventListener(ICocoEventListener listener) {
		return listeners.remove(listener);
	}
	
	
	
	
	public static void runTest() {
		final EventDispatcher ed = new EventDispatcher();
		
		ed.addEventListener(new TestListener("1") );
		ed.addEventListener(new TestListener("2") );
		ed.addEventListener(new TestListener("3") );
		ed.addEventListener(new TestListener("4") );
		ed.addEventListener(new TestListener("5") );
		
		
		
		for(int i=0; i<10; ++i) {
			System.out.println(" + " + i);
			ed.push(new CountEvent(i, 10));
		}
		
		new Thread() {
			public void run() {
				while(true) {
					ed.push(new CountEvent(1, 10));
					CS.safeSleep(500);
				}
			};
		}.start();
		
	}
}
