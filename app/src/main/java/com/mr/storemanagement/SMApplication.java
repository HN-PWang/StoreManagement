package com.mr.storemanagement;

import com.mr.lib_base.BaseApplication;
import com.mr.lib_base.network.RetrofitManager;

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

        RetrofitManager.init(BuildConfig.MAIN_SERVER_URL);
    }
}
