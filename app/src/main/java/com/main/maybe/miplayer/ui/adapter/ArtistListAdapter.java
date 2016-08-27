package com.main.maybe.miplayer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.model.ArtistBean;
import com.main.maybe.miplayer.task.LoadingListTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Maybe霏 on 2015/5/16.
 */
public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.ArtistViewHolder> {

    private ArrayList<ArtistBean> artists = new ArrayList<>();

    public ArtistListAdapter(){
        this.artists = new ArrayList<>();
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.artist_list_item, parent, false);
        ArtistViewHolder holder = new ArtistViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        holder.mArtistName.setText(artists.get(position).getArtist());
        holder.mSongCount.setText(artists.get(position).getNumAlbums());
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public void updateData(List<ArtistBean> artists) {
        if (artists != null && !artists.isEmpty()) {
            this.artists.clear();
            this.artists.addAll(artists);
        }
    }

    /**
     * artist list recycleview holder
     */
    static class ArtistViewHolder extends RecyclerView.ViewHolder {

        ImageView mArtistAvatar;
        TextView mArtistName;
        TextView mSongCount;

        public ArtistViewHolder(View itemView) {
            super(itemView);
            mArtistAvatar = (ImageView) itemView.findViewById(R.id.artist_avatar);
            mArtistName = (TextView) itemView.findViewById(R.id.artist_name);
            mSongCount = (TextView) itemView.findViewById(R.id.song_count);
        }
    }
}
