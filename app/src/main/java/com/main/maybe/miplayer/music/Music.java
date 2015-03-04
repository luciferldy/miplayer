package com.main.maybe.miplayer.music;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.File;

/**
 * Created by Maybe霏 on 2015/3/4.
 */
public class Music {
    private final String UNKNOWN = "Unknown";
    private File file;
    private String name = UNKNOWN;
    private String artist = UNKNOWN;
    private String album = UNKNOWN;
    private int timesPlayed = 0;
    private String duration;
    private File albumCover;
    private int time;
    private String LOG_TAG = Music.class.getSimpleName();

    // 构造函数
    public Music(String filePath){
        File file = new File(filePath);
        if (file.exists())
            populateMusicData(file);
        else
            Log.w(LOG_TAG, "Music file at "+filePath
            +" does not exist.");
    }
    public Music(Music music){
        file = music.file;
        name = music.name;
        artist = music.artist;
        album = music.album;
        timesPlayed = music.timesPlayed;
        duration = music.duration;
        albumCover = music.albumCover;
        Log.d(LOG_TAG, "Done making the music object with following data:　"+toString());
    }
    public Music(File file, String name, String artist, String album, String duration){
        this.file = file;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }

    // 填写歌曲信息
    private void populateMusicData(File file){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(file.getAbsolutePath());

        this.file = file;

        String name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        if (name != null && !name.equals(""))
            this.name = name;
        if (artist != null && !artist.equals(""))
            this.artist = artist;
        if (album != null && !album.equals(""))
            this.album = album;
        if (duration != null && !duration.equals("")){
            this.duration = duration;
            this.time = Integer.parseInt(this.duration)/1000;
        }
    }

    public String getMusicLocation(){
        return file.getAbsolutePath();
    }

    public File getFile(){
        return file;
    }

    public String getName(){
        return name;
    }

    public String getArtist(){
        return artist;
    }

    public int getTimesPlayed(){
        return timesPlayed;
    }

    public File getAlbumCover(){
        return albumCover;
    }

    public String getDuration(){
        return duration;
    }

    public int getTime(){
        return time;
    }

    public String getPlayableFilePath(){
        return file.getAbsolutePath();
    }

    @Override
    public String toString() {
        return "Music [ file=" + file +
                ", name="+name+
                ", artist="+artist+
                ", album="+album+
                ", timesPlayed="+timesPlayed+ " ]";
    }
}
