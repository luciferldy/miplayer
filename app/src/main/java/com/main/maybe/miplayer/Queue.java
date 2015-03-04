package com.main.maybe.miplayer;

import android.util.Log;

import com.main.maybe.miplayer.music.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maybe霏 on 2015/3/4.
 */
public class Queue {
    private List<Music> queue = new ArrayList<Music>();
    private int current = 0;
    private boolean random = false;
    private List<Music> random_queue = new ArrayList<Music>();
    private final String LOG_TAG = Queue.class.getSimpleName();

    public Music getCurrentPlaying(){
        return queue.get(current);
    }

    public void addMusicToQueue(Music music){
        if (!queue.contains(music)){
            queue.add(music);
            Log.d(LOG_TAG, "added "+music.toString());
        }
    }

    public void removeMusicToQueue(Music music){
        queue.remove(music);
        random_queue.remove(music);
    }

    public void addMusicToQueue(List<Music> list){
        for (Music music : list){
            addMusicToQueue(music);
        }
    }

    public void addMusicToQueue(Queue queue){
        addMusicToQueue(queue.queue);
    }

    public void addMusicToQueue(Music music, int index){
        queue.add(index, music);
    }

    public int getSizeOfQueue(){
        return queue.size();
    }

    public Music next(){
        current = ++current % queue.size();
        // 随机？？
        if (queue.size() >= 1 && random)
            return random_queue.get(current%random_queue.size());
        else if (queue.size() >= 1)
            return queue.get(current%queue.size());
        else
            return null;
    }

    public Music previous(){
        current = --current % queue.size();
        if (queue.size() >= 1 && random)
            return random_queue.get(current%random_queue.size());
        else if (queue.size() >= 1)
            return queue.get(current%queue.size());
        else
            return null;
    }

    public Music playGet(int position){
        current = position;
        return queue.get(position);
    }

    public void clearQueue(){
        queue.clear();
        current = 0;
    }
}
