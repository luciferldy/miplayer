package com.main.maybe.miplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.main.maybe.miplayer.AlbumCoverHelper;
import com.main.maybe.miplayer.receiver.HeadPhoneBroadcastReceiver;
import com.main.maybe.miplayer.receiver.NotificationBroadcastReceiver;
import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.binder.MusicPlayerServiceBinder;
import com.main.maybe.miplayer.model.SingleBean;
import com.main.maybe.miplayer.util.CommonUtils;
import com.main.maybe.miplayer.util.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maybe霏 on 2015/3/4.
 */
public class MusicPlayerService extends Service implements MusicPlayerServiceInterface{

    public static final String MUSIC_PLAYER_SERVICE_NAME = "com.main.maybe.miplayer.service.MusicPlayerService";
    private static final String ACTION_PLAY = "com.main.maybe.miplayer.PLAY";
    private final String LOG_TAG = MusicPlayerService.class.getSimpleName();

    public final static int PAUSED = 0;
    public final static int PLAYING = 1;
    public final static String ACTIVITY_INDENTIFY = "ACTIVITY_INDENTIFY";



    public final static String pathName = "/sdcard/miplayer/";
    public final static String fileName = "playlist.txt";

    public final static String PlayingNumber = "PlayingNumber"; // 播放的序号

    public final static int PLAY_MUSIC_NOTIFICATION_ID = 1;

//    public static final String ACTION_NOTIFICATION_PLAY_PAUSE = "action_notification_play_pause";
//    public static final String ACTION_NOTIFICATION_PREVIOUS = "action_notification_previous";
//    public static final String ACTION_NOTIFICATION_NEXT = "action_notification_next";

    private int state = PAUSED;

    private Handler mSeekBarHandler;
    private AsyncTask<Integer, Void, Void> mSeekBarTracker;

    private MusicPlayerServiceBinder mBinder;
    private List<SingleBean> mPlayQueue; // playing list
    private int mPlayPosition = 0;

    private MediaPlayer mMediaPlayer;

    private HeadPhoneBroadcastReceiver mHeadPhoneBroadcastReceiver;
    private NotificationBroadcastReceiver mNotificationBroadcastReceiver;
    private Notification mNotification;
    public NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
//        registerBroadcastReceiver();
        Logger.i(LOG_TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i(LOG_TAG, "onStartCommand");
        if (intent.getAction().equals(ACTION_PLAY)) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNext();
                }
            });
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // init title, artist, duration, etc

                    int duration = Integer.parseInt(mPlayQueue.get(mPlayPosition).getDuration());
                    mBinder.setTotalTime(CommonUtils.timeFormatMs2Str(duration));
                    mBinder.setMusicTitle(mPlayQueue.get(mPlayPosition).getTitle());
                    mBinder.setMusicAlbum(mPlayQueue.get(mPlayPosition).getAlbum());
                    mBinder.setMusicArtist(mPlayQueue.get(mPlayPosition).getArtist());
                    startSeekBarTracker(mMediaPlayer.getDuration());
                    play();
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // state Error, need reset to idle
                    return true;
                }
            });

            mSeekBarHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case 0:
                            int ms = (int) msg.obj;
                            if (mBinder != null || ms == 0){
                                mBinder.setCurrentTime(CommonUtils.timeFormatMs2Str(ms));
                            }
                            break;
                        case 1:
                            Log.d(LOG_TAG, "handle message fail match.");
                            break;
                    }
                }
            };
        }
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {

        mBinder = new MusicPlayerServiceBinder(this, this);
        return mBinder;

    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mSeekBarTracker != null)
            mSeekBarTracker.cancel(true);
        mSeekBarTracker = null;

        Logger.i(LOG_TAG, "onUnbind with state:　" + ((state == PLAYING) ? "PLAYING" : "PAUSED" ));
        return true;
    }

    @Override
    public void onDestroy() {

        Logger.i(LOG_TAG, " onDestroy");
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        super.onDestroy();

    }

    public int getPlayingPosition(){
        if (mMediaPlayer != null)
            return mMediaPlayer.getCurrentPosition();
        else
            return 0;
    }

    /**
     * 获得播放列表
     * @return
     */
    public List<SingleBean> getPlayingQueue(){
        return mPlayQueue;
    }

    /**
     * 获得当前播放音乐在播放列表中的位置
     * @return
     */
    public int getCurrentPosition(){
        return mPlayPosition;
    }

    public boolean storeSerializableList(){
        List<SingleBean> stores = new ArrayList<>();
        /**
         * 可能需要先对磁盘的挂载情况进行判断
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
            stores.addAll(mPlayQueue);
            SingleBean bean = new SingleBean();
            bean.setDuration(String.valueOf(mPlayPosition));
            stores.add(bean);
            oos.writeObject(stores);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        Log.d(LOG_TAG, "store the current list serializable");
        return true;
    }

    @Override
    public void addMusicToQueue(SingleBean bean) {
        if (mPlayQueue == null) {
            mPlayQueue = new ArrayList<>();
        }
        mPlayQueue.add(bean);
    }

    @Override
    public void addMusicToQueue(List<SingleBean> songs) {
        if (mPlayQueue == null) {
            mPlayQueue = new ArrayList<>();
        }
        mPlayQueue.addAll(songs);
    }

    @Override
    public void removeMusicFromQueue(SingleBean bean) {
        mPlayQueue.remove(bean);
    }

    @Override
    public void removeMusicFromQueue(List<SingleBean> songs) {
        mPlayQueue.removeAll(songs);
    }

    @Override
    public void skipToPoint(int point) {
        mMediaPlayer.seekTo(point);
    }

    @Override
    public void play() {

        mMediaPlayer.start();
        if (mBinder != null)
            mBinder.play();

    }

    @Override
    public void pause() {

        mMediaPlayer.pause();
        if (mBinder != null)
            mBinder.pause();

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

    /**
     * 清空播放列表
     */
    public void clearMusicQueue(){
        mPlayQueue.clear();
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



    public int getState(){
        return state;
    }

    @Override
    public void play(int position) {
        this.mPlayPosition = position;
        playFetched(mPlayQueue.get(position).getData());
    }

    public synchronized void playNext(){
        if ((mPlayPosition +1) == mPlayQueue.size())
            mPlayPosition = 0;
        else
            mPlayPosition++;
        playFetched(mPlayQueue.get(mPlayPosition).getData());
    }

    public synchronized void playPrevious(){
        if ((mPlayPosition -1) == -1)
            mPlayPosition = mPlayQueue.size()-1;
        else
            mPlayPosition--;
        playFetched(mPlayQueue.get(mPlayPosition).getData());
    }

    // play music selected
    public synchronized void playFetched(String path) {
        playSetting(path);
        storeSerializableList();
    }

    public void playSetting(String path){
        try{

            if (mMediaPlayer.isPlaying()){
                // 切换状态，切换图标
                changeState();
            }
            if (mSeekBarTracker != null)
                mSeekBarTracker.cancel(true);
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();

        } catch (IllegalArgumentException e){
            e.printStackTrace();
        } catch (SecurityException e){
            e.printStackTrace();
        } catch (IllegalStateException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 追踪 MediaPlayer 的播放状态，更新 Activity 的 SeekBar
     * @param duration
     */
    public void startSeekBarTracker (int duration) {
        if (mSeekBarTracker != null){
            mSeekBarTracker.cancel(true);
        }
        mSeekBarTracker = null;
        mSeekBarTracker = new AsyncTask<Integer, Void, Void>() {

            int ms = 0;
            @Override
            protected Void doInBackground(Integer... params) {
                try {
                    while(mMediaPlayer != null && mMediaPlayer.getCurrentPosition() < params[0]){
                        if (state == PLAYING){
                            ms = mMediaPlayer.getCurrentPosition();
//                            mSeekBar.setProgress(ms);
                            // send to ui thread
                            mSeekBarHandler.obtainMessage(0, ms).sendToTarget();
                        }
                        Thread.sleep(100);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        };
        mSeekBarTracker.execute(duration);
    }

    // custom notification with button
    public void showNotification(){
        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Playing")
                .setAutoCancel(false);
        mNotification = builder.build();
//        mNotification.bigContentView = getExpandView();
        mNotification.flags |= Notification.FLAG_NO_CLEAR;
//        mNotificationManager.notify(PLAY_MUSIC_NOTIFICATION_ID, mNotification);
        startForeground(PLAY_MUSIC_NOTIFICATION_ID, mNotification);
    }


    // 这是为了大屏的 Notification
    public RemoteViews getExpandView(){
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.play_note);


        int albumId = Integer.parseInt(mPlayQueue.get(mPlayPosition).getAlbumId());
        int songId = Integer.parseInt(mPlayQueue.get(mPlayPosition).getId());
        Bitmap albumCover = AlbumCoverHelper.getArtwork(getApplicationContext(), songId, albumId, true, true);

        if (albumCover != null){
            // two ways to get the album cover
//            Uri albumCoverUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), albumCover, null, null));
//            mRemoteViews.setImageViewUri(R.id.notification_albumcover, albumCoverUri);
            mRemoteViews.setImageViewBitmap(R.id.note_album_cover, albumCover);
        }
        else
            mRemoteViews.setImageViewResource(R.id.note_album_cover, R.drawable.list_cover_alb);

        mRemoteViews.setTextViewText(R.id.single_name, mPlayQueue.get(mPlayPosition).getTitle());
        mRemoteViews.setTextViewText(R.id.artist_name, mPlayQueue.get(mPlayPosition).getArtist());

        if(state == PLAYING){
            mRemoteViews.setImageViewResource(R.id.note_play, R.drawable.note_btn_pause_white);
        }else{
            mRemoteViews.setImageViewResource(R.id.note_play, R.drawable.note_btn_play_white);
        }

        Intent playIntent = new Intent(NotificationBroadcastReceiver.PLAY_PAUSE);
        PendingIntent pIntentPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, playIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.note_play, pIntentPlay);


//        Intent previousIntent = new Intent(NotificationBroadcastReceiver.PLAY_PREVIOUS);
//        PendingIntent pIntentPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previousIntent, 0);
//        mRemoteViews.setOnClickPendingIntent(R.id.notification_previous, pIntentPrevious);

        Intent nextIntent = new Intent(NotificationBroadcastReceiver.PLAY_NEXT);
        PendingIntent pIntentNext = PendingIntent.getBroadcast(getApplicationContext(), 0, nextIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.note_play_next, pIntentNext);

        Intent cancelIntent = new Intent(NotificationBroadcastReceiver.PLAYER_CANCEL);
        PendingIntent pIntentCancel = PendingIntent.getBroadcast(getApplicationContext(), 0, cancelIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.note_close, pIntentCancel);

        return mRemoteViews;
    }
}
