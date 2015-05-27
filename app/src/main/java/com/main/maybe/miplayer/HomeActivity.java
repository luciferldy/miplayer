package com.main.maybe.miplayer;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.main.maybe.miplayer.adapter.HomeVPAdapter;
import com.main.maybe.miplayer.service.MusicPlayerService;
import com.main.maybe.miplayer.task.LoadingListTask;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class HomeActivity extends ActionBarActivity {

    private PagerSlidingTabStrip mainTabs;
    private ViewPager mainPagers;
    private HomeVPAdapter mainPagerAdapter;
    private final String LOG_TAG = HomeActivity.class.getSimpleName();
    private ArrayList<HashMap<String, String>> songs = new ArrayList<>();
    private HashMap<String, String> currentSong = new HashMap<>();
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // full screen
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // maybe mean nothing
        setContentView(R.layout.home);
        initPaper();
        initBottomPlayer();
    }

    public void initPaper(){
        mainTabs = (PagerSlidingTabStrip)findViewById(R.id.main_tabs);
        mainTabs.setShouldExpand(true);
        mainPagers = (ViewPager)findViewById(R.id.main_pager);
        mainPagerAdapter = new HomeVPAdapter(getSupportFragmentManager());

        mainPagers.setAdapter(mainPagerAdapter);
        int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        mainPagers.setPageMargin(pageMargin);
        mainTabs.setViewPager(mainPagers);

        mainTabs.setIndicatorColor(getResources().getColor(R.color.background_floating_material_light));
    }

    public void initBottomPlayer(){
        TextView musicTitle = (TextView)findViewById(R.id.bottom_music_title);
        TextView musicArtist = (TextView)findViewById(R.id.bottom_music_artist);

        parseSerializableList();
        if (currentSong != null){
            musicTitle.setText(LoadingListTask.songName);
            musicArtist.setText(LoadingListTask.artistName);
        }

    }

    public void parseSerializableList(){
        try {
            FileInputStream fis = new FileInputStream(MusicPlayerService.CurrentListPath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            // songs 最初列表包含了当前播放的位置
            songs = (ArrayList<HashMap<String, String>>)ois.readObject();

            // get the current song
            HashMap<String, String> hashMap = songs.get(songs.size()-1);
            currentPosition = Integer.parseInt(hashMap.get(MusicPlayerService.PlayingNumber));

            currentSong = songs.get(currentPosition);
            // remove the flag at the end
            songs.remove(songs.size()-1);
            return;
        }catch (IOException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            Log.d(LOG_TAG, "ois.readObject got problems");
            e.printStackTrace();
        }
        songs = null;
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
