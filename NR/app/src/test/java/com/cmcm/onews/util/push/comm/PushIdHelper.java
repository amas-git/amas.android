package com.cmcm.onews.util.push.comm;

import android.text.TextUtils;

import com.cmcm.onews.C;
import com.cmcm.onews.NewsL;
import com.cmcm.onews.util.UIConfigManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by yuanshouhui on 2015/11/21.
 */
public class PushIdHelper {

    public static boolean isExistPushId(String pushid){

        HashSet<String> set = new HashSet<>();

        set.addAll(getExistedPushIds());


        boolean isExisted = set.contains(pushid.trim());

        NewsL.push(" NEW PUSH: " + pushid + " EXISTED:" + isExisted);
        return isExisted;
    }

    private static  ArrayList<String> getExistedPushIds() {
        String data = UIConfigManager.getInstanse(C.getAppContext()).getNEWS_GCM_PUSH_ID();
        ArrayList<String> xs = new ArrayList<>();
        String[] array = data.split(",");
        if(xs == null) {
            return xs;
        }

        for(String x : array) {
            xs.add(x);
        }
        return xs;
    }

    private static int PUSH_ID_LIMITS = 30;
    public static void pushIdSave(String pushid){
        if(TextUtils.isEmpty(pushid)){
            return;
        }

        ArrayList<String> xs = getExistedPushIds();

        xs.add(0, pushid.trim());

        List<String> rs = xs;
        if(xs.size() > PUSH_ID_LIMITS) {
            rs = xs.subList(0, PUSH_ID_LIMITS);
        }

        if(rs.isEmpty()) {
            return;
        }
        NewsL.push(" PUSHED SETS: " + rs);
        UIConfigManager.getInstanse(C.getAppContext()).setNEWS_GCM_PUSH_ID(TextUtils.join(",", rs));
    }

}
