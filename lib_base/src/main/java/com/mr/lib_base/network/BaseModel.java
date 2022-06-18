package com.mr.lib_base.network;

import android.content.Context;

import com.mr.lib_base.BaseApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Model基类
 */
public class BaseModel {

    private Context mContext;

    public BaseModel() {
        this.mContext = BaseApplication.getInstance();
    }

    /**
     * 把公共参数放在请求头里
     * 需要对value进行空判断，避免请求接口时报错
     */
    public Map<String, String> getHeader() {

        Map<String, String> headerMap = new HashMap<>();

        return headerMap;
    }

}
