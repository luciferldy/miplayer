package com.main.maybe.miplayer.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Maybe霏 on 2015/3/24.
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter{

    private final String[] TITLES = {"Song"};

    public MainViewPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }
}
