package com.main.maybe.miplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Maybe霏 on 2015/3/25.
 */
public class SongDBHelper extends SQLiteOpenHelper {

    private final static int VERSION = 1;
    public final static String DATABASE_NAME = "songlib.db";
    public final static String TABLE_SONGS = "songslist";

    public final static String DATABASE_PATH = "/data/data/com.main.maybe.miplayer/databases/";
    private final String LOG_TAG = SongDBHelper.class.getSimpleName();

    public SongDBHelper(Context context){
        super(context, DATABASE_NAME , null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists songslist("
        + "listId varchar, "
        + "path varchar)");
        Log.v(LOG_TAG, "songslist first onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(LOG_TAG, "onUpgrade");
    }
}
