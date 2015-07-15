package com.main.maybe.miplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.task.LoadingListTask;

/**
 * Created by Maybe霏 on 2015/3/25.
 */
public class HomeFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private int position;

    public static HomeFragment newInstance(int position){
        HomeFragment f = new HomeFragment();
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
        // switch options
        switch (position) {
            case 0:
                // songs
                fl.addView(initSongs());
                break;
            case 1:
                // artist
                fl.addView(initArtists());
                break;
            case 2:
                // album
                fl.addView(initAlbum());
                break;
            case 3:
                // list
                fl.addView(initList());
                break;
        }
        return fl;
    }

    // init songs
    public LinearLayout initSongs(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.play_list, null);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)linearLayout.getChildAt(0);
        swipeRefreshLayout.setEnabled(false);
        ListView songList = (ListView)swipeRefreshLayout.findViewById(R.id.play_list);
        // start async task
        (new LoadingListTask(0, getActivity(), songList, inflater, getFragmentManager())).execute();
        return linearLayout;
    }

    public LinearLayout initArtists(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.play_list, null);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)linearLayout.getChildAt(0);
        swipeRefreshLayout.setEnabled(false);
        ListView artistList = (ListView)swipeRefreshLayout.findViewById(R.id.play_list);
        (new LoadingListTask(1, getActivity(), artistList, inflater, getFragmentManager())).execute();

        return linearLayout;
    }

    public LinearLayout initAlbum(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.play_list, null);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)linearLayout.getChildAt(0);
        swipeRefreshLayout.setEnabled(false);
        ListView albumList = (ListView)swipeRefreshLayout.findViewById(R.id.play_list);
        (new LoadingListTask(2, getActivity(), albumList, inflater, getFragmentManager())).execute();
        return linearLayout;
    }

    public LinearLayout initList(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.play_list, null);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)linearLayout.getChildAt(0);
        swipeRefreshLayout.setEnabled(false);
        ListView selfList = (ListView)swipeRefreshLayout.findViewById(R.id.play_list);
        (new LoadingListTask(3, getActivity(), selfList, inflater, getFragmentManager())).execute();
        return linearLayout;
    }
}
