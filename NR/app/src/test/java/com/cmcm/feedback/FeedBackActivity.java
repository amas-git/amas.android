package com.cmcm.feedback;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcm.onews.MainEntry;
import com.cmcm.onews.R;
import com.cmcm.onews.event.ONewsEvent;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.ui.NewsSlideActivity;
import com.cmcm.onews.util.NetworkUtil;


public class FeedBackActivity extends NewsSlideActivity {

    public static final String FROM = "from_type";

    private FeedbackFragment mFragFeedback;

    private ImageView mImgSend;
    private String mEditorName;
    private String mIconUrl;
    private int mFrom = FeedbackFragment.FROM_DEFALUT;

    private final static String PHOTO = FeedbackFragment.URL;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_tag_activity_feedback);
        handleIntent();
        initView();
    }

    private void handleIntent() {
        final Intent intent = getIntent();
        if (null==intent){
            return;
        }
        mEditorName = intent.getStringExtra(FeedbackFragment.NAME);
        mIconUrl = intent.getStringExtra(FeedbackFragment.URL);
        mFrom = intent.getIntExtra(FROM,0);
        L.feedback(mIconUrl + "icon");
    }

    private void initView() {
        ((TextView) findViewById(R.id.common_title)).setText(getResources().getString(R.string.feedback_tag_feedback_by_cleanmaster));
        findViewById(R.id.common_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏输入法
                hideInput();
                finish();
            }
        });
        mImgSend = (ImageView) findViewById(R.id.send_iv);
        mImgSend.setVisibility(View.VISIBLE);

        mFragFeedback = FeedbackFragment.newInstance(mEditorName, mIconUrl, mFrom);
        //发送
        mImgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInput();
                if (!NetworkUtil.isNetworkAvailable(MainEntry.getAppContext())) {
                    Toast.makeText(MainEntry.getAppContext(), R.string.onews__no_network, Toast.LENGTH_LONG).show();
                    return;
                }
                if (mFragFeedback != null) {
                    mFragFeedback.startCommit();
                }
            }
        });
        addFragment(mFragFeedback);
    }

    private void addFragment(Fragment fragment) {
        final FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.container, fragment).commitAllowingStateLoss();
    }


    public static void startFeedBackFromSettings(Context context) {
        if (null == context) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, FeedBackActivity.class);
        intent.putExtra(FROM, FeedbackFragment.FROM_SETTINGS);
        context.startActivity(intent);
    }


    public static void startFeedBackWithPhotos(Context context, Uri uri) {
        if (null == context || uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, FeedBackActivity.class);
        intent.putExtra(FROM, FeedbackFragment.FROM_CONTENT);
        intent.putExtra(PHOTO, uri.getPath());
        context.startActivity(intent);

    }


    public void onClick(View v) {
        switch (v.getId()) {

            default:
                break;
        }
    }


    public void sendImgChanged(String email ,String content){

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(content)){
            mImgSend.setImageResource(R.drawable.feedback_send_def);
        }else{
            mImgSend.setImageResource(R.drawable.feedback_send_press);
        }

    }

    public void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            View currentFocus = getCurrentFocus();
            if (null != currentFocus) {
                IBinder binder = currentFocus.getWindowToken();
                if (null != binder) {
                    imm.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onEventInUiThread(ONewsEvent event) {
        super.onEventInUiThread(event);
        if (mFragFeedback != null) {
            mFragFeedback.onEventInUiThread(event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
