package com.cm.kinfoc;


import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.text.TextUtils;

import com.cm.kinfoc.IRData.InfocDataItemBase;
import com.cm.kinfoc.api.MMM;
import com.cm.kinfoc.base.AsyncConsumerTask;
import com.cm.kinfoc.base.InfocServerControllerBase;
import com.cm.kinfoc.base.InfocServerControllerBase.CONTROLLERTYPE;
import com.cm.kinfoc.base.InfocServerControllerBase.IResultCallback;


public class KInfocClientAssist {
	
	public static final byte SCAN_FUNCTION_TYPE_PRIVACY   = 1;
	public static final byte SCAN_FUNCTION_TYPE_CACHE     = 2;
	public static final byte SCAN_FUNCTION_TYPE_RESIDUAL  = 3;
	public static final byte SCAN_FUNCTION_TYPE_UNINSTALL = 4;
	public static final byte SCAN_FUNCTION_TYPE_APK       = 5;
	public static final byte SCAN_FUNCTION_TYPE_TASK      = 6;
	public static final byte SCAN_FUNCTION_TYPE_MOVE      = 7;
	
	private static KInfocClientAssist mInst = null;

	public static KInfocClientAssist getInstance() {
		if (mInst == null) {
			synchronized (KInfocClientAssist.class) {
				if (null == mInst) {
					mInst = new KInfocClientAssist();
				}
			}
		}
		return mInst;
	}
	
	private KInfocClientAssist() {
		
		KInfocClient.init(false);
		final ReportDataCallback rd = new ReportDataCallback();
		AsyncConsumerTask.Builder<InfocDataItemBase> builder = new AsyncConsumerTask.Builder<InfocDataItemBase>();
		mAsyncConsumer = builder.mWaitTime(1000 * 17).mCallback(new AsyncConsumerTask.ConsumerCallback<InfocDataItemBase>() {
			@Override
			public void consumeProduct(InfocDataItemBase product) {
				if (null != product) {
					if (KInfocClient.init_Sync()) {
						if (null != rd) {
							rd.reportData(KInfocClient.getInstance(), product);
						}
					}
				}
			}
		}).build();
		
	}
	
	/**
	 * 按照用户上报概，计算本用户是否需要上报。(可以根据本函数的返回值来决定是否要计算上报数据和上报。)
	 * @param tableName	表名
	 * @param reset		是否要重算概率
	 * @return 是否需要上报
	 */
	public boolean needReportData(String tableName, boolean reset) {
		if (!KInfocClient.init_Sync()) {
			return false;
		}
		
		return KInfocClient.getInstance().needReportData(tableName, reset);
	}
	
	/**
	 * 在确保不会卡的前提下上报活跃数据，如果未初始化infoc，则自动调用初始化。(内部存在24小时只报一次的逻辑)
	 * @param srv 活跃Service
	 * @param nrd 通知下次上报时间的回调
	 */
	public void reportActive(Service srv, INRDCallback nrd) {
		if (null == srv) {
			return;
		}
		
		InfocDataItemBase item = new InfocDataItemBase();
		item.itemType = InfocDataItemBase.INFOC_DATA_ITEM_TYPE_SERVICE;
		
		ServiceActiveData srvActData = new ServiceActiveData();
		srvActData.mSrv = srv;
		srvActData.mNRD = nrd;
		item.dataItemObj = srvActData;
		
		pushToReportQueue(item);
	}
	
	/**
	 * 在确保不会卡的前提下上报活跃数据，如果未初始化infoc，则自动调用初始化。(内部存在24小时只报一次的逻辑)
	 * @param act 活跃Activity
	 */
	public void reportActive(Activity act) {
		if (null == act) {
			return;
		}
		
		InfocDataItemBase item = new InfocDataItemBase();
		ActivityReportStartInfo info = new ActivityReportStartInfo();
		item.itemType = InfocDataItemBase.INFOC_DATA_ITEM_TYPE_ACTIVITY;
		info.act = act;
		item.dataItemObj = info;
		
		pushToReportQueue(item);
	}
	
	/**
	 * 在确保不会卡的前提下上报活跃数据，如果未初始化infoc，则自动调用初始化。(内部存在24小时只报一次的逻辑)
	 * @param act 活跃Activity
	 * @param s 报活的表中s字段的值
	 */
	public void reportActive(Activity act, Bundle ext) {
		if (null == act) {
			return;
		}
		
		InfocDataItemBase item = new InfocDataItemBase();
		ActivityReportStartInfo info = new ActivityReportStartInfo();
		item.itemType = InfocDataItemBase.INFOC_DATA_ITEM_TYPE_ACTIVITY;
		info.act = act;
		info.b = ext;
		item.dataItemObj = info;
		
		pushToReportQueue(item);
	}
	
	/**
	 * 在确保不会卡的前提下上报活跃数据，如果未初始化infoc，则自动调用初始化。(内部存在24小时只报一次的逻辑)
	 * @param actTableName 活跃表名
	 * @param s 报活的表中s字段的值
	 */
	public void reportActive(String actTableName, Bundle ext) {
		if (TextUtils.isEmpty(actTableName)) {
			return;
		}
		
		InfocDataItemBase item = new InfocDataItemBase();
		ActivityReportStartInfo info = new ActivityReportStartInfo();
		item.itemType = InfocDataItemBase.INFOC_DATA_ITEM_TYPE_ACTIVITY;
		info.tableName = actTableName;
		info.b = ext;
		item.dataItemObj = info;
		
		pushToReportQueue(item);
	}
	
	
	/**
	 * 在确保不会卡的前提下上报活跃数据，如果未初始化infoc，则自动调用初始化。(内部存在24小时只报一次的逻辑)
	 * @param act 活跃Activity
	 * @param s 报活的表中s字段的值
	 */
	public void reportActive(Activity act, boolean s) {
		if (null == act) {
			return;
		}
		
		InfocDataItemBase item = new InfocDataItemBase();
		ActivityReportStartInfo info = new ActivityReportStartInfo();
		item.itemType = InfocDataItemBase.INFOC_DATA_ITEM_TYPE_ACTIVITY;
		info.act = act;
		info.s = s;
		item.dataItemObj = info;
		
		pushToReportQueue(item);
	}
	
	/**
	 * 在确保不会卡的前提下上报数据，如果未初始化infoc，则自动调用初始化。
	 * @param tableName 要上报的表名
 	 * @param dataString 字符串
	 */
	public void reportData(final String tableName, final String dataString) {
		if(tableName == null || dataString == null) {
			return;
		}
		MMM.log("reportData : " + tableName);
		InfocServerControllerBase.getInstance().getInfocRepPrivateDataAval(new IResultCallback() {
			
			@Override
			public void onResult(CONTROLLERTYPE type, boolean isSuccess, String result) {
				MMM.log("reportData : " + isSuccess);
				if(!isSuccess){
					return;
				}
				
				InfocDataItemBase item = new InfocDataItemBase();
				item.itemType = InfocDataItemBase.INFOC_DATA_ITEM_TYPE_NORMAL;
				
				InfocDataItem data = new InfocDataItem();
				data.tableName = tableName;
				data.dataString = dataString;
				
				item.dataItemObj = data;
				
				pushToReportQueue(item);
			}
		});
	}
	
	/**
	 * 在确保不会卡的前提下上报数据，如果未初始化infoc，则自动调用初始化。(强制上报))
	 * @param tableName 要上报的表名
 	 * @param dataString 字符串
	 */
	public void forceReportData(final String tableName, final String dataString) {
		
		InfocServerControllerBase.getInstance().getInfocRepPrivateDataAval(new IResultCallback() {
			
			@Override
			public void onResult(CONTROLLERTYPE type, boolean isSuccess, String result) {
				if(!isSuccess){
					return;
				}
				
				InfocDataItemBase item = new InfocDataItemBase();
				item.itemType = InfocDataItemBase.INFOC_DATA_ITEM_TYPE_URGENT;
				
				InfocDataItem data = new InfocDataItem();
				data.tableName = tableName;
				data.dataString = dataString;
				
				item.dataItemObj = data;
				
				pushToReportQueue(item);
			}
		});
	}
	
	
	private void pushToReportQueue(InfocDataItemBase item) {
		mAsyncConsumer.addProduct(item);
	}
	
	private class InfocDataItem {
		public String tableName = null;
		public String dataString = null;
		@Override
		public String toString() {
			return String.format("(:name %s :data %s)", tableName, dataString);
		}
	}
	
	private class ReportDataCallback implements IRData {
		@Override
		public void reportData(KInfocClient infoc, InfocDataItemBase item) {
			if (null != infoc && null != item) {
				switch (item.itemType) {
				case InfocDataItemBase.INFOC_DATA_ITEM_TYPE_NORMAL:
					if (null != item.dataItemObj) {
						infoc.reportData(
								((InfocDataItem)item.dataItemObj).tableName, 
								((InfocDataItem)item.dataItemObj).dataString);
					}
					break;
					
				case InfocDataItemBase.INFOC_DATA_ITEM_TYPE_ACTIVITY:
					if (null != item.dataItemObj) {
						infoc.reportActivityStart((ActivityReportStartInfo)item.dataItemObj);
					}
					break;
					
				case InfocDataItemBase.INFOC_DATA_ITEM_TYPE_SERVICE:
					if (null != item.dataItemObj) {
						infoc.reportServiceActive((ServiceActiveData)item.dataItemObj);
					}
					break;
				case InfocDataItemBase.INFOC_DATA_ITEM_TYPE_URGENT:
					if (null != item.dataItemObj) {
						infoc.forceReportData(
								((InfocDataItem)item.dataItemObj).tableName, 
								((InfocDataItem)item.dataItemObj).dataString);
					}
					break;
				default:
					break;
				}
			}
		}
		
		@Override
		public String toString() {
			return "ReportDataCallback";
		}
	}
	
	private AsyncConsumerTask<InfocDataItemBase> mAsyncConsumer = null;
	
}
