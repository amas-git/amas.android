package s.a.m.a.holdon.ui.base;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import s.a.m.a.holdon.R;

/**
 * Created by amas on 15-5-30.
 */
public class BaseListAcitivty extends ListActivity {
    private View mTitleView;
    private TextView mTitle = null;
    private Button mTitleRightButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void initTitle() {
        if (mTitleView == null) {
            mTitleView = findViewById(R.id.title);
            mTitle = (TextView) mTitleView.findViewById(R.id.title_bar_title);
            mTitleRightButton = (Button) mTitleView.findViewById(R.id.title_bar_right_button);
        }
    }

    public void setTitle(int title) {
        initTitle();
        mTitle.setText(title);
    }

    public void setTitle(CharSequence title) {
        initTitle();
        mTitle.setText(title);
    }

    public void setTitleRightButtonText(CharSequence text) {
        if (mTitleRightButton != null) {
            mTitleRightButton.setVisibility(View.VISIBLE);
        }
    }
}