package com.cmcm.onews.ui;

import android.animation.ArgbEvaluator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.cmcm.onews.ui.slidr.SliderPanel;
import com.cmcm.onews.ui.slidr.SlidrConfig;

/**
 * Created by Jason.Su on 2016/4/7.
 * com.cmcm.onews.ui
 * des:
 * email:suyanjiao@conew.com
 * 1553202383
 */
public class NewsSlideActivity extends NewsBaseUIActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slidr();
    }
    private SlidrConfig config;
    private void slidr() {
        config = new SlidrConfig();
        config.primaryColor(Color.parseColor("#689F38"));
        config.secondaryColor(Color.parseColor("#00000000"));
        config.velocityThreshold(2400);
        config.distanceThreshold(0.4f);
        config.edge(true);//默认右滑退出

        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        View oldScreen = decorView.getChildAt(0);
        decorView.removeViewAt(0);

        // Setup the slider panel and attach it to the decor
        SliderPanel panel = new SliderPanel(this, oldScreen, config);
        panel.setId(com.cmcm.onews.sdk.R.id.onews__slidable_panel);
        oldScreen.setId(com.cmcm.onews.sdk.R.id.onews__slidable_content);
        panel.addView(oldScreen);
        decorView.addView(panel, 0);

        // Set the panel slide listener for when it becomes closed or opened
        panel.setOnPanelSlideListener(new SliderPanel.OnPanelSlideListener() {
            private final ArgbEvaluator mEvaluator = new ArgbEvaluator();

            @Override
            public void onStateChanged(int state) {

            }

            @Override
            public void onClosed() {
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onOpened() {

            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSlideChange(float percent) {
                // Interpolate the statusbar color
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && config.areStatusBarColorsValid()) {
                    int newColor = (int) mEvaluator.evaluate(percent, config.getPrimaryColor(), config.getSecondaryColor());
                    getWindow().setStatusBarColor(newColor);
                }
            }
        });
    }
}
