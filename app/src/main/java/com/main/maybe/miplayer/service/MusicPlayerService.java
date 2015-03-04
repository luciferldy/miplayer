package com.main.maybe.miplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.SeekBar;

import com.main.maybe.miplayer.HeadPhoneBroadcastReceiver;
import com.main.maybe.miplayer.Queue;
import com.main.maybe.miplayer.binder.MusicPlayerServiceBinder;
import com.main.maybe.miplayer.music.Music;

import java.util.List;

/**
 * Created by Maybe霏 on 2015/3/4.
 */
public class MusicPlayerService extends Service implements MusicPlayerServiceInterface{

    public final static int PAUSED = 0;
    public final static int PLAYING = 1;

    private int state;

    private MusicPlayerServiceBinder mMusicPlayerServiceBinder;
    private Queue mNowPlaying;
    private MediaPlayer mMediaPlayer;
    private OnCompletionListener mCompletionListener;

    private HeadPhoneBroadcastReceiver mHeadPhoneBroadcastReceiver;
    private SeekBar mSeekbar;

    private AsyncTask<Void, Void, Void> seekBarChanger;


    @Override
    public void addMusicToQueue(Music music) {

    }

    @Override
    public void addMusicToQueue(List<Music> music) {

    }

    @Override
    public void removeMusicFromQueue(Music music) {

    }

    @Override
    public void skipToPoint(int time) {

    }

    @Override
    public void play() {

    }

    @Override
    public void play(int position) {

    }

    @Override
    public void pause() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        mMusicPlayerServiceBinder = new MusicPlayerServiceBinder(this, this);
        return null;
    }

    public int getState(){
        return state;
    }
}
