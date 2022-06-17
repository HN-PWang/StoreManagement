package com.mr.lib_base.network;

/**
 * 网络相关的常量
 */
public class NetworkConstants {

    public static final int NET_CONNECT_TIMEOUT = 60; // 网络连接超时时间，单位秒

    public static final int NET_READ_TIMEOUT = 60; // 网络读取超时时间，单位秒

    public static final int RESPONSE_SUCCESS_CODE = 200; // 网络成功状态码

    public static final int NETWORK_ERROR_CODE = -50005; // 无网络状态

    public static final String NETWORK_ERROR_MSG = "网络异常，请稍后重试！";

    public static final int SERVER_ERROR_CODE = -50006; // 服务器无法连接

    public static final String SERVER_ERROR_MSG = "服务器连接异常，请稍后重试！";

    public static final int TIMEOUT_ERROR_CODE = -50007; // 超时

    public static final String TIMEOUT_ERROR_MSG = "服务器连接超时，请稍后重试！";

    public static final int PARSER_ERROR_CODE = -50008; // 解析错误

    public static final String PARSER_ERROR_MSG = "服务器异常，数据解析错误！";

    public static final int UNKNOWN_ERROR_CODE = -50009; // 未知错误

    public static final String UNKNOWN_ERROR_MSG = "服务器异常，有未知错误！";

}
