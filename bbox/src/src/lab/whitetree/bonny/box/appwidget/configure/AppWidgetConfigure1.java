package lab.whitetree.bonny.box.appwidget.configure;

import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.storage.LocalStorage;
import lab.whitetree.bonny.box.ui.MoomViewGalleryActivity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AppWidgetConfigure1 extends MoomViewGalleryActivity {
	private int mAppWidgetId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (!intent.hasExtra(EXTRA_KEY_SOURCE_TYPE)) {
			intent.putExtra(EXTRA_KEY_SOURCE_TYPE, VIEW_SOURCE_TYPE_1X1_WIDGET);
		}
		if (!intent.hasExtra(EXTRA_KEY_CHANNEL_ID)) {
			intent.putExtra(EXTRA_KEY_CHANNEL_ID, LocalService.CHID_MEMORY);
		}

		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}
		super.onCreate(savedInstanceState);
	}
	
	public void onClickSave(View view) {
		super.onClickSave(view);

		// 保存Moom url
		LocalStorage.getInstance().saveWidgetMoomUrl(mAppWidgetId, mMoomConfigFileUrl);
		// 保存 频道
		LocalStorage.getInstance().saveWidgetChid(mAppWidgetId, mChannelID);
		
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}
}
