package s.a.m.a.holdon.editor;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import s.a.m.a.holdon.R;
import s.a.m.a.holdon.ui.base.BaseListAcitivty;

/**
 * Created by amas on 15-5-17.
 */
public class ListEditor extends BaseListAcitivty {
    public static final String[] EMPTY_STRING_ARRAY = new String[]{};

    public String[] names  = EMPTY_STRING_ARRAY;
    public String[] values = EMPTY_STRING_ARRAY;

    public static Intent getLaunchIntent(Context context, int nameResId, int valueResId) {
        Intent intent = new Intent();
        intent.setClass(context, ListEditor.class);
        intent.putExtra(":names", nameResId);
        intent.putExtra(":values",valueResId);
        return intent;
    }

    public static void startEdit(Fragment context, int requestCode, int nameResId, int valueResId) {
        //context.startActivity(getLaunchIntent(context, nameResId, valueResId));
        context.startActivityForResult(getLaunchIntent(context.getActivity(), nameResId, valueResId), requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listeditor);


        Intent i = getIntent();
        if(i != null) {
            if(i.hasExtra(":names")) {
                int id = i.getIntExtra(":names", 0);
                names = id > 0 ? getResources().getStringArray(id) : EMPTY_STRING_ARRAY;
            }

            if(i.hasExtra(":values")) {
                int id = i.getIntExtra(":values", 0);
                values = id > 0 ? getResources().getStringArray(id) : EMPTY_STRING_ARRAY;
            }
        }


        setListAdapter(onCreateListAdapter(names));
    }

    protected ListAdapter onCreateListAdapter(String[] names) {
        return new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Object item = getListAdapter().getItem(position);
        _setResult(position);
        System.out.println("OVER: " +names[position]+"="+  values[position]);
    }

    protected void _setResult(final int position) {
        Intent intent = new Intent();
        intent.putExtra(":position", position);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static int getResultPosition(Intent intent) {
        if(intent.hasExtra(":position")) {
            return intent.getIntExtra(":position", -1);
        }
        return -1;
    }

}
