package com.main.maybe.miplayer.util;

import android.util.Log;

/**
 * Created by Lucifer on 2016/8/23.
 */
public class Logger {

    private static boolean isDebug = true;

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }
}
