package com.cmcm.onews.util;

import android.content.Context;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by yuanshouhui on 2016/3/14.
 */
public class DateUtils {

    public static long getCurentDate(){
        return getCurrentYear() * 10000 + getCurrentMonth() * 100 + getCurrentMonthDay();
    }

    /**
     * 获取当前年份
     * @return  year(int)
     */
    public static int getCurrentYear() {
        return Calendar.getInstance(Locale.getDefault()).get(Calendar.YEAR);
    }

    /**
     * 获取当前月份
     * @return month(int)
     */
    public static int getCurrentMonth() {
        return Calendar.getInstance(Locale.getDefault()).get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前月份的日期号码
     * @return day_of_month(int)
     */
    public static int getCurrentMonthDay() {
        return Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前的小时数
     * @return hour_of_day(int)
     */
    public static int getCurrentDayHour() {
        return Calendar.getInstance(Locale.getDefault()).get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前的分钟数
     * @return minute(int)
     */
    public static int getCurrentMinute() {
        return Calendar.getInstance(Locale.getDefault()).get(Calendar.MINUTE);
    }


    /**
     * 与现在的时间差值
     *
     * @param pTime 单位毫秒
     * @return String 1秒前
     */
    public static String getShortTimeString(Context pContext, long pTime) {
        String _shortString = null;
     /*   long _now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
        if (pTime < 0)
            return _shortString;
        long _delTime = (_now - pTime) / 1000;
        if (_delTime >= 365 * 24 * 60 * 60 * 2) {
            _shortString = (int) (_delTime / (365 * 24 * 60 * 60)) + " " + pContext.getResources().getString(R.string.news_date_years);
        }else if (_delTime > 365 * 24 * 60 * 60) {
            _shortString = (int) (_delTime / (365 * 24 * 60 * 60)) + " " + pContext.getResources().getString(R.string.news_date_year);
        } else if (_delTime >= 24 * 60 * 60 * 2) {
            _shortString = (int) (_delTime / (24 * 60 * 60)) + " " + pContext.getResources().getString(R.string.news_date_days);
        }else if (_delTime > 24 * 60 * 60) {
            _shortString = (int) (_delTime / (24 * 60 * 60)) + " " + pContext.getResources().getString(R.string.news_date_day);
        } else if (_delTime >= 60 * 60 * 2) {
            _shortString = (int) (_delTime / (60 * 60)) + " " + pContext.getResources().getString(R.string.news_date_hours);
        } else if (_delTime > 60 * 60) {
            _shortString = (int) (_delTime / (60 * 60)) + " " + pContext.getResources().getString(R.string.news_date_hour);
        } else if (_delTime >= 60 * 2) {
            _shortString = (int) (_delTime / (60)) + " " + pContext.getResources().getString(R.string.news_date_minutes);
        }else if (_delTime > 60) {
            _shortString = (int) (_delTime / (60)) + " " + pContext.getResources().getString(R.string.news_date_minute);
        } else if (_delTime > 1) {
            _shortString = _delTime + " " + pContext.getResources().getString(R.string.news_date_seconds);
        } else {
            _shortString = " " + pContext.getResources().getString(R.string.news_date_second);
        }*/
        return _shortString;
    }

    /**
     * 与现在的时间差值
     *
     * @param pTime 单位毫秒
     * @return 时间  毫秒
     */
    public static long getShortTime(long pTime) {
        long _now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
        return (_now - pTime);
    }


    public static String computeDateInterval(long time1, long time2)
    {
        long time = time2 - time1;
        time = time / 1000; // 将毫秒转化为秒

        long second = time % 60; // 秒
        time = time / 60;

        long minute = time % 60; // 分
        time = time / 60;

        long hour = time % 24; // 小时
        time = time / 24;

        long day = time; // 天

        String result = "";

        if (0 != day)
        {
            result += day + " days ";
        }
       /* if (0 != hour)
        {
            result += hour + " hours ";
        }
        if (0 != minute)
        {
            result += minute + " minutes ";
        }
        if (0 != second)
        {
            result += second + " seconds ";
        }
*/
        return result;

    }
}
