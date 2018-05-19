package com.main.maybe.miplayer.ui.adapter.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

abstract class ListAdapter<T, V : IAdapterView<T>>(private val mContext: Context, private var mData: MutableList<T>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemClickListener: ((Int) -> Void)? = null
    private var itemLongClickListener: ((Int) -> Void)? = null
    private var lastItemClickPosition = RecyclerView.NO_POSITION

    var data: MutableList<T>?
        get() = mData
        set(data) {
            mData = data
        }

    protected abstract fun createView(context: Context): V

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = createView(mContext) as View
        val holder = object : RecyclerView.ViewHolder(itemView) {

        }
        if (itemClickListener != null) {
            itemView.setOnClickListener {
                val position = holder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    lastItemClickPosition = position
                    itemClickListener!!.invoke(position)
                }
            }
        }
        if (itemLongClickListener != null) {
            itemView.setOnLongClickListener {
                val position = holder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemLongClickListener!!.invoke(position)
                }
                false
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView as V
        itemView.bind(getItem(position), position)
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun addData(data: MutableList<T>) {
        if (mData == null) {
            mData = data
        } else {
            mData!!.addAll(data)
        }
    }

    fun getItem(position: Int): T {
        return mData!![position]
    }

    fun clear() {
        if (mData != null)
            mData!!.clear()
    }

    fun setOnItemClickListener(listener: (Int) -> Void) {
        itemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (Int) -> Void) {
        itemLongClickListener = listener
    }
}