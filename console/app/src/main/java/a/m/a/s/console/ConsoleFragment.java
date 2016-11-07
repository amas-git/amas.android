package a.m.a.s.console;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import s.a.m.a.apidemos.R;


public class ConsoleFragment extends Fragment implements Console.MessageListener {


    public static ConsoleFragment newInstance(Console console) {
        ConsoleFragment c = new ConsoleFragment();
        c.console = console;
        c.console.setMessageListener(c);
        return c;
    }

    ListView mListView = null;
    ConsoleMessageAdapter adapter = null;
    Button btn_send = null;
    EditText et_command = null;
    Console console = null;

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
                onClickEnter();
            }
        });

        et_command = (EditText)root.findViewById(R.id.et_command);
        return root;
    }

    private void onClickEnter() {
        String cmdline = et_command.getText().toString();
        if(console != null) {
            console.exec(cmdline);
        }
        et_command.setText("");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(console != null) {
            adapter = new ConsoleMessageAdapter(context, console.getAllMessages());
            console.setMessageListener(this);
            console.enabled();
        }
    }

    @Override
    public void onDetach() {
        if(console != null) {
            console.disable();
        }
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        if(console != null) {
            console.removeMessageListener(this);
            console.cleanup();
        }
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
    public void onNewMessage(final ConsoleMessage message) {
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
