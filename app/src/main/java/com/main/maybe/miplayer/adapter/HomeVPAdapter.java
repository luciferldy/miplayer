package com.main.maybe.miplayer.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.main.maybe.miplayer.fragment.HomeFragment;

/**
 * Created by Maybe霏 on 2015/3/24.
 */
public class HomeVPAdapter extends FragmentPagerAdapter{

    private final String[] TITLES = {"歌曲", "歌手", "专辑", "列表"};

    public HomeVPAdapter(FragmentManager fm){
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
