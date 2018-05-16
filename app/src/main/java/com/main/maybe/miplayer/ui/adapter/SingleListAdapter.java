package com.main.maybe.miplayer.ui.adapter;

import android.content.Context;

import com.main.maybe.miplayer.model.SingleBean;
import com.main.maybe.miplayer.ui.adapter.base.ListAdapter;
import com.main.maybe.miplayer.ui.view.SingleListItemView;

import java.util.List;

/**
 * Created by Lucifer on 2015/5/13.
 */
public class SingleListAdapter extends ListAdapter<SingleBean, SingleListItemView> {

    public SingleListAdapter(Context context, List<SingleBean> data) {
        super(context, data);
    }

    @Override
    protected SingleListItemView createView(Context context) {
        return new SingleListItemView(context);
    }
}

