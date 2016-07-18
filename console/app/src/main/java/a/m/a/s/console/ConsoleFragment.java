package a.m.a.s.console;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import s.a.m.a.apidemos.BuildConfig;
import s.a.m.a.apidemos.R;


public class ConsoleFragment extends Fragment implements Console.MessageListener {
    public static ConsoleFragment newInstance() {
        return new ConsoleFragment();
    }

    ListView mListView = null;
    ConsoleMessageAdapter adapter = null;
    Button btn_send = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.onews__fragment_console, container, false);
        mListView = (ListView) root.findViewById(R.id.list);
        mListView.setAdapter(adapter);
        btn_send = (Button) root.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @Override
            public void onClick(View v) {
                Console.getInstance().write(""+(i++));
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter = new ConsoleMessageAdapter(context, Console.getInstance().getAllMessages());
        Console.getInstance().setMessageListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Console.getInstance().removeMessageListener(this);
        super.onDestroy();
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            if(adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onNewMessage(final Console.ConsoleMessage message) {
        // 非主线程
        Activity activity = getActivity();
        if(activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.addItem(message, _getUserVisibleHint());
                    scrollToLast();
                }
            });
        }
    }

    protected void scrollToLast() {
        if(_getUserVisibleHint() && mListView != null) {
            mListView.setSelection(adapter.getCount());
        }
    }

    public boolean _getUserVisibleHint() {
        return getUserVisibleHint();
    }
}
