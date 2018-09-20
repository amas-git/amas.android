package lab.whitetree.bonny.box.util;

import java.util.ArrayList;

import lab.whitetree.bonny.box.Main;
import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.ui.BatteryActivity;
import lab.whitetree.bonny.box.ui.CpuActivity;
import lab.whitetree.bonny.box.ui.DiskActivity;
import lab.whitetree.bonny.box.ui.NetworkInfoActivity;
import lab.whitetree.bonny.box.ui.TaskList;
import lab.whitetree.bonny.box.ui.UninstallerActivity;

import org.whitetree.systable.data.MoomCellData;
import org.whitetree.systable.system.U;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/*
 * 所有跟动态调整moom位置，属性相关的操作都放这里，便于后续添加、维护
 * 目前包括 MainPage 和 Notification Bar 的用户定制
 */
public class CustomMoomUIUtil {

	// 定义在Main page上 7个位置的MoomCell跳转时对应的动画效果
	// 即使再添加新的 CHID，也不需要改动这里
	/*
	 * 六个MoomCell的位置 0～1 | 0 | |1|2|3| |4|5|6|
	 */
	public static final String MAIN_ACTIVITY_MOOM_CELL_TAG_0 = "MoomCellLayout_0";
	public static final String MAIN_ACTIVITY_MOOM_CELL_TAG_1 = "MoomCellLayout_1";
	public static final String MAIN_ACTIVITY_MOOM_CELL_TAG_2 = "MoomCellLayout_2";
	public static final String MAIN_ACTIVITY_MOOM_CELL_TAG_3 = "MoomCellLayout_3";
	public static final String MAIN_ACTIVITY_MOOM_CELL_TAG_4 = "MoomCellLayout_4";
	public static final String MAIN_ACTIVITY_MOOM_CELL_TAG_5 = "MoomCellLayout_5";
	public static final String MAIN_ACTIVITY_MOOM_CELL_TAG_6 = "MoomCellLayout_6";

	public static ArrayList<String> getAllMoomCellTags() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(MAIN_ACTIVITY_MOOM_CELL_TAG_0);
		list.add(MAIN_ACTIVITY_MOOM_CELL_TAG_1);
		list.add(MAIN_ACTIVITY_MOOM_CELL_TAG_2);
		list.add(MAIN_ACTIVITY_MOOM_CELL_TAG_3);
		list.add(MAIN_ACTIVITY_MOOM_CELL_TAG_4);
		list.add(MAIN_ACTIVITY_MOOM_CELL_TAG_5);
		list.add(MAIN_ACTIVITY_MOOM_CELL_TAG_6);
		return list;
	}

	public static void startActivityAnimWithMoomCellTag(Activity activity,
			String moomCellTag, boolean isFromMoomCell) {
		if (MAIN_ACTIVITY_MOOM_CELL_TAG_0.equals(moomCellTag)) {
			if (isFromMoomCell)
				activity.overridePendingTransition(R.anim.in_from_top,
						R.anim.out_to_bottom);
			else
				activity.overridePendingTransition(R.anim.in_from_bottom,
						R.anim.out_to_top);
		} else if (MAIN_ACTIVITY_MOOM_CELL_TAG_1.equals(moomCellTag)) {
			if (isFromMoomCell)
				activity.overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			else
				activity.overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
		} else if (MAIN_ACTIVITY_MOOM_CELL_TAG_2.equals(moomCellTag)) {
			// 从MoomCell过来和回到MoomCell的动画相同
			activity.overridePendingTransition(R.anim.in_from_inside,
					R.anim.out_to_inside);
		} else if (MAIN_ACTIVITY_MOOM_CELL_TAG_3.equals(moomCellTag)) {
			if (isFromMoomCell)
				activity.overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			else
				activity.overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
		} else if (MAIN_ACTIVITY_MOOM_CELL_TAG_4.equals(moomCellTag)) {
			if (isFromMoomCell)
				activity.overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			else
				activity.overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
		} else if (MAIN_ACTIVITY_MOOM_CELL_TAG_5.equals(moomCellTag)) {
			if (isFromMoomCell)
				activity.overridePendingTransition(R.anim.in_from_bottom,
						R.anim.out_to_top);
			else
				activity.overridePendingTransition(R.anim.in_from_top,
						R.anim.out_to_bottom);
		} else if (MAIN_ACTIVITY_MOOM_CELL_TAG_6.equals(moomCellTag)) {
			if (isFromMoomCell)
				activity.overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			else
				activity.overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
		}
	}

	// 根据Moom的CHID，查找对应的页面Intent
	public static Intent getIntentByMoomCHID(Context context, String chid) {
		return getIntentByMoomCHIDandMoomCellTag(context, chid, null);
	}

	// TODO 如果新加了频道，需要更新这里
	public static Intent getIntentByMoomCHIDandMoomCellTag(Context context,
			String chid, String moomCellTag) {
		if (LocalService.CHID_CPU.equals(chid)) {
			return CpuActivity.getLaunchIntentWithMoomCellTag(context,
					moomCellTag);
		} else if (LocalService.CHID_MEMORY.equals(chid)) {
			return TaskList
					.getLaunchIntentWithMoomCellTag(context, moomCellTag);
		} else if (LocalService.CHID_POWER.equals(chid)) {
			return BatteryActivity.getLaunchIntentWithMoomCellTag(context,
					moomCellTag);
		} else if (LocalService.CHID_STORAGE_SDCARD.equals(chid)) {
			return DiskActivity.getLaunchIntentWithMoomCellTag(context,
					moomCellTag);
		} else if (LocalService.CHID_APPS.equals(chid)) {
			return UninstallerActivity.getLaunchIntentWithMoomCellTag(context,
					moomCellTag);
		} else if (LocalService.CHID_NETWORKS.equals(chid)) {
			return NetworkInfoActivity.getLaunchIntentWithMoomCellTag(context,
					moomCellTag);
		}

		// for default intent
		return Main.getLaunchIntent(context);
	}

	// 针对开关类的MoomCell，直接在这里调用对应方法
	public static void callFuncByMoomCHID(Context context, String chid) {
		if (LocalService.CHID_WIFI.equals(chid)) {
			U.changeWIFIStatus(context);
			return;
		}
	}

	// Main page 中用户可选的所有 Moom 频道。
	// TODO 如果新加了频道，需要更新这里
	private final static ArrayList<MoomCellData> MOOM_CELL_CHOOSE_LIST = new ArrayList<MoomCellData>();
	static {
		// 使用MoomView的CHID作为MoomView的tag
		// cpu
		MOOM_CELL_CHOOSE_LIST.add(new MoomCellData(null,
				"res:///moom_cpu_total_usage", 
				LocalService.CHID_CPU,
				LocalService.CHID_CPU, 
				R.string.desc_func_cpu,
				MoomCellData.MOOM_CELL_TYPE_ENTRANCE));
		// sdcard
		MOOM_CELL_CHOOSE_LIST.add(new MoomCellData(null,
				"res:///moom_sdcard_default", 
				LocalService.CHID_STORAGE_SDCARD,
				LocalService.CHID_STORAGE_SDCARD, 
				R.string.desc_func_sdcard,
				MoomCellData.MOOM_CELL_TYPE_ENTRANCE));
		// battery
		MOOM_CELL_CHOOSE_LIST.add(new MoomCellData(null,
				"res:///moom_battery_default", 
				LocalService.CHID_POWER,
				LocalService.CHID_POWER, 
				R.string.desc_func_power,
				MoomCellData.MOOM_CELL_TYPE_ENTRANCE));
		// apps
		MOOM_CELL_CHOOSE_LIST.add(new MoomCellData(null,
				"res:///moom_userapps", 
				LocalService.CHID_APPS,
				LocalService.CHID_APPS, 
				R.string.desc_func_apps,
				MoomCellData.MOOM_CELL_TYPE_ENTRANCE));
		// memory
		MOOM_CELL_CHOOSE_LIST.add(new MoomCellData(null,
				"res:///moom_mem_default", 
				LocalService.CHID_MEMORY,
				LocalService.CHID_MEMORY, 
				R.string.desc_func_memory,
				MoomCellData.MOOM_CELL_TYPE_ENTRANCE));
		// network
		MOOM_CELL_CHOOSE_LIST.add(new MoomCellData(null,
				"res:///moom_networks", 
				LocalService.CHID_NETWORKS,
				LocalService.CHID_NETWORKS, 
				R.string.desc_func_network,
				MoomCellData.MOOM_CELL_TYPE_ENTRANCE));

		// wifi
		MOOM_CELL_CHOOSE_LIST.add(new MoomCellData(null, 
				"res:///moom_wifi",
				LocalService.CHID_WIFI, 
				LocalService.CHID_WIFI,
				R.string.desc_func_wifi, 
				MoomCellData.MOOM_CELL_TYPE_SWITCH));
	}

	public static ArrayList<MoomCellData> getMoomCellChannelList() {
		return MOOM_CELL_CHOOSE_LIST;
	}

	// 在自定义 NOTIFICATION BAR 时，所有可供选择的 moom 类型
	// 通知栏或者widget的可选列表和上面 MOOM_CELL_CHOOSE_LIST的区别是
	// 可能有些频道不需要添加在 Widget或者通知栏上面，所有就没有那些东西，比如 app，这个就没有
	private static final ArrayList<MoomCellData> NOTIFICATION_BAR_CHOOSE_MOOM_LIST = new ArrayList<MoomCellData>();
	static {
		NOTIFICATION_BAR_CHOOSE_MOOM_LIST.add(new MoomCellData(
				LocalService.CHID_CPU, "res:///moom_notification_cpu",
				LocalService.CHID_CPU, LocalService.CHID_CPU,
				R.string.notif_cpu_desc));
		NOTIFICATION_BAR_CHOOSE_MOOM_LIST.add(new MoomCellData(
				LocalService.CHID_MEMORY, "res:///moom_notification_mem",
				LocalService.CHID_MEMORY, LocalService.CHID_MEMORY,
				R.string.notif_memory_desc));
		NOTIFICATION_BAR_CHOOSE_MOOM_LIST.add(new MoomCellData(
				LocalService.CHID_STORAGE_SDCARD,
				"res:///moom_notification_sdcard",
				LocalService.CHID_STORAGE_SDCARD,
				LocalService.CHID_STORAGE_SDCARD, R.string.notif_disk_desc));
		NOTIFICATION_BAR_CHOOSE_MOOM_LIST.add(new MoomCellData(
				LocalService.CHID_POWER, "res:///moom_notification_battery",
				LocalService.CHID_POWER, LocalService.CHID_POWER,
				R.string.notif_cell_desc));
		NOTIFICATION_BAR_CHOOSE_MOOM_LIST.add(new MoomCellData(
				LocalService.CHID_NETWORKS, "res:///moom_notification_network",
				LocalService.CHID_NETWORKS, LocalService.CHID_NETWORKS,
				R.string.notif_network_desc)); // TODO network的config需要完善
												// 现在用的是cpu的
	}

	public static ArrayList<MoomCellData> getNotifyBarChooseMoomList() {
		return NOTIFICATION_BAR_CHOOSE_MOOM_LIST;
	}

	// 返回NOTIFICATION_BAR_CHOOSE_MOOM_LIST中所有的 CHID
	public static String[] getNotifyBarChooseCHIDs() {
		String[] chids = new String[NOTIFICATION_BAR_CHOOSE_MOOM_LIST.size()];
		for (int i = 0; i < NOTIFICATION_BAR_CHOOSE_MOOM_LIST.size(); ++i) {
			chids[i] = NOTIFICATION_BAR_CHOOSE_MOOM_LIST.get(i).mMoomCHID;
		}
		return chids;
	}
}
