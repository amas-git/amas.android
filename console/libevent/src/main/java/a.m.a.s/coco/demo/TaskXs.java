package a.m.a.s.coco.demo;

import java.util.ArrayList;
import java.util.List;

import a.m.a.s.coco.Coco;
import a.m.a.s.coco.CocoEvent;
import a.m.a.s.coco.CocoTask;
import a.m.a.s.coco.TimeStamp;
import a.m.a.s.coco.algorithm.MurmurHash;
import a.m.a.s.coco.old.ICocoEventListener;
import a.m.a.s.coco.reflection.ASM;
import a.m.a.s.cs.CS;

class HashTask extends CocoTask{
	public HashTask(int i) {
		this.i = i;
	}
	
	int i = 0;
	List<String> xs = new ArrayList<String>(); 
	TimeStamp ts = null;
	
	public void add(String s) {
		xs.add(s);
	}
	
	public CocoEvent call() {
		ts = new TimeStamp("["+i+"] : " + xs.size());
		for(String x : xs) {
			barrier();
			MurmurHash.hash64(x);
			MurmurHash.hash64(x);
			MurmurHash.hash64(x);
			MurmurHash.hash64(x);
		}
		ts.put("OVER");
		return new HashDoneEvent(ts.getDumpString());
	}


	public int size() {
		return xs.size();
	}
}

class HashDoneEvent extends CocoEvent {
	String message = "";
	public HashDoneEvent(String message) {
		this.message = message;
	}
	
	public String toString() {
		return message;
	}
}

public class TaskXs extends CocoTask implements ICocoEventListener {
	List<CocoTask> tasks = new ArrayList<CocoTask>();
	
	
	
	public void setTask(List<String> xs, int n) {
		ts = new TimeStamp("批量任务: " );
		int groupSize = xs.size() / n;
		
		HashTask subTask = null;
		
		for(int i=0; i<xs.size(); ++i) {
			if(subTask == null || subTask.size() == groupSize) {
				subTask = new HashTask(i);
				subTask.setCtrl(ctrl);
				tasks.add(subTask);
			}
			subTask.add(xs.get(i));
		}
		ts.put("完成任务分解 : " + tasks.size() );
	}
	
	TimeStamp ts = null;
	protected void onPreCall() {
		ts.put("开始执行");
		Coco.getInstance().addEventListener(0, this);
	}

	public CocoEvent call() {
		if(tasks.isEmpty()) {
			return null;
		}
		
		for(CocoTask t: tasks) {
			Coco.getInstance().exec(t);
		}
		return null;
	}

	int finished;
	@Override
	public void onEvent(CocoEvent event) {
		//System.out.println("[I] : " + event);
		if(event instanceof HashDoneEvent) {
			finished++;
			if(finished == tasks.size()) {
				ts.put("---------").dump();
			}
		}
	}
	
	
	public static String rep(int l, String s) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<l; ++i) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	static List<String> source = new ArrayList<String>();
	public static void runTest(int max, int n){
		if (source.isEmpty()) {
			for (int i = 0; i < max; ++i) {
				source.add(rep(1000, "12345678"));
			}
		}
		
		TaskXs task = new TaskXs();
		
		TimeStamp ts = new TimeStamp("findmethods");
		for(int i=0; i< 1000; ++i) {
			//ASM.findMethod(task, "");
		}
		ts.dump();
//		task.setTask(source, n);
//		Coco.getInstance().exec(task);
//		task.pause(8000);
		
	}


}
