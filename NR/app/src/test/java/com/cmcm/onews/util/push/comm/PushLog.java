package com.cmcm.onews.util.push.comm;

import android.util.Log;

/**
 * Created by pc on 2015/12/29.
 */
public class PushLog {
    public  static  final boolean isDebug = true;
    public  static  final void log(String string){
        if(isDebug){
            Log.d("PushLog", string);
        }
    }

}
