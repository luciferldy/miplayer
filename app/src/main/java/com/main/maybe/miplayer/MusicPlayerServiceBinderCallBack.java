package com.main.maybe.miplayer;

/**
 * Created by Maybe霏 on 2015/3/4.
 */
public interface MusicPlayerServiceBinderCallBack {
    public void setCurrentTime(String time);
    public void setTotalTime(String time);
    public void setMusicTitle(String title);
    public void setMusicArtist(String artist);
    public void setMusicAlbum(String album);
    public void setImagePlay();
    public void setImagePaused();
}
