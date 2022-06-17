package com.mr.lib_base.util;

import android.util.Log;

import org.greenrobot.eventbus.android.BuildConfig;

/**
 * @auther: pengwang
 * @date: 2022/6/17
 * @description:
 */
public class MSLog {

    private static final String TAG = "StoreManagement";

    private static final int LOG_LINE_MAX_LENGTH = 2048;

    public static void i(String msg) {
        if (BuildConfig.DEBUG) {
            int strLength = msg.length();
            if (strLength > LOG_LINE_MAX_LENGTH) {
                for (int i = 0; i < strLength; i += LOG_LINE_MAX_LENGTH) {
                    if (i + LOG_LINE_MAX_LENGTH < strLength) {
                        Log.i(TAG, msg.substring(i, i + LOG_LINE_MAX_LENGTH));
                    } else {
                        Log.i(TAG, msg.substring(i, strLength));
                    }
                }
            } else {
                Log.i(TAG, msg);
            }
        }
    }

    public static void e(String msg) {
        if (BuildConfig.DEBUG) {
            int strLength = msg.length();
            if (strLength > LOG_LINE_MAX_LENGTH) {
                for (int i = 0; i < strLength; i += LOG_LINE_MAX_LENGTH) {
                    if (i + LOG_LINE_MAX_LENGTH < strLength) {
                        Log.e(TAG, msg.substring(i, i + LOG_LINE_MAX_LENGTH));
                    } else {
                        Log.e(TAG, msg.substring(i, strLength));
                    }
                }
            } else {
                Log.e(TAG, msg);
            }
        }
    }

}
