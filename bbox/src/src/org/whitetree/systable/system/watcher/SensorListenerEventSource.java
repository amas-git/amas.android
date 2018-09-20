package org.whitetree.systable.system.watcher;

import org.whitetree.systable.LOG;
import org.whitetree.systable.system.IPassiveEventSource;
import org.whitetree.systable.system.SystemChangedEvent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorListenerEventSource implements SensorEventListener, IPassiveEventSource {
	SystemChangedEvent mLastEvent     = null;
	String             mChId          = null;
	int                mSensorType    = Sensor.TYPE_ALL;
	int                mSensorDelay   = SensorManager.SENSOR_DELAY_NORMAL;

	SensorListenerEventSource(String chid) {
		mChId = chid;
	}
	
	@Override
	public SystemChangedEvent getEvent(String key, boolean forceQuery, String filterType) {
		return mLastEvent;
	}
	
	public void setLastEvent(SystemChangedEvent event) {
		mLastEvent = event;
	}
	
	@Override
	public void onCreate() {
		init();
		initDefaultEvent();
	}

	@Override
	public void onDestroy() {
		unregisterSensorListener();
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		SystemChangedEvent event = analySensorEvent(arg0);
		if(event != null) {
			setLastEvent(event);
			//sendNotify(mChId, event.walue);
		}
	}

	protected SystemChangedEvent analySensorEvent(SensorEvent event) {
		return null;
	}
	
	protected void init() {
		mSensorType = Sensor.TYPE_ALL;
		mSensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
	}
	
	protected void initDefaultEvent() {
		mLastEvent = null;
	}

	@Override
	public void subscribe(String key) {
		registerSensorListener();
		LOG._("RefCount", "SensorListenerEventSource CHID = " +mChId+" subscribe... register Listener");
	}

	@Override
	public void unsubscribe(String key) {
		unregisterSensorListener();
		LOG._("RefCount", "SensorListenerEventSource CHID = " +mChId+" unsubscribe... unregister Listener");
	}
	
	private void registerSensorListener() {
		//SensorManager sm = (SensorManager) LocalService.this.getSystemService(Context.SENSOR_SERVICE);
//		Sensor sensor = sm.getDefaultSensor(mSensorType);
//		if (sensor != null) {
//			sm.registerListener(this, sensor, mSensorDelay);
//		}
	}
	
	private void unregisterSensorListener() {
		//SensorManager sm = (SensorManager) LocalService.this.getSystemService(Context.SENSOR_SERVICE);
//		Sensor sensor = sm.getDefaultSensor(mSensorType);
//		if (sensor != null) {
//			sm.unregisterListener(this);
//		}
	}

	@Override
	public void reregistListener() {
		unregisterSensorListener();
		
		// 刷新 原有取值
//		if (mLastEvent != null) {
//			try {
//				float temperatureF = (Float) mLastEvent.walue.get(CHID_SENSOR_TEMP_VALUE_F);
//				float temperatureC = (Float) mLastEvent.walue.get(CHID_SENSOR_TEMP_VALUE_C);
//				//if (LocalStorage.getInstance().useCelsiusTemperature()) {
//					mLastEvent.walue.put(CHID_SENSOR_TEMP_VALUE, String.format("%.1f°C", temperatureC));
//					mLastEvent.walue.put(CHID_SENSOR_TEMP_PERCENT, (int)temperatureC * 100 / 130);
////				} else {
////					mLastEvent.walue.put(CHID_SENSOR_TEMP_VALUE, String.format("%.1f°F", temperatureF));
////					mLastEvent.walue.put(CHID_SENSOR_TEMP_PERCENT, (int)temperatureF * 100 / 130);
////				}
//			} catch (Exception e) {
//			}
//		}
		
		registerSensorListener();
	}
}
