package com.main.maybe.miplayer.music;

import android.graphics.Bitmap;
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
    private Bitmap albumCover;
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

    // write information of song
    private void populateMusicData(File file){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try{
            mmr.setDataSource(file.getAbsolutePath());

            this.file = file;

            String name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);// measuring by ms

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
            // get the album cover
            // memory exploring……
//            byte[] data = mmr.getEmbeddedPicture();
//            if(data != null){
//                albumCover = BitmapFactory.decodeByteArray(data, 0, data.length);
//            }

        }catch(IllegalArgumentException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }


    }

    public String getMusicLocation(){
        return file.getAbsolutePath();
    }

    public String getName(){
        return name;
    }

    public String getArtist(){
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public Bitmap getAlbumCover(){
        return albumCover;
    }

    public int getTime(){
        return time;
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
