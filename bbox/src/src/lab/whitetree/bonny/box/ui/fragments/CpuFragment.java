package lab.whitetree.bonny.box.ui.fragments;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.storage.LocalStorage;
import lab.whitetree.bonny.box.ui.CpuTestPerformanceActivity;

import org.whitetree.bidget.moom.MoomView;
import org.whitetree.systable.system.SystemChangedEvent;
import org.whitetree.systable.system.U;
import org.whitetree.systable.system.watcher.CpuWatcher;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CpuFragment extends Fragment {

	private static boolean sTempSensorExist = false;
	private static int sCpuCount = 1;
	
	private ViewGroup mLayout = null;
	private TextView mtvCpuName = null;
	private TextView mtvCpuMaxFreq = null;
	private TextView mtvCpuMinFreq = null;
	private TextView mtvCpuCurFreq = null;
	private Button mbtTestCpu = null;
	
	private MoomView mmvTotalCpuUsage = null;
	private MoomView mmvSubCpu0Usage = null;
	private MoomView mmvSubCpu1Usage = null;
	private MoomView mmvSubCpu2Usage = null;
	private MoomView mmvSubCpu3Usage = null;
	private MoomView mmvCurCpuFre = null;
	
	private LinearLayout mSubCpuLayout = null;
	private LinearLayout mSubCpuLayout2 = null;
	private LinearLayout mSubCpuLayout3 = null;
	
	private boolean mHasInitCpuStaticInfo = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return initView(inflater, container);
	}

	@SuppressWarnings("deprecation")
	private View initView(LayoutInflater inflater, ViewGroup container) {
		if (inflater == null) {
			return null;
		}
		
		View root = inflater.inflate(R.layout.fragment_cpu_layout, container, true);
		final Context context = root.getContext();
		
		sCpuCount = U.getCpuCount();
		SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		sTempSensorExist = sm.getDefaultSensor(Sensor.TYPE_TEMPERATURE) != null;
		
		loadMoom(context, LocalService.CHID_SENSOR_TEMP);

		mLayout = (ViewGroup) root.findViewById(R.id.layout);

		mSubCpuLayout = (LinearLayout) root.findViewById(R.id.sub_cpu_layout);
		mSubCpuLayout2 = (LinearLayout) root.findViewById(R.id.layout_2);
		mSubCpuLayout3 = (LinearLayout) root.findViewById(R.id.layout_3);
		if (sCpuCount < 2) {
			mSubCpuLayout.setVisibility(View.GONE);
		} else if (sCpuCount < 3) {
			mSubCpuLayout2.setVisibility(View.GONE);
			mSubCpuLayout3.setVisibility(View.GONE);
		}
		mtvCpuName = (TextView) root.findViewById(R.id.cpu_name);
		mtvCpuMaxFreq = (TextView) root.findViewById(R.id.cpu_max_freq_value);
		mtvCpuMinFreq = (TextView) root.findViewById(R.id.cpu_min_freq_value);
		mtvCpuCurFreq = (TextView) root.findViewById(R.id.cpu_cur_freq_value);
		
		mbtTestCpu = (Button) root.findViewById(R.id.cpu_test);
		mbtTestCpu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CpuTestPerformanceActivity.startDefault(context);				
			}
		});
		
		mmvTotalCpuUsage = (MoomView) root.findViewById(R.id.cpu_totaluse);
		mmvSubCpu0Usage = (MoomView) root.findViewById(R.id.cpu_usage_0);
		mmvSubCpu1Usage = (MoomView) root.findViewById(R.id.cpu_usage_1);
		mmvSubCpu2Usage = (MoomView) root.findViewById(R.id.cpu_usage_2);
		mmvSubCpu3Usage = (MoomView) root.findViewById(R.id.cpu_usage_3);
		mmvCurCpuFre = (MoomView) root.findViewById(R.id.cpu_cur_frequence);
		
		return root;
	}
	
	public void getSystemChangeEvent(Context context, SystemChangedEvent event) {
		if (event == null) {
			return;
		}
		
		String tag = event.id;
		if (sTempSensorExist && LocalService.CHID_SENSOR_TEMP.equals(tag)) {
			View child = mLayout.findViewWithTag(tag);
			if (child != null) {
				child.invalidate();
			}
		} else if (LocalService.CHID_CPU.equals(tag)) {

			mmvTotalCpuUsage.invalidate();
			mmvCurCpuFre.invalidate();
			mtvCpuCurFreq.setText(CpuWatcher.getFormattedCurrentFreq(event.walue)+ "Hz");
			if (sCpuCount >= 3) {
				mmvSubCpu0Usage.invalidate();
				mmvSubCpu1Usage.invalidate();
				mmvSubCpu2Usage.invalidate();
				mmvSubCpu3Usage.invalidate();
			} else if (sCpuCount > 1) {
				mmvSubCpu0Usage.invalidate();
				mmvSubCpu1Usage.invalidate();
			}

			if (!mHasInitCpuStaticInfo) {
				mtvCpuName.setText("CPU: " + CpuWatcher.getCpuName(event.walue));
				mtvCpuMaxFreq.setText(CpuWatcher.getFormattedMaxFreq(event.walue) + "Hz");
				mtvCpuMinFreq.setText(CpuWatcher.getFormattedMinFreq(event.walue) + "Hz");
				mHasInitCpuStaticInfo = true;
			}
		}
	}

	private void loadMoom(Context context, String key) {
		if (context == null) {
			return;
		}
		
		String url = LocalStorage.getInstance().getMoom(key);
		if(TextUtils.isEmpty(url)) {
			return;
		}
		
		MoomView moom = (MoomView) mLayout.findViewWithTag(key);
		if(moom != null) {
			moom.loadConfigure(context, url);
			moom.invalidate();
		}
	}
}
