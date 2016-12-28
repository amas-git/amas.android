package s.a.m.a.holdon.ui.fragment;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import client.core.model.Event;
import s.a.m.a.holdon.ui.base.BaseListFragment;


public class MyPageViewAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragments = new ArrayList<Fragment>(0);

    public MyPageViewAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Fragment fragment = (Fragment) getItem(position);
        return "";
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }


    public void onHandleEvent(Event event) {
        for(Fragment f : fragments) {
            if(f instanceof BaseListFragment) {
                ((BaseListFragment) f).onHandleEvent(event);
            }
        }
    }

}
