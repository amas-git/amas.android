package lab.whitetree.bonny.box.appwidget;

import java.util.ArrayList;

import lab.whitetree.bonny.box.LOG;
import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.storage.LocalStorage;

import org.whitetree.bidget.moom.MoomMaster;
import org.whitetree.bidget.moom.MoomParser;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.RemoteViews;

public class AppWidgetProvider1 extends BaseWidgetProvider {
	protected static ComponentName SELF = new ComponentName("lab.whitetree.bonny.box", "lab.whitetree.bonny.box.appwidget.AppWidgetProvider1");
	public static final int ON_CLICK = 1;
	public static final int W = MoomParser.DP_TO_PIX(72); // 此值必须与layout中的宽度一致(dp)
	public static final int H = MoomParser.DP_TO_PIX(72);

	private static ArrayList<Integer> sAppWidget1Ids = null;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		synchronized (this) {
			sAppWidget1Ids = addToWidgetIdList(sAppWidget1Ids, appWidgetIds);
		}
		
		updateWidget(context);
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);

		for (int widgetId : appWidgetIds) {
			String chid = LocalStorage.getInstance().getWidgetChid(widgetId);
			String[] chids = new String[] {chid};
			// 注消widget需要的频道
			LocalService.startUnsubscribe(context, chids);
			LocalStorage.getInstance().setWidgetsSubscribeChids(chids, false);
			LocalStorage.getInstance().clearWidgetCfg(widgetId);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		LOG._("WIDGET", "intent=" + intent);
		super.onReceive(context, intent);

		if ("android.appwidget.action.APPWIDGET_CHANGE_DEFAULT".equals(intent.getAction())) {
			updateWidget(context);
		}

		if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
			Uri data = intent.getData();
			int id = Integer.parseInt(data.getSchemeSpecificPart());
			switch (id) {
			case ON_CLICK:
				LOG._("WIDGET", "=============== ON CLICK ==============");
				break;
			default:
				break;
			}
		}
	}
	

	private static ArrayList<Integer> sTemp1Ids = null;
	public synchronized static void updateWidget(Context context) {
		try {
			if (sAppWidget1Ids == null) {
				return;
			}
			sTemp1Ids = (ArrayList<Integer>) sAppWidget1Ids.clone();

			final AppWidgetManager gm = AppWidgetManager.getInstance(context);
			for (int widgetId : sTemp1Ids) {
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_1);
				if (gm == null || SELF == null || views == null) {
					return;
				}
				Bitmap bm = null;
				String url = LocalStorage.getInstance().getWidgetMoomUrl(widgetId);
				if (!TextUtils.isEmpty(url)) {
					bm = MoomMaster.getInstance().getBitmap(context, url, W, H);
					views.setImageViewBitmap(R.id.moom_widget, bm);
					
				} else {
					views.setImageViewResource(R.id.moom_widget, R.drawable.not_avail);
				}

				Intent pandingIntent = getWidgetPendingIntent(context, widgetId);
				views.setOnClickPendingIntent(R.id.moom_widget,
						PendingIntent.getActivity(context, 0, pandingIntent, 0));
				gm.updateAppWidget(widgetId, views);

				if (bm != null) {
					bm.recycle();
					bm = null;
				}
				views = null;
			}
			sTemp1Ids.clear();
		} catch (Exception e) {
		} finally {
			sTemp1Ids = null;
		}
	}
}
