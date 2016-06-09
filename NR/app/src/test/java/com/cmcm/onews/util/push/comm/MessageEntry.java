/**
 * 
 */
package com.cmcm.onews.util.push.comm;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * @author dreamer
 *
 */
public class MessageEntry {
    
    private static final String TAG = "MessageEntry";
    
    private static final String KEY_TASK_ID = "pushid";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_SMALLICON_URL = "icon_url";
    private static final String KEY_BIGICON_URL = "bigicon_url";
    private static final String KEY_PRIORITY = "priority";
    private static final String KEY_SOUND = "sound";
    private static final String KEY_MAX_NOTI_NUM = "max_notification_number";
    private static final String KEY_IS_STORE_IN_DB = "is_store_in_db";
    private static final String KEY_IS_SHOW_IN_STATUSBAR = "is_show_in_statusbar";
    private static final String KEY_COMBINE = "combine";
    private static final String KEY_EXTRA = "extra";
    private static final String KEY_POPTIME = "poptime";
    private static final String KEY_EXPIRED_TIME = "expiredtime";
    private static final String KEY_NOTIFY_ID = "notify_id";
    private static final String KEY_IS_REPORT = "is_report";
    private static final String KEY_IS_SHOWN_ON_LOCK_SCREEN = "is_shown_on_lockscreen";
    
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm";

    public final static int NOTIFY_ID_INVALID = -1;
    
    public final static int DEFAULT_NOTI_NUM = 3;
    
    /**
     * 收到的推送消息类型，当新增消息类型时，需要更新 {@link MSGTYPE#}
     * @author dreamer
     *
     */
    public static class MSGTYPE {
        /**
         * 消息类型最小 ID
         */
        private static final int MIN_TYPE_ID = 1;
        /**
         * 视频剧集，包括 电视剧、综艺、动漫等
         */
        public static final int MSG_TYPE_SERIES = MIN_TYPE_ID;
        /**
         * 新闻
         */
        public static final int MSG_TYPE_NEWS = MIN_TYPE_ID + 1;
        /**
         * 升级
         */
        public static final int MSG_TYPE_UPGRADE = MIN_TYPE_ID + 2;
        /**
         * 频道，如看图
         */
        public static final int MSG_TYPE_TOPIC = MIN_TYPE_ID + 3;
        /**
         * 天气预警
         */
        public static final int MSG_TYPE_WEATHER_WARN = MIN_TYPE_ID + 4;
        /**
         * 命令类通知，此类型不弹出通知
         */
        public static final int MSG_TYPE_COMMAND = MIN_TYPE_ID + 5;
        /**
         * 抢票
         */
        public static final int MSG_TYPE_QIANGPIAO = MIN_TYPE_ID + 6;
        /**
         * 视频
         */
        public static final int MSG_TYPE_VIDEO = MIN_TYPE_ID + 7;
        /**
         * 常驻通知栏
         */
        public static final int MSG_TYPE_NOTIFICATIONBAR = MIN_TYPE_ID + 8;
        /**
         * 删除缓存
         */
        public static final int MSG_TYPE_RESCUE = MIN_TYPE_ID + 9;
        
        /**
         * 修改常驻通知栏搜索图标跳转的url
         */
        public static final int MSG_TYPE_CHANGE_SEARCH_URL = MIN_TYPE_ID + 10;

        /**
         * 通知主进程显示游戏气泡
         */
        public static final int MSG_TYPE_GAME_BUBBLE=MIN_TYPE_ID+11;

        /**
         * 判断消息类型是否合法
         */
        public static boolean isvalidType(int type) {
            return type >= MIN_TYPE_ID && MIN_TYPE_ID <= MSG_TYPE_RESCUE;
        }
    }
    
    /**
     * 消息合并行为
     * @author dreamer
     *
     */
    public static class COMBINETYPE {
        /**
         * 是否允许合并： 默认, same as {@link #MERGE}
         */
        public static final int DEFAULT = 0;
        
        /**
         * 是否允许合并： 合并（只合并允许合并的）
         */
        public static final int MERGE = 1;
        
        /**
         * 是否允许合并： 顶替
         */
        public static final int REPLACE = 2;
        
        /**
         * 是否允许合并： 不合并, 创建一条新的（暂时不用）
         */
        public static final int NEW = 3;
    }
    public static class CommonMessageType {
        
        public static final int BASE_ID = 1;
        
        //消息
        public static final int NEWS = BASE_ID;
        
        //世界杯
        public static final int WORLD_CUP = BASE_ID + 1;
        
        //世界杯多条文字显示
        public static final int MULTI_TEXT = BASE_ID + 2;
        
    }
    
    public static class CommandMessageType {
        
        public static final int BASE = 1;
        
        /**应用程序升级*/
        public static final int APP_UPDATE = BASE;
        
        /**库文件更新*/
        public static final int LIB_UPDATE = BASE + 1;
        
        /**卡片数据更新*/
        public static final int CARD_UPDATE = BASE + 2;
        
        /**天气数据更新*/
        public static final int WEATHER_UPDATE = BASE + 3;
        
        /**插件更新*/
        public static final int PLUGIN_UPDATE = BASE + 4;
    }
    /**
     * 消息的有效状态
     * @author dreamer
     *
     */
    public static enum AvailableState {
        /**
         * 激活状态（当前即在有效范围时间内）
         */
        ACTIVE,
        /**
         * 休眠状态(存在下一个有效时间点)
         */
        HIBERNATE,
        /**
         * 过期状态（不再有效）
         */
        EXPIRED
    }
    
    /**
     * 有效时间区间
     * @author dreamer
     *
     */
    public static class ValidTimeFragment {
        public String startTime;
        public String endTime;
        
        private Calendar mStartTime;
        private Calendar mEndTime;
        
        public ValidTimeFragment(String startTime, String endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
            
            initialize();
        }
        
        @Override
        public String toString() {
            return String.format("%s-%s", startTime, endTime);
        }
        
        /**
         * 判断指定时间是否在有效时间区间内
         * 
         * @param now 需要判断的时间 
         * @return 
         *      < 0 如果比该区间时间早
         *      = 0 在时间区间内
         *      > 0 如果比该区间时间晚
         */
        public int inAvailableTime(Calendar now) {
            if (mStartTime == null || mEndTime == null) return 0;
            
            if (now.compareTo(mStartTime) >= 0 && now.compareTo(mEndTime) <= 0) {
                return 0;
            } else if (now.compareTo(mStartTime) < 0) {
                return -1;
            } else if (now.compareTo(mEndTime) > 0) {
                return 1;
            }
            
            return 0;
            
        }
        
        private int[] splitTime(String time) throws NumberFormatException {
            if (TextUtils.isEmpty(time)) return null;
            
            String[] hour_minute_str = time.split(":");
            return new int[] { Integer.parseInt(hour_minute_str[0]), Integer.parseInt(hour_minute_str[1]) };
        }
        
        private void initialize() {
            mStartTime = Calendar.getInstance();
            mEndTime = Calendar.getInstance();
            try {
                int[] start_hour_minute = splitTime(startTime);
                mStartTime.set(Calendar.HOUR_OF_DAY, start_hour_minute[0]);
                mStartTime.set(Calendar.MINUTE, start_hour_minute[1]);
                
                int[] end_hour_minute = splitTime(endTime);
                mEndTime.set(Calendar.HOUR_OF_DAY, end_hour_minute[0]);
                mEndTime.set(Calendar.MINUTE, end_hour_minute[1]);
                
                if (compare(start_hour_minute, end_hour_minute) > 0) {
                    // 隔天
                    mEndTime.add(Calendar.DAY_OF_MONTH, 1);
                }
            } catch (Exception e) {
                mStartTime = mEndTime = null;
            }
        }
        
        private int compare(int[] time1, int[] time2) {
            if (time1[0] < time2[0]) {
                return -1;
            } else if (time1[0] > time2[0]) {
                return 1;
            } else {
                if (time1[1] > time2[1]) {
                    return 1;
                } else if (time1[1] < time2[1]) {
                    return -1;
                } 
            }
            return 0;
        }
    }
    
    /**
     * 有效时间区间段集合
     * @author dreamer
     *
     */
    public static class ValidTimeFragments implements Iterator<ValidTimeFragment> {
        
        private int mIndex = 0; 
        private String[] mRawItems;
        private ValidTimeFragment[] mItems;
        private boolean mIsValid;

        public ValidTimeFragments(String items_str) {
            mIsValid = true;
            if (!TextUtils.isEmpty(items_str)) {
                int i = 0;
                String[] items = items_str.split(",");
                mItems = new ValidTimeFragment[items.length];
                mRawItems = new String[items.length * 2];
                for (String item_str : items) {
                    String[] item = item_str.split("-");
                    if (item.length == 2) {
                        mRawItems[i] = item[0];
                        mRawItems[i + 1] = item[1];
                    } else {
                        mIsValid = false;
                        break;
                    }
                    i += 2;
                }
            }
        }
        
        @Override
        public boolean hasNext() {
            return mRawItems != null && 
                    mRawItems.length > 1 && 
                    (mIndex + 1) < mRawItems.length;
        }

        @Override
        public ValidTimeFragment next() {
            ValidTimeFragment time = null;
            if (hasNext()) {
                int index = mIndex / 2;
                if (mItems[index] == null) {
                    String startTime = mRawItems[mIndex];
                    String endTime = mRawItems[mIndex + 1];
                    time = new ValidTimeFragment(startTime, endTime);
                    mItems[index] = time;
                } else {
                    time = mItems[index];
                }
                mIndex += 2;
            }
            return time;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("not support remove");
        }
        
        @Override
        public String toString() {
            if (mRawItems == null || mRawItems.length == 0) return "";
            
            StringBuffer sBuff = new StringBuffer();
            for (int i = 0; i < mRawItems.length; i += 2) {
                sBuff.append(mRawItems[i]).append("-").append(mRawItems[i + 1]);
                sBuff.append(",");
            }
            
            return sBuff.substring(0, sBuff.length() - 1);
        }
        
//        public void reset() {
//            mIndex = 0;
//        }
        
        public AvailableState inAvailableArea() {
            // 指定的时间区间格式错误，认为已过期
            if (!mIsValid) return AvailableState.EXPIRED;
            
            // 没有指定时间区间
            if (mRawItems == null || mRawItems.length == 0) return AvailableState.ACTIVE;
            
            AvailableState state = AvailableState.ACTIVE;
            int index = 0;
            int oldIndex = mIndex;
            boolean found = false;
            mIndex = 0;
            Calendar now = Calendar.getInstance();
            while (hasNext()) {
                ValidTimeFragment time = next();
                int available = time.inAvailableTime(now);
                if (available == 0) {
                    state = AvailableState.ACTIVE;
                    found = true;
                    break;
                } else if (available < 0 && index == 0) {
                    // 小于最小时间有效值
                    state = AvailableState.HIBERNATE;
                    found = true;
                    break;
                } else if (available > 0 && (index + 1) == mRawItems.length / 2) {
                    // 过了最大时间有效值
                    state = AvailableState.HIBERNATE;
                    found = true;
                    break;
                }
                index ++;
            }
            
            mIndex = oldIndex;
            if (!found) {
                state = AvailableState.HIBERNATE;
            }
            return state;
        }
    }
    
    /****************************** begin *****************************/
    private String mTaskId;
    /** @see MSGTYPE */
    private int mType;
    private String mTitle;
    private String mContent;
    private String mSmallIconUrl;
    private String mBigIconUrl;
    private int mPriority;
    private int mSound; //是否强制开启声音， 0：执行原来的逻辑，1:强制播放声音
    private int mMaxNotiNum;
    private boolean mIsShownOnLockScreen = false;
    private boolean mIsStoreInDB = true;
    private int mIsShowInSB = 1;
    /** @see COMBINETYPE */
    private int mCombineType;
    private ValidTimeFragments mTimeFragments;
    private int mNotifyId = -1;
    private int mIsReport = 1;
    /**
     * 消息过期时间
     */
    private Date mExpiredTime;
    
    private Object mExtraObj;
    
    public static JSONObject writeToJSONObject(MessageEntry entry) {
        JSONObject json = null;
        if (entry != null && entry.isValid()) {
            json = new JSONObject();
            try {
                json.put(KEY_TASK_ID, entry.mTaskId);
                json.put(KEY_TYPE, entry.mType);
                json.put(KEY_TITLE, entry.mTitle);
                json.put(KEY_CONTENT, entry.mContent);
                json.put(KEY_SMALLICON_URL, entry.mSmallIconUrl);
                json.put(KEY_BIGICON_URL, entry.mBigIconUrl);
                json.put(KEY_PRIORITY,  entry.mPriority);
                json.put(KEY_SOUND, entry.mSound);
                json.put(KEY_MAX_NOTI_NUM, entry.mMaxNotiNum);
                json.put(KEY_IS_STORE_IN_DB, entry.mIsStoreInDB);
                json.put(KEY_IS_SHOW_IN_STATUSBAR, entry.mIsShowInSB);
                json.put(KEY_IS_REPORT, entry.mIsReport);
                json.put(KEY_COMBINE, entry.mCombineType);
                if (entry.mTimeFragments == null) {
                    json.put(KEY_POPTIME, "");
                } else {
                    json.put(KEY_POPTIME, entry.mTimeFragments.toString());
                }
                
                String expired_time_str = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT);
                    expired_time_str = sdf.format(entry.mExpiredTime);
                } catch (Exception e) {
                    expired_time_str = "";
                }
                json.put(KEY_NOTIFY_ID, entry.mNotifyId);
                json.put(KEY_EXPIRED_TIME, expired_time_str);
                
                json.put(KEY_EXTRA, entry.mExtraObj);
                json.put(KEY_IS_SHOWN_ON_LOCK_SCREEN, entry.mIsShownOnLockScreen? 1 : 0);
            } catch (JSONException e) {
//                KLog.e(TAG, "JSONException", e);
            }
        }
        return json;
    }
    
    public static MessageEntry readFromJSONObject(JSONObject json) {
        MessageEntry entry = new MessageEntry();
        
        if (json != null && json.length() > 0) {
            entry.mTaskId = json.optString(KEY_TASK_ID);
            entry.mType = json.optInt(KEY_TYPE, -1);
            entry.mTitle = json.optString(KEY_TITLE);
            entry.mContent = json.optString(KEY_CONTENT);
            entry.mSmallIconUrl = json.optString(KEY_SMALLICON_URL);
            entry.mBigIconUrl = json.optString(KEY_BIGICON_URL);
            entry.mPriority = json.optInt(KEY_PRIORITY, 0);
            entry.mSound = json.optInt(KEY_SOUND, 0);
            entry.mMaxNotiNum = json.optInt(KEY_MAX_NOTI_NUM, DEFAULT_NOTI_NUM);
            entry.mIsStoreInDB = json.optBoolean(KEY_IS_STORE_IN_DB, true);
            entry.mIsShowInSB = json.optInt(KEY_IS_SHOW_IN_STATUSBAR, 1);
            entry.mCombineType = json.optInt(KEY_COMBINE);  // combine
            entry.mIsReport = json.optInt(KEY_IS_REPORT, 1);
            int flagIsShownOnLockScreen = json.optInt(KEY_IS_SHOWN_ON_LOCK_SCREEN, 0);
            
            if (flagIsShownOnLockScreen == 1) {
                entry.mIsShownOnLockScreen = true;
            } else if (flagIsShownOnLockScreen == 0) {
                entry.mIsShownOnLockScreen = false;
            }
            
            String availableTimeArea = json.optString(KEY_POPTIME);
            if (!TextUtils.isEmpty(availableTimeArea)) {
                entry.mTimeFragments = new ValidTimeFragments(availableTimeArea);
            }

            entry.mNotifyId = json.optInt(KEY_NOTIFY_ID, -1);
            String expiredDate_str = json.optString(KEY_EXPIRED_TIME);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT);
                entry.mExpiredTime = sdf.parse(expiredDate_str);
            } catch (Exception e) {
                Calendar defaultExpired = Calendar.getInstance();
                defaultExpired.add(Calendar.DAY_OF_MONTH, 1);   // 没指定或者格式错误，有效期顺延一天
                entry.mExpiredTime = defaultExpired.getTime();
            } 

            if (!json.isNull(KEY_EXTRA)) {
                entry.mExtraObj = json.opt(KEY_EXTRA);
            }
        }
        
        MessageEntry msg = null;
        switch (entry.getType()) {
//            case MessageEntry.MSGTYPE.MSG_TYPE_SERIES: {
//                msg = new SeriesMessage(entry);
//                break;
//            }
//            case MessageEntry.MSGTYPE.MSG_TYPE_QIANGPIAO: {
//                // 不受消息开关影响
//                msg = new QiangPiaoMessage(entry);
//                break;
//            }
//            //FIXME fqc 新闻和小米推送在这里处理逻辑相同 待修改
//            case MessageEntry.MSGTYPE.MSG_TYPE_NEWS: {
//                msg = new NewsMessage(entry);
//                break;
//            }
//
//            case MessageEntry.MSGTYPE.MSG_TYPE_TOPIC: {
//                msg = new TopicMessage(entry);
//                break;
//            }
//
//            case MessageEntry.MSGTYPE.MSG_TYPE_UPGRADE: {
//                msg = new UpgradeMessage(entry);
//                break;
//            }
//
//            case MessageEntry.MSGTYPE.MSG_TYPE_WEATHER_WARN: {
//                msg = new WeatherWarnMessage(entry);
//                break;
//            }
//
//            case MessageEntry.MSGTYPE.MSG_TYPE_COMMAND: {
//                msg = new CommandMessage(entry);
//                break;
//            }
//
//            case MessageEntry.MSGTYPE.MSG_TYPE_VIDEO: {
//                msg = new VideoMessage(entry);
//                break;
//            }
//
//            case MessageEntry.MSGTYPE.MSG_TYPE_NOTIFICATIONBAR: {
//                msg = new ResidentNotificationBarMessage(entry);
//                break;
//            }
//
//            case MessageEntry.MSGTYPE.MSG_TYPE_CHANGE_SEARCH_URL: {
//                msg = new ChangeSearchUrlMessage(entry);
//                break;
//            }
//
//            case MessageEntry.MSGTYPE.MSG_TYPE_RESCUE:{
//                msg = new RescueMessage(entry);
//                break;
//            }
            case MSGTYPE.MSG_TYPE_GAME_BUBBLE:{
//                msg =new GameBubbleMessage(entry);
                break;
            }
        }

        return msg;
    }

    /**
     * 将字符串对象解析为一个 MessageEntry 对象，永远不会为空
     * @param s 可转换为 MessageEntry 对象的 String
     * @return
     *  return {@link MessageEntry} instance, can use {@link #isValid()} to judge this instance is valid
     */
    public static MessageEntry readFromString(String s) {
        JSONObject json = JSONParser.parseFromString(s);
        return readFromJSONObject(json);
    }

    protected MessageEntry() {
        mType = -1;
    }

    protected MessageEntry(MessageEntry e) {
        this.mTaskId = e.mTaskId;
        this.mType = e.mType;
        this.mTitle = e.mTitle;
        this.mContent = e.mContent;
        this.mSmallIconUrl = e.mSmallIconUrl;
        this.mBigIconUrl = e.mBigIconUrl;
        this.mPriority = e.mPriority;
        this.mSound = e.mSound;
        this.mMaxNotiNum = e.mMaxNotiNum;
        this.mIsStoreInDB = e.mIsStoreInDB;
        this.mIsShowInSB = e.mIsShowInSB;
        this.mCombineType = e.mCombineType;
        this.mIsReport = e.mIsReport;
        this.mTimeFragments = e.mTimeFragments;
        this.mNotifyId = e.mNotifyId;
        this.mExpiredTime = e.mExpiredTime;
        this.mExtraObj = e.mExtraObj;
        this.mIsShownOnLockScreen = e.mIsShownOnLockScreen;

        parseExtra(mExtraObj);
    }

    /**
     * 消息 ID (来自于服务端下发)
     * @return
     */
    public String getTaskId() {
        return mTaskId;
    }

    /**
     * 消息类型
     * @return {@link MSGTYPE}
     */
    public int getType() {
        return mType;
    }

    /**
     * 标题
     * @return 返回 标题
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * 消息体
     * @return 返回 消息体
     */
    public String getContent() {
        return mContent;
    }

    /**
     * 画报url
     * @return 返回 通知栏画报url
     */
    public String getSmallIconUrl() {
        return mSmallIconUrl;
    }

    /**
     * 得到大海报的url
     */
    public String getBigIconUrl() {
        return mBigIconUrl;
    }

    /**
     * 判断是否是大海报
     */
    public boolean getIsBigPic() {
        return !TextUtils.isEmpty(mBigIconUrl);
    }

    /**
     * 获取通知栏显示时的优先级
     */
    public int getPriority() {
        return mPriority;
    }
    /**
     * 是否强制播放声音
     */
    public int getSound() {
        return mSound;
    }
    /**
     * 通知栏中显示的最大数目
     */
    public int getMaxNotiNum() {
        return mMaxNotiNum >= 0 ? mMaxNotiNum : 0;
    }
    /**
     * 是否存储到数据库
     * @return
     */
    public boolean getIsStoreInDB() {
        return mIsStoreInDB;
    }

    /**
     * 是否在通知栏显示
     * @return
     */
    public boolean getIsShowInSB() {
        return mIsShowInSB > 0;
    }
    /**
     * 设置是否在通知栏显示
     * @param isShow
     */
    public void setIsShowInSB(boolean isShow) {
        mIsShowInSB = isShow ? 1:0;
    }
    /**
     * 合并方式
     * @return {@link COMBINETYPE}
     */
    public int getCombineType() {
        if (mCombineType == COMBINETYPE.DEFAULT) {
            mCombineType = COMBINETYPE.MERGE;
        }
        return mCombineType;
    }
    /**
     * 接收到消息后是否上报到达
     * @return
     */
    public boolean getIsReport() {
        return mIsReport > 0;
    }
    /**
     * 设置是否在通知栏显示
     * @param isReport
     */
    public void setIsReport(boolean isReport) {
        mIsReport = isReport ? 1:0;
    }

    /**
    /**
     * 此推送消息的有效时间段（在此时间段内才显示到通知栏中，如果没有，则任何时候收到都为有效）
     * @return
     */
    public ValidTimeFragments getValidTimeFragments() {
        return mTimeFragments;
    }

    /**
     * 获取接收时间截
     * @return
     */
    public Date getExpiredTime() {
        return mExpiredTime;
    }

    public void setNotifyId(int notifyId) {

        mNotifyId = notifyId;

    }

    public int getNotifyId() {

        return mNotifyId;

    }

    public boolean getIsShownOnLockScreen() {
        return mIsShownOnLockScreen;
    }
    /**
     * 返回该消息是否有效
     * @return
     */
    public boolean isValid() {
        return MSGTYPE.isvalidType(mType) && mContent != null && !mContent.isEmpty();
    }
    
    public boolean similar(MessageEntry entry) {
        if (entry == null) return false;
        
        return entry.getType() == this.getType();   //&& entry.getCombineType() == this.getCombineType(); ，by caisenchuan
    }
    
    /**
     * 判断消息在当前时间的状态 {@link AvailableState}
     * @return
     */
    public AvailableState inAvailableArea() {
        if (mTimeFragments == null) {
            return AvailableState.ACTIVE;
        }
        
        Calendar now = Calendar.getInstance();
        
        Calendar expiredTime = Calendar.getInstance();
        expiredTime.setTime(mExpiredTime);

        int result = now.compareTo(expiredTime);

        if (result > 0) return AvailableState.EXPIRED;
        
        return mTimeFragments.inAvailableArea();
    }
    
    /**
     * 解析 extra 字段，子类需要重载此方法 
     * @param json
     */
    protected void parseExtra(Object json) {
        // implement this in subclass
    }
}
