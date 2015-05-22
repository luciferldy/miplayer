package com.main.maybe.miplayer.fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.adapter.SongListAdapter;
import com.main.maybe.miplayer.task.LoadingListTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Maybe霏 on 2015/5/21.
 */
public class SongInAlbumOrArtFragment extends Fragment {

    private ListView songList;
    private final String LOG_TAG = SongInAlbumOrArtFragment.class.getSimpleName();
    private String type;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.play_list, null);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)linearLayout.getChildAt(0);
        Log.d(LOG_TAG, "onCreateView swipe refresh layout");
        songList = (ListView)swipeRefreshLayout.findViewById(R.id.play_list);
        Log.d(LOG_TAG, "onCreateView songList");
        // get the bundle
        Bundle bundle = getArguments();
        type = bundle.getString("type");
        int id;
        if (type.equals("album")){
            id = bundle.getInt(LoadingListTask.albumId);
        }else if (type.equals("artist")){
            id = bundle.getInt(LoadingListTask.artistId);
        }else {
            // wrong
            Toast.makeText(getActivity().getApplicationContext(), "type wrong!", Toast.LENGTH_SHORT).show();
            return linearLayout;
        }
        // start the async task
        (new GetSongsInAlbumTask(inflater)).execute(id);
        return linearLayout;
    }

    // async task to get inflate the ListView items
    public class GetSongsInAlbumTask extends AsyncTask<Integer, Void, Boolean>{

        private ArrayList<HashMap<String, String>> songs = new ArrayList<>();
        private LayoutInflater inflater;

        public GetSongsInAlbumTask(LayoutInflater inflater){
            this.inflater = inflater;
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            songs = getSongsInAlbumFromP(params[0]);
            if (songs == null)
                return false;
            else
                return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                songList.setAdapter(new SongListAdapter(songs, inflater));
                // set the click event
            }else {
                Toast.makeText(getActivity(), "can not get songs of album", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public ArrayList<HashMap<String, String>> getSongsInAlbumFromP(int id){
        ArrayList<HashMap<String, String>> songs = new ArrayList<>();
        ContentResolver resolver = getActivity().getContentResolver();
        try{
            // select the songs in album
            Cursor cursor;
            // query album
            if (type.equals("album")) {
                cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.ALBUM_ID + "="
                                + id,
                        null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            }else {
                // query artist
                cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.ARTIST_ID + "="
                                + id,
                        null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            }
            HashMap<String, String> song;
            int second;
            while (cursor.moveToNext()){
                song = new HashMap<>();
                song.put(LoadingListTask.songId, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));

//                Log.d(TAG_MSG, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
//                Log.d(TAG_MSG, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
//                Log.d(TAG_MSG, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)));

                second = Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))) / 1000;


                song.put(LoadingListTask.songName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                song.put(LoadingListTask.artistName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                song.put(LoadingListTask.albumName, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                song.put(LoadingListTask.duration, (second / 60) + ":" + (second % 60));
                song.put(LoadingListTask.duration_t, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                song.put(LoadingListTask.path, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                songs.add(song);
            }
            return songs;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
