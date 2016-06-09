package com.cm.kinfoc;

public interface INRDCallback {
	/**
	 * 通知下次上报时间
	 * @param nextTime 下次上报时间(单位ms)
	 */
	public void notifyNextReportTime(long nextTime);
}
