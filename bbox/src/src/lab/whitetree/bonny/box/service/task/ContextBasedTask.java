package lab.whitetree.bonny.box.service.task;

import android.content.Context;
import client.core.model.Task;

public class ContextBasedTask extends Task {
	protected Context mContext = null;
	
	public ContextBasedTask(Context context) {
		mContext = context;
	}
}
