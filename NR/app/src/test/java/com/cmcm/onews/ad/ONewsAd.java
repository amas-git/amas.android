package com.cmcm.onews.ad;

import android.view.View;

import com.cleanmaster.ui.app.market.Ad;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.utils.AdUtil;

import java.util.List;

public class ONewsAd implements IONewsAd {
    private INativeAd mINativeAd;

    public ONewsAd(INativeAd iNativeAd) {
        this.mINativeAd = iNativeAd;
    }

    @Override
    public void registerViewForInteraction(View convertView) {
        mINativeAd.registerViewForInteraction(convertView);
    }

    @Override
    public void unRegisterViewForInteraction() {
        mINativeAd.unregisterView();
    }

    @Override
    public CharSequence getAdTitle() {
        return mINativeAd.getAdTitle();
    }

    @Override
    public String getAdCoverImageUrl() {
        return mINativeAd.getAdCoverImageUrl();
    }

    @Override
    public boolean isAdBig() {
        int type = AdUtil.getAdAppShowType(mINativeAd);
        if (Ad.SHOW_TYPE_HAVE_PIC_BIG_CARD == type || -1 == type) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isAdSmall() {
        int type = AdUtil.getAdAppShowType(mINativeAd);
        if (Ad.SHOW_TYPE_NEWS_SMALL_PIC == type) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isAdThree() {
        int type = AdUtil.getAdAppShowType(mINativeAd);
        if (Ad.SHOW_TYPE_NEWS_THREE_PIC == type) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getAdBody() {
        return mINativeAd.getAdBody();
    }

    @Override
    public int getAdAppShowType() {
        return AdUtil.getAdAppShowType(mINativeAd);
    }

    @Override
    public List<String> getExtPics() {
        return mINativeAd.getExtPics();
    }

    @Override
    public void setAdOnClickListener(final IONewsAdClick adOnClickListener) {
        mINativeAd.setAdOnClickListener(new INativeAd.IAdOnClickListener() {
            @Override
            public void onAdClick() {
                adOnClickListener.onAdClick();
            }
        });
    }

    @Override
    public boolean isDownLoadApp() {
        return mINativeAd.isDownLoadApp();
    }
}
