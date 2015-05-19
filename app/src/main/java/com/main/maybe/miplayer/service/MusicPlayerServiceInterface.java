package com.main.maybe.miplayer.service;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Maybe霏 on 2015/3/4.
 */
public interface MusicPlayerServiceInterface {
    public void addMusicToQueue(HashMap<String, String> music);
    public void addMusicToQueue(List<HashMap<String, String>> music);
    public void removeMusicFromQueue(HashMap<String, String> music);
    public void skipToPoint(int point);
    public void play();
    public void play(int position);
    public void pause();
}
