// 需要加入编译宏，当中文版本的时候注释掉
package com.cmcm.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cmcm.onews.MainEntry;
import com.cmcm.onews.R;
import com.cmcm.onews.event.ONewsEvent;
import com.cmcm.onews.fragment.NewsBaseFragment;
import com.cmcm.onews.infoc.newsindia_login;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.ui.widget.LoadingDlgManager;
import com.cmcm.onews.util.DimenUtils;
import com.cmcm.onews.util.NetworkUtil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class OverSeasLoginFragment extends NewsBaseFragment {
    private CallbackManager mCallbackManager;
    private LoadingDlgManager mLoading;
    public final static int LOGIN_OUTOFDATE =-1;
    public final static String LOGIN_KEY ="login_key";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(MainEntry.getAppContext());
        mCallbackManager = CallbackManager.Factory.create();
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
        mProfileTracker = new ProfileTracker() {

            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile curProfile) {
                if (curProfile == null) {
                    return;
                }
                String acctoken = null;
                if (AccessToken.getCurrentAccessToken() != null) {
                    acctoken = AccessToken.getCurrentAccessToken().getToken();
                }
                LoginDataHelper.getInstance().setNewestFbName(curProfile.getName());
                LoginDataHelper.getInstance().setNewestFbAvatar(curProfile.getProfilePictureUri(DimenUtils.dp2px(MainEntry.getAppContext(), LoginDataHelper.ICON_WIDTH)
                        , DimenUtils.dp2px(MainEntry.getAppContext(), LoginDataHelper.ICON_WIDTH)).toString());
                LoginService.start_ACTION_LOGIN_BY_FACEBOOK(getActivity(), acctoken, null,getFrom());
            }
        };

        final int screenHeight = DimenUtils.getScreenHeight(getContext());
        final int screenWidth = DimenUtils.getScreenWidth(getContext());
        Log.e("suj",screenHeight+"x"+screenWidth);
    }

    private int getFrom() {
        if(null != getActivity() && getActivity() instanceof LoginActivity){
            return ((LoginActivity)getActivity()).getFrom();
        }
        return LoginActivity.FROM_LIVE_COMMENTS;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.onews_login_fragment, null, false);
        addEventsListener(view);
        return view;
    }

    @Override
    public void onDestroy() {
        if (mProfileTracker != null) {
            mProfileTracker.stopTracking();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private ProfileTracker mProfileTracker = null;

    private void log(String content){
        if (L.DEBUG){
            Log.e("login", content);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(null != mLoading){
            mLoading.hide();
        }
    }

    private void addEventsListener(View view) {
        mLoading = new LoadingDlgManager(getActivity());
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button_facebook);
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (L.DEBUG) {
                    if (loginResult != null) {
                        final AccessToken accessToken = loginResult.getAccessToken();
                        log(accessToken.toString());
                    }
                }
                LoginDataHelper.getInstance().log("onSuccess\t" + loginResult.getRecentlyGrantedPermissions());

            }

            @Override
            public void onCancel() {
                // App code
                if(!NetworkUtil.isNetworkUp(MainEntry.getAppContext())){
                    Toast.makeText(getContext(),R.string.onews__no_network,Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(),R.string.log_in_failed,Toast.LENGTH_LONG).show();
                }
                LoginDataHelper.getInstance().log("onCancel\t");
                new newsindia_login().page(getFrom()).ifsuccessful(0).stage(1).report();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                LoginDataHelper.getInstance().log("onError\t" + exception);

                if (exception instanceof FacebookOperationCanceledException) {
                    Toast.makeText(getContext(),R.string.log_in_failed,Toast.LENGTH_LONG).show();
                } else if (exception instanceof FacebookAuthorizationException) {
                    Toast.makeText(getContext(),R.string.login_fb_token_error,Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(),R.string.onews__no_network,Toast.LENGTH_LONG).show();
                }
                new newsindia_login().page(getFrom()).ifsuccessful(0).stage(1).report();
            }
        });
        loginButton.setLoginText(getString(R.string.log_in_with_facebook));
        loginButton.invalidate();
        loginButton.setFragment(this);
    }

    @Override
    public void onEventInUiThread(ONewsEvent event) {
        super.onEventInUiThread(event);
        if(event instanceof EventLogin){
            onHandleEventLogin((EventLogin)event);
        }else if(event instanceof EventShowLoadingForFb){
            if(null != mLoading){
                mLoading.show(LoadingDlgManager.STATUS_ID_LOADING, R.string.photostrim_tag_str_loading);
            }
        }
    }

    private void onHandleEventLogin(final EventLogin event) {
        if(null != mLoading){
            mLoading.hide();
        }
        LoginService.handleLoginEvent(getActivity(), event, new LoginService.OnCallback() {

            @Override
            public void onSuccess() {
                // 这个是为了标识评论页面
                Intent intent = new Intent();
                intent.setAction("com.cmcm.indianews.login.authorize");
                intent.putExtra("login_result",true);
                MainEntry.getAppContext().sendBroadcast(intent);
                getActivity().finish();
            }
        });
    }
}

