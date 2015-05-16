package com.main.maybe.miplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.maybe.miplayer.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Maybe霏 on 2015/5/16.
 */
public class AlbumListAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> albums = new ArrayList<>();
    private LayoutInflater mInflater;

    public AlbumListAdapter(ArrayList<HashMap<String, String>> albums, LayoutInflater mInflater){
        this.albums = albums;
        this.mInflater = mInflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumItem item;
        if (convertView==null){
            item = new AlbumItem();
            convertView = mInflater.inflate(R.layout.albumlistitem, null);

            item.albumCover = (ImageView)convertView.findViewById(R.id.album_cover);
            item.albumName = (TextView)convertView.findViewById(R.id.album_name);
            item.artistName = (TextView)convertView.findViewById(R.id.album_artist_name);
            item.songNumber = (TextView)convertView.findViewById(R.id.album_song_number);

            convertView.setTag(item);
        }else{
            item = (AlbumItem)convertView.getTag();
        }

        item.albumName.setText(albums.get(position).get("albumName"));
        item.artistName.setText(albums.get(position).get("artistName"));
        item.songNumber.setText(albums.get(position).get("songNumber"));

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
        return albums.size();
    }

    public class AlbumItem{
        ImageView albumCover;
        TextView albumName;
        TextView artistName;
        TextView songNumber;
    }
}
