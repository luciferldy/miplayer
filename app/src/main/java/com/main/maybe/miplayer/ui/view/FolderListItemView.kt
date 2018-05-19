package com.main.maybe.miplayer.ui.view

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import com.main.maybe.miplayer.R
import com.main.maybe.miplayer.model.FolderBean
import com.main.maybe.miplayer.ui.adapter.base.IAdapterView
import kotlinx.android.synthetic.main.folder_list_item.view.*


class FolderListItemView(context: Context) : RelativeLayout(context), IAdapterView<FolderBean> {

    init {
        View.inflate(context, R.layout.folder_list_item, this)
    }

    override fun bind(item: FolderBean, position: Int) {

    }
}