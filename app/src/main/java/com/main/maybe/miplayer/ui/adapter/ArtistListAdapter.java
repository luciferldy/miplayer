package com.main.maybe.miplayer.ui.adapter;

import android.content.Context;

import com.main.maybe.miplayer.model.ArtistBean;
import com.main.maybe.miplayer.ui.adapter.base.ListAdapter;
import com.main.maybe.miplayer.ui.view.ArtistListItemView;

import java.util.List;

/**
 * Created by Maybe霏 on 2015/5/16.
 */
public class ArtistListAdapter extends ListAdapter<ArtistBean, ArtistListItemView> {

    public ArtistListAdapter(Context context, List<ArtistBean> data) {
        super(context, data);
    }

    @Override
    protected ArtistListItemView createView(Context context) {
        return new ArtistListItemView(context);
    }

}
