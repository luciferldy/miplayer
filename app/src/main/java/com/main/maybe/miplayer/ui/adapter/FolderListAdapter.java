package com.main.maybe.miplayer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.model.FolderBean;

import java.util.ArrayList;

/**
 * Created by Lucifer on 2016/8/22.
 */
public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.FolderViewHolder> {

    private ArrayList<FolderBean> folders;

    public FolderListAdapter() {
        folders = new ArrayList<>();
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(FolderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    static class FolderViewHolder extends RecyclerView.ViewHolder {

        TextView mFolderName;
        TextView mSongCount;

        public FolderViewHolder(View itemView) {
            super(itemView);

            mFolderName = (TextView) itemView.findViewById(R.id.folder_name);
            mSongCount = (TextView) itemView.findViewById(R.id.song_count);
        }
    }
}
