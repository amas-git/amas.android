package com.cleanmaster.ui.resultpage.storage;

import java.io.Serializable;

public class PackageCleanResult extends CleanItem implements Serializable {
	private static final long serialVersionUID = 5894558619567558958L;

	public PackageCleanResult(String packageName) {
		name(packageName);
	}

	public String getPackageName() {
		return name();
	}

	public PackageCleanResult setAllCleanSize(long size) {
		value(size);
		return this;
	}

	public static PackageCleanResult create(String packageName, int totalSize) {
		return new PackageCleanResult(packageName).setAllCleanSize(totalSize);
	}
}
