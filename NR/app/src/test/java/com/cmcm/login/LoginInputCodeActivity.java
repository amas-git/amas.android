package com.cmcm.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcm.onews.R;
import com.cmcm.onews.bitmapcache.VolleySingleton;
import com.cmcm.onews.event.ONewsEvent;
import com.cmcm.onews.ui.NewsBaseActivity;
import com.cmcm.onews.ui.widget.LoadingDlgManager;
import com.cmcm.onews.util.ComponentUtils;

/**
 * Created by blue on 14-12-31.
 */
public class LoginInputCodeActivity extends NewsBaseActivity {

    private static final int ANIMATOR_TEXT = 0;
    private static final int ANIMATOR_IMAGE = 1;

    public static final int LOGIN_INPUT_REQUEST = 10;

    LoadingDlgManager mLoading;
    private TextView mTitle;
    private NetworkImageView mCodeImage;
    private EditText mCodeEdit;
    private Button mLogin;
    private Button mBtnSwitch;
    private CmViewAnimator mCmViewAnimator;
    private TextView mCodeState;

    private String mImageUrl = "";

    private int mType = LoginDataHelper.VERIFY_DIALOG_CM_LOGIN;

    private int mPageSource = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mType = savedInstanceState.getInt(":type");
            mImageUrl = savedInstanceState.getString(":url");
            mPageSource = savedInstanceState.getInt(":from");
        }
        setContentView(R.layout.login_input_code_layout);
        if(getIntent().hasExtra(":type")){
            mType = getIntent().getIntExtra(":type",LoginDataHelper.VERIFY_DIALOG_CM_LOGIN);
        }
        if(getIntent().hasExtra(":url")){
            mImageUrl = getIntent().getStringExtra(":url");
        }
        if(getIntent().hasExtra(":from")){
            mPageSource = getIntent().getIntExtra(":from",0);
        }
        initView();
    }

    private ImageLoader.ImageListener mImageCallback = new ImageLoader.ImageListener() {
        @Override
        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
            if(null != imageContainer.getBitmap()){
                mCmViewAnimator.setDisplayedChild(ANIMATOR_IMAGE);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            mCmViewAnimator.setDisplayedChild(ANIMATOR_TEXT);
            mCodeState.setText(R.string.login_load_code_error);
        }
    };

    private void initView() {
        mTitle = (TextView)findViewById(R.id.photo_trim_title_text);
        mCodeImage = (NetworkImageView)findViewById(R.id.code_image);
        mLogin = (Button)findViewById(R.id.btn_login);
        mCodeEdit = (EditText)findViewById(R.id.code_edit);
        mBtnSwitch = (Button)findViewById(R.id.btn_switch);
        mCmViewAnimator = (CmViewAnimator)findViewById(R.id.code_animator);
        mCodeState = (TextView)findViewById(R.id.code_state_text);
        mCmViewAnimator.setDisplayedChild(ANIMATOR_TEXT);
        mLoading = new LoadingDlgManager(this);
        mCodeImage.setDefaultImageResId(R.drawable.yanzhengtupian);
        mCmViewAnimator.setDisplayedChild(ANIMATOR_TEXT);
        mCodeState.setText(R.string.market_loading_content);
        VolleySingleton.getInstance().getImageLoader().get(mImageUrl + "&test=" + String.valueOf(System.currentTimeMillis()),
                mImageCallback);
        mCodeImage.setImageUrl(mImageUrl + "&test=" + String.valueOf(System.currentTimeMillis()),
                VolleySingleton.getInstance().getImageLoader());

        mTitle.setText(R.string.login_save_title);
        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });
        mBtnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsgToChangeCode(true);
            }
        });

        mCodeEdit.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(ComponentUtils.checkIsFinishing(LoginInputCodeActivity.this)){
                    return;
                }
                mCodeEdit.requestFocus();
                showSoftInput();
            }
        },150);
    }

    private void sendMsgToChangeCode(boolean isAddExtra){
        mHandler.removeMessages(0);
        Message msg = Message.obtain();
        msg.what = 0;
        if(isAddExtra){
            msg.obj = mImageUrl + "&test=" + String.valueOf(System.currentTimeMillis());
        }else{
            msg.obj = mImageUrl;
        }
        mHandler.sendMessageDelayed(msg,200);
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String url = (String)msg.obj;
            if(!TextUtils.isEmpty(url)){
                mCmViewAnimator.setDisplayedChild(ANIMATOR_TEXT);
                mCodeState.setText(R.string.market_loading_content);
                VolleySingleton.getInstance().getImageLoader().get(url, mImageCallback);
                mCodeImage.setImageUrl(url, VolleySingleton.getInstance().getImageLoader());
            }
        }
    };

    private boolean showSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm == null) {
            return false;
        }

        return imm.showSoftInput(mCodeEdit,0);
    }

    private boolean hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm == null) {
            return false;
        }

        View curFocusView = getCurrentFocus();
        if (curFocusView == null) {
            return false;
        }

        return imm.hideSoftInputFromWindow(curFocusView.getWindowToken(), 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(":type",mType);
        outState.putString(":url",mImageUrl);
        outState.putInt(":from", mPageSource);
        super.onSaveInstanceState(outState);
    }

    private void onLogin(){
        hideSoftInput();
        String code = mCodeEdit.getText().toString();
        if(!TextUtils.isEmpty(code)){
            mLoading.show(LoadingDlgManager.STATUS_ID_LOADING, R.string.photostrim_tag_str_loading);
            switch (mType) {
                case LoginDataHelper.VERIFY_DIALOG_CM_LOGIN:
                    LoginService.start_ACTION_LOGIN_BY_CM(LoginInputCodeActivity.this,LoginDataHelper.getInstance().getTmpLoginName()
                            ,LoginDataHelper.getInstance().getTmpLoginPassword(),code);
                    break;
                case LoginDataHelper.VERIFY_DIALOG_CM_REGIST:
//                    LoginService.start_ACTION_REGIST_BY_CM(LoginInputCodeActivity.this,LoginDataHelper.getInstance().getTmpLoginName()
//                            ,LoginDataHelper.getInstance().getTmpLoginPassword(),code);
                    break;
                case LoginDataHelper.VERIFY_DIALOG_FACEBOOK_LOGIN:
                    LoginService.start_ACTION_LOGIN_BY_FACEBOOK(LoginInputCodeActivity.this
                            ,LoginDataHelper.getInstance().getTmpFacebookToken(),code,mPageSource);
                    break;
                case LoginDataHelper.VERIFY_DIALOG_GOOGLE_LOGIN:
                    LoginService.start_ACTION_LOGIN_BY_GOOGLE(LoginInputCodeActivity.this
                            ,LoginDataHelper.getInstance().getTmpGoogleToken(),code);
                    break;
                default:
                    break;
            }
        }else{
            Toast.makeText(this,R.string.login_input_code,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onEventInUiThread(ONewsEvent event) {
        super.onEventInUiThread(event);
        if(ComponentUtils.checkIsFinishing(this)){
            return;
        }
        if(event instanceof EventLogin){
            onHandleEventLogin((EventLogin)event);
        }
    }

    private void onHandleEventLogin(final EventLogin event) {
        mLoading.hide();
        String url = LoginDataHelper.getInstance().getNextCodeCaptureByType(event.getLoginType());
        if(!TextUtils.isEmpty(url) && !mImageUrl.equalsIgnoreCase(url)){
            mImageUrl = url;
            sendMsgToChangeCode(false);
        }
        if(LoginService.LOGIN_CODE_ERROR == event.getLoginRaw()){
            mCodeEdit.setText("");
            mCodeEdit.requestFocus();
        }
        LoginService.handleLoginEvent(this, event, new LoginService.OnCallback() {

            @Override
            public void onSuccess() {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

//        boolean isReportMaidian = this.getClass().getSimpleName().equals(event.getFrom());
//        if(isReportMaidian){
//            switch (event.getLoginType()) {
//                case LoginDataHelper.VERIFY_DIALOG_CM_LOGIN:
////                    new cm_account_login2().page(2).fromp(mPageSource).action(4).done(event.isLoginSuccess() ? 1 : 2).report();
//                    break;
//                case LoginDataHelper.VERIFY_DIALOG_CM_REGIST:
////                    new cm_account_login2().page(2).fromp(mPageSource).action(3).done(event.isLoginSuccess() ? 1 : 2).report();
//                    break;
//                case LoginDataHelper.VERIFY_DIALOG_FACEBOOK_LOGIN:
////                    new cm_account_login2().page(2).fromp(mPageSource).action(2).done(event.isLoginSuccess() ? 1 : 2).report();
//                    break;
//                default:
//                    break;
//            }
//        }

        switch (event.getLoginRaw()){
            case LoginService.LOGIN_PASSWORD_ERROR:
            case LoginService.LOGIN_USERNAME_OR_PASSWORD_ERROR:
            case LoginService.LOGIN_USERNAME_ERROR:
            case LoginService.LOGIN_EMAIL_ERROR:
            case LoginService.LOGIN_USERNAME_USED:
            case LoginService.LOGIN_USERNAME_NOT_EXIST:
            case LoginService.LOGIN_INVALID_USER:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static Intent getLaunchIntent(Context context,int type,String url,int from) {
        Intent intent = new Intent();
//        if(context instanceof UserRegisterOptionsActivity){
//            intent.putExtra(":from",((UserRegisterOptionsActivity)context).mPageSource);
//        }else if(context instanceof UserRegisterActivity){
//            intent.putExtra(":from",((UserRegisterActivity)context).mPageSource);
//        }else if(context instanceof UserLoginActivity){
//            intent.putExtra(":from",((UserLoginActivity)context).mPageSource);
//        }
        intent.putExtra(":type",type);
        intent.putExtra(":url",url);
        intent.putExtra(":from",from);
        intent.setClass(context, LoginInputCodeActivity.class);
        return intent;
    }

}
