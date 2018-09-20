package a.m.a.s.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Text Utils
 */

public class T {
    /**
     * Repeat s n times
     *
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
     * @param format 格式串, 默认'%5s %s'
     * @param names  可以为null, 会追加行号
     * @param xs
     * @return
     */
    public static List<String> ADD_LABEL(String format, String[] names, List<String> xs) {
        StringBuilder sb = new StringBuilder();
        List<String> rs = new ArrayList<String>(xs.size());

        for (int i = 0; i < xs.size(); ++i) {
            String label = String.valueOf(i);
            if (names != null && i < names.length && i >= 0) {
                label = names[i];
                if (label.startsWith(".")) {
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
     * @param tokens an array objects to be joined. Strings will be formed from the
     *               objects by calling object.toString().
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
     * @param tokens an array objects to be joined. Strings will be formed from the
     *               objects by calling object.toString().
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

    public static String join(char delimiter, Collection<String> values) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (String token : values) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }

    public static boolean isEmpty(String[] target) {
        return target == null || target.length == 0;
    }

    /**
     *
     * @param prefix 要添加的前缀
     * @param split  分隔符
     * @param content 内容
     * @return
     */
    public static String addPrefix(String prefix, String split, char delimiter, String content) {
        ArrayList<String> _xs = new ArrayList();

        String[] xs = content.split(split);
        if(xs != null) {
            for(String x : xs) {
                _xs.add(prefix+x);
            }
            return join(delimiter, _xs);
        }

        return content;
    }

    /**
     * Added timestamp with specify date format for each line
     * @param dateFormat
     * @param content
     * @return
     */
    public static String catd(String dateFormat, String content) {
        String prefix = new SimpleDateFormat(dateFormat).format(new Date(System.currentTimeMillis()));
        return addPrefix(prefix, "\n", '\n', content);
    }

    /**
     * The String is EMPTY or NOT
     * @param text
     * @return
     */
    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    final static Pattern REGEX_TIME_UNIT = Pattern.compile("(\\d+)([hmds]*)");
    /**
     * 1d = 1day 1m=1minutes 1s=1second
     * d: day
     * m: minute
     * s: second
     * M: month
     * y: year
     */
    public static long parseTime(String text) {
        Matcher m = REGEX_TIME_UNIT.matcher(text);
        if(m.matches()) {
            int num = Integer.valueOf(m.group(1));
            String unit = m.group(2);

            int n = 1000;
            if("h".equals(unit)) {
                n = 60 * 60 * 1000;
            } else if("m".equals(unit)) {
                n = 60 * 1000;
            } else if("d".equals(unit)) {
                n = 24 * 60 * 60 * 1000;
            } else {

            }
            return num * n;
        }

        return 0;
    }
}
