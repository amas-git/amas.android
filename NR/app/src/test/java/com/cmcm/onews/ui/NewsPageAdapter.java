package com.cmcm.onews.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import com.cmcm.onews.NewsL;
import com.cmcm.onews.ONewsScenarios;
import com.cmcm.onews.event.ONewsEvent;
import com.cmcm.onews.fragment.NewsBaseFragment;
import com.cmcm.onews.fragment.NewsBaseListFragment;
import com.cmcm.onews.fragment.NewsListFragment;
import com.cmcm.onews.fragment.NewsLocalListFragment;
import com.cmcm.onews.model.ONewsScenario;
import java.util.List;

public class NewsPageAdapter extends FragmentStatePagerAdapter {
    private  List<ONewsScenario> scenarios = ONewsScenarios.getInstance().scenarios();
    private FragmentManager manager;
    private Context mContext;

    public NewsPageAdapter(Context context,FragmentManager fm) {
        super(fm);
        this.manager = fm;
        this.mContext = context;
    }

    private class VistualONewsScenario extends ONewsScenario {
        NewsBaseFragment fragment = null;
        String title = "";

        @Override
        public CharSequence getCategoryTitle(Context context) {
            return title;
        }
    }

    /**
     * 增加一个自定义的Fragment到Tab页中
     * @param f
     * @param title
     */
    public void addCustomizedFragment(NewsBaseFragment f, String title) {
        VistualONewsScenario vs = new VistualONewsScenario();
        vs.fragment = f;
        vs.title = title;

        if(null != scenarios && !scenarios.isEmpty() && !(scenarios.get(0) instanceof VistualONewsScenario)){
            scenarios.add(0,vs);
        }
    }

    @Override
    public int getCount() {
        return scenarios.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        ONewsScenario s = scenarios.get(position);
        return s.getCategoryTitle(mContext);
    }

    @Override
    public NewsBaseFragment getItem(int position) {
        ONewsScenario s = scenarios.get(position);
        NewsBaseFragment fragment = null;
        if(s instanceof VistualONewsScenario) {
            fragment = ((VistualONewsScenario) s).fragment;
        } else {
            fragment = NewsBaseListFragment.newInstance(s);
        }

        if(NewsL.DEBUG) NewsL.newspageadapter(String.format("getItem : %d,scenario : %s",position,scenarios.get(position).getStringValue()));

        return fragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if(NewsL.DEBUG) NewsL.newspageadapter(String.format("instantiateItem : %d,scenario : %s",position,scenarios.get(position).getStringValue()));
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        if(NewsL.DEBUG) NewsL.newspageadapter(String.format("destroyItem : %d",position));
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public ONewsScenario scenario(int position){
        if(position >= scenarios.size()){
            return null;
        }
        return scenarios.get(position);
    }

    public void onEventInUiThread(ONewsEvent event){
        if(null != manager){
            List<Fragment> fragments = manager.getFragments();
            if(null != fragments){
                if(NewsL.DEBUG) NewsL.newspageadapter(String.format("onEventInUiThread Fragment size : %d",fragments.size()));
                for(Fragment fragment : fragments){
                    if(fragment instanceof NewsListFragment || fragment instanceof NewsLocalListFragment){
                        ((NewsBaseFragment)fragment).onEventInUiThread(event);
                    }
                }
            }
        }
    }

    public void notifyONewsScenarios() {
        scenarios = ONewsScenarios.getInstance().scenarios();
        notifyDataSetChanged();
    }
}
