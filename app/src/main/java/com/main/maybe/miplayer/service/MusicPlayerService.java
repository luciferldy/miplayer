package com.main.maybe.miplayer.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.SeekBar;
import android.widget.Toast;

import com.main.maybe.miplayer.HeadPhoneBroadcastReceiver;
import com.main.maybe.miplayer.Queue;
import com.main.maybe.miplayer.binder.MusicPlayerServiceBinder;
import com.main.maybe.miplayer.music.Music;

import java.io.IOException;
import java.util.ArrayList;
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
    private SeekBar mSeekBar;

    private AsyncTask<Void, Void, Void> seekBarChanger;

    public void registerSeekBar(SeekBar mSeekBar){
        this.mSeekBar = mSeekBar;
    }

    @Override
    public void addMusicToQueue(Music music) {
        mNowPlaying.addMusicToQueue(music);
    }

    @Override
    public void addMusicToQueue(List<Music> music) {
        mNowPlaying.addMusicToQueue(music);
    }

    // 切换列表
    public void changeQueue(ArrayList<Music> list){
        mNowPlaying.clearQueue();
        mNowPlaying.addMusicToQueue(list);
    }

    @Override
    public void removeMusicFromQueue(Music music) {
        mNowPlaying.removeMusicToQueue(music);
    }

    @Override
    public void skipToPoint(int point) {
        mMediaPlayer.seekTo(point);
    }

    @Override
    public void pause() {
        state = PAUSED;
        mMediaPlayer.pause();
    }

    public int changeState(){
        switch (state){
            case PLAYING:
                pause();
                break;
            case PAUSED:
                play();
                break;
        }
        return state;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mMusicPlayerServiceBinder = new MusicPlayerServiceBinder(this, this);
        state = PLAYING;

        // the now playing queue
        mNowPlaying = new Queue();
        // the media player
        mMediaPlayer = new MediaPlayer();

        mCompletionListener = new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNext();
            }
        };
        mMediaPlayer.setOnCompletionListener(mCompletionListener);

        mHeadPhoneBroadcastReceiver = new HeadPhoneBroadcastReceiver();
        registerReceiver(mHeadPhoneBroadcastReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        mHeadPhoneBroadcastReceiver.registerMusicPlayerService(this);

        return mMusicPlayerServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(mHeadPhoneBroadcastReceiver);

        if (seekBarChanger != null)
            seekBarChanger.cancel(false);
        seekBarChanger = null;

        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();

        Toast.makeText(this, "Unbind with state:　"+((state == PLAYING) ? "PLAYING" : "PAUSED"),
                Toast.LENGTH_SHORT).show();
        return true;
    }

    public int getState(){
        return state;
    }

    @Override
    public void play() {
        state = PLAYING;
        mMediaPlayer.start();
    }

    @Override
    public void play(int position) {
        playFetched(mNowPlaying.playGet(position).getMusicLocation());
    }

    public synchronized void playNext(){
        playFetched(mNowPlaying.next().getMusicLocation());
    }

    public synchronized void playPrevious(){
        playFetched(mNowPlaying.previous().getMusicLocation());
    }

    // 播放选中
    public synchronized void playFetched(String path){
        state = PLAYING;
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        try{
            mMediaPlayer.setDataSource(path);

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    int totalTime = mNowPlaying.getCurrentPlaying().getTime();
                    mSeekBar.setMax(totalTime*1000);

                    int minutes = totalTime/60, seconds = totalTime%60;
                    if (seconds >= 10){
                        mMusicPlayerServiceBinder.setTotalTime("/ "+minutes+":"+seconds);
                    }else{
                        mMusicPlayerServiceBinder.setTotalTime("/ "+minutes+":0"+seconds);
                    }

                    play();
                    setSeekBarTracker();
                }
            });
            mMediaPlayer.prepare();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }catch (SecurityException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setSeekBarTracker(){
        if (seekBarChanger != null){
            seekBarChanger.cancel(false);
        }
        seekBarChanger = null;
        seekBarChanger = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                while(mMediaPlayer != null && mMediaPlayer.getCurrentPosition() <mMediaPlayer.getDuration()){
                    if (state == PLAYING){
                        int currentPosition = mMediaPlayer.getCurrentPosition();
                        mSeekBar.setProgress(currentPosition);
                    }
                    try{
                        Thread.sleep(100);
                    }catch (InterruptedException e){

                    }
                }
                return null;
            }
        };
        seekBarChanger.execute();
    }
}
