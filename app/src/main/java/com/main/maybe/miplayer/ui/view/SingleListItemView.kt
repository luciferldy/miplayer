package com.main.maybe.miplayer.ui.view

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.main.maybe.miplayer.R
import com.main.maybe.miplayer.model.SingleBean
import com.main.maybe.miplayer.ui.adapter.base.IAdapterView
import kotlinx.android.synthetic.main.single_list_item.view.*

class SingleListItemView(context: Context) : RelativeLayout(context), IAdapterView<SingleBean> {

    init {
        View.inflate(context, R.layout.single_list_item, this)
    }

    override fun bind(item: SingleBean, position: Int) {
        single_name!!.text = item.title
        album_artist!!.text = item.artist
    }
}