package com.main.maybe.miplayer.model;

/**
 * Created by Lucifer on 2016/8/23.
 */
public class ArtistBean {

    String _id;
    String artist;
    String numAlbums;
    String numTracks;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getNumAlbums() {
        return numAlbums;
    }

    public void setNumAlbums(String numAlbums) {
        this.numAlbums = numAlbums;
    }

    public String getNumTracks() {
        return numTracks;
    }

    public void setNumTracks(String numTracks) {
        this.numTracks = numTracks;
    }
}
