package com.main.maybe.miplayer.model;

/**
 * Created by Lucifer on 2016/8/23.
 * 专辑的 JavaBean 类
 */
public class AlbumBean {

    String _id; // unique id
    String album; // album name
    String artist; // artist
    String numSongs;
    String albumArt;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getNumSongs() {
        return numSongs;
    }

    public void setNumSongs(String numSongs) {
        this.numSongs = numSongs;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    @Override
    public String toString() {
        return "_id = " + _id + "\n" +
                "album = " + album + "\n" +
                "artist = " + artist + "\n" +
                "numSongs = " + numSongs + "\n" +
                "albumArt = " + albumArt;
    }
}
