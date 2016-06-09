package com.cmcm.onews.util;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by wht on 2015/11/17.
 */
public class ToastUtil {

    public static void showToast(final Activity context,final int times){
        final int _clickTotalNum = 8;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(_clickTotalNum - times <= 3){
                    String content = "click "+(_clickTotalNum - times)+" times again";
                    final Toast _toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
                    _toast.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            _toast.cancel();
                        }
                    }, 500);
                }
            }
        });
    }
}
