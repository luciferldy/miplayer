package com.main.maybe.miplayer.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.main.maybe.miplayer.R
import com.main.maybe.miplayer.model.FolderBean
import com.main.maybe.miplayer.presenter.LocalMusicPresenter
import com.main.maybe.miplayer.ui.adapter.AlbumListAdapter
import com.main.maybe.miplayer.ui.adapter.ArtistListAdapter
import com.main.maybe.miplayer.ui.adapter.FolderListAdapter
import com.main.maybe.miplayer.ui.adapter.SingleListAdapter
import com.main.maybe.miplayer.ui.view.base.BaseView
import java.util.ArrayList

class HomeFragment : Fragment(), BaseView {
    private var position: Int = 0

    private var mPresenter: LocalMusicPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments!!.getInt(ARG_POSITION)
        initPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

        val fl = FrameLayout(activity!!)
        fl.layoutParams = params

        val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
        params.setMargins(margin, margin, margin, margin)
        // switch options
        when (position) {
            0 ->
                // songs
                fl.addView(initSingle())
            1 ->
                // artist
                fl.addView(initArtists())
            2 ->
                // album
                fl.addView(initAlbum())
            3 ->
                // list
                fl.addView(initFolder())
        }
        return fl
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun initPresenter() {
        mPresenter = LocalMusicPresenter(activity, this)
    }

    // init single list
    private fun initSingle(): LinearLayout {
        val inflater = LayoutInflater.from(activity)
        val root = inflater.inflate(R.layout.base_list, null) as LinearLayout
        val rv = root.findViewById<View>(R.id.rv_ls) as RecyclerView
        val adapter = SingleListAdapter(activity, mPresenter!!.single)
        rv.layoutManager = LinearLayoutManager(activity)
        rv.adapter = adapter
        return root
    }

    // init artist list
    private fun initArtists(): LinearLayout {
        val inflater = LayoutInflater.from(activity)
        val root = inflater.inflate(R.layout.base_list, null) as LinearLayout
        val rv = root.findViewById<View>(R.id.rv_ls) as RecyclerView
        val adapter = ArtistListAdapter(activity, mPresenter!!.artist)
        rv.layoutManager = LinearLayoutManager(activity)
        rv.adapter = adapter
        return root
    }

    // init album list
    private fun initAlbum(): LinearLayout {
        val inflater = LayoutInflater.from(activity)
        val root = inflater.inflate(R.layout.base_list, null) as LinearLayout
        val rv = root.findViewById<View>(R.id.rv_ls) as RecyclerView
        val adapter = AlbumListAdapter(activity!!, mPresenter!!.album!!)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(activity)
        return root
    }

    // init folder list
    private fun initFolder(): LinearLayout {
        val inflater = LayoutInflater.from(activity)
        val root = inflater.inflate(R.layout.base_list, null) as LinearLayout
        val rv = root.findViewById<View>(R.id.rv_ls) as RecyclerView
        val adapter = FolderListAdapter(activity, ArrayList())
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(activity)
        return root
    }

    companion object {

        private val ARG_POSITION = "position"

        fun newInstance(position: Int): HomeFragment {
            val f = HomeFragment()
            val b = Bundle()
            b.putInt(ARG_POSITION, position)
            f.arguments = b
            return f
        }
    }
}