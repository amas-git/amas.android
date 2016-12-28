package s.a.m.a.holdon.editor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import s.a.m.a.holdon.R;
import s.a.m.a.holdon.ui.base.BaseActivity;

/**
 * Created by amas on 15-5-17.
 */
public class TextEditor extends BaseActivity {
    EditText edText = null;
    TextView tvNote = null;

    public static Intent getLaunchIntent(Context context, CharSequence note, CharSequence hint, int max_length) {
        Intent intent = new Intent();
        intent.setClass(context, TextEditor.class);
        //intent.putExtra(":hint", hint);
        intent.putExtra(":note", note);
        intent.putExtra(":max_length", max_length);
        return intent;
    }

    public static String EXTRA_STRING(Intent intent, String key, String s) {
        return intent.hasExtra(key) ? intent.getStringExtra(key) : s;
    }

    public static void startEdit(Fragment context, int requestCode, int promote, int hint, int max_length) {
        context.startActivityForResult(getLaunchIntent(context.getActivity(),
                context.getString(promote),
                context.getString(hint),
                max_length),
                requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texteditor);
        edText = (EditText) findViewById(R.id.et_main);
        tvNote = (TextView) findViewById(R.id.tv_note);

        Intent intent = getIntent();
        if(intent != null) {
            edText.setHint(EXTRA_STRING(intent, ":hint", ""));
            String note = EXTRA_STRING(intent, ":note", "");
            if(!TextUtils.isEmpty(note)) {
                tvNote.setText(note);
            }
        }
        setTitleRightButtonText(R.string.ok);
        setTitle("新建任务");
    }

    public void onSave() {
        Intent intent = new Intent();
        intent.putExtra(":text", edText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                onPressEnter();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onPressEnter() {
        onSave();
    }

    @Override
    public void onClick_title_button_height(View v) {
        onSave();
    }

    /**
     * 获取编辑结果
     * @param intent
     * @return
     */
    public static String getResultText(Intent intent) {
        if(intent == null) {
            return "";
        }
        return intent.hasExtra(":text") ? intent.getStringExtra(":text") : "";
    }
}
