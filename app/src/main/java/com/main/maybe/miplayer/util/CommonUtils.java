package com.main.maybe.miplayer.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by MaybeFei on 2015/7/15.
 * Modified by Lucifer on 2016/08/26.
 */
public class CommonUtils {

    private static final String LOG_TAG = CommonUtils.class.getSimpleName();

    /**
     * ms 转换为 mm:ss 格式的字符串.
     * @param ms
     * @return
     */
    public static String timeFormatMs2Str(int ms){
        String str;
        int minutes = (ms / 1000) / 60, seconds = (ms / 1000) % 60;
        if (minutes >= 10 && seconds >= 10) {
            str = "" + minutes + ":" + seconds;
        } else if (minutes >= 10 && seconds < 10) {
            str = "" + minutes + ":0" + seconds;
        } else if (minutes < 10 && seconds >= 10) {
            str = "0" + minutes + ":" + seconds;
        } else {
            str = "0" + minutes + ":0" + seconds;
        }
        return str;
    }

    /**
     * 判断播放音乐服务是否在工作
     * @param className
     * @param context
     * @return
     */
    public static boolean isServiceWorked(String className, Context context){
        ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningServiceInfo = (ArrayList<ActivityManager.RunningServiceInfo>) manager.getRunningServices(30);
        for (int i = 0; i < runningServiceInfo.size(); i++){
            if (runningServiceInfo.get(i).service.getClassName().toString().equals(className)) {
                Logger.i(LOG_TAG, "isServiceWorked true");
                return true;
            }

        }
        Logger.i(LOG_TAG, "isServiceWorked false");
        return false;
    }
}
