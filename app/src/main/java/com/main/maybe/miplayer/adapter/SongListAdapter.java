package com.main.maybe.miplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.task.LoadingListTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Maybe霏 on 2015/5/13.
 */
public class SongListAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> songs = new ArrayList<>();
    private LayoutInflater mInflater;

    public SongListAdapter(ArrayList<HashMap<String, String>> songs, LayoutInflater mInflater){
        this.songs = songs;
        this.mInflater = mInflater;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SongItem item = null;

        if (convertView == null){
            item = new SongItem();
            convertView = mInflater.inflate(R.layout.songlistitem, parent, false);

            item.songName = (TextView)convertView.findViewById(R.id.song_name);
            item.artistName = (TextView)convertView.findViewById(R.id.song_singer);
            item.songDuration = (TextView)convertView.findViewById(R.id.song_duration);

            convertView.setTag(item);
        }else {
            item = (SongItem)convertView.getTag();
        }

        item.songName.setText(songs.get(position).get(LoadingListTask.songName));
        item.artistName.setText(songs.get(position).get(LoadingListTask.artistName));
        item.songDuration.setText(songs.get(position).get(LoadingListTask.duration));

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    // custom view group
    public class SongItem{
        TextView songName;
        TextView songDuration;
        TextView artistName;
    }
}

