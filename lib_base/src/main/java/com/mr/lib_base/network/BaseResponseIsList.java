package com.mr.lib_base.network;

import java.util.List;

/**
 * content是一个jsonArray的解析类
 */
public class BaseResponseIsList<T> extends BaseResponse {
    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
