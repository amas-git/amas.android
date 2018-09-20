package org.whitetree.systable.system.watcher;


import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.systable.system.U;
import org.whitetree.systable.system.Walue;
import org.whitetree.systable.system.Watcher;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

public class WifiWatcher extends Watcher {
	// WIFI的信号强度总共分为几级？ =4时，最大强度为3级， 最小强度为0级。由此计算百分比
	private static final int WIFI_RSSI_MAX_LEVEL = 5;

	public static final String WALUE_KEY_WIFI = LocalService.CHID_WIFI;
	
	public static final String WALUE_KEY_WIFI_ON                   = WALUE_KEY_WIFI + ".on";                 // wifi打开
	public static final String WALUE_KEY_WIFI_OFF                  = WALUE_KEY_WIFI + ".off";                // wifi关闭
	public static final String WALUE_KEY_WIFI_ENABLE               = WALUE_KEY_WIFI + ".enable";             // wifi是否可用 true false
	public static final String WALUE_KEY_WIFI_NAME                 = WALUE_KEY_WIFI + ".name";               // wifi名称
	public static final String WALUE_KEY_WIFI_LINK_SPEED           = WALUE_KEY_WIFI + ".linkspeed";          // wifi传输速率 Mbps
	public static final String WALUE_KEY_WIFI_LINK_SPEED_FORMATTED = WALUE_KEY_WIFI + ".linkspeed.formatted";// wifi传输速率 Mbps 用于显示
	public static final String WALUE_KEY_WIFI_IP_INT               = WALUE_KEY_WIFI + ".ip.int";             // 当前IP地址 int类型
	public static final String WALUE_KEY_WIFI_IP_STR               = WALUE_KEY_WIFI + ".ip.str";             // 当前IP地址 String类型
	public static final String WALUE_KEY_WIFI_RSSI                 = WALUE_KEY_WIFI + ".rssi";               // wifi信号强度 dB
	public static final String WALUE_KEY_WIFI_RSSI_FORMATTED       = WALUE_KEY_WIFI + ".rssi.formatted";	 // wifi信号强度 用于显示
	public static final String WALUE_KEY_WIFI_RSSI_LEVEL           = WALUE_KEY_WIFI + ".rssi.level";         // wifi信号强度 级别，根据设定 共分几级
	public static final String WALUE_KEY_WIFI_RSSI_LEVEL_PERCENT   = WALUE_KEY_WIFI + ".rssi.level.percent"; // wifi信号强度 级别百分比，由级别计算出来
	public static final String WALUE_KEY_WIFI_RSSI_LEVEL_PERCENT_FORMATTED   = WALUE_KEY_WIFI + ".rssi.level.percent.formatted"; // wifi信号强度 百分比 显示用

	public static final String WALUE_KEY_WIFI_RSSI_LEVEL_0   = WALUE_KEY_WIFI + ".rssi.level.0"; // 信号强度 0级
	public static final String WALUE_KEY_WIFI_RSSI_LEVEL_1   = WALUE_KEY_WIFI + ".rssi.level.1"; // 信号强度 1级
	public static final String WALUE_KEY_WIFI_RSSI_LEVEL_2   = WALUE_KEY_WIFI + ".rssi.level.2"; // 信号强度 2级
	public static final String WALUE_KEY_WIFI_RSSI_LEVEL_3   = WALUE_KEY_WIFI + ".rssi.level.3"; // 信号强度 3级
	public static final String WALUE_KEY_WIFI_RSSI_LEVEL_4   = WALUE_KEY_WIFI + ".rssi.level.4"; // 信号强度 4级
	
	private WifiManager mWifiManager = null;
	
	public WifiWatcher(long updateInterval) {
		setUpdateInterval(updateInterval);
	}
	
	@Override
	protected void update(Context context, Walue walue, String filterType) {
		if (mWifiManager == null) {
			mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		}

		walue.put(WALUE_KEY_WIFI_RSSI_LEVEL_0, "false");
		walue.put(WALUE_KEY_WIFI_RSSI_LEVEL_1, "false");
		walue.put(WALUE_KEY_WIFI_RSSI_LEVEL_2, "false");
		walue.put(WALUE_KEY_WIFI_RSSI_LEVEL_3, "false");
		walue.put(WALUE_KEY_WIFI_RSSI_LEVEL_4, "false");

		WifiInfo wifiInfo = null;
		if (mWifiManager.isWifiEnabled()) {
			wifiInfo = mWifiManager.getConnectionInfo();
			
			int linkSpeed = wifiInfo.getLinkSpeed();
			int rssi = wifiInfo.getRssi();
			int rssiLevel = WifiManager.calculateSignalLevel(rssi, WIFI_RSSI_MAX_LEVEL);
			int rssiLevelPercent = (rssiLevel + 1) * 100 / WIFI_RSSI_MAX_LEVEL;
			String ssid = wifiInfo.getSSID();
			ssid = TextUtils.isEmpty(ssid) ? "" : ssid;
			
			walue.put(WALUE_KEY_WIFI_ON, "true");
			walue.put(WALUE_KEY_WIFI_OFF, "false");
			walue.put(WALUE_KEY_WIFI_ENABLE, true);

			walue.put(WALUE_KEY_WIFI_NAME, ssid);
			walue.put(WALUE_KEY_WIFI_LINK_SPEED, linkSpeed);
			walue.put(WALUE_KEY_WIFI_LINK_SPEED_FORMATTED, String.valueOf(linkSpeed) + WifiInfo.LINK_SPEED_UNITS);
			walue.put(WALUE_KEY_WIFI_IP_INT, wifiInfo.getIpAddress());
			walue.put(WALUE_KEY_WIFI_IP_STR, U.ipAddrIntToString(wifiInfo.getIpAddress()));
			walue.put(WALUE_KEY_WIFI_RSSI, rssi);
			walue.put(WALUE_KEY_WIFI_RSSI_FORMATTED, String.valueOf(rssi));
			walue.put(WALUE_KEY_WIFI_RSSI_LEVEL, rssiLevel);
			walue.put(WALUE_KEY_WIFI_RSSI_LEVEL_PERCENT, rssiLevelPercent);
			walue.put(WALUE_KEY_WIFI_RSSI_LEVEL_PERCENT_FORMATTED, String.valueOf(rssiLevelPercent) + "%");
			
			switch (rssiLevel) {
			case 4:
				walue.put(WALUE_KEY_WIFI_RSSI_LEVEL_4, "true");
			case 3:
				walue.put(WALUE_KEY_WIFI_RSSI_LEVEL_3, "true");
			case 2:
				walue.put(WALUE_KEY_WIFI_RSSI_LEVEL_2, "true");
			case 1:
				walue.put(WALUE_KEY_WIFI_RSSI_LEVEL_1, "true");
			case 0:
				walue.put(WALUE_KEY_WIFI_RSSI_LEVEL_0, "true");
				break;
			default:
				break;
			}
			
		} else {
			walue.put(WALUE_KEY_WIFI_ON, "false");
			walue.put(WALUE_KEY_WIFI_OFF, "true");
			walue.put(WALUE_KEY_WIFI_ENABLE, false);

			walue.put(WALUE_KEY_WIFI_NAME, "");
			walue.put(WALUE_KEY_WIFI_LINK_SPEED, 0);
			walue.put(WALUE_KEY_WIFI_LINK_SPEED_FORMATTED, "");
			walue.put(WALUE_KEY_WIFI_IP_INT, 0);
			walue.put(WALUE_KEY_WIFI_IP_STR, "");
			walue.put(WALUE_KEY_WIFI_RSSI, -99);
			walue.put(WALUE_KEY_WIFI_RSSI_LEVEL, 0);
			walue.put(WALUE_KEY_WIFI_RSSI_FORMATTED, "");
			walue.put(WALUE_KEY_WIFI_RSSI_LEVEL_PERCENT, 0);
			walue.put(WALUE_KEY_WIFI_RSSI_LEVEL_PERCENT_FORMATTED, "");
		}
	}
	
	@Override
	protected String formatted(Walue walue) {
		return String.format("("+WALUE_KEY_WIFI_ENABLE+"'%s'  "+WALUE_KEY_WIFI_NAME+" '%s'  " +
				WALUE_KEY_WIFI_LINK_SPEED + " '%s'  "+WALUE_KEY_WIFI_IP_STR+" '%s'  "+WALUE_KEY_WIFI_RSSI_FORMATTED+" '%s'  "+WALUE_KEY_WIFI_RSSI_LEVEL_PERCENT_FORMATTED+" '%s' " +
				WALUE_KEY_WIFI_RSSI_LEVEL_4 + " '%s' "+WALUE_KEY_WIFI_RSSI_LEVEL_3+" '%s' "+WALUE_KEY_WIFI_RSSI_LEVEL_2+" '%s' "+WALUE_KEY_WIFI_RSSI_LEVEL_1+" '%s' " +
				WALUE_KEY_WIFI_RSSI_LEVEL_0 + " '%s' "+WALUE_KEY_WIFI_ON+" '%s' "+WALUE_KEY_WIFI_OFF+" '%s')", 
				isWifiEnable(walue), 
				getWifiName(walue), 
				getWifiLinkSpeedFormatted(walue),
				getWifiIpAddr(walue),
				getWifiRssiFormatted(walue),
				getWifiRssiPercentFormatted(walue),
				walue.getString(WALUE_KEY_WIFI_RSSI_LEVEL_4),
				walue.getString(WALUE_KEY_WIFI_RSSI_LEVEL_3),
				walue.getString(WALUE_KEY_WIFI_RSSI_LEVEL_2),
				walue.getString(WALUE_KEY_WIFI_RSSI_LEVEL_1),
				walue.getString(WALUE_KEY_WIFI_RSSI_LEVEL_0),
				walue.getString(WALUE_KEY_WIFI_ON),
				walue.getString(WALUE_KEY_WIFI_OFF));
	}
	
	public static boolean isWifiEnable(Walue walue) {
		return walue.getBoolean(WALUE_KEY_WIFI_ENABLE);
	}
	
	public static String getWifiName(Walue walue) {
		return walue.getString(WALUE_KEY_WIFI_NAME);
	}
	
	public static int getWifiLinkSpeed(Walue walue) {
		return walue.getInt(WALUE_KEY_WIFI_LINK_SPEED);
	}
	public static String getWifiLinkSpeedFormatted(Walue walue) {
		return walue.getString(WALUE_KEY_WIFI_LINK_SPEED_FORMATTED);
	}
	public static String getWifiIpAddr(Walue walue) {
		return walue.getString(WALUE_KEY_WIFI_IP_STR);
	}
	public static String getWifiRssiFormatted(Walue walue) {
		return walue.getString(WALUE_KEY_WIFI_RSSI_FORMATTED);
	}
	public static String getWifiRssiPercentFormatted(Walue walue) {
		return walue.getString(WALUE_KEY_WIFI_RSSI_LEVEL_PERCENT_FORMATTED);
	}
}
