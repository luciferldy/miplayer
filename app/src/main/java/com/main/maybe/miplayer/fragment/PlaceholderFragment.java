package com.main.maybe.miplayer.fragment;

/**
 * Created by Maybe霏 on 2015/3/5.
 */

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.SeekBarTextCallBack;
import com.main.maybe.miplayer.binder.MusicPlayerServiceBinder;
import com.main.maybe.miplayer.music.Music;
import com.main.maybe.miplayer.music.MusicScanner;
import com.main.maybe.miplayer.music.MusicViewAdapter;
import com.main.maybe.miplayer.service.MusicPlayerService;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    List<Music> library;
    MusicViewAdapter mMusicViewAdapter;


    private SeekBar mSeekBar;
    private TextView mTotalTime;
    private TextView mCurrentTime;
    private ListView listOfFiles;
    private ImageButton playPausedButton;
    private ImageButton playPreviousButton;
    private ImageButton playNextButton;
    private TextView playTitle;
    private TextView playAlbum;
    private TextView playArtist;

    MusicPlayerService mService;
    MusicPlayerServiceBinder mBinder;
    ServiceConnection mConnection;
    SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    boolean mBound = false;

    List<String> filePaths = null;

    int state;

    private final String LOG_TAG = PlaceholderFragment.class.getSimpleName();

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.playmusic, container, false);

        playTitle = (TextView)rootView.findViewById(R.id.play_song_name);
        playAlbum = (TextView)rootView.findViewById(R.id.play_album);
        playArtist = (TextView)rootView.findViewById(R.id.play_singer);

        library = new ArrayList<>();

        final MusicScanner musicScanner = new MusicScanner();
        listOfFiles = (ListView)rootView.findViewById(R.id.play_list);
        mMusicViewAdapter = new MusicViewAdapter(getActivity(), R.layout.songlistitem, library);
        listOfFiles.setAdapter(mMusicViewAdapter);
        listOfFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mService.play(position);
            }
        });

        playPausedButton = (ImageButton)rootView.findViewById(R.id.play_paused);
        playPausedButton.setClickable(false);
        playPreviousButton = (ImageButton)rootView.findViewById(R.id.play_previous);
        playPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPausedButton.setImageResource(R.drawable.song_play);
                mService.playPrevious();
            }
        });
        playPreviousButton.setClickable(false);
        playNextButton = (ImageButton)rootView.findViewById(R.id.play_next);
        playNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set the icon play
                playPausedButton.setImageResource(R.drawable.song_play);
                mService.playNext();
            }
        });
        playNextButton.setClickable(false);

        mSeekBar = (SeekBar) rootView.findViewById(R.id.play_progress);
        initOnSeekBarChangeListener();
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        mTotalTime = (TextView) rootView.findViewById(R.id.play_totaltime);
        mCurrentTime = (TextView) rootView.findViewById(R.id.play_currenttime);

        defineServiceConnection();// we define our service connection mConnection
        getActivity().bindService(new Intent(getActivity(), MusicPlayerService.class), mConnection
                , Context.BIND_AUTO_CREATE);

        createLibrary(musicScanner);

        return rootView;
    }

    private void initOnSeekBarChangeListener(){
        mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mService == null || !fromUser)
                    return;
                mService.skipToPoint(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mService == null)
                    return;
                if (mBound)
                    state = mService.changeState();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mService == null)
                    return;
                if (mBound)
                    state = mService.changeState();
            }
        };
    }

    private void defineServiceConnection() {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                getActivity().startService(new Intent(getActivity(), MusicPlayerService.class));
                mBinder = (MusicPlayerServiceBinder) service;
                mService = mBinder.getService(new SeekBarTextCallBack() {
                    @Override
                    public void setCurrentTime(String time) {
                        if (mCurrentTime != null)
                            mCurrentTime.setText(time);
                    }

                    @Override
                    public void setTotalTime(String time) {
                        if (mTotalTime != null)
                            mTotalTime.setText(time);
                    }

                    @Override
                    public void setMusicTitle(String title) {
                        if (playTitle != null)
                            playTitle.setText(title);
                    }

                    @Override
                    public void setMusicArtist(String artist) {
                        if (playArtist != null)
                            playArtist.setText(artist);
                    }

                    @Override
                    public void setMusicAlbum(String album) {
                        if (playAlbum != null)
                            playAlbum.setText(album);
                    }

                    @Override
                    public void setImagePlay() {
                        if (playPausedButton != null)
                            playPausedButton.setImageResource(R.drawable.song_play);
                    }

                    @Override
                    public void setImagePaused() {
                        if (playPausedButton != null)
                            playPausedButton.setImageResource(R.drawable.song_pause);
                    }
                });

                state = mService.getState();
                setPlayPauseOnClickListener();
                mService.registerSeekBar(mSeekBar);

                mBound = true;

                if (filePaths != null)
                    initQueue();
                Log.d(LOG_TAG, "Service is connected and well to go");

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBound = false;
                Log.d(LOG_TAG, "Service is disconnected");
            }
        };
    }

    private synchronized void initQueue(){
        mService.addMusicToQueue(library);
        mService.playInit();

        // set button clickable
        playPausedButton.setClickable(true);
        playPreviousButton.setClickable(true);
        playNextButton.setClickable(true);
    }

    private void createLibrary(final MusicScanner musicScanner){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                musicScanner.scanMusic(getActivity());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                filePaths = musicScanner.getScannedMusic();
                Log.d(LOG_TAG, "Done getting the files from the music scanner");

                for (String filePath : filePaths){
                    library.add(new Music(filePath));
                }

                mMusicViewAdapter.notifyDataSetChanged();
                Log.d(LOG_TAG, "notifyDataSetChanged");
                if (mBound)
                    initQueue();
            }
        }.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().stopService(new Intent(getActivity(), MusicPlayerService.class));
        getActivity().unbindService(mConnection);
        mBound = false;
    }

    private void setPlayPauseOnClickListener(){
        playPausedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound){
                    state = mService.changeState();
                    switch (state){
                        case MusicPlayerService.PLAYING:
                            playPausedButton.setImageResource(R.drawable.song_pause);
                            break;
                        case MusicPlayerService.PAUSED:
                            playPausedButton.setImageResource(R.drawable.song_play);
                            break;
                    }
                }
            }
        });
    }

}
