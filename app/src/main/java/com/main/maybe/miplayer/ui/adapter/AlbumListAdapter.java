package com.main.maybe.miplayer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.model.AlbumBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maybe霏 on 2015/5/16.
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder> {

    private ArrayList<AlbumBean> albums = new ArrayList<>();

    public AlbumListAdapter(){
        this.albums = new ArrayList<>();
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.album_list_item, parent, false);
        AlbumViewHolder holder = new AlbumViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        holder.mAlbumName.setText(albums.get(position).getAlbum());
        holder.mAlbumArtist.setText(albums.get(position).getArtist());
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    /**
     * update albums
     * @param albums
     */
    public void updateData(List<AlbumBean> albums) {
        if (albums != null && !albums.isEmpty()) {
            this.albums.clear();
            this.albums.addAll(albums);
        }
    }

    /**
     * album list recycleview holder
     * type: friendly
     */
    static class AlbumViewHolder extends RecyclerView.ViewHolder{

        ImageView mAlbumCover;
        TextView mAlbumName;
        TextView mAlbumArtist;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            mAlbumCover = (ImageView) itemView.findViewById(R.id.album_cover);
            mAlbumName = (TextView) itemView.findViewById(R.id.album_name);
            mAlbumArtist = (TextView) itemView.findViewById(R.id.album_artist);
        }
    }
}
