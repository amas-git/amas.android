package com.cm.kinfoc;

public interface IHttpData {
	public byte[] getData();
	
	public String getStringData();
	
	public void setData(byte[] data);
	
	public void setData(String data);
	
	public String getTableName();
	
	public void setTableName(String tableName);
	
	public boolean isForce();
	
	public void setForce(boolean force);
	
	public long getCacheTime();
	
	public void setCacheTime(long cacheTime);
	
	public int getServerPriority();
	
	public void setServerPriority(int nPriority);
}
