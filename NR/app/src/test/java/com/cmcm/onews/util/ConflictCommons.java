package com.cmcm.onews.util;

public class ConflictCommons {
	public static final int PRODUCT_ID_CN 		= 1;	///< 国内版
	public static final int PRODUCT_ID_OU 		= 2;	///< 国际版

	/* BUILD_CTRL:IF:CN_VERSION_ONLY 
	public static final int PRODUCT_ID = PRODUCT_ID_CN;
	BUILD_CTRL:ENDIF:CN_VERSION_ONLY */
	/* BUILD_CTRL:IF:OU_VERSION_ONLY */
	public static final int PRODUCT_ID = PRODUCT_ID_OU;
	/* BUILD_CTRL:ENDIF:OU_VERSION_ONLY */

	/**
	 * 只区分国内 和 国际， 不区分平台
	 * @return
	 */
	public static boolean isCNVersion(){
		return PRODUCT_ID == PRODUCT_ID_CN;
	}

	/**
	 * ProductId
	 * @return
	 */
	public static String ProductId(){
      /* BUILD_CTRL:IF:NOTCNVERSION */
		if (!ConflictCommons.isCNVersion()) {
			return "11";
		}
        /* BUILD_CTRL:ENDIF:NOTCNVERSION */

        /* BUILD_CTRL:IF:CNVERSION */
		if(ConflictCommons.isCNVersion()){
			return "12";
		}
       /* BUILD_CTRL:ENDIF:CNVERSION */

		return "11";
	}
}
