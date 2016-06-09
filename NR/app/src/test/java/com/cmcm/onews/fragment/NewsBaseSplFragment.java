package com.cmcm.onews.fragment;

import android.content.Context;
import android.os.Bundle;

import com.cmcm.newsindia.MainActivity;
import com.cmcm.onews.util.ConflictCommons;

public class NewsBaseSplFragment extends NewsBaseFragment{
    public MainActivity activity;

    public static NewsBaseSplFragment newInstance() {
        /* BUILD_CTRL:IF:NOTCNVERSION */
        if (!ConflictCommons.isCNVersion()) {
            return new NewsInSpFragment();
        }
        /* BUILD_CTRL:ENDIF:NOTCNVERSION */

        /* BUILD_CTRL:IF:CNVERSION */
        if(ConflictCommons.isCNVersion()){
            return new NewsCnSpFragment();
        }
       /* BUILD_CTRL:ENDIF:CNVERSION */

        return null;
    }

    @Override
    public void onAttach(Context context) {
        activity = (MainActivity)context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initReport() {

    }
}
