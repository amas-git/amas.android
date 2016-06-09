package com.cmcm.onews.crash;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import com.cleanmaster.base.crash.BaseDependence;
import com.cmcm.onews.C;
import com.cmcm.onews.util.ConflictCommons;
import com.cmcm.onews.util.FileUtils;
import com.cmcm.onews.util.SPUtils;

import java.io.File;

/**
 */
public class CrashDependence extends BaseDependence{

    Context context = null;
    
    public CrashDependence(Context ctx){
        context = ctx.getApplicationContext();
    }
    
    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public int getVersionCode(Context context) {
        ComponentName cn = new ComponentName(context, context.getClass());
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    cn.getPackageName(), 0);
            return info.versionCode;
        } catch (/*NameNotFoundException*/Exception e) {
            return -1;
        }
    }

    @Override
    public int getVERSION_CODE() {
        /*
         * crash sdk里会判断 getVERSION_CODE()和getVersionCode()这两个方法返回的值是否相等
         * 如果不相等会强制应用退出. 
         * 如果没有自动化编译脚本修改VERSION_CODE(例如CM里的Env.VERSION_CODE), 
         * 那么可以直接 "return getVersionCode(context);"
         */
        return getVersionCode(context);
    }

    @Override
    public String getDB_NAME_COMMON() {
        return "";
    }

    @Override
    public boolean IsServiceProcess() {
        return true;
    }

    @Override
    public long GetLastUpdateDbTime() {
        return SPUtils.GetLastUpdateDbTime();
    }

    @Override
    public long GetAppStartTime() {
        return SPUtils.GetAppStartTime();
    }

    @Override
    public String getChannelIdString() {
        return null;
    }

    @Override
    public String getSQLiteDatabaseVersion() {
        return "";
    }

    @Override
    public boolean isMobileRoot() {
        return false;
    }

    @Override
    public boolean checkRoot() {
        return false;
    }

    /**
     * 崩溃统计平台分配的ID，发送申请邮件后会给回复分配ID
     * @return
     */
    @Override
    public String getCrashKey() {
        if(ConflictCommons.isCNVersion()){
            return C.NEWS_INDIA_CRASH_KEY_CN;
        }else{
            return C.NEWS_INDIA_CRASH_KEY;
        }
    }

    @Override
    public String GetProcName() {
        return "";
    }

    @Override
    public boolean IsUIProcess() {
        return true;
    }

    @Override
    public String getFileSavePath() {
        String fileSavePath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            fileSavePath = Environment.getExternalStorageDirectory().getPath() + "/.newsindia/";

            if (Build.VERSION.SDK_INT >= 8) {
                File externFilesDir = getExternalFilesRootDir(context);
                if (null != externFilesDir) {
                    if (!externFilesDir.exists()) {
                        externFilesDir.mkdirs();
                    }

                    fileSavePath = FileUtils.addSlash(externFilesDir.getPath());
                }
            }

            File fileSdDir = new File(fileSavePath);
            fileSdDir.mkdir();
            if (!fileSdDir.exists()) {
                fileSavePath = null;
            }
        } else {
            fileSavePath = null;
        }

        if (null == fileSavePath) {
            fileSavePath = FileUtils.addSlash(context.getApplicationInfo().dataDir);
        }
        return fileSavePath;
    }
    
    @Override
    public String getLogFileName() {
        return "";
    }

    @Override
    public String getLogTagName() {
        return "";
    }

    @Override
    public void saveRecentCrashTime(long l) {
        SPUtils.saveRecentCrashTime(l);
    }

    @Override
    public void onFinishReport() {
        //crash 上报结束, 
    }

    @Override
    public void onAppFinish() {
        //crash 上报结束, 准备关闭
    }

    @Override
    public void killMyself() {
        try {
            // 当kill自己前新启了个进程，有的机子可能新启的进程还没初始化完
            Thread.sleep(1500);

            Process.killProcess(Process.myPid());
            System.exit(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearDbFiles() {
        //清空有可能因文件损坏而造成崩溃的文件，以免坏文件反复造成崩溃。
    }

    @Override
    public boolean isHasGmail(Context c) {
        if (null == c)
            return false;

        boolean bHas = true;
        try {
            c.getPackageManager().getPackageInfo("com.google.android.gm", PackageManager.GET_GIDS);
        } catch (/* NameNotFoundException */Exception e) {
            // 抛出找不到的异常，说明该程序已经被卸载
            bHas = false;
        }
        return bHas;
    }

    @Override
    public long getLastCrashFeedbackTime() {
        return SPUtils.getLastCrashFeedbackTime();
    }

    @Override
    public void setLastCrashFeedbackTime(long currentTime) {
        SPUtils.setLastCrashFeedbackTime(currentTime);
    }

    @Override
    public boolean isAllowAccessNetwork(Context context) {
        return true;
    }

    @Override
    public int getLastBugFeedCount() {
        return SPUtils.getLastBugFeedCount();
    }

    @Override
    public long getLastBugFeedTime() {
        return SPUtils.getLastBugFeedTime();
    }

    @Override
    public void setLastBugFeedCount(int i) {
        SPUtils.setLastBugFeedCount(i);
    }

    @Override
    public void setLastBugFeedTime(long now_time) {
        SPUtils.setLastBugFeedTime(now_time);
    }

    @Override
    public void reportProbeExpToInfoC(int nMainType, int nSubType, String info) {
        Log.e("report", "reportProbeExpToInfoC:" + nMainType + ":" + nMainType + ":\n" + info);
    }

    @Override
    public int GetActivityNumber() {
        return 0;
    }

    @Override
    public String GetForegroundActName() {
        return "";
    }

    @Override
    public int getDataVersionInt() {
        return 0;
    }

    @Override
    public String getVIP_Assert() {
//        return "version.ini";
        return null;
    }


    @Override
    public boolean onCrashFeedback(Context context, String dump, String mDumpKey) {
        return false;
    }

    private static File getExternalFilesRootDir(Context context) {
        try{
            return context.getExternalFilesDir(null);
        }
        catch(NullPointerException e){
            return null;
        }
    }

}
