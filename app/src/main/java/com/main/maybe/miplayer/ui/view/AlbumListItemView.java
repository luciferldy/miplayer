package com.main.maybe.miplayer.ui.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.model.AlbumBean;
import com.main.maybe.miplayer.ui.adapter.base.IAdapterView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumListItemView extends RelativeLayout implements IAdapterView<AlbumBean> {

    @BindView(R.id.album_cover) ImageView mAlbumCover;
    @BindView(R.id.album_name) TextView mAlbumName;
    @BindView(R.id.album_artist) TextView mAlbumArtist;

    public AlbumListItemView(Context context) {
        super(context);
        View.inflate(context, R.layout.album_list_item, this);
        ButterKnife.bind(this);
    }

    @Override
    public void bind(AlbumBean item, int position) {
        mAlbumName.setText(item.getAlbum());
        mAlbumArtist.setText(item.getArtist());
    }
}
