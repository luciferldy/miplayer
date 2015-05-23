package com.main.maybe.miplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.binder.MusicPlayerServiceBinder;
import com.main.maybe.miplayer.task.LoadingListTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Maybe霏 on 2015/3/4.
 */
public class MusicPlayerService extends Service implements MusicPlayerServiceInterface{

    public final static int PAUSED = 0;
    public final static int PLAYING = 1;

    private final static int PLAY_MUSIC_NOTIFICATION_ID = 1;

    public static final String ACTION_NOTIFICATION_PLAY_PAUSE = "action_notification_play_pause";
    public static final String ACTION_NOTIFICATION_PREVIOUS = "action_notification_previous";
    public static final String ACTION_NOTIFICATION_NEXT = "action_notification_next";

    private int state = PAUSED;
    private int current_position = 0;

    private MusicPlayerServiceBinder mMusicPlayerServiceBinder;
    private ArrayList<HashMap<String, String>> mNowPlaying;
    private MediaPlayer mMediaPlayer;
    private OnCompletionListener mCompletionListener;

    private HeadPhoneBroadcastReceiver mHeadPhoneBroadcastReceiver;
    private SeekBar mSeekBar;

    private AsyncTask<Integer, Void, Void> seekBarChanger;

    private String LOG_TAG = MusicPlayerService.class.getSimpleName();

    private Notification mNotification;
    private NotificationManager mNotificationManager;

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
    public void addMusicToQueue(HashMap<String, String> music) {
        mNowPlaying.add(music);
    }

    @Override
    public void addMusicToQueue(List<HashMap<String, String>> music) {
        mNowPlaying = new ArrayList<>();
        for (int i = 0 ; i < music.size() ; i++){
            mNowPlaying.add(music.get(i));
        }
    }

    @Override
    public void removeMusicFromQueue(HashMap<String, String> music) {
        // it can remove object
        mNowPlaying.remove(music);
    }

    @Override
    public void skipToPoint(int point) {
        mMediaPlayer.seekTo(point);
    }

    @Override
    public void play() {
        state = PLAYING;
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
        mNowPlaying = new ArrayList<>();
        // the media player
        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // /s
                int totalTime = Integer.parseInt(mNowPlaying.get(current_position).get(LoadingListTask.duration_t))/1000;
                // SeekBar 的长度是 ms 级的
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
                mMusicPlayerServiceBinder.setMusicTitle(mNowPlaying.get(current_position).get(LoadingListTask.songName));
                mMusicPlayerServiceBinder.setMusicAlbum(mNowPlaying.get(current_position).get(LoadingListTask.albumName));
                mMusicPlayerServiceBinder.setMusicArtist(mNowPlaying.get(current_position).get(LoadingListTask.artistName));

                // 将 getDuration 和 play 放在这里保证获取信息时加载完成
                setSeekBarTracker(mMediaPlayer.getDuration());
                play();
            }
        });

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
        this.current_position = position;
        playFetched(mNowPlaying.get(position).get(LoadingListTask.path));
    }

    public synchronized void playNext(){
        if (mMusicPlayerServiceBinder != null){
//            mMusicPlayerServiceBinder.setImagePlay();
            if ((current_position+1) == mNowPlaying.size())
                current_position = 0;
            else
                current_position++;
            playFetched(mNowPlaying.get(current_position).get(LoadingListTask.path));
        }
    }

    public synchronized void playPrevious(){
        if (mMusicPlayerServiceBinder != null){
//            mMusicPlayerServiceBinder.setImagePlay();
            if ((current_position-1) == -1)
                current_position = mNowPlaying.size()-1;
            else
                current_position--;
            playFetched(mNowPlaying.get(current_position).get(LoadingListTask.path));
        }
    }

    // play music selected
    public synchronized void playFetched(String path) {
        playSetting(path);
        showNotification();
    }

    public void playSetting(String path){
        try{

            if (mMediaPlayer.isPlaying()){
                // 切换状态，切换图标
                changeState();
            }
            if (seekBarChanger != null)
                seekBarChanger.cancel(true);
            // 每次播放前都要重置一下
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path);
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

    // 开启追踪 SeekBar 变化的异步任务
    private void setSeekBarTracker(int duration){
        if (seekBarChanger != null){
            seekBarChanger.cancel(true);
        }
        seekBarChanger = null;
        seekBarChanger = new AsyncTask<Integer, Void, Void>() {

            int currentPosition = 0;
            @Override
            protected Void doInBackground(Integer... params) {
                try {
                    while(mMediaPlayer != null && mMediaPlayer.getCurrentPosition() < params[0]){
                        if (state == PLAYING){
                            currentPosition = mMediaPlayer.getCurrentPosition();
                            mSeekBar.setProgress(currentPosition);
                            // send to ui thread
                            handler.obtainMessage(0, currentPosition).sendToTarget();
                        }
                        Thread.sleep(100);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        };
        seekBarChanger.execute(duration);
    }

    // custom notification with button
    public void showNotification(){
        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Playing")
                .setAutoCancel(true);
        mNotification = builder.build();
        mNotification.bigContentView = getExpandView();

        mNotificationManager.notify(PLAY_MUSIC_NOTIFICATION_ID, mNotification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    // 这里应该注册广播去监听
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

    // this is for notification
    public RemoteViews getExpandView(){
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.musicnotification);

        Bitmap albumCover = BitmapFactory.decodeResource(this.getResources(), R.drawable.album_cover);
        if (albumCover != null){
            // two ways to get the album cover
//            Uri albumCoverUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), albumCover, null, null));
//            mRemoteViews.setImageViewUri(R.id.notification_albumcover, albumCoverUri);
            mRemoteViews.setImageViewBitmap(R.id.notification_albumcover, albumCover);
        }
        else
            mRemoteViews.setImageViewResource(R.id.notification_albumcover, R.drawable.ic_launcher);

        mRemoteViews.setTextViewText(R.id.notification_artist, mNowPlaying.get(current_position).get(LoadingListTask.artistName));
        mRemoteViews.setTextViewText(R.id.notification_songtitle, mNowPlaying.get(current_position).get(LoadingListTask.songName));

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
