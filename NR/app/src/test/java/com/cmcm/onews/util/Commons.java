package com.cmcm.onews.util;

import android.content.Context;

public final class Commons {

    public static void updateLanguage(Context context) {
        if(ConflictCommons.isCNVersion()){
            UIConfigManager.getInstanse(context).setLanguageSelected(LanguageCountry.CN());
            LanguageUtils.setLanguage(LanguageCountry.CN(), context);
        }else {
            final LanguageCountry lang = UIConfigManager.getInstanse(context).getLanguageSelected(context);
            LanguageUtils.setLanguage(lang, context);
        }
    }
}
