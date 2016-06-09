package com.cmcm.onews.util;

import java.util.ArrayList;

import com.cmcm.onews.model.ONewsCity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;


/**
 * Created by cm on 2016/4/13.
 */
public class LocalCityUtils {
    private static final String CITY_CODE = "code";
    private static final String CITY_CCODE = "ccode";
    private static final String CITY_CITY = "city";
    private static final String CITY_GID = "gid";
    private static final String CITY_STATE = "state";
    private static final String CITY_IS_USER_CITY = "isUserCity";

    public static ONewsCity updateRecentLocations(Context context, List<ONewsCity> oNewsCities) {
        if (context != null) {
            ArrayList<ONewsCity> recentLocList = getRecentLocList(context);
            if (recentLocList != null && recentLocList.size() > 0) {
                for (ONewsCity city : recentLocList) {
                    inner:
                    for (ONewsCity all : oNewsCities) {
                        if (city.getCCode().equals(all.getCCode())) {
                            city.setCity(all.getCity());
                            break inner;
                        }
                    }
                }
                saveRecentLocCityList(context, recentLocList);
                if (recentLocList != null && recentLocList.size() > 0) {
                    return recentLocList.get(0);
                }
            }
        }
        return null;
    }

    public static void saveUnChangeONewsCity(Context context, ONewsCity result) {
        if (context != null) {
            if (result != null) {
                JSONObject jsonObject = LocalCityUtils.convertToJSONObject(result);
                SDKConfigManager.getInstanse(context).setNEWS_UNCHANGE_LOCATION(jsonObject == null ? "" : jsonObject.toString());
            } else {
                SDKConfigManager.getInstanse(context).setNEWS_UNCHANGE_LOCATION("");
            }
        }
    }

    public static ONewsCity getUnChangeONewsCity(Context context) {
        if (context != null) {
            String unchangeLocation = SDKConfigManager.getInstanse(context).getNEWS_UNCHANGE_LOCATION();
            if (!TextUtils.isEmpty(unchangeLocation)) {
                try {
                    JSONObject jsonObject = new JSONObject(unchangeLocation);
                    if (jsonObject != null) {
                        ONewsCity city = LocalCityUtils.converToCity(jsonObject);
                        return city;
                    }
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static ArrayList<ONewsCity> getRecentLocList(Context context) {
        if (context != null) {
            String recentLocation = SDKConfigManager.getInstanse(context).getNEWS_RECENT_LOCATION();
            if (!TextUtils.isEmpty(recentLocation)) {
                return convertToCityList(recentLocation);
            }
        }
        return null;
    }

    public static void saveRecentLocCityList(Context context, List<ONewsCity> arrayList) {
        JSONArray jsonArray = convertToJsonArray(arrayList);
        if (jsonArray != null && jsonArray.length() > 0 && context != null) {
            SDKConfigManager.getInstanse(context).setNEWS_RECENT_LOCATION(jsonArray.toString());
        }
    }

    public static List<ONewsCity> getAllCities(Context context) {
        if (context != null) {
            String locations = SDKConfigManager.getInstanse(context).getNEWS_ALL_LOCATION(getNewsLanguage());
            if (!TextUtils.isEmpty(locations)) {
                return convertToCityList(locations);
            }
        }
        return null;
    }

    public static void saveAllLocations(Context context, List<ONewsCity> oNewsCities) {
        JSONArray jsonArray = convertToJsonArray(oNewsCities);
        if (jsonArray != null && jsonArray.length() > 0 && context != null) {
            SDKConfigManager.getInstanse(context).setNEWS_ALL_LOCATION(getNewsLanguage(), jsonArray.toString());
        }
    }


    private static ArrayList<ONewsCity> convertToCityList(String locations) {
        try {
            JSONArray jsonArray = new JSONArray(locations);
            if (jsonArray != null && jsonArray.length() > 0) {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    if (jsonObject != null) {
                        arrayList.add(converToCity(jsonObject));
                    }
                }
                if (arrayList != null && arrayList.size() > 0) {
                    return arrayList;
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    private static JSONObject convertToJSONObject(ONewsCity result) {
        if (result != null) {
            JSONObject jo = new JSONObject();
            try {
                jo.put(CITY_CODE, result.getCode());
                jo.put(CITY_CCODE, result.getCCode());
                jo.put(CITY_CITY, result.getCity());
                jo.put(CITY_GID, result.getGid());
                jo.put(CITY_STATE, result.getState());
                jo.put(CITY_IS_USER_CITY, result.isUserCity());
                return jo;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static JSONArray convertToJsonArray(List<ONewsCity> arrayList) {
        if (arrayList != null && arrayList.size() > 0) {
            JSONArray jsonArray = new JSONArray();
            for (ONewsCity result : arrayList) {
                JSONObject jo = new JSONObject();
                try {
                    jo.put(CITY_CODE, result.getCode());
                    jo.put(CITY_CCODE, result.getCCode());
                    jo.put(CITY_CITY, result.getCity());
                    jo.put(CITY_GID, result.getGid());
                    jo.put(CITY_STATE, result.getState());
                    jo.put(CITY_IS_USER_CITY, result.isUserCity());
                    jsonArray.put(jo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return jsonArray;
        }
        return null;
    }

    private static ONewsCity converToCity(JSONObject jsonObject) {
        ONewsCity city = new ONewsCity();
        city.setCode(jsonObject.optString(CITY_CODE));
        city.setCCode(jsonObject.optString(CITY_CCODE));
        city.setCity(jsonObject.optString(CITY_CITY));
        city.setGid(jsonObject.optString(CITY_GID));
        city.setState(jsonObject.optString(CITY_STATE));
        city.setIsUserCity(jsonObject.optBoolean(CITY_IS_USER_CITY));
        return city;
    }

    private static String getNewsLanguage() {
        String app_lan = com.cmcm.onews.sdk.NewsSdk.INSTAMCE.getONewsLanguage();
        return !TextUtils.isEmpty(app_lan) ? app_lan : com.cmcm.onews.sdk.NewsSdk.APP_LAN;
    }
}
