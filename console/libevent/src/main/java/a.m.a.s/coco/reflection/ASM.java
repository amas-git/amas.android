package a.m.a.s.coco.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

class BASE {
	private int base_i = 0;

	public BASE() {
		
	}
	
	private BASE(int i) {
		this.base_i = i;
		System.out.println("BASE() = " + i);
	}
	
	public int supper_sum(int x) {
		System.out.println("CALL supper_sum");
		return x;
	}
}
class SAMPLE extends BASE {
	public int _int = 1;
	public long _long = 1;
	private short _short = 1;
	private String _string = "hello";
	private String[] _string_array  = new String[] {"a", "b", "c"};
	
	private static int _static_int = 100;
	private static final int _static_final_int = 100;
	
	public SAMPLE() {
		ASM.SUPER(this, new Class[] {int.class}, 9887);
	}
	
	private void update(int _int, String _string) {
		this._int = _int;
		this._string = _string;
	}
	
	public int add(int x, int y) {
		return x + y;
	}
	
	public void call_void() {
		System.out.println("call...");
	}
	
	public void static_echo(String[] s) {
		for(String _s:s) {
			System.out.println(_s);
		}
	}
	
	public static List<String> merge_list(String[] xs, String[] ys) {
		ArrayList<String> rs = new ArrayList<String>();
		HashSet<String> s = new HashSet<String>();
		
		for(String x: xs) {
			s.add(x);
		}
		
		for(String y: ys) {
			s.add(y);
		}
		
		rs.addAll(s);
		return rs;
	}
	
}

public class ASM {
	public static boolean DEBUG = true;

	public static Object CALL(Object o, String function_name, Class<?>[] args_type, Object ...args) {
		return CALL(o.getClass(), o, function_name, args_type, args);
	}
	
	public static Object CALL(Class<?> c, String function_name, Class<?>[] args_type, Object ...args) {
		return CALL(c, null, function_name, args_type, args);
	}
	
	private static Object CALL(Class<?> c, Object o, String function_name, Class<?>[] args_type, Object ...args) {
		Object r = null;
		try {
			Method m = c.getDeclaredMethod(function_name, args_type);

			if(m != null) {
				if(!m.isAccessible()) {
					m.setAccessible(true);
				}
				if(DEBUG) m("CALL : " + m.toGenericString() + " with " + args);
				r = m.invoke(o, args);
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;
	}
	
	public static String[] FOR_CLASS_NAME(Class<?>[] clazz) {
		String[] xs = new String[clazz.length];
		int i=0;
		for(Class<?> c : clazz) {
			xs[i] = c.getSimpleName();
			++i;
		}
		return xs;
	}
	
	/**
	 * 
	 * @param o The object to search 
	 * @param perfix the target method prefix
	 * @param clazz
	 * @return
	 */
	public static Vector<Method> searchMethods(Object o, String perfix, Class<?> clazz) {
		Vector<Method> matched = new Vector<Method>(0);
		Method[] ms = o.getClass().getDeclaredMethods();
		String superClassName = clazz.getName();
		
		for(Method m : ms) {
			if(m.getName().startsWith(perfix)) {
				Class<?>[] params = m.getParameterTypes();
				if(params != null && params.length == 1) {
					Class<?> p0 = params[0];
					if(p0.isPrimitive()) {
						continue;
					}
					
					Class<?> s0 = p0.getSuperclass();
					
					// TODO: 有更好的方法判判断某个类的基类是某个制定类么
					if(superClassName.equals(s0.getName()) || superClassName.equals(p0.getName())) {
						matched.add(m);
					}
				}
			}
		}
		return matched;
	}
	
	
    public static boolean ARRAY_EQ(Object[] a1, Object[] a2) {
        if (a1 == null) {
            return a2 == null || a2.length == 0;
        }

        if (a2 == null) {
            return a1.length == 0;
        }

        if (a1.length != a2.length) {
            return false;
        }

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) {
                return false;
            }
        }

        return true;
    }
	
	public static Object GET_FIELD(Object o, String name, Object o_failed) {
		return GET_FIELD(o.getClass(), o, name, o_failed);
	}
	
	public static Object GET_FIELD(Class<?> c, String name, Object o_failed) {
		return GET_FIELD(c, null, name, o_failed);
	}
	
	private static Object GET_FIELD(Class<?> clazz, Object o, String name, Object o_failed) {
		Field  f = null;
		Object r = null;
		try {
			f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			if(f != null) r = f.get(o);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		r = r == null ? o_failed : r;
		
		if(DEBUG) m("GET_FIELD : O="+clazz.getSimpleName() +", name="+name+",value="+r);
		return r;
	}
	
	public static void SET_FIELD(Object o, String name, Object value) {
		Field  f = null;
		Object r = null;
		try {
			f = o.getClass().getDeclaredField(name);
			f.setAccessible(true);
			if(f != null) f.set(o, value);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		if(DEBUG) m("SET_FIELD : O="+o+", name="+name+",value="+r);
	}
	
	public static void DUMP(Object[] ox) {
		for(Object o: ox) {
			m(String.valueOf(o));
		}
	}
	
	public static void SUPER(Object o, Class<?>[] args_type, Object ...args) {
		Object r = null;
		try {
			Class<?> csupser = o.getClass().getSuperclass();
			if(csupser == null) {
				return;
			}
			
			Constructor<?> c = csupser.getDeclaredConstructor(args_type);
			if(c != null) {
				if(!c.isAccessible()) {
					c.setAccessible(true);
				}
				r = c.newInstance(args);
			}
			if(DEBUG) m("NEW : " + c + " -> " + r);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}	catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static Object NEW(Class<?> clazz, Class<?>[] args_type, Object ...args) {
		Object r = null;
		try {
			Constructor<?> c = clazz.getDeclaredConstructor(args_type);
			
			if(c != null) {
				if(!c.isAccessible()) {
					c.setAccessible(true);
				}
				r = c.newInstance(args);
				if(DEBUG) m("NEW : " + c + " -> " + r);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}	catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return r;
	}
	
	public static void m(String s) {
		System.out.println(s);
	}

	public static void runTest() {
		SAMPLE s = new SAMPLE();
		ASM.GET_FIELD(s, "_int", null);
		ASM.GET_FIELD(s, "_long", null);
		ASM.SET_FIELD(s, "_string", "The Big World");
		ASM.GET_FIELD(s, "_string_array", null);
		ASM.GET_FIELD(s, "_string", null);
		
		ASM.SET_FIELD(s, "_static_int", 911);
		ASM.GET_FIELD(s, "_static_int", 1);
		
		ASM.GET_FIELD(SAMPLE.class, "_static_int", 1);
		
		// ERROR ASM.SET_FIELD(s, "_static_final_int", 888);
		
		ASM.CALL(s, "update", new Class[] {int.class, String.class}, 999, "fuck");
		ASM.CALL(s, "add", new Class[] {int.class, int.class}, 999, 200);
		ASM.CALL(s, "call_void", new Class[] {});
		
		// CALL STATIC
		ASM.CALL(s, "static_echo", new Class[] { String[].class }, (Object)new String[] {"a", "b", "c"});
		ASM.CALL(s, "static_echo", new Class[] { String[].class }, new Object[]{ new String[] {"a", "b", "c"}});
		
		
		// CALL CONSTRUCTION
		ASM.NEW(String.class, new Class[] {String.class}, "Hello World!");
		
		/* FAILED */
		//ASM.CALL(s, "supper_sum", new Class[] {int.class}, 1);
		ArrayList<String> xs = new ArrayList<String>();
		xs.add("111");
		xs.add("xx");
		xs.add("xx");
		xs.add("999");
		Iterator<String> i = xs.iterator();
		while(i.hasNext()) {
			String x = i.next();
			if(x.equals("xx")) {
				i.remove();
				System.out.println("rm="+x);
			}
		}
		System.out.println("OVER");
	}
}
