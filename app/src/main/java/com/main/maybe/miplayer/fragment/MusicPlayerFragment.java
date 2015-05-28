package com.main.maybe.miplayer.fragment;

/**
 * Created by Maybe霏 on 2015/3/5.
 */

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.main.maybe.miplayer.HomeActivity;
import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.SeekBarTextCallBack;
import com.main.maybe.miplayer.binder.FullScreenMusicPlayerServiceBinder;
import com.main.maybe.miplayer.service.MusicPlayerService;
import com.main.maybe.miplayer.task.LoadingListTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MusicPlayerFragment extends Fragment {

    private SeekBar mSeekBar;
    private TextView mTotalTime;
    private TextView mCurrentTime;
    private ImageButton playPausedButton;
    private ImageButton playPreviousButton;
    private ImageButton playNextButton;
    private TextView playTitle;
    private TextView playAlbum;
    private TextView playArtist;

    MusicPlayerService mService;
    FullScreenMusicPlayerServiceBinder mBinder;
    ServiceConnection mConnection;
    SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    boolean mBound = false;
    int playPosition;

    int state;
    private List<HashMap<String, String>> songs = new ArrayList<>();

    private final String LOG_TAG = MusicPlayerFragment.class.getSimpleName();

    public MusicPlayerFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get the data
        Bundle bundle = getArguments();
        songs = (ArrayList<HashMap<String, String>>)bundle.getSerializable(LoadingListTask.songList);
        int isPlayMusic = bundle.getInt(LoadingListTask.isPlayMusic); // 是否播放音乐
        playPosition = bundle.getInt(LoadingListTask.playPosition);

        View rootView = inflater.inflate(R.layout.playmusic, container, false);

        playTitle = (TextView)rootView.findViewById(R.id.play_song_name);
        playAlbum = (TextView)rootView.findViewById(R.id.play_album);
        playArtist = (TextView)rootView.findViewById(R.id.play_singer);

        playPausedButton = (ImageButton)rootView.findViewById(R.id.play_paused);
        playPausedButton.setClickable(false);
        playPreviousButton = (ImageButton)rootView.findViewById(R.id.play_previous);
        playPreviousButton.setClickable(false);
        playNextButton = (ImageButton)rootView.findViewById(R.id.play_next);
        playNextButton.setClickable(false);

        mSeekBar = (SeekBar) rootView.findViewById(R.id.play_progress);
        initOnSeekBarChangeListener();
        initButtonOnClickListener();
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        mTotalTime = (TextView) rootView.findViewById(R.id.play_totaltime);
        mCurrentTime = (TextView) rootView.findViewById(R.id.play_currenttime);

        // 参数传递过来表示放歌
        if (isPlayMusic == 1){
            bindMusicService();
        }
        return rootView;
    }

    // bind the music service
    public void bindMusicService(){
        defineServiceConnection(); // we define our service connection mConnection
        Intent intent = new Intent(getActivity(), MusicPlayerService.class);
        intent.putExtra(MusicPlayerService.ACTIVITY_INDENTIFY, MusicPlayerService.FULLSCREEN_PLAYER_ACTIVITY);
        getActivity().bindService(intent, mConnection
                , Context.BIND_AUTO_CREATE);
    }

    // 初始化进度条的监听器
    private void initOnSeekBarChangeListener(){
        mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setProgress(progress);
                if (mService == null || !fromUser)
                    return;
                mService.skipToPoint(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mService == null)
                    return;
                if (mBound)
                    state = mService.changeState();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mService == null)
                    return;
                if (mBound)
                    state = mService.changeState();
            }
        };
    }

    // 获取与 service 的连接
    private void defineServiceConnection() {
        // 建立连接时开启了一个服务，并且返回了能够通信使用的Binder
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (!HomeActivity.isServiceWorked(MusicPlayerService.MUSIC_PLAYER_SERVICE_NAME, getActivity()))
                    getActivity().startService(new Intent(getActivity(), MusicPlayerService.class));
                mBinder = (FullScreenMusicPlayerServiceBinder) service;
                mService = mBinder.getService(new SeekBarTextCallBack() {
                    @Override
                    public void setCurrentTime(String time) {
                        if (mCurrentTime != null)
                            mCurrentTime.setText(time);
                    }

                    @Override
                    public void setTotalTime(String time) {
                        if (mTotalTime != null)
                            mTotalTime.setText(time);
                    }

                    @Override
                    public void setMusicTitle(String title) {
                        if (playTitle != null)
                            playTitle.setText(title);
                    }

                    @Override
                    public void setMusicArtist(String artist) {
                        if (playArtist != null)
                            playArtist.setText(artist);
                    }

                    @Override
                    public void setMusicAlbum(String album) {
                        if (playAlbum != null)
                            playAlbum.setText(album);
                    }

                    @Override
                    public void setImagePlay() {
                        if (playPausedButton != null)
                            playPausedButton.setImageResource(R.drawable.song_play);
                    }

                    @Override
                    public void setImagePaused() {
                        if (playPausedButton != null)
                            playPausedButton.setImageResource(R.drawable.song_pause);
                    }
                });

                state = mService.getState();
                mService.registerSeekBar(mSeekBar);

                mBound = true;

                if (songs != null){
                    mService.addMusicToQueue(songs);
                }
                Log.d(LOG_TAG, "Service is connected and well to go");

                // 成功建立连接之后开始放歌
                mService.play(playPosition);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBound = false;
                Log.d(LOG_TAG, "Service is disconnected");
            }
        };
    }

    // 当 fragment 隐藏不见时，可以解除绑定
    @Override
    public void onStop() {
        super.onStop();
        getActivity().unbindService(mConnection);
        mBound = false;
    }

    // 初始化页面的播放器按钮
    private void initButtonOnClickListener(){
        playPausedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService == null){
                    defineServiceConnection();
                    return;
                }
                if (mBound){
                    state = mService.changeState();
                    switch (state){
                        case MusicPlayerService.PLAYING:
                            playPausedButton.setImageResource(R.drawable.song_pause);
                            break;
                        case MusicPlayerService.PAUSED:
                            playPausedButton.setImageResource(R.drawable.song_play);
                            break;
                    }
                }
            }
        });

        playPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService == null)
                    defineServiceConnection();
                playPausedButton.setImageResource(R.drawable.song_play);
                mService.playPrevious();
            }
        });

        playNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set the icon play
                if (mService == null)
                    defineServiceConnection();
                playPausedButton.setImageResource(R.drawable.song_play);
                mService.playNext();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG_TAG, LOG_TAG + "is onDestroyView");
    }

}
