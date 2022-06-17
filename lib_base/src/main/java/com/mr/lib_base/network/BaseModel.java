package com.mr.lib_base.network;

import android.content.Context;
import android.text.TextUtils;

import com.mr.lib_base.BaseApplication;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.vvic.lib.base.BaseApplication;
import com.vvic.lib.base.constants.GlobalConstants;
import com.vvic.lib.base.constants.NetworkConstants;
import com.vvic.lib.base.constants.SPGlobalFlag;
import com.vvic.lib.base.encrypt.VVICJWT;
import com.vvic.lib.base.manager.MarketManager;
import com.vvic.lib.base.sp.SPUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import rx.Observable;

/**
 * Model基类
 */
public class BaseModel {

    private String mJwtIss;

    private String mJwtSecret;

    private Context mContext;

    /**
     * 服务器的时间戳，每个接口调用前，都需要从服务端获取最新的时间
     * 2020-11-16 新的请求方案是 不需要请求服务器时间，直接去请求接口。
     */
    private long mMilliseconds;

    public BaseModel(String jwtIss, String jwtSecret) {
        this.mContext = BaseApplication.getInstance();
        this.mJwtIss = jwtIss;
        this.mJwtSecret = jwtSecret;
    }

    public void setMilliseconds(long milliseconds) {
        this.mMilliseconds = milliseconds;
    }

    /**
     * 获取系统时间
     */
    public Observable<BaseEntity<ServerTimeEntity>> getServerTime() {
        GetServerTimeService getServerTimeService = VVICRetrofitManager.create(GetServerTimeService.class);
        return getServerTimeService.get();
    }

    /**
     * 把公共参数放在请求头里
     * 需要对value进行空判断，避免请求接口时报错
     */
    public Map<String, String> getHeaderMap() {
        Map<String, String> headerMap = new HashMap<>();
        return headerMap;
    }

    /**
     * 把秘钥添加到请求体
     */
    public Map<String, String> putSecretToContentMap(Map<String, String> privateMap) {
        if (privateMap == null) {
            privateMap = new TreeMap<>();
        }
        if (mMilliseconds != 0) {
            privateMap.put("secret", VVICJWT.getSecret(mMilliseconds, mJwtIss, mJwtSecret));
        }
        return privateMap;
    }

}
