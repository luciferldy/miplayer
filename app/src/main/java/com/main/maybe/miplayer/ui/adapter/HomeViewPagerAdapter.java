package com.main.maybe.miplayer.ui.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.main.maybe.miplayer.ui.fragment.HomeFragment;

/**
 * Created by Lucifer on 2015/3/24.
 */
public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"单曲", "歌手", "专辑", "文件夹"};

    public HomeViewPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public Fragment getItem(int position) {
        return HomeFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }
}
