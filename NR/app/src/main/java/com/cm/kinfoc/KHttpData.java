package com.cm.kinfoc;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Infoc上报数据类
 * @author singun
 */
public class KHttpData implements IHttpData , Cloneable {
	private byte[] mData = null;
	private String mStringData = null;
	private String mTableName = null;
	private boolean mForce = false;
	private long mCacheTime;
	private int mServerPriority = KInfoControl.emKInfoPriority_Unknow;
	private KHttpResultListener mListener;
	private ArrayList<String> mBatchFiles = null;
	
	public Object clone(){
		KHttpData object = null;
		try{
			object = (KHttpData)super.clone();
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
		
		return object;
	}
	
	@Override
	public byte[] getData() {
		return mData;
	}
	
	@Override
	public void setData(byte[] data) {
		mData = data;
	}
	
	@Override
	public String getTableName() {
		return mTableName;
	}
	
	@Override
	public void setTableName(String tableName) {
		mTableName = tableName;
	}
	
	@Override
	public boolean isForce() {
		return mForce;
	}
	
	@Override
	public void setForce(boolean force) {
		mForce = force;
	}
	
	@Override
	public long getCacheTime(){
		return mCacheTime;
	}
	
	@Override
	public void setCacheTime(long cacheTime) {
		mCacheTime = cacheTime;
	}

	@Override
	public String getStringData() {
		return mStringData;
	}

	@Override
	public void setData(String data) {
		mStringData = data;
	}
	
	@Override
	public int getServerPriority(){
		return mServerPriority;
	}
	
	@Override
	public void setServerPriority(int nPriority){
		mServerPriority = nPriority;
	}
	
	public void setHttpListener(KHttpResultListener listener) {
		mListener = listener;
	}
	
	public KHttpResultListener getHttpListener() {
		return mListener;
	}
	
	public void setBatchFiles(ArrayList<String> batchFiles){
		if(batchFiles != null && batchFiles.size() > 0) {
			mBatchFiles = new ArrayList<String>();
			mBatchFiles.addAll(batchFiles);
		}
	}
	
	public ArrayList<String> getBatchFiles() {
		return mBatchFiles;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" KHttpData : ").append("\n");
		sb.append("  * tname  : ").append(getTableName()).append("\n");
		sb.append("  * ctime  : ").append(getCacheTime()).append("\n");
		sb.append("  * sproi  : ").append(getServerPriority()).append("\n");
		sb.append("  * force  : ").append(isForce()).append("\n");
		sb.append("  * dsize  : ").append(getDataSize()).append("\n");
		sb.append("  * -----  : ").append(getStringData()).append("\n");
		return super.toString();
	}

	private int getDataSize() {
		return mData == null ? 0 : mData.length;
    }

	public boolean removeIch(Context context) {
		File dIch = isForce()
				? KInfocUtil.getExistOrCreate_CACHE_DIR_FORCE(context, getServerPriority())
				: KInfocUtil.getExistOrCreate_CACHE_DIR(context, getServerPriority());
		if (dIch == null) {
			return false;
		}
		File ich = new File(dIch.getAbsolutePath(), getTableName() + KInfocUtil.SEP_CHAR + System.currentTimeMillis() + KInfocUtil.FILE_EXT);
		return ich.delete();
    }

	public boolean saveIch(Context context) {
		File dIch = isForce()
				? KInfocUtil.getExistOrCreate_CACHE_DIR_FORCE(context, getServerPriority())
				: KInfocUtil.getExistOrCreate_CACHE_DIR(context, getServerPriority());
		if(mData == null || mData.length == 0) {
			return false;
		}
		
		if(dIch == null) {
			return false;
		}
		
		boolean ret = false;
		try {
			KFile file = new KFile(context);
			ret = file.saveCacheFile(dIch.getAbsolutePath(), getTableName() + KInfocUtil.SEP_CHAR + System.currentTimeMillis() + KInfocUtil.FILE_EXT, mData);
		} catch(IOException e) {
			e.printStackTrace();
			ret = false;
		}
		
		return ret;
    }
}
