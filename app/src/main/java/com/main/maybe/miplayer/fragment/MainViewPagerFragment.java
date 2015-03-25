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

/**
 * Created by Maybe霏 on 2015/3/25.
 */
public class MainViewPagerFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private int position;

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

        fl.addView(initSongList());

        return fl;
    }

    public LinearLayout initSongList(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.play_list, null);

        SwipeRefreshLayout songListRefresh = (SwipeRefreshLayout)linearLayout.getChildAt(0);
        songListRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        songListRefresh.setColorSchemeColors(android.R.color.holo_red_light, android.R.color.holo_blue_light,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);

        ListView songList = (ListView)songListRefresh.getChildAt(0);

        return linearLayout;
    }
}
