package org.whitetree.systable.system;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.whitetree.data.filter.PkgInfoComparator;
import org.whitetree.systable.data.AppPercent;
import org.whitetree.systable.data.ComponentInfo;
import org.whitetree.systable.data.PackageUtil;
import org.whitetree.systable.data.PkgInfo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.NotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StatFs;
import android.provider.Browser;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import client.core.Core;
import client.core.model.Notifiers;
import client.core.model.Task;

import com.android.internal.content.PackageHelper;

public class U {
	private static final long DISPLAY_RATE_US_TIME_INTERVAL = 5 * 24 * 3600 * 1000; // 5
																					// DAY

	private static long TOTAL_MEMORY = 0;
	private static String sVersionName = null;
	private static int sVersionCode = 0;
	private static final int FROYO_SDK_VERSION = 8;
	public static final int APP_STORAGE_INTERNAL_FLAG = 0;
	
	public static long getTotalMem() {
		if (TOTAL_MEMORY == 0) {
			byte[] mBuffer = new byte[1024];
			try {
				FileInputStream is = new FileInputStream("/proc/meminfo");
				int len = is.read(mBuffer);
				is.close();
				for (int i = 0; i < len; i++) {
					if (matchText(mBuffer, i, "MemTotal")) {
						i += 7;
						TOTAL_MEMORY = extractMemValue(mBuffer, i);
						break;
					}
				}
			} catch (java.io.FileNotFoundException e) {
			} catch (java.io.IOException e) {
			}
		}

		return TOTAL_MEMORY;
	}

	public static boolean matchText(byte[] buffer, int index, String text) {
		int N = text.length();
		if ((index + N) >= buffer.length) {
			return false;
		}
		for (int i = 0; i < N; i++) {
			if (buffer[index + i] != text.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public static long extractMemValue(byte[] buffer, int index) {
		while (index < buffer.length && buffer[index] != '\n') {
			if (buffer[index] >= '0' && buffer[index] <= '9') {
				int start = index;
				index++;
				while (index < buffer.length && buffer[index] >= '0'
						&& buffer[index] <= '9') {
					index++;
				}
				String str = new String(buffer, 0, start, index - start);
				return ((long) Integer.parseInt(str)) * 1024;
			}
			index++;
		}
		return 0;
	}

	/**
	 * Get free memory
	 * 
	 * @param context
	 * @return byte of free memory
	 */
	public static long getAvailMem(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		activityManager.getMemoryInfo(mi);
		return mi.availMem;
	}

	public static int getUsedMemPercent(Context context) {
		long total = getTotalMem();
		long free = getAvailMem(context);
		return (int) ((total - free) * 100 / total);
	}

	public static String getAvailMemPretty(Context context) {
		return formatBytes(getAvailMem(context));
	}

	public static String getTotalMemPretty() {
		return formatBytes(getTotalMem());
	}

	/**
	 * TODO: remove useless args Formats data size in KB, MB, from the given
	 * bytes.
	 * 
	 * @param context
	 *            the application context
	 * @param bytes
	 *            data size in bytes
	 * @return the formatted size such as 4.52 MB or 245 KB or 332 bytes
	 */
	public static String formatBytes(long bytes) {
		// TODO: I18N
		if (bytes > 1024 * 1024 * 1024) {
			return String.format("%.2fGB", ((float) (bytes / 1024f / 1024f / 1024f)));
		} else if (bytes > 1024 * 1024) {
			return String.format("%.2fMB", ((float) (bytes / 1024f / 1024f)));
		} else if (bytes > 1024) {
			return String.format("%.2fKB", ((float) (bytes / 1024f)));
		} else {
			return String.format("%dbytes", (int) bytes);
		}
	}
	
	public static String formatBytesForMb(long bytes) {
		return String.format("%.2f", ((float) (bytes / 1024f / 1024f)));
	}

	public static StatFs getFileSystemStat(File target) {
		StatFs fs = new StatFs(target.getAbsolutePath());
		return fs;
	}
	
	// list 0 - all cpu usage
	// list 1 - cpu0 usage
	// list 2 - cpu1 usage ...
	// list 3...so on
	private static ArrayList<Integer> getCpuUsage(ArrayList<String> cpuStatLines0, ArrayList<String> cpuStatLines1) {
		ArrayList<Integer> usages = new ArrayList<Integer>();
		if (cpuStatLines0 != null && cpuStatLines1 != null 
				&& cpuStatLines0.size() == cpuStatLines1.size()
				&& cpuStatLines0.size() > 0) {
			try {
				ArrayList<Integer> temp = new ArrayList<Integer>();
				for (int i = 0; i < cpuStatLines0.size(); ++i) {
					String[] toks = cpuStatLines0.get(i).split(" ");
		
					long idle1 = Long.parseLong(toks[5]);
					long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
							+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
							+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
		
					toks = cpuStatLines1.get(i).split(" ");
		
					long idle2 = Long.parseLong(toks[5]);
					long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
							+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
							+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
					long base = ((cpu2 + idle2) - (cpu1 + idle1));
					if(base == 0) {
						base = 1;
					}
					temp.add((int) (100 * (cpu2 - cpu1) / base ));
				}
				usages = temp;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return usages;
	}
	
	private static ArrayList<String> getCpuStatLines() {
		ArrayList<String> cpustats = new ArrayList<String>();
		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String strLine = null;
			while ((strLine = reader.readLine()) != null) {
				if (strLine.matches("cpu[0-9].*")) {
					// 单个cpu信息的字符串比总体cpu使用率的信息,在开头部分少一个空白字段
					// 添加无用字段 "cpu "补齐位数
					cpustats.add("cpu " + strLine);
				} else if (strLine.matches("cpu.*")) {
					// 取出所有cpu usage 相关信息， 总的和每一个cpu的
					cpustats.add(strLine);
				} else {
					break;
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cpustats;
	}
	
	// list 0 - all cpu usage
	// list 1 - cpu0 usage
	// list 2 - cpu1 usage ...
	// list 3...so on
	private static ArrayList<String> sLastCpuStat = null;
	public static ArrayList<Integer> getCpuUsed() {
		ArrayList<String> cpuStats = getCpuStatLines();
		
		if (null == sLastCpuStat) {
			// 第一次执行，sLastCpuStat为空
			// 等0.36秒后取值
			try {
				Thread.sleep(360);
			} catch (Exception e) {
			}
			sLastCpuStat = cpuStats;
			cpuStats = getCpuStatLines();
		}
		
		ArrayList<Integer> cpuUsageList = getCpuUsage(sLastCpuStat, cpuStats);
		sLastCpuStat = cpuStats;
		return cpuUsageList;
	}
	
	
	public static ArrayList<AppPercent> sAppPercents;
	public static ArrayList<AppPercent> getAppPercent(){
		ArrayList<AppPercent> appPercents = new ArrayList<AppPercent>();
		ArrayList<String> lines = U.getCmdStatLines(new String[]{"/system/bin/top","-d","1","-n","1"});
		for (String _ : lines){
			if(!_.contains("0%") && _.contains("%") && _.contains(".")){
				AppPercent app = new AppPercent();
				String[] tem = _.trim().split("\\s+");
				for(String s : tem){
					if(s.contains("%")){
						int index = s.indexOf("%");
						app.setPercent(s.substring(0, index));
					}
					if(s.contains(".")){
						app.setPkgName(s);
					}
				}
				appPercents.add(app);
			}
		}
		sAppPercents = appPercents;
		return appPercents;
	}
	
	//返回cpu占用率
	//[0] 用户占用
	//[1] 核心占用
	//[2] 总共占用
	// 重复方法
	@Deprecated
	public static int[] getCpuPercent(){
		int [] percents = new int [3]; 
		ArrayList<String> lines = U.getCmdStatLines(new String[]{"/system/bin/top","-d","1","-n","1","-m","1"});
		for (String _ : lines){
			if(_.contains("User") && _.contains("System")){
				String[] tem = _.trim().split("\\s+");
				int index = tem[1].indexOf("%");
				String user = tem[1].substring(0, index);
				int index1 = tem[3].indexOf("%");
				String kernel = tem[3].substring(0, index1);
				percents[0] = Integer.valueOf(user);
				percents[1] = Integer.valueOf(kernel);
				percents[2] = (Integer.valueOf(user)+Integer.valueOf(kernel));
			}
		}
		return percents;
	}
	
	
	// list 返回输出命令
	private static ArrayList<String> getCmdStatLines(String[] commonds) {
		ProcessBuilder cmd;
		cmd = new ProcessBuilder(commonds);
		java.lang.Process process = null;
		String result = "";
		ArrayList<String> statLines = new ArrayList<String>();
		try {
			process = cmd.start();
			InputStream in = process.getInputStream();
			InputStreamReader reader = new InputStreamReader(in);
			BufferedReader breader = new BufferedReader(reader);
			while((result = breader.readLine()) != null){
				if(!TextUtils.isEmpty(result)){
					statLines.add(result);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return statLines;
	}

	/**
	 * Gets the number of cores available in this device, across all processors.
	 * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
	 * @return The number of cores, or 1 if failed to get result
	 */
	public static int getCpuCount() {
		// bugfix 有时会报错误的数量，改用下面的方式
//		return Runtime.getRuntime().availableProcessors();
		
		//Private Class to display only CPU devices in the directory listing
	    class CpuFilter implements FileFilter {
	        @Override
	        public boolean accept(File pathname) {
	            //Check if filename is "cpu", followed by a single digit number
	            if(Pattern.matches("cpu[0-9]", pathname.getName())) {
	                return true;
	            }
	            return false;
	        }      
	    }

	    try {
	        //Get directory containing CPU info
	        File dir = new File("/sys/devices/system/cpu/");
	        //Filter to only list the devices we care about
	        File[] files = dir.listFiles(new CpuFilter());
	        //Return the number of cores (virtual CPU devices)
	        return files.length;
	    } catch(Exception e) {
	        //Default to return 1 core
	        return 1;
	    }
	}
	
	public static String getCpuName() {
		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/cpuinfo", "r");
			String load = reader.readLine();
			String[] names = load.split(":");
			return names[1].trim();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return "";
	}
	
	public static String getCpuMaxFreq() {
		// This may not existed in emulator
		return getCpuFreqInfo("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
	}

    public static String getCpuMinFreq() {
    	return getCpuFreqInfo("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
    }

    public static String getCpuCurFreq() {
    	String text = getCpuFreqInfo("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
    	if (TextUtils.isEmpty(text)) {
    		text = getCpuFreqInfo("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq");
    	} if (TextUtils.isEmpty(text)) {
    		text = getCpuMinFreq();
    	}
    	return text;
    }
	
	private static String getCpuFreqInfo(String fileName) {
        String result = "";
		try {
			RandomAccessFile reader = new RandomAccessFile(fileName, "r");
            String text = reader.readLine();
            result = text.trim();
			reader.close();
		} catch (Exception e) {
//			e.printStackTrace();
		}
        return result;
	}
	
	private static final BigDecimal ZERO = BigDecimal.valueOf(0); 
	private static final BigDecimal  ONE = BigDecimal.valueOf(1); 
	private static final BigDecimal FOUR = BigDecimal.valueOf(4); 
	private static final int roundingMode = BigDecimal.ROUND_HALF_EVEN; 
 
	public static BigDecimal computePi(int digits) { 
        int scale = digits + 5; 
        BigDecimal arctan1_5 = arctan(5, scale); 
        BigDecimal arctan1_239 = arctan(239, scale); 
        BigDecimal pi = arctan1_5.multiply(FOUR).subtract(arctan1_239).multiply(FOUR); 
        return pi.setScale(digits, BigDecimal.ROUND_HALF_UP); 
	} 

	public static BigDecimal arctan(int inverseX, int scale) { 
        BigDecimal result, numer, term; 
        BigDecimal invX = BigDecimal.valueOf(inverseX); 
        BigDecimal invX2 = BigDecimal.valueOf(inverseX * inverseX); 
        numer = ONE.divide(invX, scale, roundingMode); 
        result = numer; 
        int i = 1; 
        do { 
            numer = numer.divide(invX2, scale, roundingMode); 
            int denom = 2 * i + 1; 
            term = numer.divide(BigDecimal.valueOf(denom), scale, roundingMode); 
            if ((i % 2) != 0) {
            	result = result.subtract(term); 
            } else {
            	result = result.add(term); 
            } 
            ++i; 
        } while (term.compareTo(ZERO) != 0); 
        return result; 
	}

	public static String getLocalIpAddress(Context context) {

		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		return String.format("%d.%d.%d.%d", (ipAddress & 0xff),
				(ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
				(ipAddress >> 24 & 0xff));
	}

	/**
	 * Get IMSI
	 * 
	 * @param context
	 * @return
	 */
	public static String getImsi(Context context) {
		TelephonyManager tManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tManager.getSubscriberId();
	}

	/**
	 * Get phone IMEI
	 * 
	 * @param context
	 * @return
	 */
	public static String getImei(Context context) {
		TelephonyManager tManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tManager.getDeviceId();
	}

	public static Object getScreenSize(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		int h = dm.heightPixels;
		int w = dm.widthPixels;
		return String.format("%sx%s",w,h);
	}
	
	/**
	 * 返回网卡地址
	 * 注意: 网卡地址只有当Wifi开启时才能获得
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context) {
		WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wimanager.getConnectionInfo().getMacAddress();
	}

    /**
     * 关闭/开启飞行模式
     * @param context
     */
	public static void toggleAirplane(Context context) {
        ContentResolver cr = context.getContentResolver();
        if (Settings.System.getString(cr, Settings.System.AIRPLANE_MODE_ON).equals("0")) {
            // 获取当前飞行模式状态,返回的是String值0,或1.0为关闭飞行,1为开启飞行
            // 如果关闭飞行,则打开飞行
            Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "1");
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", true);
            context.sendBroadcast(intent);
        } else {
            // 否则关闭飞行
            Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "0");
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", false);
            context.sendBroadcast(intent);
        }
    }
	
	@SuppressWarnings("deprecation")
	public static void kill(Context context, String packageName, int pid) {
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		if(packageName.equals(context.getPackageName())) {
	        Process.killProcess(Process.myUid());
	        System.exit(Process.myUid());
		}
		
		if(Build.VERSION.SDK_INT >= 8) {
			am.killBackgroundProcesses(packageName);
		} else {
		    am.restartPackage(packageName);
		    am.forceStopPackage(packageName);
		}
	}
	
    /**
     * Format a number of tenths-units as a decimal string without using a
     * conversion to float.  E.g. 347 -> "34.7"
     */
    public static final String tenthsToFixedString(int x) {
        int tens = x / 10;
        return Integer.toString(tens) + "." + (x - 10 * tens);
    }

	/**
	 * remove useless args Formats data size in KHz, MHz, GHz, from the given
	 * frequency
	 * 
	 * @param freqKHz   cpu frequency in KHz
 	 *             
	 * @return the formatted frequency such as 1.5 GHz or 599 MHz or 466 KHz
	 */
	public static String formatCpuFrequence(int freqKHz) {
		if (freqKHz >= 1000 * 1000) {
			return String.format("%.1fG", ((int) (freqKHz / 1000)) / 1000f);
		} else if (freqKHz >= 1000) {
			return String.format("%dM", ((int) (freqKHz / 1000)));
		} else {
			return String.format("%dK", (int) freqKHz);
		}
	}
	
	public static String formatCpuFrequence(String freqKHz) {
		int value = 0;
		try {
			value = Integer.valueOf(freqKHz).intValue();
		} catch (Exception e) {
		}
		return formatCpuFrequence(value);
	}
	
	// ---------------------------------------------------------------------------------------------------------------[copy from uninstaller ]
	public static <ClearCacheObserver> void clearPkgCache(Context context, ClearCacheObserver observer) {
		PackageManager pm = context.getPackageManager();
		try {
			Class[] arrayOfClass = new Class[2];
			Class localClass2 = Long.TYPE;
			arrayOfClass[0] = localClass2;
			arrayOfClass[1] = IPackageDataObserver.class;
			Method localMethod = pm.getClass().getMethod("freeStorageAndNotify", arrayOfClass);
			Long localLong = Long.valueOf(getEnvironmentSize() - 1L);
			Object[] arrayOfObject = new Object[2];
			arrayOfObject[0] = localLong;
			arrayOfObject[1] = observer;
			localMethod.invoke(pm, arrayOfObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static long getEnvironmentSize() {
		File localFile = Environment.getDataDirectory();
		long l1;
		if (localFile == null) {
			l1 = 0L;
		}
		String str = localFile.getPath();
		StatFs localStatFs = new StatFs(str);
		long l2 = localStatFs.getBlockSize();
		l1 = localStatFs.getBlockCount() * l2;
		return l1;
	}
	
	public static void uninstallApp(Context context, String packageName) {
		Uri packageURI = Uri.parse("package:" + packageName);     
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);     
		uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(uninstallIntent); 
	}

//	public static void uninstallApps(Context context, ArrayList<ResolveInfo> arrPkgInfo) {
//		if (arrPkgInfo != null) {
//			for(ResolveInfo info : arrPkgInfo) {
//				uninstallApp(context, info.activityInfo.packageName);
//			}
//		}
//	}
	
	public static void uninstallApps(Context context, ArrayList<PkgInfo> arrPkgInfo) {
		if (arrPkgInfo != null) {
			for(PkgInfo info : arrPkgInfo) {
				if(info.mIsSelected) {
					uninstallApp(context, info.mPackageName);
				}
			}
		}
	}
	
	public static ArrayList<PkgInfo> getInstalledApps(Context context, boolean getSysPackages) {
	    ArrayList<PkgInfo> res = new ArrayList<PkgInfo>();          
	    PackageManager pm = context.getPackageManager();
	    
	    List<PackageInfo> packs = pm.getInstalledPackages(0);  
	    for(int i = 0; i < packs.size(); i++) {  
	        PackageInfo p = packs.get(i);  
	        if ((!getSysPackages) && (p.versionName == null)) {  
	            continue ;  
	        }
            
	        if(((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 
	        		|| (p.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
	        		) {

		        res.add(getPkgInfo(context, pm, p));
//	        	PkgInfo pkgInfo = new PkgInfo();  
//		        pkgInfo.mAppName = p.applicationInfo.loadLabel(pm).toString();  
//		        pkgInfo.mPackageName = p.packageName;  
//		        pkgInfo.mVersionName = p.versionName;  
//		        pkgInfo.mVersionCode = p.versionCode;
//		        pkgInfo.mAppIcon = p.applicationInfo.loadIcon(pm);
//		        pkgInfo.mSharedUserId = p.sharedUserId;
//		        
//		        res.add(pkgInfo);
	        } else {
	        	//系统应用
	        	continue;
	        }
	    }
	    
	    PkgInfoComparator comp = new PkgInfoComparator();
	    Collections.sort(res, comp);
	    return res;
	}
	
	public static PkgInfo getPkgInfo(Context context, PackageManager pm,PackageInfo p) {
		PkgInfo pkgInfo = new PkgInfo();
		try {
			pkgInfo.mAppName = p.applicationInfo.loadLabel(pm).toString();
			pkgInfo.mPackageName = p.packageName;
			pkgInfo.mVersionName = p.versionName;
			pkgInfo.mVersionCode = p.versionCode;
			pkgInfo.mAppIcon = p.applicationInfo.loadIcon(pm);
			pkgInfo.mSharedUserId = p.sharedUserId;

			// is installed in stroage or sdcard
			pkgInfo.mIsInternal = (p.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == APP_STORAGE_INTERNAL_FLAG;

			// if apk not in sdcard, check it could move to sdcard
			if (pkgInfo.mIsInternal) {
				pkgInfo.mCouldMove2Sdcard = false;

				// 2.2才开始支持
				if (!isLowerThanFroyo(context)) {
					if ((p.applicationInfo.flags & ApplicationInfo.FLAG_FORWARD_LOCK) == 0
							&& (p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
							&& p != null) {
						if (p.installLocation == PackageInfo.INSTALL_LOCATION_PREFER_EXTERNAL
								|| p.installLocation == PackageInfo.INSTALL_LOCATION_AUTO) {
							pkgInfo.mCouldMove2Sdcard = true;
						} else if (p.installLocation == PackageInfo.INSTALL_LOCATION_UNSPECIFIED) {
							IPackageManager ipm = IPackageManager.Stub
									.asInterface(ServiceManager
											.getService("package"));
							int loc;
							try {
								loc = ipm.getInstallLocation();
							} catch (RemoteException e) {
								return pkgInfo;
							}
							if (loc == PackageHelper.APP_INSTALL_EXTERNAL) {
								// For apps with no preference and the default
								// value set
								// to install on sdcard.
								pkgInfo.mCouldMove2Sdcard = true;
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
		return pkgInfo;
	}

	public static boolean isLowerThanFroyo(Context context) {
    	return Build.VERSION.SDK_INT < FROYO_SDK_VERSION; 
		
	}
	
	public static <PkgSizeObserver> void getPkgDetail(Context context, String pkgName, PkgSizeObserver observer) {
		PackageManager pm = context.getPackageManager();
		try {
			Method getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
			getPackageSizeInfo.invoke(pm, pkgName, observer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void exec(Task task, String targetGroup) {
		task.setTo(new Notifiers(targetGroup));
		Core.I().exec(task);
	}
	
	private static final String SCHEME = "package";  
	/** 
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本) 
	 */  
	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";  
	/** 
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2) 
	 */  
	private static final String APP_PKG_NAME_22 = "pkg";  
	/** 
	 * InstalledAppDetails所在包名 
	 */  
	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";  
	/** 
	 * InstalledAppDetails类名 
	 */  
	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";  
	private static final int GINGERBREAD_SDK_VERSION = 9;	// 2.3

	
	/** 
	 * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level 
	 * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。 
	 *  
	 * @param context 
	 *  
	 * @param packageName 
	 *            应用程序的包名 
	 */  
	public static void showInstalledAppDetails(Context context, String packageName) {  
	    Intent intent = new Intent();  
	    final int apiLevel = Build.VERSION.SDK_INT;  
	    if (apiLevel >= GINGERBREAD_SDK_VERSION) { 
	    	// 2.3（ApiLevel 9）以上，使用SDK提供的接口  
	        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);  
	        Uri uri = Uri.fromParts(SCHEME, packageName, null);  
	        intent.setData(uri);  
	        
	    } else { 
	    	// 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）  
	        // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。  
	        final String appPkgName = (apiLevel == FROYO_SDK_VERSION ? APP_PKG_NAME_22 : APP_PKG_NAME_21);  
	        intent.setAction(Intent.ACTION_VIEW);  
	        intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);  
	        intent.putExtra(appPkgName, packageName);  
	    }  
	    context.startActivity(intent);  
	}
	// ---------------------------------------------------------------------------------------------------------------[~~]
	 /**
     * 是否安装了AndroidMarket
     * 
     * @param context
     * @return true ? installed : uninstalled
     */
    public static boolean isMarketInstalled(Context context) {
        PackageManager localPackageManager = context.getPackageManager();
        try {
            localPackageManager.getPackageInfo("com.android.vending", 0);
            return true;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            return false;
        }
    }

    public static void startViewAppDetail(Context context, String pkgName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (isMarketInstalled(context)) {
            intent.setData(Uri.parse("market://details?id=" + pkgName));
        } else {
            intent.setData(Uri.parse("http://market.android.com/details?id=" + pkgName));
        }

        context.startActivity(intent);
    }

    /*
     * 打开　Google　Play　whitetree 的页面
     */
    public static void startViewOurApp(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
		if(isMarketInstalled(context)) {
			intent.setData(Uri.parse("market://search?q=pub:\"Whitetree\""));
		} else {
			intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Whitetree"));
		}
        context.startActivity(intent);
    }


// XXX(amas): 评分对话框
//    public static void launchContactUs(Context context) {
//        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
//        intent.setType("message/rfc822");
//        String[] recipients = new String[] { context.getString(R.string.contactus_email) };
//        intent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
//        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.contactus_subject));
//        context.startActivity(Intent.createChooser(intent, ((Activity) context).getTitle()));
//    }

//    public static void checkRateUsPrompt(final Context context) {
//    	if(!LocalStorage.getInstance().hasRatedUs()) {
//    		long nowTime = System.currentTimeMillis();
//        	long lastTime = LocalStorage.getInstance().getLastDisplayrateUsTime();
//        	if(lastTime <= 0) {
//        		LocalStorage.getInstance().setLastDisplayRateUsTime(nowTime);
//        	} 
//        	else if (nowTime - lastTime > DISPLAY_RATE_US_TIME_INTERVAL) {
//        		LocalStorage.getInstance().setLastDisplayRateUsTime(nowTime);
//        		showRateUsDialog(context);
//        	}
//    	}  	      
//    }
//    
//    public static void showRateUsDialog(final Context context) {
//    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    	View layout =  inflater.inflate(R.layout.layout_dialog_rating, null);
//    	TextView rating = (TextView) layout.findViewById(R.id.text);
//    	rating.setText(R.string.support_content);
//    	CheckBox hasRatedCheckBox = (CheckBox) layout.findViewById(R.id.support_check);
//    	hasRatedCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            	LocalStorage.getInstance().setHasRatedUs(isChecked);
//            }
//        });
//    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle(R.string.support_us)
//               .setPositiveButton(R.string.menu_rating, new DialogInterface.OnClickListener() {
//            	   public void onClick(DialogInterface dialog,int id) {
//            		   U.startViewAppDetail(context, context.getPackageName());
//            	   }
//               })
//    		   .setNegativeButton(R.string.alert_dialog_cancel, null)
//    		   .setView(layout)
//    		   .show();
//    }
    
    public static String getVersionName(Context context) {
        if (TextUtils.isEmpty(sVersionName)) {    
            PackageInfo packageInfo;
            try {
                packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                sVersionName = packageInfo.versionName +"";
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        
        return sVersionName;
    }

    public static int getVersionCode(Context context) {   
        if (sVersionCode <= 0) {    
			PackageInfo packageInfo;
			try {
				packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
				sVersionCode = packageInfo.versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
        }
        return sVersionCode;
    }
    
//    public static void showAppUpdateDialog(final Context context) {
//    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle(R.string.update_title)
//        	   .setMessage(R.string.update_msg)
//               .setPositiveButton(R.string.update_app, new DialogInterface.OnClickListener() {
//            	   public void onClick(DialogInterface dialog,int id) {
//            		   U.startViewAppDetail(context, context.getPackageName());
//            	   }
//               })
//    		   .setNegativeButton(R.string.alert_dialog_cancel, null)
//    		   .show();
//    }

    
    private static File mExtSdcard = null;
    
	/**
	 * 通过分析/etc/vold.fstab文件找到外置sdcard
	 * @return
	 */
	public static File getExternalSdcardMountPoint() {
		if(mExtSdcard != null) {
			return mExtSdcard;
		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("/etc/vold.fstab"));
			String line = br.readLine();
			while((line = br.readLine()) !=  null) {
				if(line.startsWith("#")) {
					continue;
				}
				
				if(line.contains("dev_mount") && line.contains("sdcard")) {
					String[] xs = line.split("[\t ]");
					String mountPoint = xs[2];
					if(Environment.getExternalStorageDirectory().getAbsolutePath().equals(mountPoint)) {
						continue;
					} else {
						mExtSdcard = new File(mountPoint);
						return mExtSdcard;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Deprecated
	public static ContentValues getFsTable() {
		ContentValues v = new ContentValues();
		try {
			BufferedReader br = new BufferedReader(new FileReader("/proc/mounts"));
			String line = br.readLine();
			while(line !=  null) {
				String[] xs = line.split(" ");
				if(xs.length > 2) {
					String mountPoint = xs[1];
					if(mountPoint.contains("sdcard") && !mountPoint.contains(".android_secure")) {
					}
				}
				line = br.readLine();
			}
			
		} catch (java.io.FileNotFoundException e) {
		} catch (java.io.IOException e) {
		}
		return v;
	}
	
	/**
	 * 温度转换 摄氏->华氏
	 * F = 32 + 1.8 × C
	 * @param celsius 摄氏温度
	 * @return fahrenheit 华氏温度
	 */
	public static float temperatureUnitConvertToF(float celsius) {
		return 32.0f + 1.8f * celsius;
	}
	
    public static int getPixByDip(float dip) {
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.setToDefaults();
        return (int)(dip * metrics.density);
    }
    
    /**
     * 打开WIFI
     * @param context
     * @return boolean
     */
	public static boolean openWIFI(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null && !wifiManager.isWifiEnabled()) {
			return wifiManager.setWifiEnabled(true);
		} else {
			return false;
		}
	}
    /**
     * 关闭WIFI
     * @param context
     * @return boolean
     */
	public static boolean closeWIFI(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null && wifiManager.isWifiEnabled()) {
			return wifiManager.setWifiEnabled(false);
		}
		return false;
	}
    /**
     * 改变WIFI状态，如果之前是打开，就给关上。
     * @param context
     * @return boolean
     */
	public static boolean changeWIFIStatus(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null ) {
			return wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
		} else {
			return false;
		}
	}

	/*
	 * 将int类型的IP地址转换成 xxx.xxx.xxx.xxx的字符串形式
	 */
	public static String ipAddrIntToString(int ip) {
		try {
			byte[] bytes = new byte[4];
			bytes[0] = (byte) (0xff & ip);
			bytes[1] = (byte) ((0xff00 & ip) >> 8);
			bytes[2] = (byte) ((0xff0000 & ip) >> 16);
			bytes[3] = (byte) ((0xff000000 & ip) >> 24);
			return Inet4Address.getByAddress(bytes).getHostAddress();
		} catch (Exception e) {
			return "";
		}
	}

	public static int historyCount(Context context) {
		int count = 0;
		if(isBrowserHasCache(context)){
			count ++;
		}
		if(isClipBoradHasCache(context)){
			count ++;
		}
		if(isGoogleMarketHasCache(context)){
			count ++;
		}
		if(isGmailHasCache(context)){
			count ++;
		}
		return count;
	}
	
	public static boolean isBrowserHasCache(Context context){
		return Browser.canClearHistory(context.getContentResolver());
	}
	
	public static boolean isClipBoradHasCache(Context context){
		ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		return !TextUtils.isEmpty(cm.getText());
	}
	
	public static boolean isGoogleMarketHasCache(Context context){
		if (PackageUtil.isMarketInstalled(context)) {
			return isMarketHasCache();
		} 
		return false;
	}
	
	public static boolean isGmailHasCache(Context context){
		if (PackageUtil.isGmailInstalled(context)) {
			return isGmailHasCache();
		} 
		return false;
	}
	
	public static boolean isMarketHasCache() {
		File file = new File(
				"/data/data/com.android.vending/databases/suggestions.db");
		return file.exists();
	}
	
	public static boolean isGmailHasCache() {
		File file = new File(
				"/data/data/com.google.android.gm/databases/suggestions.db");
		return file.exists();
	}
	
	public static void createShortCut(Context context, ApplicationInfo appInfo) {
		if (appInfo.packageName == null) {
			return;
		}

		PackageManager pm = context.getPackageManager();
		final CharSequence appLabel = appInfo.loadLabel(pm);
		Intent intent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		Intent shortcutIntent = new Intent(
				pm.getLaunchIntentForPackage(appInfo.packageName));

		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appLabel);
		intent.putExtra("duplicate", false);
		ShortcutIconResource iconResource = new ShortcutIconResource();
		iconResource.packageName = appInfo.packageName;
		try {
			iconResource.resourceName = pm.getResourcesForApplication(
					appInfo.packageName).getResourceName(appInfo.icon);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
		context.sendBroadcast(intent);
	}

	
	public static ArrayList<ComponentInfo> sCompenentInfoList = new ArrayList<ComponentInfo>();
	public static ArrayList<ComponentInfo> getBootUpComponent(Context context) {
		ArrayList<ComponentInfo> receList = new ArrayList<ComponentInfo>();
		HashMap<String,ComponentInfo> receMap = new HashMap<String, ComponentInfo>();
		
		ArrayList<ResolveInfo> listEnable = getEnableSelfStartApp(context);
        for(ResolveInfo r : listEnable){
        	if(!receMap.containsKey(r.activityInfo.name)){
        		ComponentInfo item = new ComponentInfo();
	        	item.setInfo(r);
	        	int stat = getComponentStat(context,r.activityInfo.packageName, r.activityInfo.name);
	        	item.setEnable(PackageManager.COMPONENT_ENABLED_STATE_DISABLED != stat);
	        	receMap.put(item.getInfo().activityInfo.name, item);
        	}
        }
        
        ArrayList<ResolveInfo> listDisable = getDisableSelfStartApp(context);
        for(ResolveInfo r : listDisable){
        	if(!receMap.containsKey(r.activityInfo.name)){
        		ComponentInfo item = new ComponentInfo();
	        	item.setInfo(r);
	        	int stat = getComponentStat(context,r.activityInfo.packageName, r.activityInfo.name);
	        	item.setEnable(PackageManager.COMPONENT_ENABLED_STATE_DISABLED != stat);
	        	receMap.put(item.getInfo().activityInfo.name, item);
        	}
        }
        
        Iterator<Entry<String, ComponentInfo>> iter = receMap.entrySet().iterator();
        while(iter.hasNext()){
        	Entry entry = (Entry) iter.next();
        	ComponentInfo item = (ComponentInfo) entry.getValue();
        	receList.add(item);
        }
        sCompenentInfoList = receList;
		return receList;
	}
	
	private static ArrayList<ResolveInfo> getEnableSelfStartApp(Context context){
		PackageManager pm = (PackageManager)context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED , null);  
        List<ResolveInfo> resolves =pm.queryBroadcastReceivers(intent, 0);
        
        List<ResolveInfo> res = new ArrayList<ResolveInfo>();
        for(ResolveInfo r : resolves){
        	if(isUserApp(r)){
        		res.add(r);
        	}
        }
		return (ArrayList<ResolveInfo>) res;
	}
    
    private static ArrayList<ResolveInfo> getDisableSelfStartApp(Context context){
		PackageManager pm = (PackageManager)context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED , null);  
        List<ResolveInfo> resolves =pm.queryBroadcastReceivers(intent, PackageManager.GET_DISABLED_COMPONENTS);
        
        List<ResolveInfo> res = new ArrayList<ResolveInfo>();
        for(ResolveInfo r : resolves){
        	if(isUserApp(r)){
        		res.add(r);
        	}
        }
		return (ArrayList<ResolveInfo>) res;
	}
    
    private static boolean isUserApp(ResolveInfo ri) {
		ApplicationInfo i = ri.activityInfo.applicationInfo;
		return ((i.flags & ApplicationInfo.FLAG_SYSTEM) == 0 || (i.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
	}
    
    public static int getComponentStat(Context context,String pkgName, String className) {
		try {
			ComponentName receiver1 = new ComponentName(pkgName, className);
			PackageManager pm = context.getPackageManager();
			return pm.getComponentEnabledSetting(receiver1);
		} catch (Exception e) {
			e.printStackTrace();
			return PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
		}
	}

    public static boolean runProcess(Context context,String[] commands) {
        java.lang.Process process = null;  
        DataOutputStream os = null;  
        
        try {  
            process = Runtime.getRuntime().exec("su");  
            os = new DataOutputStream(process.getOutputStream());  
            os.flush();
            
            for(int j=0;j<commands.length;j++){
                if(j < (commands.length - 1)){
                    os.writeBytes(commands[j]);
                    os.flush();  
                }else{
                    os.writeBytes(commands[j]);
                }
            }
//            command = "pm disable " + appPackageName + " \n";  
            os.writeBytes("exit\n");  

            os.flush();  
            os.close();  

            process.waitFor();  
            process.destroy();  
            return true;
            
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;
        } 
        
    }
    
	public static boolean isAutoBrightness(ContentResolver cR) {
		boolean automicBrightness = false;
		try {
			automicBrightness = Settings.System.getInt(cR, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return automicBrightness;
	}


	public static void setBright(Context context, int b) {
		int status = U.getSettingSysytemValue(context,Settings.System.SCREEN_BRIGHTNESS,-1);
		if(status != b){
			if (isAutoBrightness(context.getContentResolver())) {
				Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			} 
			Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, b);
		}
	}

	public static void setWifi(Context context, int b) {
		int status = U.getSettingSysytemValue(context,Settings.System.WIFI_ON,-1);
		if(status != b){
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(b == 1);
		}
	}

	public static void setBluetooth(Context context, final int b) {
		int status = U.getSettingSysytemValue(context,Settings.System.BLUETOOTH_ON,-1);
		if(status != b){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Looper.prepare();
					try {
						if (b == 1) {
							BluetoothAdapter.getDefaultAdapter().enable();
						} else {
							BluetoothAdapter.getDefaultAdapter().disable();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	public static void setVibrate(Context context, int i) {
		int status = U.getSettingSysytemValue(context,Settings.System.VIBRATE_ON,-1);
		if( status != i){
			AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, i);
		}
	}

	public static void setSyncBackground(Context context, int i) {
		int status = U.getSettingSecureValue(context,Settings.Secure.BACKGROUND_DATA,-1);
		if( status != i){
			try {
				ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				cm.setBackgroundDataSetting(i == 1);
			} catch (Exception e) {
				try {
					Intent intent = new Intent();
					intent.setClassName("com.android.providers.subscribedfeeds", "com.android.settings.ManageAccountsSettings");
					context.startActivity(intent);
				} catch (Exception e2) {
				}
			}
		}
	}
	
	public static int getSettingSysytemValue(Context context,String key,int defaultValue){
		int value = defaultValue;
		try {
			value = Settings.System.getInt(context.getContentResolver(), key);
		} catch (SettingNotFoundException snfe) {
		}
		return value;
	}
	
	public static int getSettingSecureValue(Context context,String key,int defaultValue){
		int value = defaultValue;
		try {
			value = Settings.Secure.getInt(context.getContentResolver(), key);
		} catch (SettingNotFoundException snfe) {
		}
		return value;
	}

	public static void setWalock(Context context, int i) {
		int status = U.getSettingSysytemValue(context,Settings.System.SCREEN_OFF_TIMEOUT,-1);
		if( status != i){
			Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, i);
		}
	}
}	
