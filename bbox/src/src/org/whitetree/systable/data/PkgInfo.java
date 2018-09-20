package org.whitetree.systable.data;

import org.whitetree.systable.LOG;
import org.whitetree.systable.system.U;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.ServiceManager;

import com.android.internal.content.PackageHelper;

public class PkgInfo {
	public String mAppName = "";
	public String mPackageName = "";
	public String mVersionName = "";
	public String mSharedUserId = "";
	public int mVersionCode = 0;
	public long mCacheSize = 0;
//	public long mAppSize = 0;

	public Drawable mAppIcon = null;
	public boolean mIsInternal = true;
	public boolean mCouldMove2Sdcard = false;

	public boolean mIsSelected = false;
	public long mCodeSize = 0;
	public long mDateSize = 0;

	public String toString() {
		return (" AppName:" + mAppName + "; PackageName:" + mPackageName
				+ "; VersionName:" + mVersionName + "; VersionCode:"
				+ mVersionCode + "; CodeSize:" + mCodeSize + "; DataSize: " + mDateSize + "CacheSize:" + mCacheSize + "; mHash:"
				+ hashCode() + "; ");
	}
	

	public static PkgInfo create(Context context, PackageManager pm, ResolveInfo i) {
		PkgInfo pkgInfo = new PkgInfo();
		try {
			// LOG.dump(i);
			ApplicationInfo ai = i.activityInfo.applicationInfo;

			pkgInfo.mAppName = ai.loadLabel(pm).toString();
			pkgInfo.mPackageName = i.activityInfo.packageName;
			// pkgInfo.mVersionName = i.activityInfo.applicationInfo.
			// pkgInfo.mVersionCode = p.versionCode;
			pkgInfo.mAppIcon = ai.loadIcon(pm);
			LOG._("---permission---> " + ai.permission);
			// pkgInfo.mSharedUserId = p.sharedUserId;

			// is installed in stroage or sdcard
			pkgInfo.mIsInternal = (ai.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == U.APP_STORAGE_INTERNAL_FLAG;

			// if apk not in sdcard, check it could move to sdcard
			if (pkgInfo.mIsInternal) {
				pkgInfo.mCouldMove2Sdcard = false;

				// 2.2才开始支持
				if (ai != null && (ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0
						&& (ai.flags & ApplicationInfo.FLAG_FORWARD_LOCK) == 0) {

					if (ai.installLocation == PackageInfo.INSTALL_LOCATION_PREFER_EXTERNAL
							|| ai.installLocation == PackageInfo.INSTALL_LOCATION_AUTO) {
						pkgInfo.mCouldMove2Sdcard = true;
					} else if (ai.installLocation == PackageInfo.INSTALL_LOCATION_UNSPECIFIED) {
						IPackageManager ipm = IPackageManager.Stub
								.asInterface(ServiceManager
										.getService("package"));
						int loc;
						try {
							loc = ipm.getInstallLocation();
						} catch (RemoteException e) {
							return pkgInfo;
						}
						if (loc == PackageHelper.APP_INSTALL_EXTERNAL) {
							// For apps with no preference and the default
							// value set
							// to install on sdcard.
							pkgInfo.mCouldMove2Sdcard = true;
						}
					}
				}
			}
		} catch (Exception ex) {
		}
		return pkgInfo;
	}
}
