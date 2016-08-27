package com.main.maybe.miplayer.model;

/**
 * Created by Lucifer on 2016/8/22.
 * 单曲的 JavaBean 类
 */
public class SingleBean {

    String _id; // unique id in db
    String duration; // duration
    String title;
    String artist_id;
    String artist;
    String album_id;
    String album;
    String data; // path ?

    public String get_id() {
        return _id;
    }

    public String getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist_id() {
        return artist_id;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public String getAlbum() {
        return album;
    }

    public String getData() {
        return data;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setData(String data) {
        this.data = data;
    }
}
