package com.cmcm.onews.util.share;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;


import com.cleanmaster.base.crash.MyCrashHandler;
import com.cmcm.onews.C;
import com.cmcm.onews.sdk.L;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;



public class ShareToFacebook extends iFBShare{
    private volatile static ShareToFacebook instance;
    private CallbackManager mCallbackManager;
    ShareDialog shareDialog;

    private ShareToFacebook(){
        initFBSdk();
    }

    public static ShareToFacebook getInstance() {
        if (instance == null) {
            synchronized (FBShare.class) {
                if (instance == null) {
                    instance = new ShareToFacebook();
                }
            }
        }
        return instance;
    }

    /**
     * FacebookSdk init
     */
    private void initFBSdk() {
        FacebookSdk.sdkInitialize(C.getAppContext());
        mCallbackManager = CallbackManager.Factory.create();
    }



    /**
     * share to fb
     * @param link
     * @param title
     * @param imgUrl
     * @param des
     */
     public void shareWithFbSDK(Activity act,String link, String title, String imgUrl, String des) {
        try {

            shareDialog = new ShareDialog(act);
            shareDialog.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    L.newsDetail("shareToFb onSuccess :" + result.toString());
                }

                @Override
                public void onCancel() {
                    L.newsDetail("shareToFb onCancel");
                }

                @Override
                public void onError(FacebookException error) {
                    L.newsDetail("shareToFb error :" + error.toString());
                }
            });
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent.Builder builder = new ShareLinkContent.Builder();
                if(!TextUtils.isEmpty(link)){
                    builder.setContentUrl(Uri.parse(link));
                }

                if(!TextUtils.isEmpty(title)){
                    builder.setContentTitle(title);
                }

                if(!TextUtils.isEmpty(imgUrl)){
                    builder.setImageUrl(Uri.parse(imgUrl));
                }

                if(!TextUtils.isEmpty(des)){
                    builder.setContentDescription(des);
                }

                shareDialog.show(builder.build());
            }else{
                ShareHelper.invokeShareApp(C.getAppContext(), ShareHelper.APK_FACEBOOK,
                        link,
                        link, "");
            }

        }catch (Exception e){
            MyCrashHandler.getInstance().throwOne(e,false);
        }
    }

    public void onResult(int requestCode, int resultCode, Intent data){
        if(null !=mCallbackManager) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

}



