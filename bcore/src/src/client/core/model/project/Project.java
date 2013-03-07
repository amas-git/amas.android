package client.core.model.project;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import client.core.Core;
import client.core.model.Event;
import client.core.model.EventHandler;
import client.core.model.EventListener;
import client.core.model.Notifiers;
import client.core.model.Task;
import client.core.model.TimeStamp.Tag;

/** 
 * A project contains some tasks,
 * it keeps these tasks be processed by workers as
 * expected order. 
 * 
 * Each task in project called a subtask, and a todo item define how to create it.
 * 1. each project will create a new listener group for push & listen events.
 * 2. once the project finished or canceled, the listener group will be removed.
 * @author amas
 * TODO: maybe we do not need remove task and exe one by one, how to avoid 
 * relocated the duplicate project ???
 */
public abstract class Project extends Task implements EventListener, ISubscribable {
	ConcurrentHashMap<Class<?  extends Event>, Launcher>     mLaunchers = new ConcurrentHashMap<Class<? extends Event>, Launcher>();
	ConcurrentHashMap<Class<?  extends Event>, EventHandler> mEh        = new ConcurrentHashMap<Class<? extends Event>, EventHandler>();
	
	public enum Status {
		Ready,
		Running,
		Stop,
		Done,
		Idle
	}
	
	Status mStatus = Status.Ready;
	
	public synchronized Status getStatus() {
		return mStatus;
	}
	
	public synchronized void setStatus(Status status) {
		mStatus = status;
	}
		
	/**
	 * project开始执行之事件，仅用于告知外界， 是否有必要???
	 * @author amas
	 *
	 */
	public class StartEvent extends Event {
		public StartEvent() {
			setFrom(getId());
		}
	}
	
	/**
	 * A project started
	 * @author amas
	 *
	 */
	public class EndEvent  extends Event {
		public EndEvent() {
			setFrom(getId());
		}
	}
	
	public class StartLauncher extends Launcher {
		@Override
		public Class<? extends Event> getTriggerEvent() {
			return StartEvent.class;
		}
		
		public Task tryLaunch(Event event) {
			mTimeStamp.touch(Tag.START_TIME);
			setStatus(Status.Running);
			onStart();
			return null;
		}
	}
	

	public Project(String id) {
		setId(id);
		
		// 设定产生事件的去向
		setTo(new Notifiers(getSelfListenerGroup()));
		// 开始
		addLauncher(new StartLauncher());
	}
	
	public void addLauncher(Launcher launcher) {
		Core.LOG.d(String.format("PROJECT.PUSH@%s", getUid()), launcher.toString());
		mLaunchers.put(launcher.getTriggerEvent(), launcher);
	}
	
	public void replaceLauncher(Launcher launcher) {
		
	}
	
	/* The project task start here
	 * @see client.core.model.Task#process()
	 */
	@Override
	final protected Event process() {
		// 监听自己的事件
		Core.I().addListener(getSelfListenerGroup(), this);
		return new StartEvent();
	}
	
	/**
	 * 在这里开始执行
	 */
	protected void onStart() {
		
	}
	
	protected void onStop() {
		
	}
	
	protected void addEventHandler(Class<? extends Event> eventClazz, EventHandler handler) {
		mEh.put(eventClazz, handler);
	}
	
	protected void removeEventHandler(Class<? extends Event> eventClazz) {
		Object o = mEh.remove(eventClazz);
		Core.LOG.d(String.format("PROJECT.-HAND@%s", getUid()), o.toString());
	}
	
	/**
	 * 执行子任务，任务产生的事件报告到Project的ListenerGroup中
	 * @param task
	 */
	public void exec(Task task) {
		Core.LOG.d(String.format("PROJECT.EXEC@%s", getUid()), task.toString());
		task.setTo(getTo());
		Core.I().exec(task);
	}
	
	final public void onEvent(Event event) {
		Core.LOG.d(String.format("PROJECT.RECV@%s", getUid()), event.toString());
		EventHandler eh = mEh.get(event.getClass());
		if(eh != null) {
			eh.onHandleEvent(event);
		}
		
		Launcher launch = mLaunchers.get(event.getClass());
		if(launch != null && !launch.isClosed()) {
			Task nextTask = launch.tryLaunch(event);
			if(nextTask!=null) {
				nextTask.setTo(getTo());
				Core.I().exec(nextTask);
			}
		}
	}
	
	/**
	 * 工程结束,清理，发送 {@link EndEvent}
	 */
	public void finish() {
		onStop();
		setStatus(Status.Done);
		mTimeStamp.touch(Tag.END_TIME);
		Event e = new EndEvent();
		e.setDesc("time="+mTimeStamp.getLifeTimeSec());
		Core.I().removeListener(getSelfListenerGroup(), this);
		pushEvent(e);
		Core.LOG.d(String.format("PROJECT._FIN@%s", getUid()), "FINISHED");
	}

	protected String getSelfListenerGroup() {
		return "lg://"+getUid();
	}
		
	/** push event to the private listener group */
	protected void pushEvent(Event event) {
		if(event == null) {
			return;
		}
		touch(event);
		Core.I().push(event);
	}
	
	/**
	 * The result event to
	 * TODO: doc
	 * @param event
	 * @return
	 */
	protected boolean touch(Event event) {
		if(getTo() == null || event == null) {
			return false;
		}
		
		event.setTo(getTo());
		return true;
	}
	
	/** 
	 * 添加事件投递到指定监听组
	 * @see client.core.model.project.ISubscribable#subscribeTo(java.lang.String)
	 */
	public void addTo(String listenerGroupUri) {
		getTo().addNotifyUri(listenerGroupUri);
	}
	
	public String toString() {
		return String.format("(:project %s :status %s)", super.toString(), getStatus());
	}
	
	/**
	 * 重置launcher,以及project状态
	 */
	public void reset() {
		// 重置Launcher
		Iterator<Entry<Class<? extends Event>, Launcher>> iter = mLaunchers.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Class<? extends Event>, Launcher> entry = iter.next();
			Launcher l = entry.getValue();
			if(l != null) {
				l.open();
			}
		}
		
		setStatus(Status.Ready);
		// 监听自己的事件
		Core.I().addListener(getSelfListenerGroup(), this);
	}
}
