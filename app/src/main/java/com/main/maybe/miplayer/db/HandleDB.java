package com.main.maybe.miplayer.db;

import android.content.ContentValues;
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
    private final String duration = "duration";

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
                // song duration
                hashMap.put(duration, cursor.getString(cursor.getColumnIndex(duration)));

                songs.add(hashMap);
            }
            dbHelper.close();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            mdb.close();
            dbHelper.close();
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

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            mdb.close();
            dbHelper.close();
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
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            mdb.close();
            dbHelper.close();
        }
        return albums;
    }

    public boolean insertSongsIntoDB(ArrayList<HashMap<String, String>> songs){
        try {
            dbHelper = new SongDBHelper(context);
            mdb = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            for (int i = 0; i<songs.size(); i++) {
                values.put(songId, songs.get(i).get(songId));
                values.put(songName, songs.get(i).get(songName));
                values.put(artistName, songs.get(i).get(artistName));
                values.put(albumName, songs.get(i).get(albumName));
                values.put(path, songs.get(i).get(path));
                values.put(duration, songs.get(i).get(duration));

                mdb.insert(SongDBHelper.TABLE_SONG, songId, values);
                values.clear();
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            mdb.close();
            dbHelper.close();
        }

    }

    public boolean insertArtistIntoDB(ArrayList<HashMap<String, String>> artists){
        try {
            dbHelper = new SongDBHelper(context);
            mdb = dbHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            for (int i = 0 ; i < artists.size() ; i++){
                contentValues.put(artistId, artists.get(i).get(artistId));
                contentValues.put(artistName, artists.get(i).get(artistName));
                contentValues.put(songNumber, artists.get(i).get(songNumber));

                mdb.insert(SongDBHelper.TABLE_ARTIST, artistId, contentValues);
                contentValues.clear();
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            mdb.close();
            dbHelper.close();
        }
    }

    public boolean insertAlbumIntoDB(ArrayList<HashMap<String, String>> albums){
        try {
            dbHelper = new SongDBHelper(context);
            mdb = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            for ( int i = 0 ; i < albums.size() ; i++ ){
                values.put(albumId, albums.get(i).get(albumId));
                values.put(albumName, albums.get(i).get(albumName));
                values.put(songNumber, albums.get(i).get(songNumber));

                mdb.insert(SongDBHelper.TABLE_ALBUM, albumId, values);
                values.clear();
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            mdb.close();
            dbHelper.close();
        }
    }
}
