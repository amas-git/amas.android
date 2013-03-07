package org.whitetree.sched;


public class SchedParser {
	public static final String REGEX_NUMBER    = "[0-9]+";         // OR "\\d+"
	public static final String REGEX_RANGE     = "[0-9]+-[0-9]+";  // OR "\\d+-\\d+"
	public static final String REGEX_STAR      = "\\*";
	public static final String REGEX_ANY       = "^"+REGEX_STAR+"$";
	public static final String REGEX_LIST      = "("+REGEX_NUMBER+","+"|"+REGEX_RANGE+","+")+"+"("+REGEX_NUMBER+"|"+REGEX_RANGE+")";
	public static final String REGEX_REPEAT    = "("+REGEX_NUMBER+"|"+REGEX_STAR+")"+"/"+REGEX_NUMBER+"+";
	public static final String REGEX_SEP       = " ";
	


	
	public static Sched parse(String record) {
		Sched r = new Sched();
		
		String[] columns  = record.split(REGEX_SEP);
		SchedItem item = null;
		
		if(columns.length >= Sched.CRON_ITEM_NUMS) {
			for(int i=0; i<Sched.CRON_ITEM_NUMS; ++i) {
				item = parseItem(columns[i], i);
				r.addItem(item);
			}
		}
		
		// TODO(amas): parse action
		return r;
	}
	
	
	public static SchedItem parseItem(String text, int i) {
		SchedItem item = null;
		switch (i) {
		case Sched.CRON_ITEM_MINUTE_OF_HOUR:
			item = new SchedItemMinute();
			break;
		case Sched.CRON_ITEM_HOUR_OF_DAY:
			item = new SchedItemHourOfDay();
			break;
		case Sched.CRON_ITEM_DAY_OF_MONTH:
			item = new SchedItemDayOfMonth();
			break;
		case Sched.CRON_ITEM_MONTH:
			item = new SchedItemMonth();
			break;
		case Sched.CRON_ITEM_YEAR:
			item = new SchedItemYear();
			break;
		case Sched.CRON_ITEM_DAY_OF_WEEK:
			item = new SchedItemDayOfWeek();
			break;
		default:
			return null;
		}
		
		item.text = text;
		
		if(text.matches(REGEX_LIST)) {
			//_("REGEX_LIST    = "+ text);
			addList(item, text);
		} else if(text.matches(REGEX_RANGE)) {
			//_("REGEX_RANGE   = "+ text);
			addList(item, text);
		} else if(text.matches(REGEX_ANY)) {
			//_("REGEX_ANY     = "+ text);
			item.setWhenever(true);
		} else if(text.matches(REGEX_REPEAT)) {
			//_("REGEX_REPEAT  = "+ text);
			//item.setRepeatInterval(parseRepeateInterval(text));
			addArithmeticProgression(item, text);
		} else if(text.matches(REGEX_NUMBER)) {
			//_("REGEX_NUMBER  = "+ text);
			addList(item, text);
		} else {
			throw new IllegalArgumentException("Unknow token : " + text);
		}
		
		return item;
	}
	
	public static void addList(SchedItem item, final String listNotion) {
		String[] xs = listNotion.split(",");
		
		int N = xs.length;
		
		for(int i=0; i<N; ++i) {
			if(xs[i].matches(REGEX_RANGE)) {
				String[] range = xs[i].split("-"); 
				item.addRange(Integer.valueOf(range[0]), Integer.valueOf(range[1]));
			} else {
				item.add(Integer.valueOf(xs[i]));
			}
		}
	}
	
	/**
	 * @param item
	 * @param apNotion e.g: 1/5,
	 */
	public static void addArithmeticProgression(SchedItem item, final String apNotion) {
		String [] xs = apNotion.split("/");
		
		int start = "*".equals(xs[0]) ? 0 : Integer.valueOf(xs[0]);
		int cd    = Integer.valueOf(xs[1]);
		
		for(int i=start; i<item.getRangeMax(); i+=cd) {
			item.add(i);
		}
	}
	
	public static int parseRepeateInterval(String text) {
		String interval = text.replace("*/", "");
		return Integer.valueOf(interval);
	}
	
	public static void _(String text) {
		System.out.println("[MATCH] : " + text);
	}
}
