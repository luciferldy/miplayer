package com.main.maybe.miplayer.model;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Lucifer on 2016/8/23.
 * 专辑的 JavaBean 类
 */
@Data
public class AlbumBean {

    // unique id
    private String Id;

    // album name
    private String album;

    // artist
    private String artist;

    private String numSongs;

    private String albumArt;

}
