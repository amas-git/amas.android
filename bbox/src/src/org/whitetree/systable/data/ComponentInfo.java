package org.whitetree.systable.data;

import android.content.pm.ResolveInfo;

public class ComponentInfo {
	
	private boolean mIsEnable;
	private ResolveInfo mInfo;

	public ResolveInfo getInfo() {
		return mInfo;
	}

	public void setInfo(ResolveInfo info) {
		mInfo = info;
	}

	public boolean isEnable() {
		return mIsEnable;
	}

	public void setEnable(boolean isEnable) {
		mIsEnable = isEnable;
	}
}
