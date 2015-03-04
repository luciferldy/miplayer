package com.main.maybe.miplayer.music;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maybe霏 on 2015/3/4.
 */
public class MusicScanner {

    private static List<String> allFiles = new ArrayList<String>();

    public void scanMusic(String path, int level){
        File origin = new File(path);
        File[] files = origin.listFiles();

        if (files == null)
            return ;
        for (File file : files){
            // 筛选文件夹
            if (file.isDirectory() && !file.isHidden() && file.canRead() && level>0){
                scanMusic(path+"/"+file.getName(), level-1);
            }
            // 筛选文件
            else if (file.isFile() && file.canRead() && file.getName().endsWith("mp3")){
                allFiles.add(file.getAbsolutePath());
            }
        }
    }

    public List<String> getScannedMusic(){
        return allFiles;
    }
}
