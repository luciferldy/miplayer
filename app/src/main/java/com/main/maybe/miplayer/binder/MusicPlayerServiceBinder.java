package com.main.maybe.miplayer.binder;

import android.content.Context;
import android.os.Binder;

import com.main.maybe.miplayer.MusicPlayerServiceBinderCallBack;
import com.main.maybe.miplayer.service.MusicPlayerService;

/**
 * Created by Maybe霏 on 2015/3/4.
 */
public class MusicPlayerServiceBinder extends Binder {
    MusicPlayerService mMusicPlayerService;
    Context mApplication;
    MusicPlayerServiceBinderCallBack mSeekBarTextCallBack;

    public MusicPlayerServiceBinder(MusicPlayerService musicPlayerService, Context application){
        mMusicPlayerService = musicPlayerService;
        mApplication = application;
    }

    public MusicPlayerService getService(MusicPlayerServiceBinderCallBack seekBarTextCallBack){
        mSeekBarTextCallBack = seekBarTextCallBack;
        return mMusicPlayerService;
    }

    public void setTotalTime(String time){
        if (mApplication != null && mSeekBarTextCallBack != null)
            mSeekBarTextCallBack.setTotalTime(time);
    }

    public void setCurrentTime(String time){
        if (mApplication != null && mSeekBarTextCallBack != null)
            mSeekBarTextCallBack.setCurrentTime(time);
    }

    public void setMusicTitle(String title){
        if (mApplication != null && mSeekBarTextCallBack != null)
            mSeekBarTextCallBack.setMusicTitle(title);
    }

    public void setAlbumCover(int songId, int albumId){
        if (mApplication != null && mSeekBarTextCallBack != null)
            mSeekBarTextCallBack.setAlbumCover(songId, albumId);
    }

    public void setMusicAlbum(String album){
        if (mApplication != null && mSeekBarTextCallBack != null)
            mSeekBarTextCallBack.setMusicAlbum(album);
    }

    public void setMusicArtist(String artist){
        if (mApplication != null && mSeekBarTextCallBack != null)
            mSeekBarTextCallBack.setMusicArtist(artist);
    }

    public void setImagePlay(){
        if (mApplication != null && mSeekBarTextCallBack != null)
            mSeekBarTextCallBack.setImagePlay();
    }

    public void setImagePaused(){
        if (mApplication != null && mSeekBarTextCallBack != null)
            mSeekBarTextCallBack.setImagePaused();
    }

}
