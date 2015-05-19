package com.main.maybe.miplayer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.main.maybe.miplayer.fragment.MusicPlayerFragment;

/**
 * Created by Maybe霏 on 2015/5/19.
 */
public class MusicPlayerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_container);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.music_player_container, new MusicPlayerFragment())
                    .commit();
        }

    }
}
