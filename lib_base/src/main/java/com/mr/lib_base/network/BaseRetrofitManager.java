package com.mr.lib_base.network;


import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.mr.storemanagement.MyApplication;

import org.greenrobot.eventbus.android.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit及OKhttp的配置
 * baseUrl不同，都要创建一个对应的BaseRetrofitManager子类来实现
 */
public class BaseRetrofitManager {

    protected static Retrofit generalRetrofit(String url, long readTimeOut) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        // 设置OkHttp超时时间
        clientBuilder.connectTimeout(NetworkConstants.NET_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        clientBuilder.readTimeout(readTimeOut, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            // OkHttp 拦截处理Log
            NetworkLogInterceptor logInterceptor = new NetworkLogInterceptor();
            clientBuilder.addInterceptor(logInterceptor);

            // 添加Stetho调试拦截器，并初始化
            clientBuilder.addNetworkInterceptor(new StethoInterceptor());
            Stetho.initializeWithDefaults(MyApplication.getInstance());
        }

        return new Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build();
    }

}
