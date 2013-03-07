package org.whitetree.sched.log;

import com.fanxer.midian.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class LogMessageAdapter extends CursorAdapter {
	private LayoutInflater mInflater;

	public LogMessageAdapter(Context context, Cursor c) {
		super(context, c);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public void bindView(View view, final Context context, Cursor cursor) {
		TextView tvMesg = (TextView) view.findViewById(R.id.tv_1);
		TextView tvTime = (TextView) view.findViewById(R.id.tv_2);
		
		String mesg = cursor.getString(cursor.getColumnIndexOrThrow(WtLog.MESG));
		String time = cursor.getString(cursor.getColumnIndexOrThrow(WtLog.TIME));
		
		tvMesg.setText(mesg);
		tvTime.setText(time);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.log_msg_item, parent, false);
	}
}
