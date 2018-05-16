package com.main.maybe.miplayer.ui.adapter;

import android.content.Context;

import com.main.maybe.miplayer.model.FolderBean;
import com.main.maybe.miplayer.ui.adapter.base.ListAdapter;
import com.main.maybe.miplayer.ui.view.FolderListItemView;

import java.util.List;

/**
 * Created by Lucifer on 2016/8/22.
 */
public class FolderListAdapter extends ListAdapter<FolderBean, FolderListItemView> {

    public FolderListAdapter(Context context, List<FolderBean> data) {
        super(context, data);
    }

    @Override
    protected FolderListItemView createView(Context context) {
        return new FolderListItemView(context);
    }
}
