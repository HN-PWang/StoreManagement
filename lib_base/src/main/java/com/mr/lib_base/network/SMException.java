package com.mr.lib_base.network;

import android.text.TextUtils;

/**
 * 网络层自定义异常
 */
public class SMException extends Exception {

    private int mErrorCode; // 错误码

    private String mErrorMsg; // 错误信息

    private String mData;

    private int mHttpErrorCode;

    private String mHttpErrorMsg;

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public String getData() {
        return mData;
    }

    public int getHttpErrorCode() {
        return mHttpErrorCode;
    }

    public String getHttpErrorMsg() {
        return mHttpErrorMsg;
    }

    public SMException(int errorCode) {
        this.mErrorCode = errorCode;
        this.mErrorMsg = getErrorMsgFromLocalCode(errorCode);
    }

    public SMException(int errorCode, String errorMsg, String data) {
        this.mErrorCode = errorCode;
        this.mData = data;
        if (!TextUtils.isEmpty(errorMsg)) {
            this.mErrorMsg = errorMsg;
        } else {
            this.mErrorMsg = NetworkConstants.SERVER_ERROR_MSG;
        }
    }

    public SMException(int errorCode, String errorMsg, String data, int httpErrorCode, String httpErrorMsg) {
        this.mErrorCode = errorCode;
        this.mData = data;
        if (!TextUtils.isEmpty(errorMsg)) {
            this.mErrorMsg = errorMsg;
        } else {
            this.mErrorMsg = NetworkConstants.SERVER_ERROR_MSG;
        }
        this.mHttpErrorCode = httpErrorCode;
        this.mHttpErrorMsg = httpErrorMsg;
    }

    /**
     * 根据本地错误码获取错误信息
     *
     * @param errorCode
     * @return
     */
    static String getErrorMsgFromLocalCode(int errorCode) {
        String msg;
        switch (errorCode) {
            case NetworkConstants.NETWORK_ERROR_CODE:
                msg = NetworkConstants.NETWORK_ERROR_MSG;
                break;
            case NetworkConstants.TIMEOUT_ERROR_CODE:
                msg = NetworkConstants.TIMEOUT_ERROR_MSG;
                break;
            case NetworkConstants.PARSER_ERROR_CODE:
                msg = NetworkConstants.PARSER_ERROR_MSG;
                break;
            case NetworkConstants.SERVER_ERROR_CODE:
                msg = NetworkConstants.SERVER_ERROR_MSG;
                break;
            case NetworkConstants.UNKNOWN_ERROR_CODE:
            default:
                msg = NetworkConstants.UNKNOWN_ERROR_MSG;
                break;
        }
        return msg;
    }

}
