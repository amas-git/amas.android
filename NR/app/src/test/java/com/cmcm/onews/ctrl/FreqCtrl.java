package com.cmcm.onews.ctrl;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by amas on 11/20/15.
 */
public class FreqCtrl {
    public class FreqCtrlTask {
        String id = "";
        /**
         * 执行间隔时间
         */
        long interval = 0;


        /**
         * 最后一次执行成功的时间
         */
        long last_success = 0;


        /**
         * 执行成功次数
         */
        int success_times = 0;

        /**
         * 回合数
         */
        int success_round = 0;

        public void fromJson(String json) {
            if (TextUtils.isEmpty(json)) {
                return;
            }

            try {
                JSONObject o = new JSONObject(json);
                id = o.getString("id");
                interval = o.getLong("interval");
                success_times = o.getInt("success_times");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String toJson() {
            try {
                JSONObject o = new JSONObject();
                o.put("id", id);
                o.put("interval", interval);
                o.put("success_times", success_times);
                return o.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void run() {

        }
    }
}
