package client.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import client.core.Core;
import client.core.model.Event;
import client.core.model.EventListener;


/**
 * Base activity will register listener to "lgroup://ui" group 
 * @author zhanggn
 */
public class BaseCloudFragmentActivity extends FragmentActivity implements EventListener{
	
	/**
	 * <p>A default handler, subclass may overrides onMessage() to implement
	 * custom logic</p>
	 */
	protected Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(!onMessage(msg)) {
				super.handleMessage(msg);
			}
		}
	};
	
	/**
	 * <p>Invoked when a message is arrived</p>
	 * 
	 * @param msg Message object
	 * @return true if the message is processed, or false if you want
	 * 		deliver the message to super class
	 */
	protected boolean onMessage(Message msg) {
		return false;
	}
	
	/**
	 * Notice: 
     * Do not update UI in this method, since it not be called on UI thread!!!
	 */
	public final void onEvent(final Event event) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {		
			public void run() {
				onEventInUiThread(event);	
			}
		});
	}
	
	/**
	 * You can update UI in this method safely, since it will be called on UI thread only.
	 * @param event
	 */
	protected void onEventInUiThread(Event event) {
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Core.I().addListener("ui", this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Core.I().removeListener("ui", this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Core.I().addListener("ui", this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Core.I().removeListener("ui", this);
	}
}

