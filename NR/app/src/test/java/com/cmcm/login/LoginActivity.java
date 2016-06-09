package com.cmcm.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcm.onews.R;
import com.cmcm.onews.event.ONewsEvent;
import com.cmcm.onews.fragment.NewsBaseFragment;
import com.cmcm.onews.ui.NewsBaseUIActivity;
import com.cmcm.onews.util.ConflictCommons;

public class LoginActivity extends NewsBaseUIActivity implements View.OnClickListener {

    private NewsBaseFragment mLogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onews_activity_login);
        init();
    }

    private View mBack = null;
    private TextView mTitle;
    private int mFrom = FROM_LIVE_COMMENTS;

    private void init() {
        mBack = findViewById(R.id.common_back);
        mTitle = (TextView) findViewById(R.id.common_title);
        mTitle.setText(R.string.log_in);
        mBack.setOnClickListener(this);

        if (ConflictCommons.isCNVersion()) {
            addOverSeasFragment();
        } else {
            addOverSeasFragment();
        }
        handleIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
    }

    private final static String COMMENT_LOG_TYPE = "comment_login_type";
    private final static String FROM = ":from";

    private void handleIntent() {
        final Intent intent = getIntent();
        if (null != intent) {
            // sstoken 失效的情形
            final int intExtra = intent.getIntExtra(COMMENT_LOG_TYPE, 1);
            mFrom = intent.getIntExtra(FROM, FROM_LIVE_COMMENTS);
            if (intExtra == -1 && null != mLogFragment && null == mLogFragment.getArguments()) {
                LoginService.logout();
                Toast.makeText(getBaseContext(), R.string.log_in_token_failed, Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                bundle.putInt(OverSeasLoginFragment.LOGIN_KEY,OverSeasLoginFragment.LOGIN_OUTOFDATE);
                mLogFragment.setArguments(bundle);
            }
        }
    }

    public int getFrom(){
        return mFrom;
    }

    private void addFragment(Fragment fragment) {
        final FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.login_fragment_container, fragment)
                .commitAllowingStateLoss();
    }

    private void addOverSeasFragment() {
         /* BUILD_CTRL:IF:OU_VERSION_ONLY */
        mLogFragment = new OverSeasLoginFragment();
        addFragment(mLogFragment);
          /* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private final static String ACTION_COMMNENT="com.cmcm.news.login";

    public static final int FROM_LIVE_COMMENTS = 2;
    public static final int FROM_SETTINGS = 3;
    public static final int FROM_DEFAULT_COMMENTS = 1;

    @Override
    protected void onEventInUiThread(ONewsEvent event) {
        super.onEventInUiThread(event);
        if (isFinishing()){
            return;
        }
        if(null != mLogFragment){
            mLogFragment.onEventInUiThread(event);
        }
    }



    public static void startLoginActivity(Context context, int source) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        intent.setAction(ACTION_COMMNENT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(FROM,source);
        intent.putExtra(COMMENT_LOG_TYPE,1);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        final int id = v.getId();
        switch (id) {
            case R.id.common_back:{
                finish();
                break;
            }
        }

    }

    private void startRegisterActivity() {
//        UserRegisterActivity.startUserRegisterActivity(this);
    }

}
