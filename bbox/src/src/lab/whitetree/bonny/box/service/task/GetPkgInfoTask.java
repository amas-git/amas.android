package lab.whitetree.bonny.box.service.task;

import java.util.ArrayList;

import org.whitetree.systable.data.PkgInfo;
import org.whitetree.systable.system.U;

import android.content.Context;
import client.core.model.Event;

public class GetPkgInfoTask extends ContextBasedTask {
	private ArrayList<PkgInfo> mPkgInfoList = null;

	public class EsGetPkgInfoTask extends Event {
		public ArrayList<PkgInfo> getPkgInfos() {
			return mPkgInfoList;
		}
	}

	public GetPkgInfoTask(Context context) {
		super(context);
	}

	@Override
	protected Event process() {
		mPkgInfoList = U.getInstalledApps(mContext, false);
		return new EsGetPkgInfoTask();
	}
}
