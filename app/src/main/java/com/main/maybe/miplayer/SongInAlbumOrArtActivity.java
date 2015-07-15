package com.main.maybe.miplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.main.maybe.miplayer.binder.MusicPlayerServiceBinder;
import com.main.maybe.miplayer.fragment.MusicPlayerFragment;
import com.main.maybe.miplayer.fragment.SongInAlbumOrArtFragment;
import com.main.maybe.miplayer.service.MusicPlayerService;
import com.main.maybe.miplayer.task.LoadingListTask;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Maybe霏 on 2015/5/22.
 */
public class SongInAlbumOrArtActivity extends ActionBarActivity {

    private final String LOG_TAG = SongInAlbumOrArtActivity.class.getSimpleName();

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
    private ImageView bottom_album_cover;

    private boolean mBound = false;
    private int mState;

    private ArrayList<HashMap<String, String>> songs = new ArrayList<>();
    private HashMap<String, String> currentSong = new HashMap<>();
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_from_album_or_art);
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        Bundle b = new Bundle();
        b.putString("type", type);
        int i;
        if (type.equals("artist")){
            i = intent.getIntExtra(LoadingListTask.artistId, -1);
            b.putInt(LoadingListTask.artistId, i);
        }else if (type.equals("album")){
            i = intent.getIntExtra(LoadingListTask.albumId, -1);
            b.putInt(LoadingListTask.albumId, i);
        }else if (type.equals("self_list")){
            i = 0; // 不需要获取id
        }else{
            // wrong
            i = -1;
        }
        // if albumId == -1 wrong
        if (i == -1){
            Toast.makeText(getApplicationContext(), "the argument through activities is wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        Fragment sia = new SongInAlbumOrArtFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.song_from_album_or_art_list, sia);
        // bundle
        sia.setArguments(b);
        ft.commit();
        Log.d(LOG_TAG, "ft commit");
    }

    @Override
    protected void onStart() {
        super.onStart();
        initBottomPlayer();
    }

    public void initBottomPlayer(){

        bottom_music_player = (RelativeLayout)findViewById(R.id.bottom_music_show);
        bottom_music_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 底部播放器，点击跳转
                Intent intent = new Intent(SongInAlbumOrArtActivity.this, MusicPlayerActivity.class);
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
        bottom_album_cover = (ImageView)findViewById(R.id.bottom_music_albumcover);

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
        if (HomeActivity.isServiceWorked(MusicPlayerService.MUSIC_PLAYER_SERVICE_NAME, getApplicationContext())){
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
            bottom_music_player.setClickable(false);
            return;
        }else {
            musicSongName.setText(currentSong.get(LoadingListTask.songName));
            musicArtistName.setText(currentSong.get(LoadingListTask.artistName));

            Log.d(LOG_TAG, "song number in file" + songs.size());
            int songId = Integer.parseInt(currentSong.get(LoadingListTask.songId));
            int albumId = Integer.parseInt(currentSong.get(LoadingListTask.albumId));

            bottom_album_cover.setImageBitmap(AlbumCoverHelper.getArtwork(getApplication(), songId, albumId, true, true));
        }
    }

    public void bindMusicService(){
        defineServiceConnection();
        Intent intent = new Intent(SongInAlbumOrArtActivity.this, MusicPlayerService.class);
        intent.putExtra(MusicPlayerService.ACTIVITY_INDENTIFY, MusicPlayerService.BOTTOM_PLAYER_ACTIVITY);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private void defineServiceConnection(){

        // 当后台没有服务时，需要启动
        if (!HomeActivity.isServiceWorked(MusicPlayerService.MUSIC_PLAYER_SERVICE_NAME, getApplicationContext()))
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
                    public void setAlbumCover(int songId, int albumId) {
                        if (bottom_album_cover != null)
                            AlbumCoverHelper.getArtwork(getApplicationContext(), songId, albumId, true, true);
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

                    Log.d(LOG_TAG, LOG_TAG + " from music service");
                    if (mMusicPlayerService.getState() == MusicPlayerService.PLAYING)
                        btmPlayOrPause.setImageResource(R.drawable.song_pause);
                    else
                        btmPlayOrPause.setImageResource(R.drawable.song_play);
                }
                else if (FROMWHERE == FROM_LAUNCHER){
                    if (songs != null)
                        mMusicPlayerService.addMusicToQueue(songs);
                    mMusicPlayerService.play(currentPosition);
                    Log.d(LOG_TAG, LOG_TAG + " from launcher");
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
        currentSong = null;
    }

    /*
     * 当 activity 隐藏时，解除绑定
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mServiceConnection != null && mMusicPlayerService != null)
            unbindService(mServiceConnection);
        Log.d(LOG_TAG, LOG_TAG+" is onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, LOG_TAG+" onStop");
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, LOG_TAG+" is onDestroy!");
        super.onDestroy();
    }

}
