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
    public static final String NOTIFICATION_BROADCAST_RECEIVER = "NOTIFICATION_BROADCAST_RECEIVER";
    public static final String TYPE = "NotificationBroadcastReceiver";
    private MusicPlayerService musicPlayerService;
    private final String LOG_TAG = NotificationBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null || musicPlayerService == null)
            return;
        String type = intent.getStringExtra(TYPE);
        if (type.equals(PLAY_PREVIOUS))
            musicPlayerService.playPrevious();
        else if (type.equals(PLAY_NEXT))
            musicPlayerService.playNext();
        else if (type.equals(PLAY_PAUSE))
            musicPlayerService.changeState();
        else
            Log.d(LOG_TAG, LOG_TAG+" get the wrong intent action");
    }

    public void registMusicPlayerService(MusicPlayerService musicPlayerService){
        if (musicPlayerService != null)
            this.musicPlayerService = musicPlayerService;
    }
}
