package client.core.model.project;

import client.core.model.Event;
import client.core.model.Task;

public class Launcher {
	boolean  mIsClosed = false;
	Class<? extends Event>  mClazz    = null;
	
	public Launcher() {
		
	}
	
	public Launcher(Class<? extends Event> trigger) {
		mClazz = trigger;
	}

	public Class<? extends Event> getTriggerEvent() {
		return mClazz;
	}
	
	// 如果返回空，则表示没有继续提交的任务
	public Task tryLaunch(Event event) {
		return null;
	}
	
	public boolean isClosed() {
		return mIsClosed;
	}
	
	public void close() {
		mIsClosed = true;
	}
	
	public void open() {
		mIsClosed = false;
	}
	
	@Override
	public String toString() {
		return String.format("(launcher :trigger '%s' :opened '%s')", mClazz, !mIsClosed);
	}
	
}
