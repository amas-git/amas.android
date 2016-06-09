package com.cmcm.onews.util.push.comm;


import com.cmcm.onews.C;
import com.cmcm.onews.util.UIConfigManager;

/**
 * Created by cm on 2015/11/27.
 */
public class NotifyHelper {

    private static NotifyHelper sInstance = null;
    /** 通知栏显示通知最大条数，超过30显示最新的30条*/
    private static int NOTIFY_ID_LIMITS = 30;
    private static final int NO_FOUND = -1;

    private String[] mNotifyTable;
    private int mIdx;
    private int mLimit;

    public static NotifyHelper getInstance() {
        if (null == sInstance) {
            synchronized (NotifyHelper.class) {
                if (null == sInstance) {
                    sInstance = new NotifyHelper();
                }
            }
        }
        return sInstance;
    }

    private NotifyHelper() {
        initONewsGCM_NotifyHelper();
    }

    private void initONewsGCM_NotifyHelper() {
        mIdx   = UIConfigManager.getInstanse(C.getAppContext()).getNEWS_GCM_NOTIFY_ID_TABLE_IDX();
        mLimit =  UIConfigManager.getInstanse(C.getAppContext()).getNEWS_GCM_NOTIFY_ID_TABLE_LIMIT();
        if (0 == mLimit) {
            mLimit = NOTIFY_ID_LIMITS;
        }

        if (mIdx > (mLimit -1) ) {
            mIdx = (mLimit -1);
        }

        String map = UIConfigManager.getInstanse(C.getAppContext()).getNEWS_GCM_NOTIFY_ID_TABLE();
        String[] mapArr = map.split(",");
        mNotifyTable = new String[mLimit];
        for (int idx = 0; idx < mapArr.length; idx++) {
            mNotifyTable[idx] = mapArr[idx];
        }
    }

    public String setNotifyLimit(int limit) {
        StringBuilder removeNotifyList = new StringBuilder();
        // if limit reduce, reset pushID table
        if (mLimit != limit) {
            String[] tmp = new String[limit];
            int tmp_idx = limit -1;
            for(int idx = 0; idx < mNotifyTable.length; idx++){
                if (tmp_idx >= 0) {
                    tmp[tmp_idx] = mNotifyTable[(mIdx+mLimit-idx) % mLimit];
                } else {
                    removeNotifyList.append((mIdx+mLimit-idx) % mLimit).append(",");
                }
                tmp_idx--;
            }
            mNotifyTable = tmp;
        }
        mLimit = limit;
        mIdx = (mLimit -1);
        UIConfigManager.getInstanse(C.getAppContext()).setNEWS_GCM_NOTIFY_ID_TABLE_IDX(mIdx);
        UIConfigManager.getInstanse(C.getAppContext()).setNEWS_GCM_NOTIFY_ID_TABLE_LIMIT(mLimit);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mNotifyTable.length; i++) {
            sb.append(mNotifyTable[i]).append(",");
        }
        UIConfigManager.getInstanse(C.getAppContext()).setNEWS_GCM_NOTIFY_ID_TABLE(sb.toString());


        return removeNotifyList.toString();
    }

    public int findNotifyID(String recall_pushId) {
        int position = NO_FOUND;
        for (int idx = 0; idx < (mNotifyTable.length -1); idx++) {
            if (recall_pushId.equals(mNotifyTable[idx])) {
                position = idx;
                return position;
            }
        }
        return position;
    }

    public int getNewNotifyId(String pushId) {
        mIdx = (mIdx+1) % mLimit;
        mNotifyTable[mIdx] = pushId;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (mNotifyTable.length -1); i++) {
            sb.append(mNotifyTable[i]).append(",");
        }
        UIConfigManager.getInstanse(C.getAppContext()).setNEWS_GCM_NOTIFY_ID_TABLE(sb.toString());
        UIConfigManager.getInstanse(C.getAppContext()).setNEWS_GCM_NOTIFY_ID_TABLE_IDX(mIdx);
        return mIdx;
    }
}
