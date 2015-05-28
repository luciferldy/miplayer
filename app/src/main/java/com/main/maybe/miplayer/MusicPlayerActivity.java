package com.main.maybe.miplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.main.maybe.miplayer.fragment.MusicPlayerFragment;
import com.main.maybe.miplayer.task.LoadingListTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Maybe霏 on 2015/5/19.
 */
public class MusicPlayerActivity extends ActionBarActivity {

    private ArrayList<HashMap<String, String>> songs = new ArrayList<>();
    private final String LOG_TAG = MusicPlayerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_container);
        Intent intent = getIntent();
        songs = (ArrayList<HashMap<String, String>>)intent.getSerializableExtra(LoadingListTask.songList);
        int isPlayMusic = intent.getIntExtra(LoadingListTask.isPlayMusic, 0);
        int position = intent.getIntExtra(LoadingListTask.playPosition, 0);

        if (isPlayMusic == 0){
            // wrong
            return;
        }

        if (savedInstanceState == null && isPlayMusic!=0) {

            MusicPlayerFragment mpf = new MusicPlayerFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(LoadingListTask.songList, songs);
            bundle.putInt(LoadingListTask.playPosition, position);
            bundle.putInt(LoadingListTask.isPlayMusic, isPlayMusic);
            mpf.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.music_player_container,mpf)
                    .commit();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, LOG_TAG+"is on destroy");
    }
}
