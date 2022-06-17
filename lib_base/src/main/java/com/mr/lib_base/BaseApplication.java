package com.mr.lib_base;

import android.app.Application;

/**
 * @auther: pengwang
 * @date: 2022/6/17
 * @description:
 */
public class BaseApplication extends Application {

    private static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
