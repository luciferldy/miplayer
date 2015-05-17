package com.main.maybe.miplayer.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.main.maybe.miplayer.db.HandleDB;
import com.main.maybe.miplayer.music.MusicScanner;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Maybe霏 on 2015/5/17.
 */
public class LoadingListTask extends AsyncTask<Void, Void, Void> {

    private int type = 0; // 0 song, 1 artist, 2 album
    private Context context;
    private final String songId = "songId";
    private final String songName = "songName";
    private final String artistName = "artistName";
    private final String albumName = "albumName";
    private final String path = "path";
    private final String duration = "duration";

    private final String artistId = "artistId";
    private final String songNumber = "songNumber";

    private final String albumId = "albumId";

    public LoadingListTask(int type, Context context){
        this.type = type;
        this.context = context;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... params) {
        HandleDB handleDB = new HandleDB(context);
        ArrayList<HashMap<String, String>> items = new ArrayList<>();
        switch (type){
            case 0:
                items = handleDB.getSongsFromDB();
                break;
            case 1:
                items = handleDB.getArtistsFromDB();
                break;
            case 2:
                items = handleDB.getAlbumFromDB();
                break;
        }
        // find music and insert into db
        if (items == null){
            MusicScanner scanner = new MusicScanner();

        }
        return null;
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
                item.put(songName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
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
                item.put(albumId, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID)));
                item.put(albumName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
                item.put(artistName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
                item.put(songNumber, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._COUNT)));

                albums.add(item);
            }
            return albums;
        }catch (Exception e){
            e.printStackTrace();
            return albums;
        }
    }
}
