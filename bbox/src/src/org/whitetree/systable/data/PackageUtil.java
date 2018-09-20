package org.whitetree.systable.data;

import java.util.ArrayList;
import java.util.List;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

public class PackageUtil {
	
	public static List<ResolveInfo> getAllApp(Context context){
		PackageManager pm = (PackageManager)context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN , null);  
        intent.addCategory(Intent.CATEGORY_LAUNCHER);  
          
        //如何条件的查出来  
        return pm.queryIntentActivities(intent, 0);  
	}
	
	public static List<ResolveInfo> getSelfStartApp(Context context){
		PackageManager pm = (PackageManager)context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN , null);  
        intent.addCategory(Intent.CATEGORY_LAUNCHER);  
        List<ResolveInfo> resolves =pm.queryIntentActivities(intent, 0);  
        
        List<ResolveInfo> resolveSelf = new ArrayList<ResolveInfo>();
        
        for(ResolveInfo info : resolves){
        	String pkgName = info.activityInfo.packageName;
        	PackageInfo pkgInfo = null;
			try {
				if(pkgName.contains("com.android")){
					continue;
				}
				pkgInfo = pm.getPackageInfo(pkgName,  
				        PackageManager.GET_PERMISSIONS);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//通过包名，返回包信息  
            String sharedPkgList[] = pkgInfo.requestedPermissions;//得到权限列表  
            if(sharedPkgList != null && sharedPkgList.length != 0){
            	 for(String s : sharedPkgList){
                 	if(s.equals(permission.RECEIVE_BOOT_COMPLETED.toString())){
                 		resolveSelf.add(info);
                 	}
                 }
            }
        }
		
		return resolveSelf;
		
	}
	
	/**
     * 是否安装了AndroidMarket
     * @param context
     * @return true ? installed : uninstalled
     */
    public static  boolean isMarketInstalled(Context context) {                                                                                                                                                 
        PackageManager localPackageManager = context.getPackageManager();
        try 
        {   
          localPackageManager.getPackageInfo("com.android.vending", 0); 
          return true;
        }   
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {   
            return false;
        }   
    }
    
    
    /**
     * 是否安装了Gmail
     * @param context
     * @return true ? installed : uninstalled
     */
    public static  boolean isGmailInstalled(Context context) {                                                                                                                                                 
        PackageManager localPackageManager = context.getPackageManager();
        try 
        {   
          localPackageManager.getPackageInfo("com.google.android.gm", 0); 
          return true;
        }   
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {   
            return false;
        }   
    }
    
    /**
     * 是否安装了Map
     * @param context
     * @return true ? installed : uninstalled
     */
    public static  boolean isMapInstalled(Context context) {                                                                                                                                                 
        PackageManager localPackageManager = context.getPackageManager();
        try 
        {   
          localPackageManager.getPackageInfo("com.google.android.apps.maps", 0); 
          return true;
        }   
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {   
            return false;
        }   
    }
    
    /**
     * 是否安装了Earth
     * @param context
     * @return true ? installed : uninstalled
     */
    public static  boolean isEarthInstalled(Context context) {                                                                                                                                                 
        PackageManager localPackageManager = context.getPackageManager();
        try 
        {   
          localPackageManager.getPackageInfo("com.google.earth", 0); 
          return true;
        }   
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {   
            return false;
        }   
    }

}
