package com.cmcm.onews.ui;

import android.os.Bundle;

import com.cmcm.onews.C;
import com.cmcm.onews.infoc.newsindia_language;

/**
 * Created by yzx on 2015/12/10.
 */
public class NewsBaseUIActivity extends NewsBaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        C.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        C.getInstance().removeActivity(this);
        super.onDestroy();
    }

    public void reportLanguage(int action){
        newsindia_language language = new newsindia_language();
        language.action(action);
        language.report();
    }
}
