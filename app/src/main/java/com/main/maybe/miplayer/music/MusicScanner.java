package com.main.maybe.miplayer.music;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maybe霏 on 2015/3/4.
 */
public class MusicScanner {

    private List<String> allFiles = new ArrayList<String>();

    public void scanMusic(Context context){

        // 下面的命令将返回所有在外部存储卡上的音乐文件的信息
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        while (cursor.moveToNext()){
            allFiles.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
        }
    }

    public List<String> getScannedMusic(){
        return allFiles;
    }
}
