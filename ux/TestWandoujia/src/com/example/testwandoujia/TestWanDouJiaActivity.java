package com.example.testwandoujia;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class TestWanDouJiaActivity extends ActionBarActivity {

	private ListView mListView;
	private TestAdapter mListAdapter;
	private WandoujiaHead mHead;
	private WandowCoverMenu mCoverMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_test_wan_dou_jia);
		initView();
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.list);
		mCoverMenu = (WandowCoverMenu) findViewById(R.id.cover_menu);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (null != mHead) {
					mHead.onScroll();
				}
				
				if (null != mCoverMenu) {
					if (null != mHead) {
						mCoverMenu.onScroll(mHead.getTop(),mHead.mBottomMenu.getTop());
					}
				}
			}
		});
		onAddHeaderOrBottom();
		setAdatper();
	}

	private void setAdatper() {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < 30; i++) {
			list.add(String.valueOf(System.currentTimeMillis()));
		}
		mListAdapter = new TestAdapter(this, list);
		mListView.setAdapter(mListAdapter);
	}

	private void onAddHeaderOrBottom() {
		mHead = new WandoujiaHead(this) {
			@Override
			protected int getAppLeft() {
				if (null != mCoverMenu) {
					return mCoverMenu.mImgApp.getLeft();
				}
				return super.getAppLeft();
			}

			@Override
			protected int getGameLeft() {
				if (null != mCoverMenu) {
					return mCoverMenu.mImgGame.getLeft();
				}
				return super.getGameLeft();
			}

			@Override
			protected int getVideoLeft() {
				if (null != mCoverMenu) {
					return mCoverMenu.mImgVideo.getLeft();
				}
				return super.getVideoLeft();
			}

			@Override
			protected int getMoreLeft() {
				if (null != mCoverMenu) {
					return mCoverMenu.mImgMore.getLeft();
				}
				return super.getMoreLeft();
			}
		};
		
		mCoverMenu.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				if(mCoverMenu.getHeight() > 0){
					mCoverMenu.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					mCoverMenu.initPosition();
				}
			}
		});
		
		mHead.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				if(mHead.getHeight() > 0){
					mHead.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					if(mCoverMenu != null){
						mCoverMenu.setMenuHeight(mHead.getHeight());
					}
				}
			}
		});
		mListView.addHeaderView(mHead);
	}

}
