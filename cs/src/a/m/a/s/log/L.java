package a.m.a.s.log;

import java.util.HashMap;

public class L {
	public static boolean ENABLED = true;

	public static boolean DEBUG = true;
	public static final int L1 = 0;
	public static final int L2 = 2;
	public static final int L3 = 4;
	public static final int L4 = 8;
	
	public static final int TAB_WIDTH = 4;
	public static String SUFFIX = ".1";

	public static void setSuffix(String s) {
		SUFFIX = s;
	}

}
