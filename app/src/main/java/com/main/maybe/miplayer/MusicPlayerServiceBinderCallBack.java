package com.main.maybe.miplayer;

/**
 * Created by MaybeFei on 2015/3/4.
 */
public interface MusicPlayerServiceBinderCallBack {

    void setCurrentTime(String time);

    void setTotalTime(String time);

    void setMusicTitle(String title);

    void setMusicArtist(String artist);

    void setMusicAlbum(String album);

    void setAlbumCover(int songId, int albumId);

    void setImagePlay();

    void setImagePaused();

}
