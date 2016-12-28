package com.example.testwandoujia;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;

public class WandoujiaHead extends RelativeLayout {

	private TextView mTvApp;
	private RelativeLayout mImgApp;
	private TextView mTvGmae;
	private RelativeLayout mImgGame;
	private TextView mTvVideo;
	private RelativeLayout mImgVideo;
	private TextView mTvBook;
	private RelativeLayout mImgBook;
	private TextView mTvMore;
	private RelativeLayout mImgMore;
	
	private ImageView mApp;

	public LinearLayout mBottomMenu;
	
	
	
	int mLeft60;
	int mLeft50;
	int mBaseWidth;

	public WandoujiaHead(Context context) {
		this(context, null);
	}

	public WandoujiaHead(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.wandow_head_layout, this);
		initView();
		mLeft60 = dip2px(context, 60);
		mLeft50 = dip2px(context, 50);
	}

	private void initView() {
		mTvApp = (TextView) findViewById(R.id.tv_app);
		mTvGmae = (TextView) findViewById(R.id.tv_game);
		mTvVideo = (TextView) findViewById(R.id.tv_video);
		mTvBook = (TextView) findViewById(R.id.tv_book);
		mTvMore = (TextView) findViewById(R.id.tv_more);
		mImgApp = (RelativeLayout) findViewById(R.id.layout_app);
		mImgGame = (RelativeLayout) findViewById(R.id.layout_game);
		mImgVideo = (RelativeLayout) findViewById(R.id.layout_video);
		mImgBook = (RelativeLayout) findViewById(R.id.layout_book);
		mImgMore = (RelativeLayout) findViewById(R.id.layout_more);
		
		mApp = (ImageView)findViewById(R.id.img_app);

		mBottomMenu = (LinearLayout) findViewById(R.id.bottom_menu);
	}

	public void onScroll() {
		int top = -getTop();
		getBaseWidth();
		
		int alphaLimit = getHeight()*2/10;
		if(top > alphaLimit){
			float alphaPercent = (float)(top - alphaLimit)/(alphaLimit/2);
			if(alphaPercent >= 0f || alphaPercent <= 1f){
				playAnimAlpha(mTvApp,alphaPercent,1f,0f);
				playAnimAlpha(mTvGmae,alphaPercent,1f,0f);
				playAnimAlpha(mTvVideo,alphaPercent,1f,0f);
				playAnimAlpha(mTvBook,alphaPercent,1f,0f);
				playAnimAlpha(mTvMore,alphaPercent,1f,0f);
			}
		}else{
			playAnimAlpha(mTvApp,0,1f,0f);
			playAnimAlpha(mTvGmae,0,1f,0f);
			playAnimAlpha(mTvVideo,0,1f,0f);
			playAnimAlpha(mTvBook,0,1f,0f);
			playAnimAlpha(mTvMore,0,1f,0f);
		}
		
		int xLimit = getHeight()*3/10;
		int bottomMenu = mBottomMenu.getTop();
		int last = bottomMenu - mLeft50;
		if(top > xLimit){
			float xPercent = (float)(top - xLimit)/(last - xLimit);
			if(xPercent >= 0f || xPercent <= 1f){
				playAnimX(mImgApp,xPercent,mImgApp.getLeft(),getAppLeft());
				playAnimX(mImgGame,xPercent,mImgGame.getLeft(),getGameLeft());
				playAnimX(mImgVideo,xPercent,mImgVideo.getLeft(),getVideoLeft());
				playAnimX(mImgBook,xPercent,mImgBook.getLeft(),getMoreLeft());
				playAnimX(mImgMore,xPercent,mImgMore.getLeft(),getMoreLeft());
			}
		}else{
			playAnimX(mImgApp,0,mImgApp.getLeft(),getAppLeft());
			playAnimX(mImgGame,0,mImgGame.getLeft(),getGameLeft());
			playAnimX(mImgVideo,0,mImgVideo.getLeft(),getVideoLeft());
			playAnimX(mImgBook,0,mImgBook.getLeft(),getMoreLeft());
			playAnimX(mImgMore,0,mImgMore.getLeft(),getMoreLeft());
		}
	}

	private void getBaseWidth() {
		if(mApp.getWidth() > 0){
			mBaseWidth = mApp.getLeft() - mImgApp.getLeft();
		}
    }
	
	private void playAnimAlpha(View v, float percent, float from, float to) {
		ObjectAnimator transeYAnimator = ObjectAnimator.ofFloat(v, "alpha", from, to);
		transeYAnimator.setDuration(5000);
		transeYAnimator.setCurrentPlayTime((long) (percent * 5000));
	}

	private void playAnimX(View v, float percent, int from, int to) {
		ObjectAnimator transeYAnimator = ObjectAnimator.ofFloat(v, "x", from, to + mLeft60 - mBaseWidth);
		transeYAnimator.setDuration(5000);
		transeYAnimator.setCurrentPlayTime((long) (percent * 5000));
	}

	protected int getAppLeft() {
		return 0;
	}

	protected int getGameLeft() {
		return 0;
	}

	protected int getVideoLeft() {
		return 0;
	}

	protected int getMoreLeft() {
		return 0;
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
