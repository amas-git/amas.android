package a.m.a.s.coco.algorithm;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class TimeRange {
	
	
	/**
	 * 逗号分割的时间,比如: 20150908,20150401
	 * @param time
	 * @param target
	 * @return
	 */
	public static boolean timeIn(long time, Set<String> target) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(time));
		
		int year  = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day   = c.get(Calendar.DAY_OF_MONTH);
        
		return target.contains(String.format("%4d%02d%02d", year, month, day));
	}
	
	public static boolean timeIn(String target) {
		return timeIn(System.currentTimeMillis(), parseTarget(target));
	}

	private static Set<String> parseTarget(String target) {
		String[] xs = target.split(",");
		HashSet<String> set = new HashSet<String>();
		
		for(String x : xs) {
			set.add(x.trim());
		}
		return set;
	}
}
