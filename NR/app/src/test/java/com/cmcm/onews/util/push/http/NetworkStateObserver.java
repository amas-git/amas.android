package com.cmcm.onews.util.push.http;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import java.util.ArrayList;
import java.util.List;

/**
 * network state observer
 * user can register network state callback
 *
 * @author jshlv
 *
 */
public class NetworkStateObserver extends BroadcastReceiver {
    private static final String TAG = "NetworkStateObserver";
    private static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private static Integer[]sObject = new Integer[0];
    private static List<NetworkStateListener> sObservers = new ArrayList<NetworkStateListener>();

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        notify(networkInfo);
    }

    /**
     * register network state callback
     *
     * @param observer
     */
    public static void registerNetworkObserver(NetworkStateListener observer){
        if(observer != null){
            synchronized(sObject){
                if(sObservers.contains(observer))
                    return;
                sObservers.add(observer);
            }
        }
    }

    /**
     * unregister network state callback
     *
     * @param observer
     */
    public static void unRegisterNetworkObserver(NetworkStateListener observer){
        if(observer != null){
            synchronized(sObject){
                sObservers.remove(observer);
            }
        }
    }

    private void notify(final NetworkInfo info){
        synchronized(sObject){
            for(NetworkStateListener observer : sObservers){
                if(observer != null){
                    observer.onNetworkChanged(info);
                }
            }
        }
    }

    private static NetworkStateObserver mNetChangedReceiver = null;
    public static void registerConnectivityReceiver(Context context) {
        if(context == null) {
            return;
        }
        if (mNetChangedReceiver == null) {
            IntentFilter filter = new IntentFilter(ACTION);
            mNetChangedReceiver = new NetworkStateObserver();
            context.registerReceiver(mNetChangedReceiver, filter);
        }
    }
    
//    public static void unregisterConnectivityReceiver(Context context) {
//        if(context == null) {
//            return;
//        }
//        
//        if(mNetChangedReceiver != null) {
//            context.unregisterReceiver(mNetChangedReceiver);
//            mNetChangedReceiver = null;
//        }
//    }
    
    /**
     * callback interface
     *
     */
    public static interface NetworkStateListener{
        public void onNetworkChanged(NetworkInfo info);
    }

    public static int getNetworkType(Context context){
        NetworkInfo info = getNetworkInfo(context);
        if(info!=null){
            return info.getType();
        }else{
            return -1;
        }
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        if(context == null) {
            return null;
        }
        
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    public static boolean isOnWifi(Context context) {
        return isOnWifi(getNetworkInfo(context));
    }

    public static boolean isOnWifi(NetworkInfo info) {
        if (info == null)
            return false;
        switch (info.getType()) {
            case ConnectivityManager.TYPE_MOBILE:
            case ConnectivityManager.TYPE_MOBILE_DUN:
            case ConnectivityManager.TYPE_MOBILE_MMS:
            case ConnectivityManager.TYPE_MOBILE_SUPL:
            case ConnectivityManager.TYPE_MOBILE_HIPRI:
            case ConnectivityManager.TYPE_WIMAX: // separate case for this?
                return false;
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_ETHERNET:
            case ConnectivityManager.TYPE_BLUETOOTH:
                return true;
            default:
                return false;
        }
    }

}
