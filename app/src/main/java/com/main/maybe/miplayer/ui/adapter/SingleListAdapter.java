package com.main.maybe.miplayer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.model.SingleBean;
import com.main.maybe.miplayer.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucifer on 2015/5/13.
 */
public class SingleListAdapter extends RecyclerView.Adapter<SingleListAdapter.SingleViewHolder> {

    private static final String LOG_TAG = SingleListAdapter.class.getSimpleName();
    private ArrayList<SingleBean> songs;

    public SingleListAdapter(){
        this.songs = new ArrayList<>();
    }

    @Override
    public SingleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.single_list_item, parent, false);
        SingleViewHolder holder = new SingleViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(SingleViewHolder holder, int position) {

        holder.mSingleName.setText(songs.get(position).getTitle());
        holder.mArtistName.setText(songs.get(position).getArtist());

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void updateData(List<SingleBean> songs) {
        if (songs != null && !songs.isEmpty()) {
            this.songs.clear();
            this.songs.addAll(songs);
        } else {
            Logger.i(LOG_TAG, "updateData but arg is null or empty.");
        }

    }

    public static class SingleViewHolder extends RecyclerView.ViewHolder {

        TextView mSingleName;
        TextView mArtistName;

        public SingleViewHolder(View itemView) {
            super(itemView);
            mSingleName = (TextView) itemView.findViewById(R.id.single_name);
            mArtistName = (TextView) itemView.findViewById(R.id.artist_name);
        }
    }
}

