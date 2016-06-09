package com.cmcm.onews.util.push;

import org.json.JSONObject;

public interface iPushCallBackProvider {
     void handlePush(JSONObject jsonObject);
}
