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

import com.main.maybe.miplayer.HeadPhoneBroadcastReceiver;
import com.main.maybe.miplayer.NotificationBroadcastReceiver;
import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.binder.MusicPlayerServiceBinder;
import com.main.maybe.miplayer.task.LoadingListTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Maybe霏 on 2015/3/4.
 */
public class MusicPlayerService extends Service implements MusicPlayerServiceInterface{

    public final static int PAUSED = 0;
    public final static int PLAYING = 1;
    public final static int BOTTOM_PLAYER_ACTIVITY = 1;
    public final static int FULLSCREEN_PLAYER_ACTIVITY = 2;
    public final static String ACTIVITY_INDENTIFY = "ACTIVITY_INDENTIFY";

    public final static String MUSIC_PLAYER_SERVICE_NAME = "com.main.maybe.miplayer.service.MusicPlayerService";

    public final static String pathName = "/sdcard/miplayer/";
    public final static String fileName = "playlist.txt";

    public final static String PlayingNumber = "PlayingNumber"; // 播放的序号

    public final static int PLAY_MUSIC_NOTIFICATION_ID = 1;

//    public static final String ACTION_NOTIFICATION_PLAY_PAUSE = "action_notification_play_pause";
//    public static final String ACTION_NOTIFICATION_PREVIOUS = "action_notification_previous";
//    public static final String ACTION_NOTIFICATION_NEXT = "action_notification_next";

    private int state = PAUSED;
    private int current_position = 0;

    private MusicPlayerServiceBinder mBinder;
    private ArrayList<HashMap<String, String>> mNowPlaying;
    private MediaPlayer mMediaPlayer;
    private OnCompletionListener mCompletionListener;

    private HeadPhoneBroadcastReceiver mHeadPhoneBroadcastReceiver;
    private NotificationBroadcastReceiver mNotificationBroadcastReceiver;
    private SeekBar mSeekBar;

    private AsyncTask<Integer, Void, Void> seekBarChanger;

    private final String LOG_TAG = MusicPlayerService.class.getSimpleName();

    private Notification mNotification;
    public NotificationManager mNotificationManager;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        registerBroadcastReceiver();
        Log.d(LOG_TAG, LOG_TAG + " is onCreate");
    }

    public boolean bindMusicPlayer(){

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        int currentPosition = (int)msg.obj;
                        if (mBinder != null || currentPosition == 0){

                            int minutes = (currentPosition/1000)/60, seconds = (currentPosition/1000)%60;
                            if (minutes >= 10 && seconds >= 10){
                                mBinder.setCurrentTime("" + minutes + ":" + seconds);
                            }else if (minutes >= 10 && seconds < 10){
                                mBinder.setCurrentTime("" + minutes + ":0" + seconds);
                            }else if (minutes < 10 && seconds >= 10){
                                mBinder.setCurrentTime("0" + minutes + ":" + seconds);
                            }else {
                                mBinder.setCurrentTime("0" + minutes + ":0" + seconds);
                            }
                        }
                        break;
                    case 1:
                        Log.d(LOG_TAG, "handle message fail.");
                        break;
                }
            }
        };

        mBinder = new MusicPlayerServiceBinder(this, this);

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // /s
                int totalTime = Integer.parseInt(mNowPlaying.get(current_position).get(LoadingListTask.duration_t)) / 1000;
                // SeekBar 的长度是 ms 级的
                if (mSeekBar != null)
                    mSeekBar.setMax(totalTime * 1000);

                int minutes = totalTime / 60, seconds = totalTime % 60;
                if (minutes >= 10 && seconds >= 10) {
                    mBinder.setTotalTime("" + minutes + ":" + seconds);
                } else if (minutes >= 10 && seconds < 10) {
                    mBinder.setTotalTime("" + minutes + ":0" + seconds);
                } else if (minutes < 10 && seconds >= 10) {
                    mBinder.setTotalTime("0" + minutes + ":" + seconds);
                } else {
                    mBinder.setTotalTime("0" + minutes + ":0" + seconds);
                }

                // set music information
                mBinder.setMusicTitle(mNowPlaying.get(current_position).get(LoadingListTask.songName));
                mBinder.setMusicAlbum(mNowPlaying.get(current_position).get(LoadingListTask.albumName));
                mBinder.setMusicArtist(mNowPlaying.get(current_position).get(LoadingListTask.artistName));

                // 将 getDuration 和 play 放在这里保证获取信息时加载完成
                if (mSeekBar != null)
                    setSeekBarTracker(mMediaPlayer.getDuration());
                play();
            }
        });
        return true;
    }

    /*
     * 判断Service中是否有队列
     * true 有播放队列
     * false 没有播放队列
     */
//    public boolean hasPlayingQueue(){
//        if ( mNowPlaying != null && mNowPlaying.size() != 0 )
//            return true;
//        else
//            return false;
//    }

    public ArrayList<HashMap<String, String>> getPlayingQueue(){
        return mNowPlaying;
    }

    public int getCurrentPosition(){
        return current_position;
    }

//    public void setCurrentPosition(int currentPosition){
//        this.current_position = currentPosition;
//    }

    public boolean storeSerializableList(){
        ArrayList<HashMap<String, String>> stores;
        /*
         * 可能需要先对磁盘的挂载情况进行判断
         *
         */
        try {
            File path = new File(pathName);
            File file = new File(pathName+fileName);
            if (!path.exists()){
                Log.d(LOG_TAG, "path create");
                path.mkdir();
            }
            // if exist, delete and create
            if (file.exists()){
                file.delete();
                file = new File(pathName+fileName);
            }else {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            stores = mNowPlaying;
            HashMap<String, String> map = new HashMap<>();
            map.put(PlayingNumber, current_position+"");
            stores.add(map);

            oos.writeObject(stores);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        Log.d(LOG_TAG, "store the current list serializable");
        return true;
    }

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

    public boolean registerBroadcastReceiver(){

        // register the BroadcastReceiver
        mHeadPhoneBroadcastReceiver = new HeadPhoneBroadcastReceiver();
        registerReceiver(mHeadPhoneBroadcastReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        mHeadPhoneBroadcastReceiver.registerMusicPlayerService(this);

        mNotificationBroadcastReceiver = new NotificationBroadcastReceiver();
        IntentFilter musicIntentFilter = new IntentFilter();
        musicIntentFilter.addAction(NotificationBroadcastReceiver.PLAY_PAUSE);
        musicIntentFilter.addAction(NotificationBroadcastReceiver.PLAY_PREVIOUS);
        musicIntentFilter.addAction(NotificationBroadcastReceiver.PLAY_NEXT);
        musicIntentFilter.addAction(NotificationBroadcastReceiver.PLAYER_CANCEL);
        registerReceiver(mNotificationBroadcastReceiver, musicIntentFilter);
        mNotificationBroadcastReceiver.registerMusicPlayerService(MusicPlayerService.this);
        return true;
    }

    @Override
    public void removeMusicFromQueue(HashMap<String, String> music) {
        // it can remove object
        mNowPlaying.remove(music);
    }

    public void clearMusicQueue(){
        mNowPlaying.clear();
    }

    @Override
    public void skipToPoint(int point) {
        mMediaPlayer.seekTo(point);
    }

    @Override
    public void play() {
        state = PLAYING;
        mMediaPlayer.start();
        if (mBinder != null)
            mBinder.setImagePaused();
        showNotification();
    }

    @Override
    public void pause() {
        state = PAUSED;
        mMediaPlayer.pause();
        if (mBinder != null)
            mBinder.setImagePlay();
        showNotification();
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

        state = PLAYING;
        // the now playing queue
        mNowPlaying = new ArrayList<>();
        // the media player
        mMediaPlayer = new MediaPlayer();
        mCompletionListener = new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNext();
            }
        };
        mMediaPlayer.setOnCompletionListener(mCompletionListener);

        // bind the music service
        bindMusicPlayer();

        // 当正在有音乐播放时
//        if (state == PLAYING)
//            mBinder.setImagePaused();
//        setSeekBarTracker(Integer.parseInt(mNowPlaying.get(current_position).get(LoadingListTask.duration)));

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (seekBarChanger != null)
            seekBarChanger.cancel(true);
        seekBarChanger = null;

        Log.d(LOG_TAG, "Unbind with state:　" + ((state == PLAYING) ? "PLAYING" : "PAUSED" ));
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
        if ((current_position+1) == mNowPlaying.size())
            current_position = 0;
        else
            current_position++;
        playFetched(mNowPlaying.get(current_position).get(LoadingListTask.path));
    }

    public synchronized void playPrevious(){
        if ((current_position-1) == -1)
            current_position = mNowPlaying.size()-1;
        else
            current_position--;
        playFetched(mNowPlaying.get(current_position).get(LoadingListTask.path));
    }

    @Override
    public void onDestroy() {

        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();

        if (mHeadPhoneBroadcastReceiver != null)
            unregisterReceiver(mHeadPhoneBroadcastReceiver);
        if (mNotificationBroadcastReceiver != null)
            unregisterReceiver(mNotificationBroadcastReceiver);
        // store the music list
        storeSerializableList();
        mNotificationManager.cancel(PLAY_MUSIC_NOTIFICATION_ID);
        Log.d(LOG_TAG, LOG_TAG+" is onDestroy");
        super.onDestroy();
    }

    // play music selected
    public synchronized void playFetched(String path) {
        playSetting(path);
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
    public void setSeekBarTracker(int duration){
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
                .setAutoCancel(false);
        mNotification = builder.build();
        mNotification.bigContentView = getExpandView();
        mNotification.flags = Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(PLAY_MUSIC_NOTIFICATION_ID, mNotification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    // 这是为了大屏的 Notification
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

        Intent playIntent = new Intent(NotificationBroadcastReceiver.PLAY_PAUSE);
        PendingIntent pIntentPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, playIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_play, pIntentPlay);


        Intent previousIntent = new Intent(NotificationBroadcastReceiver.PLAY_PREVIOUS);
        PendingIntent pIntentPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previousIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_previous, pIntentPrevious);

        Intent nextIntent = new Intent(NotificationBroadcastReceiver.PLAY_NEXT);
        PendingIntent pIntentNext = PendingIntent.getBroadcast(getApplicationContext(), 0, nextIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_next, pIntentNext);

        Intent cancelIntent = new Intent(NotificationBroadcastReceiver.PLAYER_CANCEL);
        PendingIntent pIntentCancel = PendingIntent.getBroadcast(getApplicationContext(), 0, cancelIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_cancel, pIntentCancel);

        return mRemoteViews;
    }
}
