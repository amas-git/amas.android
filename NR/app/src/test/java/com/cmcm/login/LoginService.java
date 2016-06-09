package com.cmcm.login;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cleanmaster.sdk.cmloginsdkjar.CmLoginSdkException;
import com.cleanmaster.sdk.cmloginsdkjar.model.CmProxyLogin;
import com.cleanmaster.sdk.cmloginsdkjar.model.CmProxyUpAvatar;
import com.cleanmaster.sdk.cmloginsdkjar.model.CmRawObject;
import com.cleanmaster.sdk.cmloginsdkjar.sdk.usermanager.UserManagerImpl;
import com.cmcm.onews.MainEntry;
import com.cmcm.onews.R;
import com.cmcm.onews.event.ONewsEvent;
import com.cmcm.onews.event.ONewsEventManager;
import com.cmcm.onews.infoc.newsindia_login;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.util.DeviceUtils;
import com.cmcm.onews.util.NetworkUtil;
import com.cmcm.onews.util.push.comm.AppEnvUtils;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by blue on 14-12-24.
 */
public class LoginService extends IntentService{

    public static final String USER_HOST = "http://uc.cm.ksmobile.com";
    public static final String GET_USER_DATA = "/user/getData";
    public static final String UPLOAD_USER_DATA = "/user/report";

    public static final int LOGIN_LOGIN_PARSE_DATA_FAIL = -2;
    public static final int LOGIN_EXCEPTION = -1;
    public static final int LOGIN_RET_SUCCESS = 1;
    public static final int LOGIN_EMAIL_REGISTED = 12005;
    public static final int LOGIN_USERNAME_USED = 12006;
    public static final int LOGIN_PASSWORD_ERROR = 12004;
    public static final int LOGIN_USERNAME_OR_PASSWORD_ERROR = 12024;
    public static final int LOGIN_TIMEOUT = 12104;
    public static final int LOGIN_CODE_ERROR = 12102;
    public static final int LOGIN_USERNAME_NOT_EXIST = 12018;
    public static final int LOGIN_TOO_BUSINESS = 12101;
    public static final int LOGIN_INVALID_USER = 12008;
    public static final int LOGIN_HAS_SENT_EMAIL = 12009;
    public static final int LOGIN_USERNAME_ERROR = 12002;
    public static final int LOGIN_EMAIL_ERROR = 12000;
    public static final int LOGIN_FB_TOKEN_ERROR = 11104;

    public static final int CLEAN_USER_TOKEN_INVALID = -7;
    public static final int CLEAN_USER_HAS_NO_DATA = -6;
    public static final int CLEAN_USER_INFO_SUCCESS = 0;
    public static final int CLEAN_USER_INFO_EXCEPRION = 1;
    public static final int CLEAN_USER_INFO_PARSE_DATA_FAIL = 2;

    public static final int CMB_USER_INFO_SUCCESS = 0;
    public static final int CMB_USER_INFO_EXCEPRION = 1;

    public static final int CM_LOGIN_SDK_EXCEPTION_SID_INVALID = 4035;

    public static final int CM_LOGIN_OLD_GOOGLE_ACCOUNT = 3;

    public static final String ACTION_LOGIN_BY_FACEBOOK = "ACTION_LOGIN_BY_FACEBOOK";
    public static final String ACTION_LOGIN_BY_CM = "ACTION_LOGIN_BY_CM";
    public static final String ACTION_UPLOAD_USER_ICON = "ACTION_UPLOAD_USER_ICON";
    public static final String ACTION_RESET_PASSWORD = "ACTION_RESET_PASSWORD";
    public static final String ACTION_LOGOUT = "ACTION_LOGOUT";
    public static final String ACTION_REPLACE_NICKNAME = "ACTION_REPLACE_NICKNAME";
    public static final String ACTION_LOGIN_BY_GOOGLE = "ACTION_LOGIN_BY_GOOGLE";
    public static final String ACTION_CHECK_ACCOUNT_EXIST = "ACTION_CHECK_ACCOUNT_EXIST";
    public static final String ACTION_SEND_EMAIL_TO_ACTIVE = "ACTION_SEND_EMAIL_TO_ACTIVE";
    public static final String ACTION_VERIFY_EMAIL = "ACTION_VERIFY_EMAIL";
    public static final String ACTION_FRESH_USER_INFO = "ACTION_FRESH_USER_INFO";


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public LoginService() {
        super("LoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(null == intent){
            return;
        }
        String action = intent.getAction();
        if(TextUtils.isEmpty(action)){
            return;
        }

        LoginDataHelper.getInstance().log("onHandleIntent\t" + action);

        if(LoginDataHelper.getInstance().initSid()){
            LoginDataHelper.getInstance().saveInitSid();
        }

        if(ACTION_LOGIN_BY_FACEBOOK.equalsIgnoreCase(action)){
            onHandle_ACTION_LOGIN_BY_FACEBOOK(intent);
        }else if(ACTION_LOGIN_BY_CM.equalsIgnoreCase(action)){
            onHandle_ACTION_LOGIN_BY_CM(intent);
        }else if(ACTION_UPLOAD_USER_ICON.equalsIgnoreCase(action)){
            onHandle_ACTION_UPLOAD_USER_ICON(intent);
        }else if(ACTION_RESET_PASSWORD.equalsIgnoreCase(action)){
            onHandle_ACTION_RESET_PASSWORD(intent);
        }else if(ACTION_LOGOUT.equalsIgnoreCase(action)){
            onHandle_ACTION_LOGOUT(intent);
        }else if(ACTION_REPLACE_NICKNAME.equalsIgnoreCase(action)){
            onHandle_ACTION_REPLACE_NICKNAME(intent);
        }else if(ACTION_LOGIN_BY_GOOGLE.equalsIgnoreCase(action)){
            onHandle_ACTION_LOGIN_BY_GOOGLE(intent);
        }else if(ACTION_CHECK_ACCOUNT_EXIST.equalsIgnoreCase(action)){
            onHandle_ACTION_CHECK_ACCOUNT_EXIST(intent);
        }else if(ACTION_SEND_EMAIL_TO_ACTIVE.equalsIgnoreCase(action)){
            onHandle_ACTION_SEND_EMAIL_TO_ACTIVE(intent);
        }else if(ACTION_VERIFY_EMAIL.equalsIgnoreCase(action)){
            onHandle_ACTION_VERIFY_EMAIL(intent);
        }else if(ACTION_FRESH_USER_INFO.equalsIgnoreCase(action)){
            onHandle_ACTION_FRESH_USER_INFO(intent);
        }
    }

    private static void start_ACTION_LOGOUT(Context context,String sid){
        Intent intent = new Intent();
        intent.setClass(context, LoginService.class);
        intent.setAction(ACTION_LOGOUT);
        intent.putExtra(":sid", sid);
        intent.putExtra(":tag", context.getClass().getSimpleName());
        context.startService(intent);
    }

    public static void start_ACTION_VERIFY_EMAIL(Context context,String code){
        Intent intent = new Intent();
        intent.setClass(context, LoginService.class);
        intent.setAction(ACTION_VERIFY_EMAIL);
        intent.putExtra(":code", code);
        context.startService(intent);
    }

    public static void start_ACTION_RESET_PASSWORD(Activity context,String address){
        Intent intent = new Intent();
        intent.setClass(context, LoginService.class);
        intent.setAction(ACTION_RESET_PASSWORD);
        intent.putExtra(":tag", context.getClass().getSimpleName());
        intent.putExtra(":address", address);
        context.startService(intent);
    }

    public static void start_ACTION_REPLACE_NICKNAME(Activity context,String nickName){
        Intent intent = new Intent();
        intent.setClass(context, LoginService.class);
        intent.setAction(ACTION_REPLACE_NICKNAME);
        intent.putExtra(":tag", context.getClass().getSimpleName());
        intent.putExtra(":new_name", nickName);
        context.startService(intent);
    }

    public static void start_ACTION_UPLOAD_USER_ICON(Activity context,Bitmap map){
        Intent intent = new Intent();
        intent.setClass(context, LoginService.class);
        intent.setAction(ACTION_UPLOAD_USER_ICON);
        intent.putExtra(":tag", context.getClass().getSimpleName());
        intent.putExtra(":icon", map);
        context.startService(intent);
    }

    public static void start_ACTION_SEND_EMAIL_TO_ACTIVE(Context context){
        Intent intent = new Intent();
        intent.setClass(context, LoginService.class);
        intent.setAction(ACTION_SEND_EMAIL_TO_ACTIVE);
        context.startService(intent);
    }

    public static boolean start_ACTION_LOGIN_BY_FACEBOOK(Activity context,String fbToken,String code,int from){
        LoginDataHelper.getInstance().tmpSaveFacebookToken(fbToken);
        String capture = LoginDataHelper.getInstance().getNextLoginFacebookCodeCapture();
        if(TextUtils.isEmpty(code) && !TextUtils.isEmpty(capture)){
            context.startActivityForResult(LoginInputCodeActivity.getLaunchIntent(context,
                    LoginDataHelper.VERIFY_DIALOG_FACEBOOK_LOGIN,capture,from),LoginInputCodeActivity.LOGIN_INPUT_REQUEST);
            return false;
        }
        Intent intent = new Intent();
        intent.setClass(context, LoginService.class);
        intent.setAction(ACTION_LOGIN_BY_FACEBOOK);
        intent.putExtra(":tag", context.getClass().getSimpleName());
        intent.putExtra(":fbToken", fbToken);
        intent.putExtra(":code", code);
        intent.putExtra(":from", from);
        context.startService(intent);
        return true;
    }

    public static boolean start_ACTION_LOGIN_BY_GOOGLE(Activity context,String glToken,String code){
        LoginDataHelper.getInstance().tmpSaveGoogleToken(glToken);
        String capture = LoginDataHelper.getInstance().getNextLoginFacebookCodeCapture();
//        if(TextUtils.isEmpty(code) && !TextUtils.isEmpty(capture)){
//            context.startActivityForResult(LoginDataHelper.getLaunchIntent(context,
//                    LoginDataHelper.VERIFY_DIALOG_GOOGLE_LOGIN,capture),LoginDataHelper.LOGIN_INPUT_REQUEST);
//            return false;
//        }
        Intent intent = new Intent();
        intent.setClass(context, LoginService.class);
        intent.setAction(ACTION_LOGIN_BY_GOOGLE);
        intent.putExtra(":tag", context.getClass().getSimpleName());
        intent.putExtra(":glToken", glToken);
        intent.putExtra(":code",code);
        context.startService(intent);
        return true;
    }

    public static boolean start_ACTION_LOGIN_BY_CM(Activity context,String name,String password,String code){
        LoginDataHelper.getInstance().tmpSaveNameAndPd(name, password);
        String capture = LoginDataHelper.getInstance().getNextCmLoginCodeCapture();
//        if(TextUtils.isEmpty(code) && !TextUtils.isEmpty(capture)){
//            context.startActivityForResult(LoginDataHelper.getLaunchIntent(context,
//                    LoginDataHelper.VERIFY_DIALOG_CM_LOGIN,capture), LoginDataHelper.LOGIN_INPUT_REQUEST);
//            return false;
//        }
        Intent intent = new Intent();
        intent.setClass(context, LoginService.class);
        intent.setAction(ACTION_LOGIN_BY_CM);
        intent.putExtra(":tag", context.getClass().getSimpleName());
        intent.putExtra(":name", name);
        intent.putExtra(":password", password);
        intent.putExtra(":code",code);
        context.startService(intent);
        return true;
    }

    public static void start_ACTION_FRESH_USER_INFO(Context context){
        Intent intent = new Intent();
        intent.setClass(context, LoginService.class);
        intent.setAction(ACTION_FRESH_USER_INFO);
        context.startService(intent);
    }

    private static String getApkVersion() {
        String version = DeviceUtils.getVersionCodeS(MainEntry.getInstance().getApplicationContext());
        return version == null ? "" : version;
    }

    private void onHandle_ACTION_REPLACE_NICKNAME(Intent intent) {
        String newName = intent.getStringExtra(":new_name");
        String tag = intent.getStringExtra(":tag");
        LoginDataHelper.LoginInfo info = LoginDataHelper.getInstance().getLoginInfo();
        if(!TextUtils.isEmpty(newName) && null != info){
            CmRawObject raw = null;
            long start = System.currentTimeMillis();
            try {
                LoginDataHelper.LoginInitSid initSid = LoginDataHelper.getInstance().getLoginInitSid();
                if(null != initSid){
                    raw = UserManagerImpl.getInstance(getApplicationContext()).allUpNickname(initSid.getLogin_sid(),initSid.getLogin_sid_sig(),
                            info.getSid(),newName, AppEnvUtils.GetAndroidID(this), getApkVersion());
                }
                if(null != raw){
                    if(raw.getRet() == CM_LOGIN_SDK_EXCEPTION_SID_INVALID){
                        handleLogout();
                        if(!LoginDataHelper.getInstance().isCurrentLoginByGoogleAccount()){
                            return;
                        }
                    }
//                    reportFeedback(cm_account_login.REPLACE_NICKNAME,start,raw.getRet(),false);
                }else{
//                    reportFeedback(cm_account_login.REPLACE_NICKNAME,start,cm_account_login.RETURN_NULL_EXCEPTION,true);
                }
                EventLoginNickname avatar = new EventLoginNickname();
                if(null != raw){
                    avatar.setResult(raw.getRet());
                    if(avatar.isSuccess()){
//                        LoginDataHelper.LoginUserInfo userInfo = LoginDataHelper.getInstance().getLoginUserInfo();
//                        if(null != userInfo){
//                            userInfo.setNickname(newName);
//                            LoginDataHelper.getInstance().saveUserInfoNoClear(userInfo);
//                        }
                    }
                }
                fireEvent(avatar);
                return;
            } catch (CmLoginSdkException e) {
                if(e.getExceptionRet() == CM_LOGIN_SDK_EXCEPTION_SID_INVALID){
                    handleLogout();
                }
//                reportFeedback(cm_account_login.REPLACE_NICKNAME,start,e.getExceptionRet(),true);
                e.printStackTrace();
            }
        }
        EventLoginNickname avatar = new EventLoginNickname();
        avatar.setResult(LOGIN_EXCEPTION);
        fireEvent(avatar);
    }


    private void onHandle_ACTION_VERIFY_EMAIL(Intent intent) {
        CmRawObject raw = null;
        long start = System.currentTimeMillis();
        try {
            LoginDataHelper.LoginInitSid initSid = LoginDataHelper.getInstance().getLoginInitSid();
            LoginDataHelper.LoginInfo info = LoginDataHelper.getInstance().getLoginInfo();
            String code = intent.getStringExtra(":code");
            if(null != info && null != initSid && !TextUtils.isEmpty(code)){
                raw = UserManagerImpl.getInstance(getApplicationContext()).cmEmailActive(
                        initSid.getLogin_sid(), initSid.getLogin_sid_sig(), info.getSid(), code,
                        AppEnvUtils.GetAndroidID(this), getApkVersion());
                if(null != raw){
//                    reportFeedback(cm_account_login.VERFY_BY_EMAIL,start,raw.getRet(),false);
                }else{
//                    reportFeedback(cm_account_login.VERFY_BY_EMAIL,start,cm_account_login.RETURN_NULL_EXCEPTION,true);
                }
                if(null != raw && raw.getRet() == LOGIN_RET_SUCCESS && null != raw.getData()){
                    fireEvent(new EventVeryfyByEmail(raw.getRet()));
                }else{
                    if(raw != null) {
                        fireEvent(new EventVeryfyByEmail(raw.getRet()));
                    }else{
                        fireEvent(new EventVeryfyByEmail(LOGIN_EXCEPTION));
                    }
                }
            }else{
                fireEvent(new EventVeryfyByEmail(LOGIN_EXCEPTION));
            }
        } catch (CmLoginSdkException e) {
            fireEvent(new EventVeryfyByEmail(e.getExceptionRet()));
            e.printStackTrace();
//            reportFeedback(cm_account_login.VERFY_BY_EMAIL,start,e.getExceptionRet(),true);
        }
    }

    private void onHandle_ACTION_SEND_EMAIL_TO_ACTIVE(Intent intent) {
        CmRawObject raw = null;
        long start = System.currentTimeMillis();
        try {
            LoginDataHelper.LoginInitSid initSid = LoginDataHelper.getInstance().getLoginInitSid();
            LoginDataHelper.LoginInfo info = LoginDataHelper.getInstance().getLoginInfo();
            if(null != info && null != initSid){
                raw = UserManagerImpl.getInstance(getApplicationContext()).cmSendMail(
                        initSid.getLogin_sid(), initSid.getLogin_sid_sig(), info.getSid(), 0, null,
                        AppEnvUtils.GetAndroidID(this), getApkVersion());
                if(null != raw){
//                    reportFeedback(cm_account_login.LOGOUT_SEND_EMAIL,start,raw.getRet(),false);
                }else{
//                    reportFeedback(cm_account_login.LOGOUT_SEND_EMAIL,start,cm_account_login.RETURN_NULL_EXCEPTION,true);
                }
                if(null != raw){
                    fireEvent(new EventSendEmailResult(raw.getRet()));
                }else{
                    fireEvent(new EventSendEmailResult(-1));
                }
            } else {
                fireEvent(new EventFreshUserInfo(-1));
            }
        } catch (CmLoginSdkException e) {
            fireEvent(new EventSendEmailResult(e.getExceptionRet()));
            e.printStackTrace();
//            reportFeedback(cm_account_login.LOGOUT_SEND_EMAIL,start,e.getExceptionRet(),true);
        }
    }

    private void onHandle_ACTION_LOGOUT(Intent intent) {
        String sid = intent.getStringExtra(":sid");
        CmRawObject raw = null;
        long start = System.currentTimeMillis();
        try {
            LoginDataHelper.LoginInitSid initSid = LoginDataHelper.getInstance().getLoginInitSid();
            if(null != initSid){
                raw = UserManagerImpl.getInstance(getApplicationContext()).allLogout(
                        initSid.getLogin_sid(), initSid.getLogin_sid_sig(), sid,
                        AppEnvUtils.GetAndroidID(this), getApkVersion());
            }
            if (LoginConfigManager.getInstance(getApplicationContext()).getLastLoginOption()
                == LoginDataHelper.VERIFY_DIALOG_FACEBOOK_LOGIN) {
                FacebookSdk.sdkInitialize(getApplicationContext());
                LoginManager.getInstance().logOut();
            }
            if(null != raw){
//                reportFeedback(isLogOldToken ? cm_account_login.LOGOUT_OLD_TOKEN : cm_account_login.LOGOUT,start,raw.getRet(),false);
            }else{
//                reportFeedback(isLogOldToken ? cm_account_login.LOGOUT_OLD_TOKEN : cm_account_login.LOGOUT,start,cm_account_login.RETURN_NULL_EXCEPTION,true);
            }
        } catch (CmLoginSdkException e) {
            e.printStackTrace();
//            reportFeedback(isLogOldToken ? cm_account_login.LOGOUT_OLD_TOKEN : cm_account_login.LOGOUT,start,e.getExceptionRet(),true);
        }
    }

    private void onHandle_ACTION_RESET_PASSWORD(Intent intent) {
        String address = intent.getStringExtra(":address");
        String tag = intent.getStringExtra(":tag");
        int result = LOGIN_EXCEPTION;
        long start = System.currentTimeMillis();
        if(!TextUtils.isEmpty(address)){
            CmRawObject raw = null;
            try {
                LoginDataHelper.LoginInitSid initSid = LoginDataHelper.getInstance().getLoginInitSid();
                if(null != initSid){
                    String login_sid = initSid.getLogin_sid();
                    String login_sid_sig = initSid.getLogin_sid_sig();
                    if(!TextUtils.isEmpty(login_sid) && !TextUtils.isEmpty(login_sid_sig)){
                        raw = UserManagerImpl.getInstance(getApplicationContext()).cmFindPasswordByMail(login_sid, login_sid_sig,
                                address, AppEnvUtils.GetAndroidID(this), getApkVersion());
                    }

                }
                if(null != raw){
//                    reportFeedback(cm_account_login.RESET_PASSWORD,start,raw.getRet(),false);
                }else{
//                    reportFeedback(cm_account_login.RESET_PASSWORD,start,cm_account_login.RETURN_NULL_EXCEPTION,true);
                }
                if(raw != null) {
                    result = raw.getRet();
                }
            } catch (CmLoginSdkException e) {
                e.printStackTrace();
//                reportFeedback(cm_account_login.RESET_PASSWORD, start, e.getExceptionRet(), true);
            }
        }
        EventResetPassword event = new EventResetPassword(result);
        fireEvent(event);
    }

    private void onHandle_ACTION_UPLOAD_USER_ICON(Intent intent) {
        Bitmap map = intent.getParcelableExtra(":icon");
        String tag = intent.getStringExtra(":tag");
        LoginDataHelper.LoginInfo info = LoginDataHelper.getInstance().getLoginInfo();
        CmProxyUpAvatar upAvatar = null;
        if(null != map && null != info){
            CmRawObject raw = null;
            long start = System.currentTimeMillis();
            try {
                LoginDataHelper.LoginInitSid initSid = LoginDataHelper.getInstance().getLoginInitSid();
                if(null != initSid){
                    raw = UserManagerImpl.getInstance(getApplicationContext()).allUpAvatar(initSid.getLogin_sid(), initSid.getLogin_sid_sig(),
                            info.getSid(), map, AppEnvUtils.GetAndroidID(this), getApkVersion());
                }
                if(null != raw){
                    if(raw.getRet() == CM_LOGIN_SDK_EXCEPTION_SID_INVALID){
                        handleLogout();
                        if(!LoginDataHelper.getInstance().isCurrentLoginByGoogleAccount()){
                            return;
                        }
                    }
//                    reportFeedback(cm_account_login.UPLOAD_USER_ICON,start,raw.getRet(),false);
                }else{
//                    reportFeedback(cm_account_login.UPLOAD_USER_ICON,start,cm_account_login.RETURN_NULL_EXCEPTION,true);
                }
                if(null != raw && null != raw.getData()){
                    upAvatar = raw.getData().cast(CmProxyUpAvatar.class);
                }
                EventLoginAvatar avatar = new EventLoginAvatar();
                if(null != raw){
                    avatar.setResultRet(raw.getRet());
                }
                if(null != upAvatar && !TextUtils.isEmpty(upAvatar.getUrl())){
                    avatar.setAvatarUrl(upAvatar.getUrl());
//                    LoginDataHelper.LoginUserInfo userInfo = LoginDataHelper.getInstance().getLoginUserInfo();
//                    if(null != userInfo){
//                        userInfo.setAvatar(upAvatar.getUrl());
//                        LoginDataHelper.getInstance().saveUserInfoNoClear(userInfo);
//                    }
                }
                fireEvent(avatar);
                return;
            } catch (CmLoginSdkException e) {
                if(e.getExceptionRet() == CM_LOGIN_SDK_EXCEPTION_SID_INVALID){
                    handleLogout();
                }
//                reportFeedback(cm_account_login.UPLOAD_USER_ICON, start, e.getExceptionRet(), true);
                e.printStackTrace();
            }
        }
        EventLoginAvatar avatar = new EventLoginAvatar(LOGIN_EXCEPTION,"");
        fireEvent(avatar);
    }

    private static void cancelUpdateUserinfo(){
        Context context = MainEntry.getAppContext().getApplicationContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent();
        intent.setClass(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ALARM_UPDATE_LOGIN_DATA);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1111, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendIntentToUpdateUserinfo() {
        Context context = MainEntry.getAppContext().getApplicationContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent();
        intent.setClass(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ALARM_UPDATE_LOGIN_DATA);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1111, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long today = getTodayZeroPoint().getTime();
        long tomorrowZero = today + 1000L * 60 * 60 * 24;
        long current = tomorrowZero + (24L * 1000 * 60 * 60 * 7);
        try {
            alarmManager.set(AlarmManager.RTC, current, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Date getTodayZeroPoint() {
        GregorianCalendar now = new GregorianCalendar();
        return new GregorianCalendar(now.get(GregorianCalendar.YEAR),
                now.get(GregorianCalendar.MONTH), now.get(GregorianCalendar.DAY_OF_MONTH))
                .getTime();
    }

    private void onHandle_ACTION_REGIST_BY_CM(Intent intent) {
        String name = intent.getStringExtra(":name");
        String password = intent.getStringExtra(":password");
        String code = intent.getStringExtra(":code");
        String tag = intent.getStringExtra(":tag");
        CmRawObject raw = null;
        long start = System.currentTimeMillis();
        try {
            LoginDataHelper.LoginInitSid initSid = LoginDataHelper.getInstance().getLoginInitSid();
            if(null != initSid){
                raw = UserManagerImpl.getInstance(getApplicationContext()).cmRegistWithExtra(initSid.getRegist_sid(),
                        initSid.getRegist_sid_sig(),name, password, code,
                        AppEnvUtils.GetAndroidID(this), getApkVersion());
            }
            if(null != raw){
//                reportFeedback(cm_account_login.LOGIN_BY_REGIST,start,raw.getRet(),false);
            }else{
//                reportFeedback(cm_account_login.LOGIN_BY_REGIST,start,cm_account_login.RETURN_NULL_EXCEPTION,true);
            }
        } catch (CmLoginSdkException e) {
//            reportFeedback(cm_account_login.LOGIN_BY_REGIST,start,e.getExceptionRet(),true);
            e.printStackTrace();
        }
        handleRawAndGetUserInfo(raw, LoginDataHelper.VERIFY_DIALOG_CM_REGIST, tag, name,0);
    }

    private void onHandle_ACTION_FRESH_USER_INFO(Intent intent) {
        CmRawObject raw = null;
        try {
//            cancelUpdateUserinfo();
//            sendIntentToUpdateUserinfo();
            LoginDataHelper.LoginInitSid initSid = LoginDataHelper.getInstance().getLoginInitSid();
            LoginDataHelper.LoginInfo info = LoginDataHelper.getInstance().getLoginInfo();
            if(null != info && null != initSid){
                raw = UserManagerImpl.getInstance(getApplicationContext()).allUserinfo(
                        initSid.getLogin_sid(), initSid.getLogin_sid_sig(), info.getSid(),
                        AppEnvUtils.GetAndroidID(this), getApkVersion());
                if(null != raw){
                    if(raw.getRet() == CM_LOGIN_SDK_EXCEPTION_SID_INVALID){
                        if(!LoginDataHelper.getInstance().isCurrentLoginByGoogleAccount()){
                            return;
                        }
                    }
                }
                if(null != raw && raw.getRet() == LOGIN_RET_SUCCESS && null != raw.getData()){
                    LoginDataHelper.getInstance().saveLoginUserInfo(raw.getData().getInnerJSONObject().toString(),LoginDataHelper.VERIFY_DIALOG_DEFAULT);
                    fireEvent(new EventFreshUserInfo(raw.getRet()));
                }else{
                    if(raw != null) {
                        fireEvent(new EventFreshUserInfo(raw.getRet()));
                    }
                }
            }else{
                fireEvent(new EventFreshUserInfo(LOGIN_EXCEPTION));
            }
        } catch (CmLoginSdkException e) {
            fireEvent(new EventFreshUserInfo(e.getExceptionRet()));
            e.printStackTrace();
//            reportFeedback(cm_account_login.LOGOUT_FRESH_USER_INFO, start, e.getExceptionRet(), true);
        }
    }

    private static void reportFeedback(int type,long startTime,int feedback,boolean isReportOnlyInNet){
//        if(isReportOnlyInNet){
//            if(NetworkUtil.isNetworkUp(MainEntry.getAppContext())){
//                new cm_account_login().stage(type).stagetime((int)((System.currentTimeMillis() - startTime)))
//                        .feedback(feedback).report();
//            }
//        }else{
//            new cm_account_login().stage(type).stagetime((int)((System.currentTimeMillis() - startTime)))
//                    .feedback(feedback).report();
//        }
    }

    private void handleRawAndGetUserInfo(CmRawObject raw,int type,String tag,String email,int from) {
        if(TextUtils.isEmpty(tag)){
            tag = "";
        }
        int loginResult = LOGIN_EXCEPTION;
        if(null != raw){
            LoginDataHelper.getInstance().log("handleRawAndGetUserInfo\t" + raw.getRet());
            try {
                loginResult = raw.getRet();
                CmProxyLogin cmLogin = raw.getData().cast(CmProxyLogin.class);
                String capture = cmLogin.getCaptcha();
                LoginDataHelper.getInstance().log("capture\t" + capture);
                LoginDataHelper.getInstance().saveNextCodeCapture(capture,type);
                EventLogin login = null;
                switch (raw.getRet()) {
                    case LOGIN_RET_SUCCESS:
                        JSONObject loginJson = new JSONObject();
                        try {
                            loginJson.put("sid",cmLogin.getSid());
                            loginJson.put("sso_token", cmLogin.getSsoToken());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        CmRawObject userInfo = raw.getData().cast(CmProxyLogin.class).getExtra();
                        LoginDataHelper.getInstance().setLoginState(true);
                        LoginDataHelper.getInstance().saveLoginData(loginJson.toString());
                        LoginDataHelper.getInstance().saveLoginUserInfo(userInfo.getData().getInnerJSONObject().toString(),type);
//                        cancelUpdateUserinfo();
//                        sendIntentToUpdateUserinfo();
                        LoginDataHelper.LoginInfo info = LoginDataHelper.getInstance().getLoginInfo();
                        login = new EventLogin(loginResult,tag);
                        login.setSouce(from);
                        login.setLoginType(type);
                        fireEvent(login);
                        if(email != null) {
                            LoginConfigManager.getInstance(MainEntry.getAppContext()).setLoginUserEmail(email);
                        }
                        //登录时记录登录的方式
                        switch(type){
                            case LoginDataHelper.VERIFY_DIALOG_FACEBOOK_LOGIN:
                                LoginConfigManager.getInstance(MainEntry.getAppContext()).setLoginOption(LoginDataHelper.VERIFY_DIALOG_FACEBOOK_LOGIN);
                                break;
                            case LoginDataHelper.VERIFY_DIALOG_GOOGLE_LOGIN:
                                LoginConfigManager.getInstance(MainEntry.getAppContext()).setLoginOption(LoginDataHelper.VERIFY_DIALOG_GOOGLE_LOGIN);
                                LoginConfigManager.getInstance(MainEntry.getAppContext()).
                                        setLastTimeFreshGoogleToken(System.currentTimeMillis());
                                break;
                            case LoginDataHelper.VERIFY_DIALOG_CM_LOGIN:
                            case LoginDataHelper.VERIFY_DIALOG_CM_REGIST:
                                LoginConfigManager.getInstance(MainEntry.getAppContext()).setLoginOption(LoginDataHelper.VERIFY_DIALOG_CM_LOGIN);
                                break;
                            default:
                                break;
                        }

                        LoginConfigManager.getInstance(MainEntry.getAppContext()).putFreshTime("");
                        if(info != null){
                            String address = LoginDataHelper.getInstance().getTmpLoginName();
                            if(!TextUtils.isEmpty(address)){
                                LoginDataHelper.getInstance().saveLastLoginAddress(address);
                            }
                            if(!LoginDataHelper.getInstance().isLogined()){
                                return;
                            }
                        }else{
                            loginResult = LOGIN_LOGIN_PARSE_DATA_FAIL;
                        }
                        break;
                    default:
                        login = new EventLogin(loginResult,tag);
                        login.setSouce(from);
                        login.setLoginType(type);
                        fireEvent(login);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                EventLogin login = new EventLogin(loginResult,tag);
                login.setSouce(from);
                login.setLoginType(type);
                fireEvent(login);
            }
        }else{
            EventLogin login = new EventLogin(loginResult,tag);
            login.setSouce(from);
            login.setLoginType(type);
            fireEvent(login);
        }
    }

    public static void logout(){
        LoginDataHelper.LoginInfo info = LoginDataHelper.getInstance().getLoginInfo();
        if(null != info){
            LoginService.start_ACTION_LOGOUT(MainEntry.getAppContext(), info.getSid());
        }
        LoginDataHelper.getInstance().setLoginState(false);
        fireEvent(new EventLogout());
    }


    private void handleLogout(){
        logout();
        fireEvent(new EventLoginTokenInvalid());
    }

    public class HttpCode{
        public HttpCode(int code){
            this.code = code;
        }
        public int code = 0;
    }

    private void onHandle_ACTION_LOGIN_BY_FACEBOOK(Intent intent) {
        String fbToken = intent.getStringExtra(":fbToken");
        String code = intent.getStringExtra(":code");
        String tag = intent.getStringExtra(":tag");
        int from = intent.getIntExtra(":from",0);
        fireEvent(new EventShowLoadingForFb());
        CmRawObject raw = null;
        long start = System.currentTimeMillis();
        try {
            LoginDataHelper.LoginInitSid initSid = LoginDataHelper.getInstance().getLoginInitSid();
            if(null != initSid){
                raw = UserManagerImpl.getInstance(getApplicationContext()).thirdPartyLogin(initSid.getThird_sid(),
                        initSid.getThird_sid_sig(),fbToken, code, "103", true,
                        AppEnvUtils.GetAndroidID(this), getApkVersion());
            }
            if(null != raw){
//                reportFeedback(cm_account_login.LOGIN_BY_FACEBOOK,start,raw.getRet(),false);
            }else{
//                reportFeedback(cm_account_login.LOGIN_BY_FACEBOOK,start,cm_account_login.RETURN_NULL_EXCEPTION,true);
            }
        } catch (CmLoginSdkException e) {
//            reportFeedback(cm_account_login.LOGIN_BY_FACEBOOK,start,e.getExceptionRet(),true);
            e.printStackTrace();
        }
        handleRawAndGetUserInfo(raw, LoginDataHelper.VERIFY_DIALOG_FACEBOOK_LOGIN,tag, "",from);
    }

    private void onHandle_ACTION_CHECK_ACCOUNT_EXIST(Intent intent){
        String account = intent.getStringExtra(":account");
        int result = LOGIN_EXCEPTION;
        long start = System.currentTimeMillis();
        if(!TextUtils.isEmpty(account)){
            CmRawObject raw = null;
            try {
                LoginDataHelper.LoginInitSid initSid = LoginDataHelper.getInstance().getLoginInitSid();
                if(null != initSid){
                    raw = UserManagerImpl.getInstance(getApplicationContext()).cmIsAccountExist(initSid.getLogin_sid(), initSid.getLogin_sid_sig(),account,
                            AppEnvUtils.GetAndroidID(this), getApkVersion());
                }
                if(null != raw){
//                    reportFeedback(cm_account_login.CHECK_IS_ACCOUNT_EXIST,start,raw.getRet(),false);
                }else{
//                    reportFeedback(cm_account_login.CHECK_IS_ACCOUNT_EXIST,start,cm_account_login.RETURN_NULL_EXCEPTION,true);
                }
                result = raw.getRet();
            } catch (CmLoginSdkException e) {
                e.printStackTrace();
//                reportFeedback(cm_account_login.CHECK_IS_ACCOUNT_EXIST,start,e.getExceptionRet(),true);
            }
        }
        EventAccountExist event = new EventAccountExist(result);
        fireEvent(event);
    }

    private void onHandle_ACTION_LOGIN_BY_GOOGLE(Intent intent) {
        String glToken = intent.getStringExtra(":glToken");
        String code = intent.getStringExtra(":code");
        String tag = intent.getStringExtra(":tag");
        fireEvent(new EventShowLoadingForFb());
        CmRawObject raw = null;
        long start = System.currentTimeMillis();
        try {
            LoginDataHelper.LoginInitSid initSid = LoginDataHelper.getInstance().getLoginInitSid();
            if(null != initSid){
                raw = UserManagerImpl.getInstance(getApplicationContext()).googleLoginWithExtra(initSid.getThird_sid(),
                        initSid.getThird_sid_sig(), glToken, code,
                        AppEnvUtils.GetAndroidID(this), getApkVersion());
            }
            if(null != raw){
//                reportFeedback(cm_account_login.LOGIN_BY_GOOGLE,start,raw.getRet(),false);
            }else{
//                reportFeedback(cm_account_login.LOGIN_BY_GOOGLE,start,cm_account_login.RETURN_NULL_EXCEPTION,true);
            }
        } catch (CmLoginSdkException e) {
//            reportFeedback(cm_account_login.LOGIN_BY_GOOGLE,start,e.getExceptionRet(),true);
            e.printStackTrace();
        }
        handleRawAndGetUserInfo(raw, LoginDataHelper.VERIFY_DIALOG_GOOGLE_LOGIN, tag, "",0);
    }

    private void onHandle_ACTION_LOGIN_BY_CM(Intent intent) {
        String name = intent.getStringExtra(":name");
        String password = intent.getStringExtra(":password");
        String code = intent.getStringExtra(":code");
        String tag = intent.getStringExtra(":tag");
        CmRawObject raw = null;
        long start = System.currentTimeMillis();
        try {
            LoginDataHelper.LoginInitSid initSid = LoginDataHelper.getInstance().getLoginInitSid();
            if(null != initSid){
                raw = UserManagerImpl.getInstance(getApplicationContext()).cmLoginWithExtra(initSid.getLogin_sid(),
                        initSid.getLogin_sid_sig(),name, password, code,
                        AppEnvUtils.GetAndroidID(this), getApkVersion());
            }
            if(null != raw){
//                reportFeedback(cm_account_login.LOGIN_BY_CM_EMAIL,start,raw.getRet(),false);
            }else{
//                reportFeedback(cm_account_login.LOGIN_BY_CM_EMAIL,start,cm_account_login.RETURN_NULL_EXCEPTION,true);
            }
        } catch (CmLoginSdkException e) {
//            reportFeedback(cm_account_login.LOGIN_BY_CM_EMAIL,start,e.getExceptionRet(),true);
            e.printStackTrace();
        }
        handleRawAndGetUserInfo(raw,LoginDataHelper.VERIFY_DIALOG_CM_LOGIN,tag, name,0);
    }

    private static void fireEvent(ONewsEvent event) {
        if(event instanceof EventLogin){
            reprotLoginMaidian((EventLogin)event);
        }
        LoginDataHelper.getInstance().log("fireEvent\t" + event);
        ONewsEventManager.getInstance().sendEvent(event);
    }

    private static void reprotLoginMaidian(EventLogin event) {
        if(null != event){
            new newsindia_login().page(event.getSouce()).ifsuccessful(event.isLoginSuccess() ? 1 : 0).stage(1).report();
        }
    }


    public static void handleLoginEvent(Activity activity, EventLogin event,final OnCallback onVerifyCallback) {
        int toast = 0;
        boolean isShowToast = event.getFrom().equalsIgnoreCase(activity.getClass().getSimpleName());
        if(event.isLoginSuccess()){
            if(null != onVerifyCallback){
                onVerifyCallback.onSuccess();
            }
            toast = R.string.log_in_sucess;
            //CloudRecycleEngineWrapper.getInstance().setCloudrecycleEnabled(true);
        }else{
            switch (event.getLoginRaw()){
                case LOGIN_PASSWORD_ERROR:
                case LOGIN_USERNAME_OR_PASSWORD_ERROR:
                    toast = R.string.login_password_error;
                    break;
                case LOGIN_USERNAME_USED:
                case LOGIN_EMAIL_REGISTED:
//                    toast = R.string.login_email_is_used;

                    break;
                case LOGIN_TIMEOUT:
                    toast = R.string.login_timeout;
                    break;
                case LOGIN_CODE_ERROR:
                    toast = R.string.login_code_error;
                    break;
                case LOGIN_USERNAME_NOT_EXIST:
//                    toast = R.string.login_username_not_exist;
                    break;
                case LOGIN_TOO_BUSINESS:
                    toast = R.string.login_too_business;
                    break;
                case LOGIN_INVALID_USER:
                    toast = R.string.login_invalid_user;
                    break;
                case LOGIN_FB_TOKEN_ERROR:
                    toast = R.string.login_fb_token_error;
                    break;
                case LOGIN_USERNAME_ERROR:
                case LOGIN_EMAIL_ERROR:
                    toast = R.string.login_username_error;
                    break;
                default:
                    if(NetworkUtil.isNetworkUp(MainEntry.getAppContext())){
                        if(isShowToast){
                            Toast.makeText(activity,R.string.log_in_failed,Toast.LENGTH_SHORT).show();
                            if (L.DEBUG){
                                final String string = activity.getString(event.getLoginType() == LoginDataHelper.VERIFY_DIALOG_CM_REGIST ?
                                        R.string.login_regist_failed : R.string.login_failed, event.getLoginRaw());
                                LoginDataHelper.getInstance().log(string);
                            }
                        }
                    }else{
                        toast = R.string.onews__no_network;
                    }
                    break;
            }
        }
        if(isShowToast && 0 != toast){
            Toast.makeText(activity,toast,Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnCallback {
        public void onSuccess();
    }

}
