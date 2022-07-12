package com.mr.storemanagement;

import android.util.Log;

import com.mr.lib_base.BaseApplication;
import com.mr.lib_base.network.RetrofitManager;

/**
 * @auther: pengwang
 * @date: 2022/6/17
 * @description:
 */
public class SMApplication extends BaseApplication {

    private static final String TAG = "SMApplication";

    private static SMApplication instance;

    public static SMApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        RetrofitManager.init(BuildConfig.MAIN_SERVER_URL);

        boolean isDebugBuild = BuildConfig.BUILD_TYPE.equals("debug");

//        NeverCrash.getInstance()
//                .setDebugMode(isDebugBuild)
//                .setMainCrashHandler((t, e) -> {
//                    //todo 跨线程操作时注意线程调度回主线程操作
//                    Log.e(TAG, "主线程异常");//此处log只是展示，当debug为true时，主类内部log会打印异常信息
//                    //todo 此处做你的日志记录即可
//                })
//                .setUncaughtCrashHandler((t, e) -> {
//                    //todo 跨线程操作时注意线程调度回主线程操作
//                    Log.e(TAG, "子线程异常");//此处log只是展示，当debug为true时，主类内部log会打印异常信息
//                    //todo 此处做你的日志记录即可
//                })
//                .register(this);
    }

}
