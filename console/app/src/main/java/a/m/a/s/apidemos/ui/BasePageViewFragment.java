package a.m.a.s.apidemos.ui;

import android.support.v4.app.Fragment;

public class BasePageViewFragment extends Fragment {
    CharSequence mTitle = BasePageViewFragment.class.getSimpleName();

    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    public CharSequence getTitle() {
        return mTitle;
    }
}
