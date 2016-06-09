package com.cm.kinfoc.api;

import android.util.Log;

import com.cm.kinfoc.base.AsyncConsumerTask;
import com.cm.kinfoc.base.InfocServerControllerBase;

public class InfocServerControllerImp extends InfocServerControllerBase {
	
	public InfocServerControllerImp() {
		AsyncConsumerTask.Builder<ConsumerItem> builder = new AsyncConsumerTask.Builder<ConsumerItem>();
		mAsyncConsumer = builder.mWaitTime(1000 * 17).mCallback(new AsyncConsumerTask.ConsumerCallback<ConsumerItem>() {
			@Override
			public void consumeProduct(final ConsumerItem product) {
				
				if (null == product || product.mCb == null) {
					return;
				}
				
				switch(product.mCItemType){
				
				case ConsumerItem.CITEM_TYPE_INFOCREPORT:
					onInfocReport(product.mCb);
					break;
					default:
						break;
				}
			}

			private void onInfocReport(IResultCallback cb) {
				cb.onResult(CONTROLLERTYPE.REP_PRIVATE_DATA, true, null);
			}
		}).build();
	}

	
	@Override
	public void getInfocRepPrivateDataAval(final IResultCallback cb){
		if(null == cb){
			return;
		}
		
		ConsumerItem item = new ConsumerItem(ConsumerItem.CITEM_TYPE_INFOCREPORT, cb);
		mAsyncConsumer.addProduct(item);
	}
	
//	private void onInfocReport(final IResultCallback cb){
//		if(null == cb){
//			return;
//		}
//		
//		
//		final Context context = MoSecurityApplication.getInstance().getApplicationContext();
//		String data = ServiceConfigManager.getInstanse(context).getInfocRepPrivateDataAval();
//		String[] dataSplite = data.split("-");
//		boolean isDataError = false;
//		if (dataSplite.length < 2) {
//			isDataError = true;
//		}
//		
//		long lastCheckedTime = Long.parseLong(dataSplite[0], 10);
//		final long currentTime = System.currentTimeMillis();
//
//		if ((currentTime - lastCheckedTime > getGapTime() || isDataError) && NetworkUtil.IsNetworkAvailable(context)) {
//
//			String url = buildUrl(CONTROLLERTYPE.REP_PRIVATE_DATA);
//			
//			byte[]	bufferRes = null;
//			try {
//				
//				if(FirstAccessNetDialogActivity.isAllowAccessNetwork(MoSecurityApplication.getInstance().getApplicationContext())){
//					bufferRes = AdwareHttpConnector.GetHttpResult(url);
//				}else{
//					cb.onResult(CONTROLLERTYPE.REP_PRIVATE_DATA, false, null);
//					return;
//				}
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			boolean isSuccess = false;
//			String result = null;
//			if(null != bufferRes && bufferRes.length > 0) {
//				isSuccess = true;
//				result = new String(bufferRes);
//			}
//			
//			if (isSuccess && result.equals(REPPRIVATEDATAOPENFLAG)) {
//				ServiceConfigManager.getInstanse(context).setInfocRepPrivateDataAval(
//								currentTime, "0");
//				cb.onResult(CONTROLLERTYPE.REP_PRIVATE_DATA, false, null);
//				return;
//			}
//
//			ServiceConfigManager.getInstanse(context).setInfocRepPrivateDataAval(currentTime, "1");
//			cb.onResult(CONTROLLERTYPE.REP_PRIVATE_DATA, true, null);
//
//		} else {
//			if (isDataError) {
//				cb.onResult(CONTROLLERTYPE.REP_PRIVATE_DATA, true, null);
//				ServiceConfigManager.getInstanse(context).setInfocRepPrivateDataAval(currentTime, "1");
//			} else {
//				boolean canReport = dataSplite[1].equalsIgnoreCase("1");
//				cb.onResult(CONTROLLERTYPE.REP_PRIVATE_DATA, canReport, null);
//			}
//		}
//	}
	
}
