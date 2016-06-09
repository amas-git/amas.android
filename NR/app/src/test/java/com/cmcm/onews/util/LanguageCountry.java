package com.cmcm.onews.util;

import android.content.Context;
import android.text.TextUtils;

import com.cmcm.onews.C;
import com.cmcm.onews.R;

public class LanguageCountry {
	//语言
	//带2的，是旧的语言language code，用于和我们自带语言包进行匹配
	static public String LANGUAGE_OPTION_DEFAULT = "language_default";
	static public String LANGUAGE_OPTION_EN = "en";
	static public String LANGUAGE_OPTION_HI ="hi";
	static public String LANGUAGE_OPTION_ZH = "zh";

	//国家地区
	static public String COUNTRY_OPTION_DEFAULT = "country_default";
	static public String COUNTRY_OPTION_CN = "CN";
	
	private String mLanguage = "";
	private String mCountry = "";
	private int mLanguageNameResId = 0;	//因为MoSecurityApplication中更新语言时，getstring有时会发生NotFoundException崩溃，所以改成保存字符资源id
	private boolean mbLanguageCheck = false;
	
	public LanguageCountry(String language){
		mLanguage = language;
		matchLanguageName();
	}
	
	//同语种但不同国家的语言，需要传入country参数，
	//例如language：LANGUAGE_OPTION_ZH，country：COUNTRY_OPTION_CN
	public LanguageCountry(String language, String country){
		mLanguage = language;
		if(TextUtils.isEmpty(country)){
			country = "";
		}
		mCountry = country;
		matchLanguageName();
	}
	
	public void matchLanguageName(){
		mLanguageNameResId = R.string.settings_language_en;
		if(mLanguage.equalsIgnoreCase(LANGUAGE_OPTION_HI)){
			mLanguageNameResId = R.string.settings_language_hi;
		}else if(mLanguage.equalsIgnoreCase(LANGUAGE_OPTION_ZH)){
			mLanguageNameResId = R.string.settings_language_zh;
		}

		//如果上面匹配不到，则是默认的英语（包括英语国家和不支持的语种），所以将mLanguage和mCountry置为英语
		if(mLanguageNameResId == R.string.settings_language_en){
			mLanguage = LANGUAGE_OPTION_EN;
			mCountry = "";
		}
	}
	
	public String getLanguage(){
		return this.mLanguage;
	}
	/*
	 * 获得语言带上国家的参数，这样更有利于精确的区分国家
	 * ex:  zh-CN zh-TW 表示中文简体和中文繁体
	 * ex:  en-BR en-US 表示英国英语和美国英语
	 * 
	 */
    public String getLanguageWithCountry() {
        if (TextUtils.isEmpty(mCountry)) {
            return this.mLanguage;
        } else {
            return this.mLanguage + "-" + mCountry;
        }
    }

    public String getLanguageWithCountryUnderline() {
        if (TextUtils.isEmpty(mCountry)) {
            return this.mLanguage + "_";
        } else {
            return this.mLanguage + "_" + mCountry;
        }
    }
	
	public String getCountry(){
		return this.mCountry;
	}
	
	public String getLanguageName(Context context){
		return context.getString(mLanguageNameResId);
	}
	
	public void setLanguageCheck(boolean check){
		this.mbLanguageCheck = check;
	}
	
	public boolean isLanguageCheck(){
		return this.mbLanguageCheck;
	}

	/**
	 * 国内
	 * @return
	 */
	public static LanguageCountry CN(){
		return new LanguageCountry(LANGUAGE_OPTION_ZH,COUNTRY_OPTION_CN);
	}

	public boolean isLanguageHidi(){
		return mLanguage.equalsIgnoreCase(LANGUAGE_OPTION_HI);
	}
}
