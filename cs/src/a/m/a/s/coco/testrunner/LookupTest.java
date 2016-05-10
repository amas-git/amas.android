package a.m.a.s.coco.testrunner;

import java.net.URI;
import java.util.HashMap;
import java.util.Vector;

import a.m.a.s.coco.TimeStamp;
import a.m.a.s.coco.algorithm.MurmurHash;

public class LookupTest {

	public static void runTest() {
		//t1(10000);
		//t1(10000);
		hashTest(10000);
	}
	
	public static void t1(int max) {
		TimeStamp ts = new TimeStamp("容器测试");
		ts.put("map.new");
		HashMap<String, String> table = new HashMap<String,String>();
		ts.put("map.new.done");
		
		ts.put("map.put");
		for (int i = 0; i < max; ++i) {
			String k = "1234567890"+i;
			table.put(k, k);
		}
		ts.put("map.put.done : " + max);
		for (int i = 0; i < max; ++i) {
			table.get( "1234567890"+i);
		}
		ts.put("map.lookup.done : " + max);
		
		table.clear();
		
		ts.put("vector.new");
		Vector<String> v = new Vector();
		ts.put("vector.put");
		for (int i = 0; i < max; ++i) {
			String k = "1234567890"+i;
			v.add(k);
		}
		ts.put("vector.put.done : " + max);
		
		for (int i = 0; i < max; ++i) {
			v.get(i);
		}
		ts.put("vector.put.lookup : " + max);
		
		ts.dump();
	}
	
	public static void hashTest(int max) {
		TimeStamp ts = new TimeStamp("HASH TEST");
		ts.put("murmurhash.start : " + max);
		for (int i = 0; i < max; ++i) {
			String k = "1234567890"+i;
			k.hashCode();
			MurmurHash.hash32(k);
		}
		ts.put("murmurhash.start end");
		
		
		for (int i = 0; i < max; ++i) {
			String k = "1234567890"+i;
			k.hashCode();
		}
		ts.put("uri.hash");
		URI uri = URI.create("12345678");
		for (int i = 0; i < max; ++i) {
			String k = "1234567890"+i;
			k.hashCode();
		}
		ts.put("uri.hash."+max);
		
		ts.dump();
	}
}
