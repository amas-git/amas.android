package lab.whitetree.bonny.box.ui;

import lab.whitetree.bonny.box.DEV;
import android.view.Window;
import client.ui.BaseCloudListActivity;

import com.umeng.analytics.MobclickAgent;

public class BaseListActivity extends BaseCloudListActivity {
	
	@Override
	public void setContentView(int layoutResID) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(layoutResID);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (DEV.ENABLE_ADMOB) {
			try {
				// umeng
				MobclickAgent.onResume(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onPause() {
		super.onPause();

		if (DEV.ENABLE_ADMOB) {
			try {
				// uemng
				MobclickAgent.onPause(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
