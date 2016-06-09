package com.cm.kinfoc;

import android.content.Context;
import android.util.Log;

import com.cm.kinfoc.api.MMM;
import com.cm.kinfoc.base.InfocCommonBase;
import com.cm.kinfoc.base.KHttpsPoster;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class KInfocBatchReporter {
	private static final int MAX_BATCH_BUFFER_LENGTH  = 1072;
	public static final int REPORT_RESULT_FAIL_NETWORK = -1;
	public static final int REPORT_RESULT_FAIL_DATA = -2;
	
	private static final String BATCHTABLENAME = "batchdata_table_";
	private KHttpPoster mPoster = new KHttpPoster();
	private KHttpsPoster mHttpsPoster = new KHttpsPoster();
	private static boolean DEBUG = KInfocUtil.debugLog;
	
	private boolean reprotDataX(String url, byte[] data, int dcount) {
		if (null == mPoster || data == null) {
			return false;
		}
		MMM.log("-> reprotDataX");
		
		KHttpData kdata = new KHttpData();
		kdata.setData(data);
		kdata.setTableName(BATCHTABLENAME + String.valueOf(dcount));
		kdata.setForce(false);
		kdata.setCacheTime(0);
		
		d(" [ BATCH REPORT ]");
		d(kdata.toString());

		boolean success = false;
		String sUrlString = url;
		
		
		if(sUrlString != null && sUrlString.contains("https")){
			success = mHttpsPoster.httpsPost(kdata, sUrlString, null);
		}else{
			success = mPoster.httpPost(kdata, sUrlString, null);
		}
		
		if (success) {
			d("    * SUCCESS");
		} else {
			d("    * FAILED!!");
		}
		
		return success;
	}
	
	public static void sleepx(long msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] safeReadIch(File ich, int sizeLimit) {
		byte[] chunk = null;
		// TODO: 不能用file.length()么???
		try {
			chunk = KFile.readBuffer(ich);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(chunk == null) {
			return null;
		}
		if(chunk.length > Math.abs(sizeLimit)) {
			return null;
		}
		
		return chunk;
	}
	
	public static boolean isEmpty(byte[] xs) {
		return xs == null || xs.length == 0;
	}
			
	public int startReportX(String url, File[] ichs, String dataPublic, int produceId, String kfmtPath, int expireDay) {
		if (url == null || ichs == null || ichs.length == 0 || dataPublic == null || -1 == produceId || kfmtPath == null || !KInfocClient.isInited()) {
			return REPORT_RESULT_FAIL_DATA;
		}

		Context context = InfocCommonBase.getInstance().getApplication().getApplicationContext();

		// 创建公共表头
		byte[] header = KInfocUtil.encodeHeader(context, dataPublic, produceId, kfmtPath);
		if(isEmpty(header)) {
			return REPORT_RESULT_FAIL_DATA;
		}
		d(" * ENCODED HEADER : " + header.length + " LIMIT="+MAX_BATCH_BUFFER_LENGTH);

		ArrayList<File> penddingIchs = new ArrayList<File>();
		int limit = MAX_BATCH_BUFFER_LENGTH - header.length;
		ByteBuffer bf = ByteBuffer.allocate(limit);
		
		int bodyLength = 0;
		
		for(File ich : ichs) {
			// 数据空间用完,不能再向报表中添加数据
			if (limit <= 0) {
				d(" * REACH LIMITAIONS @BREAK");
				break;
			}
			
			if(!ich.isFile()) {
				continue;
			}
			
			// 是否过期，过期删除
			long createTime = getCreateTime(ich.getName());
			if (isExpired(ich.getName(), createTime, expireDay)) {
				ich.delete();
				continue;
			}
			
			// 读取缓存报表
			byte[] chunk = safeReadIch(ich, limit);
			if (isEmpty(chunk)) {
				d(" * EMPTY DATA : " + ich.getName());
				continue;
			}
			
			limit -= chunk.length;
			bodyLength += chunk.length;
			bf.put(chunk);
			penddingIchs.add(ich);
		}
				
		if (penddingIchs.isEmpty()) {
			d(" * NO ICHS NEED REPORT");
			return 0;
		}
		
		bf.rewind();
		byte[] body = new byte[bodyLength];
		bf.get(body);
		
		
		int dcount =  penddingIchs.size()+1;
		boolean success = reprotDataX(url, createBatchReportBin(header, body, dcount, produceId), dcount);
		if (success) {
			rm(penddingIchs);
			d(" * REPORT SUCCESS : " + "DC+H=" + dcount + " DC=" + penddingIchs.size() + " SIZE="+(header.length + body.length));
		} else {
			return REPORT_RESULT_FAIL_NETWORK;
		}
		sleepx(2000);
		return 0;
	}
	
	public static void d(final String message) {
		if(DEBUG) {
			Log.d(KInfocUtil.LOG_TAG, message);
		}
	}
	
	public static boolean rm(List<File> files) {
		int n = 0;
		if(files != null) {
			for(File f : files) {
				if(f.delete()) {
					n++;
				}
			}
		} else {
			return true;
		}
		return n == files.size();
	}

	/**
	 * 建立批量上报数据
	 * @param header 公共字段头
	 * @param body   数据段
	 * @param dc     Infoc数据条目(等于 数据段中包含的报表数 + 1 , 公共字段占一个数据项)
	 * @param produceId produceId
	 * @return null 出错了，否则返回协议数据
	 */
	private byte[] createBatchReportBin(byte[] header, byte[] body, int dc, int produceId) {
		byte[] data = join(header,body);
		return a.d(data, data.length, dc, produceId);
	}
	
	/**
	 * 合并给定的两个数组
	 * @param header
	 * @param body
	 * @return
	 */
	public static byte[] join(byte[] header, byte[] body) {
		ByteBuffer b = ByteBuffer.allocate(header.length + body.length);
		b.put(header);
		b.put(body);
		return b.array();
	}

	/**
	 * 是否过期
	 * @param timestamp 测试时间
	 * @param expireDay 有效天数
	 * @return 测试时间超过有效天数或timestamp无效(<=0)，则过期返回true,否则false
	 */
	public static boolean isExpired(String ich, long timestamp, int expireDay) {
		long diff = KInfocUtil.getDayDiff(timestamp);
		boolean b =  timestamp <= 0 ? true : diff >= expireDay;
		if(b) {
			d(" * IS EXPIRED : " + ich + " CTIME="+timestamp + " DAY="+diff + " | limit="+expireDay + " | " + ( diff >= expireDay ) + " | " + (timestamp <= 0) );
		}
		return b;
    }

	static Pattern ICH_FILE_PATTERN = Pattern.compile(".*_(\\d+).ich");
	public static long getCreateTime(String name) {
		long timestamp = 0;
		try {
			Matcher m = ICH_FILE_PATTERN.matcher(name);
			if(m.matches()) {
				timestamp = Long.parseLong(m.group(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
			timestamp = 0;
		}
		return timestamp;
	}
}
