package a.m.a.s.coco;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TaskDispatcher extends BaseDispatcher<CocoTask>{
	ThreadPoolExecutor workers = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

	@Override
	public void onDispatch(CocoTask elem) {
		// just dispatch the task to works, do not handle any computation in this threading.
		workers.execute(elem);
	}
	
	public void remove() {
		//workers.remove(task)
	}
}
