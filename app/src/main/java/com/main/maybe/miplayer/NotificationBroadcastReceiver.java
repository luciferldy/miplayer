package com.main.maybe.miplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.main.maybe.miplayer.service.MusicPlayerService;

/**
 * Created by Maybeö­ on 2015/6/3.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public static final String PLAY_PREVIOUS = "PLAY_PREVIOUS";
    public static final String PLAY_NEXT = "PLAY_NEXT";
    public static final String PLAY_PAUSE = "PLAY_PAUSE";
    public static final String PLAYER_CANCEL = "PLAYER_CANCEL";
    private MusicPlayerService musicPlayerService;
    private final String LOG_TAG = NotificationBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getAction();
        if (type == null){
            Log.d(LOG_TAG, "action is null!");
            return;
        }
        if (musicPlayerService == null){
            Log.d(LOG_TAG, "music player service is null");
            return;
        }

        if (type.equals(PLAY_PREVIOUS))
            musicPlayerService.playPrevious();
        else if (type.equals(PLAY_NEXT))
            musicPlayerService.playNext();
        else if (type.equals(PLAY_PAUSE))
            musicPlayerService.changeState();
        else if (type.equals(PLAYER_CANCEL)){
            musicPlayerService.mNotificationManager.cancel(MusicPlayerService.PLAY_MUSIC_NOTIFICATION_ID);

        }
        else
            Log.d(LOG_TAG, LOG_TAG+" get the wrong intent action");
    }

    public void registerMusicPlayerService(MusicPlayerService musicPlayerService){
        if (musicPlayerService != null)
            this.musicPlayerService = musicPlayerService;
    }

}
