package com.cmcm.onews.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.cmcm.onews.util.push.PushOutAPI;

import java.util.Locale;
public class LanguageUtils {
	  public static void registerGCM(Context context){
		PushOutAPI.register(context);
	}

	public static void setLanguage(LanguageCountry languageCountry, Context context) {
		Resources res = context.getResources();
		if(res == null){
			return;
		}
		
		Configuration config = res.getConfiguration();
		if(config == null){
			return;
		}
		Locale locale = new Locale(languageCountry.getLanguage(), languageCountry.getCountry());
		config.locale = locale;
		Locale.setDefault(locale);
		DisplayMetrics dm = res.getDisplayMetrics();
		res.updateConfiguration(config, dm);

		registerGCM(context);
	}

	public static boolean isEnglish(LanguageCountry language){
		String languageStr =  language.getLanguage();
		String countryStr  =  language.getCountry();
		if(TextUtils.isEmpty(languageStr))
			return false;
		return languageStr.equals(LanguageCountry.LANGUAGE_OPTION_EN);
	}
}
