package com.main.maybe.miplayer.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.main.maybe.miplayer.ui.fragment.HomeFragment

class HomeViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val TITLES = arrayOf("单曲", "歌手", "专辑", "文件夹")

    override fun getPageTitle(position: Int): CharSequence? {
        return TITLES[position]
    }

    override fun getItem(position: Int): Fragment {
        return HomeFragment.newInstance(position)
    }

    override fun getCount(): Int {
        return TITLES.size
    }
}
