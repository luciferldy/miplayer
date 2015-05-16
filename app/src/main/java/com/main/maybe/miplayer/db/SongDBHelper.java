package com.main.maybe.miplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Maybe霏 on 2015/3/25.
 */
// 数据库辅助类
public class SongDBHelper extends SQLiteOpenHelper {

    private final static int VERSION = 1;
    public final static String DATABASE_NAME = "music.db";
    public final static String TABLE_SONG = "song";
    public final static String TABLE_ARTIST = "artist";
    public final static String TABLE_ALBUM = "album";

    public final static String DATABASE_PATH = "/data/data/com.main.maybe.miplayer/databases/";
    private final String LOG_TAG = SongDBHelper.class.getSimpleName();

    public SongDBHelper(Context context){
        super(context, DATABASE_NAME , null, VERSION);
    }

    @Override
    // first use
    public void onCreate(SQLiteDatabase db) {
        // create songs
        db.execSQL("create table if not exists "+TABLE_SONG+"("
        + "songId varchar, "
        + "songName varchar, "
        + "artistName varchar, "
        + "albumName varchar, "
        + "path)");
        Log.v(LOG_TAG, "table song onCreate");

        // create artist
        db.execSQL("create table if not exist "+TABLE_ARTIST+"("
        + "artistId varchar, "
        + "artistName varchar, "
        + "songNumber)");
        Log.v(LOG_TAG, "table artist onCreate");

        // create album
        db.execSQL("create table if not exist "+TABLE_ALBUM+"("
        + "albumId varchar, "
        + "albumName varchar, "
        + "artistName varchar, "
        + "songNumber)");
        Log.v(LOG_TAG, "table album onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(LOG_TAG, "onUpgrade");
    }
}
