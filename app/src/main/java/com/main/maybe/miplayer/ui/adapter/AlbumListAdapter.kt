package com.main.maybe.miplayer.ui.adapter

import android.content.Context
import com.main.maybe.miplayer.model.AlbumBean
import com.main.maybe.miplayer.ui.adapter.base.ListAdapter
import com.main.maybe.miplayer.ui.view.AlbumListItemView

class AlbumListAdapter(context: Context, data: MutableList<AlbumBean>) : ListAdapter<AlbumBean, AlbumListItemView>(context, data) {

    override fun createView(context: Context): AlbumListItemView {
        return AlbumListItemView(context)
    }
}