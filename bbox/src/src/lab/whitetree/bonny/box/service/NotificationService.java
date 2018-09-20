package lab.whitetree.bonny.box.service;

import java.util.ArrayList;
import java.util.HashSet;

import lab.whitetree.bonny.box.Main;
import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.storage.LocalStorage;

import org.whitetree.bidget.moom.MoomMaster;
import org.whitetree.systable.data.MoomCellData;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.widget.RemoteViews;

public class NotificationService extends Service {
	
	public static final int NOTIFICATION_MOOM_VIEW_COUNT = 4; // 通知栏就显示4个moomview
	
	private static final String INTENT_ACTION_REFRESH = "lab.whitetree.bonny.box.ACTION_NOTIFICATION_REFRESH";
	private static final String INTENT_ACTION_MOOM_CONFIG_CHANGED = "lab.whitetree.bonny.box.ACTION_MOOM_CONFIG_CHANGED";
	
	private static final int NOTIFICATION_ID = 20121223;
	
	private NotificationManager mNManager = null;
	private BroadcastReceiver mBroadcastReceiver = null;
	private HashSet<String> mCHIDSet = null;
	
	// default value
	private String[] mCHIDS = null; 
	private String[] mMoomConfigUrl = null;
	
	public static void start(Context context) {
		if (LocalStorage.getInstance().isNotificationBarEnable()) {
			Intent intent = new Intent();
			intent.setClass(context, NotificationService.class);
			context.startService(intent);
		}
	}

	public static void refresh(Context context) {
		Intent intent = new Intent(INTENT_ACTION_REFRESH);
    	context.sendBroadcast(intent);
	}
	
	public static void disable(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, NotificationService.class);
		context.stopService(intent);
	}
	
	public static void moomConfigChanged(Context context) {
		Intent intent = new Intent(INTENT_ACTION_MOOM_CONFIG_CHANGED);
    	context.sendBroadcast(intent);
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		IntentFilter filter = new IntentFilter();
	    filter.addAction(INTENT_ACTION_REFRESH);
	    filter.addAction(INTENT_ACTION_MOOM_CONFIG_CHANGED);
	    
	    mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				if (INTENT_ACTION_REFRESH.equals(arg1.getAction())) {
					refreshNotificationBar();
				}
				else if (INTENT_ACTION_MOOM_CONFIG_CHANGED.equals(arg1.getAction())) {
					loadAndSubscribeMoomConfigData();
				}
			}
		};
		
	    registerReceiver(mBroadcastReceiver, filter);
		
		mNManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		//初始化时就使用默认的moomview
//		initNotificationBar();

		loadAndSubscribeMoomConfigData();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if (mBroadcastReceiver != null) {
			this.unregisterReceiver(mBroadcastReceiver);
		}
		LocalService.startUnsubscribe(this, mCHIDS);
		if (mNManager != null) {
			mNManager.cancel(NOTIFICATION_ID);
		}
	}
	
	@SuppressWarnings("deprecation")
	private synchronized void refreshNotificationBar() {
		if (mMoomConfigUrl == null || mMoomConfigUrl.length != NOTIFICATION_MOOM_VIEW_COUNT) {
			return;
		}
		
		Bitmap bm = null;

		Notification notification = new Notification(R.drawable.x, null, System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.notification_bar_layout);

		bm = MoomMaster.getInstance().getBitmap(this, mMoomConfigUrl[0], 100, 100);
		views.setImageViewBitmap(R.id.moom_view_1, bm);

		bm = MoomMaster.getInstance().getBitmap(this, mMoomConfigUrl[1], 100, 100);
		views.setImageViewBitmap(R.id.moom_view_2, bm);

		bm = MoomMaster.getInstance().getBitmap(this, mMoomConfigUrl[2], 100, 100);
		views.setImageViewBitmap(R.id.moom_view_3, bm);

		bm = MoomMaster.getInstance().getBitmap(this, mMoomConfigUrl[3], 100, 100);
		views.setImageViewBitmap(R.id.moom_view_4, bm);
		
		notification.contentView = views; 
 
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, Main.getLaunchIntent(this), 0); 
    	notification.contentIntent = contentIntent;

    	if (mNManager != null) {
    		mNManager.notify(NOTIFICATION_ID, notification);
    	}
    	
    	views = null;
    	notification = null;
    	bm.recycle();
    	bm = null;
		//System.gc();
	}
	
	@SuppressWarnings({ "unused", "deprecation" })
	private void initNotificationBar() {
		Notification notification = new Notification(R.drawable.x, null, System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_INSISTENT;

		RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification_bar_layout);
		remoteViews.setImageViewResource(R.id.moom_view_1, R.drawable.x);
		remoteViews.setImageViewResource(R.id.moom_view_2, R.drawable.x);
		remoteViews.setImageViewResource(R.id.moom_view_3, R.drawable.x);
		remoteViews.setImageViewResource(R.id.moom_view_4, R.drawable.x);
		
		notification.contentView = remoteViews; 
 
    	Intent notificationIntent = Main.getLaunchIntent(this); 
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0); 
    	notification.contentIntent = contentIntent;

    	if (mNManager != null) {
    		mNManager.notify(NOTIFICATION_ID, notification);
    	}
	}
	
	private void loadAndSubscribeMoomConfigData() {
		if (mCHIDS != null) {
			LocalService.startUnsubscribe(getApplicationContext(), mCHIDS);
		}
		
		if (mMoomConfigUrl == null) {
			mMoomConfigUrl = new String[NOTIFICATION_MOOM_VIEW_COUNT];
		}
		
		ArrayList<MoomCellData> list = LocalStorage.getInstance().getNotificationMoomData();
		if (list != null && list.size() == NOTIFICATION_MOOM_VIEW_COUNT) {
			if (mCHIDSet == null) {
				mCHIDSet = new HashSet<String>();
			}
			for (int i = 0; i < list.size(); ++i) {
				mCHIDSet.add(list.get(i).mMoomCHID);
				mMoomConfigUrl[i] = list.get(i).mMoomViewCfg;
			}
			mCHIDS = mCHIDSet.toArray(new String[mCHIDSet.size()]);
		}
		
		LocalService.startSubscribe(getApplicationContext(), mCHIDS);

		refreshNotificationBar();
	}
}
