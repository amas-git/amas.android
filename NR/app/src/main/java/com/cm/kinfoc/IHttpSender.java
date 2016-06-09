package com.cm.kinfoc;


/**
 * post get方式接口
 */
public interface IHttpSender {
	
	public void send(KHttpData data, String url, OnResultListener onResult);
	
	public static interface OnResultListener {
		void onSuccess(long lasttime, KHttpData data);
		void onFail(KHttpData data);
	}
}
