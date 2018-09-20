package lab.whitetree.bonny.box.storage;

import java.util.ArrayList;
import java.util.Arrays;

import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.service.NotificationService;
import lab.whitetree.bonny.box.ui.SettingActivity;

import org.whitetree.systable.data.MoomCellData;
import org.whitetree.systable.system.U;

import android.content.Context;
import android.text.TextUtils;

public class LocalStorage {
	private static LocalStorage sInstance = new LocalStorage();
	private static final String MOOM_DB = "moom";
	private static final String SP_KEY_HAS_RATED_US = "has_rated_us";
	private static final String SP_KEY_LAST_DISPLAY_RATE_US_TIME = "last_display_rate_us_time";
	private static final String SP_KEY_WIDGETS_SUBSCRIBED_CHIDS = "widgets_chids";
	private static final String SP_KEY_WIDGET_PREFIX = "widget_";
	private static final String SP_KEY_WIDGET_MOOM_PREFIX = "widget_moom_";
	private static final String SP_KEY_TEMPERATURE_UNIT_TYPE_CELSIUS = "temperature_unit_type_celsius";
	private static final String SP_KEY_MOOMCELL_DATA_PREFIX = "moomcell_";
	private static final String SP_KEY_FIRST_ENTERED_MAIN_ACTIVITY = "first_entered_main_activity";
	private static final String SP_KEY_NOTIFICATION_MOOM_CFGS = "notification_moom_cfgs";
	private static final String SP_KEY_NOTIFICATION_MOOM_CHIDS = "notification_moom_chids";

	private static final String ITEMS_SEPARATOR = ";";
	

	private static final String NOTIFICATION_MOOM_DEFAULT_CFGS = "res:///moom_notification_mem" + ITEMS_SEPARATOR 
																+ "res:///moom_notification_cpu" + ITEMS_SEPARATOR 
																+ "res:///moom_notification_sdcard" + ITEMS_SEPARATOR 
																+ "res:///moom_notification_battery" + ITEMS_SEPARATOR;
	private static final String NOTIFICATION_MOOM_DEFAULT_CHIDS = LocalService.CHID_MEMORY + ITEMS_SEPARATOR 
																+ LocalService.CHID_CPU + ITEMS_SEPARATOR 
																+ LocalService.CHID_STORAGE_SDCARD + ITEMS_SEPARATOR 
																+ LocalService.CHID_POWER + ITEMS_SEPARATOR ;

	private boolean mUseTemperatureUnitCelsius = true;
	private ArrayList<MoomCellData> mNotificationMoomData = null;

	private Context mContext = null;

	private LocalStorage() {
	}

	public void init(Context context) {
		mContext = context;

		// 温度单位 设置 初始化
		loadUseTemperatureUnitType();
	}

	public static LocalStorage getInstance() {
		return sInstance;
	}

	public void saveMoom(String key, String url) {
		mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE).edit()
				.putString(key, url).commit();
	}

	public String getMoom(String key) {
		return mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE)
				.getString(key, null);
	}

	public String getMoom(String key, String failThroght) {
		return mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE)
				.getString(key, failThroght);
	}

	synchronized public void setWidgetsSubscribeChids(String[] chids,
			boolean isSubscribe) {
		if (chids != null && chids.length > 0) {
			String[] ids = getWidgetsSubscribedChids();
			ArrayList<String> idList = new ArrayList<String>(Arrays.asList(ids));
			if (isSubscribe) {
				for (String chid : chids) {
					idList.add(chid);
				}
			} else {
				for (String chid : chids) {
					idList.remove(chid);
				}
			}

			saveChidsList(idList);
		}
	}

	private void saveChidsList(ArrayList<String> chids) {
		String idString = "";
		if (chids != null) {
			for (String id : chids) {
				if (!TextUtils.isEmpty(id)) {
					idString = id + ITEMS_SEPARATOR + idString;
				}
			}
		}

		if (TextUtils.isEmpty(idString)) {
			// 清除
			mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE).edit()
					.remove(SP_KEY_WIDGETS_SUBSCRIBED_CHIDS).commit();
		} else {
			mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE).edit()
					.putString(SP_KEY_WIDGETS_SUBSCRIBED_CHIDS, idString)
					.commit();
		}
	}

	public String[] getWidgetsSubscribedChids() {
		return mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE)
				.getString(SP_KEY_WIDGETS_SUBSCRIBED_CHIDS, "")
				.split(ITEMS_SEPARATOR);
	}

	public void saveWidgetChid(int widgetId, String chid) {
		if (!TextUtils.isEmpty(chid)) {
			mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE).edit()
					.putString((SP_KEY_WIDGET_PREFIX + widgetId), chid)
					.commit();
		}
	}

	public String getWidgetChid(int widgetId) {
		return mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE)
				.getString((SP_KEY_WIDGET_PREFIX + widgetId), null);
	}

	public void saveWidgetMoomUrl(int widgetId, String url) {
		if (!TextUtils.isEmpty(url)) {
			mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE).edit()
					.putString((SP_KEY_WIDGET_MOOM_PREFIX + widgetId), url)
					.commit();
		}
	}

	public String getWidgetMoomUrl(int widgetId) {
		return mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE)
				.getString((SP_KEY_WIDGET_MOOM_PREFIX + widgetId), null);
	}

	public void clearWidgetCfg(int widgetId) {
		mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE).edit()
				.remove((SP_KEY_WIDGET_PREFIX + widgetId))
				.remove((SP_KEY_WIDGET_MOOM_PREFIX + widgetId)).commit();
	}

	public boolean isNotificationBarEnable() {
		return mContext.getSharedPreferences(
				SettingActivity.getSettingSharedPrefFileName(mContext),
				Context.MODE_PRIVATE).getBoolean(
				SettingActivity.SP_KEY_ENABLE_NOTIFICATION_BAR, true);
	}

	public void setHasRatedUs(boolean hasRated) {
		mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE).edit()
				.putBoolean(SP_KEY_HAS_RATED_US, hasRated).commit();
	}

	public boolean hasRatedUs() {
		return mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE)
				.getBoolean(SP_KEY_HAS_RATED_US, false);
	}

	public void setLastDisplayRateUsTime(long time) {
		mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE).edit()
				.putLong(SP_KEY_LAST_DISPLAY_RATE_US_TIME, time).commit();
	}

	public long getLastDisplayrateUsTime() {
		return mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE)
				.getLong(SP_KEY_LAST_DISPLAY_RATE_US_TIME, 0);
	}

	private void loadUseTemperatureUnitType() {
		mUseTemperatureUnitCelsius = mContext.getSharedPreferences(MOOM_DB,
				Context.MODE_PRIVATE).getBoolean(
				SP_KEY_TEMPERATURE_UNIT_TYPE_CELSIUS, true);
	}

	// 温度单位 是否是摄氏温度 ？ 或者 华氏温度
	// 默认是摄氏温度
	public boolean useCelsiusTemperature() {
		return mUseTemperatureUnitCelsius;
	}

	// true 摄氏 false 华氏
	public void setTemperatureUnitAsCelsius(boolean flag) {
		if (mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE).edit()
				.putBoolean(SP_KEY_TEMPERATURE_UNIT_TYPE_CELSIUS, flag)
				.commit()) {
			mUseTemperatureUnitCelsius = flag;
		}
	}

	// moomCfg;moomTag;moomCHID;cellType;
	public boolean saveMoomCellData(MoomCellData celldata) {
		if (TextUtils.isEmpty(celldata.mTag) || TextUtils.isEmpty(celldata.mMoomViewCfg)) {
			return false;
		}
		return mContext
				.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE)
				.edit()
				.putString(SP_KEY_MOOMCELL_DATA_PREFIX + celldata.mTag,
						  celldata.mMoomViewCfg + ITEMS_SEPARATOR
						+ celldata.mMoomViewTag + ITEMS_SEPARATOR
						+ celldata.mMoomCHID + ITEMS_SEPARATOR
						+ celldata.mCellType + ITEMS_SEPARATOR)
				.commit();
	}
	public boolean getMoomCellData(MoomCellData celldata) {
		// moomCfg;moomTag;moomCHID;cellType;
		String[] strs = mContext
				.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE)
				.getString(SP_KEY_MOOMCELL_DATA_PREFIX + celldata.mTag, "")
				.split(ITEMS_SEPARATOR);
		if (strs == null || strs.length != 4) {
			return false;
		}
		celldata.mMoomViewCfg = strs[0];
		celldata.mMoomViewTag = strs[1];
		celldata.mMoomCHID = strs[2];
		celldata.mCellType = Integer.valueOf(strs[3]);
		return true;
	}
	public boolean clearMoomCellData(String tag) {
		return mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE)
				.edit()
				.remove((SP_KEY_MOOMCELL_DATA_PREFIX + tag))
				.commit();
	}
	
	public boolean isFirstEnteredMainActivity() {
		return mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE)
				.getBoolean(SP_KEY_FIRST_ENTERED_MAIN_ACTIVITY, true);
	}
	public void setFirstEnteredMainActivityFlag(boolean isFirst) {
		mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE).edit()
			.putBoolean(SP_KEY_FIRST_ENTERED_MAIN_ACTIVITY, isFirst)
			.commit();
	}
	
	// 获取 notification bar 上面4个位置的moom信息 包括 config 和 chid
	public ArrayList<MoomCellData> getNotificationMoomData() {
		if (mNotificationMoomData == null) {
			mNotificationMoomData = new ArrayList<MoomCellData>();
			for (int i = 0; i < NotificationService.NOTIFICATION_MOOM_VIEW_COUNT; ++i) {
				mNotificationMoomData.add(new MoomCellData());
			}
			
			String cfgs = mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE)
									.getString(SP_KEY_NOTIFICATION_MOOM_CFGS, NOTIFICATION_MOOM_DEFAULT_CFGS);
			String chids = mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE)
									.getString(SP_KEY_NOTIFICATION_MOOM_CHIDS, NOTIFICATION_MOOM_DEFAULT_CHIDS);
			String cfg[] = cfgs.split(ITEMS_SEPARATOR);
			String chid[] = chids.split(ITEMS_SEPARATOR);
			
			if (cfg == null || chid == null 
					|| cfg.length != NotificationService.NOTIFICATION_MOOM_VIEW_COUNT 
					|| chid.length != NotificationService.NOTIFICATION_MOOM_VIEW_COUNT) {
				return null;
			}
			for (int i = 0; i < NotificationService.NOTIFICATION_MOOM_VIEW_COUNT; ++i) {
				mNotificationMoomData.get(i).mMoomViewCfg = cfg[i];
				mNotificationMoomData.get(i).mMoomCHID = chid[i];
			}
		}

		// clone 一份
		ArrayList<MoomCellData> clone = new ArrayList<MoomCellData>(mNotificationMoomData.size());
	    for(MoomCellData item: mNotificationMoomData) {
	    	clone.add((MoomCellData)item.clone());
	    }
		return clone;
	}
	
	public boolean saveNotificationMoomData(ArrayList<MoomCellData> moomdata) {
		if (moomdata == null || moomdata.size() != NotificationService.NOTIFICATION_MOOM_VIEW_COUNT) {
			return false;
		}
		String cfgs = "";
		String chids = "";
		MoomCellData mcd = null;
		for (int i = 0; i < NotificationService.NOTIFICATION_MOOM_VIEW_COUNT; ++i) {
			mNotificationMoomData.get(i).mMoomCHID = moomdata.get(i).mMoomCHID;
			mNotificationMoomData.get(i).mMoomViewCfg = moomdata.get(i).mMoomViewCfg;
			
			mcd = moomdata.get(i);
			if (mcd != null) {
				cfgs += mcd.mMoomViewCfg + ITEMS_SEPARATOR;
				chids += mcd.mMoomCHID + ITEMS_SEPARATOR;
			}
		}

		return mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE).edit()
			.putString(SP_KEY_NOTIFICATION_MOOM_CFGS, cfgs)
			.putString(SP_KEY_NOTIFICATION_MOOM_CHIDS, chids)
			.commit();
	}

	public String getMac() {
		String mac = mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE).getString("mac", "");
		if(TextUtils.isEmpty(mac)) {
			mac = U.getMacAddress(mContext);
			if(TextUtils.isEmpty(mac)){
				return "N/A";
			}
			
			mac.toUpperCase();
			mContext.getSharedPreferences(MOOM_DB, Context.MODE_PRIVATE)
			.edit()
			.putString("mac", mac)
			.commit();
		}
		return mac;
	}
}
