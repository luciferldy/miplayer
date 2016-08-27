package com.main.maybe.miplayer.service;

import com.main.maybe.miplayer.model.SingleBean;

import java.util.HashMap;
import java.util.List;

/**
 * Created by MaybeFei on 2015/3/4.
 */
public interface MusicPlayerServiceInterface {

    void addMusicToQueue(SingleBean bean);
    void addMusicToQueue(List<SingleBean> songs);
    void removeMusicFromQueue(SingleBean bean);
    void removeMusicFromQueue(List<SingleBean> songs);
    void skipToPoint(int point);
    void play(int position);
    void play();
    void pause();

}
