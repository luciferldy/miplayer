package com.main.maybe.miplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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

    public static final String ACTION_NOTIFICATION_PLAY_PAUSE = "action_notification_play_pause";
    public static final String ACTION_NOTIFICATION_PREVIOUS = "action_notification_previous";
    public static final String ACTION_NOTIFICATION_NEXT = "action_notification_next";

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

    // custom notification with button
    public void showButtonNotify(){

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Playing")
                .setAutoCancel(true);
        Notification notification = builder.build();
        notification.bigContentView = getExpandView();

        mNotificationManager.notify(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleNotificationIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    public void handleNotificationIntent( Intent intent ){
        if (intent != null && intent.getAction() != null){
            if (intent.getAction().equalsIgnoreCase( ACTION_NOTIFICATION_PLAY_PAUSE )){
                changeState();
            }
            else if ( intent.getAction().equalsIgnoreCase( ACTION_NOTIFICATION_PREVIOUS )){
                playPrevious();
            }else if ( intent.getAction().equalsIgnoreCase( ACTION_NOTIFICATION_NEXT )){
                playNext();
            }
        }
    }

    public RemoteViews getExpandView(){
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.musicnotification);
        mRemoteViews.setImageViewResource(R.id.notification_albumcover, R.drawable.ic_launcher);

        mRemoteViews.setTextViewText(R.id.notification_artist, mNowPlaying.getCurrentPlaying().getArtist());
        mRemoteViews.setTextViewText(R.id.notification_songtitle, mNowPlaying.getCurrentPlaying().getName());

        if(state == PLAYING){
            mRemoteViews.setImageViewResource(R.id.notification_play, R.drawable.song_pause);
        }else{
            mRemoteViews.setImageViewResource(R.id.notification_play, R.drawable.song_play);
        }

        Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);

        intent.setAction(ACTION_NOTIFICATION_PLAY_PAUSE);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_play, pendingIntent);

        intent.setAction(ACTION_NOTIFICATION_PREVIOUS);
        pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_previous, pendingIntent);

        intent.setAction(ACTION_NOTIFICATION_NEXT);
        pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_next, pendingIntent);

        return mRemoteViews;
    }
}
