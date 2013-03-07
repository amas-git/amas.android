package client.core;

import java.util.concurrent.ConcurrentHashMap;

import client.core.model.project.Project;
import client.core.model.project.Project.Status;

/**
 * 
 * @author amas
 *
 */
public class ProjectManager {
	ConcurrentHashMap<String, Project> mProjects = new ConcurrentHashMap<String, Project>();
	private static ProjectManager sInstance = new ProjectManager();
	
	public  ProjectManager() {
		
	}

	public static ProjectManager I() {
		return sInstance;
	}
	
	public void register(String key, Project project) {
		Project p =  mProjects.get(key);
		if(p == null) {
			mProjects.put(key, project);
		}
	}
	
	public void unregister(String key) {
		mProjects.remove(key);
	}
	
	public Project getProject(String key) {
		Project p = mProjects.get(key);
		return p;
	}
	
	public boolean isDone(String key) {
		Project p = mProjects.get(key);
		return p != null && p.getStatus() == Status.Done;
	}
	
	public void exec(String key) {
		Project p = mProjects.get(key);
		if(p != null) {
			Core.LOG.d("*PM.EXEC", ":project="+p.toString());
			if(p.getStatus() == Status.Done || p.getStatus() == Status.Ready) {
				p.reset();
				Core.I().exec(p);
			}
		}
	}
}
