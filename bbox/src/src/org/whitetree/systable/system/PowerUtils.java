package org.whitetree.systable.system;

import java.util.Map;

import org.whitetree.systable.LOG;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.BatteryStats;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.BatteryStats.Uid;
import android.util.Log;
import android.util.SparseArray;

import com.android.internal.app.IBatteryStats;
import com.android.internal.os.BatteryStatsImpl;
import com.android.internal.os.PowerProfile;


/**
 * Power utils for handle power usage data
 * Time Unit:
 *  * s  (second)       1           s
 *  * ms (millisecond)  1 / 1000    s
 *  * μs (microsecond)  1 / 1000000 s
 * @author amas
 */
public class PowerUtils {
	private static final String TAG = "PowerUtils";

	private static final boolean DEBUG = false;

	/**
	 * Android battery service name
	 */
	private static final String ANDROID_BATTERY_SERVICE = "batteryinfo";
	
	/**
	 * Power profile 
	 */
	private static PowerProfile sPowerProfile = null;
	
	private static double sNetAvgPowerUsage = 0.0;

	/**
	 * Get power profile
	 * @param context
	 * @return
	 */
	public static PowerProfile getPowerProfile(Context context) {
		if(sPowerProfile == null) {
			sPowerProfile = new PowerProfile(context);
		}
		return sPowerProfile;
	}
	
	/**
	 * Get system battery status of current time (since boot up)
	 * @return
	 */
	public static BatteryStatsImpl getBatteryStatus() {
		IBatteryStats    bs = IBatteryStats.Stub.asInterface(ServiceManager.getService(ANDROID_BATTERY_SERVICE));
		BatteryStatsImpl st = null; 
        try {
            byte[] data   = bs.getStatistics();
            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(data, 0, data.length);
            parcel.setDataPosition(0);
            st = com.android.internal.os.BatteryStatsImpl.CREATOR.createFromParcel(parcel);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException:", e);
        }
        return st;
	}
	
	/**
	 * NOTICE: 
	 * @param device running time by million second
	 * @param mAPs milliAm per second
	 * @return mA
	 */
	public static double calcPowerUsage_mA(final long ms, final double mAPs) {
		return (ms / 1000.0) * mAPs;
	}
	
	/**
	 * ADPT(zhoujb): the adaptor function to the android system
	 * Get specify devices average power consumption 
	 * @param context 
	 * @param deviceType  See: {@link PowerProfile}
	 * @return mA per Second
	 */
	private static double getAvgPower(Context context, String deviceType) {
		return getPowerProfile(context).getAveragePower(deviceType);
	}
	
	/**
	 * ADPT(zhoujb): the adaptor function to the android system
	 * Get specify devices average power consumption 
	 * @param context 
	 * @param deviceType  See: {@link PowerProfile}
	 * @param the device working level
	 * @return mA per Second
	 */
	private static double getAvgPower(Context context, String deviceType, int level) {
		return getPowerProfile(context).getAveragePower(deviceType, level);
	}
	
	/**
	 * Get the average power consumption of full working screen
	 * See: PowerProfile.POWER_SCREEN_FULL
	 * @param context
	 * @return mA per Second
	 */
	public static double getAvgPower_ScreenFull(Context context) {
		return getAvgPower(context,PowerProfile.POWER_SCREEN_FULL);
	}
	
	/**
	 * Get the average power consumption of full working screen
	 * See: PowerProfile.POWER_SCREEN_ON
	 * @param context
	 * @return mA per Second
	 */
	public static double getAvgPower_ScreenOn(Context context) {
		return getAvgPower(context,PowerProfile.POWER_SCREEN_ON);
	}
	
	/**
	 * Get the average power consumption of specify screen working level
	 * 
	 * The screen brightness may have sever predefined level.
	 * e.g: 
	 * <code>
	 * static final String[] SCREEN_BRIGHTNESS_NAMES = {
	 *   "dark", "dim", "medium", "light", "bright"
	 * };
	 * </code>
	 * 
	 * @param context context
	 * @param level See: {@link BatteryStats.NUM_SCREEN_BRIGHTNESS_BINS}
	 * @return mA per Second
	 */
	public static double getAvgPower_ScreenLevel(Context context, int level) {
		return getAvgPower_ScreenFull(context)*(level+0.5f) / BatteryStats.NUM_SCREEN_BRIGHTNESS_BINS;
	}
	
	
	/**
	 * See: PowerProfile.POWER_RADIO_ACTIVE
	 * @param context
	 * @return mA per Second 
	 */
	public static double getAvgPower_RadioActive(Context context) {
		return getAvgPower(context,PowerProfile.POWER_RADIO_ACTIVE);
	}
	
	/**
	 * See: PowerProfile.POWER_RADIO_SCANNING
	 * @param context
	 * @return  mA per Second 
	 */
	public static double getAvgPower_RadioScanning(Context context) {
		return getAvgPower(context, PowerProfile.POWER_RADIO_SCANNING);
	}

	/**
	 * @param context
	 * @param level
	 * @return
	 */
	public static double getAvgPower_RadioOn(Context context, int level) {
		return getAvgPower(context, PowerProfile.POWER_RADIO_ON, level);
	}
	
	/**
	 * @param context
	 * @return
	 */
	public static double getAvgPower_WifiOn(Context context) {
		return getAvgPower(context, PowerProfile.POWER_WIFI_ON);
	}
	
	/**
	 * @param context
	 * @return
	 */
	public static double getAvgPower_WifiActive(Context context) {
		return getAvgPower(context, PowerProfile.POWER_WIFI_ACTIVE);
	}
	
	/**
	 * @param context
	 * @return
	 */
	public static double getAvgPower_WifiScanning(Context context) {
		return getAvgPower(context, PowerProfile.POWER_WIFI_SCAN);
	}
	
	/**
	 * @param context
	 * @return
	 */
	public static double getAvgPower_CpuIdle(Context context) {
		return getAvgPower(context, PowerProfile.POWER_CPU_IDLE);
	}
	
	/**
	 * @param context
	 * @return
	 */
	public static double getAvgPower_BluetoothOn(Context context) {
		return getAvgPower(context, PowerProfile.POWER_BLUETOOTH_ON);
	}
	
	/**
	 * @param context
	 * @return
	 */
	public static double getAvgPower_BluetoothAtCmd(Context context) {
		return getAvgPower(context, PowerProfile.POWER_BLUETOOTH_AT_CMD);
	}

	/**
	 * Power consumption when bluetooth device send/recv data
	 * @param context
	 * @return
	 */
	public static double getAvgPower_BluetoothActive(Context context) {
		return getAvgPower(context, PowerProfile.POWER_BLUETOOTH_ACTIVE);
	}
	
	/**
	 * Get number of cpu speed steps
	 * @param context
	 * @return
	 */
	public static final int getNumCpuSpeedSteps(Context context) {
		return getPowerProfile(context).getNumSpeedSteps();
	}
	
	/**
	 * @param context
	 * @param level
	 * @return
	 */
	public static final double getAvgPower_CpuActive(Context context, int level) {
		return getAvgPower(context, PowerProfile.POWER_CPU_ACTIVE, level);
	}
	
	/**
	 * @param context
	 * @param level
	 * @return
	 */
	public static final double getAvgPower_CpsOn(Context context) {
		return getAvgPower(context, PowerProfile.POWER_GPS_ON);
	}
	
	// -------------------------------------------------------------- [ Top Level Functions ]
	/**
	 * @param context
	 * @param uNow
	 * @param status
	 * @param type
	 * @return
	 */
	public static double getScreenUsage(Context context, long uNow, BatteryStatsImpl status, int type) {
		double power = 0;
		
		// 1. calc full screen on power usage
		power += calcPowerUsage_mA(status.getScreenOnTime(uNow, type),
									  getAvgPower_ScreenOn(context));
//		Log.e("xxx", "MY DISP SCREEN ON: " + power);
		// TODO(zhoujb): is the highest level equal to the screen full ???
		// 2. calc the other brightness level
		for (int i = 0; i < BatteryStats.NUM_SCREEN_BRIGHTNESS_BINS; i++) {
			power += calcPowerUsage_mA(status.getScreenBrightnessTime(i, uNow, type),
										  getAvgPower_ScreenLevel(context, i));
			Log.e("xxx","M: screenBinPower + " + i + " = " +getAvgPower_ScreenLevel(context, i));
		}
		
		return power / 1000;
	}
	
	/**
	 * @param context
	 * @param uNow
	 * @param status
	 * @param type
	 * @return
	 */
	public static double getPhoneUsage(Context context, long uNow, BatteryStatsImpl status, int type) {
		return calcPowerUsage_mA(status.getPhoneOnTime(uNow, type),
				                  getAvgPower_RadioActive(context)) / 1000;
	}
	
	/**
	 * @param context
	 * @param uNow
	 * @param status
	 * @param type
	 * @return
	 */
	public static double getRadioUsage(Context context, long uNow, BatteryStatsImpl status, int type) {
		double power = 0;
		
		// 1. radio on 
		for(int i=0; i<BatteryStats.NUM_SIGNAL_STRENGTH_BINS; ++i) {
			power += calcPowerUsage_mA(status.getPhoneSignalStrengthTime(i, uNow, type), 
					                    getAvgPower_RadioOn(context, i));
		}
		
		// 2. radio scanning
		power += calcPowerUsage_mA(status.getPhoneSignalScanningTime(uNow, type),
				                    getAvgPower_RadioScanning(context));
		return power / 1000;
	}
	
	/**
	 * @param context
	 * @param uNow
	 * @param status
	 * @param type
	 * @return
	 */
	public static double getWifiUsage(Context context, long uNow, BatteryStatsImpl status, int type) {
		double power = 0;
		// TODO(zhoujb): there's something missing add in android code, please follow.
		// 1. wifi on
//		power += calcPowerUsage_mA(status.getWifiOnTime(uNow, type),
//				                    getAvgPower_WifiOn(context));
//		// 2. wifi running
//		power += calcPowerUsage_mA(status.getWifiRunningTime(uNow, type),
//		                           getAvgPower_WifiActive(context));
		power += calcPowerUsage_mA(status.getWifiOnTime(uNow, type),
				                    getAvgPower_WifiOn(context));
		return power / 1000;	                     
	}
	
	/**
	 * Cpu idle power
	 * @param context
	 * @param uNow
	 * @param status
	 * @param type
	 * @return
	 */
	public static double getIdleUsage(Context context, long uNow, BatteryStatsImpl status, int type) {
		return calcPowerUsage_mA(uNow - status.getScreenOnTime(uNow, type),
				getAvgPower_CpuIdle(context)) / 1000;
	}

	/**
	 * @param context
	 * @param uNow
	 * @param status
	 * @param type
	 * @return
	 */
	public static double getBluetoothUsage(Context context, long uNow, BatteryStatsImpl status, int type) {
		double power = 0;
		// 1. bluetooth on
		power += calcPowerUsage_mA(status.getBluetoothOnTime(uNow, type),
		                           getAvgPower_BluetoothOn(context));
		// 2. bluetooth at command
		power += (status.getBluetoothPingCount() * getAvgPower_BluetoothOn(context));
		return power / 1000;
	}
	
    /**
     * Notice: this method is low performance, please do not use it in a large looooooooooooooooooooooooooooooooop
     * @param context
     * @param status
     * @param type
     * @return
     */
    public static double getAverageDataCost(Context context, BatteryStatsImpl status, int type) {
        if(sNetAvgPowerUsage > 0.0000001) {
        	return sNetAvgPowerUsage;
        }
        
    	final long WIFI_BPS          = 1000000; // TODO: Extract average bit rates from system 
        final long MOBILE_BPS        = 200000;  // TODO: Extract average bit rates from system
        final double WIFI_POWER      = getAvgPower_WifiActive(context) / 3600;
        final double MOBILE_POWER    = getAvgPower_RadioActive(context)/ 3600;
        
        final long mobileData        = status.getMobileTcpBytesReceived(type) + status.getMobileTcpBytesSent(type);
        final long wifiData          = status.getTotalTcpBytesReceived(type)  + status.getTotalTcpBytesSent(type) - mobileData;
        final long radioDataUptimeMs = status.getRadioDataUptime() / 1000;
        final long mobileBps         = radioDataUptimeMs != 0 ? mobileData * 8 * 1000 / radioDataUptimeMs : MOBILE_BPS;
        double mobileCostPerByte     = MOBILE_POWER / (mobileBps / 8.0);
        double wifiCostPerByte       = WIFI_POWER / (WIFI_BPS / 8);
        
        if (wifiData + mobileData != 0) {
        	sNetAvgPowerUsage = (mobileCostPerByte * mobileData + wifiCostPerByte * wifiData) / (mobileData + wifiData);
            return sNetAvgPowerUsage;
        } else {
            return 0;
        }
    }
	
	public static double getAppsUsage(Context context, long uNow, BatteryStatsImpl status, int type) {
		double power  = 0;
		double netAvg = getAverageDataCost(context, status, type);
		
		SparseArray<? extends Uid> uids = status.getUidStats();
		//TODO(zhoujb): may uidStats.size() will case lower performance ? 
		
		double cpu = 0;
		double sen = 0; 
		double net = 0;	
		for(int i=0; i<uids.size(); ++i) {
			Uid uid = uids.valueAt(i);
			cpu = _getAppCpuUsage(context, uid, type);
			net = getAppTrafficUsage(context, status, uid, type, netAvg);
			sen = getAppSensorUsage(context, uNow, uid, type);
			if(DEBUG) Log.e("xxx", String.format("(:CPU %f :NET %f :SEN %f :SUM %f)", cpu,net,sen,cpu+net+sen));
			power += cpu+net+sen;
		}
		return power;
	}
	
	public interface UidPicker {
		public boolean onPick(Uid uid);
	}
	
	public static double getAppsUsage(Context context, long uNow, BatteryStatsImpl status, int type, UidPicker picker) {
		double power  = 0;
		double netAvg = getAverageDataCost(context, status, type);
		
		SparseArray<? extends Uid> uids = status.getUidStats();
		//TODO(zhoujb): may uidStats.size() will case lower performance ? 
		
		double cpu = 0;
		double sen = 0; 
		double net = 0;	
		for(int i=0; i<uids.size(); ++i) {
			Uid uid = uids.valueAt(i);
			
			// ask picker
			if(picker != null) {
				if(picker.onPick(uid)) {
					continue;
				}
			}
			
			cpu = _getAppCpuUsage(context, uid, type);
			net = getAppTrafficUsage(context, status, uid, type, netAvg);
			sen = getAppSensorUsage(context, uNow, uid, type);
			if(DEBUG) Log.e("xxx", String.format("(:CPU %f :NET %f :SEN %f :SUM %f)", cpu,net,sen,cpu+net+sen));
			power += cpu+net+sen;
		}
		return power;
	}
	
	public static double getAppPowerByUid(Context context, Uid uid, long uNow, BatteryStatsImpl status, int type) {
		double cpu = 0;
		double sen = 0; 
		double net = 0;
		double netAvg = getAverageDataCost(context, status, type);
		
		cpu = _getAppCpuUsage(context, uid, type);
		net = getAppTrafficUsage(context, status, uid, type, netAvg);
		sen = getAppSensorUsage(context, uNow, uid, type);
		//if(DEBUG) Log.e("xxx", String.format("(:CPU %f :NET %f :SEN %f :SUM %f)", cpu,net,sen,cpu+net+sen));
		LOG._(String.format("(:UID %s :CPU %f :NET %f :SEN %f :SUM %f)", uid.getUid(), cpu,net,sen,cpu+net+sen));
		return cpu + sen + net;
	}
	
	/**
	 * Get data traffic power usage of specify application
	 * @param context
	 * @param status
	 * @param uid
	 * @param type
	 * @param netAvg
	 * @return
	 */
	public static double getAppTrafficUsage(Context context, BatteryStatsImpl status, Uid uid, int type, double netAvg) {
	    return (uid.getTcpBytesReceived(type) + uid.getTcpBytesSent(type)) * netAvg;
	}
	
	public static double getAppTrafficUsageByUid(Uid uid, int type) {
		double net = 0;

		net = uid.getTcpBytesReceived(type) + uid.getTcpBytesSent(type);
		return net;
	}
	
	@Deprecated
	public static double _getAppCpuUsage(Context context, Uid uid, int type) {
		double power = 0;
		// get proc status map
		long cpuTime = 0;
		long cpuFgTime = 0;
		final int speedSteps = getNumCpuSpeedSteps(context);
		final double[] powerCpuNormal = new double[speedSteps];
		final long[] cpuSpeedStepTimes = new long[speedSteps];
		for (int p = 0; p < speedSteps; p++) {
			powerCpuNormal[p] = getAvgPower_CpuActive(context, p);
		}

		Map<String, ? extends BatteryStats.Uid.Proc> proc = uid.getProcessStats();
		if (proc.size() > 0) {
			for (Map.Entry<String, ? extends BatteryStats.Uid.Proc> e : proc.entrySet()) {
				if (DEBUG)
					Log.i(TAG, "Process name = " + e.getKey());
				Uid.Proc ps = e.getValue();
				final long userTime = ps.getUserTime(type);
				final long systemTime = ps.getSystemTime(type);
				final long foregroundTime = ps.getForegroundTime(type);
				cpuFgTime += foregroundTime * 10; // convert to millis
				final long tmpCpuTime = (userTime + systemTime) * 10; // convert to millis
				int totalTimeAtSpeeds = 0;
				// Get the total first
				for (int step = 0; step < speedSteps; step++) {
					cpuSpeedStepTimes[step] = ps.getTimeAtCpuSpeedStep(step, type);
					totalTimeAtSpeeds += cpuSpeedStepTimes[step];
				}
				if (totalTimeAtSpeeds == 0)
					totalTimeAtSpeeds = 1;
				// Then compute the ratio of time spent at each speed
				double processPower = 0;
				for (int step = 0; step < speedSteps; step++) {
					double ratio = (double) cpuSpeedStepTimes[step] / totalTimeAtSpeeds;
					processPower += ratio * tmpCpuTime * powerCpuNormal[step];
				}
				cpuTime += tmpCpuTime;
				power += processPower;
				//                    if (highestDrain < processPower) {
				//                        highestDrain = processPower;
				//                        packageWithHighestDrain = e.getKey();
				//                    }               
			}
		}
		power /= 1000;
		return power;
	}
	
	

	
	public static double getAppSensorUsage(Context context, long uNow, Uid uid, int type) {
		double power = 0;
		SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		Map<Integer, ? extends BatteryStats.Uid.Sensor> sensorStats = uid.getSensorStats();
		for (Map.Entry<Integer, ? extends BatteryStats.Uid.Sensor> sensorEntry : sensorStats.entrySet()) {
			Uid.Sensor sensor = sensorEntry.getValue();
			int sensorType = sensor.getHandle();
			BatteryStats.Timer timer = sensor.getSensorTime();
			long sensorTime = timer.getTotalTimeLocked(uNow, type) / 1000;
			double multiplier = 0;
			switch (sensorType) {
			case Uid.Sensor.GPS:
				multiplier = getAvgPower_CpsOn(context);
				break;
			default:
				android.hardware.Sensor sensorData = sensorManager.getDefaultSensor(sensorType);
				if (sensorData != null) {
					multiplier = sensorData.getPower();
					if (DEBUG) {
						Log.i(TAG, "Got sensor " + sensorData.getName() + " with power = " + multiplier);
					}
				}
			}
			power += (multiplier * sensorTime) / 1000;
		}
		return power;
	}
}