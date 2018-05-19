package com.main.maybe.miplayer.ui.adapter.base

interface IAdapterView<T> {
    fun bind(item: T, position: Int)
}
