package lab.whitetree.bonny.box.ui.fragments;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;

import org.whitetree.bidget.CellLayout;
import org.whitetree.bidget.OilBottleView;
import org.whitetree.bidget.moom.MoomView;
import org.whitetree.systable.system.SystemChangedEvent;
import org.whitetree.systable.system.watcher.PowerWatcher;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BatteryFragment extends Fragment {
	
	MoomView mVoltage = null;
	MoomView mTemp = null;
	OilBottleView mBattery = null;
	TextView mTextBoard = null;
	String mPluginType = null;
	String mHealth = null;
	
	ImageView _IvPlugged = null;
	CellLayout mPanel = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return initView(inflater, container);
	}

	private View initView(LayoutInflater inflater, ViewGroup container) {
		if (inflater == null) {
			return null;
		}
		
		View root = inflater.inflate(R.layout.fragment_battery_layout, container, true);
		mVoltage = (MoomView) root.findViewById(R.id.power_voltage);
		mTemp = (MoomView) root.findViewById(R.id.power_temp);
		mBattery = (OilBottleView) root.findViewById(R.id.image);
		mTextBoard = (TextView) root.findViewById(R.id.level);
		
		mPanel = (CellLayout) root.findViewById(R.id.signal);
		mPanel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			}
		});
		
		return root;
	}
	
	public void getSystemChangeEvent(Context context, SystemChangedEvent event) {
		if (event == null) {
			return;
		}
		
		if (event.id.equals(":power")) {
			mVoltage.invalidate();
			mTemp.invalidate();
			int level = (Integer)(event.walue.get(PowerWatcher.WALUE_POWER_LEVEL));
			mBattery.setPercent(level);
			mTextBoard.setText(String.format("%d%%", level));
			
			// 接入类型
			// TODO: 隐藏。。。。
			mPluginType = (String)(event.walue.get(PowerWatcher.WALUE_POWER_PLUGGED));
			mPanel.onlySelectChild("plugged/"+mPluginType);
			
			// 健康状态
			mHealth = (String)(event.walue.get(PowerWatcher.WALUE_POWER_HEALTH));
			mPanel.onlySelectChild("health/"+mHealth);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void getSensorChangedEvent(Context context, SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			float z = event.values[2];
			mBattery.adjust((int) z);
		}
	}

	public void onClickOverheat(View view) {
		mkToast(view.getContext(), R.string.overheat);
	}
	
	public void onClickOvervoltage(View view) {
		mkToast(view.getContext(), R.string.overv);
	}
	
	public void onClickDead(View view) {
		mkToast(view.getContext(), R.string.dead);
	}
	
	public void onClickUnknown(View view) {
		mkToast(view.getContext(), R.string.unknown_error);
	}
	
	public void onClickGood(View view) {
		mkToast(view.getContext(), R.string.good);
	}
	
	public void onClickAc(View view) {
		mkToast(view.getContext(), R.string.ac);
	}
	
	public void onClickUsb(View view) {
		mkToast(view.getContext(), R.string.usb);
	}
	
	public void onClickAcUsb(View view) {
		mkToast(view.getContext(), R.string.acusb);
	}
	
	public void onClickUnplugged(View view) {
		mkToast(view.getContext(), R.string.unplugged);
	}
	
	private void mkToast(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	public void onClick(View view) {
	}

}
