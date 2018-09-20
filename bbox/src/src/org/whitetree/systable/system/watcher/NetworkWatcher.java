package org.whitetree.systable.system.watcher;

import org.whitetree.systable.system.U;
import org.whitetree.systable.system.Walue;
import org.whitetree.systable.system.Watcher;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

public class NetworkWatcher extends Watcher {
	public static final String WALUE_HEAD = ":network";
	public static final String WALUE_NETWORK_LOCAL_IP = ":network.local-ip";
	public static final String WALUE_NETWORK_MAC = ":network.mac";
	
	public static final String WALUE_NETWORK_DNS1 = ":network.dhcp.dns1";
	public static final String WALUE_NETWORK_DNS2 = ":network.dhcp.dns2";
	public static final String WALUE_GATEWAY      = ":network.dhcp.gateway";
	public static final String WALUE_SUBNETMASK   = ":network.dhcp.subnetmask";
	public static final String WALUE_LEASETIME    = ":network.dhcp.lease-time";
	
	public NetworkWatcher(long updateInterval) {
		setUpdateInterval(updateInterval);
	}
	
	@Override
	protected void update(Context context, Walue walue, String filterType) {
//		long mrx = TrafficStats.getMobileRxBytes();
//		long mtx = TrafficStats.getMobileTxBytes();
//		
//		System.out.println("---------------- mrx="+mrx+" mtx="+mtx);
//		
//		walue.put(":stats.mrx", mrx);
//		walue.put(":stats.mtx", mtx);
//		walue.put(":stats.m.n", mtx+mrx);
//		
//		walue.put(":stats.mrx.rate.n", mrx - mLastMrx );
//		walue.put(":stats.mtx.rate.n", mtx - mLastMtx );
		
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcpi = wm.getDhcpInfo();
        
		walue.put(WALUE_NETWORK_LOCAL_IP, U.getLocalIpAddress(context));
		//walue.put(WALUE_NETWORK_MAC, LocalStorage.getInstance().getMac());
		walue.put(WALUE_NETWORK_DNS1, Formatter.formatIpAddress(dhcpi.dns1));
		walue.put(WALUE_NETWORK_DNS2, Formatter.formatIpAddress(dhcpi.dns2));
		walue.put(WALUE_GATEWAY, Formatter.formatIpAddress(dhcpi.gateway));
		walue.put(WALUE_SUBNETMASK, Formatter.formatIpAddress(dhcpi.netmask));
		walue.put(WALUE_LEASETIME, dhcpi.leaseDuration);
//		mLastMrx = mrx;
//		mLastMtx = mtx;
		
		// 外部IP
		
		
	}

	@Override
	protected String formatted(Walue _) {
		//return String.format("(:stats.mrx %d :stats mtx %d :stats.mrx.rate.n %d :stats.mtx.rate.n %d)", getMobileRx(_), getMobileTx(_),getMobileRxRateByte(_),getMobileTxRateByte(_));
		return String.format(""+_.toString()); 
	}
	
	public static String getDns1(Walue _) {
		return  _.getString(WALUE_NETWORK_DNS1);
	}
	
	public static String getDns2(Walue _) {
		return  _.getString(WALUE_NETWORK_DNS2);
	}
	
	public static int getLeaseTime(Walue _) {
		return  _.getInt(WALUE_LEASETIME);
	}
	
	public static String getGateway(Walue _) {
		return  _.getString(WALUE_GATEWAY);
	}
	
	public static String getSubnetMask(Walue _) {
		return  _.getString(WALUE_SUBNETMASK);
	}
	
	public static String getLocalIp(Walue _) {
		return  _.getString(WALUE_NETWORK_LOCAL_IP);
	}
	
	public static String getMac(Walue _) {
		return  _.getString(WALUE_NETWORK_MAC);
	}
	
	@Deprecated
	public static long getMobileRx(Walue _) {
		return _.getLong(":stats.mrx");
	}
	
	@Deprecated
	public static long getMobileTx(Walue _) {
		return _.getLong(":stats.mtx");
	}
	
	@Deprecated
	public static long getMobileRxRateByte(Walue _) {
		return _.getLong(":stats.mrx.rate.n");
	}
	
	@Deprecated
	public static long getMobileTxRateByte(Walue _) {
		return _.getLong(":stats.mtx.rate.n");
	}
}
