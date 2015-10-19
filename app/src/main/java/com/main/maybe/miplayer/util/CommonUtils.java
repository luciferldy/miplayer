package com.main.maybe.miplayer.util;

/**
 * Created by Maybe on 2015/7/15.
 */
public class CommonUtils {

    public static int SELF_LIST_ID = -111;

    public static String MSToStringTime(int ms){
        String str;
        int minutes = (ms/1000)/60, seconds = (ms/1000)%60;
        if (minutes >= 10 && seconds >= 10){
            str = "" + minutes + ":" + seconds;
        }else if (minutes >= 10 && seconds < 10){
            str = "" + minutes + ":0" + seconds;
        }else if (minutes < 10 && seconds >= 10){
            str = "0" + minutes + ":" + seconds;
        }else {
            str = "0" + minutes + ":0" + seconds;
        }
        return str;
    }
}
