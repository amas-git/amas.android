package client.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

import client.core.model.Dispatcher;
import client.core.model.Event;
import client.core.model.Task;


/**
 * <p> Task manager, singleton pattern </p>
 * @author amas
 */
public class TaskManager {
	private static TaskManager     sInstance                        = new TaskManager();
	
	private BlockingQueue<Task>    mTaskQueue                       = new LinkedBlockingQueue<Task>(Core.TM_QUEUE_INIT_SIZE);
	private BlockingQueue<FutureTask<Event>> mPendingEventQueue     = new LinkedBlockingQueue<FutureTask<Event>>();
	private TaskDispather          mDispather                       = new TaskDispather(mTaskQueue);
	private PendingEventDispatcher mPendingEventDispatcher          = new PendingEventDispatcher(mPendingEventQueue);
	private ExecutorService        mTaskWorkers                     = Executors.newFixedThreadPool(Core.TM_WORKERS); // TODO: reuse dispatcher workers  
	
	
	class TaskDispather extends Dispatcher<Task> {
		public TaskDispather(BlockingQueue<Task> queue) {
			mQueue   = queue;
		    mWorkers = Executors.newFixedThreadPool(Core.TM_DIPATCHER_WORKERS); 	
		    setName("TaskDispather");
		}
				
		@Override
		public Event onDispatch(Task elem) {
			FutureTask<Event> ftask = new FutureTask<Event>(elem); 
			watch(ftask);
			mTaskWorkers.submit(ftask);
			
/*			try {
				EventManager.I().push(ftask.get());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			return null; /* we do not need notify anything */
		}
	}
	
	class PendingEventDispatcher extends Dispatcher<FutureTask<Event>> {
		public PendingEventDispatcher(BlockingQueue<FutureTask<Event>> queue) {
			mQueue   = queue;
			mWorkers = Executors.newFixedThreadPool(1); 
			setName("PendingEventDispatcher");
		}
		
		@Override
		public Event onDispatch(FutureTask<Event> ftask) {
			try {
				Event event = ftask.get();
				EventManager.I().push(event);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
			return null; /* we do not need notify anything */
		}
	}
		
	private TaskManager() {
		mDispather.start();
		mPendingEventDispatcher.start();
	}
	
	/** short for getInstance() */
	public static TaskManager I() {
		return  sInstance;
	}
	
	public void exec(Task task) {
		mDispather.assign(task);
	}
	
	public void watch(FutureTask<Event> ftask) {
		mPendingEventDispatcher.assign(ftask);
	}
	
/*	public synchronized void restart() {
		
	}*/
	
/*	public void clear() {
		mTaskQueue.clear();
	}*/
}
