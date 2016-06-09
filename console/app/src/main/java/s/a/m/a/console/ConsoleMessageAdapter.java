package s.a.m.a.console;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import s.a.m.a.cs.CircularBuffer;

/**
 * Created by amas on 3/30/16.
 */
public class ConsoleMessageAdapter extends BaseAdapter {
    private Context mContext = null;
    private LayoutInflater mInflater = null;
    CircularBuffer<Console.ConsoleMessage> messages = new CircularBuffer<>(0);

    public ConsoleMessageAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ConsoleMessageAdapter(Context context, CircularBuffer<Console.ConsoleMessage> messages) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages == null ? 0 : messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 追加一条消息
     * @param message 消息
     * @param sync 是否刷新界面
     */
    public void addItem(Console.ConsoleMessage message, boolean sync) {
        messages.put(message);
        if(sync) {
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.adapter_consolemessageadapter, null);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Console.ConsoleMessage item = (Console.ConsoleMessage) getItem(position);
        holder.message.setText(item.message);
        //holder.message.setText();
        return convertView;
    }

    static class ViewHolder {
        TextView time = null;
        TextView message = null;
    }

// Need list item un clickable ???
//      public boolean areAllItemsEnabled() {
//              return true;
//      }
//
//      public boolean isEnabled(int position) {
//              return false;
//      }


}
