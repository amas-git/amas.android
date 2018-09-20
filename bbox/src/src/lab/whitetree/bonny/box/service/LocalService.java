package lab.whitetree.bonny.box.service;


import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lab.whitetree.bonny.box.LOG;

import org.whitetree.android.service.GenServer;
import org.whitetree.bidget.moom.Moom;
import org.whitetree.bidget.moom.MoomMaster;
import org.whitetree.systable.system.IEventSource;
import org.whitetree.systable.system.SystemChangedEvent;
import org.whitetree.systable.system.SystemReceiverEventSource;
import org.whitetree.systable.system.U;
import org.whitetree.systable.system.Walue;
import org.whitetree.systable.system.watcher.ApplicationStatsWatcher;
import org.whitetree.systable.system.watcher.ApplicationWatcher;
import org.whitetree.systable.system.watcher.CpuPercentWatcher;
import org.whitetree.systable.system.watcher.CpuWatcher;
import org.whitetree.systable.system.watcher.DiskWatcher;
import org.whitetree.systable.system.watcher.MemoryWatcher;
import org.whitetree.systable.system.watcher.PowerWatcher;
import org.whitetree.systable.system.watcher.RunningProcessWatcher;
import org.whitetree.systable.system.watcher.WatcherEventSource;

import com.google.ads.ac;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import client.core.Core;
import client.core.model.Event;
import client.core.model.EventListener;
import client.core.model.Notifiers;


/**
 * @author amas
 *
 */
public class LocalService extends GenServer implements EventListener {
	
	public static final int SERVICE_REFRESH_INTERVAL_MIN = 1000;
	public static final int SERVICE_REFRESH_INTERVAL_MAX = 8000;

	// DONE
	public static final String CHID_MEMORY             = ":memory";
	public static final String CHID_PROC_RUNNING       = ":proc.running";
	public static final String CHID_CPU                = ":cpu";
	
	public static final String CHID_CPU_APP_PERCENT    = CHID_CPU + ".app.percent";
	public static final String CHID_CPU_PERCENT        = CHID_CPU + ".percent";
	

	public static final String CHID_STORAGE_SDCARD     = ":fs.sdcard";
	public static final String CHID_STORAGE_SDCARD_EXT = ":fs.extsdcard";
	public static final String CHID_STORAGE_SYSTEM     = ":fs.system";
	public static final String CHID_STORAGE_DATA       = ":fs.data";
	
	public static final String CHID_NETWORKS           = ":network";

	public static final String CHID_WIFI               = ":wifi";
	
	public static final String[] CHID_GROUP_FS = new String[] { CHID_STORAGE_SDCARD, CHID_STORAGE_SDCARD_EXT, CHID_STORAGE_SYSTEM, CHID_STORAGE_DATA };
	
	public static final String CHID_POWER              = ":power";
	
	public static final String CHID_HISTORY              = ":history";

	public static final String CHID_SENSOR_TEMP          = ":sensor.temp"; // 温度传感器
	public static final String CHID_SENSOR_TEMP_VALUE    = CHID_SENSOR_TEMP + ".value";
	public static final String CHID_SENSOR_TEMP_VALUE_C  = CHID_SENSOR_TEMP + ".value.C";
	public static final String CHID_SENSOR_TEMP_VALUE_F  = CHID_SENSOR_TEMP + ".value.F";
	public static final String CHID_SENSOR_TEMP_PERCENT  = CHID_SENSOR_TEMP + ".percent";
	
	public static final String CHID_APPS_STATS =":apps.stats";

	public static final String CHID_POWERSTATS =":power.stats";
	
	// TODO
	public static final String CHID_APPS           = ":apps";
	public static final String CHID_APPS_BOOTUP    = ":apps.bootup";
	
	public static final String NAME = "LocalService";
	
	
	private static final String DEFAULT_EVENT_SOURCE = "@system-table";
	
	public static final String PACKAGE_NAME       = "com.mobfly.booster";
	public static final String ACTION_START       = PACKAGE_NAME+".ACTION_START";
	public static final String ACTION_SUBSCRIBE   = PACKAGE_NAME+".ACTION_SUBSCRIBE";
	public static final String ACTION_UNSUBSCRIBE = PACKAGE_NAME+".ACTION_UNSUBSCRIBE";
	public static final String ACTION_QUERY       = PACKAGE_NAME+".ACTION_QUERY";
	public static final String ACTION_SUBSCRIBE_WIDGETS_CH  = PACKAGE_NAME + ".ACTION_SUBSCRIBE_WIDGETS_CH";
	public static final String ACTION_SCREEN_STATUS_CHANGED = PACKAGE_NAME + ".ACTION_SCREEN_STATUS_CHANGED";
	public static final String ACTION_REREGIST_TEMPERATURE_LISTENER  = PACKAGE_NAME+".ACTION_REREGISTER_TEMPERATURE_LISTENER";
	public static final String ACTION_TOUCH  = PACKAGE_NAME+"ACTION_TOUCH";
	public static final String ACTION_OPTIMIZE = PACKAGE_NAME+"ACTION_OPTIMIZE";
	
	public static final String OPTIONS  = ":opt";
	public static final String CHANNELS = ":channels";
	public static final String OPTION_MONITOR_INTERVAL = ":opt.monitor.interval";
	
	public static final String CHID_SYSTEM_QUERY_DONE = "system/query-done";
	public static final String EVID_APP_SIZE = "event/app.size";

	// TODO: 试验性质
	public static Notifiers DEFAULT_BROADCAST = new Notifiers("ui");
	public static final Notifiers SELF = new Notifiers(NAME);
	
	static {
		DEFAULT_BROADCAST.addNotifyUri(NAME); // TODO: 没用了，考虑去掉
	}
	
	public interface OnTraverseListener {
		/**
		 * @param key
		 * @param walue
		 * @return return false to stop
		 */
		public boolean onTraverse(String key, IEventSource walue);
	}
	
	/**
	 * 频道列表, 频道种类目前有两种:
	 * 1. {@link SystemTableEventSource}, 用于主动监测信息, 只有一个, ID为 DEFAULT_EVENT_SOURCE
	 * 2. {@link SystemReceiverEventSource}, 用于被动接收信息, 可以有多个, ID可自定义，但不能是DEFAULT_EVENT_SOURCE
	 */
	HashMap<String, IEventSource> mChannels    = new HashMap<String, IEventSource>();
	
	/**
	 * 被订阅的频道 
	 * TODO: 现在订阅频道A, 默认会广播到"ui", 以后应当支持多播
	 */
	HashMap<String, Object>      mSubscribers = new HashMap<String, Object>();
	
	/**
	 * 通过Receiver接收的系统事件缓存
	 */
	ConcurrentHashMap<String, Event> mLastEvent = new ConcurrentHashMap<String, Event>();
	PMonitor mPMonitor = null;
	
	//---------------------------------------------------------------------------------------[ API ]
	// force loop
	public static void startTouch(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, LocalService.class);
		intent.setAction(ACTION_TOUCH);
		context.startService(intent);
	}
	
	public static void startDefault(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, LocalService.class);
		intent.setAction(ACTION_START);
		context.startService(intent);
	}
	
	public static void startSubscribe(Context context, String[] channels, Bundle options) {
		Intent intent = new Intent();
		intent.setClass(context, LocalService.class);
		intent.setAction(ACTION_SUBSCRIBE);
		intent.putExtra(CHANNELS, channels);
		intent.putExtra(OPTIONS,  options);
		context.startService(intent);
	}
	
	public static void startSubscribe(Context context, String[] channels) {
		Bundle options = new Bundle();
		options.putInt(OPTION_MONITOR_INTERVAL, SERVICE_REFRESH_INTERVAL_MIN);
		startSubscribe(context, channels, options);
	}
	
	public static void startUnsubscribe(Context context, String[] channels) {
		Bundle options = new Bundle();
		options.putInt(OPTION_MONITOR_INTERVAL, SERVICE_REFRESH_INTERVAL_MAX); // TODO: 确定低速更新间隔
		startUnsubscribe(context, channels, options);
	}
	
	public static void startSubscribe(Context context, String[] channels, int refreshInterval) {
		if (refreshInterval <= 0) {
			refreshInterval = SERVICE_REFRESH_INTERVAL_MIN;
		}
		Bundle options = new Bundle();
		options.putInt(OPTION_MONITOR_INTERVAL, refreshInterval);
		startSubscribe(context, channels, options);
	}
	
	public static void startUnsubscribe(Context context, String[] channels, Bundle options) {
		Intent intent = new Intent();
		intent.setClass(context, LocalService.class);
		intent.setAction(ACTION_UNSUBSCRIBE);
		intent.putExtra(CHANNELS, channels);
		intent.putExtra(OPTIONS,  options);
		context.startService(intent);
	}
	
	public static void startOptimize(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, LocalService.class);
		intent.setAction(ACTION_OPTIMIZE);
		context.startService(intent);
	}
	
	
	/**
	 * 请求查询指定频道的信息，查询顺序与channels中指定的频道顺序相同
	 * @param context
	 * @param verbose true: query will send extra event on query start 
	 * @param channels
	 */
	public static void startQuery(Context context, String[] channels, boolean verbose) {
		Intent intent = new Intent();
		intent.setAction(ACTION_QUERY);
		intent.setClass(context, LocalService.class);
		intent.putExtra(CHANNELS, channels);
		intent.putExtra(":verbose", verbose);
		context.startService(intent);
	}
	
	public static void startQuery(Context context, String[] channels) {
		startQuery(context,channels,false);
	}
		
	/**
	 * NOTICE: 
	 * 请求查询指定频道的信息，查询顺序与channels中指定的频道顺序相同
	 * TODO: 处理查询频道没有注册的情形
	 * @param context
	 * @param channels
	 */
	public static void startForceQuery(Context context, String[] channels) {
		Intent intent = new Intent();
		intent.setAction(ACTION_QUERY);
		intent.setClass(context, LocalService.class);
		intent.putExtra(CHANNELS, channels);
		intent.putExtra(":force", true);
		context.startService(intent);
	}
	
	public static void startWidgetsSubscribe(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, LocalService.class);
		intent.setAction(ACTION_SUBSCRIBE_WIDGETS_CH);
		context.startService(intent);
	}
	
	public static void startScreenStatusChanged(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, LocalService.class);
		intent.setAction(ACTION_SCREEN_STATUS_CHANGED);
		context.startService(intent);
	}
	
	public static void startReregistTemperatureListener(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, LocalService.class);
		intent.setAction(ACTION_REREGIST_TEMPERATURE_LISTENER);
		context.startService(intent);
	}
	
	public void foreach(OnTraverseListener t) {
		Iterator<Map.Entry<String, IEventSource>> iter = mChannels.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, IEventSource> entry = (Map.Entry<String, IEventSource>) iter.next();
			try {
				if (!t.onTraverse((String) entry.getKey(), (IEventSource) entry.getValue())) {
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private OnTraverseListener mLooper = new OnTraverseListener() {
		
		@Override
		public boolean onTraverse(String key, IEventSource esource) {
			if(esource instanceof WatcherEventSource) {
				onTraverseWatcherEventSource((WatcherEventSource)esource);
			}
			return true;
		}
	};
	
	@Override
	protected void loop() {
		foreach(mLooper);
	}
	
	private void onTraverseWatcherEventSource(WatcherEventSource esource) {
		//esource.isExpired()
		//LOG._("==[ onTraverseWatcherEventSource ] : " + esource);
		if(esource.isExpired()) {
			SystemChangedEvent event = esource.getEvent("", false, null);
			//LOG._("event ----> " + event);
			
			send(event);
		}
	}
	
	@Override
	public void onEvent(Event event) {
		// XXX: 暂时没事么可做的
	}

	//--------------------------------------------------------------------[ 注册 ]
	@Override
	public void onCreate() {
		mPMonitor = new PMonitor(this);
		mPMonitor.register(this, true);
		super.onCreate();
		setName(NAME);
		Core.I().addListener(NAME, this);
		onCreateChannels();
	}
	
	static ApplicationWatcher mApplicationWatcher = null;
	
	private void onCreateChannels() {
		// 系统信息，主动监控
		// mChannels.put(DEFAULT_EVENT_SOURCE, new SystemTableEventSource());
		installChannel(new MemoryWatcher(this, 5000));
		mApplicationWatcher = new ApplicationWatcher(this);
		installChannel(mApplicationWatcher);
		installChannel(new RunningProcessWatcher(this));
		installChannel(new CpuWatcher(this, 1000));
		//installChannel(new BootupApplicationWatcher(this));
		installChannel(new CpuPercentWatcher(this));
		installChannel(new ApplicationStatsWatcher(this, DEFAULT_BROADCAST));
		//installChannel(new AppPercentWatcher(this, 5000));
		//installChannel(new HistoryWatcher(this));
		installChannel(new DiskWatcher(this, CHID_STORAGE_DATA, new File("/data"), 5000));
		installChannel(new DiskWatcher(this, CHID_STORAGE_SYSTEM, new File("/system"), 5000));
		installChannel(new DiskWatcher(this, CHID_STORAGE_SDCARD, Environment.getExternalStorageDirectory(), 5000));
		installChannel(new DiskWatcher(this, CHID_STORAGE_SDCARD_EXT, U.getExternalSdcardMountPoint(), 5000));
		//installChannel(new PowerStatsWatcher(this));
		
		installChannel(new PowerWatcher(this, DEFAULT_BROADCAST));
		
		
		foreach(new OnTraverseListener() {
			@Override
			public boolean onTraverse(String key, IEventSource esource) {
				esource.onCreate();
				return true;
			}
		});
	}
	
	private void installChannel(SystemReceiverEventSource ses) {
		mChannels.put(ses.getId(), ses);
	}
	
	private void installChannel(WatcherEventSource wes) {
		mChannels.put(wes.getId(), wes);
	}


	protected void onHandleIntent(Intent intent, int startId) {
		String action = intent.getAction();
		if(ACTION_SUBSCRIBE.equals(action)) {
			onHandleSubscribe(intent);
		} else if (ACTION_UNSUBSCRIBE.equals(action)) {
			onHandleUnsubscribe(intent);
		} else if (ACTION_QUERY.equals(action)) {
			onHandleQuery(intent);
		} else if (ACTION_SUBSCRIBE_WIDGETS_CH.equals(action)) {
			//onHandleSubscribeWidgetsCh();
		} else if (ACTION_SCREEN_STATUS_CHANGED.equals(action)) {
			onHandleScreenStatusChangedAction();
		} else if (ACTION_REREGIST_TEMPERATURE_LISTENER.equals(action)) {
			onHandleReregistTemperatureListenerAction();
		} else if (ACTION_TOUCH.equals(action)) {
			onHandleTouch();
		} else if (ACTION_OPTIMIZE.equals(action)) {
			onHandleOptimize();
		}
	}
	
	private void onHandleOptimize() {
		// 1. kill task
		
		// 2. cache clearn
		
		// 3. clear history
	}

	private void onHandleTouch() {
		setUpdateInterval(-1, true);
	}
	
	private void onHandleQuery(Intent intent) {
		if(intent.hasExtra(CHANNELS)) {
			String[] xs = intent.getStringArrayExtra(CHANNELS);
			boolean force = false;
			if(intent.hasExtra(":force")) {
				force = true;
			}
			
			String filter = null;
			if(intent.hasExtra(":filter")) {
				filter = intent.getStringExtra(":filter");
			}

			
			boolean verbose = false;
			if(intent.hasExtra(":verbose")) {
				verbose = intent.getBooleanExtra(":verbose", false);
			}
			
			
			for(String x : xs) {
				LOG._("[ QUERY ] ----------: " + x);
				IEventSource esource = mChannels.get(x);
				if(esource == null) {
					LOG._("[ QUERY ] ----------: not found '" + x + "'");
					continue;
				} 
				
				if(verbose) {
					send(new QueryStartEvent(x));
				}
				// 追加到广播列表
				SystemChangedEvent event = esource.getEvent(x,force,filter);
				
				send(event);
				LOG._("[ QUERY DONE ] ----------: " + x);
			}
			
			// send query end event
			LOG._("[ QUERY DONE ]--------------");
			send(new SystemChangedEvent(CHID_SYSTEM_QUERY_DONE, null));
		}
	}
	

	private void onHandleUnsubscribe(Intent intent) {
		LOG._("取消注册: " + "-------------------------------");
		if(intent.hasExtra(OPTIONS)) {
			Bundle options = intent.getBundleExtra(OPTIONS);
			onHandleOptions(options);
		}
		
		String[] xs = intent.getStringArrayExtra(CHANNELS);
		for(String x: xs) {
			LOG._("取消注册: " + x);
			//maintainSubScribers(x, false);
		}
	}

	private void onHandleOptions(Bundle options) {
		int interval = options.getInt(OPTION_MONITOR_INTERVAL);
		if(interval > 0) {
			setUpdateInterval(interval, true);
		}
	}

	private void onHandleSubscribe(Intent intent) {
		if(intent.hasExtra(OPTIONS)) {
			Bundle options = intent.getBundleExtra(OPTIONS);
			onHandleOptions(options);
		}
	}
		
	// 屏幕亮/暗状态改变
	private void onHandleScreenStatusChangedAction() {
		onScreenStatusChanged();
	}
	
	private void onHandleReregistTemperatureListenerAction() {
		String[] chids = {CHID_POWER, CHID_SENSOR_TEMP};
		
//		for (String id : chids) {
//			IEventSource esource = mChannels.get(id);
//			if(esource != null && esource instanceof IPassiveEventSource) {
//				((IPassiveEventSource) esource).reregistListener();
//			}
//		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mPMonitor.unregister();
		onDestroyChannels();
		Core.I().removeListener(NAME, this);
	}
	
	private void onDestroyChannels() {
		foreach(new OnTraverseListener() {
			@Override
			public boolean onTraverse(String key, IEventSource esource) {
				esource.onDestroy();
				return true;
			}
		});
	}
	
	private static void send(final SystemChangedEvent event) {
		if(event != null) {
			
			// update moom data set
			MoomMaster.getInstance().foreach(new MoomMaster.OnTraverseListener() {
				public boolean onTraverse(String url, Moom moom) {
					//System.out.println("moom ------------------- " + moom.toString());
					if (moom.getTag().contains(event.id)) {
						try {
							moom.eval(event.walue);
						} catch (Exception e) {
							LOG._("MOOM EVAL ERROR : " + e.getLocalizedMessage());
						}
					}
					// next element
					return true;
				}
			});
			
			LOG._("<<SYSTEM CHANGED>> : " + event);
			event.setTo(DEFAULT_BROADCAST);
			Core.I().push(event);
		}
	}
	
	public static void sendSelf(Event event) {
		event.setTo(SELF);
		Core.I().push(event);
	}
	
	// TODO: 想办法去掉，参看:  AppFlagFIlter
	@Deprecated
	public static ApplicationInfo getApplicationInfo(String procName) {
		Walue w = mApplicationWatcher.get(null);
		ResolveInfo i = ApplicationWatcher.get(w, procName);
		if(i == null) {
			return null;
		}
		if(i.activityInfo == null) {
			return null;
		}
		return i.activityInfo.applicationInfo;
	}
}
