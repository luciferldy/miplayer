package com.main.maybe.miplayer;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.main.maybe.miplayer.adapter.MainViewPagerAdapter;


public class MainActivity extends ActionBarActivity {

    private PagerSlidingTabStrip mainTabs;
    private ViewPager mainPagers;
    private MainViewPagerAdapter mainPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // full screen
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // maybe mean nothing
        setContentView(R.layout.activity_main);
        initPaper();
    }

    public void initPaper(){
        mainTabs = (PagerSlidingTabStrip)findViewById(R.id.main_tabs);
        mainTabs.setShouldExpand(true);
        mainPagers = (ViewPager)findViewById(R.id.main_pager);
        mainPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());

        mainPagers.setAdapter(mainPagerAdapter);
        int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        mainPagers.setPageMargin(pageMargin);
        mainTabs.setViewPager(mainPagers);

        mainTabs.setIndicatorColor(getResources().getColor(R.color.background_floating_material_light));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
