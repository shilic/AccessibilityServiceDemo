package com.yufs.accessibility.utils;

import android.util.Log;

import com.yufs.accessibility.BuildConfig;


public class LogUtils {
    private static final String TAG = "yufs";

    // 默认TAG的函数
    public static void i(String msg) {
        if (BuildConfig.DEBUG)
            Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (BuildConfig.DEBUG)
            Log.v(TAG, msg);
    }

    // 自定义TAG函数
    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.e(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }

    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.v(tag, msg);
    }





}

