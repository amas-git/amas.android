package a.m.a.s.cs.reflection;

import java.lang.reflect.Field;

public class Boom {
	private String text = "hello";

	public Boom(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
	
	public static void boom() {
		Boom boom = new Boom("Hello, major tom");
		try {
			Field f = boom.getClass().getDeclaredField("text");
			f.setAccessible(true);
			try {
				f.set(boom, "fuck you!!!");
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		System.out.println(boom);
		int N = Runtime.getRuntime().availableProcessors();
		System.out.println("CPU CORE = " + N);
	}
	
}
