package com.main.maybe.miplayer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.task.LoadingListTask;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucifer on 2015/5/13.
 */
public class SingleListAdapter extends RecyclerView.Adapter<SingleListAdapter.SingleViewHolder> {

    private ArrayList<HashMap<String, String>> songs = new ArrayList<>();
    private LayoutInflater mInflater;

    public SingleListAdapter(ArrayList<HashMap<String, String>> songs, LayoutInflater mInflater){
        this.songs = songs;
        this.mInflater = mInflater;
    }

    @Override
    public SingleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.single_list_view, parent, false);
        SingleViewHolder holder = new SingleViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(SingleViewHolder holder, int position) {

        holder.mSingleName.setText(songs.get(position).get(LoadingListTask.songName));
        holder.mArtistName.artistName.setText(songs.get(position).get(LoadingListTask.artistName));

    }

    @Override
    public int getItemCount() {
        return songs.size();
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

