package com.cm.kinfoc;

import android.util.Log;

import java.util.Map.Entry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;


/**
 * 基础统计对象，提供累加，设置等基本操作， 使用者需要在构造对象时指定所有需要上报的key, 而后可以通过acc/inc等操作修改统计数据, 最后可以通过 report()方法提交数据到infoc
 *
 * @author amas
 */
public abstract class BaseTracer {
    public static boolean DEBUG = true;
    protected static final int FALSE = 0;
    protected static final int TRUE = 1;
    private boolean mEnableLog = true;
    private boolean mEnableForceReport = false;


    private boolean mHaveProbabilityCtrl = true;

    String name = null;
    protected ContentValues data = new ContentValues();

    /**
     * 设置表名
     *
     * @param tableName
     */
    protected void setTableName(final String tableName) {
        this.name = tableName;
    }

    /**
     * add by qiuruifeng
     * 设置是否有概率控制
     *
     * @parem haveProbabilityCtrl 是否有概率控制,true为有概率控制,false为没有概率控制
     * 垃圾扫描首扫的用户很少,进行概率控制后首扫的数据就更少,
     * 所以有首扫相关的数据全部上报,非首扫数据还是概率控制的需求,
     * 所以增加这个参数,传入false可以跳开概率控制,谨慎使用该参数
     */
    protected void setHaveProbabilityCtrl(boolean haveProbabilityCtrl) {
        mHaveProbabilityCtrl = haveProbabilityCtrl;
    }

    /**
     * 是否采用强制上报
     */
    protected void setForceReportEnabled() {
        mEnableForceReport = true;
    }

    public BaseTracer(String name) {
        this.name = name;
        _reset();
    }

    protected void set(String key, boolean b) {
        set(key, b ? TRUE : FALSE);
    }

    /**
     * 累加
     *
     * @param key
     * @param inc
     */
    protected void acc(String key, int inc) {
        Integer value = data.getAsInteger(key);
        if (null == value) {
            value = 0;
        }
        data.put(key, (value + inc));
        if (DEBUG) {
            log(String.format("ACC I: %s=%d (+%d)", key, data.getAsInteger(key), inc));
        }
    }

    /**
     * 累加
     *
     * @param key
     * @param inc
     */
    protected void acc(String key, long inc) {
        Long value = data.getAsLong(key);
        if (null == value) {
            value = 0L;
        }
        data.put(key, (value + inc));
        if (DEBUG) {
            log(String.format("ACC L: %s=%d (+%d)", key, data.getAsLong(key), inc));
        }
    }

    /**
     * 自增, 总是至少从0开始
     *
     * @param key
     */
    protected void inc(String key) {
        acc(key, 1);
    }

    /**
     * 设置
     *
     * @param key
     * @param value
     */
    protected void set(String key, int value) {
        data.put(key, value);
        if (DEBUG) {
            log(String.format("SET I: %s=%d", key, data.getAsInteger(key)));
        }
    }

    /**
     * 设置
     *
     * @param key
     * @param value
     */
    protected void set(String key, short value) {
        data.put(key, value);
        if (DEBUG) {
            log(String.format("SET I: %s=%d", key, data.getAsShort(key)));
        }
    }

    /**
     * 设置
     *
     * @param key
     * @param value
     */
    protected void set(String key, byte value) {
        data.put(key, value);
        if (DEBUG) {
            log(String.format("SET I: %s=%d", key, data.getAsByte(key)));
        }
    }

    /**
     * 设置
     *
     * @param key
     * @param value
     */
    protected void set(String key, String value) {
        if (value == null) {
            return;
        }
        if (!TextUtils.isEmpty(value)) {
            value = value.replace("&", "_");
        }
        data.put(key, value);
        if (DEBUG) {
            log(String.format("SET I: %s='%s'", key, value));
        }
    }

    /**
     * 设置
     *
     * @param key
     * @param value
     */
    protected void set(String key, long value) {
        data.put(key, value);
        if (DEBUG) {
            log(String.format("SET L: %s=%d", key, data.getAsLong(key)));
        }
    }

    protected void toKb(String key) {
        long value = getAsLong(key, 0L);
        set(key, K(value));
    }

    protected void log(String msg) {
        if (DEBUG) {
            if (mEnableLog) {
                android.util.Log.d(name == null ? "report" : name, String.format("[%s] --> %s", name, msg));
            }
        }
    }

    /**
     * 字节转换为M
     *
     * @param size
     * @return
     */
    public static int M(final long size) {
        return Math.round(size / (1024 * 1024.0f));
    }

    /**
     * 字节转换为M
     *
     * @param size
     * @return
     */
    public static long K(final long size) {
        return Math.round(size / (1024.0f));
    }

    /**
     * 微秒转化为小时
     *
     * @param msec
     * @return 最小返回1
     */
    public static int HOUR(final long msec) {
        int hour = (int) (msec / (1000L * 60 * 60));
        if (hour < 0) {
            return -1;
        } else if (hour == 0) {
            return 0;
        } else {
            return hour;
        }
    }

    /**
     * 上报前调用,可以在此做数据转换
     */
    protected void onPreReport() {

    }

    /**
     * 开始上报数据
     */
    final public void report() {
        try {
            onPreReport();
        } catch (Exception e) {
            e.printStackTrace();
            log("onPreReport() 出现异常, 请检查 : " + e.getLocalizedMessage());
        }

        String report = toInfocString();
        if (isValidate()) {
            if (DEBUG) {
                if (!KInfocClient.getInstance().isValidateData(name, report)) {
                    log(String.format("天王盖地虎！埋点不靠谱！赶紧找研发! 不找二百五:  '%s' 上报的数据格式不对，可能是以下情况造成的", name));
                    log("   1. assets/kfmt.dat文件中没有此表的定义");
                    log("   2. 上报数据的字段与assets/kfmt.dat文件中的定义不符");
                    log("原始数据: " + "'" + report + "'");
                    log(String.format("查看定义: 'http://bi.kingsoft.net:81/concern/listpage?product_id=11&filter_code=%s'", name));
                }

                try {
                    StringBuilder builder = new StringBuilder();
                    if (data.valueSet() == null) {
                        return;
                    }

                    for (Entry<String, Object> item : data.valueSet()) {
                        String key = item.getKey();     // getting key
                        Object value = item.getValue(); // getting value
                        builder.append("\n").append("             +  ").append(String.format("%15s = %-15s", key, "" + value));
                    }
                    log("SEND" + builder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (mEnableForceReport) {
                KInfocClientAssist.getInstance().forceReportData(name, report);
            } else {
                if (mHaveProbabilityCtrl) {
                    KInfocClientAssist.getInstance().reportData(name, report);
                } else {
                    //KInfocClientAssist.getInstance().reportDataWithProbabilityCtrl(name, report, mHaveProbabilityCtrl);
                }
            }
            _reset();
            onPostReport();
            log("RESET");
        } else {
            log("\n  DROP : " + report + "\n\n");
        }
    }

    protected void onPostReport() {

    }

    /**
     * 数据合法性检查,返回true则完成上报, false丢弃数据
     *
     * @return
     */
    public boolean isValidate() {
        return true;
    }

    private void _reset() {
        mEnableLog = false;
        disableLog();
        reset();
        enableLog();
    }

    /**
     * report()后自动调用此函数
     */
    public void reset() {

    }

    protected String toInfocString() {
        if (data.valueSet() == null) {
            return "";
        }
        ArrayList<String> chunk = new ArrayList<String>();
        for (Entry<String, Object> item : data.valueSet()) {
            String key = item.getKey();     // getting key
            Object value = item.getValue(); // getting value
            chunk.add(key + "=" + value);
        }
        return TextUtils.join("&", chunk);
    }

    @Override
    public String toString() {
        return String.valueOf(data);
    }

    protected void disableLog() {
        mEnableLog = false;
    }

    protected void enableLog() {
        mEnableLog = true;
    }

    /**
     * 獲取系統屬性
     *
     * @param key 鍵值
     * @return 如屬性不存在或取值為空則返回'@null'
     */
    public static String SP1(String key) {
        return SP2(key, "@null");
    }

    /**
     * 獲取系統屬性
     *
     * @param key  鍵值
     * @param fail 失敗或為空時返回此值
     * @return
     */
    public static String SP2(String key, String fail) {
        String value = SystemProperties.get(key);
        if (TextUtils.isEmpty(value)) {
            value = android.os.Build.MODEL;
        }
        return !TextUtils.isEmpty(value) ? value : fail;
    }


    /**
     * NOTICE: any '&' will be replaced with '_' for infoc DO NOT supporting & escaped
     * ro.product.brand
     *
     * @return
     */
    public static String brand() {
        return SP2("ro.product.brand", "").replace("&", "_");
    }

    /**
     * NOTICE: any '&' will be replaced with '_' for infoc DO NOT supporting & escaped
     * ro.product.model
     *
     * @return
     */
    public static String model() {
        return SP2("ro.product.model", "").replace("&", "_");
    }

    @SuppressLint("NewApi")
    public static String SERIAL() {
        return (Build.VERSION.SDK_INT >= 9) ? Build.SERIAL : "";
    }

/*	final public void report(boolean force) {
		Log.i("report", "report enter");
	}*/


    public long getAsLong(String key, long fail) {
        Long v = data.getAsLong(key);
        log(String.format(key + " = " + v));
        return v == null ? fail : v;
    }

    public static String infocEscape(String data) {
        return TextUtils.isEmpty(data) ? "" : data.replace("&", "_");
    }

    public void clear() {
        data.clear();
    }

/*	public int getAsInteger(String key, int fail) {
		Integer v = data.getAsInteger(key);
		log(String.format(key + " = " + v));
		return v == null ? fail : v;
	}*/

/*	public BaseTracer setV(String key, short value) {
		set(key, value);
		return this;
	}*/

/*	public BaseTracer setV(String key, int value) {
		set(key, value);
		return this;
	}*/

/*	public BaseTracer setV(String key, boolean b) {
		set(key, b ? TRUE : FALSE);
		return this;
	}

	public BaseTracer setV(String key, long value) {
		set(key, value);
		return this;
	}

	public BaseTracer setV(String key, String value) {
		set(key, value);
		return this;
	}*/

}
