package org.whitetree.sched.utils;

import java.util.Calendar;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.telephony.gsm.SmsManager;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.fanxer.midian.R;
import com.fanxer.midian.cloud.XmppClient;
import com.fanxer.midian.storage.LocalStorage;

public class SysUtils {
	
	private static Integer SDK;
    /**
     * return sdk API level
     */
    public static int getAPILevel() {
        if (SDK == null) {
            SDK = Integer.valueOf(Integer.parseInt(Build.VERSION.SDK));
        }
        return SDK;
    }
    
    // Do the common stuff when starting the alarm.
    public static void startAlarm(Context context, MediaPlayer player)
            throws java.io.IOException, IllegalArgumentException,
                   IllegalStateException {
        final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        // do not play alarms if stream volume is 0  (typically because ringer mode is silent).
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            player.setAudioStreamType(AudioManager.STREAM_ALARM);
            player.setLooping(true);
            player.prepare();
            player.start();
        }
    }
    
    public static void sendSms(String destinationAddress, String content) {
        SmsManager smsMgr = SmsManager.getDefault();  
        smsMgr.sendTextMessage(destinationAddress, null, content, null, null);
    }
    

    public static final CharSequence getRelativeDaysString(Context context, long millis) {
        long currentSystemTimeMillis = System.currentTimeMillis();

        if (currentSystemTimeMillis > millis) {
            return "已过期";
        } 
        
        long relativeDay = (Math.abs((millis - currentSystemTimeMillis))/(1000*60*60*24));  
        if (relativeDay < 1) {
            return (getDayFromMillis(currentSystemTimeMillis) != getDayFromMillis(millis))?
                    context.getString(R.string.tomorrow) : context.getString(R.string.today);
        } else {
            return context.getString(R.string.leave_days, Long.toString(relativeDay));
        }
    }
    
    public static final int getDayFromMillis(long millisecond) {
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(millisecond);
        return time.get(Calendar.DATE);
    }
    
    public static final CharSequence getFormatTime(Context context, int resId, long millis) {
        return DateFormat.format(context.getText(resId), millis);
    }
    
    public static final boolean getLoginStatus() {
//        return XmppClient.getInstance().isAuthenticated();
        if (!"".equals(LocalStorage.getInstance().getLatestLogin()) && LocalStorage.getInstance().getLoginStatus()) {
            return true;
        }
        return false;
    }
    
    public static void hideInputKeyBoard(View view) {
        InputMethodManager imm = InputMethodManager.peekInstance();
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    
    public static void showInputKeyBoard(View view) {
        InputMethodManager imm = InputMethodManager.peekInstance();
        if (imm != null && view != null) {
            imm.showSoftInput(view, 0);
        }
    }
    
    /**
     * return true if the sdk level >= 8
     */
    public synchronized static boolean isFroyoSDK() {
        if (SDK == null){
            SDK = Integer.valueOf(Integer.parseInt(Build.VERSION.SDK));
        }
        if (SDK.intValue() >= Build.VERSION_CODES.FROYO) {
            return true;
        }
        return false;
    }
    
    public static int getPixByDip(float dip) {
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.setToDefaults();
        return (int)(dip * metrics.density);
    }
    
    public static int getSpByPix(float pix) {
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.setToDefaults();
        return (int)(pix / metrics.density);
    }
    
}
