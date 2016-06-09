package com.cmcm.terminal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cmcm.onews.R;
import com.cmcm.onews.event.ONewsEvent;
import com.cmcm.onews.fragment.NewsBaseFragment;

public class ConsoleFragment extends NewsBaseFragment {
    public static ConsoleFragment newInstance() {
        return new ConsoleFragment();
    }

    ListView mListView = null;
    ConsoleMessageAdapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.onews__fragment_console, container, false);
        mListView = (ListView) root.findViewById(R.id.list);
        return root;
    }

    protected ConsoleMessageAdapter getConsoleMessageAdapter() {
        Context c = getContext();
        if(c != null && adapter == null) {
            adapter = new ConsoleMessageAdapter(c, Console.getInstance().getAllMessages());
        }
        return adapter;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onEventInUiThread(ONewsEvent event) {
        super.onEventInUiThread(event);
        if(adapter == null) {
            mListView.setAdapter(getConsoleMessageAdapter());
        } else {
            Console.Message message = null;
            if(event instanceof EventConsoleMessage) {
                message = ((EventConsoleMessage) event).message();
            } else {
                message = new Console.Message();
                message.message = event.toString();
            }
            adapter.addItem(message, isVisibleToUser);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            if(adapter != null) {
                adapter.notifyDataSetInvalidated();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
