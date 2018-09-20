package lab.whitetree.bonny.box.ui.fragments;

import java.util.Iterator;
import java.util.Map;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.ui.NetworkInfoBoard;
import lab.whitetree.bonny.box.ui.NetworkInfoBoard.BeamText;

import org.whitetree.systable.system.ISystemChanged;
import org.whitetree.systable.system.SystemChangedEvent;
import org.whitetree.systable.system.watcher.NetworkWatcher;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NetworkInfoFragment extends Fragment implements ISystemChanged {
	TextView mTvMac     = null;
	TextView mTvLocalIp = null;
	TextView mTvExtIp   = null;
	
	NetworkInfoBoard mBoard = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return initView(inflater, container);
	}

	private View initView(LayoutInflater inflater, ViewGroup container) {
		if (inflater == null) {
			return null;
		}
	     
		View root = inflater.inflate(R.layout.fragment_networkinfo_layout, container, true);
		mBoard = (NetworkInfoBoard) root.findViewById(R.id.board);
		final Context context = root.getContext();
		
		float baseline = 0.25f;
		BeamText b = new BeamText(context, mBoard);
		b.setBackgroundResource(R.drawable.paper_l);
		b.setLayoutParam(-30f, 0.1f, 1.0f, 10);
		b.setHAdjusment(0.1f);
		b.setDisableText(true);
		mBoard.installBoard("", b);

		
		b = new BeamText(context, mBoard);
		b.setLayoutParam(-30f, 0.1f, 1.0f, 52);
		b.setHAdjusment(-0.2f);
		b.setBackgroundResource(R.drawable.paper_l);
		b.label = "IP";
		b.setContentPaddingBottom(18);
		b.setLabelPaddingBottom(18);
		mBoard.installBoard(NetworkWatcher.WALUE_NETWORK_LOCAL_IP, b);
		
		b = new BeamText(context, mBoard);
		b.setLayoutParam(-30f, 0.1f, 1.0f, 30);
		b.setHAdjusment(-0.45f);
		b.label = "子网掩码";
		b.setContentPaddingBottom(12);
		b.setLabelPaddingBottom(12);
		b.setBackgroundResource(R.drawable.paper_l);
		mBoard.installBoard(NetworkWatcher.WALUE_SUBNETMASK, b);
		
		b = new BeamText(context, mBoard);
		b.setBackgroundResource(R.drawable.paper_l);
		b.setLayoutParam(-30f, 0.1f, 1.0f, 10);
		b.setHAdjusment(-0.1f);
		b.setDisableText(true);
		mBoard.installBoard("---------", b);
		
		b = new BeamText(context, mBoard);
		b.setBackgroundResource(R.drawable.paper_l);
		b.setLayoutParam(-30f, 0.1f, 1.0f, 32);
		b.setHAdjusment(0.2f);
		b.setContentPaddingBottom(12);
		b.setLabelPaddingBottom(12);
		b.setGravity(0);
		b.label = "DNS1";
		b.setTextOffset(40);
		
		mBoard.installBoard(NetworkWatcher.WALUE_NETWORK_DNS1, b);
		

		b = new BeamText(context, mBoard);
		b.setBackgroundResource(R.drawable.paper_l);
		b.setLayoutParam(-30f, 0.1f, 1.0f, 32);
		b.setHAdjusment(0.2f);
		b.setContentPaddingBottom(12);
		b.setLabelPaddingBottom(12);
		b.setGravity(0);
		b.label = "DNS2";
		b.setTextOffset(58);
		mBoard.installBoard(NetworkWatcher.WALUE_NETWORK_DNS2, b);
		//mBoard.installBoard(NetworkWatcher.WALUE_SUBNETMASK, b);


		b = new BeamText(context, mBoard);
		b.setBackgroundResource(R.drawable.paper_l);
		b.setLayoutParam(-30f, 0.1f, 1.0f, 10);
		b.setHAdjusment(-0.7f);
		b.setDisableText(true);
		mBoard.installBoard("---------", b);
		
		b = new BeamText(context, mBoard);
		b.setLayoutParam(-30f, 0.1f, 1.0f, 40);
		b.setHAdjusment(-0.4f);
		b.label = "网关";
		b.setBackgroundResource(R.drawable.paper_l);
		mBoard.installBoard(NetworkWatcher.WALUE_GATEWAY, b);



//		b = new BeamText(context, mBoard);
//		b.setLayoutParam(-30f, 0.1f, 1.0f, 48);
//		b.setHAdjusment(-0.2f);
//		b.setBackgroundResource(R.drawable.paper_l);
//		b.label = "IP过期时间";
//		mBoard.installBoard(NetworkWatcher.WALUE_LEASETIME, b);
		
		b = new BeamText(context, mBoard);
		b.setLayoutParam(-30f, 0.1f, 1.0f, 48);
		b.setHAdjusment(0.3f);
		b.setGravity(0);
		b.label = "MAC";
		b.setBackgroundResource(R.drawable.paper_l);
		mBoard.installBoard(NetworkWatcher.WALUE_NETWORK_MAC, b);
		return root;
	}


	@Override
	public void onSystemChanged(SystemChangedEvent event) {
//		if(NetworkWatcher.CHID_NETWORKS.equals(event.id)) {
//            Iterator<Map.Entry<String, Object>> iter = event.walue.entrySet().iterator();
//            while(iter.hasNext()) {
//                Map.Entry<String, Object> entry = (Map.Entry<String, Object>)iter.next(); 
//                String         k = (String)entry.getKey();
//                String         v = String.valueOf(entry.getValue());
//                if(k.startsWith(":network")) {
//                	try {
//                		mBoard.setBoardContent(k, v);
//                	} catch (Exception e) {
//						e.printStackTrace();
//					}
//                }
//            }
//		}
	}
	
	
}
