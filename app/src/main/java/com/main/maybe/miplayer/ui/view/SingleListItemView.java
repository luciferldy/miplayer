package com.main.maybe.miplayer.ui.view;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.maybe.miplayer.R;
import com.main.maybe.miplayer.model.SingleBean;
import com.main.maybe.miplayer.ui.adapter.base.IAdapterView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleListItemView extends RelativeLayout implements IAdapterView<SingleBean> {

    @BindView(R.id.single_name) TextView mSingleName;
    @BindView(R.id.album_artist) TextView mArtistName;

    public SingleListItemView(Context context) {
        super(context);
        View.inflate(context, R.layout.single_list_item, this);
        ButterKnife.bind(this);
    }

    @Override
    public void bind(SingleBean item, int position) {
        mSingleName.setText(item.getTitle());
        mArtistName.setText(item.getArtist());
    }
}
