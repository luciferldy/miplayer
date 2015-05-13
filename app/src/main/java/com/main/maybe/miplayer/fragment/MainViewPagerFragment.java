package com.main.maybe.miplayer.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.adapter.MusicViewAdapter;
import com.main.maybe.miplayer.music.Music;
import com.main.maybe.miplayer.music.MusicScanner;
import com.main.maybe.miplayer.service.MusicPlayerService;

import java.util.List;

/**
 * Created by Maybe霏 on 2015/3/25.
 */
public class MainViewPagerFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private int position;
    private List<Music> library;
    private MusicViewAdapter mMusicViewAdapter;
    private List<String> filePaths = null;
    private MusicPlayerService mService;
    boolean mBound = false;

    private final String LOG_TAG = MainViewPagerFragment.class.getSimpleName();

    public static MainViewPagerFragment newInstance(int position){
        MainViewPagerFragment f = new MainViewPagerFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);

        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        params.setMargins(margin, margin, margin ,margin);

        // 在这里选择选项
        switch (position) {
            case 0:
                // 歌曲界面
                fl.addView(initSongs());
                break;
            case 1:
                // 艺术家界面
                fl.addView(initArtists());
                break;
            case 2:
                // 专辑
                fl.addView(initAlbum());
                break;
            case 3:
                // 列表
                fl.addView(initList());
                break;
        }
        return fl;
    }

    public LinearLayout initSongs(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.play_list, null);
//        ListView songlist = (ListView)linearLayout.getChildAt(0);
//        SongListAdapter adapter = new SongListAdapter();
//        songlist.setAdapter(adapter);
        return linearLayout;
    }

    public LinearLayout initArtists(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.play_list, null);
        return linearLayout;
    }

    public LinearLayout initAlbum(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.play_list, null);
        return linearLayout;
    }

    public LinearLayout initList(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.play_list, null);
        return linearLayout;
    }
    public LinearLayout initSongList(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.play_list, null);

//        SwipeRefreshLayout songListRefresh = (SwipeRefreshLayout)linearLayout.getChildAt(0);
//        songListRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//            }
//        });
//        songListRefresh.setColorSchemeColors(android.R.color.holo_red_light, android.R.color.holo_blue_light,
//                android.R.color.holo_green_light, android.R.color.holo_orange_light);
//
//        ListView songList = (ListView)songListRefresh.getChildAt(0);
//        library = new ArrayList<>();
//
//        final MusicScanner musicScanner = new MusicScanner();
//        mMusicViewAdapter = new MusicViewAdapter(getActivity(), R.layout.songlistitem, library);
//
//        songList.setAdapter(mMusicViewAdapter);
//        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mService.play(position);
//            }
//        });
        return linearLayout;
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

    private synchronized void initQueue(){
        mService.addMusicToQueue(library);
        mService.playInit();
    }
}
