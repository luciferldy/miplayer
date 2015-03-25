package com.main.maybe.miplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.music.Music;

import java.util.List;

/**
 * Created by Maybe霏 on 2015/3/5.
 */
public class MusicViewAdapter extends ArrayAdapter<Music> {
    private List<Music> list;
    private Context context;

    public MusicViewAdapter(Context context, int textViewResourceId, List<Music> objects){
        super(context, textViewResourceId, objects);
        this.context = context;
        list = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.songlistitem, null);
        }
        Music music = list.get(position);
        if (music != null){
            TextView title = (TextView)view.findViewById(R.id.song_name);
            TextView artist = (TextView)view.findViewById(R.id.song_singer);
            TextView duration = (TextView)view.findViewById(R.id.song_duration);

            title.setText(music.getName());
            artist.setText(music.getArtist());

            int minute = music.getTime()/60, second = music.getTime()%60;
            if (second >= 10)
                duration.setText(minute+":"+second);
            else
                duration.setText(minute+":0"+second);
        }
        return view;
    }
}
