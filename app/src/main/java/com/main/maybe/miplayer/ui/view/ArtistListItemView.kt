package com.main.maybe.miplayer.ui.view

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.main.maybe.miplayer.R
import com.main.maybe.miplayer.model.ArtistBean
import com.main.maybe.miplayer.ui.adapter.base.IAdapterView
import kotlinx.android.synthetic.main.artist_list_item.view.*

class ArtistListItemView(context: Context) : RelativeLayout(context), IAdapterView<ArtistBean> {

    init {
        View.inflate(context, R.layout.artist_list_item, this)
    }

    override fun bind(item: ArtistBean, position: Int) {
        artist_name.text = item.artist
        song_count.text = item.numAlbums
    }
}