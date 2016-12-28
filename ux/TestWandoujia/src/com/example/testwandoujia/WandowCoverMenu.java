package com.example.testwandoujia;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;

public class WandowCoverMenu extends RelativeLayout {
	
	public ImageView mImgApp;
	public ImageView mImgGame;
	public ImageView mImgVideo;
	public ImageView mImgMore;
	
	private int mMenuHeight;
	
	private FrameLayout mHome;
	private ImageView mImgSearch;
	private TextView mTvUpdate;
	
	private int mOffsetY = 0;
	

	public WandowCoverMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.cover_menu_layout, this);
		initView();
		
		mOffsetY = dip2px(getContext(), 2);
	}

	private void initView() {
		mImgApp = (ImageView)findViewById(R.id.img_app);
		mImgGame = (ImageView)findViewById(R.id.img_game);
		mImgVideo = (ImageView)findViewById(R.id.img_video);
		mImgMore = (ImageView)findViewById(R.id.img_more);	
		
		mHome = (FrameLayout)findViewById(R.id.home);
		mImgSearch = (ImageView)findViewById(R.id.img_search);
		mTvUpdate = (TextView)findViewById(R.id.update);
		playAnimAlpha(this,0f,0f,1f);
		
    }
	
	public void initPosition(){
		playAnimY(mImgApp,0f, getBottom() - mOffsetY,mImgApp.getTop());
		playAnimY(mImgGame,0f, getBottom() - mOffsetY,mImgGame.getTop());
		playAnimY(mImgVideo,0f, getBottom() - mOffsetY,mImgVideo.getTop());
		playAnimY(mImgMore,0f, getBottom() - mOffsetY,mImgMore.getTop());
		playAnimX(mHome,0f,-mHome.getWidth(),0);
		playAnimX(mImgSearch,0,mTvUpdate.getRight(),mImgSearch.getLeft());
		playAnimX(mTvUpdate,0,mImgSearch.getWidth() + mTvUpdate.getRight(),mTvUpdate.getLeft());
	}

	public WandowCoverMenu(Context context) {
		this(context, null);
	}

	public void onScroll(int currentTop, int bottomMenuTop) {
		int top = -currentTop;
		
		int alphaLimit = mMenuHeight*2/10;
		if(top > alphaLimit){
			float alphaPercent = (float)(top - alphaLimit)/alphaLimit;
			if(alphaPercent >= 0f || alphaPercent <= 1f){
				playAnimAlpha(this,alphaPercent,0f,1f);
			}
		}else{
			playAnimAlpha(this,0,0f,1f);
		}
		
		int yLimit = bottomMenuTop - getHeight();
		if(top > yLimit){
			float yPercent = (float)(top - yLimit)/(mMenuHeight*147/1000);
			if(yPercent >= 0f || yPercent <= 1f){
				playAnimY(mImgApp,yPercent, getBottom() - mOffsetY,mImgApp.getTop());
				playAnimY(mImgGame,yPercent, getBottom() - mOffsetY,mImgGame.getTop());
				playAnimY(mImgVideo,yPercent, getBottom() - mOffsetY,mImgVideo.getTop());
				playAnimY(mImgMore,yPercent, getBottom() - mOffsetY,mImgMore.getTop());
			}
		}else{
			playAnimY(mImgApp,0, getBottom() - mOffsetY,mImgApp.getTop());
			playAnimY(mImgGame,0, getBottom() - mOffsetY,mImgGame.getTop());
			playAnimY(mImgVideo,0, getBottom() - mOffsetY,mImgVideo.getTop());
			playAnimY(mImgMore,0, getBottom() - mOffsetY,mImgMore.getTop());
		}
		
		int xLimit = bottomMenuTop - getHeight() - 100;
		if(top > xLimit){
			float xPercent = (float)(top - xLimit)/xLimit;
			if(xPercent >= 0f || xPercent <= 1f){
				playAnimX(mHome,xPercent,-mHome.getWidth(),0);
				playAnimX(mImgSearch,xPercent,mTvUpdate.getRight(),mImgSearch.getLeft());
				playAnimX(mTvUpdate,xPercent,mImgSearch.getWidth() + mTvUpdate.getRight(),mTvUpdate.getLeft());
			}
		}else{
			playAnimX(mHome,0,-mHome.getWidth(),0);
			playAnimX(mImgSearch,0,mTvUpdate.getRight(),mImgSearch.getLeft());
			playAnimX(mTvUpdate,0,mImgSearch.getWidth() + mTvUpdate.getRight(),mTvUpdate.getLeft());
		}
		
    }
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	
	private void playAnimY(View v, float percent, int from, int to) {
		ObjectAnimator transeYAnimator = ObjectAnimator.ofFloat(v, "y", from, to);
		transeYAnimator.setDuration(5000);
		transeYAnimator.setCurrentPlayTime((long) (percent * 5000));
	}
	
	private void playAnimX(View v, float percent, int from, int to) {
		ObjectAnimator transeYAnimator = ObjectAnimator.ofFloat(v, "x", from, to);
		transeYAnimator.setDuration(5000);
		transeYAnimator.setCurrentPlayTime((long) (percent * 5000));
	}
	
	private void playAnimAlpha(View v, float percent, float from, float to) {
		ObjectAnimator transeYAnimator = ObjectAnimator.ofFloat(v, "alpha", from, to);
		transeYAnimator.setDuration(5000);
		transeYAnimator.setCurrentPlayTime((long) (percent * 5000));
	}
	
	public void setMenuHeight(int height){
		mMenuHeight = height;
	}

	

}
