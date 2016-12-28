package com.example.testfloatwindow;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FloatService extends Service {

	WindowManager mWm = null;
	View mView;
	RelativeLayout mImView;
	TextView mTvMem;
	TextView mTvHigh;
	private float mTouchStartX;
	private float mTouchStartY;
	private float mX;
	private float mY;
	private float mMoveX;
	private float mMoveY;
	private int mScreenWidth;
	private int mScreenHeight;
	private boolean mIsUserMove = false;
	private int mLastDrawableRes = 0;

	private float mDx = 3;
	private static final int DSLEEP = 10;
	private boolean mIsShowing = false;
	
	private static long TOTAL_MEMORY = 0;
	
	private static int MEM_FRESH_TIME = 1000;
	
	
	private WindowManager.LayoutParams mWmParams = new WindowManager.LayoutParams();
	
	public static final String ACTION_SHOW_FLOAT = "com.cleanmaster_mguard_cn_ACTION_SHOW_FLOAT";
	
	private static final int UPDATE_FLOAT_POSITION = 0;
	private static final int UPDATE_FLOAT_VIEW = 1;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mView = LayoutInflater.from(this).inflate(R.layout.floating, null);
		mImView = (RelativeLayout)mView.findViewById(R.id.float_image);
		mTvMem = (TextView)mView.findViewById(R.id.tv_float_mem);
		mTvHigh = (TextView)mView.findViewById(R.id.tv_float_high);
		createView();
		showSelf();
		
	}
	
	
	private void createView() {
		mWm = (WindowManager) getApplicationContext().getSystemService("window");

		Display display = mWm.getDefaultDisplay();
		mScreenWidth = display.getWidth();
		mScreenHeight = display.getHeight();

		mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		mWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		mWmParams.gravity = Gravity.LEFT | Gravity.TOP;
		mWmParams.x = mView.getWidth() / 2;
		mWmParams.y = display.getHeight()/2;
		mWmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWmParams.format = PixelFormat.RGBA_8888;

		mWm.addView(mView, mWmParams);

		mView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				mX = event.getRawX();
				mY = event.getRawY() - 25;
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mTouchStartX = event.getX();
					mTouchStartY = event.getY() + mView.getHeight() / 2;
					break;
				case MotionEvent.ACTION_MOVE:
					mMoveX = event.getX();
					mMoveY = event.getY() + mView.getHeight() / 2;
					updateViewPosition(true);
					break;
				case MotionEvent.ACTION_UP:
					if (mIsUserMove) {
						reset();
					} else {
//						HideSelf(true);
					}
					break;
				}
				return true;
			}
		});
		
		updateFloatView();
	}
	
	public Set<String> getCurrentLauncherPackages() {
		Set<String> packages = new HashSet<String>();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> res = getPackageManager().queryIntentActivities(intent, 0);

	    for (ResolveInfo activity : res) {
	        if (!packages.contains(activity.activityInfo.packageName)) {
	        	packages.add(activity.activityInfo.packageName);
	        }
	    }
	    return packages;
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
			String action = intent.getAction();
			if(!TextUtils.isEmpty(action)){
				if(ACTION_SHOW_FLOAT.equals(action)){
					if(mImView.getVisibility() != View.VISIBLE){
						showSelf();
					}
				}
			}
		}
	    return super.onStartCommand(intent, flags, startId);
	}
	
	public static Intent getStartIntent(Context context){
		Intent intent = new Intent();
		intent.setClass(context, FloatService.class);
		return intent;
	}
	
	private void showSelf() {
		mIsShowing = true;
		float from = 0f;
		float to = 0f;
		if(mWmParams.x > mScreenWidth/2){
			from = 1f;
			to = 0f;
		}else{
			from = -1f;
			to = 0f;
		}
		TranslateAnimation transAnim = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF,from,TranslateAnimation.RELATIVE_TO_SELF,to,
						TranslateAnimation.RELATIVE_TO_SELF,0f,TranslateAnimation.RELATIVE_TO_SELF,0f);
		transAnim.setDuration(300);
		mImView.setAnimation(transAnim);
		mImView.setVisibility(View.VISIBLE);
		mHandler.sendEmptyMessage(UPDATE_FLOAT_VIEW);
    }

	private void changeDrawable(int res) {
		if(res != mLastDrawableRes){
			mTvMem.setVisibility(View.INVISIBLE);
			mImView.setBackgroundResource(res);
			mLastDrawableRes = res;
		}
	}
	
	public void onConfigurationChanged(android.content.res.Configuration newConfig) {

		float heightDao = (float) mWmParams.y / mScreenHeight;

		if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			mScreenWidth = mWm.getDefaultDisplay().getHeight();
			mScreenHeight = mWm.getDefaultDisplay().getWidth();
		} else {
			mScreenWidth = mWm.getDefaultDisplay().getWidth();
			mScreenHeight = mWm.getDefaultDisplay().getHeight();
		}

		mWmParams.y = (int) (mScreenHeight * heightDao);

		reset();
	};

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_FLOAT_POSITION:
				updateViewPosition(false);
				break;
			case UPDATE_FLOAT_VIEW:
				updateFloatView();
				removeMessages(UPDATE_FLOAT_VIEW);
				sendEmptyMessageDelayed(UPDATE_FLOAT_VIEW, MEM_FRESH_TIME);
				setLastSendTime();
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * Get free memory
	 * 
	 * @param context
	 * @return byte of free memory
	 */
	public static long getAvailMem(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		activityManager.getMemoryInfo(mi);
		return mi.availMem;
	}
	
	public static long getTotalMem() {
		if (TOTAL_MEMORY == 0) {
			byte[] mBuffer = new byte[1024];
			try {
				FileInputStream is = new FileInputStream("/proc/meminfo");
				int len = is.read(mBuffer);
				is.close();
				for (int i = 0; i < len; i++) {
					if (matchText(mBuffer, i, "MemTotal")) {
						i += 7;
						TOTAL_MEMORY = extractMemValue(mBuffer, i);
						break;
					}
				}
			} catch (java.io.FileNotFoundException e) {
			} catch (java.io.IOException e) {
			}
		}
		return TOTAL_MEMORY;
	}
	
	public static long extractMemValue(byte[] buffer, int index) {
		while (index < buffer.length && buffer[index] != '\n') {
			if (buffer[index] >= '0' && buffer[index] <= '9') {
				int start = index;
				index++;
				while (index < buffer.length && buffer[index] >= '0' && buffer[index] <= '9') {
					index++;
				}
				String str = new String(buffer, 0, start, index - start);
				return ((long) Integer.parseInt(str)) * 1024;
			}
			index++;
		}
		return 0;
	}

	public static boolean matchText(byte[] buffer, int index, String text) {
		int N = text.length();
		if ((index + N) >= buffer.length) {
			return false;
		}
		for (int i = 0; i < N; i++) {
			if (buffer[index + i] != text.charAt(i)) {
				return false;
			}
		}
		return true;
	}
	
	private void updateFloatView() {
//		long total = getTotalMem();
//		long used = total - getAvailMem(this);
//		int percent = (int) (((float) used / total) * 100);
		if(mTvMem.getVisibility() != View.VISIBLE){
			mTvMem.setVisibility(View.VISIBLE);
		}
		int data[] = getLastSendTime();
		if(data[0] > (MEM_FRESH_TIME - 1)){
			mTvMem.setText(String.valueOf(data[0]));
			mTvHigh.setText(String.valueOf(data[1]));
		}

		if (mWmParams.x > mScreenWidth / 2) {
			if (data[0] > MEM_FRESH_TIME + 10) {
				mImView.setBackgroundResource(R.drawable.floatingwindown_small_right_red);
				mLastDrawableRes = R.drawable.floatingwindown_small_right_red;
			} else {
				mImView.setBackgroundResource(R.drawable.floatingwindown_small_right_normal);
				mLastDrawableRes = R.drawable.floatingwindown_small_right_normal;
			}
			mTvMem.setGravity(Gravity.LEFT| Gravity.CENTER_VERTICAL);
			mImView.setPadding(dip2px(getBaseContext(), 23), 0, 0, 0);
		} else {
			if (data[0] > MEM_FRESH_TIME + 10) {
				mImView.setBackgroundResource(R.drawable.floatingwindown_small_left_red);
				mLastDrawableRes = R.drawable.floatingwindown_small_left_red;
			} else {
				mImView.setBackgroundResource(R.drawable.floatingwindown_small_left_normal);
				mLastDrawableRes = R.drawable.floatingwindown_small_left_normal;
			}
			mTvMem.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			mImView.setPadding(dip2px(getBaseContext(), 5), 0, 0, 0);
		}
		
		
	};
	
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }
    
    public static int px2dip(Context context, float pxValue){ 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(pxValue / scale + 0.5f); 
} 

	private void reset() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				float offset = 0;
				if (mX > mScreenWidth / 2) {
					offset = (mScreenWidth - mView.getWidth() / 2) - mX;
					while (offset > 0) {
						mDx = getDx(offset);
						offset -= mDx;
						if (offset < 0) {
							offset = 0;
						}
						mX = (mScreenWidth - mView.getWidth() / 2) - offset;
						try {
							Thread.sleep(DSLEEP);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						mHandler.sendEmptyMessage(UPDATE_FLOAT_POSITION);
					}
				} else {
					offset = mX;
					while (offset > 0) {
						mDx = getDx(offset);
						offset -= mDx;
						if (offset < 0) {
							offset = 0;
						}
						mX = offset;
						try {
							Thread.sleep(DSLEEP);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						mHandler.sendEmptyMessage(UPDATE_FLOAT_POSITION);
					}
				}
				mHandler.sendEmptyMessage(UPDATE_FLOAT_VIEW);
				mTouchStartX = 0;
			}

			private float getDx(float offset) {
				int divide = mScreenWidth / 2 / 3;
				if (offset > 2 * divide) {
					return divide/4;
				} else if (offset > divide) {
					return divide/7;
				} else if (offset < divide) {
					return divide/9;
				}
				return 1;
			}
		}).start();
	}

	private void updateViewPosition(boolean isTouchMove) {
		if (!isTouchMove || Math.abs(mMoveX - mTouchStartX) > 20 || Math.abs(mMoveY - mTouchStartY) > 20) {
			mWmParams.x = (int) (mX - mTouchStartX);
			mWmParams.y = (int) (mY - mTouchStartY);
			mWm.updateViewLayout(mView, mWmParams);
			mIsUserMove = isTouchMove;
			if(mIsUserMove){
				changeDrawable(R.drawable.about_logo);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private int[] getLastSendTime(){
		SharedPreferences p = getSharedPreferences("misc", Context.MODE_PRIVATE);
		String v = p.getString(":last_time1", "");
		int high = MEM_FRESH_TIME;
		long currentTime = System.currentTimeMillis();
		long last = currentTime;
		if(!TextUtils.isEmpty(v)){
			high = Integer.parseInt(v.split(",")[0]);
			last = Long.parseLong(v.split(",")[1]);
		}
		int current = (int)(currentTime - last);
		if(current > high){
			high = current;
			p.edit().putString(":last_time1", high+","+last).commit();
		}
		int[] dd = new int[2];
		dd[0] = current;
		dd[1] = high;
		return dd;
	}
	
	private void setLastSendTime(){
		SharedPreferences p = getSharedPreferences("misc", Context.MODE_PRIVATE);
		String v = p.getString(":last_time1", "");
		int high = MEM_FRESH_TIME;
		long last = System.currentTimeMillis();
		if(!TextUtils.isEmpty(v)){
			high = Integer.parseInt(v.split(",")[0]);
		}
		p.edit().putString(":last_time1", high+","+last).commit();
	}
	
}
