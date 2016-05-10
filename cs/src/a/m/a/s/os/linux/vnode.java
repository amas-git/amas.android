package a.m.a.s.os.linux;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * 时间从T1 -> T2, 对应的一组状态的集合从S1 -> S2, vnode主要是魏了方便观察这种变化
 *  1. 一些数据是永远不发生变化的
 *  2. 一些数据做累加
 *  3. 一些数据做减法
 *  4. 
 * <ID>:<TYPE>:<OP>
 * <TYPE>
 *   * int
 *   * long
 *   * short
 *   * byte
 *   * String
 *   * boolean
 *   * 
 * @author amas
 *
 */
public class vnode {

	public static vnode createFromFile(rules r, File source) {
		try {
			return createFromScanner(new Scanner(source), r);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static vnode createFromString(rules r, String content) {
		return createFromScanner(new Scanner(content), r);
	}
	
	private static vnode createFromScanner(Scanner s, rules r) {
		vnode node = new vnode();
		node.r = r;
		
		try {
			int i = 0;
			rules_item ritem = null;
			
			while (s.hasNext()) {
				ritem = r.get(i);
				
				if(ritem == null) {
					break;
				}
				
				if(ritem.skip()) {
					++i;
					continue;
				}
				
				if(ritem.type == Integer.class) {
					node.addInteger(ritem.name, s.nextInt());
				} else if(ritem.type == Long.class) {
					node.addLong(ritem.name, s.nextLong());
				} else {
					node.addString(ritem.name, s.next());
				}
				++i;
			}
		} finally {
			if (s != null) {
				s.close();
			}
		}
		return node;
	}
	
	enum op {
		NOT_CHANGED,
		INC, // 累加
		DEC, // 减
		REP  // 替换,总是用最新的
	}
	
	public static class rules_item {
		public rules_item(String name, a.m.a.s.os.linux.vnode.op op, Class<?> type) {
			this.name = name;
			this.op = op;
			this.type = type;
		}
		
		public boolean skip() {
			return name.startsWith(".");
		}

		op op = a.m.a.s.os.linux.vnode.op.REP;
		Class<?> type = String.class;
		String name = "";
		
		@Override
		public String toString() {
			return String.format("[RULE %s %s %s]", name, type, op);
		}
	}
	
	public static class rules {
		List<rules_item> data = new ArrayList<rules_item>();
		public rules notchanged(String key) {
			data.add(new rules_item(key, op.NOT_CHANGED, String.class));
			return this;
		}
		
		public rules notchanged(String key, Class<?> type) {
			data.add(new rules_item(key, op.NOT_CHANGED, type));
			return this;
		}


		public rules inc(String key, Class<?> type) {
			data.add(new rules_item(key, op.INC, type));
			return this;
		}
		
		public rules dec(String key, Class<?> type) {
			data.add(new rules_item(key, op.DEC, type));
			return this;
		}
		
		public rules uselatest(String key, Class<?> type) {
			data.add(new rules_item(key, op.REP, type));
			return this;
		}
		
		public rules_item get(int i) {
			if(i < 0 || i >= data.size()) {
				return null;
			}
			return data.get(i);
		}
		
		public int size() {
			return data.size();
		}
	}
	
	Map<String,Object> chunk = new TreeMap<String, Object>();
	rules r = null;
	
	public vnode changeTo(vnode newState) {
		return null;
	}
	
	public vnode addInteger(String key, int i) {
		chunk.put(key, i);
		return this;
	}
	
	public vnode addString(String key, String s) {
		chunk.put(key, s);
		return this;
	}
	
	public vnode addLong(String key, long l) {
		chunk.put(key, l);
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for(String k: chunk.keySet()) {
			Object v = chunk.get(k);
			sb.append(String.format("%-15s %s", k, v)).append("\n");
		}
		return sb.toString();
	}

}
