package com.main.maybe.miplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.main.maybe.miplayer.fragment.MusicPlayerFragment;
import com.main.maybe.miplayer.task.LoadingListTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Maybe霏 on 2015/5/19.
 */
public class MusicPlayerActivity extends ActionBarActivity {

    private ArrayList<HashMap<String, String>> songs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_container);
        Intent intent = getIntent();
        songs = (ArrayList<HashMap<String, String>>)intent.getSerializableExtra(LoadingListTask.songList);
        int position = intent.getIntExtra(LoadingListTask.playPosition, 0);
        if (savedInstanceState == null) {

            MusicPlayerFragment mpf = new MusicPlayerFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(LoadingListTask.songList, songs);
            bundle.putInt(LoadingListTask.playPosition, position);
            mpf.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.music_player_container,mpf)
                    .commit();
        }

    }
}
