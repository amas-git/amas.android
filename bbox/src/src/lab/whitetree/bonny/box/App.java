package lab.whitetree.bonny.box;

import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.service.NotificationService;
import lab.whitetree.bonny.box.storage.LocalStorage;

import org.whitetree.bidget.moom.MoomParser;
import org.whitetree.systable.system.SystemTable;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		//CPPAgent.hideAdWallDefaultAppsOfOtherCompany(true);
		//CPPAgent.init(getApplicationContext());

		SystemTable.getInstance().setContext(this);
		LocalStorage.getInstance().init(this);

		LocalService.startDefault(this);
		// TODO: 合并启动项,减少Intent
		LocalService.startSubscribe(this, new String[] { LocalService.CHID_APPS, LocalService.CHID_PROC_RUNNING, LocalService.CHID_NETWORKS });
		LocalService.startQuery(this, new String[] { LocalService.CHID_APPS });
		LocalService.startWidgetsSubscribe(this);

		NotificationService.start(this);

		registerScreenActionReceiver();
		
		addMacro();
	}

	private void addMacro() {
		MoomParser.addExprMacro("%MEM_PERCENT", "(percent :memory.used.percent)");
		MoomParser.addExprMacro("%MEM_USED", "(format :memory.used.percent %)");
		MoomParser.addExprMacro("%MEM_USEDT", "(formatb :memory.used)");
		MoomParser.addExprMacro("%MEM_TOTAL", "(formatb :memory.TOTAL)");
		MoomParser.addExprMacro("%MEM_FREE", "(formatb :memory.free)");
		MoomParser.addExprMacro("%MEM_FREEP", "(format :memory.free.percent %)");

		// 电池
		MoomParser.addExprMacro("%POW_LP", "(percent :power.level)");
		MoomParser.addExprMacro("%POW_LT", "(format :power.level %)");
		MoomParser.addExprMacro("%POW_VT", "(format :power.voltage.v V)");
		MoomParser.addExprMacro("%POW_TT","(format :power.temperature)");
		MoomParser.addExprMacro("%POW_LT_VALUE", "(format :power.level)");
		
		// CPU
		MoomParser.addExprMacro("%CPU_UP","(percent :cpu.usage.total)");
		MoomParser.addExprMacro("%CPU_UPT","(format :cpu.usage.total %)");
		MoomParser.addExprMacro("%CPU_FC","(format :cpu.cur.freq.formatted Hz)");
		MoomParser.addExprMacro("%CPU_CN","(format :cpu.count)");
		MoomParser.addExprMacro("%CPU_UPT_VALUE","(format :cpu.usage.total)");
		
		MoomParser.addExprMacro("%CPU0_UP","(percent :cpu.usage.cpu0)");
		MoomParser.addExprMacro("%CPU1_UP","(percent :cpu.usage.cpu1)");
		MoomParser.addExprMacro("%CPU2_UP","(percent :cpu.usage.cpu2)");
		MoomParser.addExprMacro("%CPU3_UP","(percent :cpu.usage.cpu3)");
		
		// 存储
		MoomParser.addExprMacro("%DISK_UP", "(percent :space.used.percent)");
		MoomParser.addExprMacro("%DISK_UPT","(format :space.used.percent %)");
		MoomParser.addExprMacro("%DISK_UUT", "(formatb :space.used)");
		MoomParser.addExprMacro("%DISK_UTT", "(formatb :space)");
		MoomParser.addExprMacro("%DISK_UPT_VALUE","(format :space.used.percent)");
		
		// WIFI states
		MoomParser.addExprMacro("%RSSI_LP", "(percent :wifi.rssi.level.percent)");
	    
    }

	private void registerScreenActionReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);

		BroadcastReceiver screenActionReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(final Context context, final Intent intent) {
				if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction()) || Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
					LocalService.startScreenStatusChanged(context);
				}
			}
		};
		registerReceiver(screenActionReceiver, filter);
	}
}
