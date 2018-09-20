package org.whitetree.systable.data;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

public class Application implements Comparable<Application> {
	private static ActivityManager mActivityManager;
	private static PackageManager mPackageManager;
	
	public String label;
	private Drawable icon;
    private int memory;
	public boolean isChecked = false;
	public String packageName;
	public String processName;
	public int importance;
	public int pid;
	public int uid;


	public int compareTo(Application app) {
		return packageName.compareTo(app.packageName);
	}
	
	
	
	public Application() {
    }

	public Application(String packageName) {
	    this.packageName = packageName;
    }
	

    public int getMemory(Context context) {
        if(memory > 0){
            return memory;
        }
        if(context == null){
            return 0;
        }
        if(mActivityManager == null){
            mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        MemoryInfo [] memoryInfos = mActivityManager.getProcessMemoryInfo(new int[]{this.pid});
        this.memory =  memoryInfos[0].getTotalPss() * 1000;

        return memory;
    }
    
    public void setMemory(int memory) {
        this.memory = memory;
    }


    public Drawable getIcon(Context context) {
        if(this.icon != null){
            return this.icon;
        }
        
        if(context == null){
            return null;
        }
        
        if(mPackageManager == null){
            mPackageManager = context.getPackageManager();
        }
        try {
            this.icon = mPackageManager.getApplicationIcon(this.packageName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }


    public String getLabelName(Context context) {
        if(label != null){
            return label;
        }
        
        if(context == null){
            return null;
        }
        
        if(mPackageManager == null){
            mPackageManager = context.getPackageManager();
        }
        try {
            ApplicationInfo info = mPackageManager.getApplicationInfo(this.packageName, PackageManager.GET_META_DATA);
            label = mPackageManager.getApplicationLabel(info).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            label = packageName;
        }
        
        return label;
    }

    public void setLabelName(String labelName) {
        this.label = labelName;
    }
    
    @Override
	public String toString() {
		return String.format("(:PKG_NAME '%s' :PROC_NAME '%s' :PID %d :SEL_STAT %B :IMPORTANCE '%d' :UID %d )", packageName, 
		        processName, pid, isChecked , importance, uid);
	}
}
