package org.whitetree.systable.system.watcher;

import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.systable.system.SystemChangedEvent;
import org.whitetree.systable.system.SystemReceiverEventSource;
import org.whitetree.systable.system.U;
import org.whitetree.systable.system.Walue;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import client.core.model.Notifiers;

public class PowerWatcher extends SystemReceiverEventSource {
	
	public static final String CHID_POWER = LocalService.CHID_POWER;
	
	public static final String WALUE_POWER_LEVEL     = CHID_POWER+".level";
	public static final String WALUE_POWER_PLUGGED   = CHID_POWER+".plugged";
	public static final String WALUE_POWER_STATUS    = CHID_POWER+".status";
	public static final String WALUE_POWER_VOLTAGE   = CHID_POWER+".voltage";
	public static final String WALUE_POWER_VOLTAGE_V = CHID_POWER+".voltage.v";
	public static final String WALUE_POWER_VOLTAGE_PERCENT = CHID_POWER+".voltage.percent";
	public static final String WALUE_POWER_TEMP      = CHID_POWER+".temperature";
	public static final String WALUE_POWER_TEMP_F    = CHID_POWER+".temperature.F";
	public static final String WALUE_POWER_TEMP_C      = CHID_POWER+".temperature.C";
	public static final String WALUE_POWER_TEMP_PERCENT   = CHID_POWER+".temperature.percent";
	public static final String WALUE_POWER_TEMP_SCALE  =   CHID_POWER+".scale";
	public static final String WALUE_POWER_HEALTH      =CHID_POWER+".health";
	
	public PowerWatcher(Context context, Notifiers to) {
		super(context, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		setId(CHID_POWER);
		setEventTo(to);
	}
	
	@Override
	public SystemChangedEvent onHandleIntent(Context context, Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
			Walue data = new Walue();
			int voltage = intent.getIntExtra("voltage", 0);
			data.put(WALUE_POWER_LEVEL, intent.getIntExtra("level", 0));
			data.put(WALUE_POWER_PLUGGED, tagPlugged(intent.getIntExtra("plugged", 0)));
			data.put(WALUE_POWER_STATUS, tagStatus(intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN)));
			data.put(WALUE_POWER_VOLTAGE, voltage);
			data.put(WALUE_POWER_VOLTAGE_V, String.format("%.2f", voltage / 1000f));
			data.put(WALUE_POWER_VOLTAGE_PERCENT, (100 * voltage) / (1000 * 12));
			data.put(WALUE_POWER_HEALTH, tagHealth(intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN)));
			data.put(WALUE_POWER_TEMP_SCALE, intent.getIntExtra("scale", 100));

			// 温度值，需要考虑单位 华氏还是摄氏
			float tempC = intent.getIntExtra("temperature", 30) / 10.0f;
			float tempF = U.temperatureUnitConvertToF(tempC);
			data.put(WALUE_POWER_TEMP_C, tempC);
			data.put(WALUE_POWER_TEMP_F, tempF);
			//			if (LocalStorage.getInstance().useCelsiusTemperature()) {
			data.put(WALUE_POWER_TEMP, String.format("%.1f°C", tempC));
			data.put(WALUE_POWER_TEMP_PERCENT, (int) (tempC * 100 / 130));
			//
			//			} else {
			//				data.put(WALUE_POWER_TEMP,         String.format("%.1f°F", tempF));	
			//				data.put(WALUE_POWER_TEMP_PERCENT, (int)(tempF * 100 / 130));
			//			}

			return new SystemChangedEvent(CHID_POWER, data);
		}
		return null;
	}

	private String tagStatus(int status) {
		if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
			return "charging";
		} else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
			return "discharging";
		} else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
			return "!charging";
		} else if (status == BatteryManager.BATTERY_STATUS_FULL) {
			return "full";
		} else {
			return "unknown";
		}
	}

	/**
	 * 这种转换只是从维护角度上考虑的, 直接在moom配置中写数字也是可以的
	 * 
	 * @param plugType
	 * @return
	 */
	private String tagPlugged(int plugType) {
		switch (plugType) {
		case 0:
			return "unplugged";
		case BatteryManager.BATTERY_PLUGGED_AC:
			return "ac";
		case BatteryManager.BATTERY_PLUGGED_USB:
			return "usb";
		case (BatteryManager.BATTERY_PLUGGED_AC | BatteryManager.BATTERY_PLUGGED_USB):
			return "ac-usb";
		default:
		}
		return "unknown";
	}

	/**
	 * 返回电池健康状况的tag
	 * 
	 * @param intExtra
	 * @return
	 */
	protected String tagHealth(int health) {
		if (health == BatteryManager.BATTERY_HEALTH_GOOD) {
			return "good";
		} else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
			return "overheat";
		} else if (health == BatteryManager.BATTERY_HEALTH_DEAD) {
			return "dead";
		} else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
			return "overvoltage";
		} else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
			return "unknown";
		} else if (health == 7) {
			return "cold";
		} else {
			return "unknown";
		}
	}

	public static int getPowerLevel(Walue walue) {
	    return walue.getInt(WALUE_POWER_LEVEL);
    }
}

	/*
mChannels.put(CHID_SENSOR_TEMP, new SensorListenerEventSource(CHID_SENSOR_TEMP) {
	@Override
	protected SystemChangedEvent analySensorEvent(SensorEvent event) {
		Walue data = new Walue();
		
		// 温度 要区分单位 
		float temperatureC = event.values[0];
		float temperatureF = U.temperatureUnitConvertToF(temperatureC); 

		data.put(CHID_SENSOR_TEMP_VALUE_C, temperatureC);
		data.put(CHID_SENSOR_TEMP_VALUE_F, temperatureF);
//		if (LocalStorage.getInstance().useCelsiusTemperature()) {
			data.put(CHID_SENSOR_TEMP_VALUE, String.format("%.1f°C", temperatureC));
			data.put(CHID_SENSOR_TEMP_PERCENT, (int)(temperatureC * 100 / 130));

//		} else {
//			data.put(CHID_SENSOR_TEMP_VALUE, String.format("%.1f°F", temperatureF));
//			data.put(CHID_SENSOR_TEMP_PERCENT, (int)(temperatureF * 100 / 130));
//		}
		
		return new SystemChangedEvent(CHID_SENSOR_TEMP, data);
	}
	
	@Override
	protected void init() {
		mSensorType = Sensor.TYPE_TEMPERATURE;
		mSensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
	}
	
	protected void initDefaultEvent() {
		Walue data = new Walue();
		data.put(CHID_SENSOR_TEMP_VALUE, "N/A");
		data.put(CHID_SENSOR_TEMP_PERCENT, 0);
		mLastEvent = new SystemChangedEvent(CHID_SENSOR_TEMP, data);
	}
});

// ---------------------------------------------------------------[ 真正的初始化 ]
Iterator<Map.Entry<String, IEventSource>> iter = mChannels.entrySet().iterator();
while (iter.hasNext()) {
	Map.Entry<String, IEventSource> entry = (Map.Entry<String, IEventSource>) iter.next();
	
	//String      k = (String) entry.getKey();
	IEventSource v = (IEventSource) entry.getValue();
	v.onCreate(this);
}
}
	
*/