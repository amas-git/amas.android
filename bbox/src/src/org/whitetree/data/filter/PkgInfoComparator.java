package org.whitetree.data.filter;

import java.util.Comparator;

import org.whitetree.systable.data.PkgInfo;


public class PkgInfoComparator implements Comparator<PkgInfo> {

	@Override
	public int compare(PkgInfo arg0, PkgInfo arg1) {
		return arg0.mAppName.compareTo(arg1.mAppName); //升序
//        return arg0.mPackageName.compareTo(arg1.mPackageName);  //降序
	}
 }
