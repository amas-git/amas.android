package a.m.a.s.coco;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import a.m.a.s.coco.algorithm.MurmurHash;
import a.m.a.s.coco.testrunner.CountEvent;
import a.m.a.s.cs.CS;

public class SmartEventDispatcher extends BaseDispatcher<CocoEvent> {

	Collection<Object> listeners = new LinkedBlockingQueue<Object>();
	/**
	 * object -> (event.class -> object.method)
	 */
	Map<Object, Map<Class<?>, Method>> route = new ConcurrentHashMap<Object, Map<Class<?>, Method>>();
	
	public SmartEventDispatcher() {

	}

	public static SmartEventDispatcher FIFO() {
		SmartEventDispatcher ed = new SmartEventDispatcher();
		return ed;
	}
	
	public static SmartEventDispatcher LIFO() {
		SmartEventDispatcher ed = new SmartEventDispatcher();
		ed.listeners =  Collections.asLifoQueue(new LinkedBlockingDeque<Object>());
		return ed;
	}
	
	public static SmartEventDispatcher PROI() {
		SmartEventDispatcher ed = new SmartEventDispatcher();
		ed.listeners = new PriorityBlockingQueue<Object>();
		return ed;
	}

	@Override
	public void onDispatch(final CocoEvent elem) {
		 for (Object l : listeners) {
			 Map<Class<?>, Method> ms = route.get(l);
			 if(ms != null) {
				 Method m = ms.get(elem.getClass());
				 callMethods(l, m, elem);
			 }
		 }
	}
	
	public static boolean callMethods(Object o, Method m, Object ...args) {
		 if(m != null && o != null) {
			try {
				m.invoke(o, args);
				return true;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		 }
		return false;
	}

	public boolean register(Object o) {
		Map<Class<?>, Method> ms = searchMethods(o,"onEvent", CocoEvent.class);
		if(!ms.isEmpty() && !listeners.contains(o)) {
			System.out.println(" +++ " + o);
			listeners.add(o);
			route.put(o, ms);
			start();
			return true;
		}
		
		return false;
	}

	public boolean unregister(Object o) {
		route.remove(o);
		listeners.remove(o);
		System.out.println(" ---  " + o);
		return false;
	}
	
	public Map<Class<?>, Method> searchMethods(Object o, String perfix, Class<?> superClazz) {
		Map<Class<?>, Method> matched = new HashMap<Class<?>, Method>();
		Class<?> clazz = o.getClass();
		Method[] ms = clazz.getDeclaredMethods();
		String superClassName = superClazz.getName();
		
		for(Method m : ms) {
			if(m.getName().startsWith(perfix)) {
				Class<?>[] params = m.getParameterTypes();
				if(params != null && params.length == 1) {
					Class<?> p0 = params[0];
					if(p0.isPrimitive() /* || TODO: 检测是否为public*/) {
						continue;
					}
					
					Class<?> s0 = p0.getSuperclass();
					
					// TODO: 有更好的方法判判断某个类的基类是某个指定类么
					if(superClassName.equals(s0.getName())) {
						matched.put(p0, m);
					}
				}
			}
		}
		return matched;
	}
	
	
	static class C1 {
		int i = 0;
		TimeStamp ts = null;
		public void onEvent() {
			
		}
		
		public void onEvent(CocoEvent e, int i) {
			System.out.println("ERROR 1");
		}
		
		public void onEvent_1(CocoEvent e) {
			System.out.println("ERROR 2");
		}
		
		public void onEvent(CountEvent e) {
			i++;
			if(e.isFirst()) {
				ts = new TimeStamp("C1");
			}
			
			if(e.isLast()) {
				//ts.put("-------------").dump();
			}
			
			MurmurHash.hash64("ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"+i);
		}
	}
	
	static class C2 {
		TimeStamp ts = null;
		int i = 0;
		public void onEvent() {
			
		}
		
		public void onEvent_1(CocoEvent e, int i) {
			
		}
		public void onEvent_1(CocoEvent e) {
			System.out.println("onEvent_1");
		}
		
		public void onEvent(CountEvent e) {
			//System.out.println(e);
			i++;
			if(e.isFirst()) {
				ts = new TimeStamp("C2");
			}
			
			if(e.isLast()) {
				//ts.put("-------------").dump();
			}
			MurmurHash.hash64("ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"+i);
			//System.out.println(" C2 -->" + e);
		}
	}
	
	public static void runTest(final int m, final int max) {
		final SmartEventDispatcher sed1 = LIFO();
		final SmartEventDispatcher sed2 = LIFO();
		//sed1.degrade();
		//sed1.degrade();

		sed2.promote();
		sed2.promote();
		final C1 c1 = new C1();
		final C2 c2 = new C2();
		
//		TimeStamp ts = new TimeStamp();
//		ts.put("methods.search"+max);
		

		sed1.register(c1);
		sed1.register(c2);
		sed2.register(c1);
		sed2.register(c2);
		
		for(int i=0; i<max; ++i) {
			sed1.push(new CountEvent(i, max));
			CS.safeSleep(10);
			sed2.push(new CountEvent(i, max));
		}
		

		
		sed1.dump();
		sed2.dump();
	}
}
