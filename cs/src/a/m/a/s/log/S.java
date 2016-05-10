package a.m.a.s.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class S {
	/**
	 * Repeat s n times
	 * @param s the string
	 * @param n repeat times
	 * @return repeated string
	 */
	public static String repeat(String s, int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; ++i) {
			sb.append(s);
		}
		return sb.toString();
	}

	public static String padding(final String prefix, String text, int level) {
		StringBuilder sb = new StringBuilder();
		String[] xs = text.split("\n");

		String paddings = getPaddingString(" ", level);
		for (String x : xs) {
			sb.append(paddings).append(prefix).append(x).append("\n");
		}
		return sb.toString();
	}

	public static HashMap<String, String> PADDING_CACHE = new HashMap<String, String>();

	public static String getPaddingString(String s, int n) {
		String key = s + "." + n;
		String paddings = PADDING_CACHE.get(key);
		if (paddings == null) {
			paddings = repeat(s, n);
			PADDING_CACHE.put(key, paddings);
		}
		return paddings;
	}
	
	
	/**
	 * 
	 * @param format 格式串, 默认'%5s %s'
	 * @param names 可以为null, 会追加行号
	 * @param xs
	 * @return
	 */
	public static List<String> ADD_LABEL(String format, String[] names,  List<String> xs) {
		StringBuilder sb = new StringBuilder();
		List<String> rs = new ArrayList<String>(xs.size());
		
		for(int i=0; i<xs.size(); ++i) {
			String label = String.valueOf(i);
			if(names  != null && i<names.length && i >= 0) {
				label = names[i];
				if(label.startsWith(".")) {
					continue;
				}
			}
			
			rs.add(String.format(format, label, xs.get(i)));
		}
		return rs;
	}
	

	
	/**
	 * Returns a string containing the tokens joined by delimiters.
	 * 
	 * @param tokens
	 *            an array objects to be joined. Strings will be formed from the
	 *            objects by calling object.toString().
	 */
	public static String join(CharSequence delimiter, Object[] tokens) {
		StringBuilder sb = new StringBuilder();
		boolean firstTime = true;
		for (Object token : tokens) {
			if (firstTime) {
				firstTime = false;
			} else {
				sb.append(delimiter);
			}
			sb.append(token);
		}
		return sb.toString();
	}

	/**
	 * Returns a string containing the tokens joined by delimiters.
	 * 
	 * @param tokens
	 *            an array objects to be joined. Strings will be formed from the
	 *            objects by calling object.toString().
	 */
	public static String join(CharSequence delimiter, Iterable tokens) {
		StringBuilder sb = new StringBuilder();
		boolean firstTime = true;
		for (Object token : tokens) {
			if (firstTime) {
				firstTime = false;
			} else {
				sb.append(delimiter);
			}
			sb.append(token);
		}
		return sb.toString();
	}

}
