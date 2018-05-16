package com.main.maybe.miplayer.ui.adapter;

import android.content.Context;

import com.main.maybe.miplayer.model.AlbumBean;
import com.main.maybe.miplayer.ui.adapter.base.ListAdapter;
import com.main.maybe.miplayer.ui.view.AlbumListItemView;

import java.util.List;

/**
 * Created by Maybe霏 on 2015/5/16.
 */
public class AlbumListAdapter extends ListAdapter<AlbumBean, AlbumListItemView> {

    public AlbumListAdapter(Context context, List<AlbumBean> data) {
        super(context, data);
    }

    @Override
    protected AlbumListItemView createView(Context context) {
        return new AlbumListItemView(context);
    }
}
