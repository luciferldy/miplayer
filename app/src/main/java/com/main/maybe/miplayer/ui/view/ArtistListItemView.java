package com.main.maybe.miplayer.ui.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.model.ArtistBean;
import com.main.maybe.miplayer.ui.adapter.base.IAdapterView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistListItemView extends RelativeLayout implements IAdapterView<ArtistBean> {

    @BindView(R.id.artist_avatar) ImageView mArtistAvatar;
    @BindView(R.id.artist_name) TextView mArtistName;
    @BindView(R.id.song_count) TextView mSongCount;

    public ArtistListItemView(Context context) {
        super(context);
        View.inflate(context, R.layout.artist_list_item, this);
        ButterKnife.bind(this);
    }

    @Override
    public void bind(ArtistBean item, int position) {
        mArtistName.setText(item.getArtist());
        mSongCount.setText(item.getNumAlbums());
    }
}
