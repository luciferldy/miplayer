package com.main.maybe.miplayer.presenter;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;

import com.main.maybe.miplayer.model.AlbumBean;
import com.main.maybe.miplayer.model.ArtistBean;
import com.main.maybe.miplayer.model.FolderBean;
import com.main.maybe.miplayer.model.SingleBean;
import com.main.maybe.miplayer.presenter.base.BasePresenter;
import com.main.maybe.miplayer.ui.view.base.BaseView;
import com.main.maybe.miplayer.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucifer on 2016/8/22.
 */
public class LocalMusicPresenter implements BasePresenter {

    private static final String LOG_TAG = LocalMusicPresenter.class.getSimpleName();

    private BaseView mBaseView;
    private FragmentActivity mActivity;

    public LocalMusicPresenter(FragmentActivity activity, BaseView view) {
        this.mActivity = activity;
        this.mBaseView = view;
    }

    /**
     * get single from ContentResolver
     * @return
     */
    public List<SingleBean> getSingle() {
        ArrayList<SingleBean> singles = new ArrayList<>();
        ContentResolver resolver = mActivity.getContentResolver();
        try {
            // get audio from content resolver
            Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if (cursor == null) {
                Logger.i(LOG_TAG, "getSingle cursor is null.");
                return null;
            }
            SingleBean bean;
            while (cursor.moveToNext()){
                bean = new SingleBean();
                bean.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                bean.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                bean.setArtistId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)));
                bean.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                bean.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                bean.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                bean.setData(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                singles.add(bean);
            }
            cursor.close();
            return singles;
        }catch (Exception e){
            e.printStackTrace();
            Logger.i(LOG_TAG, "getSingle occur error, msg = " + e.getMessage());
            return null;
        }
    }

    /**
     * get artist from ContentResolver
     * @return
     */
    public List<ArtistBean> getArtist() {
        List<ArtistBean> artists = new ArrayList<>();
        ContentResolver resolver = mActivity.getContentResolver();
        try{
            Cursor cursor = resolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
            if (cursor == null) {
                Logger.i(LOG_TAG, "getArtist cursor is null.");
                return null;
            }
            ArtistBean bean;
            while (cursor.moveToNext()){
                bean = new ArtistBean();
                bean.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)));
                bean.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
                bean.setNumAlbums(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)));
                bean.setNumTracks(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)));
                artists.add(bean);
            }
            cursor.close();
            return artists;
        } catch (Exception e){
            e.printStackTrace();
            Logger.i(LOG_TAG, "getArtist occur error, msg = " + e.getMessage());
            return null;
        }
    }

    /**
     * get album from ContentResolver
     * @return
     */
    public List<AlbumBean> getAlbum() {
        ArrayList<AlbumBean> albums = new ArrayList<>();
        ContentResolver resolver = mActivity.getContentResolver();
        try {
            Cursor cursor = resolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
            if (cursor == null) {
                Logger.i(LOG_TAG, "getAlbum cursor is null.");
                return null;
            }
            AlbumBean bean;
            while (cursor.moveToNext()){
                bean = new AlbumBean();

                bean.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)));
                bean.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
                bean.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
                bean.setNumSongs(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
                bean.setAlbumArt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
                albums.add(bean);
            }
            cursor.close();
            return albums;
        }catch (Exception e){
            e.printStackTrace();
            Logger.i(LOG_TAG, "getAlbum occur error, msg = " + e.getMessage());
            return albums;
        }
    }

    /**
     * get folder from where?
     * @return
     */
    public List<FolderBean> getFolder() {
        List<FolderBean> folders = new ArrayList<>();
        FolderBean bean = new FolderBean();
        folders.add(bean);
        return folders;
    }

}
