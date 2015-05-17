package com.main.maybe.miplayer.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

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

    public ArrayList<HashMap<String, String>> files2Details(ArrayList<String> files){

    }
}
