package com.main.maybe.miplayer.ui.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.maybe.miplayer.AlbumCoverHelper;
import com.main.maybe.miplayer.MusicPlayerServiceBinderCallBack;
import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.model.SingleBean;
import com.main.maybe.miplayer.ui.adapter.HomeVPAdapter;
import com.main.maybe.miplayer.binder.MusicPlayerServiceBinder;
import com.main.maybe.miplayer.service.MusicPlayerService;
import com.main.maybe.miplayer.util.CommonUtils;
import com.main.maybe.miplayer.util.Logger;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeActivity extends AppCompatActivity {

    private static final String LOG_TAG = HomeActivity.class.getSimpleName(); // class name

    private static final int MEDIA_STORE_PERMISSION_REQUEST_CODE = 1009;

    private MusicPlayerService mMusicPlayerService;
    private ServiceConnection mServiceConnection;
    private MusicPlayerServiceBinder mServiceBinder;

    @BindView(R.id.categories_layout) ViewPager mPagers;
    @BindView(R.id.sliding_tabs) TabLayout mTabLayout;

    @BindView(R.id.song_name) TextView mSongName;
    @BindView(R.id.artist_name) TextView mArtistName;
    @BindView(R.id.play_bar_play) ImageView mPlay;
    @BindView(R.id.play_bar_next) ImageView mPlayNext;

    @BindView(R.id.play_bar_container) RelativeLayout mPlayBarContainer;
    @BindView(R.id.album_cover) ImageView mAlbumCover;

    private boolean mBound = false;
    private int mState;
    private List<SingleBean> songs;
    private SingleBean currentSong;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MEDIA_STORE_PERMISSION_REQUEST_CODE);
        }

        else
        {
            initViews();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                              String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MEDIA_STORE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initViews();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }

    }

    public void initViews() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViewPaper();
        initPlayBar();
    }

    public void initViewPaper() {
        mPagers.setAdapter(new HomeVPAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mPagers);
    }

    public void initPlayBar() {
        mPlayBarContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(HomeActivity.this, MusicPlayerActivity.class);
//                intent.putExtra(LoadingListTask.songList, songs);
//                intent.putExtra(LoadingListTask.playPosition, currentPosition);
//                intent.putExtra(LoadingListTask.ENTER_FSMUSIC_PLAYER_FROM_WHERE, MusicPlayerFragment.FROM_BOTTOM_MUSIC_PLAYER);
//                startActivity(intent);
            }
        });

        mPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // mMusicPlayerService 为空时表示第一次点击进入而且是播放
                if (mMusicPlayerService == null) {
                    // 开启并绑定
                    bindMusicService();
                    return;
                }
                if (mBound) {
                    mState = mMusicPlayerService.changeState();
                    switch (mState) {
                        case MusicPlayerService.PAUSED:
                            mPlay.setImageResource(R.drawable.playbar_btn_play);
                            break;
                        case MusicPlayerService.PLAYING:
                            mPlay.setImageResource(R.drawable.playbar_btn_pause);
                            break;
                    }
                }
            }
        });

        mPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicPlayerService == null) {
                    // 将 currentPosition 增加1，如果到 list 尾就返回第一个
                    if (currentPosition == (songs.size() - 1))
                        currentPosition = 0;
                    else
                        currentPosition++;
                    bindMusicService();
                    return;
                }
                if (mBound) {
                    mPlay.setImageResource(R.drawable.playbar_btn_play);
                    mMusicPlayerService.playNext();
                }
            }
        });


        /*
         * 判断是否后台有音乐播放的服务
         */
        if (CommonUtils.isServiceWorked(MusicPlayerService.MUSIC_PLAYER_SERVICE_NAME, getApplicationContext())){
            bindMusicService();
            return;
        } else {
            // 否则的话从本地文件加载播放列表
            parseSerializableList();
        }

        /*
         * 当第一次进入应用，currentSong 没有
         * 当解析出现错误时，currentSong 同样没有
         * 将 playbar 隐藏
         */
        if (currentSong == null){
            mPlay.setClickable(false);
            mPlayNext.setClickable(false);
            mPlayBarContainer.setClickable(false);
            return;
        } else {
            mSongName.setText(currentSong.getTitle());
            mArtistName.setText(currentSong.getArtist());

            Logger.i(LOG_TAG, "list has " + songs.size() + "");
            int songId = Integer.parseInt(currentSong.getId());
            int albumId = Integer.parseInt(currentSong.getAlbumId());

            mAlbumCover.setImageBitmap(AlbumCoverHelper.getArtwork(getApplication(), songId, albumId, true, true));
        }
    }

    public void bindMusicService(){

        defineServiceConnection();
        Intent intent = new Intent(HomeActivity.this, MusicPlayerService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    private synchronized void defineServiceConnection(){

        // 当后台没有服务时，需要启动
        if (!CommonUtils.isServiceWorked(MusicPlayerService.MUSIC_PLAYER_SERVICE_NAME, getApplicationContext()))
            getApplicationContext().startService(new Intent(getApplicationContext(), MusicPlayerService.class));

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mServiceBinder = (MusicPlayerServiceBinder) service;
                mMusicPlayerService = mServiceBinder.getService(new MusicPlayerServiceBinderCallBack() {
                    @Override

                    public void setImagePlay() {
                        if (mPlay != null)
                            mPlay.setImageResource(R.drawable.playbar_btn_play);
                    }

                    @Override
                    public void setImagePaused() {
                        if (mPlay != null)
                            mPlay.setImageResource(R.drawable.playbar_btn_pause);
                    }

                    @Override
                    public void setAlbumCover(int songId, int albumId) {
                        if (mAlbumCover != null)
                            AlbumCoverHelper.getArtwork(getApplicationContext(), songId, albumId, true, true);
                    }

                    @Override
                    public void setMusicAlbum(String album) {

                    }

                    @Override
                    public void setMusicArtist(String artist) {
                        if (mArtistName != null)
                            mArtistName.setText(artist);
                    }

                    @Override
                    public void setTotalTime(String time) {

                    }

                    @Override
                    public void setCurrentTime(String time) {

                    }

                    @Override
                    public void setMusicTitle(String title) {
                        if (mSongName != null)
                            mSongName.setText(title);
                    }
                });

                mBound  = true;
                // 判断建立的连接来自于启动还是上一级活动退出
//                if (FROMWHERE == FROM_MUSICPLAYER){
//
//                    songs = mMusicPlayerService.getPlayingQueue();
//                    currentPosition = mMusicPlayerService.getCurrentPosition();
//                    currentSong = songs.get(currentPosition);
//                    mSongName.setText(currentSong.get(LoadingListTask.songName));
//                    mArtistName.setText(currentSong.get(LoadingListTask.artistName));
//
//                    Log.d(LOG_TAG, LOG_TAG + " from music service");
//                    if (mMusicPlayerService.getState() == MusicPlayerService.PLAYING)
//                        mPlay.setImageResource(R.drawable.song_pause);
//                    else
//                        mPlay.setImageResource(R.drawable.song_play);
//                }
//                else if (FROMWHERE == FROM_LAUNCHER){
//                    if (songs != null)
//                        mMusicPlayerService.addMusicToQueue(songs);
//                    mMusicPlayerService.play(currentPosition);
//                    Log.d(LOG_TAG, LOG_TAG + " from launcher");
//                }else {
//                    Log.d(LOG_TAG, LOG_TAG+" find error FROMWHERE");
//                }
//                Log.d(LOG_TAG, LOG_TAG + " service connected and can be controlled");
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
            songs = (ArrayList<SingleBean>) ois.readObject();

            // get the current song
            SingleBean bean = songs.get(songs.size()-1);
            currentPosition = Integer.parseInt(bean.getDuration());

            currentSong = songs.get(currentPosition);
            // remove the flag at the end
            songs.remove(songs.size()-1);
        } catch (Exception e){
            e.printStackTrace();
            Logger.i(LOG_TAG, "ois.readObject occur error. msg = " + e.getMessage());
            songs = null;
            currentSong = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mServiceConnection != null && mMusicPlayerService != null)
            unbindService(mServiceConnection);
        Logger.i(LOG_TAG, "onPause");
    }

    /*
     * 当 activity 隐藏时，解除绑定
     */
    @Override
    protected void onStop() {
        super.onStop();
        Logger.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMusicPlayerService != null)
            stopService(new Intent(HomeActivity.this, MusicPlayerService.class));
        Logger.i(LOG_TAG, "onDestroy");
    }
}
