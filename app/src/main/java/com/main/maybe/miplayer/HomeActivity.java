package com.main.maybe.miplayer;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.main.maybe.miplayer.adapter.HomeVPAdapter;
import com.main.maybe.miplayer.binder.BottomMusicPlayerServiceBinder;
import com.main.maybe.miplayer.service.MusicPlayerService;
import com.main.maybe.miplayer.task.LoadingListTask;

import java.io.FileInputStream;
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

    MusicPlayerService mMusicPlayerService;
    ServiceConnection mServiceConnection;
    BottomMusicPlayerServiceBinder bmpServiceBinder;

    private TextView musicSongName;
    private TextView musicArtistName;
    private ImageButton btmPlayOrPause;
    private ImageButton btmPlayNext;

    private boolean mBound = false;
    private int mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // full screen
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // maybe mean nothing
        setContentView(R.layout.home);
        initPaper();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        musicSongName = (TextView)findViewById(R.id.bottom_music_song_name);
        musicArtistName = (TextView)findViewById(R.id.bottom_music_artist);
        btmPlayOrPause = (ImageButton)findViewById(R.id.bottom_music_play);
        btmPlayNext = (ImageButton)findViewById(R.id.bottom_music_next);

        parseSerializableList();

        /*
         * 当第一次进入应用，currentSong没有
         * 当解析出现错误时，currentSong同样没有
         */
        if (currentSong == null){
            btmPlayOrPause.setClickable(false);
            btmPlayNext.setClickable(false);
            return;
        }

        musicSongName.setText(currentSong.get(LoadingListTask.songName));
        musicArtistName.setText(currentSong.get(LoadingListTask.artistName));
        btmPlayOrPause.setClickable(true);
        btmPlayNext.setClickable(true);

        btmPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 当service为空时
                if (mMusicPlayerService == null) {
                    // 开启并绑定
                    defineServiceConnection();
                }
                if (mBound) {
                    mState = mMusicPlayerService.changeState();
                    switch (mState) {
                        case MusicPlayerService.PAUSED:
                            btmPlayOrPause.setImageResource(R.drawable.song_play);
                            break;
                        case MusicPlayerService.PLAYING:
                            btmPlayOrPause.setImageResource(R.drawable.song_pause);
                            break;
                    }
                }
            }
        });

        btmPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicPlayerService == null) {
                    // 开启并绑定
                    defineServiceConnection();
                }
                if (mBound) {
                    btmPlayOrPause.setImageResource(R.drawable.song_play);
                    mMusicPlayerService.playNext();
                }
            }
        });

    }



    private void defineServiceConnection(){
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // 当后台没有服务时，需要启动
                if (!isServiceWorked(MusicPlayerService.MUSIC_PLAYER_SERVICE_NAME, getApplicationContext()))
                    getApplicationContext().startService(new Intent(getApplicationContext(), MusicPlayerService.class));
                bmpServiceBinder = (BottomMusicPlayerServiceBinder)service;
                mMusicPlayerService = bmpServiceBinder.getService(new BottomMusicPlayerCallBack() {
                    @Override

                    public void setImagePlay() {
                        if (btmPlayOrPause != null)
                            btmPlayOrPause.setImageResource(R.drawable.song_play);
                    }

                    @Override
                    public void setImagePaused() {
                        if (btmPlayOrPause != null)
                            btmPlayOrPause.setImageResource(R.drawable.song_pause);
                    }

                    @Override
                    public void setSongName(String songName) {
                        if (musicSongName != null)
                            musicSongName.setText(songName);
                    }

                    @Override
                    public void setArtistName(String artistName) {
                        if (musicArtistName != null)
                            musicArtistName.setText(artistName);
                    }
                });

                mBound  = true;
                // 在连接的时候传入数据
                if (songs != null)
                    mMusicPlayerService.addMusicToQueue(songs);

                Log.d(LOG_TAG, LOG_TAG+" service connected and can be controlled");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBound = false;
                Log.d(LOG_TAG, "onServiceDisconnected");
            }
        };
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
        }catch (Exception e){
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

    /*
     * 当 activity 隐藏时，解除绑定
     */
    @Override
    protected void onStop() {
        unbindService(mServiceConnection);
        super.onStop();
        Log.d(LOG_TAG, LOG_TAG+" onStop");
    }

    public static boolean isServiceWorked(String className, Context context){
        ActivityManager activityManager = (ActivityManager)context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningServiceInfos = (ArrayList<RunningServiceInfo>)activityManager.getRunningServices(30);
        for (int i=0; i<runningServiceInfos.size(); i++){
            if (runningServiceInfos.get(i).service.getClassName().toString().equals(className))
                return true;
        }
        return false;
    }
}
