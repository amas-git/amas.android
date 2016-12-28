package s.a.m.a.holdon.ui;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.view.*;
import android.widget.*;

import s.a.m.a.holdon.HTask;
import s.a.m.a.holdon.R;
import s.a.m.a.holdon.ui.widget.SimpleHTaskView;
import s.a.m.a.service.LocalService;

public class HTaskListAdapter extends BaseAdapter {
    private ArrayList<HTask> mData = null;
    private Context mContext = null;
    private LayoutInflater mInflater = null;
    public static final boolean DEBUG = true;

    public HTaskListAdapter(Context context, ArrayList<HTask> data) {
        mContext = context;
        mData = data;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.adapter_htasklist, null);
            holder.taskview = (SimpleHTaskView) convertView.findViewById(R.id.taskview);
            holder.debug_msg = (TextView) convertView.findViewById(R.id.debug_msg);
//            holder.progress = (TextView) convertView.findViewById(R.id.progress);
//            holder.number = (TextView) convertView.findViewById(R.id.number);
            holder.taskview.setCTRL(new SimpleHTaskView.CTRL(){
                int p = 10;
                @Override
                public void onCheckIn(SimpleHTaskView v, HTask hTask) {
                    //Toast.makeText(getContext(),"Helllo",2000).show();
                    v.setHtProgress(p+=10);
                    LocalService.start_ACTION_CHECK_IN(getContext(), hTask);
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final HTask item = (HTask) getItem(position);
        holder.taskview.setHTask(item);
        if(DEBUG) {
            holder.debug_msg.setText(item.toString());
        }

        //holder.title.setText(item.toJSONObject().toString());
        //holder.progress.setText(item.getProgress());
        //holder.number.setText(item.getNumber());
        return convertView;
    }

    public void update(HTask hTask) {
        int i = -1;
        int j = 0;
        for(HTask x : mData) {
            if(x._ID() == hTask._ID()) {
                System.out.println("update : :::::: " + hTask);
                i = j;
                break;
            }
            ++j;
        }

        if(i >= 0) {
            mData.set(i, hTask);
            notifyDataSetChanged();
        }
    }

    public void add(HTask htask) {
        mData.add(htask);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        public SimpleHTaskView taskview = null;
        public TextView debug_msg;
    }

}
