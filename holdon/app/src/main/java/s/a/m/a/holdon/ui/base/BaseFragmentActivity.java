package s.a.m.a.holdon.ui.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import client.core.Core;
import client.core.model.Event;
import client.core.model.EventListener;
import s.a.m.a.holdon.R;

/**
 * Created by amas on 15-5-30.
 */
public class BaseFragmentActivity extends FragmentActivity implements EventListener {
    private View mTitleView;
    private TextView mTitle = null;
    private Button mTitleRightButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Core.I().addListener("ui", this);
    }

    public void initTitle() {
        if (mTitleView == null) {
            mTitleView = findViewById(R.id.title);
            mTitle = (TextView) mTitleView.findViewById(R.id.title_bar_title);
            mTitleRightButton = (Button) mTitleView.findViewById(R.id.title_bar_right_button);
        }
    }

    public void setTitle(int title) {
        initTitle();
        mTitle.setText(title);
    }

    public void setTitle(CharSequence title) {
        initTitle();
        mTitle.setText(title);
    }

    public void setTitleRightButtonText(CharSequence text) {
        if (mTitleRightButton != null) {
            mTitleRightButton.setVisibility(View.VISIBLE);
            mTitleRightButton.setText(text);
        }
    }
     /**
	 * <p>
	 * A default handler, subclass may overrides onMessage() to implement custom
	 * logic
	 * </p>
	 */
	protected Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!onMessage(msg)) {
				super.handleMessage(msg);
			}
		}
	};

	/**
	 * <p>
	 * Invoked when a message is arrived
	 * </p>
	 *
	 * @param msg
	 *            Message object
	 * @return true if the message is processed, or false if you want deliver
	 *         the message to super class
	 */
	protected boolean onMessage(Message msg) {
		return false;
	}

	/**
	 * Notice: Do not update UI in this method, since it not be called on UI
	 * thread!!!
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
	 * You can update UI in this method safely, since it will be called on UI
	 * thread only.
	 *
	 * @param event
	 */
	protected void onEventInUiThread(Event event) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Core.I().removeListener("ui", this);
	}
}
