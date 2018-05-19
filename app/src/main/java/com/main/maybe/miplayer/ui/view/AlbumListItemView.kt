package com.main.maybe.miplayer.ui.view

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.main.maybe.miplayer.R
import com.main.maybe.miplayer.model.AlbumBean
import com.main.maybe.miplayer.ui.adapter.base.IAdapterView
import kotlinx.android.synthetic.main.album_list_item.view.*

class AlbumListItemView(context: Context) : RelativeLayout(context), IAdapterView<AlbumBean> {

    init {
        View.inflate(context, R.layout.album_list_item, this)
    }

    override fun bind(item: AlbumBean, position: Int) {
        album_name.text = item.album
        album_artist.text = item.artist
    }
}