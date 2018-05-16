package com.main.maybe.miplayer.ui.view;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.model.FolderBean;
import com.main.maybe.miplayer.ui.adapter.base.IAdapterView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FolderListItemView extends RelativeLayout implements IAdapterView<FolderBean> {

    @BindView(R.id.folder_name) TextView mFolderName;
    @BindView(R.id.song_count) TextView mSongCount;

    public FolderListItemView(Context context) {
        super(context);
        View.inflate(context, R.layout.folder_list_item, this);
        ButterKnife.bind(this);
    }

    @Override
    public void bind(FolderBean item, int position) {

    }
}
