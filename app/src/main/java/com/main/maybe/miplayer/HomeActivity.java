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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.main.maybe.miplayer.adapter.HomeVPAdapter;
import com.main.maybe.miplayer.binder.MusicPlayerServiceBinder;
import com.main.maybe.miplayer.fragment.MusicPlayerFragment;
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
    private int FROMWHERE = 0;
    private static int FROM_MUSICPLAYER = 1;
    private static int FROM_LAUNCHER = 2;

    MusicPlayerService mMusicPlayerService;
    ServiceConnection mServiceConnection;
    MusicPlayerServiceBinder mServiceBinder;

    private TextView musicSongName;
    private TextView musicArtistName;
    private ImageButton btmPlayOrPause;
    private ImageButton btmPlayNext;
    private RelativeLayout bottom_music_player;

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
        Log.d(LOG_TAG, LOG_TAG + " is onStart");
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

        bottom_music_player = (RelativeLayout)findViewById(R.id.bottom_music_show);
        bottom_music_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 底部播放器，点击跳转
                Intent intent = new Intent(HomeActivity.this, MusicPlayerActivity.class);
                intent.putExtra(LoadingListTask.songList, songs);
                intent.putExtra(LoadingListTask.playPosition, currentPosition);
                intent.putExtra(LoadingListTask.ENTER_FSMUSIC_PLAYER_FROM_WHERE, MusicPlayerFragment.FROM_BOTTOM_MUSIC_PLAYER);
                startActivity(intent);
            }
        });

        musicSongName = (TextView)findViewById(R.id.bottom_music_song_name);
        musicArtistName = (TextView)findViewById(R.id.bottom_music_artist);
        btmPlayOrPause = (ImageButton)findViewById(R.id.bottom_music_play);
        btmPlayNext = (ImageButton)findViewById(R.id.bottom_music_next);

        btmPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * 当 mMusicPlayerService 为空时表示第一次点击进入而且是播放
                 */
                if (mMusicPlayerService == null) {
                    // 开启并绑定
                    FROMWHERE = FROM_LAUNCHER;
                    bindMusicService();
                    return;
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
                    // 首先将CurrentPosition向后移动一位
                    if ((currentPosition+1) == songs.size())
                        currentPosition = 0;
                    else
                        currentPosition++;
                    FROMWHERE = FROM_LAUNCHER;
                    bindMusicService();
                    return;
                }
                if (mBound) {
                    btmPlayOrPause.setImageResource(R.drawable.song_play);
                    mMusicPlayerService.playNext();
                }
            }
        });


        /*
         * 判断是否后台有音乐播放的服务
         */
        if (isServiceWorked(MusicPlayerService.MUSIC_PLAYER_SERVICE_NAME, getApplicationContext())){
            FROMWHERE = FROM_MUSICPLAYER;
            bindMusicService();
            return;
        }else {
            // 否则的话从本地文件加载播放列表
            parseSerializableList();
        }
        /*
         * 当第一次进入应用，currentSong没有
         * 当解析出现错误时，currentSong同样没有
         */
        if (currentSong == null){
            btmPlayOrPause.setClickable(false);
            btmPlayNext.setClickable(false);
            return;
        }else {
            musicSongName.setText(currentSong.get(LoadingListTask.songName));
            musicArtistName.setText(currentSong.get(LoadingListTask.artistName));
        }
    }

    public void bindMusicService(){
        defineServiceConnection();
        Intent intent = new Intent(HomeActivity.this, MusicPlayerService.class);
        intent.putExtra(MusicPlayerService.ACTIVITY_INDENTIFY, MusicPlayerService.BOTTOM_PLAYER_ACTIVITY);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private void defineServiceConnection(){

        // 当后台没有服务时，需要启动
        if (!isServiceWorked(MusicPlayerService.MUSIC_PLAYER_SERVICE_NAME, getApplicationContext()))
            getApplicationContext().startService(new Intent(getApplicationContext(), MusicPlayerService.class));

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mServiceBinder = (MusicPlayerServiceBinder)service;
                mMusicPlayerService = mServiceBinder.getService(new MusicPlayerServiceBinderCallBack() {
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
                    public void setMusicAlbum(String album) {

                    }

                    @Override
                    public void setMusicArtist(String artist) {
                        if (musicArtistName != null)
                            musicArtistName.setText(artist);
                    }

                    @Override
                    public void setTotalTime(String time) {

                    }

                    @Override
                    public void setCurrentTime(String time) {

                    }

                    @Override
                    public void setMusicTitle(String title) {
                        if (musicSongName != null)
                            musicSongName.setText(title);
                    }
                });

                mBound  = true;
                // 判断建立的连接来自于启动还是上一级活动退出
                if (FROMWHERE == FROM_MUSICPLAYER){

                    songs = mMusicPlayerService.getPlayingQueue();
                    currentPosition = mMusicPlayerService.getCurrentPosition();
                    currentSong = songs.get(currentPosition);
                    musicSongName.setText(currentSong.get(LoadingListTask.songName));
                    musicArtistName.setText(currentSong.get(LoadingListTask.artistName));

                    if (mMusicPlayerService.getState() == MusicPlayerService.PLAYING)
                        btmPlayOrPause.setImageResource(R.drawable.song_pause);
                    else
                        btmPlayOrPause.setImageResource(R.drawable.song_play);
                }
                else if (FROMWHERE == FROM_LAUNCHER){
                    if (songs != null)
                        mMusicPlayerService.addMusicToQueue(songs);
                    mMusicPlayerService.play(currentPosition);
                }else {
                    Log.d(LOG_TAG, LOG_TAG+" find error FROMWHERE");
                }
                Log.d(LOG_TAG, LOG_TAG + " service connected and can be controlled");
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

            FileInputStream fis = new FileInputStream(MusicPlayerService.pathName+MusicPlayerService.fileName);
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

    @Override
    protected void onPause() {
        super.onPause();
        if (mServiceConnection != null && mMusicPlayerService != null)
            unbindService(mServiceConnection);
        Log.d(LOG_TAG, LOG_TAG+" is onPause");
    }

    /*
         * 当 activity 隐藏时，解除绑定
         */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, LOG_TAG+" onStop");
    }

    public static boolean isServiceWorked(String className, Context context){
        ActivityManager activityManager = (ActivityManager)context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningServiceInfos = (ArrayList<RunningServiceInfo>)activityManager.getRunningServices(30);
        for (int i=0; i<runningServiceInfos.size(); i++){
            if (runningServiceInfos.get(i).service.getClassName().toString().equals(className)){
                Log.d("isServiceWorked", "return true");
                return true;
            }

        }
        Log.d("isServiceWorked", "return false");
        return false;
    }

    @Override
    protected void onDestroy() {
        if (mMusicPlayerService!=null)
            stopService(new Intent(HomeActivity.this, MusicPlayerService.class));
        Log.d(LOG_TAG, LOG_TAG+" is onDestroy!");
        super.onDestroy();
    }
}
