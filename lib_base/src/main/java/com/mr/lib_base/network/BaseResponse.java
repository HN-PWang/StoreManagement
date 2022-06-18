package com.mr.lib_base.network;

/**
 * 返回值基类
 */
public class BaseResponse<T> {
    public int code;
    public T data;
    public String message;
}
