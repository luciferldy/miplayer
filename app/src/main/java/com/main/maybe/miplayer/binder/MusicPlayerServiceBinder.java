package com.main.maybe.miplayer.binder;

import android.content.Context;
import android.os.Binder;

import com.main.maybe.miplayer.SeekBarTextCallBack;
import com.main.maybe.miplayer.service.MusicPlayerService;

/**
 * Created by Maybe霏 on 2015/3/4.
 */
public class MusicPlayerServiceBinder extends Binder {
    MusicPlayerService mMusicPlayerService;
    Context mApplication;
    SeekBarTextCallBack mSeekBarTextCallBack;

    public MusicPlayerServiceBinder(MusicPlayerService musicPlayerService, Context application){
        mMusicPlayerService = musicPlayerService;
        mApplication = application;
    }

    public MusicPlayerService getService(SeekBarTextCallBack seekBarTextCallBack){
        mSeekBarTextCallBack = seekBarTextCallBack;
        return mMusicPlayerService;
    }

    public synchronized void setCurrentTime(String time){
        if (mApplication != null && mSeekBarTextCallBack != null){
            mSeekBarTextCallBack.setCurrentTime(time);
        }
    }

    public void setTotalTime(String time){
        if (mApplication != null && mSeekBarTextCallBack != null){
            mSeekBarTextCallBack.setTotalTime(time);
        }
    }
}
