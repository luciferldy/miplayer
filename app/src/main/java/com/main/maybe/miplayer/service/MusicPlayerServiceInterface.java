package com.main.maybe.miplayer.service;

import com.main.maybe.miplayer.music.Music;

import java.util.List;

/**
 * Created by Maybe霏 on 2015/3/4.
 */
public interface MusicPlayerServiceInterface {
    public void addMusicToQueue(Music music);
    public void addMusicToQueue(List<Music> music);
    public void removeMusicFromQueue(Music music);
    public void skipToPoint(int point);
    public void play();
    public void play(int position);
    public void pause();
}
