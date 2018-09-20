package lab.whitetree.bonny.box.appwidget;

import java.util.ArrayList;
import java.util.HashSet;

import lab.whitetree.bonny.box.LOG;
import lab.whitetree.bonny.box.storage.LocalStorage;
import lab.whitetree.bonny.box.util.CustomMoomUIUtil;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class BaseWidgetProvider extends AppWidgetProvider {
	
	protected final HashSet<String> mCHIDS = new HashSet<String>();
	
	@Override
	public void onEnabled(Context context) {
		LOG._("WIDGET", "=============== onEnabled ==============");
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		LOG._("WIDGET", "=============== onDisabled ==============");
		super.onDisabled(context);
	}
	
	protected ArrayList<Integer> addToWidgetIdList(ArrayList<Integer> list, int[] ids) {
		if (ids != null && ids.length > 0) {
			if (list == null) {
				list = new ArrayList<Integer>();
			}
			for (int id : ids) {
				list.add(Integer.valueOf(id));
			}
		}
		return list;
	}
	
	protected static Intent getWidgetPendingIntent(Context context, int widgetId) {
		String widgetChid = LocalStorage.getInstance().getWidgetChid(widgetId);
		return CustomMoomUIUtil.getIntentByMoomCHID(context, widgetChid);
	}
	
}
