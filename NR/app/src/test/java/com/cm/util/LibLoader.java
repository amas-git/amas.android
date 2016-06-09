package com.cm.util;

import android.content.Context;

import com.cm.kinfoc.api.MMM;

public class LibLoader {
	
	private static LibLoader mInst = new LibLoader();
	
	public static LibLoader getInstance() {
		return mInst;
	}
	
	private LibLoader() {
	}
	
	
	// @FIXME: brett
	//@REMARK
	// 优先加载本地目录、之后再执行默认加载
	//
	public String loadLibrary(Context context, String fileName){
		
		if (mUtilsLoaded && (null == mUtils || mUtils.isLibraryOk())) {
			// 已成功加载过
			return libString;
		}
		
		mUtilsLoaded = false;
		
		boolean keepUtils = true;
		mUtils = new LibLoadUtils(context, fileName);
        MMM.log(" * mUtils : " + mUtils + " context="+context+" fileName="+fileName);
        if (!mUtils.load()) {
			System.loadLibrary(fileName);
			if(KcmutilSoLoader.LIB_NAME.equals(fileName)){
				libString = fileName;
			}
			keepUtils = false;
		}
        MMM.log(" * keepUtils :" + keepUtils);
		if(keepUtils && KcmutilSoLoader.LIB_NAME.equals(fileName)){
            libString = mUtils.getLibFullPath();
        }
        MMM.log(" * libString :" + libString);

		mUtilsLoaded = true;

		mUtils.close();
		
		if (!keepUtils) {
			mUtils = null;
		}
        MMM.log(" * libString :" + libString);
		return libString;
	}
	
	private String libString = null;
	
	private LibLoadUtils mUtils = null;
	private boolean mUtilsLoaded = false;
}

