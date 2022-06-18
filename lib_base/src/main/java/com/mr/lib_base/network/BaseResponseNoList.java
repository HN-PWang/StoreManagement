package com.mr.lib_base.network;

/**
 * content是一个jsonObject、String等的解析类
 */
public class BaseResponseNoList<T> extends BaseResponse {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
