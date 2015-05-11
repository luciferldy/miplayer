package com.main.maybe.miplayer.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.main.maybe.miplayer.fragment.MainViewPagerFragment;

/**
 * Created by Maybe霏 on 2015/3/24.
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter{

    private final String[] TITLES = {"发现", "我的"};

    public MainViewPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public Fragment getItem(int position) {
        return MainViewPagerFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }
}
