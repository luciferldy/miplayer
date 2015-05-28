package com.main.maybe.miplayer.binder;

import android.content.Context;
import android.os.Binder;

import com.main.maybe.miplayer.BottomMusicPlayerCallBack;
import com.main.maybe.miplayer.service.MusicPlayerService;

/**
 * Created by Maybe霏 on 2015/5/27.
 */
public class BottomMusicPlayerServiceBinder extends Binder {

    MusicPlayerService mMusicPlayerService;
    Context context;
    BottomMusicPlayerCallBack bmpCallBack;

    public BottomMusicPlayerServiceBinder(MusicPlayerService mMusicPlayerService, Context context){
        this.mMusicPlayerService = mMusicPlayerService;
        this.context = context;
    }

    public MusicPlayerService getService(BottomMusicPlayerCallBack bmpCallBack){
        this.bmpCallBack = bmpCallBack;
        return mMusicPlayerService;
    }

    public void setImagePlay(){
        if (context != null && bmpCallBack != null)
            bmpCallBack.setImagePlay();
    }

    public void setImagePaused(){
        if (context != null && bmpCallBack != null)
            bmpCallBack.setImagePaused();
    }

    public void setSongName(String songName){
        if (context != null && bmpCallBack != null)
            bmpCallBack.setSongName(songName);
    }

    public void setArtistName(String artistName){
        if (context != null && bmpCallBack != null)
            bmpCallBack.setArtistName(artistName);
    }
}
