package com.main.maybe.miplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Toast;

import com.main.maybe.miplayer.HeadPhoneBroadcastReceiver;
import com.main.maybe.miplayer.Queue;
import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.binder.MusicPlayerServiceBinder;
import com.main.maybe.miplayer.music.Music;

import java.io.IOException;
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

    private String LOG_TAG = MusicPlayerService.class.getSimpleName();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    int currentPosition = (int)msg.obj;
                    if (mMusicPlayerServiceBinder != null || currentPosition == 0){

                        int minutes = (currentPosition/1000)/60, seconds = (currentPosition/1000)%60;
                        if (minutes >= 10 && seconds >= 10){
                            mMusicPlayerServiceBinder.setCurrentTime("" + minutes + ":" + seconds);
                        }else if (minutes >= 10 && seconds < 10){
                            mMusicPlayerServiceBinder.setCurrentTime("" + minutes + ":0" + seconds);
                        }else if (minutes < 10 && seconds >= 10){
                            mMusicPlayerServiceBinder.setCurrentTime("0" + minutes + ":" + seconds);
                        }else {
                            mMusicPlayerServiceBinder.setCurrentTime("0" + minutes + ":0" + seconds);
                        }
                    }
                    break;
                case 1:
                    Log.d(LOG_TAG, "handle message fail.");
                    break;
            }

        }
    };

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

    @Override
    public void removeMusicFromQueue(Music music) {
        mNowPlaying.removeMusicToQueue(music);
    }

    @Override
    public void skipToPoint(int point) {
        mMediaPlayer.seekTo(point);
    }

    @Override
    public void play() {
        state = PLAYING;
        showButtonNotify();
        mMediaPlayer.start();
        if (mMusicPlayerServiceBinder != null)
            mMusicPlayerServiceBinder.setImagePaused();
    }

    @Override
    public void pause() {
        state = PAUSED;
        mMediaPlayer.pause();
        if (mMusicPlayerServiceBinder != null)
            mMusicPlayerServiceBinder.setImagePlay();
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
    public void play(int position) {
        playFetched(mNowPlaying.playGet(position).getMusicLocation());
    }

    public synchronized void playNext(){
        if (mMusicPlayerServiceBinder != null)
            mMusicPlayerServiceBinder.setImagePlay();
        playFetched(mNowPlaying.next().getMusicLocation());
    }

    public synchronized void playPrevious(){
        if (mMusicPlayerServiceBinder != null)
            mMusicPlayerServiceBinder.setImagePlay();
        playFetched(mNowPlaying.previous().getMusicLocation());
    }

    // play music selected
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
                    if (minutes >= 10 && seconds >= 10){
                        mMusicPlayerServiceBinder.setTotalTime(""+minutes+":"+seconds);
                    }else if (minutes >= 10 && seconds < 10){
                        mMusicPlayerServiceBinder.setTotalTime(""+minutes+":0"+seconds);
                    }else if (minutes < 10 && seconds >= 10){
                        mMusicPlayerServiceBinder.setTotalTime("0"+minutes+":"+seconds);
                    }else {
                        mMusicPlayerServiceBinder.setTotalTime("0"+minutes+":0"+seconds);
                    }

                    // set music information
                    mMusicPlayerServiceBinder.setMusicTitle(mNowPlaying.getCurrentPlaying().getName());
                    mMusicPlayerServiceBinder.setMusicAlbum(mNowPlaying.getCurrentPlaying().getAlbum());
                    mMusicPlayerServiceBinder.setMusicArtist(mNowPlaying.getCurrentPlaying().getArtist());

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
            int currentPosition = 0;
            @Override
            protected Void doInBackground(Void... params) {
                while(mMediaPlayer != null && mMediaPlayer.getCurrentPosition() < mMediaPlayer.getDuration()){
                    if (state == PLAYING){
                        currentPosition = mMediaPlayer.getCurrentPosition();
                        mSeekBar.setProgress(currentPosition);
                        // send to ui thread
                        handler.obtainMessage(0, currentPosition).sendToTarget();
                    }
                    try{
                        Thread.sleep(100);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                return null;
            }

        };
        seekBarChanger.execute();
    }

    /**
     * 带按钮的通知栏
     */
    public void showButtonNotify(){

        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.musicnotification);
        mRemoteViews.setImageViewResource(R.id.notification_albumcover, R.drawable.ic_launcher);
        //API3.0 以上的时候显示按钮，否则消失
        mRemoteViews.setTextViewText(R.id.notification_artist, mNowPlaying.getCurrentPlaying().getArtist());
        mRemoteViews.setTextViewText(R.id.notification_songtitle, mNowPlaying.getCurrentPlaying().getName());
        // 状态是播放
        if(state == PLAYING){
            mRemoteViews.setImageViewResource(R.id.notification_play, R.drawable.song_pause);
        }else{
            mRemoteViews.setImageViewResource(R.id.notification_play, R.drawable.song_play);
        }
        mRemoteViews.setImageViewResource(R.id.notification_previous, R.drawable.ic_music_previous);
        mRemoteViews.setImageViewResource(R.id.notification_next, R.drawable.ic_music_next);
        //点击的事件处理
        Intent buttonIntent = new Intent(getApplicationContext(), MusicPlayerService.class);

        /* 上一首按钮 */
//        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PREV_ID);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_previous, intent_prev);

        /* 播放/暂停  按钮 */
//        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PALY_ID);
        PendingIntent intent_paly = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_play, intent_paly);

        /* 下一首 按钮  */
//        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
        PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_next, intent_next);

        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());

        mBuilder.setContent(mRemoteViews)
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker("正在播放")
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(true)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher);
        Notification notify = mBuilder.build();
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(8, notify);
    }
}
