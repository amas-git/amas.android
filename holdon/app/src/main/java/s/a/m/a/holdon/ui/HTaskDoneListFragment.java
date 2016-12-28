package s.a.m.a.holdon.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import s.a.m.a.holdon.R;

/**
 * Created by amas on 15-5-17.
 */
public class HTaskDoneListFragment extends Fragment {
    public static HTaskDoneListFragment newInstance() {
        return new HTaskDoneListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pagefragment3, container, false);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
