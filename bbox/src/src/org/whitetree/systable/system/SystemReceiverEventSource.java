package org.whitetree.systable.system;

import client.core.Core;
import client.core.model.Notifiers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class SystemReceiverEventSource extends BroadcastReceiver implements IEventSource {
	IntentFilter mIntentFileter = null;
	SystemChangedEvent mLastEvent = null;
	String mId = null;
	Context mContext = null;
	private Notifiers mEventTo = null;

	public void setEventTo(Notifiers eventTo) {
		mEventTo = eventTo;
	}

	public SystemReceiverEventSource(Context context, IntentFilter intentFileter) {
		mContext = context;
		mIntentFileter = intentFileter;
	}

	protected void setId(String id) {
		mId = id;
	}
	
	public String getId() {
		return mId;
	}

	@Override
	public SystemChangedEvent getEvent(String key, boolean forceQuery, String filterType) {
		return mLastEvent;
	}

	public void setLastEvent(SystemChangedEvent event) {
		mLastEvent = event;
	}

	@Override
	public void onCreate() {
		mContext.registerReceiver(this, mIntentFileter);
	}

	@Override
	public void onDestroy() {
		mContext.unregisterReceiver(this);
		
		// clear stuff
		mContext = null;
		mEventTo = null;
		mLastEvent = null;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		SystemChangedEvent event = onHandleIntent(context, intent);
		if (event != null) {
			setLastEvent(event);
			send(onHandleIntent(context, intent));
		}
	}

	private void send(SystemChangedEvent event) {
		if (event != null) {
			event.setTo(mEventTo);
			Core.I().push(event);
		}
	}

	public SystemChangedEvent onHandleIntent(Context context, Intent intent) {
		return null;
	}

	@Override
	public void subscribe(String key) {
		mContext.registerReceiver(this, mIntentFileter);
	}

	@Override
	public void unsubscribe(String key) {
		mContext.unregisterReceiver(this);
	}
}