package com.main.maybe.miplayer.model;

import lombok.Data;

/**
 * Created by Lucifer on 2016/8/22.
 * 单曲的 JavaBean 类
 */
@Data
public class SingleBean {

    private String id;

    private String duration;

    private String title;

    private String artistId;

    private String artist;

    private String albumId;

    private String album;

    private String data; // path ?

}
