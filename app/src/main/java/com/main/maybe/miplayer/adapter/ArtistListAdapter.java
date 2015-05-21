package com.main.maybe.miplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.task.LoadingListTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Maybe霏 on 2015/5/16.
 */
public class ArtistListAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> artists = new ArrayList<>();
    private LayoutInflater mInflater;

    public ArtistListAdapter(ArrayList<HashMap<String, String>> artists, LayoutInflater mInflater){
        this.artists = artists;
        this.mInflater = mInflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArtistItem item;
        if (convertView==null){
            item = new ArtistItem();
            convertView = mInflater.inflate(R.layout.artistlistitem, parent, false);
            item.artistCover = (ImageView)convertView.findViewById(R.id.artist_cover);
            item.artistName = (TextView)convertView.findViewById(R.id.artist_name);
            item.songNumber = (TextView)convertView.findViewById(R.id.artist_song_number);

            convertView.setTag(item);
        }else{
            item = (ArtistItem)convertView.getTag();
        }

        item.artistName.setText(artists.get(position).get(LoadingListTask.artistName));
        item.songNumber.setText(artists.get(position).get(LoadingListTask.songNumber));

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return artists.size();
    }

    public class ArtistItem{
        ImageView artistCover;
        TextView artistName;
        TextView songNumber;
    }
}
