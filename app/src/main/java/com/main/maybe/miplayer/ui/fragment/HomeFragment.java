package com.main.maybe.miplayer.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.model.FolderBean;
import com.main.maybe.miplayer.presenter.LocalMusicPresenter;
import com.main.maybe.miplayer.ui.adapter.AlbumListAdapter;
import com.main.maybe.miplayer.ui.adapter.ArtistListAdapter;
import com.main.maybe.miplayer.ui.adapter.FolderListAdapter;
import com.main.maybe.miplayer.ui.adapter.SingleListAdapter;
import com.main.maybe.miplayer.ui.view.BaseView;

import java.util.ArrayList;

/**
 * Created by Lucifer on 2015/3/25.
 */
public class HomeFragment extends Fragment implements BaseView {

    private static final String ARG_POSITION = "position";
    private int position;

    private LocalMusicPresenter mPresenter;

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
        initPresenter();
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
                fl.addView(initSingle());
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
                fl.addView(initFolder());
                break;
        }
        return fl;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void initPresenter() {
        mPresenter = new LocalMusicPresenter(getActivity(), this);
    }

    // init single list
    public LinearLayout initSingle(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout root = (LinearLayout)inflater.inflate(R.layout.base_list, null);
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.rv_ls);
        final SingleListAdapter adapter = new SingleListAdapter(getActivity(), mPresenter.getSingle());
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(adapter);
        return root;
    }

    // init artist list
    public LinearLayout initArtists(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout root = (LinearLayout)inflater.inflate(R.layout.base_list, null);
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.rv_ls);
        final ArtistListAdapter adapter = new ArtistListAdapter(getActivity(), mPresenter.getArtist());
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(adapter);
        return root;
    }

    // init album list
    public LinearLayout initAlbum(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout root = (LinearLayout)inflater.inflate(R.layout.base_list, null);
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.rv_ls);
        final AlbumListAdapter adapter = new AlbumListAdapter(getActivity(), mPresenter.getAlbum());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }

    // init folder list
    public LinearLayout initFolder(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout root = (LinearLayout)inflater.inflate(R.layout.base_list, null);
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.rv_ls);
        FolderListAdapter adapter = new FolderListAdapter(getActivity(), new ArrayList<FolderBean>());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }
}
