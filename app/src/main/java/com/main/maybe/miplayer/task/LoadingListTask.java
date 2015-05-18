package com.main.maybe.miplayer.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.widget.ListView;

import com.main.maybe.miplayer.adapter.AlbumListAdapter;
import com.main.maybe.miplayer.adapter.ArtistListAdapter;
import com.main.maybe.miplayer.adapter.SongListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Maybe霏 on 2015/5/17.
 */
public class LoadingListTask extends AsyncTask<Void, Void, Boolean> {

    private int type = 0; // 0 song, 1 artist, 2 album
    private Context context;
    public static final String songId = "songId";
    public static final String songName = "songName";
    public static final String artistName = "artistName";
    public static final String albumName = "albumName";
    public static final String path = "path";
    public static final String duration = "duration";

    public static final String artistId = "artistId";
    public static final String songNumber = "songNumber";

    public static final String albumId = "albumId";

    private ArrayList<HashMap<String, String>> items;
    private ListView lv;
    private LayoutInflater mInflater;

    public LoadingListTask(int type, Context context, ListView lv, LayoutInflater mInflater){
        this.type = type;
        this.context = context;
        this.lv = lv;
        this.mInflater = mInflater;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean){
            switch (type){
                case 0:
                    lv.setAdapter(new SongListAdapter(items, mInflater));
                    break;
                case 1:
                    lv.setAdapter(new ArtistListAdapter(items, mInflater));
                    break;
                case 2:
                    lv.setAdapter(new AlbumListAdapter(items, mInflater));
                    break;
            }
        }else {
            // notice that something wrong
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        items = new ArrayList<>();
        switch (type){
            case 0:
                items = getSongFromProvider();
                break;
            case 1:
                items = getArtistFromProvider();
                break;
            case 2:
                items = getAlbumFromProvider();
                break;
        }
        if (items==null)
            return false;
        else
            return true;
    }

    public ArrayList<HashMap<String, String>> getSongFromProvider(){
        ArrayList<HashMap<String, String>> songs = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        try {
            Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            HashMap<String, String> item;
            while (cursor.moveToNext()){
                item = new HashMap<>();
                item.put(songId, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                item.put(songName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                item.put(artistName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                item.put(albumName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                item.put(duration, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                item.put(path, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                songs.add(item);
            }
            return songs;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public ArrayList<HashMap<String, String>> getAlbumFromProvider(){
        ArrayList<HashMap<String, String>> albums = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        try {
            Cursor cursor = resolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
            HashMap<String, String> item;
            while (cursor.moveToNext()){
                item = new HashMap<>();
                item.put(albumId, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)));
                item.put(albumName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
                item.put(artistName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
                item.put(songNumber, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
                albums.add(item);
            }
            return albums;
        }catch (Exception e){
            e.printStackTrace();
            return albums;
        }
    }

    public ArrayList<HashMap<String, String>> getArtistFromProvider(){
        ArrayList<HashMap<String, String>> artists = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        try{
            Cursor cursor = resolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
            HashMap<String, String> item;
            while (cursor.moveToNext()){
                item = new HashMap<>();
                item.put(artistId, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)));
                item.put(artistName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
                item.put(songNumber, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)));
                artists.add(item);
            }
            return artists;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
