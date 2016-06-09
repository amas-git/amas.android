package com.cmcm.login;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.toolbox.Volley;
import com.cleanmaster.sdk.cmloginsdkjar.Settings;
import com.cmcm.onews.BuildConfig;
import com.cmcm.onews.MainEntry;
import com.cmcm.onews.bitmapcache.VolleySingleton;
import com.cmcm.onews.event.ONewsEventManager;
import com.cmcm.onews.util.DimenUtils;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by blue on 14-12-26.
 */
public class LoginDataHelper {

    private static LoginDataHelper sInstance = null;

    private static final int LOGIN_INIT = -1;
    public static final int LOGIN_OUT = 0;
    public static final int LOGINED_AND_GETTING_DATA = 1;
    public static final int LOGINED_AND_GETTING_DATA_FAILED = 2;
    public static final int LOGINED_AND_GET_DATA_SUCCESS = 3;
    public static final int LOGINED_AND_SERVICE_HAS_NO_DATA = 4;
    public static final int LOGINED_AND_LOGINED_BY_OLD_TOKEN = 5;

    public static final int VERIFY_DIALOG_DEFAULT = -1;
    public static final int VERIFY_DIALOG_CM_LOGIN = 0;
    public static final int VERIFY_DIALOG_CM_REGIST = 1;
    public static final int VERIFY_DIALOG_FACEBOOK_LOGIN = 2;
    public static final int VERIFY_DIALOG_GOOGLE_LOGIN = 4;

    public static final int ICON_WIDTH = 35;

    private LoginInfo mLoginInfo = null;
    private LoginInitSid mLoginInitSid = null;
    private GoogleAccountData mGoogleAccountData = null;
    private LoginUserInfo mLoginUserInfo = null;
    private String mLastAddress = "";
    private int mCrrentLoginType = -1;

    private String mLoginName = "";
    private String mLoginPassword = "";
    private String mFacebookToken = "";
    private String mGoogleToken = "";
    private String mNewestFbName = "";
    private String mNewestFbAvatar = "";
    private volatile int mLoginBasicState = LOGIN_INIT;

    private static final long SIX_DAYS = 1000L * 60 * 60 * 24 * 6;

    private static final long SEVEN_DAYS = 1000L * 60 * 60 * 24 * 7;
    private static final long FOUR_HOURS = 1000L * 60 * 60 * 4;
    private static final int SEVEN_DAYS_TOTAL_LIMIT = 20;
    private static final int FRESH_EACH_DAY_LIMIT = 3;


    private LoginDataHelper() {
    }

    public void tmpSaveNameAndPd(String name, String password) {
        mLoginName = name;
        mLoginPassword = password;
    }

    public void setNewestFbName(String fbName){
        mNewestFbName = fbName;
    }

    public void setNewestFbAvatar(String fbAvatar){
        mNewestFbAvatar = fbAvatar;
    }

    public String getNewestFbAvatar(){
        return mNewestFbAvatar;
    }

    public String getNewestFbName(){
        return mNewestFbName;
    }

    public void tmpSaveFacebookToken(String token) {
        mFacebookToken = token;
    }

    public void tmpSaveGoogleToken(String token) {
        mGoogleToken = token;
    }

    public String getTmpLoginName() {
        return mLoginName;
    }

    public String getTmpLoginPassword() {
        return mLoginPassword;
    }

    public String getTmpFacebookToken() {
        return mFacebookToken;
    }

    public String getTmpGoogleToken() {
        return mGoogleToken;
    }

    public synchronized static LoginDataHelper getInstance() {
        if (null == sInstance) {
            sInstance = new LoginDataHelper();
        }
        return sInstance;
    }

    public synchronized void saveLoginData(String data) {
        LoginDataHelper.getInstance().log("saveLoginData\t" + data);
        LoginConfigManager.getInstance(MainEntry.getAppContext()).setLoginData(data);
        mLoginInfo = null;
        mLoginInfo = getLoginInfo();
    }

    public synchronized void setLoginState(boolean isLogined) {
        LoginDataHelper.getInstance().log("setLoginState\t" + isLogined);
        String iconUrl = "";
        if(null != mLoginUserInfo){
            iconUrl = mLoginUserInfo.getAvatar();
        }
        saveLoginBasicState(isLogined ? LOGINED_AND_GET_DATA_SUCCESS : LOGIN_OUT);
        if (!isLogined) {
            saveLoginData("");
            saveLoginUserInfo("",VERIFY_DIALOG_DEFAULT);
            mLoginName = "";
            mLoginPassword = "";
            mFacebookToken = "";
            mGoogleToken = "";
            mNewestFbName = "";
            mNewestFbAvatar = "";
            mLoginBasicState = LOGIN_OUT;
        }
        if(LoginConfigManager.getInstance(MainEntry.getAppContext()).getLastLoginOption() == VERIFY_DIALOG_FACEBOOK_LOGIN &&
                !TextUtils.isEmpty(iconUrl)){
            VolleySingleton.getInstance().getDiskBasedCache().remove(iconUrl);
        }
    }

    public void saveLoginBasicState(int state) {
        LoginDataHelper.getInstance().log("saveLoginBasicState\t" + state);
        mLoginBasicState = state;
        LoginConfigManager.getInstance(MainEntry.getAppContext()).setLoginBasicState(state);
    }

    public boolean isLogined() {
        return getLoginBasicStat() == LOGINED_AND_GET_DATA_SUCCESS;
    }

    public synchronized int getLoginBasicStat() {
        if (LOGIN_INIT == mLoginBasicState) {
            mLoginBasicState = LoginConfigManager.getInstance(MainEntry.getAppContext()).getLoginBasicState();
        }
        return mLoginBasicState;
    }

    public synchronized LoginInfo getLoginInfo() {
        if (null == mLoginInfo) {
            String data = LoginConfigManager.getInstance(MainEntry.getAppContext()).getLoginData();
            if (!TextUtils.isEmpty(data)) {
                mLoginInfo = new LoginInfo(data);
            }
        }
        return mLoginInfo;
    }

    public LoginInitSid getLoginInitSid() {
        if (null == mLoginInitSid) {
            String data = LoginConfigManager.getInstance(MainEntry.getAppContext()).getLoginSidInit();
            if (!TextUtils.isEmpty(data)) {
                mLoginInitSid = new LoginInitSid(data);
            }
        }
        return mLoginInitSid;
    }

    public boolean initSid() {
        LoginInitSid sid = getLoginInitSid();
        if (sid == null || !(!TextUtils.isEmpty(sid.getLogin_sid()) && !TextUtils.isEmpty(sid.getLogin_sid_sig()) &&
                !TextUtils.isEmpty(sid.getRegist_sid()) && !TextUtils.isEmpty(sid.getRegist_sid_sig())
                && !TextUtils.isEmpty(sid.getThird_sid()) && !TextUtils.isEmpty(sid.getThird_sid_sig()))) {
            try {
                Settings.sdkInitialize(MainEntry.getAppContext().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public void saveInitSid() {
        JSONObject object = new JSONObject();
        try {
            object.put("regist_sid", Settings.getRegistSid());
            object.put("regist_sid_sig", Settings.getRegistSidSig());
            object.put("third_sid", Settings.getThirdSid());
            object.put("third_sid_sig", Settings.getThirdSidSig());
            object.put("login_sid", Settings.getLoginSid());
            object.put("login_sid_sig", Settings.getLoginSidSig());
            LoginConfigManager.getInstance(MainEntry.getAppContext()).setLoginSidInit(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void log(String log){
        if(BuildConfig.DEBUG){
            Log.i("login", log);
        }
    }

    public synchronized void saveLoginUserInfo(String data,int loginType) {
        LoginDataHelper.getInstance().log("saveLoginUserInfo before\t" + data);
        if(loginType == VERIFY_DIALOG_FACEBOOK_LOGIN){
            if(!TextUtils.isEmpty(getNewestFbName()) || !TextUtils.isEmpty(getNewestFbAvatar())){
                try {
                    JSONObject o = new JSONObject(data);
                    if(!TextUtils.isEmpty(getNewestFbName())){
                        o.put("nickname",getNewestFbName());
                    }
                    if(!TextUtils.isEmpty(getNewestFbAvatar())){
                        o.put("avatar", getNewestFbAvatar());
                    }
                    data = o.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LoginDataHelper.getInstance().log("saveLoginUserInfo after\t" + data);
        }
        LoginConfigManager.getInstance(MainEntry.getAppContext()).setLoginUserInfo(data);
        mLoginUserInfo = null;
        mLoginUserInfo = getLoginUserInfo();
    }

    public void saveNextCodeCapture(String capture, int type) {
        switch (type) {
            case VERIFY_DIALOG_CM_LOGIN:
                LoginConfigManager.getInstance(MainEntry.getAppContext()).setLoginCmCaptureCodeUrl(capture);
                break;
            case VERIFY_DIALOG_CM_REGIST:
                LoginConfigManager.getInstance(MainEntry.getAppContext()).setRegistCmCaptureCodeUrl(capture);
                break;
            case VERIFY_DIALOG_FACEBOOK_LOGIN:
                LoginConfigManager.getInstance(MainEntry.getAppContext()).setLoginFacebookCaptureCodeUrl(capture);
                break;
            case VERIFY_DIALOG_GOOGLE_LOGIN:
                LoginConfigManager.getInstance(MainEntry.getAppContext()).setLoginGoogleCaptureCodeUrl(capture);
                break;
            default:
                break;
        }
    }

    public String getNextCodeCaptureByType(int loginType) {
        switch (loginType) {
            case VERIFY_DIALOG_CM_LOGIN:
                return getNextCmLoginCodeCapture();
            case VERIFY_DIALOG_CM_REGIST:
                return getNextCmRegistCodeCapture();
            case VERIFY_DIALOG_FACEBOOK_LOGIN:
                return getNextLoginFacebookCodeCapture();
            case VERIFY_DIALOG_GOOGLE_LOGIN:
                return getNextLoginGoogleCodeCapture();
            default:
                break;
        }
        return "";
    }

    public String getNextCmLoginCodeCapture() {
        return LoginConfigManager.getInstance(MainEntry.getAppContext()).getLoginCmCaptureCodeUrl();
    }

    public String getNextCmRegistCodeCapture() {
        return LoginConfigManager.getInstance(MainEntry.getAppContext()).getRegistCmCaptureCodeUrl();
    }

    public String getNextLoginFacebookCodeCapture() {
        return LoginConfigManager.getInstance(MainEntry.getAppContext()).getLoginFacebookCaptureCodeUrl();
    }

    public String getNextLoginGoogleCodeCapture() {
        return LoginConfigManager.getInstance(MainEntry.getAppContext()).getLoginGoogleCaptureCodeUrl();
    }

    public void saveLastLoginAddress(String address) {
        LoginConfigManager.getInstance(MainEntry.getAppContext()).setLoginLastAddress(address);
        mLastAddress = address;
    }

    public String getLastLoginAddress() {
        if (TextUtils.isEmpty(mLastAddress)) {
            mLastAddress = LoginConfigManager.getInstance(MainEntry.getAppContext()).getLoginLastAddress();
        }
        return mLastAddress;
    }

    public boolean isCurrentLoginByGoogleAccount() {
        if (-1 == mCrrentLoginType) {
            mCrrentLoginType = LoginConfigManager.getInstance(MainEntry.getAppContext()).getLastLoginOption();
        }
        return mCrrentLoginType == VERIFY_DIALOG_GOOGLE_LOGIN;
    }

    public void freshGoogleToken() {
//        BackgroundThread.post(new Runnable() {
//            @Override
//            public void run() {
//                if (NetworkUtil.isWifiNetworkUp(MainEntry.getAppContext())) {
//                    handleFreshToken();
//                } else {
//                    int use3g = CloudCfgDataWrapper.getCloudCfgIntValue(CloudCfgKey.CLOUD_LOGIN, CloudCfgKey.LOGIN_FRESH_TOKEN_USE_3G, 0);
//                    if (1 == use3g) {
//                        String strNetType = NetworkUtil.getNetworkType(MainEntry.getAppContext(), "0");
//                        if (strNetType.equalsIgnoreCase("3g") && NetworkUtil.isNetworkUp(MainEntry.getAppContext())) {
//                            handleFreshToken();
//                        }
//                    }
//                }
//            }
//
//            private void handleFreshToken() {
//                long lastTime = LoginConfigManager.getInstance(MainEntry.getAppContext()).getLastTimeFreshGoogleToken();
//                if (System.currentTimeMillis() - lastTime <= SIX_DAYS) {
//                    return;
//                }
//                if (LoginDataHelper.getInstance().canFreshToday()) {
//                    LoginService.start_ACTION_FRESH_GOOGLE_TOKEN(MainEntry.getAppContext());
//                }
//            }
//        });
    }

    public boolean canFreshToday() {
//        String get = UIConfigManager.getInstanse(MainEntry.getAppContext()).getFreshLimit();
//        int currentShow = 0;
//        int showCount = 0;
//        long lastTime = 0l;
//        long initTime = 0l;
//        if (!TextUtils.isEmpty(get)) {
//            currentShow = Integer.parseInt(get.split(";")[0]);
//            showCount = Integer.parseInt(get.split(";")[1]);
//            lastTime = Long.parseLong(get.split(";")[2]);
//            initTime = Long.parseLong(get.split(";")[3]);
//        } else {
//            String put = 1 + ";" + 1 + ";" + System.currentTimeMillis() + ";" + System.currentTimeMillis();
//            UIConfigManager.getInstanse(MainEntry.getAppContext()).putFreshTime(put);
//            return true;
//        }
//
//        if (System.currentTimeMillis() - initTime > SEVEN_DAYS) {
//            if (showCount < SEVEN_DAYS_TOTAL_LIMIT) {
//                String put = 1 + ";" + 1 + ";" + System.currentTimeMillis() + ";" + System.currentTimeMillis();
//                UIConfigManager.getInstanse(MainEntry.getAppContext()).putFreshTime(put);
//                return true;
//            }
//        } else {
//            if (DateUtil.isToday(lastTime)) {
//                if (System.currentTimeMillis() - lastTime > FOUR_HOURS && currentShow < FRESH_EACH_DAY_LIMIT) {
//                    lastTime = System.currentTimeMillis();
//                    currentShow += 1;
//                    showCount += 1;
//                    String put = currentShow + ";" + showCount + ";" + lastTime + ";" + initTime;
//                    UIConfigManager.getInstanse(MainEntry.getAppContext()).putFreshTime(put);
//                    return true;
//                }
//            } else {
//                lastTime = System.currentTimeMillis();
//                currentShow = 1;
//                showCount += 1;
//                String put = currentShow + ";" + showCount + ";" + lastTime + ";" + initTime;
//                UIConfigManager.getInstanse(MainEntry.getAppContext()).putFreshTime(put);
//                return true;
//            }
//        }
        return false;
    }

    public void saveGoogleAccountData(String token, String user, String userFace) {
        JSONObject object = new JSONObject();
        try {
            if (!TextUtils.isEmpty(token)) {
                object.put(":token", token);
            }
            if (!TextUtils.isEmpty(user)) {
                object.put(":user", user);
            }
            if (!TextUtils.isEmpty(userFace)) {
                object.put(":userFace", userFace);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LoginConfigManager.getInstance(MainEntry.getAppContext()).setGoogleAccountData(object.toString());
        mGoogleAccountData = null;
        mGoogleAccountData = getGoogleAccountData();
    }

    public GoogleAccountData getGoogleAccountData() {
        if (null == mGoogleAccountData) {
            String data = LoginConfigManager.getInstance(MainEntry.getAppContext()).getGoogleAccountData();
            if (!TextUtils.isEmpty(data)) {
                mGoogleAccountData = new GoogleAccountData(data);
            }
        }
        return mGoogleAccountData;
    }

    public LoginUserInfo getLoginUserInfo() {
        if (!isLogined()) {
            return null;
        }
        if (null == mLoginUserInfo) {
            String data = LoginConfigManager.getInstance(MainEntry.getAppContext()).getLoginUserInfo();
            if (!TextUtils.isEmpty(data)) {
                mLoginUserInfo = new LoginUserInfo(data);
            }
        }
        return mLoginUserInfo;
    }

    public class GoogleAccountData {
        String token;
        String userName;
        String userFace;

        public GoogleAccountData(String data) {
            JSONObject object = null;
            try {
                object = new JSONObject(data);
                setToken(object.optString(":token"));
                setUserName(object.optString(":user"));
                setUserFace(object.optString(":userFace"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserFace() {
            return userFace;
        }

        public void setUserFace(String userFace) {
            this.userFace = userFace;
        }

    }

    public class LoginInitSid {

        private String regist_sid;
        private String regist_sid_sig;
        private String third_sid;
        private String third_sid_sig;
        private String login_sid;
        private String login_sid_sig;

        public LoginInitSid(String data) {
            JSONObject object = null;
            try {
                object = new JSONObject(data);
                setRegist_sid(object.optString("regist_sid"));
                setRegist_sid_sig(object.optString("regist_sid_sig"));
                setThird_sid(object.optString("third_sid"));
                setThird_sid_sig(object.optString("third_sid_sig"));
                setLogin_sid(object.optString("login_sid"));
                setLogin_sid_sig(object.optString("login_sid_sig"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getRegist_sid() {
            return regist_sid;
        }

        public void setRegist_sid(String regist_sid) {
            this.regist_sid = regist_sid;
        }

        public String getRegist_sid_sig() {
            return regist_sid_sig;
        }

        public void setRegist_sid_sig(String regist_sid_sig) {
            this.regist_sid_sig = regist_sid_sig;
        }

        public String getThird_sid() {
            return third_sid;
        }

        public void setThird_sid(String third_sid) {
            this.third_sid = third_sid;
        }

        public String getThird_sid_sig() {
            return third_sid_sig;
        }

        public void setThird_sid_sig(String third_sid_sig) {
            this.third_sid_sig = third_sid_sig;
        }

        public String getLogin_sid() {
            return login_sid;
        }

        public void setLogin_sid(String login_sid) {
            this.login_sid = login_sid;
        }

        public String getLogin_sid_sig() {
            return login_sid_sig;
        }

        public void setLogin_sid_sig(String login_sid_sig) {
            this.login_sid_sig = login_sid_sig;
        }

    }


    public class LoginUserInfo {

        private String sign;
        private String open_id_str;
        private String birthday;
        private String is_active;
        private String address;
        private String nickname;
        private String profession;
        private String gender;
        private String education;
        private String avatar;
        private String fullname;
        private String email;
        private String is_email;
        private String is_mobile;
        private String mobile;
        private String has_pwd;

        public LoginUserInfo(String data) {
            try {
                JSONObject object = new JSONObject(data);
                setSign(object.optString("sign"));
                setOpenId(object.optString("open_id_str"));
                setBirthday(object.optString("birthday"));
                setIs_active(object.optString("is_active"));
                setAddress(object.optString("address"));
                setNickname(object.optString("nickname"));
                setProfession(object.optString("profession"));
                setGender(object.optString("gender"));
                setEducation(object.optString("education"));
                setAvatar(object.optString("avatar"));
                setFullname(object.optString("fullname"));
                setEmail(object.optString("email"));
                setIs_email(object.optString("is_email"));
                setIs_mobile(object.optString("is_mobile"));
                setMobile(object.optString("mobile"));
                setHaspsw(object.optString("has_pwd"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public LoginUserInfo() {
        }

        public void setHaspsw(String haspsw) {
            this.has_pwd = haspsw;
        }

        public String getHaspsw() {
            return has_pwd;
        }

        public String getIs_active() {
            return is_active;
        }

        public void setIs_email(String is_email) {
            this.is_email = is_email;
        }

        public String getIs_email() {
            return is_email;
        }

        public String getIs_mobile() {
            if (is_mobile == null) {
                return "0";
            }
            return is_mobile;
        }

        public String getMobile() {
            return mobile;
        }

        public void setIs_mobile(String is_mobile) {
            this.is_mobile = is_mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getOpenId() {
            return open_id_str;
        }

        public void setOpenId(String openId) {
            this.open_id_str = openId;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getIsActive() {
            return is_active;
        }

        public void setIs_active(String is_active) {
            this.is_active = is_active;
//            ServiceConfigManager.getInstanse(MoSecurityApplication.getAppContext()).setEmailVerify(is_active);
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getProfession() {
            return profession;
        }

        public void setProfession(String profession) {
            this.profession = profession;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getEducation() {
            return education;
        }

        public void setEducation(String education) {
            this.education = education;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
//            if (!TextUtils.isEmpty(email)) {
//                ServiceConfigManager.getInstanse(MoSecurityApplication.getAppContext()).setLoginUserEmail(email);
//            }
        }

    }

    public class LoginInfo {

        public LoginInfo(String data) {
            try {
                JSONObject object = new JSONObject(data);
                setSid(object.optString("sid"));
                setSso_token(object.optString("sso_token"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getSso_token() {
            return sso_token;
        }

        public void setSso_token(String sso_token) {
            this.sso_token = sso_token;
        }

        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }

        private String sso_token;
        private String sid;

    }


}
