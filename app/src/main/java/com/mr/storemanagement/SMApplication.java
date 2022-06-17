package com.mr.storemanagement;

import com.mr.lib_base.BaseApplication;

/**
 * @auther: pengwang
 * @date: 2022/6/17
 * @description:
 */
public class SMApplication extends BaseApplication {

    private static SMApplication instance;

    public static SMApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
