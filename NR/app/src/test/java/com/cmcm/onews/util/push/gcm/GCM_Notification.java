package com.cmcm.onews.util.push.gcm;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.cmcm.onews.NewsL;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Iterator;
import java.util.MissingFormatArgumentException;

//
// Source code recreated from a .class file by IntelliJ IDEA
// decompiler from google push lib
//

class GCM_Notification {
    static GCM_Notification mInstance;
    private Context mContext;

    static synchronized GCM_Notification getInstance(Context var0) {
        if(mInstance == null) {
            mInstance = new GCM_Notification(var0);
        }

        return mInstance;
    }

    // I am not quite sure this
    static boolean isGoogle_Notification(Bundle bundle) {
        return replaceN(bundle, "push.n.icon") != null;
    }

    static String replaceN(Bundle bundle, String str_traget) {
        String str_notification_target = bundle.getString(str_traget);
        if(str_notification_target == null) {
            str_notification_target = bundle.getString(str_traget.replace("push.n.", "push.notification."));
        }

        return str_notification_target;
    }

    private GCM_Notification(Context context) {
        this.mContext = context.getApplicationContext();
    }

    boolean zzv(Bundle var1) {
        try {
            Notification notification = this.getNotification(var1);
            this.startNotification(replaceN(var1, "push.n.tag"), notification);
            return true;
        } catch (Exception e) {
            NewsL.push("GcmNotification Failed to show notification: ");
            return false;
        }
    }

    private Notification getNotification(Bundle var1) {
        String var2 = this.zzd(var1, "push.n.title");
        if(TextUtils.isEmpty(var2)) {
            throw new gcm_exception("Missing title");
        } else {
            String var3 = this.zzd(var1, "push.n.body");
            int var4 = this.zzdk(replaceN(var1, "push.n.icon"));
            Uri var5 = this.zzdl(replaceN(var1, "push.n.sound"));
            PendingIntent var6 = this.zzx(var1);
            Notification var7;
            if(VERSION.SDK_INT >= 11) {
                Builder var8 = (new Builder(this.mContext)).setAutoCancel(true).setSmallIcon(var4).setContentTitle(var2).setContentText(var3);
                if(VERSION.SDK_INT >= 21) {
                    String var9 = replaceN(var1, "push.n.color");
                    if(!TextUtils.isEmpty(var9)) {
                        var8.setColor(Color.parseColor(var9));
                    }
                }

                if(var5 != null) {
                    var8.setSound(var5);
                }

                if(var6 != null) {
                    var8.setContentIntent(var6);
                }

                if(VERSION.SDK_INT >= 16) {
                    var7 = var8.build();
                } else {
                    var7 = var8.getNotification();
                }
            } else {
                if(var6 == null) {
                    Intent var11 = new Intent();
                    var11.setPackage("com.google.example.invalidpackage");
                    var6 = PendingIntent.getBroadcast(this.mContext, 0, var11, 0);
                }

                android.support.v4.app.NotificationCompat.Builder var10 = (new android.support.v4.app.NotificationCompat.Builder(this.mContext)).setSmallIcon(var4).setAutoCancel(true).setContentIntent(var6).setContentTitle(var2).setContentText(var3);
                if(var5 != null) {
                    var10.setSound(var5);
                }

                var7 = var10.build();
            }

            return var7;
        }
    }

    private void startNotification(String var1, Notification var2) {
        if(Log.isLoggable("GcmNotification", 3)) {
            Log.d("GcmNotification", "Showing notification");
        }

        NotificationManager var3 = (NotificationManager)this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if(TextUtils.isEmpty(var1)) {
            var1 = "GCM-Notification:" + SystemClock.uptimeMillis();
        }

        var3.notify(var1, 0, var2);
    }

    private String zzd(Bundle var1, String var2) {
        String var3 = replaceN(var1, var2);
        if(!TextUtils.isEmpty(var3)) {
            return var3;
        } else {
            String var4 = replaceN(var1, var2 + "_loc_key");
            if(TextUtils.isEmpty(var4)) {
                return null;
            } else {
                Resources var5 = this.mContext.getResources();
                int var6 = var5.getIdentifier(var4, "string", this.mContext.getPackageName());
                if(var6 == 0) {
                    throw new gcm_exception(this.zzdj(var2 + "_loc_key") + " resource not found: " + var4);
                } else {
                    String var7 = replaceN(var1, var2 + "_loc_args");
                    if(TextUtils.isEmpty(var7)) {
                        return var5.getString(var6);
                    } else {
                        JSONArray var8;
                        try {
                            var8 = new JSONArray(var7);
                        } catch (JSONException var12) {
                            throw new gcm_exception("Malformed " + this.zzdj(var2 + "_loc_args") + ": " + var7);
                        }

                        Object[] var9 = new Object[var8.length()];

                        for(int var10 = 0; var10 < var9.length; ++var10) {
                            var9[var10] = (Object)var8.opt(var10);
                        }

                        try {
                            return var5.getString(var6, var9);
                        } catch (MissingFormatArgumentException var11) {
                            throw new gcm_exception("Missing format argument for " + var4 + ": " + var11);
                        }
                    }
                }
            }
        }
    }

    private String zzdj(String var1) {
        return var1.substring("push.n.".length());
    }

    private int zzdk(String var1) {
        if(TextUtils.isEmpty(var1)) {
            throw new gcm_exception("Missing icon");
        } else {
            Resources var2 = this.mContext.getResources();
            int var3 = var2.getIdentifier(var1, "drawable", this.mContext.getPackageName());
            if(var3 != 0) {
                return var3;
            } else {
                var3 = var2.getIdentifier(var1, "mipmap", this.mContext.getPackageName());
                if(var3 != 0) {
                    return var3;
                } else {
                    throw new gcm_exception("Icon resource not found: " + var1);
                }
            }
        }
    }

    private Uri zzdl(String var1) {
        if(TextUtils.isEmpty(var1)) {
            return null;
        } else if("default".equals(var1)) {
            return RingtoneManager.getDefaultUri(2);
        } else {
            throw new gcm_exception("Invalid sound: " + var1);
        }
    }

    private PendingIntent zzx(Bundle var1) {
        String var2 = replaceN(var1, "push.n.click_action");
        if(TextUtils.isEmpty(var2)) {
            return null;
        } else {
            Intent var3 = new Intent(var2);
            var3.setPackage(this.mContext.getPackageName());
            var3.setFlags(268435456);
            var3.putExtras(var1);
            Iterator var4 = var1.keySet().iterator();

            while(true) {
                String var5;
                do {
                    if(!var4.hasNext()) {
                        return PendingIntent.getActivity(this.mContext, this.zzvW(), var3, PendingIntent.FLAG_ONE_SHOT);
                    }

                    var5 = (String)var4.next();
                } while(!var5.startsWith("push.n.") && !var5.startsWith("push.notification."));

                var3.removeExtra(var5);
            }
        }
    }

    private int zzvW() {
        return (int)SystemClock.uptimeMillis();
    }

    private class gcm_exception extends IllegalArgumentException {
        private gcm_exception(String var2) {
            super(var2);
        }
    }
}
