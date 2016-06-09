package com.cmcm.feedback.service;


import android.text.TextUtils;

import com.cmcm.onews.event.ONewsEvent;

import org.json.JSONException;
import org.json.JSONObject;

public class EvFeedbackResult extends ONewsEvent {

    public String mFeedbackResult;
    public long mUploadTime;
    private boolean mIsOutOfTime = false;

    public EvFeedbackResult(String feedbackResult, long startCommitTime) {
        mFeedbackResult = feedbackResult;
        mUploadTime = System.currentTimeMillis() - startCommitTime;
        mIsOutOfTime = mUploadTime > 40 * 1000;
    }

    public boolean isOutOfTime() {
        return mIsOutOfTime;
    }

    public boolean getFeedbackResult() {
        if (TextUtils.isEmpty(mFeedbackResult)) {
            return false;
        }
        try {
            JSONObject object = new JSONObject(mFeedbackResult);
            final int code = object.optInt("code", -1);
            return code == 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getMsg() {
        if (TextUtils.isEmpty(mFeedbackResult)) {
            return "";
        }
        try {
            JSONObject object = new JSONObject(mFeedbackResult);
            final String code = object.optString("msg", "");
            return code;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


}
