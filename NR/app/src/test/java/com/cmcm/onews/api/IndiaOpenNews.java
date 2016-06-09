package com.cmcm.onews.api;

import android.app.Activity;
import android.os.Bundle;

import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.sdk.SOURCE;
import com.cmcm.onews.ui.NewsAlbumActivity;
import com.cmcm.onews.ui.NewsBaseActivity;
import com.cmcm.onews.ui.item.BaseNewsItem;

public class IndiaOpenNews {
    public static boolean openNews(BaseNewsItem item, Activity activity) {
        if (activity instanceof NewsAlbumActivity) {
            NewsAlbumActivity album = (NewsAlbumActivity) activity;
            Bundle bundle = new Bundle();
            bundle.putString(NewsBaseActivity.KEY_RELATED_CONTENTID, album.contentid());
            return NewsSdk.INSTAMCE.openOnewsWithSource(activity, item.scenario(), item.oNews(), SOURCE.DETAIL_FROM_ALBUM, bundle);
        } else{
            return NewsSdk.INSTAMCE.openOnewsWithSource(activity, item.scenario(), item.oNews(),SOURCE.DETAIL_FROM_LIST,null);
        }
    }
}
