package com.main.maybe.miplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.main.maybe.miplayer.fragment.SongInAlbumOrArtFragment;
import com.main.maybe.miplayer.task.LoadingListTask;

/**
 * Created by Maybe霏 on 2015/5/22.
 */
public class SongInAlbumOrArtActivity extends ActionBarActivity {

    private final String LOG_TAG = SongInAlbumOrArtActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_from_album_or_art);
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        Bundle b = new Bundle();
        b.putString("type", type);
        int i;
        if (type.equals("artist")){
            i = intent.getIntExtra(LoadingListTask.artistId, -1);
            b.putInt(LoadingListTask.artistId, i);
        }else if (type.equals("album")){
            i = intent.getIntExtra(LoadingListTask.albumId, -1);
            b.putInt(LoadingListTask.albumId, i);
        }else{
            // wrong
            i = -1;
        }
        // if albumId == -1 wrong
        if (i == -1){
            Toast.makeText(getApplicationContext(), "Album is wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        Fragment sia = new SongInAlbumOrArtFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.song_from_album_or_art_list, sia);
        // bundle
        sia.setArguments(b);
        ft.commit();
        Log.d(LOG_TAG, "ft commit");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}
