package com.main.maybe.miplayer.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Maybe霏 on 2015/5/15.
 */
public class HandleDB {

    private Context context = null;
    private SongDBHelper dbHelper = null;
    private SQLiteDatabase mdb = null;

    private final String songId = "songId";
    private final String songName = "songName";
    private final String artistName = "artistName";
    private final String albumName = "albumName";
    private final String path = "path";

    private final String artistId = "artistId";
    private final String songNumber = "songNumber";

    private final String albumId = "albumId";
    public HandleDB(Context context){
        this.context = context;
    }

    public ArrayList<HashMap<String, String>> getSongsFromDB(){
        ArrayList<HashMap<String, String>> songs;
        try{
            // create db
            dbHelper = new SongDBHelper(context);
            mdb = dbHelper.getReadableDatabase();

            Cursor cursor;
            cursor = mdb.query(SongDBHelper.TABLE_SONG, new String[]{songId, songName, artistName
            , albumName, path}, null, null, null, null, null);

            if (cursor.getCount()==0){
                Toast.makeText(context, "no song in db", Toast.LENGTH_SHORT).show();
                return null;
            }

            songs = new ArrayList<>();
            // create hashMap
            HashMap<String, String> hashMap;
            while (cursor.moveToNext()){
                hashMap = new HashMap<>();
                // get id
                hashMap.put(songId, cursor.getString(cursor.getColumnIndex(songId)));
                // get song name
                hashMap.put(songName, cursor.getString(cursor.getColumnIndex(songName)));
                // get artist
                hashMap.put(artistName, cursor.getString(cursor.getColumnIndex(artistName)));
                // get album
                hashMap.put(albumName, cursor.getString(cursor.getColumnIndex(albumName)));
                // get path
                hashMap.put(path, cursor.getString(cursor.getColumnIndex(path)));

                songs.add(hashMap);
            }
            mdb.close();
        }catch (Exception e){
            e.printStackTrace();
            mdb.close();
            return null;
        }
        return songs;
    }

    public ArrayList<HashMap<String, String>> getArtistsFromDB(){
        ArrayList<HashMap<String, String>> artists;
        try{
            // create db
            dbHelper = new SongDBHelper(context);
            mdb = dbHelper.getReadableDatabase();

            Cursor cursor;
            cursor = mdb.query(SongDBHelper.TABLE_ARTIST, new String[]{artistId, artistName, songNumber}, null, null
            , null, null, null);

            if (cursor.getCount()==0){
                Toast.makeText(context, "no artist in db", Toast.LENGTH_SHORT).show();
                return null;
            }

            artists = new ArrayList<>();
            HashMap<String, String> hashMap;
            while(cursor.moveToNext()){
                hashMap = new HashMap<>();
                hashMap.put(artistId, cursor.getString(cursor.getColumnIndex(artistId)));
                hashMap.put(artistName, cursor.getString(cursor.getColumnIndex(artistName)));
                hashMap.put(songNumber, cursor.getString(cursor.getColumnIndex(songNumber)));

                artists.add(hashMap);
            }

            mdb.close();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return artists;
    }

    public ArrayList<HashMap<String, String>> getAlbumFromDB(){
        ArrayList<HashMap<String, String>> albums;
        try {
            // create db
            dbHelper = new SongDBHelper(context);

            mdb = dbHelper.getReadableDatabase();
            Cursor cursor;
            cursor = mdb.query(SongDBHelper.TABLE_ALBUM, new String[]{albumId, albumName, artistName, songNumber},
                    null, null, null, null, null);

            if (cursor.getCount()==0){
                Toast.makeText(context, "no album in db", Toast.LENGTH_SHORT).show();
                return null;
            }

            albums = new ArrayList<>();
            HashMap<String, String> hashMap;
            while (cursor.moveToNext()){
                hashMap = new HashMap<>();
                hashMap.put(albumId, cursor.getString(cursor.getColumnIndex(albumId)));
                hashMap.put(albumName, cursor.getString(cursor.getColumnIndex(albumName)));
                hashMap.put(artistName, cursor.getString(cursor.getColumnIndex(artistName)));
                hashMap.put(songNumber, cursor.getString(cursor.getColumnIndex(songNumber)));

                albums.add(hashMap);
            }
            mdb.close();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return albums;
    }
}
