package com.cm.kinfoc;

import android.text.TextUtils;

/* 
 * infoc http结果解析类
 *   
 *   胖大海2013-08-20 14:52:17    廖杰(QQ:1611755):
 *   0表示失败, 1表示成功
 */

public class HttpResult {
	public static final int SUCCESS = 1;
	public static final int FAILED = 0;
	
	public long nServeTime = 0;
	public int nResult = 0;

	public boolean isSucess() {
		return nResult == SUCCESS;
	}

	public static  HttpResult parserString(String strResult) {

		if (TextUtils.isEmpty(strResult)) {
			return null;
		}

		String[] lines = strResult.split("\r\n");
		HttpResult res = new HttpResult();
		if (lines != null && lines.length >= 3) {
			if (!lines[0].equals("[common]")) {
				return null;
			}

			String resMark = "result=";
			if (lines[1].startsWith(resMark)) {
				String strRes = lines[1].substring(resMark.length()).trim();
				try {
					res.nResult = Integer.parseInt(strRes);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			} else {
				return null;
			}

			String resMarkTime = "time=";
			if (lines[2].startsWith(resMarkTime)) {
				String strRes = lines[2].substring(resMarkTime.length()).trim();
				try {
					res.nServeTime = Long.parseLong(strRes);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			} else {
				return null;
			}
		}

		return res;
	}
}
